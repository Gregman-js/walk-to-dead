package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;

import loader.HighScoreLoader;
import map.Map;

public class Gui {
	
	private Handler handler;
	private static final int roundCorner = 14, alpha = 170;
	private static final RoundRectangle2D healBar = new RoundRectangle2D.Float(20, 20, 200, 20, roundCorner, roundCorner);
	public Font font;
	private float fontSize = 20f;
	private boolean requestEkey = false;
	private boolean addL = true;
	private int addLFrameMax = 100, addLFrame = addLFrameMax;
	
	public Gui(Handler handler) {
		this.handler = handler;
		InputStream file = HighScoreLoader.class.getResourceAsStream("/minecraftfont.otf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(fontSize);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 ge.registerFont(font);
	}

	public void tick() {
    	if(addL) {
        	addLFrame--;
        	if(addLFrame <= 0)
        		addL = false;
    	}
		
	}

	public void render(Graphics g) {
		if(!Map.editable) {
			g.setFont(font);
			
			renderHealBar(g);
			
			renderEnemiesCount(g);
			
			renderAmmo(g);
			
			renderGunSide(g);
			
			renderHealthPacks(g);
			
			renderGrenades(g);
			
			renderFPS(g);
			
			if(requestEkey)
				renderERequest(g);
			
			renderLevel(g);
		}
		
	}

	private void renderERequest(Graphics g) {
		Font currentFont = g.getFont();
    	g.setFont(new Font("Courier New", Font.BOLD, 13));
    	g.setColor(Color.black);
    	String text = "E to pick";
    	FontMetrics fontMetrics = g.getFontMetrics();
    	g.drawString(text, Window.getWindowSize().width/2 - fontMetrics.stringWidth(text)/2,
    			Window.getWindowSize().height/2 - 5);
    	g.setFont(currentFont);
	}

	private void renderHealthPacks(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(3));
    	g.setColor(Color.darkGray);
    	int top = Window.getWindowSize().height - 80;
    	int left = Window.getWindowSize().width - 220;
    	int size = 60;
    	RoundRectangle2D gunbar = new RoundRectangle2D.Double(left, top, size, size, roundCorner, roundCorner);
    	g2d.draw(gunbar);
    	g.setColor(new Color(100, 100, 100, 75));
    	g2d.fill(gunbar);
    	g.drawImage(handler.getImageLoader().healthPackSide, left, top, size, size, null);
    	g.setFont(g.getFont().deriveFont(20f));
    	g.setColor(Color.white);
    	g.drawString(handler.getPlayer().getBackpack().getHealthPack() + "", left + 8, Window.getWindowSize().height - 30);
    	Font currentFont = g.getFont();
    	g.setFont(new Font("Courier New", Font.BOLD, 13));
    	g.setColor(Color.black);
    	g.drawString("H", left + 3, top + 12);
    	g.setFont(currentFont);
//    	g.getFont().re
	}
	
