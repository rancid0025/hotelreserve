package app;

import domain.payment.PaymentManager;
import domain.reservation.ReservationManager;
import domain.room.RoomManager;
import domain.stay.StayManager;

/**
 * Manager のインスタンスを生成する Factory クラス
 */
public class ManagerFactory {

	private static ManagerFactory instance = new ManagerFactory();

	private ManagerFactory() {
	}

	private ReservationManager reservationManager = new ReservationManager();

	private RoomManager roomManager = new RoomManager();

	private StayManager stayManager = new StayManager();

	private PaymentManager paymentManager = new PaymentManager();

	public static ManagerFactory getInstance() {
		return instance;
	}

	public ReservationManager getReservationManager() {
		return reservationManager;
	}

	public RoomManager getRoomManager() {
		return roomManager;
	}

	public StayManager getStayManager() {
		return stayManager;
	}

	public PaymentManager getPaymentManager() {
		return paymentManager;
	}
}
