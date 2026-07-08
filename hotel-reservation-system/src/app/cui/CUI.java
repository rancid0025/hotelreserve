package app.cui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import app.AppException;
import app.cancel.CancelReservationForm;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.management.RoomManagementForm;
import app.reservation.ReserveRoomForm;
import app.search.RoomSearchForm;
import domain.reservation.Reservation;
import util.DateUtil;

/**
 * ホテル予約システムの CUI (メインクラス)<br>
 * 各画面 (Boundary) を呼び出すメニューを提供する。
 */
public class CUI {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private BufferedReader reader;

	CUI() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	private void execute() throws IOException {
		try {
			while (true) {
				int selectMenu;
				System.out.println("");
				System.out.println("=== ホテル予約システム ===");
				System.out.println("1: 空室検索");
				System.out.println("2: 宿泊予約");
				System.out.println("3: チェックイン");
				System.out.println("4: チェックアウト");
				System.out.println("5: 部屋管理");
				System.out.println("6: 予約キャンセル");
				System.out.println("9: 終了");
				System.out.print("> ");

				try {
					selectMenu = Integer.parseInt(reader.readLine());
				}
				catch (NumberFormatException e) {
					selectMenu = 0;
				}

				if (selectMenu == 9) {
					break;
				}

				try {
					switch (selectMenu) {
						case 1:
							searchRoom();
							break;
						case 2:
							reserveRoom();
							break;
						case 3:
							checkInRoom();
							break;
						case 4:
							checkOutRoom();
							break;
						case 5:
							manageRoom();
							break;
						case 6:
							cancelReservation();
							break;
						default:
							System.out.println("1〜6または9を入力してください。");
							break;
					}
				}
				catch (AppException e) {
					// 各処理でエラーが起きてもメニューに戻る
					System.out.println("【エラー】" + e.getFormattedDetailMessages(LINE_SEPARATOR));
				}
			}
			System.out.println("終了しました。");
		}
		finally {
			reader.close();
		}
	}

	/** 空室検索 (宿泊部屋検索画面) */
	private void searchRoom() throws IOException, AppException {
		System.out.println("部屋タイプを入力してください (シングル/ダブル/スイート、未入力で全タイプ)");
		System.out.print("> ");
		String roomType = reader.readLine();

		RoomSearchForm form = new RoomSearchForm();
		form.inputSearchCondition(roomType); // 部屋検索条件を入力する
		form.displayVacantRoomList(); // 空室一覧を表示する (空室なしの場合は警告を表示する)
	}

	/** 宿泊予約 (宿泊予約画面) */
	private void reserveRoom() throws IOException, AppException {
		System.out.println("利用者名を入力してください");
		System.out.print("> ");
		String userName = reader.readLine();
		if (userName == null || userName.length() == 0) {
			System.out.println("利用者名が不正です。");
			return;
		}

		System.out.println("部屋タイプを入力してください (シングル/ダブル/スイート)");
		System.out.print("> ");
		String roomType = reader.readLine();
		if (roomType == null || roomType.length() == 0) {
			System.out.println("部屋タイプが不正です。");
			return;
		}

		System.out.println("宿泊予定日を yyyy/mm/dd の形式で入力してください");
		System.out.print("> ");
		Date stayingDate = DateUtil.convertToDate(reader.readLine());
		if (stayingDate == null) {
			System.out.println("日付の形式が不正です。");
			return;
		}

		ReserveRoomForm form = new ReserveRoomForm();
		form.setUserName(userName);
		form.selectRoom(roomType); // 部屋を選択する
		form.setStayingDate(stayingDate);
		Reservation reservation = form.submitReservation();
		form.displayReservationNumber(reservation); // 予約番号を表示する
	}

	/** チェックイン (チェックイン画面) */
	private void checkInRoom() throws IOException, AppException {
		System.out.println("予約番号を入力してください");
		System.out.print("> ");
		int reservationNumber;
		try {
			reservationNumber = Integer.parseInt(reader.readLine());
		}
		catch (NumberFormatException e) {
			System.out.println("予約番号が不正です。");
			return;
		}

		CheckInRoomForm form = new CheckInRoomForm();
		form.inputReservationNumber(reservationNumber); // 予約番号を入力する
		form.displayReservationInfo(); // 予約情報を表示する

		System.out.println("担当受付係の名前を入力してください");
		System.out.print("> ");
		String receptionistName = reader.readLine();
		if (receptionistName == null || receptionistName.length() == 0) {
			receptionistName = "-";
		}

		// 予約された部屋番号でチェックインを確定する
		int roomNumber = form.getReservation().getRoomNumber();
		form.confirmCheckIn(roomNumber, receptionistName); // 予約を選択して確定する(部屋番号)
	}

	/** チェックアウト (チェックアウト画面) */
	private void checkOutRoom() throws IOException, AppException {
		System.out.println("部屋番号を入力してください");
		System.out.print("> ");
		int roomNumber;
		try {
			roomNumber = Integer.parseInt(reader.readLine());
		}
		catch (NumberFormatException e) {
			System.out.println("部屋番号が不正です。");
			return;
		}

		CheckOutRoomForm form = new CheckOutRoomForm();
		form.inputRoomNumber(roomNumber); // 部屋番号を入力する
		form.displayStayInfo();
		form.confirmPayment(); // 支払いを確定する
	}

	/** 予約キャンセル (予約キャンセル画面) — 演習6: 保守で追加 */
	private void cancelReservation() throws IOException, AppException {
		System.out.println("キャンセルする予約番号を入力してください");
		System.out.print("> ");
		int reservationNumber;
		try {
			reservationNumber = Integer.parseInt(reader.readLine());
		}
		catch (NumberFormatException e) {
			System.out.println("予約番号が不正です。");
			return;
		}

		CancelReservationForm form = new CancelReservationForm();
		form.inputReservationNumber(reservationNumber); // 予約番号を入力する
		form.displayReservationInfo(); // 予約情報を表示する
		form.confirmCancel(); // キャンセルを確定する
	}

	/** 部屋管理 (部屋管理画面) */
	private void manageRoom() throws IOException, AppException {
		System.out.println("1: 部屋を追加, 2: 全部屋を表示");
		System.out.print("> ");
		String select = reader.readLine();

		RoomManagementForm form = new RoomManagementForm();
		if ("1".equals(select)) {
			System.out.println("部屋番号を入力してください");
			System.out.print("> ");
			int roomNumber;
			try {
				roomNumber = Integer.parseInt(reader.readLine());
			}
			catch (NumberFormatException e) {
				System.out.println("部屋番号が不正です。");
				return;
			}

			System.out.println("部屋タイプを入力してください (シングル/ダブル/スイート)");
			System.out.print("> ");
			String roomType = reader.readLine();
			if (roomType == null || roomType.length() == 0) {
				System.out.println("部屋タイプが不正です。");
				return;
			}

			System.out.println("基本宿泊料を入力してください");
			System.out.print("> ");
			int basicRate;
			try {
				basicRate = Integer.parseInt(reader.readLine());
			}
			catch (NumberFormatException e) {
				System.out.println("基本宿泊料が不正です。");
				return;
			}

			form.inputRoomInfo(roomNumber, roomType, basicRate); // 部屋情報を入力する
		}
		else if ("2".equals(select)) {
			form.displayAllRooms(); // 全部屋を表示する
		}
		else {
			System.out.println("1または2を入力してください。");
		}
	}

	public static void main(String[] args) throws Exception {
		CUI cui = new CUI();
		cui.execute();
	}
}
