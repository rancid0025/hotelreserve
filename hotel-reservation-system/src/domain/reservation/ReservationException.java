package domain.reservation;

import java.util.ArrayList;
import java.util.List;

/**
 * 予約に関する例外クラス
 */
public class ReservationException extends Exception {

	private List<String> detailMessages = new ArrayList<String>();

	public ReservationException(String message) {
		super(message);
	}

	public ReservationException(String message, Throwable cause) {
		super(message, cause);
	}

	public List<String> getDetailMessages() {
		return detailMessages;
	}
}
