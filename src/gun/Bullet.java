package gun;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import gameObject.Enemy;
import gameObject.Player;
import gameObject.PlayerMp;
import main.Handler;
import main.Window;
import map.Field;
import map.FieldType;
import map.Map;
import net.User;

public class Bullet {
	
	private Handler handler;
	private float posX, posY, velX, velY, speed = 15, angle, size = 0.6f, diesize = 0.4f, decreasHeal = 15;
	private int dieFrame = 0, dieMaxFrame = 3, dieskips = 3, diegoes = dieskips;
	private boolean die = false;
	
	public Bullet(Handler handler, float posX, float posY, float angle, float decreasHeal) {
		this.handler = handler;
		this.posX = posX;
		this.posY = posY;
		this.angle = angle;
		this.decreasHeal = decreasHeal;
		this.velX = speed * (float)Math.cos(Math.toRadians(angle));
		this.velY = speed * (float)Math.sin(Math.toRadians(angle));
//		System.out.println(posX + "" + posY);
	}
	

	public boolean tick() {
		
		if(!die) {
			int xMin = (int)((posX - posX%Map.fieldSize) / Map.fieldSize - (posX < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
			int yMin = (int)((posY - posY%Map.fieldSize) / Map.fieldSize - (posY < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
			int rozb = 2;
			posX += velX;
			posY += velY;
			
			Field mapView[][][] = handler.getMap().getMapView();
			
			if(posX < mapView[0][0][0].getX() || posX + 5 > mapView[0][99][99].getX() + mapView[0][99][99].getFieldSize())
				return true;
			if(posY < mapView[0][0][0].getY() || posY + 5 > mapView[0][99][99].getY() + mapView[0][99][99].getFieldSize())
				return true;
			
//			System.out.println(posX + " - " + posY);
			
			for(int l = 0; l < mapView.length; l++)
				for(int i = (xMin - rozb < 0 ? 0 : xMin - rozb); i < (xMin + rozb > mapView[l].length ? mapView[l].length : xMin + rozb); i++) {
					for(int j = (yMin - rozb < 0 ? 0 : yMin - rozb); j < (yMin + rozb > mapView[l][i].length ? mapView[l][i].length : yMin + rozb); j++) {
						if(getBounds().intersects(mapView[0][i][j].getBounds()) && mapView[0][i][j].getType().isCollide() && mapView[0][i][j].getType() != FieldType.Water)
							return true;
					}
				}
			for(Enemy enemy : handler.getEnemies()) {
				if(!enemy.getDie()) {
					Point bulletO = new Point((int)(posX + handler.getImageLoader().bulletImage.getWidth()*size/2),
							(int)(posY + handler.getImageLoader().bulletImage.getHeight()*size/2));
					Point enemyO = new Point((int)(enemy.getPosX() + enemy.getSize()/2),
							(int)(enemy.getPosY() + enemy.getSize()/2));
					
					Point diff = new Point((int)Math.abs(bulletO.x - enemyO.x), (int)Math.abs(bulletO.y - enemyO.y));
					
					if(Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2)) < enemy.getSize()/2*0.8 + handler.getImageLoader().bulletImage.getWidth()*size/2) {
						if(!handler.getGame().getClient().running)
							enemy.decreasHeal(decreasHeal);
						return true;
					}
				}
			}
			if(handler.getGame().getServer().running) {
				for(User usr : handler.getGame().getServer().getUsers()) {
					PlayerMp enemy = usr.getPlayermp();
					if(enemy.getHeal() > 0) {
						Point bulletO = new Point((int)(posX + handler.getImageLoader().bulletImage.getWidth()*size/2),
								(int)(posY + handler.getImageLoader().bulletImage.getHeight()*size/2));
						Point enemyO = new Point((int)(enemy.getPosX() + enemy.getSize()/2),
								(int)(enemy.getPosY() + enemy.getSize()/2));
						
						Point diff = new Point((int)Math.abs(bulletO.x - enemyO.x), (int)Math.abs(bulletO.y - enemyO.y));
						
						if(Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2)) < enemy.getSize()/2*0.8 + handler.getImageLoader().bulletImage.getWidth()*size/2) {
							enemy.decreaseHeal(decreasHeal);
							return true;
						}
					}
				}
			} else {
				for(PlayerMp enemy : handler.getGame().getClient().getPlayersMp()) {
					if(enemy.getHeal() > 0) {
						Point bulletO = new Point((int)(posX + handler.getImageLoader().bulletImage.getWidth()*size/2),
								(int)(posY + handler.getImageLoader().bulletImage.getHeight()*size/2));
						Point enemyO = new Point((int)(enemy.getPosX() + enemy.getSize()/2),
								(int)(enemy.getPosY() + enemy.getSize()/2));
						
						Point diff = new Point((int)Math.abs(bulletO.x - enemyO.x), (int)Math.abs(bulletO.y - enemyO.y));
						
						if(Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2)) < enemy.getSize()/2*0.8 + handler.getImageLoader().bulletImage.getWidth()*size/2) {
							enemy.decreaseHeal(decreasHeal);
							return true;
						}
					}
				}
			}
			Player enemy = handler.getPlayer();
			if(enemy.getHeal() > 0) {
				Point bulletO = new Point((int)(posX + handler.getImageLoader().bulletImage.getWidth()*size/2),
						(int)(posY + handler.getImageLoader().bulletImage.getHeight()*size/2));
				Point enemyO = new Point((int)(enemy.getPosX() + enemy.getSize()/2),
						(int)(enemy.getPosY() + enemy.getSize()/2));
				
				Point diff = new Point((int)Math.abs(bulletO.x - enemyO.x), (int)Math.abs(bulletO.y - enemyO.y));
				
				if(Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2)) < enemy.getSize()/2*0.8 + handler.getImageLoader().bulletImage.getWidth()*size/2) {
					if(handler.getGame().getServer().running)
						enemy.decreaseHeal(decreasHeal);
					return true;
				}
			}
			return false;
		} else {
			if(diegoes <= 0) {
				diegoes = dieskips;
				dieFrame++;
				if(dieFrame >= dieMaxFrame) {
					handler.removeBullet(this);
				}
			} else {
				diegoes--;
			}
		}
		return false;
		
	}

	public void render(Graphics g, float offsetX,float offsetY) {
//		System.out.println("Shoot");
		if(Window.isInWindow(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() + handler.getImageLoader().bulletImage.getWidth()/2 - 8,
				posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() + handler.getImageLoader().bulletImage.getHeight()/5,
				15))
			return;
		Graphics2D g2d = (Graphics2D)g;
		
//		g2d.fill(getEllipse());
		AffineTransform backup = g2d.getTransform();
	    AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians(angle), (int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() + handler.getImageLoader().bulletImage.getWidth()/2 - 8),
	    		(int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() + handler.getImageLoader().bulletImage.getHeight()/5));
	    g2d.setTransform(a);
	    if(!die) {
	    	g.drawImage(handler.getImageLoader().bulletImage, (int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() - 8),
	    			(int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() - 2),
	    			(int)(handler.getImageLoader().bulletImage.getWidth()*size), (int)(handler.getImageLoader().bulletImage.getHeight()*size), null);
	    } else {
	    	g.drawImage(handler.getImageLoader().bulletDieImage.getSubimage(dieFrame * 64, 0, 64, 64), (int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() - (int)(64*diesize)/2),
	    			(int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() - (int)(64*diesize)/2), (int)(64*diesize), (int)(64*diesize), null);
	    }
	    g2d.setTransform(backup);
		
	}
	
	public Ellipse2D getEllipse() {
		return new Ellipse2D.Double((int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX()),
				(int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY()), 5, 5);
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int)posX, (int)posY, 5, 5);
	}

	public void die() {
		die = true;
	}
	public boolean getDie() {
		return die;
	}

}
