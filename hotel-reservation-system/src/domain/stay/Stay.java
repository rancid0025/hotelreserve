package domain.stay;

import java.util.Date;

/**
 * 宿泊 (Entity)<br>
 * クラス図「宿泊」に対応。部屋 1 --記録-- 0..1 宿泊 の関連を持ち、
 * ホテル受付係が「対応」し、支払いと「精算」の関連を持つ。
 */
public class Stay {

	/** -イン日時: DateTime */
	private Date checkInDateTime;

	/** -アウト日時: DateTime (チェックアウト前は null) */
	private Date checkOutDateTime;

	/** 宿泊している部屋の番号 (部屋 --記録--> 宿泊 の関連) */
	private int roomNumber;

	/** もととなった予約の番号 */
	private int reservationNumber;

	/** 対応したホテル受付係の名前 (ホテル受付係 --対応--> 宿泊 の関連) */
	private String receptionistName;

	/**
	 * +アウト日時を記録する(現在日時: DateTime)
	 */
	public void recordCheckOutDateTime(Date currentDateTime) {
		this.checkOutDateTime = currentDateTime;
	}

	/** 宿泊中かどうか (アウト日時が未記録なら宿泊中) */
	public boolean isStaying() {
		return checkOutDateTime == null;
	}

	public Date getCheckInDateTime() {
		return checkInDateTime;
	}

	public void setCheckInDateTime(Date checkInDateTime) {
		this.checkInDateTime = checkInDateTime;
	}

	public Date getCheckOutDateTime() {
		return checkOutDateTime;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public int getReservationNumber() {
		return reservationNumber;
	}

	public void setReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	public String getReceptionistName() {
		return receptionistName;
	}

	public void setReceptionistName(String receptionistName) {
		this.receptionistName = receptionistName;
	}
}
