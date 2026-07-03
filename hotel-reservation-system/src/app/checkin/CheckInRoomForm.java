package app.checkin;

import app.AppException;
import domain.reservation.Reservation;
import util.DateUtil;

/**
 * チェックイン画面 (Boundary)<br>
 * クラス図「チェックイン画面」に対応。
 */
public class CheckInRoomForm {

	private CheckInRoomControl checkInRoomControl = new CheckInRoomControl();

	/** 入力された予約番号 */
	private int reservationNumber;

	/** 検索された予約 */
	private Reservation reservation;

	/**
	 * +予約番号を入力する(予約番号: int)
	 */
	public void inputReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	/**
	 * +予約情報を表示する()<br>
	 * 予約番号から予約を検索して表示する
	 */
	public void displayReservationInfo() throws AppException {
		reservation = checkInRoomControl.findReservation(reservationNumber);
		System.out.println("--- 予約情報 ---");
		System.out.println("予約番号: " + reservation.getReservationNumber());
		System.out.println("利用者名: " + reservation.getUserName());
		System.out.println("宿泊予定日: " + DateUtil.convertToString(reservation.getStayingDate()));
		System.out.println("部屋番号: " + reservation.getRoomNumber());
		System.out.println("状態: " + reservation.getStatus());
	}

	/**
	 * +予約を選択して確定する(部屋番号: int)<br>
	 * チェックイン完了を指示する
	 */
	public void confirmCheckIn(int roomNumber, String receptionistName) throws AppException {
		checkInRoomControl.completeCheckIn(reservationNumber, roomNumber, receptionistName);
		System.out.println("チェックインが完了しました。");
		System.out.println("部屋番号: " + roomNumber);
	}

	public Reservation getReservation() {
		return reservation;
	}
}
