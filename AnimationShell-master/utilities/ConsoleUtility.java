
public class ConsoleUtility {

	public static String describeSpriteXY(DisplayableSprite sprite) {
		return String.format("type: %20s; (%7.2f,%7.2f) - (%7.2f,%7.2f); dispose = %5b; visible = %5b", 
				sprite.getClass().toString(), 
				sprite.getMinX(), 
				sprite.getMinY(), 
				sprite.getMaxX(), 
				sprite.getMaxY(),
				sprite.getDispose(),
				sprite.getVisible());
	}

	public static String describeSpriteCenter(DisplayableSprite sprite) {
		return String.format("type: %20s; center: (%7.2f,%7.2f); height: %7.2f; width: %7.2f; dispose = %5b; visible = %5b", 
				sprite.getClass().toString(), 
				sprite.getCenterX(), 
				sprite.getCenterY(), 
				sprite.getWidth(), 
				sprite.getHeight(),
				sprite.getDispose(),
				sprite.getVisible());
	}

	public static String describeUniverse(Universe universe) {
		return String.format("type: %20s; center: (%7.2f,%7.2f); isComplete = %5b; centerOnPlayer = %5b", 
				universe.getClass().toString(), 
				universe.getXCenter(), 
				universe.getYCenter(), 
				universe.isComplete(), 
				universe.centerOnPlayer());
	}
	
	public static String describeBackground(Background background) {
		return String.format("type: %20s; shiftX: %7.2f; shiftY: %7.2f",
				background.getClass().toString(), 
				background.getShiftX(),
				background.getShiftY());
	}
}
