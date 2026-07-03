package app.checkout;

import java.util.Date;

import app.AppException;
import app.ManagerFactory;
import domain.payment.Payment;
import domain.payment.PaymentException;
import domain.payment.PaymentManager;
import domain.room.Room;
import domain.room.RoomException;
import domain.room.RoomManager;
import domain.stay.Stay;
import domain.stay.StayException;
import domain.stay.StayManager;

/**
 * チェックアウト制御 (Control)<br>
 * クラス図「チェックアウト制御」に対応。
 */
public class CheckOutRoomControl {

	/**
	 * +宿泊データを検索する(部屋番号: int): 宿泊
	 */
	public Stay findStay(int roomNumber) throws AppException {
		try {
			return getStayManager().getStayDetail(roomNumber);
		}
		catch (StayException e) {
			AppException exception = new AppException("宿泊データの検索に失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	/**
	 * +チェックアウトを完了する()<br>
	 * アウト日時を記録し、支払い記録を生成して (精算)、部屋を空室に戻す
	 */
	public Payment completeCheckOut(int roomNumber) throws AppException {
		try {
			// 宿泊データを検索する
			StayManager stayManager = getStayManager();
			Stay stay = stayManager.getStayDetail(roomNumber);

			// アウト日時を記録する (現在日時)
			stayManager.recordCheckOut(stay, new Date());

			// 支払い料金 = 部屋の基本宿泊料 (1泊)
			RoomManager roomManager = getRoomManager();
			Room room = roomManager.getExistingRoom(roomNumber);
			int amount = room.getBasicRate();

			// 支払い記録を生成する(支払い料金) : 宿泊との「精算」
			PaymentManager paymentManager = getPaymentManager();
			Payment payment = paymentManager.createPayment(amount, roomNumber);

			// 部屋を空室にする
			roomManager.vacateRoom(roomNumber);

			return payment;
		}
		catch (StayException e) {
			AppException exception = new AppException("チェックアウトに失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (RoomException e) {
			AppException exception = new AppException("チェックアウトに失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (PaymentException e) {
			AppException exception = new AppException("チェックアウトに失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	private StayManager getStayManager() {
		return ManagerFactory.getInstance().getStayManager();
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}

	private PaymentManager getPaymentManager() {
		return ManagerFactory.getInstance().getPaymentManager();
	}
}
