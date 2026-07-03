package domain.user;

/**
 * 利用者 (Entity)<br>
 * クラス図「利用者」に対応。予約を行うホテルの利用者を表す。
 */
public class User {

	/** -利用者名: String */
	private String userName;

	public User() {
	}

	public User(String userName) {
		this.userName = userName;
	}

	/** 利用者名を取得する */
	public String getUserName() {
		return userName;
	}

	/** 利用者名を設定する */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
