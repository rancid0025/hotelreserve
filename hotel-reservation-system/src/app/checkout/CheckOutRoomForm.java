package app.checkout;

import app.AppException;
import domain.payment.Payment;
import domain.stay.Stay;
import util.DateUtil;

/**
 * チェックアウト画面 (Boundary)<br>
 * クラス図「チェックアウト画面」に対応。
 */
public class CheckOutRoomForm {

	private CheckOutRoomControl checkOutRoomControl = new CheckOutRoomControl();

	/** 入力された部屋番号 */
	private int roomNumber;

	/**
	 * +部屋番号を入力する(部屋番号: int)
	 */
	public void inputRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	/** 宿泊情報を表示する */
	public void displayStayInfo() throws AppException {
		Stay stay = checkOutRoomControl.findStay(roomNumber);
		System.out.println("--- 宿泊情報 ---");
		System.out.println("部屋番号: " + stay.getRoomNumber());
		System.out.println("予約番号: " + stay.getReservationNumber());
		System.out.println("イン日時: " + DateUtil.convertToDateTimeString(stay.getCheckInDateTime()));
		System.out.println("担当受付係: " + stay.getReceptionistName());
	}

	/**
	 * +支払いを確定する()<br>
	 * チェックアウトを完了し、支払い料金を表示する
	 */
	public void confirmPayment() throws AppException {
		Payment payment = checkOutRoomControl.completeCheckOut(roomNumber);
		System.out.println("チェックアウトが完了しました。");
		System.out.println("支払い料金: " + payment.getAmount() + "円");
	}
}
