package domain.user;

/**
 * ホテル受付係 (Entity)<br>
 * クラス図「ホテル受付係」に対応。宿泊(チェックイン)に「対応」する担当者を表す。
 */
public class Receptionist {

	/** -名前: String */
	private String name;

	public Receptionist() {
	}

	public Receptionist(String name) {
		this.name = name;
	}

	/** 名前を取得する */
	public String getName() {
		return name;
	}

	/** 名前を設定する */
	public void setName(String name) {
		this.name = name;
	}
}
