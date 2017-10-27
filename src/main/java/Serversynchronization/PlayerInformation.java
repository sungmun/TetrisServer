package Serversynchronization;

public class PlayerInformation {

	private int level;
	private int score;

	public PlayerInformation() {
	}

	PlayerInformation(int level, int score){
		setLevel(level);
		setScore(score);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
