import java.awt.Image;

public class Tile {

	Image image = null;
	int x = 0;
	int y = 0;
	int height = 0;
	int width = 0;
	boolean outOfBounds = false;
		
	public Tile(Image image, int x, int y, int width, int height, boolean outOfBounds) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.outOfBounds = outOfBounds;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public int getMinX() {
		return x;
	}
	public void setMinX(int x) {
		this.x = x;
	}
	public int getMinY() {
		return y;
	}
	public void setMinY(int y) {
		this.y = y;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public boolean isOutOfBounds() {
		return outOfBounds;
	}
		
}
