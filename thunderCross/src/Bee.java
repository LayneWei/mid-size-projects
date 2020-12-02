
import java.util.Random;

/** bee */
public class Bee extends FlyingObject implements Award{
	private int xSpeed = 1;   //x moving speed
	private int ySpeed = 2;   //y moving speed
	private int awardType;    //award type
	
	/** initilaze data */
	public Bee(){
		this.image = ShootGame.bee;
		width = image.getWidth();
		height = image.getHeight();
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH - width);
		awardType = rand.nextInt(2);   //award when initialized
	}
	
	/** award type */
	public int getType(){
		return awardType;
	}

	/** exceed bound */
	@Override
	public boolean outOfBounds() {
		return y>ShootGame.HEIGHT;
	}

	/** moving */
	@Override
	public void step() {      
		x += xSpeed;
		y += ySpeed;
		if(x > ShootGame.WIDTH-width){  
			xSpeed = -1;
		}
		if(x < 0){
			xSpeed = 1;
		}
	}
}