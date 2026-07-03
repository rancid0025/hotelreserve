package domain.reservation;

/**
 * 予約データへアクセスするための DAO インタフェース
 */
public interface ReservationDao {

	/** 予約番号を指定して予約を取得する (存在しない場合は null) */
	public Reservation getReservation(int reservationNumber) throws ReservationException;

	/** 予約情報を更新する */
	public void updateReservation(Reservation reservation) throws ReservationException;

	/** 予約を新規登録する */
	public void createReservation(Reservation reservation) throws ReservationException;
}
