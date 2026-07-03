package domain.payment;

/**
 * 支払い (Entity)<br>
 * クラス図「支払い」に対応。宿泊と「精算」の関連を持つ。
 */
public class Payment {

	/** -支払い料金: int */
	private int amount;

	/** 精算対象の部屋番号 (宿泊 --精算--> 支払い の関連) */
	private int roomNumber;

	/**
	 * +支払い記録を生成する(支払い料金: int)
	 */
	public static Payment createPaymentRecord(int amount) {
		Payment payment = new Payment();
		payment.setAmount(amount);
		return payment;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
}
