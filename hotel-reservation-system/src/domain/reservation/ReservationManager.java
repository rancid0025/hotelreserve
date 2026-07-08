package domain.reservation;

import java.util.Date;

import domain.DaoFactory;

/**
 * 予約を管理する Manager クラス<br>
 * クラス図「予約」エンティティに対する操作 (作成・番号発行・詳細取得・状態変更) を提供する。
 */
public class ReservationManager {

	/** 予約番号の採番カウンタ (1001 から発行する) */
	private static int nextReservationNumber = 1001;

	/**
	 * +予約を作成する()<br>
	 * 予約を作成し、発行した予約番号を返す
	 */
	public Reservation createReservation(String userName, Date stayingDate, int roomNumber)
			throws ReservationException {
		if (stayingDate == null) {
			throw new NullPointerException("stayingDate");
		}

		Reservation reservation = new Reservation();
		reservation.setReservationNumber(issueReservationNumber()); // +予約番号を発行する(): int
		reservation.setUserName(userName);
		reservation.setStayingDate(stayingDate);
		reservation.setRoomNumber(roomNumber);
		reservation.setStatus(Reservation.STATUS_RESERVED);

		getReservationDao().createReservation(reservation);
		return reservation;
	}

	/** +予約番号を発行する(): int */
	private synchronized int issueReservationNumber() {
		return nextReservationNumber++;
	}

	/**
	 * +予約詳細を取得する(): 予約<br>
	 * 予約番号から予約を検索する (存在しない場合は例外)
	 */
	public Reservation getReservationDetail(int reservationNumber) throws ReservationException {
		Reservation reservation = getReservationDao().getReservation(reservationNumber);
		if (reservation == null) {
			ReservationException exception = new ReservationException("予約が見つかりません");
			exception.getDetailMessages().add("予約番号[" + reservationNumber + "]");
			throw exception;
		}
		return reservation;
	}

	/**
	 * +状態を「宿泊中」にする()<br>
	 * チェックイン時に予約の状態を「宿泊中」へ変更する
	 */
	public void changeStatusToStaying(int reservationNumber) throws ReservationException {
		Reservation reservation = getReservationDetail(reservationNumber);
		// 既に宿泊中の場合はチェックイン済み
		if (Reservation.STATUS_STAYING.equals(reservation.getStatus())) {
			ReservationException exception = new ReservationException("既にチェックイン済みの予約です");
			exception.getDetailMessages().add("予約番号[" + reservationNumber + "]");
			throw exception;
		}
		reservation.changeStatusToStaying();
		getReservationDao().updateReservation(reservation);
	}

	/**
	 * +予約をキャンセルする() (演習6: 保守で追加)<br>
	 * 予約の状態を「キャンセル」へ変更する。<br>
	 * 既に宿泊中(チェックイン済み)、またはキャンセル済みの予約はキャンセルできない。
	 */
	public Reservation cancelReservation(int reservationNumber) throws ReservationException {
		Reservation reservation = getReservationDetail(reservationNumber);
		// 既にチェックイン済みの予約はキャンセル不可
		if (Reservation.STATUS_STAYING.equals(reservation.getStatus())) {
			ReservationException exception = new ReservationException("チェックイン済みの予約はキャンセルできません");
			exception.getDetailMessages().add("予約番号[" + reservationNumber + "]");
			throw exception;
		}
		// 既にキャンセル済みの予約は再キャンセル不可
		if (Reservation.STATUS_CANCELLED.equals(reservation.getStatus())) {
			ReservationException exception = new ReservationException("既にキャンセル済みの予約です");
			exception.getDetailMessages().add("予約番号[" + reservationNumber + "]");
			throw exception;
		}
		reservation.changeStatusToCancelled();
		getReservationDao().updateReservation(reservation);
		return reservation;
	}

	private ReservationDao getReservationDao() {
		return DaoFactory.getInstance().getReservationDao();
	}
}
