package domain.payment;

import java.util.ArrayList;
import java.util.List;

/**
 * 支払いに関する例外クラス
 */
public class PaymentException extends Exception {

	private List<String> detailMessages = new ArrayList<String>();

	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(String message, Throwable cause) {
		super(message, cause);
	}

	public List<String> getDetailMessages() {
		return detailMessages;
	}
}
