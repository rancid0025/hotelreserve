package domain.reservation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 予約 DAO のメモリ実装
 */
public class ReservationMemoryDao implements ReservationDao {

	/** 予約番号 → 予約 のマップ */
	private Map<Integer, Reservation> reservations = new LinkedHashMap<Integer, Reservation>();

	public Reservation getReservation(int reservationNumber) throws ReservationException {
		return reservations.get(reservationNumber);
	}

	public void updateReservation(Reservation reservation) throws ReservationException {
		reservations.put(reservation.getReservationNumber(), reservation);
	}

	public void createReservation(Reservation reservation) throws ReservationException {
		reservations.put(reservation.getReservationNumber(), reservation);
	}
}
