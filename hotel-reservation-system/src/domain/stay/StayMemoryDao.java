package domain.stay;

import java.util.ArrayList;
import java.util.List;

/**
 * 宿泊 DAO のメモリ実装
 */
public class StayMemoryDao implements StayDao {

	/** 宿泊記録の一覧 */
	private List<Stay> stays = new ArrayList<Stay>();

	public Stay getStayingStay(int roomNumber) throws StayException {
		for (Stay stay : stays) {
			// 部屋 1 -- 0..1 宿泊: 宿泊中 (アウト日時未記録) の記録を探す
			if (stay.getRoomNumber() == roomNumber && stay.isStaying()) {
				return stay;
			}
		}
		return null;
	}

	public void updateStay(Stay stay) throws StayException {
		// メモリ実装のため参照が共有されており、更新処理は不要
	}

	public void createStay(Stay stay) throws StayException {
		stays.add(stay);
	}
}
