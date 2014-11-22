import java.awt.Graphics2D;

public class Paddle2 extends Entity {
	private Sprite paddle;
	private int ySpeed;
	private float paddleheight;
	private float paddlewidth;
	private int move;
	private int color;

	public Paddle2(float x, float y, int colorChoice) {
		super(x, y);
		// TODO Auto-generated constructor stub
		color = colorChoice;
		if (color == 1)
			paddle = new Sprite("paddle", this.x, this.y);
		if (color == 2)
			paddle = new Sprite("paddle2", this.x, this.y);
		if (color == 3)
			paddle = new Sprite("paddle3", this.x, this.y);
		if (color == 4)
			paddle = new Sprite("paddle4", this.x, this.y);
		ySpeed = 15;
		this.paddleheight = 150;
		this.paddlewidth = 68;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		move = 0;

		if (GameClient.getInstance().getMyID() == 1) {
			float py = GameClient.getInstance().getEnemyLocation();

			if (py < y)
				move = -2;
			else if (py > y)
				move = +2;

			this.y = py;
			paddle.setY(this.y);
		} else {
			if (KeyboardInputService.getInstance().isUp()) {
				if (this.y - ySpeed < 0)
					this.y = 0;
				else {
					this.y -= this.ySpeed;
					move = -2;
				}
			}

			if (KeyboardInputService.getInstance().isDown()) {
				if (this.y + this.paddleheight + ySpeed > GameSystem
						.getInstance().getScreenHeight())
					this.y = (GameSystem.getInstance().getScreenHeight() - this.paddleheight);
				else {
					this.y += this.ySpeed;
					move = 2;
				}
			}
			
			GameClient.getInstance().setMyLocation(this.y);
			paddle.setY(this.y);
		}
	}

	@Override
	public void draw(Graphics2D g2d) {
		// TODO Auto-generated method stub
		paddle.draw(g2d);
	}

	public float getPaddleWidth() {
		return paddlewidth;
	}

	public float getPaddleHeight() {
		return paddleheight;
	}

	public int getPlayerMove() {
		return move;
	}

}
