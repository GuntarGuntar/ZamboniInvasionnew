
public class ShellAnimation implements Animation {

	private int universeCount = 0;
	private Universe current = null;
	private static int score = 0;
	
	public static int getScore() {
		return score;
	}

	public static void setScore(int score) {
		ShellAnimation.score = score;
	}

	public static void addScore(int score) {
		ShellAnimation.score += score;
	}

	public int getLevel() {
		return universeCount;
	}
	
	public Universe getNextUniverse() {

		universeCount++;
		
		if (universeCount == 1) {
			return new ShellUniverse();
		}
		else {
			return null;
		}

	}

	public Universe getCurrentUniverse() {
		return this.current;
	}
		
	public void restart() {
		universeCount = 0;
		current = null;
		score = 0;		
	}
	
}
