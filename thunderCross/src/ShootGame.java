
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ShootGame extends JPanel {
	public static final int WIDTH = 400; // Ãæ°å¿í
	public static final int HEIGHT = 654; // Ãæ°å¸ß
	/** Current game state: START RUNNING PAUSE GAME_OVER */
	private int state;
	private static final int START = 0;
	private static final int RUNNING = 1;
	private static final int PAUSE = 2;
	private static final int GAME_OVER = 3;

	private int score = 0;
	private Timer timer;
	private int interval = 1000 / 100; // time interval (ms)

	public static BufferedImage background;
	public static BufferedImage start;
	public static BufferedImage airplane;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	public static BufferedImage pause;
	public static BufferedImage gameover;

	private FlyingObject[] flyings = {}; // enemy array
	private Bullet[] bullets = {}; // bullet array
	private Hero hero = new Hero(); // hero

	static { // static block, init image resources
		try {
			background = ImageIO.read(ShootGame.class
					.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			airplane = ImageIO
					.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO
					.read(ShootGame.class.getResource("gameover.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** draw */
	@Override
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null); // draw background
		paintHero(g); // draw hero
		paintBullets(g); // draw bullet
		paintFlyingObjects(g); // draw flying objects
		paintScore(g); // draw score
		paintState(g); // draw game state
	}

	/** draw hero */
	public void paintHero(Graphics g) {
		g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
	}

	/** draw bullet */
	public void paintBullets(Graphics g) {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			g.drawImage(b.getImage(), b.getX() - b.getWidth() / 2, b.getY(),
					null);
		}
	}

	/** draw flying objects */
	public void paintFlyingObjects(Graphics g) {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			g.drawImage(f.getImage(), f.getX(), f.getY(), null);
		}
	}

	/** draw score */
	public void paintScore(Graphics g) {
		int x = 10; // x
		int y = 25; // y
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 22); // font
		g.setColor(new Color(0xFF0000));
		g.setFont(font);
		g.drawString("SCORE:" + score, x, y); // draw score
		y=y+20;
		g.drawString("LIFE:" + hero.getLife(), x, y); // draw life
	}

	/** draw game state */
	public void paintState(Graphics g) {
		switch (state) {
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER:
			g.drawImage(gameover, 0, 0, null);
			break;
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly");
		ShootGame game = new ShootGame(); // painting board
		frame.add(game); // add painting board to JFrame
		frame.setSize(WIDTH, HEIGHT); // set size
		frame.setAlwaysOnTop(true); // set to the top
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon("images/icon.jpg").getImage()); // set icon image
		frame.setLocationRelativeTo(null); // set location
		frame.setVisible(true);

		game.action(); // init
	}

	/** init */
	public void action() {
		// mouse adapter
		MouseAdapter l = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) { // mouse movement
				if (state == RUNNING) { // move hero with mouse movement
					int x = e.getX();
					int y = e.getY();
					hero.moveTo(x, y);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) { // mouse enter
				if (state == PAUSE) { // pause
					state = RUNNING;
				}
			}

			@Override
			public void mouseExited(MouseEvent e) { // mouse quit
				if (state == RUNNING) { // set to pause
					state = PAUSE;
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) { // mouse click
				switch (state) {
				case START:
					state = RUNNING; // start
					break;
				case GAME_OVER: // clean game state
					flyings = new FlyingObject[0]; // clean flying objects
					bullets = new Bullet[0]; // clean bullet
					hero = new Hero(); // rebuild hero
					score = 0; // clean score
					state = START; // set state to start
					break;
				}
			}
		};
		this.addMouseListener(l); // mouse click
		this.addMouseMotionListener(l); // mouse move

		timer = new Timer(); // timer
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (state == RUNNING) { // running state
					enterAction(); // flying object enter
					stepAction(); // move
					shootAction(); // hero shoot action
					bangAction(); // bullet shoot action
					outOfBoundsAction(); // delete out of bound flying object
					checkGameOverAction(); // check if game is over
				}
				repaint();
			}

		}, interval, interval);
	}

	int flyEnteredIndex = 0; // count flying object

	/** flying object enter */
	public void enterAction() {
		flyEnteredIndex++;
		if (flyEnteredIndex % 40 == 0) { // 400ms to generate a flying object--10*40
			FlyingObject obj = nextOne(); // randomly generate a flying object
			flyings = Arrays.copyOf(flyings, flyings.length + 1);
			flyings[flyings.length - 1] = obj;
		}
	}

	/** move one step */
	public void stepAction() {
		for (int i = 0; i < flyings.length; i++) { // flying object move one step
			FlyingObject f = flyings[i];
			f.step();
		}

		for (int i = 0; i < bullets.length; i++) { // bullet move one step
			Bullet b = bullets[i];
			b.step();
		}
		hero.step(); // hero move one step
	}

	/** flying object move one step */
	public void flyingStepAction() {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			f.step();
		}
	}

	int shootIndex = 0; // shoot count

	/** shoot */
	public void shootAction() {
		shootIndex++;
		if (shootIndex % 30 == 0) { // 300ms for one shoot
			Bullet[] bs = hero.shoot(); // hero makes shoot
			bullets = Arrays.copyOf(bullets, bullets.length + bs.length); // expand size
			System.arraycopy(bs, 0, bullets, bullets.length - bs.length,
					bs.length); // add array
		}
	}

	/** detect if bullet hit flying object */
	public void bangAction() {
		for (int i = 0; i < bullets.length; i++) { // iterate all bullet
			Bullet b = bullets[i];
			bang(b); // check if hit
		}
	}

	/** delete out of bound object */
	public void outOfBoundsAction() {
		int index = 0; // index
		FlyingObject[] flyingLives = new FlyingObject[flyings.length]; // living flying object
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			if (!f.outOfBounds()) {
				flyingLives[index++] = f; // keep flying object doesn't exceed bound
			}
		}
		flyings = Arrays.copyOf(flyingLives, index); // keep flying object doesn't exceed bound

		index = 0; // reset index to 0
		Bullet[] bulletLives = new Bullet[bullets.length];
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			if (!b.outOfBounds()) {
				bulletLives[index++] = b;
			}
		}
		bullets = Arrays.copyOf(bulletLives, index); // keep bullet doesn't exceed bound
	}

	/** check game is over */
	public void checkGameOverAction() {
		if (isGameOver()==true) {
			state = GAME_OVER; // change state
		}
	}

	/** check game is over */
	public boolean isGameOver() {
		
		for (int i = 0; i < flyings.length; i++) {
			int index = -1;
			FlyingObject obj = flyings[i];
			if (hero.hit(obj)) { // hero hit flying object
				hero.subtractLife(); // decrease life
				hero.setDoubleFire(0); // reset fire
				index = i; // record the index of hit flying object
			}
			if (index != -1) {
				FlyingObject t = flyings[index];
				flyings[index] = flyings[flyings.length - 1];
				flyings[flyings.length - 1] = t; // exchange with the last hit flying obecjt

				flyings = Arrays.copyOf(flyings, flyings.length - 1); // delete hit flying obecjt
			}
		}
		
		return hero.getLife() <= 0;
	}

	/** check bullet hit flying object */
	public void bang(Bullet bullet) {
		int index = -1; // index of hit flying object
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject obj = flyings[i];
			if (obj.shootBy(bullet)) { // check if hit
				index = i; // record the index of hit flying object
				break;
			}
		}
		if (index != -1) { // hit flying object
			FlyingObject one = flyings[index]; // record hit flying object

			FlyingObject temp = flyings[index]; // swap hit flying object with last flying object
			flyings[index] = flyings[flyings.length - 1];
			flyings[flyings.length - 1] = temp;

			flyings = Arrays.copyOf(flyings, flyings.length - 1); // delete hit flying object

			// check the type of one
			if (one instanceof Enemy) { // enemy : add score
				Enemy e = (Enemy) one;
				score += e.getScore(); // add score
			} else { // award: set award
				Award a = (Award) one;
				int type = a.getType(); // award type
				switch (type) {
				case Award.DOUBLE_FIRE:
					hero.addDoubleFire(); // double fire
					break;
				case Award.LIFE:
					hero.addLife(); // add life
					break;
				}
			}
		}
	}

	/**
	 * randomly generate flying object
	 * 
	 * @return flying object
	 */
	public static FlyingObject nextOne() {
		Random random = new Random();
		int type = random.nextInt(20); // [0,20)
		if (type < 4) {
			return new Bee();
		} else {
			return new Airplane();
		}
	}

}
