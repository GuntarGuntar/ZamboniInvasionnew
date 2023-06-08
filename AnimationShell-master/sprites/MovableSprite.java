
public interface MovableSprite {

	public void setCenterX(double centerX);
	
	public void setCenterY(double centerY);
	
	public void moveX(double pixelsPerSecond);
	
	public void moveY(double pixelsPerSecond);
		
	public void stop();
	
}
