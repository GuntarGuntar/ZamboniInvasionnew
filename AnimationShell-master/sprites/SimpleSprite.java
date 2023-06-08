import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class SimpleSprite implements DisplayableSprite {

	private final double ACCELERATION = 6000;          			//PIXELS PER SECOND PER SECOND 
	private final double ROTATION_SPEED = 180;					//degrees per second
	private final double BULLET_VELOCITY = 751;
	private final int RELOAD_TIME = 500;

	private static Image image;	
	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	
	private double velocityX = 000;        	//PIXELS PER SECOND
	private double velocityY = 0;          	//PIXELS PER SECOND

	private static Image[] rotatedImages = new Image[360];
	private AudioPlayer thrustSound = new AudioPlayer();
	private AudioPlayer bulletSound = new AudioPlayer();
	
	private double reloadTime = RELOAD_TIME;	
	private double currentAngle = 0;
	private int maxAmmo = 300;
	private int ammo = maxAmmo;
	private double health = 100;
	private int astronautsRescued = 0;
	
	private double accelerationX = 0;
	private double accelerationY = 0;		
	
	private CollisionDetection collisionDetection;
	private VirtualSprite virtual = new VirtualSprite();
	ArrayList<Class> collisionTargetTypes = new ArrayList<Class>();
	
	public SimpleSprite(double centerX, double centerY) {

		super();
		this.centerX = centerX;
		this.centerY = centerY;
		
		collisionDetection = new CollisionDetection();
		collisionTargetTypes.add(BarrierSprite.class);
		collisionTargetTypes.add(SimpleSprite.class);
		collisionTargetTypes.add(ZamboniSprite.class);
		collisionDetection.setCollisionTargetTypes(collisionTargetTypes);
		collisionDetection.setBounceFactorX(0.01);
		collisionDetection.setBounceFactorY(0.01);


		Image image = null;
		try {
			image = ImageIO.read(new File("res/gudtank.png"));
		}
		catch (IOException e) {
			System.out.print(e.toString());
		}

		//create rotated images
		if (image != null) {
			for (int i = 0; i < 360; i++) {
				rotatedImages[i] = ImageRotator.rotate(image, i);			
			}
			this.height = image.getHeight(null);
			this.width = image.getWidth(null);
		}		
				
	}
	
	public SimpleSprite(double centerX, double centerY, double height, double width) {
		this(centerX, centerY);
		
		this.height = height;
		this.width = width;
	}
	//DISPLAYABLE
	
	public Image getImage() {
		return rotatedImages[(int)currentAngle];
	}
	
	public Image getImage(int angle) {
		return rotatedImages[angle];
	}
	
	public boolean getVisible() {
		return true;
	}
	
	public double getMinX() {
		return centerX - (width / 2);
	}

	public double getMaxX() {
		return centerX + (width / 2);
	}

	public double getMinY() {
		return centerY - (height / 2);
	}

	public double getMaxY() {
		return centerY + (height / 2);
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public double getCenterX() {
		return centerX;
	};

	public double getCenterY() {
		return centerY;
	};
	
	
	public boolean getDispose() {
		return dispose;
	}

	public void setDispose(boolean dispose) {
		this.dispose = dispose;
	}	
	
	//exposed to GUI
	public int getMaxAmmo() {
		return maxAmmo;
	}

	//exposed to GUI
	public void setMaxAmmo(int maxAmmo) {
		this.maxAmmo = maxAmmo;
	}

	//exposed to GUI
	public int getAmmo() {
		return ammo;
	}

	//exposed to GUI
	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}
	
	//exposed to GUI
	public double getHealth() {
		return health;
	}

	//exposed to GUI
	public void setHealth(double health) {
		this.health = health;
	}
	
	public int getAstronautsRescued() {
		return this.astronautsRescued;
	}
	
	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		
		//LEFT
		if (keyboard.keyDown(37)) {
			currentAngle -= (ROTATION_SPEED * (actual_delta_time * 0.001));
		}
		// RIGHT
		if (keyboard.keyDown(39)) {
			currentAngle += (ROTATION_SPEED * (actual_delta_time * 0.001));
		}
		//SPACE
		if (keyboard.keyDown(32)) {
			shoot(universe, actual_delta_time);
		}
		//ensure that we don't have a negative angle
		if (currentAngle < 0) {
			currentAngle += 360;
		}	
		//ensure that we don't have an angle >= 360
		currentAngle %= 360;
		
		//calculate potential change in position based on velocity and time elapsed		
		double deltaX = actual_delta_time * 0.001 * this.velocityX;
		double deltaY = actual_delta_time * 0.001 * this.velocityY;

		//before changing position, check if the new position would result in a collision with another sprite
		//move only if no collision results. 
		boolean collidingBarrierX = checkCollisionWithBarrier(universe.getSprites(), deltaX, 0);
		boolean collidingBarrierY = checkCollisionWithBarrier(universe.getSprites(), 0, deltaY);
		
		this.height = rotatedImages[(int)currentAngle].getHeight(null);
		this.width = rotatedImages[(int)currentAngle].getWidth(null);
		
		System.out.println(String.format("vx: %5.2f; vy%5.2f", deltaX, deltaY));
		
//		if (collidingBarrierX == false) {
//			this.centerX = this.getCenterX() + deltaX;
//		}
//		else {
//			System.out.println("collidingBarrierX: " +  ConsoleUtility.describeSpriteXY(this) + ";deltaX = " + Double.toString(deltaX));			
//			this.velocityX = 0;
//		}
//		if (collidingBarrierY == false) {
//			this.centerY = this.getCenterY()  + deltaY;
//		}
//		else {
//			System.out.println("collidingBarrierY: " + ConsoleUtility.describeSpriteXY(this) + ";deltaX = " + Double.toString(deltaX));
//			this.velocityY = 0;
//		}
		
		reloadTime -= actual_delta_time;

		//only move if there is no collision with pinball in any dimension and no collision with barrier in X dimension 
		if ((collidingBarrierX == false)) {
			this.centerX += deltaX;
		}
		//only move if there is no collision with pinball in any dimension and no collision with barrier in Y dimension 
		if ((collidingBarrierY == false)) {
			this.centerY += deltaY;
		}
		
		collisionDetection.calculate2DBounce(virtual, this, universe.getSprites(), velocityX, velocityY, actual_delta_time);
		this.centerX = virtual.getCenterX();
		this.centerY = virtual.getCenterY();
		this.velocityX = virtual.getVelocityX();
		this.velocityY = virtual.getVelocityY();			

		this.velocityX = this.velocityX + accelerationX * 0.001 * actual_delta_time;
		this.velocityY = this.velocityY + accelerationY * 0.001 * actual_delta_time;
		
//		checkAstronautPickup(universe);	
	}

