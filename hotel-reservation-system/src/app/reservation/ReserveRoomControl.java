package app.reservation;

import java.util.Date;

import app.AppException;
import app.ManagerFactory;
import domain.reservation.Reservation;
import domain.reservation.ReservationException;
import domain.reservation.ReservationManager;
import domain.room.Room;
import domain.room.RoomException;
import domain.room.RoomManager;

/**
 * 宿泊予約処理 (Control)<br>
 * クラス図「宿泊予約処理」に対応。
 */
public class ReserveRoomControl {

	/**
	 * +部屋を予約する()<br>
	 * 空室を1つ確保して (空室数を1減らす)、予約を作成する
	 */
	public Reservation reserveRoom(String userName, String roomType, Date stayingDate)
			throws AppException {
		try {
			// 部屋を確保する (空室数を1減らす)
			RoomManager roomManager = getRoomManager();
			Room room = roomManager.reserveRoom(roomType);

			// 予約を作成する (予約番号を発行する)
			ReservationManager reservationManager = getReservationManager();
			return reservationManager.createReservation(userName, stayingDate,
					room.getRoomNumber());
		}
		catch (RoomException e) {
			AppException exception = new AppException("予約に失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (ReservationException e) {
			AppException exception = new AppException("予約に失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}

	private ReservationManager getReservationManager() {
		return ManagerFactory.getInstance().getReservationManager();
	}
}
