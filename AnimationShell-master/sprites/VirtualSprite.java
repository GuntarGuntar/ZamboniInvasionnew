import java.awt.Image;

//a class that serves only to store the new location and velocity of a virtual sprite; it can be used to temporarily store
//another sprite's location / velocity or to return a potential location / velocity (e.g. in collision detection)
//
//this class implements DisplayableSprite so that it can be used generically

public class VirtualSprite implements DisplayableSprite {

	protected boolean didBounce = false;
	protected double velocityX = 0;
	protected double velocityY = 0;
	protected double centerX = 0;
	protected double centerY = 0;
	protected double width = 0;
	protected double height = 0;

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
		return this.centerX;
	}

	public double getCenterY() {
		return this.centerY;
	}
	
	//additional information
	public boolean didBounce() {
		return didBounce;
	}

	public double getVelocityX() {
		return velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}
	
	//required for interface but not implemented
	public Image getImage() {
		return null;
	}

	public boolean getVisible() {
		return false;
	}

	public boolean getDispose() {
		return false;
	}

	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
	}

	@Override
	public void setDispose(boolean dispose) {
		//ignore		
	}
	
}
