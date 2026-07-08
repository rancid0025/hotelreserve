package app.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import app.AppException;
import app.ManagerFactory;
import app.checkin.CheckInRoomControl;
import app.checkout.CheckOutRoomControl;
import app.reservation.ReserveRoomControl;
import domain.payment.Payment;
import domain.reservation.Reservation;
import domain.room.Room;
import domain.room.RoomManager;
import domain.stay.Stay;
import util.DateUtil;

/**
 * ホテル予約システムの GUI (Boundary)<br>
 * 既存のドメイン層 (Control / Manager) をそのまま再利用する Swing 画面。<br>
 * コア機能 (空室検索・宿泊予約・チェックイン・チェックアウト) を提供する。
 */
public class HotelReservationGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	/** 結果・メッセージを表示する共通エリア */
	private final JTextArea outputArea = new JTextArea(10, 60);

	public HotelReservationGUI() {
		setTitle("ホテル予約システム");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(760, 620);
		setLocationRelativeTo(null);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("空室検索", createSearchPanel());
		tabs.addTab("宿泊予約", createReservePanel());
		tabs.addTab("チェックイン", createCheckInPanel());
		tabs.addTab("チェックアウト", createCheckOutPanel());

		outputArea.setEditable(false);
		outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		JScrollPane outScroll = new JScrollPane(outputArea);
		outScroll.setBorder(BorderFactory.createTitledBorder("結果 / メッセージ"));

		setLayout(new BorderLayout(8, 8));
		add(tabs, BorderLayout.CENTER);
		add(outScroll, BorderLayout.SOUTH);
	}

	// ============================================================
	// 各タブの構築
	// ============================================================

	/** 空室検索タブ */
	private JPanel createSearchPanel() {
		final JComboBox<String> typeBox = new JComboBox<String>(
				new String[] { "全タイプ", "シングル", "ダブル", "スイート" });
		JButton searchButton = new JButton("空室を検索する");

		searchButton.addActionListener(e -> {
			try {
				String type = (String) typeBox.getSelectedItem();
				String roomType = "全タイプ".equals(type) ? null : type;
				List<Room> rooms = getRoomManager().searchVacantRooms(roomType);
				if (rooms.isEmpty()) {
					print("【空室検索】条件に合う空室がありません。 (" + type + ")");
					return;
				}
				print("【空室検索】" + type + " の空室一覧");
				print("  部屋番号  タイプ    基本宿泊料");
				for (Room room : rooms) {
					print(String.format("  %-8d%-8s%,d円",
							room.getRoomNumber(), room.getRoomType(), room.getBasicRate()));
				}
			}
			catch (Exception ex) {
				handleError(ex);
			}
		});

		JPanel form = verticalForm(
				row("部屋タイプ", typeBox));
		return actionPanel(form, searchButton);
	}

	/** 宿泊予約タブ */
	private JPanel createReservePanel() {
		final JTextField userNameField = new JTextField(16);
		final JComboBox<String> typeBox = new JComboBox<String>(
				new String[] { "シングル", "ダブル", "スイート" });
		final JTextField dateField = new JTextField("2026/07/10", 12);
		JButton reserveButton = new JButton("予約する");

		reserveButton.addActionListener(e -> {
			try {
				String userName = userNameField.getText().trim();
				if (userName.isEmpty()) {
					print("【予約】利用者名を入力してください。");
					return;
				}
				String roomType = (String) typeBox.getSelectedItem();
				Date stayingDate = DateUtil.convertToDate(dateField.getText().trim());
				if (stayingDate == null) {
					print("【予約】宿泊予定日を yyyy/mm/dd の形式で入力してください。");
					return;
				}
				Reservation reservation = new ReserveRoomControl()
						.reserveRoom(userName, roomType, stayingDate);
				print("【予約】予約が完了しました。");
				print("  予約番号: " + reservation.getReservationNumber());
				print("  部屋番号: " + reservation.getRoomNumber());
			}
			catch (Exception ex) {
				handleError(ex);
			}
		});

		JPanel form = verticalForm(
				row("利用者名", userNameField),
				row("部屋タイプ", typeBox),
				row("宿泊予定日", dateField));
		return actionPanel(form, reserveButton);
	}

	/** チェックインタブ */
	private JPanel createCheckInPanel() {
		final JTextField reservationNumberField = new JTextField(10);
		final JTextField receptionistField = new JTextField(12);
		JButton infoButton = new JButton("予約情報を照会する");
		JButton checkInButton = new JButton("チェックインする");

		infoButton.addActionListener(e -> {
			try {
				int no = parseInt(reservationNumberField.getText(), "予約番号");
				if (no == Integer.MIN_VALUE) {
					return;
				}
				Reservation r = new CheckInRoomControl().findReservation(no);
				printReservation("チェックイン照会", r);
			}
			catch (Exception ex) {
				handleError(ex);
			}
		});

		checkInButton.addActionListener(e -> {
			try {
				int no = parseInt(reservationNumberField.getText(), "予約番号");
				if (no == Integer.MIN_VALUE) {
					return;
				}
				String receptionist = receptionistField.getText().trim();
				if (receptionist.isEmpty()) {
					receptionist = "-";
				}
				CheckInRoomControl control = new CheckInRoomControl();
				Reservation r = control.findReservation(no); // 予約から部屋番号を得る
				control.completeCheckIn(no, r.getRoomNumber(), receptionist);
				print("【チェックイン】完了しました。");
				print("  予約番号: " + no + " / 部屋番号: " + r.getRoomNumber()
						+ " / 担当受付係: " + receptionist);
			}
			catch (Exception ex) {
				handleError(ex);
			}
		});

		JPanel form = verticalForm(
				row("予約番号", reservationNumberField),
				row("担当受付係名", receptionistField));
		return actionPanel(form, infoButton, checkInButton);
	}

	/** チェックアウトタブ */
	private JPanel createCheckOutPanel() {
		final JTextField roomNumberField = new JTextField(10);
		JButton infoButton = new JButton("宿泊情報を照会する");
		JButton checkOutButton = new JButton("チェックアウトする");

		infoButton.addActionListener(e -> {
			try {
				int no = parseInt(roomNumberField.getText(), "部屋番号");
				if (no == Integer.MIN_VALUE) {
					return;
				}
				Stay stay = new CheckOutRoomControl().findStay(no);
				print("【チェックアウト照会】");
				print("  部屋番号: " + stay.getRoomNumber());
				print("  予約番号: " + stay.getReservationNumber());
				print("  イン日時: " + DateUtil.convertToDateTimeString(stay.getCheckInDateTime()));
				print("  担当受付係: " + stay.getReceptionistName());
			}
			catch (Exception ex) {
				handleError(ex);
			}
		});

		checkOutButton.addActionListener(e -> {
			try {
				int no = parseInt(roomNumberField.getText(), "部屋番号");
				if (no == Integer.MIN_VALUE) {
					return;
				}
				Payment payment = new CheckOutRoomControl().completeCheckOut(no);
				print("【チェックアウト】完了しました。");
				print("  支払い料金: " + String.format("%,d円", payment.getAmount()));
			}
			catch (Exception ex) {
				handleError(ex);
			}
		});

		JPanel form = verticalForm(
				row("部屋番号", roomNumberField));
		return actionPanel(form, infoButton, checkOutButton);
	}

	// ============================================================
	// 表示・入力のヘルパー
	// ============================================================

	private void printReservation(String title, Reservation r) {
		print("【" + title + "】");
		print("  予約番号: " + r.getReservationNumber());
		print("  利用者名: " + r.getUserName());
		print("  宿泊予定日: " + DateUtil.convertToString(r.getStayingDate()));
		print("  部屋番号: " + r.getRoomNumber());
		print("  状態: " + r.getStatus());
	}

	/** 出力エリアに1行追記する */
	private void print(String message) {
		outputArea.append(message + "\n");
		outputArea.setCaretPosition(outputArea.getDocument().getLength());
	}

	/** 例外を結果エリアに表示する */
	private void handleError(Exception ex) {
		if (ex instanceof AppException) {
			AppException ae = (AppException) ex;
			print("【エラー】" + ae.getMessage());
			for (String detail : ae.getDetailMessages()) {
				print("  " + detail);
			}
		}
		else {
			print("【エラー】" + ex.getMessage());
		}
	}

	/** 文字列を int に変換する (不正なときはメッセージを出して MIN_VALUE を返す) */
	private int parseInt(String text, String fieldName) {
		try {
			return Integer.parseInt(text.trim());
		}
		catch (NumberFormatException e) {
			print("【入力エラー】" + fieldName + "は数値で入力してください。");
			return Integer.MIN_VALUE;
		}
	}

	/** ラベル + 入力欄 の1行を作る */
	private JPanel row(String label, JComponent field) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
		JLabel labelComponent = new JLabel(label + "：");
		labelComponent.setPreferredSize(new Dimension(90, 24));
		panel.add(labelComponent);
		panel.add(field);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return panel;
	}

	/** 複数の行を縦に並べたフォームを作る */
	private JPanel verticalForm(JPanel... rows) {
		JPanel form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
		for (JPanel row : rows) {
			form.add(row);
		}
		return form;
	}

	/** フォーム + 操作ボタン群 をまとめたタブ用パネルを作る */
	private JPanel actionPanel(JPanel form, JButton... buttons) {
		JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
		for (JButton button : buttons) {
			buttonBar.add(button);
		}

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		content.add(form);
		content.add(Box.createVerticalStrut(8));
		buttonBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		content.add(buttonBar);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(content, BorderLayout.NORTH);
		return wrapper;
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}

	// ============================================================
	// エントリポイント
	// ============================================================

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception ignored) {
				// 既定のルック&フィールで続行する
			}
			new HotelReservationGUI().setVisible(true);
		});
	}
}
