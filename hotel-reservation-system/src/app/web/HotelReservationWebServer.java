package app.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import app.AppException;
import app.ManagerFactory;
import app.checkin.CheckInRoomControl;
import app.checkout.CheckOutRoomControl;
import app.reservation.ReserveRoomControl;
import domain.payment.Payment;
import domain.reservation.Reservation;
import domain.room.Room;
import domain.stay.Stay;
import util.DateUtil;

/**
 * ホテル予約システムの Web UI (Boundary)<br>
 * JDK 標準の HttpServer でホームページ風の画面を提供する。<br>
 * 既存のドメイン層 (Control / Manager) をそのまま再利用する。<br>
 * 起動後、ブラウザで http://localhost:8080/ を開く。
 */
public class HotelReservationWebServer {

	/** 待ち受けポート */
	private static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

		// トップページ (HTML)
		server.createContext("/", exchange -> {
			if ("/".equals(exchange.getRequestURI().getPath())) {
				sendResponse(exchange, 200, "text/html; charset=UTF-8", INDEX_HTML);
			}
			else {
				sendResponse(exchange, 404, "text/plain; charset=UTF-8", "Not Found");
			}
		});

		// 空室検索 API
		server.createContext("/api/search", exchange -> {
			try {
				Map<String, String> params = parseParams(exchange);
				String roomType = params.get("roomType");
				if (roomType == null || roomType.isEmpty() || "全タイプ".equals(roomType)) {
					roomType = null;
				}
				List<Room> rooms = ManagerFactory.getInstance().getRoomManager()
						.searchVacantRooms(roomType);
				StringBuilder json = new StringBuilder("{\"ok\":true,\"rooms\":[");
				for (int i = 0; i < rooms.size(); i++) {
					Room room = rooms.get(i);
					if (i > 0) {
						json.append(',');
					}
					json.append("{\"roomNumber\":").append(room.getRoomNumber())
							.append(",\"roomType\":\"").append(escapeJson(room.getRoomType()))
							.append("\",\"basicRate\":").append(room.getBasicRate()).append('}');
				}
				json.append("]}");
				sendResponse(exchange, 200, "application/json; charset=UTF-8", json.toString());
			}
			catch (Exception e) {
				sendError(exchange, e);
			}
		});

		// 宿泊予約 API
		server.createContext("/api/reserve", exchange -> {
			try {
				Map<String, String> params = parseParams(exchange);
				String userName = required(params, "userName", "利用者名");
				String roomType = required(params, "roomType", "部屋タイプ");
				Date stayingDate = DateUtil.convertToDate(params.get("stayingDate"));
				if (stayingDate == null) {
					throw new AppException("宿泊予定日は yyyy/mm/dd の形式で入力してください");
				}
				Reservation reservation = new ReserveRoomControl()
						.reserveRoom(userName, roomType, stayingDate);
				sendMessage(exchange, "予約が完了しました。予約番号: "
						+ reservation.getReservationNumber()
						+ " / 部屋番号: " + reservation.getRoomNumber());
			}
			catch (Exception e) {
				sendError(exchange, e);
			}
		});

		// チェックイン API
		server.createContext("/api/checkin", exchange -> {
			try {
				Map<String, String> params = parseParams(exchange);
				int reservationNumber = parseIntParam(params, "reservationNumber", "予約番号");
				String receptionist = params.get("receptionist");
				if (receptionist == null || receptionist.trim().isEmpty()) {
					receptionist = "-";
				}
				CheckInRoomControl control = new CheckInRoomControl();
				Reservation reservation = control.findReservation(reservationNumber);
				control.completeCheckIn(reservationNumber,
						reservation.getRoomNumber(), receptionist.trim());
				sendMessage(exchange, "チェックインが完了しました。部屋番号: "
						+ reservation.getRoomNumber()
						+ " (利用者: " + reservation.getUserName()
						+ " / 担当: " + receptionist.trim() + ")");
			}
			catch (Exception e) {
				sendError(exchange, e);
			}
		});