	private void renderGrenades(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(3));
    	g.setColor(Color.darkGray);
    	int top = Window.getWindowSize().height - 80;
    	int left = Window.getWindowSize().width - 300;
    	int size = 60;
    	RoundRectangle2D gunbar = new RoundRectangle2D.Double(left, top, size, size, roundCorner, roundCorner);
    	g2d.draw(gunbar);
    	g.setColor(new Color(100, 100, 100, 75));
    	g2d.fill(gunbar);
    	g.drawImage(handler.getImageLoader().grenadeSide, left, top, size, size, null);
    	g.setFont(g.getFont().deriveFont(20f));
    	g.setColor(Color.white);
    	g.drawString(handler.getPlayer().getBackpack().getGrenades() + "", left + 8, Window.getWindowSize().height - 30);
    	Font currentFont = g.getFont();
    	g.setFont(new Font("Courier New", Font.BOLD, 13));
    	g.setColor(Color.black);
    	g.drawString("G", left + 3, top + 12);
    	g.setFont(currentFont);
//    	g.getFont().re
	}

	private void renderHealBar(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		float heal = handler.getPlayer().getHeal();
		int re = (int)(255 * (100 - heal)/100);
    	int gr = (int)(255 * heal/100);
    	g.setColor(new Color(re, gr, 0, alpha));
    	RoundRectangle2D bar = new RoundRectangle2D.Double(healBar.getMinX(), healBar.getMinY(), healBar.getWidth() * heal / 100, healBar.getHeight(), roundCorner, roundCorner);
    	g2d.fill(bar);
    	g.setColor(Color.darkGray);
    	g2d.draw(healBar);
	}
	
	private void renderLevel(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
		g.setColor(Color.white);
		g.setFont(g.getFont().deriveFont(28f));
		String text = "Level " + Game.getLevel();
		int lte = 20;
		if(!addL)
			g.drawString(text, lte, 80);
    	if(addL) {
    		int addLFrameLocal = addLFrame;
    		if(addLFrame > addLFrameMax/2) {
    			addLFrameLocal = addLFrameMax/2;
    		}
    		float czc = 72f/(addLFrameMax/2);
    		g.setFont(g.getFont().deriveFont(100-czc*(addLFrameMax/2-addLFrameLocal)));
    		FontMetrics fontMetrics = g.getFontMetrics();
    		float disX = (Window.getWindowSize().width/2 - fontMetrics.stringWidth(text)/2) - lte;
    		float oileX = disX/(addLFrameMax/2);
    		float disY = (Window.getWindowSize().height/2) + 15 - 80;
    		float oileY = disY/(addLFrameMax/2);
        	g.drawString(text, (int) (Window.getWindowSize().width/2 - fontMetrics.stringWidth(text)/2 - (addLFrameMax/2 - addLFrameLocal)*oileX),
        			(int) (Window.getWindowSize().height/2 - (addLFrameMax/2 - addLFrameLocal)*oileY + 15));
    	}
	}
	
	public void addLevel() {
		addL = true;
		addLFrame  = addLFrameMax;
	}
	
	private void renderEnemiesCount(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(20f));
		String text = "Killed: " + handler.getPlayer().getKill() + "  Enemies: " + handler.getEnemiesSize();
		FontMetrics fontMetrics = g.getFontMetrics();
    	g.drawString(text, Window.getWindowSize().width - fontMetrics.stringWidth(text) - 10, 30);
	}
	
	private void renderAmmo(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
		int[] ammo = handler.getPlayer().getGun().getAmmo();
		g.setColor(Color.black);
    	g.setFont(g.getFont().deriveFont(30f));
    	String text = ammo[0] + " / " + ammo[1];
    	FontMetrics fontMetrics = g.getFontMetrics();
    	g.drawString(text, Window.getWindowSize().width - fontMetrics.stringWidth(text) - 15, Window.getWindowSize().height - 15);
    	
    	if(handler.getPlayer().getGun().isReloading()) {
    		int[] rel = handler.getPlayer().getGun().getReload();
    		float progress = (float)rel[0] / (float)rel[1];
    		int top = Window.getWindowSize().height - 140;
    		g.setColor(Color.darkGray);
    		g.fillRect(Window.getWindowSize().width - 90, top, (int)(70*(1-progress)), 10);
    		g.setColor(Color.black);
    		g.drawRect(Window.getWindowSize().width - 90, top, 70, 10);
    	}
	}
	
	private void renderGunSide(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(3));
    	g.setColor(Color.darkGray);
    	int top = Window.getWindowSize().height - 120;
    	RoundRectangle2D gunbar = new RoundRectangle2D.Double(Window.getWindowSize().width - 90, top, 70, 70, roundCorner, roundCorner);
    	g2d.draw(gunbar);
    	g.setColor(new Color(100, 100, 100, 75));
    	g2d.fill(gunbar);
    	g.drawImage(handler.getPlayer().getGun().getGunSideImage(), Window.getWindowSize().width - 90, top, 70, 70, null);
	}
	
	private void renderFPS(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(20f));
    	g.drawString("FPS: " + Game.getFPS(), 10, Window.getWindowSize().height - 20);
	}

	public void requestEkey(boolean requestEkey) {
		this.requestEkey = requestEkey;
	}

}
