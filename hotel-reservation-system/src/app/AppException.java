package app;

import java.util.ArrayList;
import java.util.List;

/**
 * ホテル予約システム共通の例外クラス
 */
public class AppException extends Exception {

	/** 詳細メッセージの一覧 */
	private List<String> detailMessages = new ArrayList<String>();

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppException(String message) {
		super(message);
	}

	public List<String> getDetailMessages() {
		return detailMessages;
	}

	/** 詳細メッセージを整形して返す */
	public String getFormattedDetailMessages(String separator) {
		StringBuilder buffer = new StringBuilder();
		String message = getMessage();
		if (message != null) {
			buffer.append(message);
			buffer.append(separator);
		}
		if (detailMessages.size() > 0) {
			buffer.append("詳細:");
			buffer.append(separator);
			for (String detail : detailMessages) {
				buffer.append(detail);
				buffer.append(separator);
			}
		}
		return buffer.toString();
	}
}
