package domain.stay;

import java.util.Date;

import domain.DaoFactory;

/**
 * 宿泊を管理する Manager クラス<br>
 * クラス図「宿泊」エンティティに対する操作 (記録生成・詳細取得・アウト日時記録) を提供する。
 */
public class StayManager {

	/**
	 * +宿泊記録を生成する()<br>
	 * チェックイン時に宿泊記録を作成する (イン日時 = 現在日時)
	 */
	public Stay createStay(int roomNumber, int reservationNumber, String receptionistName)
			throws StayException {
		Stay stay = new Stay();
		stay.setRoomNumber(roomNumber);
		stay.setReservationNumber(reservationNumber);
		stay.setReceptionistName(receptionistName);
		stay.setCheckInDateTime(new Date());

		getStayDao().createStay(stay);
		return stay;
	}

	/**
	 * +滞在詳細を取得する(): 宿泊<br>
	 * 部屋番号から宿泊中の宿泊記録を検索する (存在しない場合は例外)
	 */
	public Stay getStayDetail(int roomNumber) throws StayException {
		Stay stay = getStayDao().getStayingStay(roomNumber);
		if (stay == null) {
			StayException exception = new StayException("宿泊データが見つかりません");
			exception.getDetailMessages().add("部屋番号[" + roomNumber + "]");
			throw exception;
		}
		return stay;
	}

	/**
	 * +アウト日時を記録する(現在日時: DateTime)<br>
	 * チェックアウト時にアウト日時を記録する
	 */
	public void recordCheckOut(Stay stay, Date currentDateTime) throws StayException {
		stay.recordCheckOutDateTime(currentDateTime);
		getStayDao().updateStay(stay);
	}

	private StayDao getStayDao() {
		return DaoFactory.getInstance().getStayDao();
	}
}
