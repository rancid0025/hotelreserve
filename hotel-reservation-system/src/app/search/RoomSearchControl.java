package app.search;

import java.util.List;

import app.AppException;
import app.ManagerFactory;
import domain.room.Room;
import domain.room.RoomException;
import domain.room.RoomManager;

/**
 * 宿泊部屋検索処理 (Control)<br>
 * クラス図「宿泊部屋検索処理」に対応。
 */
public class RoomSearchControl {

	/**
	 * +部屋を検索する()<br>
	 * 指定タイプの空室を検索する
	 */
	public List<Room> searchRooms(String roomType) throws AppException {
		try {
			return getRoomManager().searchVacantRooms(roomType);
		}
		catch (RoomException e) {
			throw createAppException(e);
		}
	}

	/**
	 * +部屋の空室を確認する()<br>
	 * 指定タイプに空室があるかを確認する
	 */
	public boolean checkVacancy(String roomType) throws AppException {
		try {
			return getRoomManager().countVacantRooms(roomType) > 0;
		}
		catch (RoomException e) {
			throw createAppException(e);
		}
	}

	/**
	 * +空室一覧を作成する(): List<部屋><br>
	 * 全タイプの空室一覧を作成する
	 */
	public List<Room> makeVacantRoomList() throws AppException {
		try {
			return getRoomManager().searchVacantRooms(null);
		}
		catch (RoomException e) {
			throw createAppException(e);
		}
	}

	private AppException createAppException(RoomException e) {
		AppException exception = new AppException("部屋の検索に失敗しました", e);
		exception.getDetailMessages().add(e.getMessage());
		exception.getDetailMessages().addAll(e.getDetailMessages());
		return exception;
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}
}
