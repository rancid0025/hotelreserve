package domain;

import domain.payment.PaymentDao;
import domain.payment.PaymentMemoryDao;
import domain.reservation.ReservationDao;
import domain.reservation.ReservationMemoryDao;
import domain.room.RoomDao;
import domain.room.RoomMemoryDao;
import domain.stay.StayDao;
import domain.stay.StayMemoryDao;

/**
 * DAO のインスタンスを生成する Factory クラス<br>
 * (授業用にメモリ実装の DAO を返す。DB 実装に切り替える場合はここを変更する)
 */
public class DaoFactory {

	private static DaoFactory instance = new DaoFactory();

	private ReservationDao reservationDao = new ReservationMemoryDao();

	private RoomDao roomDao = new RoomMemoryDao();

	private StayDao stayDao = new StayMemoryDao();

	private PaymentDao paymentDao = new PaymentMemoryDao();

	private DaoFactory() {
	}

	public static DaoFactory getInstance() {
		return instance;
	}

	public ReservationDao getReservationDao() {
		return reservationDao;
	}

	public RoomDao getRoomDao() {
		return roomDao;
	}

	public StayDao getStayDao() {
		return stayDao;
	}

	public PaymentDao getPaymentDao() {
		return paymentDao;
	}
}
