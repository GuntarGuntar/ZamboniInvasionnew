import java.awt.Image; 
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public interface Background {

	public Tile getTile(int col, int row);
	
	public int getCol(double x);
	
	public int getRow(double y);
	
	public double getShiftX();
	
	public double getShiftY();
	
	public void setShiftX(double shiftX);

	public void setShiftY(double shiftY);
	
}