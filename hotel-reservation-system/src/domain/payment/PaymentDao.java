package domain.payment;

import java.util.List;

/**
 * 支払いデータへアクセスするための DAO インタフェース
 */
public interface PaymentDao {

	/** 支払い記録を新規登録する */
	public void createPayment(Payment payment) throws PaymentException;

	/** 全支払い記録を取得する */
	public List<Payment> getPayments() throws PaymentException;
}
