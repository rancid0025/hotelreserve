package domain.room;

import java.util.List;

import domain.DaoFactory;

/**
 * 部屋を管理する Manager クラス<br>
 * クラス図「部屋」エンティティに対する操作 (検索・空室数の増減・状態変更) を提供する。
 */
public class RoomManager {

	/**
	 * [空室数>0]部屋を検索する(): 部屋<br>
	 * 指定タイプの空室一覧を返す (タイプが null の場合は全タイプ)
	 */
	public List<Room> searchVacantRooms(String roomType) throws RoomException {
		RoomDao roomDao = getRoomDao();
		if (roomType == null || roomType.length() == 0) {
			return roomDao.getVacantRooms();
		}
		return roomDao.getVacantRooms(roomType);
	}

	/** 空室数を取得する */
	public int countVacantRooms(String roomType) throws RoomException {
		return searchVacantRooms(roomType).size();
	}

	/**
	 * 部屋を予約する: 空室を1つ確保し「空室数を1減らす」<br>
	 * 空室が無い場合は例外を投げる
	 */
	public Room reserveRoom(String roomType) throws RoomException {
		List<Room> vacantRooms = searchVacantRooms(roomType);
		// [空室数>0] のガード条件
		if (vacantRooms.size() == 0) {
			RoomException exception = new RoomException("空室がありません");
			exception.getDetailMessages().add("部屋タイプ[" + roomType + "]");
			throw exception;
		}
		Room room = vacantRooms.get(0);
		// 空室数を1減らす (確保した部屋を空室でなくする)
		room.markOccupied();
		getRoomDao().updateRoom(room);
		return room;
	}

	/** 空室を使用中にする() : チェックイン時に部屋を使用中状態にする */
	public void occupyRoom(int roomNumber) throws RoomException {
		Room room = getExistingRoom(roomNumber);
		room.markOccupied();
		getRoomDao().updateRoom(room);
	}

	/** 部屋を空室にする() : チェックアウト時に部屋を空室に戻す */
	public void vacateRoom(int roomNumber) throws RoomException {
		Room room = getExistingRoom(roomNumber);
		room.markVacant();
		getRoomDao().updateRoom(room);
	}

	/** 部屋を追加する (部屋管理処理から利用) */
	public void addRoom(int roomNumber, String roomType, int basicRate) throws RoomException {
		Room room = new Room();
		room.setRoomNumber(roomNumber); // +部屋番号を作成する
		room.setRoomType(roomType); // +部屋タイプを作成する
		room.setBasicRate(basicRate); // +宿泊料を作成する
		getRoomDao().createRoom(room);
	}

	/** 全部屋を取得する(): List<部屋> */
	public List<Room> getAllRooms() throws RoomException {
		return getRoomDao().getRooms();
	}

	/** 部屋番号を指定して部屋を取得する (存在しない場合は例外) */
	public Room getExistingRoom(int roomNumber) throws RoomException {
		Room room = getRoomDao().getRoom(roomNumber);
		if (room == null) {
			RoomException exception = new RoomException("部屋が見つかりません");
			exception.getDetailMessages().add("部屋番号[" + roomNumber + "]");
			throw exception;
		}
		return room;
	}

	private RoomDao getRoomDao() {
		return DaoFactory.getInstance().getRoomDao();
	}
}
