package domain.room;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 部屋 DAO のメモリ実装<br>
 * (授業用: HSQLDB サーバ不要で動作するよう、DB の代わりにメモリ上でデータを保持する)
 */
public class RoomMemoryDao implements RoomDao {

	/** 部屋番号 → 部屋 のマップ */
	private Map<Integer, Room> rooms = new LinkedHashMap<Integer, Room>();

	public RoomMemoryDao() {
		// 初期データ (部屋番号, タイプ, 基本宿泊料)
		seed(101, "シングル", 8000);
		seed(102, "シングル", 8000);
		seed(103, "シングル", 8000);
		seed(201, "ダブル", 12000);
		seed(202, "ダブル", 12000);
		seed(301, "スイート", 30000);
	}

	private void seed(int roomNumber, String roomType, int basicRate) {
		Room room = new Room();
		room.setRoomNumber(roomNumber);
		room.setRoomType(roomType);
		room.setBasicRate(basicRate);
		rooms.put(roomNumber, room);
	}

	public List<Room> getRooms() throws RoomException {
		return new ArrayList<Room>(rooms.values());
	}

	public List<Room> getVacantRooms() throws RoomException {
		List<Room> result = new ArrayList<Room>();
		for (Room room : rooms.values()) {
			if (room.isVacant()) {
				result.add(room);
			}
		}
		return result;
	}

	public List<Room> getVacantRooms(String roomType) throws RoomException {
		List<Room> result = new ArrayList<Room>();
		for (Room room : rooms.values()) {
			if (room.isVacant() && room.getRoomType().equals(roomType)) {
				result.add(room);
			}
		}
		return result;
	}

	public Room getRoom(int roomNumber) throws RoomException {
		return rooms.get(roomNumber);
	}

	public void updateRoom(Room room) throws RoomException {
		rooms.put(room.getRoomNumber(), room);
	}

	public void createRoom(Room room) throws RoomException {
		if (rooms.containsKey(room.getRoomNumber())) {
			RoomException exception = new RoomException("部屋番号が既に存在します");
			exception.getDetailMessages().add("部屋番号[" + room.getRoomNumber() + "]");
			throw exception;
		}
		rooms.put(room.getRoomNumber(), room);
	}
}
