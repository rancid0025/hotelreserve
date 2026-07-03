package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日付ユーティリティクラス
 */
public class DateUtil {

	/** 日付書式 (yyyy/MM/dd) */
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd");

	/** 日時書式 (yyyy/MM/dd HH:mm) */
	private static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");

	static {
		DATE_FORMATTER.setLenient(false);
		DATETIME_FORMATTER.setLenient(false);
	}

	public static Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/** 日付を "yyyy/MM/dd" 形式の文字列に変換する */
	public static String convertToString(Date date) {
		if (date == null) {
			return "-";
		}
		return DATE_FORMATTER.format(date);
	}

	/** 日時を "yyyy/MM/dd HH:mm" 形式の文字列に変換する */
	public static String convertToDateTimeString(Date date) {
		if (date == null) {
			return "-";
		}
		return DATETIME_FORMATTER.format(date);
	}

	/** "yyyy/MM/dd" 形式の文字列を日付に変換する (不正な場合は null) */
	public static Date convertToDate(String dateStr) {
		Date result = null;
		try {
			if (dateStr != null) {
				result = DATE_FORMATTER.parse(dateStr);
			}
		}
		catch (ParseException e) {
			// 変換失敗時は null を返す
		}
		return result;
	}
}
