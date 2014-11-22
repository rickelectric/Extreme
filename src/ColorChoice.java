import java.awt.Graphics2D;

public class ColorChoice extends Entity {
	private Sprite p1chooser, p2chooser;
	private int buttontimer = 0, c1 = 0, c2 = 0;
	private boolean disable = false, disable2 = false;

	private static final int[] cpts = new int[] { 223, 367, 515, 663 };

	public ColorChoice(float x, float y) {
		super(x, y);
		c1 = 1;
		c2 = 3;

		p1chooser = new Sprite("choosep1", 223, 322) {
			public void setX(float x) {
				super.setX(x);
				this.boundingRect.setLocation((int) x, (int) y);
			}
		};
		p2chooser = new Sprite("choosep2", 663, 355) {
			public void setX(float x) {
				super.setX(x);
				this.boundingRect.setLocation((int) x, (int) y);
			}
		};
	}

	@Override
	public void update() {
		buttontimer++;

		// If I Am Player 1, Choose My Selection Using Keyboard W,S,E
		if (GameClient.getInstance().getMyID() == 1) {
			if (!disable && (buttontimer > 15)) {
				if (KeyboardInputService.getInstance().isW()) {
					c1 = c1 - 1;
					if (c1 < 1)
						c1 = 4;
					p1chooser.setX(cpts[c1 - 1]);
					buttontimer = 0;
					GameClient.getInstance().setMyColor(c1);
				} else if (KeyboardInputService.getInstance().isS()) {
					c1 = c1 + 1;
					if (c1 > 4)
						c1 = 1;
					p1chooser.setX(cpts[c1 - 1]);
					buttontimer = 0;
					GameClient.getInstance().setMyColor(c1);
				}
			}

			if (KeyboardInputService.getInstance().isE()) {
				GameClient.getInstance().setColorReady();
				disable = true;
			}
		} else {// Else, Update Other Player's Choice From Server
			c1 = GameClient.getInstance().getEnemyColor();
			p1chooser.setX(cpts[c1 - 1]);
			buttontimer = 0;
		}

		// If I Am Player 2, Choose My Selection Using Keyboard Up, Down, Enter
		if (GameClient.getInstance().getMyID() == 2) {
			if (!disable2 && (buttontimer > 15)) {
				if (KeyboardInputService.getInstance().isUp()) {
					c2 = c2 - 1;
					if (c2 < 1)
						c2 = 4;
					p2chooser.setX(cpts[c2 - 1]);
					buttontimer = 0;
					GameClient.getInstance().setMyColor(c2);
				}

				if (KeyboardInputService.getInstance().isDown()
						&& (buttontimer > 15)) {
					c2 = c2 + 1;
					if (c2 > 4)
						c2 = 1;
					p2chooser.setX(cpts[c2 - 1]);
					buttontimer = 0;
					GameClient.getInstance().setMyColor(c2);
				}
			}

			if (KeyboardInputService.getInstance().isEnter()) {
				GameSystem.getInstance().setColorChoice2(c2);
				disable2 = true;
			}
		} else {// Else, I Am Player 2, So Detect Player 2 Keys (Up,Down,Enter)
				// And Set To Send To Server
			c2 = GameClient.getInstance().getEnemyColor();
			p1chooser.setX(cpts[c2 - 1]);
			buttontimer = 0;
		}

	}

	@Override
	public void draw(Graphics2D g2d) {
		// TODO Auto-generated method stub
		p1chooser.draw(g2d);
		p2chooser.draw(g2d);
	}

}
