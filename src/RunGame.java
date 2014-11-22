
public class RunGame {
	public static void main(String[] args) {
		// get new game system
		GameSystem game = GameSystem.getInstance();
		GameClient.getInstance().addObserver(game);
		// run game
		game.run();
	}
}
