import java.awt.Image;

public interface DisplayableSprite {

	public abstract Image getImage();
	
	public boolean getVisible();

	public double getMinX();

	public double getMaxX();

	public double getMinY();

	public double getMaxY();

	public double getHeight();

	public double getWidth();

	public double getCenterX();

	public double getCenterY();
	
	public boolean getDispose();
	
	public int getNumKilled();
	
	public void setDispose(boolean dispose);

	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time);	
	
}
