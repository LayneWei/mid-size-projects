
/**
 * Bullet:Flying Object
 */
public class Bullet extends FlyingObject {
	private int speed = 3;  //moving speed
	
	/** initialize data */
	public Bullet(int x,int y){
		this.x = x;
		this.y = y;
		this.image = ShootGame.bullet;
	}

	/** moving */
	@Override
	public void step(){   
		y-=speed;
	}

	/** exceed bound */
	@Override
	public boolean outOfBounds() {
		return y<-height;
	}

}
