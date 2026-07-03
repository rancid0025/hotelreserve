package app.management;

import java.util.List;

import app.AppException;
import domain.room.Room;

/**
 * 部屋管理画面 (Boundary)<br>
 * クラス図「部屋管理画面」に対応。
 */
public class RoomManagementForm {

	private RoomManagementControl roomManagementControl = new RoomManagementControl();

	/**
	 * +部屋情報を入力する()<br>
	 * 入力された部屋情報で部屋を追加する
	 */
	public void inputRoomInfo(int roomNumber, String roomType, int basicRate) throws AppException {
		roomManagementControl.addRoom(roomNumber, roomType, basicRate);
		System.out.println("部屋を追加しました: " + roomNumber + " (" + roomType + ", " + basicRate
				+ "円)");
	}

	/**
	 * +全部屋を表示する()
	 */
	public void displayAllRooms() throws AppException {
		List<Room> rooms = roomManagementControl.getAllRooms();
		System.out.println("--- 全部屋一覧 ---");
		System.out.println("部屋番号\tタイプ\t基本宿泊料\t空室状態");
		for (Room room : rooms) {
			System.out.println(room.getRoomNumber() + "\t" + room.getRoomType() + "\t"
					+ room.getBasicRate() + "円\t" + (room.isVacant() ? "空室" : "使用中"));
		}
	}
}
