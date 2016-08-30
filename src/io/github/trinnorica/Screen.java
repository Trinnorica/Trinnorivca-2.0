package io.github.trinnorica;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.Timer;

import io.github.trinnorica.entity.Player;
import io.github.trinnorica.objects.tools.Sword;
import io.github.trinnorica.utils.Backgrounds;
import io.github.trinnorica.utils.Board;
import io.github.trinnorica.utils.Clickable;
import io.github.trinnorica.utils.Direction;
import io.github.trinnorica.utils.Images;
import io.github.trinnorica.utils.Keyable;
import io.github.trinnorica.utils.Moveable;
import io.github.trinnorica.utils.Sprite;
import io.github.trinnorica.utils.Utils;
import res.ExternalFile;

public class Screen extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Timer timer;
//	java.util.Timer t;
	int DELAY = 15;
	boolean debug = false;
	int board = 0;
	int totalFrameCount = 0;
	double menuvar = 0;
	int creditvar = 0;
	public List<Sprite> objects = new ArrayList<>();
	double r = 0.0;


	public Screen() {
		init();
	}

	public void init() {
		
		timer = new Timer(DELAY, this);
		timer.start();

		addKeyListener(new TAdapter());
		addMouseMotionListener(new MMListener());
		addMouseListener(new MListener());
		
		setLayout(null);

		setFocusable(true);

		setPreferredSize(new Dimension(1920, 1080));
		
		Main.setScreen(this);
		java.util.Timer t = new java.util.Timer();
		t.schedule(new TimerTask(){ public void run(){ Main.setBoard(Board.MAIN);}}, 500);
		
		
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawMenu(g);
		Toolkit.getDefaultToolkit().sync();
	}

	public void drawMenu(Graphics g) {
		
		g.setFont(new Font("Helvetica", Font.PLAIN, getWidth()/50));
		Utils.drawOutlineString(g, "Loading...", getWidth()/2 - g.getFontMetrics().stringWidth("Loading...")/2, getHeight()/2, Color.RED, Color.BLACK, 1);
		if(board == Board.MAIN){
			menuvar = Utils.drawScrollingImage(g, Backgrounds.MAIN.getImage(), menuvar, 0, this.getWidth(), this.getHeight(), 1);
			
			for(Sprite sprite : objects){
				if(sprite instanceof Moveable){
					((Moveable) sprite).move();
				}
				sprite.draw(g);
				
//				g.drawImage(sprite.getImage(), sprite.x, sprite.y, sprite.getWidth(), sprite.getHeight(), this);
			}
			
			g.drawImage(ExternalFile.loadTexture("entity/player/bobbing.gif"), getWidth()/4, getHeight()/2, 60, 60, this);
			g.drawImage(ExternalFile.loadTexture("entity/knight/bobbing.gif"), getWidth()/4 + getWidth()/2, getHeight()/2, 60, 60, this);
			Image logo  = ExternalFile.loadTexture("logos/logo-title.png");
			g.drawImage(Images.makeImageTranslucent(Images.toBufferedImage(logo), 0.9), this.getWidth()/2 - logo.getWidth(this)/2, this.getHeight()/2 - logo.getHeight(this)/2, this);

		}
		
		if(board == Board.CREDITS){
			g.drawImage(Backgrounds.CREDITS.getImage(), 0, 0, getWidth(), getHeight(), this);
			Image dark = Images.makeImageTranslucent(Images.toBufferedImage(Images.createColorImage("#000000")), 0.5);
			g.drawImage(dark, 0, 0, getWidth(), getHeight(), this);
			dark = null;
			Utils.drawCredit(g, "Author & Developers", creditvar, 1, Color.BLACK, Color.WHITE, 1);
			Utils.drawCredit(g, "Cameron Witcher (Author)", creditvar, 2, Color.BLACK, Color.WHITE, 1);
			Utils.drawCredit(g, "Artists and Concept designers", creditvar, 4, Color.BLACK, Color.WHITE, 1);
			Utils.drawCredit(g, "Herb Yeliab (Head Artist)", creditvar, 5, Color.BLACK, Color.WHITE, 1);
			
			Image logo = ExternalFile.loadTexture("logos/logo-title.png");
			
			Utils.drawCreditImage(g, logo, creditvar, 7);
			
			
			creditvar-=1;
			
			
		}
		
		
		for(Clickable c : Main.getClickables()){
			c.drawPolygon(g);
		}
		
		//Debug overlay
		if(debug){
			g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
			Utils.drawOutlineString(g, "Version: " + Utils.getVersion(), 0, 20, Color.WHITE, Color.BLACK, 1);
			Utils.drawOutlineString(g, "Clickables: " + Main.getClickables().size(), 0, 40, Color.WHITE, Color.BLACK, 1);
			Utils.drawOutlineString(g, "Objects: " + objects.size(), 0, 60, Color.WHITE, Color.BLACK, 1);
			Utils.drawOutlineString(g, "Rotation: " + r, 0, 80, Color.WHITE, Color.BLACK, 1);
			try{
				Utils.drawOutlineString(g, "Flying: " + ((Player) objects.get(1)).flying, 0, 100, Color.WHITE, Color.BLACK, 1);
				Utils.drawOutlineString(g, "Jumping: " + ((Player) objects.get(1)).jumping, 0, 120, Color.WHITE, Color.BLACK, 1);
				Utils.drawOutlineString(g, "Onground: " + ((Player) objects.get(1)).onground, 0, 140, Color.WHITE, Color.BLACK, 1);
				Utils.drawOutlineString(g, "Playing: True", 0, 160, Color.WHITE, Color.BLACK, 1);
				Utils.drawOutlineString(g, "Tool: " + ((Player) objects.get(1)).getTool(), 0, 180, Color.WHITE, Color.BLACK, 1);
			} catch(IndexOutOfBoundsException ex){
				Utils.drawOutlineString(g, "Playing: False", 0, 100, Color.WHITE, Color.BLACK, 1);
			}
			
			for(Sprite s : objects){
				g.drawRect((int) s.getPolygon().getBounds().getX(), (int) s.getPolygon().getBounds().getY(), (int) s.getPolygon().getBounds().getWidth(), (int) s.getPolygon().getBounds().getHeight());
			}
			
				
		}
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	private class TAdapter extends KeyAdapter {
		
		

		@Override
		public void keyReleased(KeyEvent e) {
			for(Sprite sprite : objects){
				if(sprite instanceof Keyable)
					((Keyable) sprite).keyReleased(e);
			}
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_5){
				for(Sprite sprite : objects){
					
					if(sprite instanceof Player){
						Player player = (Player) sprite;
						player.setTool(new Sword(0,0));
					}
				}
			}
			if(key == KeyEvent.VK_F3){
				if(debug) debug = false;
				else debug = true;
			}
			if(key == KeyEvent.VK_R){
				Main.setBoard(Board.MAIN);
			}
			
			if(key == KeyEvent.VK_LEFT){
				r = r+4;
			}
			if(key == KeyEvent.VK_RIGHT){
				r = r-4;
			}
			
			
			
			for(Sprite sprite : objects){
				if(sprite instanceof Keyable)
					((Keyable) sprite).keyPressed(e);
			}
			
		}
	}

	private class MMListener extends MouseMotionAdapter {

		public void mouseMoved(MouseEvent e) {
			for(Clickable c : Main.getClickables()){
				if(c.getPolygon().contains(e.getPoint()))
					c.mouseEntered(e);
				else c.mouseExited(e);
			}
			
		}
		public void mouseDragged(MouseEvent e) {
			
		}
	}

	private class MListener extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e){
			
		}

		public void mousePressed(MouseEvent e) {
			try{
				for(Clickable c : Main.getClickables())
					if(c.getPolygon().contains(e.getPoint()))
						c.mousePressed(e);
			} catch(ConcurrentModificationException ex){
				return;
			}
			
			
		}

		public void mouseReleased(MouseEvent e) {
			for(Clickable c : Main.getClickables())
				if(c.getPolygon().contains(e.getPoint()))
						c.mouseReleased(e);
			
		}
	}

}