		// チェックアウト API
		server.createContext("/api/checkout", exchange -> {
			try {
				Map<String, String> params = parseParams(exchange);
				int roomNumber = parseIntParam(params, "roomNumber", "部屋番号");
				CheckOutRoomControl control = new CheckOutRoomControl();
				Stay stay = control.findStay(roomNumber); // 宿泊情報 (存在確認)
				Payment payment = control.completeCheckOut(roomNumber);
				sendMessage(exchange, "チェックアウトが完了しました。部屋番号: "
						+ stay.getRoomNumber()
						+ " / イン日時: " + DateUtil.convertToDateTimeString(stay.getCheckInDateTime())
						+ " / 支払い料金: " + String.format("%,d", payment.getAmount()) + "円");
			}
			catch (Exception e) {
				sendError(exchange, e);
			}
		});

		server.start();
		System.out.println("ホテル予約システム Web サーバーを起動しました。");
		System.out.println("ブラウザで http://localhost:" + PORT + "/ を開いてください。");
		System.out.println("(停止するには Ctrl+C)");
	}

	// ============================================================
	// リクエスト/レスポンスのヘルパー
	// ============================================================

	/** GETのクエリ文字列とPOSTのフォームボディの両方からパラメータを読む */
	private static Map<String, String> parseParams(HttpExchange exchange) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		putQueryParams(params, exchange.getRequestURI().getRawQuery());
		if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			InputStream in = exchange.getRequestBody();
			String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			putQueryParams(params, body);
		}
		return params;
	}

	private static void putQueryParams(Map<String, String> params, String query) {
		if (query == null || query.isEmpty()) {
			return;
		}
		for (String pair : query.split("&")) {
			int eq = pair.indexOf('=');
			if (eq > 0) {
				String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
				String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
				params.put(key, value);
			}
		}
	}

	private static String required(Map<String, String> params, String key, String label)
			throws AppException {
		String value = params.get(key);
		if (value == null || value.trim().isEmpty()) {
			throw new AppException(label + "を入力してください");
		}
		return value.trim();
	}

	private static int parseIntParam(Map<String, String> params, String key, String label)
			throws AppException {
		try {
			return Integer.parseInt(required(params, key, label));
		}
		catch (NumberFormatException e) {
			throw new AppException(label + "は数値で入力してください");
		}
	}

	/** 成功メッセージを JSON で返す */
	private static void sendMessage(HttpExchange exchange, String message) throws IOException {
		sendResponse(exchange, 200, "application/json; charset=UTF-8",
				"{\"ok\":true,\"message\":\"" + escapeJson(message) + "\"}");
	}

	/** 例外をエラー JSON で返す */
	private static void sendError(HttpExchange exchange, Exception e) throws IOException {
		StringBuilder message = new StringBuilder();
		message.append(e.getMessage() == null ? "エラーが発生しました" : e.getMessage());
		if (e instanceof AppException) {
			for (String detail : ((AppException) e).getDetailMessages()) {
				message.append(" / ").append(detail);
			}
		}
		sendResponse(exchange, 200, "application/json; charset=UTF-8",
				"{\"ok\":false,\"message\":\"" + escapeJson(message.toString()) + "\"}");
	}

	private static void sendResponse(HttpExchange exchange, int status,
			String contentType, String body) throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", contentType);
		exchange.sendResponseHeaders(status, bytes.length);
		OutputStream out = exchange.getResponseBody();
		out.write(bytes);
		out.close();
	}

	private static String escapeJson(String text) {
		return text.replace("\\", "\\\\").replace("\"", "\\\"")
				.replace("\n", "\\n").replace("\r", "");
	}

	// ============================================================
	// トップページの HTML (ホームページ風のデザイン)
	// ============================================================

	private static final String INDEX_HTML = """
			<!DOCTYPE html>
			<html lang="ja">
			<head>
			<meta charset="UTF-8">
			<meta name="viewport" content="width=device-width, initial-scale=1">
			<title>HOTEL WASEDA - ホテル予約システム</title>
			<style>
			  * { margin: 0; padding: 0; box-sizing: border-box; }
			  body {
			    font-family: "Hiragino Kaku Gothic ProN", "Yu Gothic", Meiryo, sans-serif;
			    background: #f6f4ef; color: #33302b; line-height: 1.7;
			  }
			  header {
			    background: linear-gradient(120deg, #1f3a5f 0%, #2e5d8f 60%, #7fa8cc 100%);
			    color: #fff; padding: 48px 24px 42px; text-align: center;
			  }
			  header h1 { font-size: 2rem; letter-spacing: .18em; margin-bottom: 8px; }
			  header p { opacity: .9; letter-spacing: .06em; }
			  nav {
			    background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,.08);
			    position: sticky; top: 0; z-index: 10;
			  }
			  nav ul {
			    display: flex; justify-content: center; gap: 4px;
			    list-style: none; flex-wrap: wrap;
			  }
			  nav a {
			    display: block; padding: 14px 22px; text-decoration: none;
			    color: #1f3a5f; font-weight: 600; font-size: .95rem;
			    border-bottom: 3px solid transparent;
			  }
			  nav a:hover { border-bottom-color: #b98a2f; color: #b98a2f; }
			  main { max-width: 960px; margin: 0 auto; padding: 32px 16px 64px; }
			  section {
			    background: #fff; border-radius: 12px; padding: 28px 28px 24px;
			    margin-top: 28px; box-shadow: 0 2px 10px rgba(0,0,0,.06);
			  }
			  section h2 {
			    font-size: 1.2rem; color: #1f3a5f; margin-bottom: 4px;
			    padding-left: 12px; border-left: 5px solid #b98a2f;
			  }
			  section p.desc { color: #77716a; font-size: .9rem; margin-bottom: 16px; }
			  .form-row { display: flex; align-items: center; gap: 12px; margin: 10px 0; flex-wrap: wrap; }
			  .form-row label { width: 110px; font-weight: 600; font-size: .92rem; }
			  input, select {
			    padding: 9px 12px; border: 1px solid #cfc9bf; border-radius: 8px;
			    font-size: .95rem; background: #fdfcfa; min-width: 200px;
			  }
			  input:focus, select:focus { outline: 2px solid #7fa8cc; border-color: #7fa8cc; }
			  button {
			    margin-top: 12px; padding: 10px 26px; border: none; border-radius: 999px;
			    background: #1f3a5f; color: #fff; font-size: .95rem; font-weight: 600;
			    cursor: pointer; letter-spacing: .06em;
			  }
			  button:hover { background: #b98a2f; }
			  .result {
			    margin-top: 14px; padding: 12px 16px; border-radius: 8px;
			    font-size: .93rem; display: none; white-space: pre-wrap;
			  }
			  .result.ok { display: block; background: #eef5ec; border: 1px solid #9dc39a; color: #2f5d2b; }
			  .result.ng { display: block; background: #faeeee; border: 1px solid #d9a0a0; color: #8c2f2f; }
			  table { width: 100%; border-collapse: collapse; margin-top: 14px; }
			  th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #e7e2d9; font-size: .93rem; }
			  th { background: #f2efe8; color: #55503f; }
			  footer { text-align: center; padding: 26px; color: #8d867c; font-size: .85rem; }
			</style>
			</head>
			<body>
			<header>
			  <h1>HOTEL WASEDA</h1>
			  <p>ホテル予約システム — Hotel Reservation System</p>
			</header>
			<nav>
			  <ul>
			    <li><a href="#search">空室検索</a></li>
			    <li><a href="#reserve">宿泊予約</a></li>
			    <li><a href="#checkin">チェックイン</a></li>
			    <li><a href="#checkout">チェックアウト</a></li>
			  </ul>
			</nav>
			<main>

			  <section id="search">
			    <h2>空室検索</h2>
			    <p class="desc">部屋タイプを選んで、現在の空室をご確認いただけます。</p>
			    <div class="form-row">
			      <label>部屋タイプ</label>
			      <select id="searchType">
			        <option>全タイプ</option><option>シングル</option>
			        <option>ダブル</option><option>スイート</option>
			      </select>
			    </div>
			    <button onclick="search()">空室を検索する</button>
			    <div id="searchResult" class="result"></div>
			    <div id="searchTable"></div>
			  </section>

			  <section id="reserve">
			    <h2>宿泊予約</h2>
			    <p class="desc">お名前と部屋タイプ、宿泊予定日を入力してご予約ください。</p>
			    <div class="form-row"><label>お名前</label><input id="userName" placeholder="例: 早稲田 太郎"></div>
			    <div class="form-row"><label>部屋タイプ</label>
			      <select id="reserveType"><option>シングル</option><option>ダブル</option><option>スイート</option></select>
			    </div>
			    <div class="form-row"><label>宿泊予定日</label><input id="stayingDate" placeholder="yyyy/mm/dd"></div>
			    <button onclick="reserve()">予約する</button>
			    <div id="reserveResult" class="result"></div>
			  </section>

			  <section id="checkin">
			    <h2>チェックイン</h2>
			    <p class="desc">予約番号でチェックインの手続きを行います。</p>
			    <div class="form-row"><label>予約番号</label><input id="reservationNumber" placeholder="例: 1001"></div>
			    <div class="form-row"><label>担当受付係</label><input id="receptionist" placeholder="例: そういちろう"></div>
			    <button onclick="checkIn()">チェックインする</button>
			    <div id="checkinResult" class="result"></div>
			  </section>

			  <section id="checkout">
			    <h2>チェックアウト</h2>
			    <p class="desc">部屋番号でチェックアウトし、宿泊料金を精算します。</p>
			    <div class="form-row"><label>部屋番号</label><input id="roomNumber" placeholder="例: 101"></div>
			    <button onclick="checkOut()">チェックアウトする</button>
			    <div id="checkoutResult" class="result"></div>
			  </section>

			</main>
			<footer>ソフトウェア工学A チーム開発課題 — Hotel Reservation System</footer>

			<script>
			async function callApi(path, params) {
			  const res = await fetch(path, {
			    method: 'POST',
			    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			    body: new URLSearchParams(params)
			  });
			  return res.json();
			}
			function show(id, ok, message) {
			  const el = document.getElementById(id);
			  el.className = 'result ' + (ok ? 'ok' : 'ng');
			  el.textContent = message;
			}
			async function search() {
			  const data = await callApi('/api/search', { roomType: document.getElementById('searchType').value });
			  const tableDiv = document.getElementById('searchTable');
			  if (!data.ok) { show('searchResult', false, data.message); tableDiv.innerHTML = ''; return; }
			  if (data.rooms.length === 0) {
			    show('searchResult', false, '条件に合う空室がありません。');
			    tableDiv.innerHTML = ''; return;
			  }
			  show('searchResult', true, data.rooms.length + ' 件の空室が見つかりました。');
			  let html = '<table><tr><th>部屋番号</th><th>タイプ</th><th>基本宿泊料</th></tr>';
			  for (const r of data.rooms) {
			    html += `<tr><td>${r.roomNumber}</td><td>${r.roomType}</td><td>${r.basicRate.toLocaleString()}円</td></tr>`;
			  }
			  tableDiv.innerHTML = html + '</table>';
			}
			async function reserve() {
			  const data = await callApi('/api/reserve', {
			    userName: document.getElementById('userName').value,
			    roomType: document.getElementById('reserveType').value,
			    stayingDate: document.getElementById('stayingDate').value
			  });
			  show('reserveResult', data.ok, data.message);
			}
			async function checkIn() {
			  const data = await callApi('/api/checkin', {
			    reservationNumber: document.getElementById('reservationNumber').value,
			    receptionist: document.getElementById('receptionist').value
			  });
			  show('checkinResult', data.ok, data.message);
			}
			async function checkOut() {
			  const data = await callApi('/api/checkout', {
			    roomNumber: document.getElementById('roomNumber').value
			  });
			  show('checkoutResult', data.ok, data.message);
			}
			</script>
			</body>
			</html>
			""";
}
