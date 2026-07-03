package domain.payment;

import java.util.ArrayList;
import java.util.List;

/**
 * 支払い DAO のメモリ実装
 */
public class PaymentMemoryDao implements PaymentDao {

	/** 支払い記録の一覧 */
	private List<Payment> payments = new ArrayList<Payment>();

	public void createPayment(Payment payment) throws PaymentException {
		payments.add(payment);
	}

	public List<Payment> getPayments() throws PaymentException {
		return new ArrayList<Payment>(payments);
	}
}
