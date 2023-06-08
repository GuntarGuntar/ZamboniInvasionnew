import javax.swing.*;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseMotionAdapter;


public class AnimationFrame extends JFrame {

	final public static int FRAMES_PER_SECOND = 60;	
	final long REFRESH_TIME = 1000 / FRAMES_PER_SECOND;	//MILLISECONDS	
	final boolean DISPLAY_TIMING = true;
	
	final public static int SCREEN_HEIGHT = 750;
	final public static int SCREEN_WIDTH = 900;

	private TitleFrame titleFrame = null;
	
	private int screenCenterX = SCREEN_WIDTH / 2;
	private int screenCenterY = SCREEN_HEIGHT / 2;
	final private static boolean SHOW_GRID = true;
	
	private double scale = 1;
	//point in universe on which the screen will center
	private double logicalCenterX = 0;		
	private double logicalCenterY = 0;

	protected JPanel panel = null;	
	protected JButton btnPauseRun;	
	private JLabel lblStatus;
	private JLabel lblLevel;	
	private JLabel lblAmmoLabel;	
	private JLabel lblHealthLabel;	
	private JLabel lblHealth;	
	private JLabel lblFuelLabel;	
	private JLabel lblFuel;	
	private JLabel lblAmmo;

	private static boolean stop = false;

	protected long total_elapsed_time = 0;
	protected long lastRefreshTime = 0;
	protected long deltaTime = 0;
	private boolean isPaused = false;

	protected KeyboardInput keyboard = new KeyboardInput();
	protected Universe universe = null;

	//local (and direct references to various objects in universe ... should reduce lag by avoiding dynamic lookup
	private Animation animation = null;
	private SimpleSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = null;
	private ArrayList<Background> backgrounds = null;
	private Background background = null;
	boolean centreOnPlayer = false;
	int universeLevel = 0;
	
	public AnimationFrame(Animation animation)
	{
		super("");
		getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				thisContentPane_mousePressed(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				thisContentPane_mouseReleased(e);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				contentPane_mouseExited(e);
			}
		});
		
		this.animation = animation;
//		this.setVisible(true);		
		this.setFocusable(true);
		this.setSize(SCREEN_WIDTH + 20, SCREEN_HEIGHT + 36);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				keyboard.keyPressed(arg0);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				keyboard.keyReleased(arg0);
			}
		});
		getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				contentPane_mouseMoved(e);
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				contentPane_mouseMoved(e);
			}
		});

		Container cp = getContentPane();
		cp.setBackground(Color.BLACK);
		cp.setLayout(null);

		panel = new DrawPanel();
		panel.setLayout(null);
		panel.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		getContentPane().add(panel, BorderLayout.CENTER);

		btnPauseRun = new JButton("||");
		btnPauseRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnPauseRun_mouseClicked(arg0);
			}
		});

		btnPauseRun.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPauseRun.setBounds(SCREEN_WIDTH - 64, 20, 48, 32);
		btnPauseRun.setFocusable(false);
		getContentPane().add(btnPauseRun);
		getContentPane().setComponentZOrder(btnPauseRun, 0);

		lblStatus = new JLabel("");
		lblStatus.setForeground(Color.WHITE);
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblStatus.setBounds(20, 22, 500, 30);
		getContentPane().add(lblStatus);
		getContentPane().setComponentZOrder(lblStatus, 0);

		lblLevel = new JLabel("");
		lblLevel.setForeground(Color.YELLOW);
		lblLevel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblLevel.setBounds(690, 20, 192, 30);
		getContentPane().add(lblLevel);
		getContentPane().setComponentZOrder(lblLevel, 0);
		
		lblHealthLabel = new JLabel("Health");
		lblHealthLabel.setForeground(Color.WHITE);
		lblHealthLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblHealthLabel.setBounds(20, SCREEN_HEIGHT - 57, 152, 30);
		getContentPane().add(lblHealthLabel);
		getContentPane().setComponentZOrder(lblHealthLabel, 0);
		
		lblHealth = new JLabel("5");
		lblHealth.setBounds(190, SCREEN_HEIGHT - 49, 100,14);
		lblHealth.setHorizontalAlignment(SwingConstants.CENTER);
		lblHealth.setOpaque(true);
		lblHealth.setForeground(Color.BLACK);
		lblHealth.setBackground(Color.ORANGE);
		setBarLabelBounds(this.lblHealth, 100);
		getContentPane().add(lblHealth);
		getContentPane().setComponentZOrder(lblHealth, 0);

