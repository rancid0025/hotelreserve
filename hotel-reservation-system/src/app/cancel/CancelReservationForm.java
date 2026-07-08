package app.cancel;

import app.AppException;
import domain.reservation.Reservation;
import util.DateUtil;

/**
 * 予約キャンセル画面 (Boundary)<br>
 * クラス図「予約キャンセル画面」に対応。(演習6: 保守で追加)
 */
public class CancelReservationForm {

	private CancelReservationControl cancelReservationControl = new CancelReservationControl();

	/** 入力された予約番号 */
	private int reservationNumber;

	/**
	 * +予約番号を入力する(予約番号: int)
	 */
	public void inputReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	/**
	 * +予約情報を表示する()<br>
	 * 予約番号から予約を検索して表示する (キャンセル前の確認用)
	 */
	public void displayReservationInfo() throws AppException {
		Reservation reservation = cancelReservationControl.findReservation(reservationNumber);
		System.out.println("--- 予約情報 ---");
		System.out.println("予約番号: " + reservation.getReservationNumber());
		System.out.println("利用者名: " + reservation.getUserName());
		System.out.println("宿泊予定日: " + DateUtil.convertToString(reservation.getStayingDate()));
		System.out.println("部屋番号: " + reservation.getRoomNumber());
		System.out.println("状態: " + reservation.getStatus());
	}

	/**
	 * +キャンセルを確定する()<br>
	 * 予約をキャンセルし、確保していた部屋を解放する
	 */
	public void confirmCancel() throws AppException {
		cancelReservationControl.completeCancel(reservationNumber);
		System.out.println("予約をキャンセルしました。");
	}
}
