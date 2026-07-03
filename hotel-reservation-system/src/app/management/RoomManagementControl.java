package app.management;

import java.util.List;

import app.AppException;
import app.ManagerFactory;
import domain.room.Room;
import domain.room.RoomException;
import domain.room.RoomManager;

/**
 * 部屋管理処理 (Control)<br>
 * クラス図「部屋管理処理」に対応。
 */
public class RoomManagementControl {

	/**
	 * +部屋を追加する()<br>
	 * 部屋番号・部屋タイプ・基本宿泊料を指定して部屋を追加する
	 */
	public void addRoom(int roomNumber, String roomType, int basicRate) throws AppException {
		try {
			getRoomManager().addRoom(roomNumber, roomType, basicRate);
		}
		catch (RoomException e) {
			AppException exception = new AppException("部屋の追加に失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	/**
	 * +全部屋を取得する(): List<部屋>
	 */
	public List<Room> getAllRooms() throws AppException {
		try {
			return getRoomManager().getAllRooms();
		}
		catch (RoomException e) {
			AppException exception = new AppException("部屋一覧の取得に失敗しました", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}
}