//		lblFuelLabel = new JLabel("Fuel:");
//		lblFuelLabel.setForeground(Color.WHITE);
//		lblFuelLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
//		lblFuelLabel.setBounds(20, SCREEN_HEIGHT - 139, 152, 30);
//		getContentPane().add(lblFuelLabel);
//		getContentPane().setComponentZOrder(lblFuelLabel, 0);
//		
//		lblFuel = new JLabel("80");
//		lblFuel.setBounds(190, SCREEN_HEIGHT - 131, 100,14);
//		lblFuel.setHorizontalAlignment(SwingConstants.CENTER);
//		lblFuel.setBackground(Color.GREEN);
//		lblFuel.setForeground(Color.BLACK);
//		setBarLabelBounds(this.lblFuel, 100);
//		lblFuel.setOpaque(true);
//		getContentPane().add(lblFuel);
//		getContentPane().setComponentZOrder(lblFuel, 0);

	}

	private void setBarLabelBounds(JLabel label, double percent) {

		int minX = 189;
		int minY = label.getY();
		int maxX = this.getWidth() - 32;
		
		label.setBounds(minX, minY,(int)(minX + (maxX - minX) * percent / 100) - minX, 14);
	}
	
	public void start()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				animationLoop();
				System.out.println("run() complete");
			}
		};

		thread.start();
		//start the animation loop so that it can initialize at the same time as a title screen being visible
		//as it runs on a separate thread, it will execute asynchronously
		displayTitleScreen();
				
		System.out.println("main() complete");

	}
	
	protected void displayTitleScreen() {
		//hide interface
		this.setVisible(false);
		titleFrame = new TitleFrame();
		//center on the parent
		titleFrame.setLocationRelativeTo(this);
		//display title screen
		//set the modality to APPLICATION_MODAL
		titleFrame.setModalityType(ModalityType.APPLICATION_MODAL);
		//by setting the dialog to visible, the application will start running the dialog
		titleFrame.setVisible(true);
		
		//when title screen has been closed, execution will resume here.
		titleFrame.dispose();
		this.setVisible(true);
		
	}
	private void animationLoop() {

		lastRefreshTime = System.currentTimeMillis();
		
		universe = animation.getNextUniverse();
		universeLevel++;
		
		while (stop == false && universe != null) {

			//initialize animation
			sprites = universe.getSprites();
			player1 = (SimpleSprite) universe.getPlayer1();
			backgrounds = universe.getBackgrounds();
			centreOnPlayer = universe.centerOnPlayer();
			this.scale = universe.getScale();
			
			//pause while title screen is displayed
			while (titleFrame != null && titleFrame.isVisible() == true) {
				Thread.yield();
				try {
					Thread.sleep(1);
				}
				catch(Exception e) {    					
				} 				
			}

			// main game loop
			while (stop == false && universe.isComplete() == false) {

				if (DISPLAY_TIMING == true) System.out.println(String.format("animation loop: %10s @ %6d", "sleep", System.currentTimeMillis() % 1000000));

				//adapted from http://www.java-gaming.org/index.php?topic=24220.0
				long target_wake_time = System.currentTimeMillis() + REFRESH_TIME;
				//sleep until the next refresh time
				while (System.currentTimeMillis() < target_wake_time)
				{
					//allow other threads (i.e. the Swing thread) to do its work
					Thread.yield();

					try {
						Thread.sleep(1);
					}
					catch(Exception e) {    					
					} 

				}

				if (DISPLAY_TIMING == true) System.out.println(String.format("animation loop: %10s @ %6d  (+%4d ms)", "wake", System.currentTimeMillis() % 1000000, System.currentTimeMillis() - lastRefreshTime));

				//track time that has elapsed since the last update, and note the refresh time
				deltaTime = (isPaused ? 0 : System.currentTimeMillis() - lastRefreshTime);
				lastRefreshTime = System.currentTimeMillis();
				total_elapsed_time += deltaTime;
				
				//read input
				keyboard.poll();
				handleKeyboardInput();

				//update logical
				universe.update(keyboard, deltaTime);
				if (DISPLAY_TIMING == true) System.out.println(String.format("animation loop: %10s @ %6d  (+%4d ms)", "logic", System.currentTimeMillis() % 1000000, System.currentTimeMillis() - lastRefreshTime));
				
				//update interface
				updateControls();
				//align animation frame with logical universe
				if (player1 != null && centreOnPlayer) {
					this.logicalCenterX = player1.getCenterX();
					this.logicalCenterY = player1.getCenterY();     
				}
				else {
					this.logicalCenterX = universe.getXCenter();
					this.logicalCenterY = universe.getYCenter();
				}

				this.repaint();

			}

			//handleUniverseComplete();
			keyboard.poll();

		}

		System.out.println("animation complete");
		AudioPlayer.setStopAll(true);
		dispose();	

	}
	
