package domain.stay;

import java.util.ArrayList;
import java.util.List;

/**
 * 宿泊に関する例外クラス
 */
public class StayException extends Exception {

	private List<String> detailMessages = new ArrayList<String>();

	public StayException(String message) {
		super(message);
	}

	public StayException(String message, Throwable cause) {
		super(message, cause);
	}

	public List<String> getDetailMessages() {
		return detailMessages;
	}
}
