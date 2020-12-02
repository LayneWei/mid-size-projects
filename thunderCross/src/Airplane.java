
import java.util.Random;

/**
 * �зɻ�: �Ƿ����Ҳ�ǵ���
 */
public class Airplane extends FlyingObject implements Enemy {
	private int speed = 3;  //�ƶ�����
	
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

