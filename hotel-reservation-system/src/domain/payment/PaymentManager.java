package domain.payment;

import domain.DaoFactory;

/**
 * 支払いを管理する Manager クラス<br>
 * クラス図「支払い」エンティティに対する操作 (支払い記録の生成) を提供する。
 */
public class PaymentManager {

	/**
	 * +支払い記録を生成する(支払い料金: int)<br>
	 * チェックアウト(精算)時に支払い記録を作成する
	 */
	public Payment createPayment(int amount, int roomNumber) throws PaymentException {
		Payment payment = Payment.createPaymentRecord(amount);
		payment.setRoomNumber(roomNumber);

		getPaymentDao().createPayment(payment);
		return payment;
	}

	private PaymentDao getPaymentDao() {
		return DaoFactory.getInstance().getPaymentDao();
	}
}
