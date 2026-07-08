package domain.reservation;

import java.util.Date;

/**
 * 予約 (Entity)<br>
 * クラス図「予約」に対応。利用者が「予約する」対象であり、「対象」の部屋を1つ持つ。
 */
public class Reservation {

	/** 状態: 予約中 */
	public static final String STATUS_RESERVED = "予約中";

	/** 状態: 宿泊中 */
	public static final String STATUS_STAYING = "宿泊中";

	/** 状態: キャンセル (演習6: 保守で追加) */
	public static final String STATUS_CANCELLED = "キャンセル";

	/** -予約番号: int */
	private int reservationNumber;

	/** -予約宿泊予定日: DateTime */
	private Date stayingDate;

	/** -状態: String */
	private String status;

	/** 予約した利用者の名前 (利用者 --予約する--> 予約 の関連) */
	private String userName;

	/** 対象の部屋番号 (予約 --対象--> 部屋 の関連) */
	private int roomNumber;

	/** +状態を「宿泊中」にする() */
	public void changeStatusToStaying() {
		this.status = STATUS_STAYING;
	}

	/** +状態を「キャンセル」にする() (演習6: 保守で追加) */
	public void changeStatusToCancelled() {
		this.status = STATUS_CANCELLED;
	}

	public int getReservationNumber() {
		return reservationNumber;
	}

	public void setReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	public Date getStayingDate() {
		return stayingDate;
	}

	public void setStayingDate(Date stayingDate) {
		this.stayingDate = stayingDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
}
