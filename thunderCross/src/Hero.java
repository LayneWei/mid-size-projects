import java.awt.image.BufferedImage;

/**
 * Hero: is Flying Object
 */
public class Hero extends FlyingObject{
	
	private BufferedImage[] images = {};  //Hero image
	private int index = 0;                //change hero index
	
	private int doubleFire;
	private int life;
	
	/** initialize data */
	public Hero(){
		life = 3;   // life set to 3 when initialized
		doubleFire = 0;   // fire set to 0
		images = new BufferedImage[]{ShootGame.hero0, ShootGame.hero1}; //Hero pic array
		image = ShootGame.hero0;   //初始为hero0图片
		width = image.getWidth();
		height = image.getHeight();
		x = 150;
		y = 400;
	}
	
	/** 获取双倍火力 */
	public int isDoubleFire() {
		return doubleFire;
	}

	/** 设置双倍火力 */
	public void setDoubleFire(int doubleFire) {
		this.doubleFire = doubleFire;
	}
	
	/** 增加火力 */
	public void addDoubleFire(){
		doubleFire = 40;
	}
	
	/** 增命 */
	public void addLife(){  //增命
		life++;
	}
	
	/** 减命 */
	public void subtractLife(){   //减命
		life--;
	}
	
	/** 获取命 */
	public int getLife(){
		return life;
	}
	
	/** 当前物体移动了一下，相对距离，x,y鼠标位置  */
	public void moveTo(int x,int y){   
		this.x = x - width/2;
		this.y = y - height/2;
	}

	/** 越界处理 */
	@Override
	public boolean outOfBounds() {
		return false;  
	}

	/** 发射子弹 */
	public Bullet[] shoot(){   
		int xStep = width/4;      //4半
		int yStep = 20;  //步
		if(doubleFire>0){  //双倍火力
			Bullet[] bullets = new Bullet[2];
			bullets[0] = new Bullet(x+xStep,y-yStep);  //y-yStep(子弹距飞机的位置)
			bullets[1] = new Bullet(x+3*xStep,y-yStep);
			return bullets;
		}else{      //单倍火力
			Bullet[] bullets = new Bullet[1];
			bullets[0] = new Bullet(x+2*xStep,y-yStep);  
			return bullets;
		}
	}

	/** 移动 */
	@Override
	public void step() {
		if(images.length>0){
			image = images[index++/10%images.length];  //切换图片hero0，hero1
		}
	}
	
	/** 碰撞算法 */
	public boolean hit(FlyingObject other){
		
		int x1 = other.x - this.width/2;                 //x坐标最小距离
		int x2 = other.x + this.width/2 + other.width;   //x坐标最大距离
		int y1 = other.y - this.height/2;                //y坐标最小距离
		int y2 = other.y + this.height/2 + other.height; //y坐标最大距离
	
		int herox = this.x + this.width/2;               //英雄机x坐标中心点距离
		int heroy = this.y + this.height/2;              //英雄机y坐标中心点距离
		
		return herox>x1 && herox<x2 && heroy>y1 && heroy<y2;   //区间范围内为撞上了
	}
	
}










