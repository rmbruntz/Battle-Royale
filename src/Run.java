
public class Run {

	public static void main(String[] args) {
		Map m = new Map(10, 10);
		char character = 'A';
		for (int i = 0; i < 26; i++) {
			m.addPlayer(new Player(character, Math.random(), Math.random(), m, (int)Math.floor(Math.random()*(m.getX())), (int)Math.floor(Math.random()*(m.getY()))));
			character++;
		}
		
		m.playGame(1, 5);
		
	}
}
