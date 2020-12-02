
import java.util.Random;

/**
 * 敌飞机: 是飞行物，也是敌人
 */
public class Airplane extends FlyingObject implements Enemy {
	private int speed = 3;  //移动步骤
	
	/** initialize game */
	public Airplane(){
		this.image = ShootGame.airplane;
		width = image.getWidth();
		height = image.getHeight();
		y = -height;          
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH - width);
	}
	
	/** get score */
	@Override
	public int getScore() {  
		return 5;
	}

	/** //exceed bound */
	@Override
	public 	boolean outOfBounds() {   
		return y>ShootGame.HEIGHT;
	}

	/** move */
	@Override
	public void step() {   
		y += speed;
	}

}

