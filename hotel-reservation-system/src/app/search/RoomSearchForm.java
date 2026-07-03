package app.search;

import java.util.List;

import app.AppException;
import domain.room.Room;

/**
 * 宿泊部屋検索画面 (Boundary)<br>
 * クラス図「宿泊部屋検索画面」に対応。
 */
public class RoomSearchForm {

	private RoomSearchControl roomSearchControl = new RoomSearchControl();

	/** 検索条件: 部屋タイプ (null の場合は全タイプ) */
	private String roomType;

	/**
	 * +部屋検索条件を入力する()<br>
	 * 検索条件 (部屋タイプ) を設定する
	 */
	public void inputSearchCondition(String roomType) {
		this.roomType = roomType;
	}

	/**
	 * +空室一覧を表示する()<br>
	 * 検索条件に合う空室の一覧を表示する。空室が無い場合は警告を表示する。
	 */
	public void displayVacantRoomList() throws AppException {
		List<Room> vacantRooms;
		if (roomType == null || roomType.length() == 0) {
			// 条件未指定の場合は空室一覧を作成する
			vacantRooms = roomSearchControl.makeVacantRoomList();
		}
		else {
			// 部屋の空室を確認してから検索する
			if (!roomSearchControl.checkVacancy(roomType)) {
				displayWarning();
				return;
			}
			vacantRooms = roomSearchControl.searchRooms(roomType);
		}

		if (vacantRooms.size() == 0) {
			displayWarning();
			return;
		}

		System.out.println("--- 空室一覧 ---");
		System.out.println("部屋番号\tタイプ\t基本宿泊料");
		for (Room room : vacantRooms) {
			System.out.println(room.getRoomNumber() + "\t" + room.getRoomType() + "\t"
					+ room.getBasicRate() + "円");
		}
	}

	/**
	 * +警告を表示する()<br>
	 * 空室が無い場合の警告を表示する
	 */
	public void displayWarning() {
		System.out.println("【警告】条件に合う空室がありません。");
	}
}
