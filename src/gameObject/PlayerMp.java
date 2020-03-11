package gameObject;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.Handler;
import main.Window;
import map.Field;

public class PlayerMp extends Player {
	
	
	public PlayerMp(Handler handler, float posX, float posY, String uuid, String username) {
		super(handler, uuid, username);
		this.posX = posX;
		this.posY = posY;
		angle = 270;
	}
	
	public void tick() {
		if(handler.getGame().getServer().running) {
			Field[][][] mapView = handler.getMap().getMapView();
			if(currentMazeRefresh <= 0) {
				generateMaze(mapView);
				currentMazeRefresh = mazeRefreshRate;
			}
			currentMazeRefresh--;
		}
		
	}
	@Override
	public void render(Graphics g) {
		int offsetX = (int)(handler.getPlayer().getCenterX() - handler.getPlayer().getPosX());
		int offsetY = (int)(handler.getPlayer().getCenterY() - handler.getPlayer().getPosY());
//		System.out.println("render " + (posX + offsetX + handler.getMap().getOfsmap().x) + " - " + (posY + offsetY + handler.getMap().getOfsmap().y));
		if(Window.isInWindow(posX + offsetX + handler.getMap().getOfsmap().x, posY + offsetY + handler.getMap().getOfsmap().y, size))
			return;
		
		Graphics2D g2d = (Graphics2D) g;
	    AffineTransform backup = g2d.getTransform();
	    double rotationRequired = Math.toRadians (angle - 270);
	    AffineTransform a = AffineTransform.getRotateInstance(rotationRequired, posX + offsetX + handler.getMap().getOfsmap().x + size/2, posY + offsetY + handler.getMap().getOfsmap().y + size/2);
	    g2d.setTransform(a);
	    if(gun != null)
	    	gun.renderGun(g, (int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y), size);
    	g.drawImage(playerImage, (int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y), size, size, null);
	    g2d.setTransform(backup);
	    
	    g.setColor(Color.white);
		String yus = username;
		if(yus != null && !yus.isEmpty() && (handler.getGame().getServer().running || handler.getGame().getClient().running)) {
			g.setFont(handler.getGui().font);
			FontMetrics fontMetrics = g.getFontMetrics();
			g.drawString(yus, (int) (posX + offsetX + handler.getMap().getOfsmap().x + size/2 - fontMetrics.stringWidth(yus)/2), (int) (posY + offsetY + handler.getMap().getOfsmap().y - 10));
		}
		if(heal < 100) {
	    	int re = (int)(255 * (100 - heal))/100;
	    	int gr = (int)(255 * heal)/100;
	    	g.setColor(new Color(re, gr, 0));
	    	g.fillRect((int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y - 10), (int)(size*heal/100), 5);
	    	g.setColor(Color.darkGray);
	    	g.drawRect((int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y - 10), size, 5);
	    }
	}

}
