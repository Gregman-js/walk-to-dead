package menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import main.Window;

public class MenuButton {
	
	private String title;
	private boolean hover = false;
	private int lightColor = 200;
	private boolean f = false;
	private int index;
	private int top;
	private int space;
	private int left;
	private int width;
	private int height;

	public MenuButton(String title, int index, int top, int space, int width, int height) {
		this.title = title;
		this.index = index;
		this.top = top;
		this.space = space;
		this.width = width;
		this.height = height;
	}
	
	public MenuButton(String title, int top, int left, int width, int height) {
		f = true;
		this.title = title;
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
	}
	
//	public void render(Graphics g, boolean sen) {
//		if(sen) {
//			g.setColor(new Color(0, 200, 0, 100));
//		} else {
//			g.setColor(new Color(200, 0, 0, 100));
//		}
//		render(g);
//	}

	public void render(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
//		Rectangle rect = getBounds();
		if(isHover())
			g.setColor(Color.white);
		else
			g.setColor(new Color(lightColor, lightColor, lightColor));
		FontMetrics fontMetrics = g.getFontMetrics();
//		g2d.draw(rect);
		g.drawString(getTitle(), (int)(f ? left : Window.getWindowSize().width/2 - fontMetrics.stringWidth(getTitle())/2), (int)(top + (height+space)*index + (f ? 38 : 32)));
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int)(f ? left : Window.getWindowSize().width/2 - width/2), (int)(top + (height+space)*index), width, height);
	}

	public boolean isHover() {
		return hover;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

}