//		private void checkAstronautPickup(Universe universe) {
//			
//			for (DisplayableSprite sprite : universe.getSprites()) {
//				
//				if (sprite instanceof BarrierSprite) {
//									
//					if (CollisionDetection.pixelBasedOverlaps(this, sprite))
//					{
//						if (sprite instanceof BarrierSprite) {
//							System.out.println("detecting");
//							//astronaut pickup
//						
//						}
//						if (sprite instanceof AsteroidSprite) {
//							((AsteroidSprite)sprite).setDispose(true);
//							//collision with asteroid
//							//damage is proportional to size of asteroid
//							this.health -= sprite.getWidth() * 0.2;
//						}
//						if (sprite instanceof UFOSprite) {
//							//collision with UFO
//							((UFOSprite)sprite).setDispose(true);
//							this.health -= 10;
//						}
//					}
//				}
//			}
//			
//		}

	public void shoot(Universe universe, long actual_delta_time) {

		if (reloadTime <= 0) {

			double angleInRadians = Math.toRadians(currentAngle);
			double bulletVelocityX = Math.cos(angleInRadians) * BULLET_VELOCITY + velocityX;
			double bulletVelocityY = Math.sin(angleInRadians) * BULLET_VELOCITY + velocityY;

			double bulletCurrentX = this.getCenterX();
			double bulletCurrentY = this.getCenterY();

			BulletSprite bullet = new BulletSprite(bulletCurrentX, bulletCurrentY, bulletVelocityX, bulletVelocityY);
			universe.getSprites().add(bullet);
			if (bulletSound.isPlayCompleted()) {
				bulletSound.playAsynchronous("res/missile.wav");
			}

			this.ammo--;

			reloadTime = RELOAD_TIME;
			
			double angleInRadians1 = Math.toRadians(currentAngle);
			velocityX += Math.cos(angleInRadians1) * ACCELERATION * actual_delta_time * 0.001;
			velocityY += Math.sin(angleInRadians1) * ACCELERATION * actual_delta_time * 0.001;
			
			collisionDetection.calculate2DBounce(virtual, this, universe.getSprites(), velocityX, velocityY, actual_delta_time);
			this.centerX = virtual.getCenterX();
			this.centerY = virtual.getCenterY();
			this.velocityX = virtual.getVelocityX();
			this.velocityY = virtual.getVelocityY();			

			this.velocityX = this.velocityX + accelerationX * 0.001 * actual_delta_time;
			this.velocityY = this.velocityY + accelerationY * 0.001 * actual_delta_time;

		}
	}
	private boolean checkCollisionWithBarrier(ArrayList<DisplayableSprite> sprites, double deltaX, double deltaY) {

		//deltaX and deltaY represent the potential change in position
		boolean colliding = false;

		for (DisplayableSprite sprite : sprites) {
			if (sprite instanceof BarrierSprite) {
				if (CollisionDetection.overlaps(this.getMinX() + deltaX, this.getMinY() + deltaY, 
						this.getMaxX()  + deltaX, this.getMaxY() + deltaY, 
						sprite.getMinX(),sprite.getMinY(), 
						sprite.getMaxX(), sprite.getMaxY())) {
					colliding = true;
					break;					
				}
			}
			if (sprite instanceof ZamboniSprite) {
				if (CollisionDetection.overlaps(this.getMinX() + deltaX, this.getMinY() + deltaY, 
						this.getMaxX()  + deltaX, this.getMaxY() + deltaY, 
						sprite.getMinX(),sprite.getMinY(), 
						sprite.getMaxX(), sprite.getMaxY())) {
					colliding = true;
					this.dispose = true;
					break;					
				}
			}
		}		
		return colliding;		
	}
}
