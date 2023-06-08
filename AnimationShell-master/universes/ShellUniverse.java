import java.util.ArrayList;
import java.util.Random;

public class ShellUniverse implements Universe {

	private boolean complete = false;
	private ArrayList<Background> backgrounds = null;
	private Background background = null;	
	private DisplayableSprite player1 = null;
	private ZamboniSprite player2 = null;
	private ArrayList<DisplayableSprite> sprites = new ArrayList<DisplayableSprite>();
	private ArrayList<DisplayableSprite> disposalList = new ArrayList<DisplayableSprite>();
	public static int numKilled = 0;
	private boolean successful = false;
	int limit = 5;  //TODO -TEMP

	public ShellUniverse () {

		background = new IceBackground();
		ArrayList<DisplayableSprite> barriers = ((IceBackground)background).getBarriers();
		backgrounds =new ArrayList<Background>();
		backgrounds.add(background);
		
		player1 = new SimpleSprite(IceBackground.TILE_HEIGHT * 2, IceBackground.TILE_WIDTH * 2, IceBackground.TILE_HEIGHT * 0.9, IceBackground.TILE_HEIGHT * 0.9);
		sprites.add(player1);
		
		sprites.addAll(barriers);
		
	
		for (int i = 0; i < limit; i++) {
			Random xPos = new Random();
			int randomX = xPos.nextInt(901) + 100;
			Random yPos = new Random();
			int randomY = yPos.nextInt(901) + 100;
			player2 = new ZamboniSprite(randomX, randomY, 100, 100);
			sprites.add(player2);
		}
//		//bottom
//		sprites.add(new BarrierSprite(AnimationFrame.SCREEN_WIDTH / -2,AnimationFrame.SCREEN_HEIGHT / 2 - 16, AnimationFrame.SCREEN_WIDTH / 2, AnimationFrame.SCREEN_HEIGHT / 2, true));
//		//left
//		sprites.add(new BarrierSprite(AnimationFrame.SCREEN_WIDTH / -2,AnimationFrame.SCREEN_HEIGHT / -2, AnimationFrame.SCREEN_WIDTH / -2 + 16, AnimationFrame.SCREEN_HEIGHT / 2, true));
//		//right
//		sprites.add(new BarrierSprite(AnimationFrame.SCREEN_WIDTH / 2 - 16,AnimationFrame.SCREEN_HEIGHT / -2, AnimationFrame.SCREEN_WIDTH / 2, AnimationFrame.SCREEN_HEIGHT / 2, true));


	}

	public double getScale() {
		return 1;
	}	
	
	public double getXCenter() {
		return this.player1.getCenterX();
	}

	public double getYCenter() {
		return this.player1.getCenterY();
	}
	
	public void setXCenter(double xCenter) {
	}

	public void setYCenter(double yCenter) {
	}
	
	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		complete = true;
	}
	
	public boolean isSuccessful() {
		return successful;
	}

	public ArrayList<Background> getBackgrounds() {
		return backgrounds;
	}	

	public DisplayableSprite getPlayer1() {
		return player1;
	}
	
	public ZamboniSprite getPlayer2() {
		return player2;
	}

	public ArrayList<DisplayableSprite> getSprites() {
		return sprites;
	}
		
	public boolean centerOnPlayer() {
		return true;
	}		
	
	public void update(KeyboardInput keyboard, long actual_delta_time) {

		if (keyboard.keyDownOnce(27)) {
			complete = true;
		}
		
		for (int i = 0; i < sprites.size(); i++) {
			DisplayableSprite sprite = sprites.get(i);
			sprite.update(this, keyboard, actual_delta_time);
    	} 
		
		if (numKilled == limit) {
			limit += 3;
			for (int j = 0; j < limit; j++) {
				Random xPos = new Random();
				int randomX = xPos.nextInt(901) + 100;
				Random yPos = new Random();
				int randomY = yPos.nextInt(901) + 100;
				player2 = new ZamboniSprite(randomX, randomY, 100, 100);
				sprites.add(player2);
				System.out.println("working sorta");
				numKilled = 0;
			}
		}
	
		disposeSprites();
	}

	public String toString() {
		return "MappedUniverse";
	}	
	 protected void disposeSprites() {
	        
	    	//collect a list of sprites to dispose
	    	//this is done in a temporary list to avoid a concurrent modification exception
			for (int i = 0; i < sprites.size(); i++) {
				DisplayableSprite sprite = sprites.get(i);
	    		if (sprite.getDispose() == true) {
	    			disposalList.add(sprite);
	    			//numKilled ++;
	    			//System.out.println("killed");
	    		}
	    	}
			
			//go through the list of sprites to dispose
			//note that the sprites are being removed from the original list
			for (int i = 0; i < disposalList.size(); i++) {
				DisplayableSprite sprite = disposalList.get(i);
				sprites.remove(sprite);
				System.out.println("Remove: " + sprite.toString());
	    	}
			
			//clear disposal list if necessary
	    	if (disposalList.size() > 0) {
	    		disposalList.clear();
	    	}
	    }

	public Object getTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
