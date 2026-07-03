package domain.stay;

/**
 * 宿泊データへアクセスするための DAO インタフェース
 */
public interface StayDao {

	/** 部屋番号を指定して宿泊中の宿泊記録を取得する (存在しない場合は null) */
	public Stay getStayingStay(int roomNumber) throws StayException;

	/** 宿泊記録を更新する */
	public void updateStay(Stay stay) throws StayException;

	/** 宿泊記録を新規登録する */
	public void createStay(Stay stay) throws StayException;
}
