package domain.room;

import java.util.ArrayList;
import java.util.List;

/**
 * 部屋に関する例外クラス
 */
public class RoomException extends Exception {

	private List<String> detailMessages = new ArrayList<String>();

	public RoomException(String message) {
		super(message);
	}

	public RoomException(String message, Throwable cause) {
		super(message, cause);
	}

	public List<String> getDetailMessages() {
		return detailMessages;
	}
}
