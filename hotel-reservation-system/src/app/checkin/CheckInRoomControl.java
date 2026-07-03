package app.checkin;

import app.AppException;
import app.ManagerFactory;
import domain.reservation.Reservation;
import domain.reservation.ReservationException;
import domain.reservation.ReservationManager;
import domain.room.RoomException;
import domain.room.RoomManager;
import domain.stay.StayException;
import domain.stay.StayManager;

/**
 * チェックイン制御 (Control)<br>
 * クラス図「チェックイン制御」に対応。
 */
public class CheckInRoomControl {

	/**
	 * +予約を検索する(予約番号: int): 予約
	 */
	public Reservation findReservation(int reservationNumber) throws AppException {
		try {
			return getReservationManager().getReservationDetail(reservationNumber);
		}
		catch (ReservationException e) {
			AppException exception = new AppException("予約の検索に失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	/**
	 * +チェックイン完了を指示する(部屋番号: int)<br>
	 * 予約の状態を「宿泊中」にし、部屋を使用中にして、宿泊記録を生成する
	 */
	public void completeCheckIn(int reservationNumber, int roomNumber, String receptionistName)
			throws AppException {
		try {
			// 予約の状態を「宿泊中」にする
			ReservationManager reservationManager = getReservationManager();
			reservationManager.changeStatusToStaying(reservationNumber);

			// 空室を使用中にする
			RoomManager roomManager = getRoomManager();
			roomManager.occupyRoom(roomNumber);

			// 宿泊記録を生成する (ホテル受付係が対応)
			StayManager stayManager = getStayManager();
			stayManager.createStay(roomNumber, reservationNumber, receptionistName);
		}
		catch (ReservationException e) {
			AppException exception = new AppException("チェックインに失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (RoomException e) {
			AppException exception = new AppException("チェックインに失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (StayException e) {
			AppException exception = new AppException("チェックインに失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	private ReservationManager getReservationManager() {
		return ManagerFactory.getInstance().getReservationManager();
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}

	private StayManager getStayManager() {
		return ManagerFactory.getInstance().getStayManager();
	}
}
