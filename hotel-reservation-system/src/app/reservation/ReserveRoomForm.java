package app.reservation;

import java.util.Date;

import app.AppException;
import domain.reservation.Reservation;

/**
 * 宿泊予約画面 (Boundary)<br>
 * クラス図「宿泊予約画面」に対応。
 */
public class ReserveRoomForm {

	private ReserveRoomControl reserveRoomControl = new ReserveRoomControl();

	/** 予約する利用者名 */
	private String userName;

	/** 選択された部屋タイプ */
	private String roomType;

	/** 宿泊予定日 */
	private Date stayingDate;

	/**
	 * +部屋を選択する()<br>
	 * 予約する部屋タイプを選択する
	 */
	public void selectRoom(String roomType) {
		this.roomType = roomType;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setStayingDate(Date stayingDate) {
		this.stayingDate = stayingDate;
	}

	/** 予約を実行し、作成された予約を返す */
	public Reservation submitReservation() throws AppException {
		return reserveRoomControl.reserveRoom(userName, roomType, stayingDate);
	}

	/**
	 * +予約番号を表示する()
	 */
	public void displayReservationNumber(Reservation reservation) {
		System.out.println("予約が完了しました。");
		System.out.println("予約番号: " + reservation.getReservationNumber());
		System.out.println("部屋番号: " + reservation.getRoomNumber());
	}
}
