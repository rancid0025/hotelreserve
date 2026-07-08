package app.cancel;

import app.AppException;
import app.ManagerFactory;
import domain.reservation.Reservation;
import domain.reservation.ReservationException;
import domain.reservation.ReservationManager;
import domain.room.RoomException;
import domain.room.RoomManager;

/**
 * 予約キャンセル制御 (Control)<br>
 * クラス図「予約キャンセル制御」に対応。(演習6: 保守で追加)<br>
 * 「顧客が予約をキャンセルする」ユースケースを実現する。
 */
public class CancelReservationControl {

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
	 * +予約キャンセルを完了する(予約番号: int)<br>
	 * 予約の状態を「キャンセル」にし、確保していた部屋を空室に戻す
	 */
	public void completeCancel(int reservationNumber) throws AppException {
		try {
			// 予約をキャンセルする (状態を「キャンセル」にする)
			ReservationManager reservationManager = getReservationManager();
			Reservation reservation = reservationManager.cancelReservation(reservationNumber);

			// 確保していた部屋を空室に戻す
			RoomManager roomManager = getRoomManager();
			roomManager.vacateRoom(reservation.getRoomNumber());
		}
		catch (ReservationException e) {
			AppException exception = new AppException("予約キャンセルに失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (RoomException e) {
			AppException exception = new AppException("予約キャンセルに失敗しました", e);
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
}
