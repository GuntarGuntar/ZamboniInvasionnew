import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/*
 * This class is an example of how to implement two basic types of collision detection. Refer to the update() method
 * for detailed explanation.
 */

public class CollidingSprite implements DisplayableSprite {

	private static Image image;	
	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	

	private final double VELOCITY = 200;	
	
	public CollidingSprite(double centerX, double centerY) {

		this.centerX = centerX;
		this.centerY = centerY;
		
		if (image == null) {
			try {
				image = ImageIO.read(new File("res/gudtank.png"));
			}
			catch (IOException e) {
				System.out.println(e.toString());
			}		
		}		
	}
	
	//overloaded constructor which allows universe to change aspects of the sprite
	public CollidingSprite(double centerX, double centerY, double height, double width) {
		this(centerX, centerY);
		
		this.height = height;
		this.width = width;
	}
	

	public Image getImage() {
		return image;
	}
	
	//DISPLAYABLE
	
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


	/*
	 * Basic collision detection is usually done in the update method, as it determines whether this sprite
	 * is or will be colliding (i.e. this sprite's dimensions are overlapping with another sprite in the universe).
	 * If so, the movement of this sprite is adjusted. 
	 * 
	 * For this specific sprite, the intent is to stop prior to colliding with another sprite of certain types. Because
	 * this sprite is visually a sphere, it is simple enough to determine collision with other sprites that are
	 * rectangular (i.e. BarrierSprites, which have only horizontal and vertical lines). 
	 * The CollisionDetection utility class provides methods that checks for geometric 'overlap' of two rectangles.
	 * The sprite 'looks ahead' to where it would be located using its current velocity. If this new position does not
	 * overlap, then move; if it does, then set velocity to zero, thereby not moving into the collision. 
	 * 
	 * Note that the x and y movement are calculated separately; thus, this sprite can
	 * still move in one dimension even if the other dimension velocity has to be zero. This allows this sprite to
	 * 'slide' along a barrier if the user tries to move the sprite diagonally, with both x and y velocity.
	 * 
	 * The second type of collision detection is more precise but also requires more processing. The Pinball sprite is
	 * itself also spherical. It is possible for two spheres to collide at any point on their circumference, not just
	 * along their left/right/up/down edges. That is, even though two images (as rectangles) may overlap, there is not
	 * an actual collision as part of the image may be 'invisible'. The CollisionDetection class also provides methods
	 * for checking on a pixel-by-pixel basis whether two images are truly overlapping. Even for small images
	 * (e.g. 50 x 50 pixels), this can require up to 2500 checks for each pixel. There is substantial optimization
	 * built into the pixelBasedOverlaps methods, and it can thus be used judiciously without noticably slowing down the
	 * animation. Note that since we no longer know if this is just a collision on the left/right/up/down edges, this
	 * sprite sets the velocity in both dimensions to zero and does not move if a pixel based overlap is detected in the
	 * calculated new location.
	 */
	
	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		
		/*
		 * This sprite controls its own velocity by reading keyboard input		
		 */
		double velocityX = 0;
		double velocityY = 0;
		
		//LEFT ARROW
		if (keyboard.keyDown(37)) {
			velocityX = -VELOCITY;
		}
		//UP ARROW
		if (keyboard.keyDown(38)) {
			velocityY = -VELOCITY;			
		}
		//RIGHT ARROW
		if (keyboard.keyDown(39)) {
			velocityX += VELOCITY;
		}
		// DOWN ARROW
		if (keyboard.keyDown(40)) {
			velocityY += VELOCITY;			
		}		

		//calculate potential change in position based on velocity and time elapsed		
		double deltaX = actual_delta_time * 0.001 * velocityX;
		double deltaY = actual_delta_time * 0.001 * velocityY;
		
		//before changing position, check if the new position would result in a collision with another sprite
		//move only if no collision results. 
		boolean collidingWithPinball = checkCollisionWithPinball(universe.getSprites(), deltaX, deltaY);
		boolean collidingBarrierX = checkCollisionWithBarrier(universe.getSprites(), deltaX, 0);
		boolean collidingBarrierY = checkCollisionWithBarrier(universe.getSprites(), 0, deltaY);
		
		System.out.println(collidingBarrierX);
		

		//only move if there is no collision with pinball in any dimension and no collision with barrier in X dimension 
		if ((collidingWithPinball == false) && (collidingBarrierX == false)) {
			this.centerX += deltaX;
		}
		//only move if there is no collision with pinball in any dimension and no collision with barrier in Y dimension 
		if ((collidingWithPinball == false) && (collidingBarrierY == false)) {
			this.centerY += deltaY;
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
		}		
		return colliding;		
	}
	
	private boolean checkCollisionWithPinball(ArrayList<DisplayableSprite> sprites, double deltaX, double deltaY) {

		//deltaX and deltaY represent the potential change in position
		boolean colliding = false;

		for (DisplayableSprite sprite : sprites) {
			if (sprite instanceof SimpleSprite) {
				
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite, deltaX, deltaY)) {
					colliding = true;
					break;					
				}
			}
		}		
		return colliding;		
	}
	
}