//	private void handleUniverseComplete() {
//	
//		if (universe.isComplete()) {
//			
//			if (((ShellUniverse)universe).isSuccessful()) {
//				JOptionPane.showMessageDialog(this,
//						"Proceed to next level!");
//				
//				universe = animation.getNextUniverse();
//			}
//			else {
//				int choice = JOptionPane.showOptionDialog(this,
//						"Spaceship go Boom. Play again?",
//								"Game Over",
//								JOptionPane.YES_NO_OPTION,
//								JOptionPane.QUESTION_MESSAGE,
//								null,
//								null,
//								null);
//				
//				if (choice == 0) {
//					((AstronautPickupAnimation) animation).restart();
//					universe = animation.getNextUniverse();
//					keyboard.poll();					
//				}
//				else {
//					universe = null;
//				}
//			}				
//		}			
//	}


	protected void updateControls() {
		
		this.lblStatus.setText(String.format("Killed: %2d", BulletSprite.getkillScore(), ((ShellUniverse) universe).getTarget()));
		//this.lblTop.setText(String.format("Time: %9.3f;  centerX: %5d; centerY: %5d;  scale: %3.3f", elapsed_time / 1000.0, screenCenterX, screenCenterY, scale));
		//this.lblBottom.setText(Integer.toString(universeLevel));
		//if (universe != null) {
			//this.lblBottom.setText(universe.toString());
		}

	protected void btnPauseRun_mouseClicked(MouseEvent arg0) {
		if (isPaused) {
			isPaused = false;
			this.btnPauseRun.setText("||");
		}
		else {
			isPaused = true;
			this.btnPauseRun.setText(">");
		}
	}

	private void handleKeyboardInput() {
		
		if (keyboard.keyDown(80) && ! isPaused) {
			btnPauseRun_mouseClicked(null);	
		}
		if (keyboard.keyDown(79) && isPaused ) {
			btnPauseRun_mouseClicked(null);
		}
		if (keyboard.keyDown(112)) {
			scale *= 1.01;
			contentPane_mouseMoved(null);
		}
		if (keyboard.keyDown(113)) {
			scale /= 1.01;
			contentPane_mouseMoved(null);
		}
		
		if (keyboard.keyDown(65)) {
			screenCenterX += 1;
		}
		if (keyboard.keyDown(68)) {
			screenCenterX -= 1;
		}
		if (keyboard.keyDown(83)) {
			screenCenterY += 1;
		}
		if (keyboard.keyDown(88)) {
			screenCenterY -= 1;
		}		
	}

	class DrawPanel extends JPanel {

		public void paintComponent(Graphics g)
		{	
			if (universe == null) {
				return;
			}

			if (backgrounds != null) {
				for (Background background: backgrounds) {
					paintBackground(g, background);
				}
			}

			if (sprites != null) {
				for (DisplayableSprite activeSprite : sprites) {
					DisplayableSprite sprite = activeSprite;
					if (sprite.getVisible()) {
						if (sprite.getImage() != null) {
							g.drawImage(sprite.getImage(), translateToScreenX(sprite.getMinX()), translateToScreenY(sprite.getMinY()), scaleLogicalX(sprite.getWidth()), scaleLogicalY(sprite.getHeight()), null);
						}
						else {
							g.setColor(Color.BLUE);
							g.fillRect(translateToScreenX(sprite.getMinX()), translateToScreenY(sprite.getMinY()), scaleLogicalX(sprite.getWidth()), scaleLogicalY(sprite.getHeight()));
						}
					}
				}				
			}

			if (DISPLAY_TIMING == true) System.out.println(String.format("animation loop: %10s @ %6d  (+%4d ms)", "interface", System.currentTimeMillis() % 1000000, System.currentTimeMillis() - lastRefreshTime));

		}
		
		private void paintBackground(Graphics g, Background background) {
			
			if ((g == null) || (background == null)) {
				return;
			}
			
			//what tile covers the top-left corner?
			double logicalLeft = (logicalCenterX  - (screenCenterX / scale) - background.getShiftX());
			double logicalTop =  (logicalCenterY - (screenCenterY / scale) - background.getShiftY()) ;
						
			int row = background.getRow((int)(logicalTop - background.getShiftY() ));
			int col = background.getCol((int)(logicalLeft - background.getShiftX()  ));
			Tile tile = background.getTile(col, row);
			
			boolean rowDrawn = false;
			boolean screenDrawn = false;
			while (screenDrawn == false) {
				while (rowDrawn == false) {
					tile = background.getTile(col, row);
					if (tile.getWidth() <= 0 || tile.getHeight() <= 0) {
						//no increase in width; will cause an infinite loop, so consider this screen to be done
						g.setColor(Color.GRAY);
						g.fillRect(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);					
						rowDrawn = true;
						screenDrawn = true;						
					}
					else {
						Tile nextTile = background.getTile(col+1, row+1);
						int width = translateToScreenX(nextTile.getMinX()) - translateToScreenX(tile.getMinX());
						int height = translateToScreenY(nextTile.getMinY()) - translateToScreenY(tile.getMinY());
						g.drawImage(tile.getImage(), translateToScreenX(tile.getMinX() + background.getShiftX()), translateToScreenY(tile.getMinY() + background.getShiftY()), width, height, null);
					}					
					//does the RHE of this tile extend past the RHE of the visible area?
					if (translateToScreenX(tile.getMinX() + background.getShiftX() + tile.getWidth()) > SCREEN_WIDTH || tile.isOutOfBounds()) {
						rowDrawn = true;
					}
					else {
						col++;
					}
				}
				//does the bottom edge of this tile extend past the bottom edge of the visible area?
				if (translateToScreenY(tile.getMinY() + background.getShiftY() + tile.getHeight()) > SCREEN_HEIGHT || tile.isOutOfBounds()) {
					screenDrawn = true;
				}
				else {
					col = background.getCol(logicalLeft);
					row++;
					rowDrawn = false;
				}
			}
		}				
	}

	private int translateToScreenX(double logicalX) {
		return screenCenterX + scaleLogicalX(logicalX - logicalCenterX);
	}		
	private int scaleLogicalX(double logicalX) {
		return (int) Math.round(scale * logicalX);
	}
	private int translateToScreenY(double logicalY) {
		return screenCenterY + scaleLogicalY(logicalY - logicalCenterY);
	}		
	private int scaleLogicalY(double logicalY) {
		return (int) Math.round(scale * logicalY);
	}

	private double translateToLogicalX(int screenX) {
		int offset = screenX - screenCenterX;
		return offset / scale;
	}
	private double translateToLogicalY(int screenY) {
		int offset = screenY - screenCenterY;
		return offset / scale;			
	}
	
	protected void contentPane_mouseMoved(MouseEvent e) {
		Point point = this.getContentPane().getMousePosition();
		if (point != null) {
			MouseInput.screenX = point.x;		
			MouseInput.screenY = point.y;
			MouseInput.logicalX = translateToLogicalX(MouseInput.screenX);
			MouseInput.logicalY = translateToLogicalY(MouseInput.screenY);
		}
		else {
			MouseInput.screenX = -1;		
			MouseInput.screenY = -1;
			MouseInput.logicalX = Double.NaN;
			MouseInput.logicalY = Double.NaN;
		}
	}
	
	protected void thisContentPane_mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			MouseInput.leftButtonDown = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			MouseInput.rightButtonDown = true;
		} else {
			//DO NOTHING
		}
	}
	protected void thisContentPane_mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			MouseInput.leftButtonDown = false;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			MouseInput.rightButtonDown = false;
		} else {
			//DO NOTHING
		}
	}

	protected void this_windowClosing(WindowEvent e) {
		System.out.println("windowClosing()");
		stop = true;
		dispose();	
	}
	protected void contentPane_mouseExited(MouseEvent e) {
		contentPane_mouseMoved(e);
	}
}
