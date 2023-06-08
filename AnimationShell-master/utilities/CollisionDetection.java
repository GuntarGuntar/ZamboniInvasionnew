import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CollisionDetection {

	private double bounceFactorX = 1;
	private double bounceFactorY = 1;
	ArrayList<Class> collisionTargetTypes = null;
	
	public double getBounceFactorX() {
		return bounceFactorX;
	}

	public void setBounceFactorX(double bounceFactorX) {
		this.bounceFactorX = bounceFactorX;
	}

	public double getBounceFactorY() {
		return bounceFactorY;
	}

	public void setBounceFactorY(double bounceFactorY) {
		this.bounceFactorY = bounceFactorY;
	}
	
	public ArrayList<Class> getCollisionTargetTypes() {
		return collisionTargetTypes;
	}

	public void setCollisionTargetTypes(ArrayList<Class> collisionTargetTypes) {
		this.collisionTargetTypes = collisionTargetTypes;
	}


	public static boolean overlaps(DisplayableSprite spriteA, DisplayableSprite spriteB) {
		return overlaps(
				spriteA.getMinX(), 
				spriteA.getMinY(), 
				spriteA.getMaxX(), 
				spriteA.getMaxY(), 
				spriteB.getMinX(), 
				spriteB.getMinY(), 
				spriteB.getMaxX(), 
				spriteB.getMaxY());		
	}

	public static boolean overlaps(DisplayableSprite spriteA, DisplayableSprite spriteB, double deltaAX, double deltaAY) {
		
		return overlaps(
				spriteA.getMinX() + deltaAX, 
				spriteA.getMinY() + deltaAY, 
				spriteA.getMaxX() + deltaAX, 
				spriteA.getMaxY() + deltaAY, 
				spriteB.getMinX(), 
				spriteB.getMinY(), 
				spriteB.getMaxX(), 
				spriteB.getMaxY());		
	}

	public static boolean overlaps(double leftA, double topA, double rightA, double bottomA, double leftB, double topB, double rightB, double bottomB) {
		boolean toLeft = (rightA < leftB); //case 1: right edge of A is to the left of left edge of B, so A is fully to left of A
		boolean toRight = (leftA > rightB); //case 2: left edge of A is to the right of right edge of B, so A is fully to right of B
		boolean overlapX = !(toLeft || toRight);

		boolean above = (bottomA < topB); //case 1: bottom edge of A is above top edge of B, so A is fully above B
		boolean below = (topA > bottomB); //case 2: top edge of A is below bottom edge of B, so A is fully below B
		boolean overlapY = !(above || below);

		return (overlapX && overlapY);
	}
	
	public static boolean inside(DisplayableSprite spriteA, DisplayableSprite spriteB) {
		return inside(
				spriteA.getMinX(), 
				spriteA.getMinY(), 
				spriteA.getMaxX(), 
				spriteA.getMaxY(), 
				spriteB.getMinX(), 
				spriteB.getMinY(), 
				spriteB.getMaxX(), 
				spriteB.getMaxY());		
	}

	public static boolean inside(DisplayableSprite spriteA, DisplayableSprite spriteB, double deltaAX, double deltaAY) {
		
		return inside(
				spriteA.getMinX() + deltaAX, 
				spriteA.getMinY() + deltaAY, 
				spriteA.getMaxX() + deltaAX, 
				spriteA.getMaxY() + deltaAY, 
				spriteB.getMinX(), 
				spriteB.getMinY(), 
				spriteB.getMaxX(), 
				spriteB.getMaxY());		
	}
	
	public static boolean inside(double leftA, double topA, double rightA, double bottomA, double leftB, double topB, double rightB, double bottomB) {
		boolean insideX = ((leftB <= leftA) && (rightA <= rightB));
		boolean insideY = ((topB <= topA) && (bottomA <= bottomB));
		if (insideX && insideY) {
			return true;
		}
		else {
			return false;	    	
		}
	}	
	
	public static boolean covers (DisplayableSprite spriteA, DisplayableSprite spriteB) {
		//A cover B <-->  B inside A
		return inside(spriteB, spriteA);
	}

	public static boolean covers (DisplayableSprite spriteA, DisplayableSprite spriteB, double deltaAX, double deltaAY) {
		//A cover B <-->  B inside A
		//offset is equivalent
		return inside(spriteB, spriteA, deltaAX, deltaAY);
	}
	
	public static boolean covers (double leftA, double topA, double rightA, double bottomA, double leftB, double topB, double rightB, double bottomB) {
		//A cover B <-->  B inside A
		return inside(leftB, topB, rightB, bottomB, leftA, topA, rightA, bottomA);
	}
	
	public static double overlap(double minA, double maxA,double minB, double maxB) {
		double widthA = maxA - minA;
		double widthB = maxB - minB;
		
		//A is to left of B or A is to right of B
		if ((maxA <= minB) || (minA >= maxB)) {
			return 0;
		}
		//B is entirely inside A
		else if (minA < minB && maxA > maxB) {
			return widthB;
		}
		//A is entirely inside B
		else if (minB < minA && maxB > maxA) {
			return widthA;
		}
		//A is on B's right edge
		else if ( (minB < minA) && (minA < maxB)) {
			return maxB - minA;
		}
		else if ( (minA < minB) && (minB < maxB)) {
			return maxA - minB;
		}
		//A and B are identical
		else if ((minA == minB) && (maxA == maxB)) {
			return widthA;			
		}
		else {
			//there should not be a case here!
//			System.out.println("!overlapX - unexpected case");
			return 0;
		}
	}
	
	public static boolean pixelBasedOverlaps(DisplayableSprite spriteA, DisplayableSprite spriteB) {
		return pixelBasedOverlaps(spriteA, spriteB, 0, 0);
	}

	public static boolean pixelBasedOverlaps(DisplayableSprite spriteA, DisplayableSprite spriteB, double deltaAX, double deltaAY) {

		if (overlaps(spriteA, spriteB, deltaAX, deltaAY) == false) {
			return false;
		}
		
		BufferedImage bufferedA = (BufferedImage) spriteA.getImage();
		BufferedImage bufferedB = (BufferedImage) spriteB.getImage();
		
		int offsetX = (int) (spriteB.getMinX() - (spriteA.getMinX() + deltaAX));
		int offsetY = (int) (spriteB.getMinY() - (spriteA.getMinY() + deltaAY));
		
		int left = Math.max(0, (int) (offsetX));
		int top =  Math.max(0, (int) (offsetY));
		int right = (int) (spriteA.getWidth() - Math.max(0, spriteA.getMaxX() + deltaAX - spriteB.getMaxX()));
		int bottom = (int) (spriteA.getHeight() - Math.max(0, spriteA.getMaxY() + deltaAY - spriteB.getMaxY()));
		
		double scaleXA = bufferedA.getHeight() / (float)spriteA.getWidth();
		double scaleYA = bufferedA.getHeight() / (float)spriteA.getHeight();
		double scaleXB = bufferedB.getHeight() /  (float)spriteB.getWidth();
		double scaleYB = bufferedB.getHeight() /  (float)spriteB.getHeight();

		try {
			for (int x = left; x < right; x++) {
				for (int y = top; y < bottom; y++) {
					int xA = (int)(x * scaleXA);
					int yA = (int)(y * scaleYA);				
					int xB = (int) ((x - offsetX) * scaleXB);
					int yB = (int) ((y - offsetY) * scaleYB);
					if ((xB >= 0) && (yB >= 0) && (yB < bufferedB.getWidth()) && (yB < bufferedB.getHeight())) {
						int pixelA = bufferedA.getRGB(xA, yA);
						int pixelB = bufferedB.getRGB(xB, yB);
						if ((pixelA>>>24 > 0x00) && (pixelB>>>24 > 0x00)) {
							return true;
						}
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("pixelBasedOverlaps - ArrayIndexOutOfBoundsException");
		}	
		
		return false;
				
	}
	
	public VirtualSprite calculate2DBounce(VirtualSprite bounce, DisplayableSprite sprite, ArrayList<DisplayableSprite> barriers, double velocityX, double velocityY, long actual_delta_time) {

		if (bounce == null) {
			bounce = new VirtualSprite();
		}

		//create a copy of the sprite's location and velocity
		bounce.velocityX = velocityX;
		bounce.velocityY = velocityY;
		bounce.centerX = sprite.getCenterX();
		bounce.centerY = sprite.getCenterY();
		bounce.width = sprite.getWidth();
		bounce.height = sprite.getHeight();
		bounce.didBounce = false;		

		//calculate new position assuming there are no changes in direction
		double movementX = (velocityX * actual_delta_time * 0.001);
		double movementY = (velocityY * actual_delta_time * 0.001);

		//regular motion
		bounce.centerX += movementX;
		bounce.centerY += movementY;
		
		for (DisplayableSprite barrier : barriers) {

			//if the target of the current sprite is not contained in a list of 'target' types, then do not attempt to 
			//calculate a collision
			if ( (sprite == barrier) || (collisionTargetTypes != null) && ( collisionTargetTypes.contains(barrier.getClass()) == false)) {
				continue;				
			}
						
			//collision for sprite's current position
			boolean doesCollide = overlaps(sprite, barrier, 0, 0);
			//collision for sprite's next position
			boolean willCollide = overlaps(sprite, barrier, movementX, movementY);
			
			//is already colliding!
			if (doesCollide) {
				//determine distance of overlap in both dimensions with current position of A
				double overlapX = overlap(sprite.getMinX(), sprite.getMaxX(), barrier.getMinX(), barrier.getMaxX());
				double overlapY = overlap(sprite.getMinY(), sprite.getMaxY(), barrier.getMinY(), barrier.getMaxY());
				//likely that one of the overlaps is very small (A moved slightly into B); the other overlap may be zero;
				//choose the shorter overlap and bounce only in that direction to extract from the collision
				if (overlapY > overlapX) {
					//bounce in X dimension, but only if A is moving closer to B... otherwise, collision is already resolving
					if ( Math.signum(velocityX) ==  Math.signum(barrier.getCenterX() - sprite.getCenterX())) {
						//invert the proposed motion
						bounce.centerX = sprite.getCenterX() - movementX;
						bounce.velocityX = (velocityX * bounceFactorX * -1);
					}					
				} else {
					//bounce in Y dimension
					if ( Math.signum(velocityY) ==  Math.signum(barrier.getCenterY() - sprite.getCenterY())) {
						//invert the proposed motion
						bounce.centerY = sprite.getCenterY() - movementY;
						bounce.velocityY = (velocityY * bounceFactorY * -1);
					}										
				}				
			}
			//will collide with calculated movement
			else if (willCollide) {
				//treat x dimension and y dimension separately
				//x dimension
				boolean willCollideX = overlaps(sprite, barrier, movementX, 0);
				if (willCollideX) {
					if (velocityX > 0) {
						//moving to right
						double distanceToBoundary = (barrier.getMinX() - sprite.getMaxX());
						double leadingEdge = barrier.getMinX() - ( (movementX - distanceToBoundary) * bounceFactorX);
						bounce.centerX = leadingEdge - (sprite.getWidth() / 2);
						bounce.velocityX = (velocityX * bounceFactorX * -1);
					}
					else {
						//moving to left
						double distanceToBoundary = (sprite.getMinX() - barrier.getMaxX() );
						double leadingEdge = barrier.getMaxX() + ( ((movementX * -1) - distanceToBoundary) * bounceFactorX);
						bounce.centerX = leadingEdge + (sprite.getWidth() / 2);
						bounce.velocityX = (velocityX * bounceFactorX * -1);
					}
				}
				//y dimension
				boolean willCollideY = overlaps(sprite, barrier, 0, movementY);
				if (willCollideY) {
					if (velocityY > 0) {
						//moving down
						double distanceToBoundary = (barrier.getMinY() - sprite.getMaxY());
						double leadingEdge = barrier.getMinY() - ( (movementY - distanceToBoundary) * bounceFactorY);
						bounce.centerY = leadingEdge - (sprite.getWidth() / 2);
						bounce.velocityY = (velocityY * bounceFactorY * -1);
					}
					else {
						//moving up
						double distanceToBoundary = (sprite.getMinY() - barrier.getMaxY() );
						double leadingEdge = barrier.getMaxY() + ( ((movementY * -1) - distanceToBoundary) * bounceFactorY);
						bounce.centerY = leadingEdge + (sprite.getWidth() / 2);
						bounce.velocityY = (velocityY * bounceFactorY * -1);
					}
					
				}
			}
			//will not collide
			else {
			}
		}
	
		return bounce;
		
	}
}
