package domain.room;

/**
 * 部屋 (Entity)<br>
 * クラス図「部屋」に対応。
 */
public class Room {

	/** -部屋番号: int */
	private int roomNumber;

	/** -部屋タイプ: String (例: シングル / ダブル / スイート) */
	private String roomType;

	/** -基本宿泊料: int */
	private int basicRate;

	/** -空室状態: boolean (true = 空室) */
	private boolean vacant = true;

	/** +部屋番号を作成する(部屋番号: int) */
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	/** +部屋タイプを作成する(部屋タイプ: String) */
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	/** +宿泊料を作成する(宿泊料: int) */
	public void setBasicRate(int basicRate) {
		this.basicRate = basicRate;
	}

	/** +基本宿泊料を取得する(): int */
	public int getBasicRate() {
		return basicRate;
	}

	/** +空室を使用中にする() */
	public void markOccupied() {
		this.vacant = false;
	}

	/** +部屋を空室にする() */
	public void markVacant() {
		this.vacant = true;
	}

	/** 部屋番号を取得する */
	public int getRoomNumber() {
		return roomNumber;
	}

	/** 部屋タイプを取得する */
	public String getRoomType() {
		return roomType;
	}

	/** 空室状態を取得する (true = 空室) */
	public boolean isVacant() {
		return vacant;
	}
}
