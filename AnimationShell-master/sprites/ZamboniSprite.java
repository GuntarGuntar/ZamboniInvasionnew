import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/*
 * This class is an example of how to implement collision detection and bouncing for multiple sprites.
 * Refer to the update() method for a detailed explanation.
 */

public class ZamboniSprite implements DisplayableSprite {

	private static Image image;	
	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	

	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;

	//PIXELS PER SECOND PER SECOND
	private double accelerationX = 0;
	private double accelerationY = 0;		
	private double velocityX = 300;
	private double velocityY = 300;
	
	//required for advanced collision detection
	private CollisionDetection collisionDetection;
	private VirtualSprite virtual = new VirtualSprite();
	ArrayList<Class> collisionTargetTypes = new ArrayList<Class>();

	public ZamboniSprite(double centerX, double centerY, double velocityX, double velocityY) {

		super();
		this.centerX = centerX;
		this.centerY = centerY;	

		/*
		 * Instantiation of the CollisionDetection object. As this sprite uses the calculate2DBounce method, it needs to set certain
		 * characteristics
		 */
		collisionDetection = new CollisionDetection();
		collisionTargetTypes.add(BarrierSprite.class);
		collisionTargetTypes.add(SimpleSprite.class);
		collisionTargetTypes.add(ZamboniSprite.class);
		collisionDetection.setCollisionTargetTypes(collisionTargetTypes);
		collisionDetection.setBounceFactorX(1);
		collisionDetection.setBounceFactorY(1);

		this.velocityX = velocityX;
		this.velocityY = velocityY;

		if (image == null) {
			try {
				image = ImageIO.read(new File("res/zamboni.png"));
			}
			catch (IOException e) {
				System.err.println(e.toString());
			}
		}

		this.height = HEIGHT;
		this.width = WIDTH;
		
	}

	//DISPLAYABLE
	public Image getImage() {
		return image;
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

	//Allow other objects to get / set velocity and acceleration
	public double getAccelerationX() {
		return accelerationX;
	}

	public void setAccelerationX(double accelerationX) {
		this.accelerationX = accelerationX;
	}

	public double getAccelerationY() {
		return accelerationY;
	}

	public void setAccelerationY(double accelerationY) {
		this.accelerationY = accelerationY;
	}

	public double getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(double velocityX) {
		this.velocityX = velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(double velocityY) {
		this.velocityY = velocityY;
	}
	
	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		
		/*
		 * If this sprite is moving at a given velocity and collides with another sprite, it may be desirable to
		 * have this sprite (and/or the other sprite) 'bounce'. That is, the velocities should be adjusted so that
		 * the sprites' momentum is preserved (i.e act in accordance with Newtonian physics)
		 * 
		 * The problem is further complicated by the potential for two or more collisions to occur in one update. Consider
		 * throwing a tennis ball at the bottom edge of a wall. The ball will collide with the wall, but may also at nearly
		 * the same time collide with the floor. How does the velocity of the ball change?
		 * 
		 * The CollisionDetection class has a non-static method that attempts to solve this calculation. It is a rather complicated
		 * piece of code but has been refined over the years it works reasonably well. Note the use of several parameters and mutators
		 * that control the bounces
		 *   - bounceFactor (set in constructor): the momentum preserved is modified by this factor every time a collision occurs. A factor
		 *   of 1 means that no momentum is lost; a factor of 0.9 means that 90% of momentum is retained; factors greater than 1 mean
		 *   that the sprite gains momentum
		 * 	- collisionTargetTypes (created in constructor): the types of sprites that this sprite will bounce with. If null, all sprites
		 *  will be targets. Note that the method in the CollisionDetection is overloaded.
		 *  -virtual: a blank sprite that contains the new location and velocity of this sprite after all collisions have been calculated. These
		 *  values can then be used (or not) to adjust this sprite.	 
		 */
		
		
		collisionDetection.calculate2DBounce(virtual, this, universe.getSprites(), velocityX, velocityY, actual_delta_time);
		this.centerX = virtual.getCenterX();
		this.centerY = virtual.getCenterY();
		this.velocityX = virtual.getVelocityX();
		this.velocityY = virtual.getVelocityY();			

		this.velocityX = this.velocityX + accelerationX * 0.001 * actual_delta_time;
		this.velocityY = this.velocityY + accelerationY * 0.001 * actual_delta_time;
		
		checkCollision(universe);

	}

	private void checkCollision(Universe universe) {

		for (int i = 0; i < universe.getSprites().size(); i++) {

			DisplayableSprite sprite = universe.getSprites().get(i);

			if (sprite instanceof SimpleSprite) {

				if (CollisionDetection.pixelBasedOverlaps(this, sprite))
				{	
					if (sprite instanceof SimpleSprite) {
						((SimpleSprite)sprite).setDispose(true);
					}


					break;

				}

			}
	}

}

}

