package pugbot.core.entities;

public enum RPSMove {
	ROCK,
	PAPER,
	SCISSORS,
	FORFEIT;
	
	public static int getWinner(RPSMove m1, RPSMove m2) {
		if(m1 == m2) {
			return -1;
		} else if(m1 == ROCK) {
			return m2 == PAPER ? 1 : 0;
		} else if(m1 == PAPER) {
			return m2 == SCISSORS ? 1 : 0;
		} else if(m1 == SCISSORS) {
			return m2 == ROCK ? 1 : 0;
		} else {
			return -1;
		}
	}
}
