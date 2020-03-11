package loader;

import java.io.Serializable;

public class Score implements Serializable {
	private static final long serialVersionUID = 1L;
	public String username = "";
	public int level = 0;
	public int killed = 0;
	public Score() {
		
	}
	public Score(String username, int l, int k) {
		this.username = username;
		this.level = l;
		this.killed = k;
	}
}