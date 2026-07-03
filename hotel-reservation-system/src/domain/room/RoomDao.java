package domain.room;

import java.util.List;

/**
 * 部屋データへアクセスするための DAO インタフェース
 */
public interface RoomDao {

	/** 全部屋を取得する */
	public List<Room> getRooms() throws RoomException;

	/** 空室の一覧を取得する */
	public List<Room> getVacantRooms() throws RoomException;

	/** 指定タイプの空室一覧を取得する */
	public List<Room> getVacantRooms(String roomType) throws RoomException;

	/** 部屋番号を指定して部屋を取得する (存在しない場合は null) */
	public Room getRoom(int roomNumber) throws RoomException;

	/** 部屋情報を更新する */
	public void updateRoom(Room room) throws RoomException;

	/** 部屋を新規登録する */
	public void createRoom(Room room) throws RoomException;
}
