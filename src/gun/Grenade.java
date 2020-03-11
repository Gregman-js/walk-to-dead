package gun;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import gameObject.Enemy;
import gameObject.PlayerMp;
import main.Handler;
import main.Window;
import map.Field;
import map.FieldType;
import map.Map;
import net.User;

public class Grenade {
	
	protected static int tickX = -25, tickY = -10;
	private float posX, posY, velX, velY, speed = 7;
	private float size = 16, relSize = 0;
	private Handler handler;
	private final static float decreasHeal = 80;
	private int dieFrame = 0, dieMaxFrame = 20, dieskips = 1, diegoes = dieskips;
	private boolean die = false, boom = false;
	private boolean isColliding = false, falling = false;
	private int life = 60, boomsize = 100;
	private float gravity = 0.99f, gravityDown = 0.8f, bouncy = 0.5f;
	
	public Grenade(Handler handler, float angle) {
		this.handler = handler;
		double odl = Math.sqrt(Math.pow(tickX, 2) + Math.pow(tickY, 2));
		double rotationRequired = Math.toRadians (angle + 30);
		double x = odl * Math.cos(rotationRequired);
		double y = odl * Math.sin(rotationRequired);
		this.posX = handler.getPlayer().getPosX() + handler.getPlayer().getSize()/2 + (float)x;
		this.posY = handler.getPlayer().getPosY() + handler.getPlayer().getSize()/2 + (float)y;
		this.velX = speed * (float)Math.cos(Math.toRadians(angle));
		this.velY = speed * (float)Math.sin(Math.toRadians(angle));
	}
	
	public Grenade(Handler handler, float posX, float posY, float angle) {
		this.handler = handler;
		double odl = Math.sqrt(Math.pow(tickX, 2) + Math.pow(tickY, 2));
		double rotationRequired = Math.toRadians (angle + 30);
		double x = odl * Math.cos(rotationRequired);
		double y = odl * Math.sin(rotationRequired);
		this.posX = posX + (float)x;
		this.posY = posY + (float)y;
		this.velX = speed * (float)Math.cos(Math.toRadians(angle));
		this.velY = speed * (float)Math.sin(Math.toRadians(angle));
	}

public boolean tick() {
	if(life > 0) {
		life--;
	} else {
		die = true;
	}
	boolean isDown = false;
	float fspeed = 0.15f;
	if(!falling) {
		relSize = relSize + fspeed;
		if(relSize > 1.5f) {
			falling = true;
			relSize = 1.5f;
		}
	} else {
		relSize = relSize - fspeed;
		float minSize = -2;
		if(relSize < minSize) {
			isDown = true;
			relSize = minSize;
		}
	}
	
	
	velX = velX * (isDown ? gravityDown : gravity);
	velY = velY * (isDown ? gravityDown : gravity);
	float oldposX = posX;
	float oldposY = posY;
	float velrelX = velX;
	float velrelY = velY;
	if(!die) {
		int xMin = (int)((posX - posX%Map.fieldSize) / Map.fieldSize - (posX < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
		int yMin = (int)((posY - posY%Map.fieldSize) / Map.fieldSize - (posY < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
		int rozb = 3;
		posX += velX;
		posY += velY;
		
		Field mapView[][][] = handler.getMap().getMapView();
		
		boolean bok = false;
		
		if(posX < mapView[0][0][0].getX() || posX + size > mapView[0][99][99].getX() + mapView[0][99][99].getFieldSize()) {
			bok = true;
			if(!isColliding) {
				velX = -velX * bouncy;
				isColliding = true;
			}
		}
		if(posY < mapView[0][0][0].getY() || posY + size > mapView[0][99][99].getY() + mapView[0][99][99].getFieldSize()) {
			bok = true;
			if(!isColliding) {
				velY = -velY * bouncy;
				isColliding = true;
			}
		}
		
		int ile = 0;
		for(int l = 0; l < mapView.length; l++)
			for(int i = (xMin - rozb < 0 ? 0 : xMin - rozb); i < (xMin + rozb > mapView[l].length ? mapView[l].length : xMin + rozb); i++) {
				for(int j = (yMin - rozb < 0 ? 0 : yMin - rozb); j < (yMin + rozb > mapView[l][i].length ? mapView[l][i].length : yMin + rozb); j++) {
					if(getBounds().intersects(mapView[0][i][j].getBounds()) && mapView[0][i][j].getType().isCollide() && mapView[0][i][j].getType() != FieldType.Water) {
						ile++;
						if(getBounds(oldposX, oldposY, velrelX, 0).intersects(mapView[0][i][j].getBounds()) && !isColliding) {
							isColliding = true;
							velX = -velX * bouncy;
						}
						if(getBounds(oldposX, oldposY, 0, velrelY).intersects(mapView[0][i][j].getBounds()) && !isColliding) {
							isColliding = true;
							velY = -velY * bouncy;
						}
					}
					if(getBounds().intersects(mapView[0][i][j].getBounds()) && mapView[0][i][j].getType() == FieldType.Water && isDown) {
						handler.removeGrenade(this);
					}
				}
			}
		int ile2 = 0;
		for(Enemy enemy : handler.getEnemies()) {
			if(getBounds().intersects(enemy.getBounds())) {
				ile2++;
				if(getBounds(oldposX, oldposY, velrelX, 0).intersects(enemy.getBounds()) && !isColliding) {
					velX = -velX * bouncy;
					isColliding = true;
				}
				if(getBounds(oldposX, oldposY, 0, velrelY).intersects(enemy.getBounds()) && !isColliding) {
					velY = -velY * bouncy;
					isColliding = true;
				}
			}
		}
		if(handler.getGame().getServer().running)
		for(User usr : handler.getGame().getServer().getUsers()) {
			PlayerMp enemy = usr.getPlayermp();
			if(getBounds().intersects(enemy.getBounds())) {
				ile2++;
				if(getBounds(oldposX, oldposY, velrelX, 0).intersects(enemy.getBounds()) && !isColliding) {
					velX = -velX * bouncy;
					isColliding = true;
				}
				if(getBounds(oldposX, oldposY, 0, velrelY).intersects(enemy.getBounds()) && !isColliding) {
					velY = -velY * bouncy;
					isColliding = true;
				}
			}
		}
		else
			for(PlayerMp enemy : handler.getGame().getClient().getPlayersMp()) {
				if(getBounds().intersects(enemy.getBounds())) {
					ile2++;
					if(getBounds(oldposX, oldposY, velrelX, 0).intersects(enemy.getBounds()) && !isColliding) {
						velX = -velX * bouncy;
						isColliding = true;
					}
					if(getBounds(oldposX, oldposY, 0, velrelY).intersects(enemy.getBounds()) && !isColliding) {
						velY = -velY * bouncy;
						isColliding = true;
					}
				}
			}
		if(!bok && ile == 0 && ile2 == 0)
			isColliding = false;
		
		return false;
	} else {
		if(!boom && handler.getGame().getServer().running) {
			Point grenadeO = new Point((int)(posX + size/2),
					(int)(posY + size/2));
			for(Enemy enemy : handler.getEnemies()) {
				if(!enemy.getDie()) {
					Point enemyO = new Point((int)(enemy.getPosX() + enemy.getSize()/2),
							(int)(enemy.getPosY() + enemy.getSize()/2));
					
					Point diff = new Point((int)Math.abs(grenadeO.x - enemyO.x), (int)Math.abs(grenadeO.y - enemyO.y));
					
					float dis = (float)Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
					System.out.println("1");
					
					if(dis < boomsize) {
						enemy.decreasHeal(getDecHeal(boomsize, dis, decreasHeal));
					}
				}
			}
			for(User usr : handler.getGame().getServer().getUsers()) {
				PlayerMp enemy = usr.getPlayermp();
				if(enemy.getHeal() > 0) {
					Point enemyO = new Point((int)(enemy.getPosX() + enemy.getSize()/2),
							(int)(enemy.getPosY() + enemy.getSize()/2));
					
					Point diff = new Point((int)Math.abs(grenadeO.x - enemyO.x), (int)Math.abs(grenadeO.y - enemyO.y));
					
					float dis = (float)Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
					
					System.out.println("2");
					if(dis < boomsize) {
						enemy.decreaseHeal(getDecHeal(boomsize, dis, decreasHeal));
					}
				}
			}
			if(handler.getPlayer().getHeal() > 0) {
				Point playerO = new Point((int)(handler.getPlayer().getPosX() + handler.getPlayer().getSize()/2),
						(int)(handler.getPlayer().getPosY() + handler.getPlayer().getSize()/2));
				
				Point diff = new Point((int)Math.abs(grenadeO.x - playerO.x), (int)Math.abs(grenadeO.y - playerO.y));
				
				float dis = (float)Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
				
				if(dis < boomsize) {
					System.out.println("3");
					handler.getPlayer().decreaseHeal(getDecHeal(boomsize, dis, decreasHeal));
				}
			}
			boom = true;
		}
		if(diegoes <= 0) {
			diegoes = dieskips;
			dieFrame++;
			if(dieFrame >= dieMaxFrame) {
				handler.removeGrenade(this);
			}
		} else {
			diegoes--;
		}
	}
	return false;
		
	}

	private float getDecHeal(float area, float dist, float heal) {
		float rage = (area - dist)+area/2;
		if(rage > area)
			rage = area;
		return rage/area*heal;
	}

	public void render(Graphics g, float offsetX,float offsetY) {
//		System.out.println("Shoot");
		if(Window.isInWindow(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() + handler.getImageLoader().bulletImage.getWidth()/2 - 8,
				posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() + handler.getImageLoader().bulletImage.getHeight()/5,
				15))
			return;
		
//		g2d.fill(getEllipse());
	    if(!die) {
//	    	g.drawImage(handler.getImageLoader().bulletImage, (int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() - 8),
//	    			(int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() - 2),
//	    			(int)(handler.getImageLoader().bulletImage.getWidth()*size), (int)(handler.getImageLoader().bulletImage.getHeight()*size), null);
//	    	g.setColor(Color.darkGray);
//	    	g2d.fill(getEllipse());
	    	g.drawImage(handler.getImageLoader().grenadeImage, (int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() - relSize),
					(int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() - relSize), (int)(size + relSize*2), (int)(size + relSize*2), null);
	    } else {
	    	int px = (int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX());
	    	int py = (int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY());
	    	px = px + (int)size/2 - boomsize;
	    	py = py + (int)size/2 - boomsize;
	    	g.drawImage(handler.getImageLoader().grenadeDieImage.getSubimage(dieFrame%5*192, (dieFrame - dieFrame%5) / 5 * 192, 192, 192), px, py,
	    			(int)boomsize*2, (int)boomsize*2, null);
//	    	g2d.draw(getBoomEllipse());
	    }
		
	}
	
	public Ellipse2D getEllipse() {
		return new Ellipse2D.Double((int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX() - relSize),
				(int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY() - relSize), size + relSize*2, size + relSize*2);
	}
	
	public Ellipse2D getBoomEllipse(){
		int px = (int)(posX + handler.getPlayer().getCenterX() + handler.getMap().getOfsmap().x - handler.getPlayer().getPosX());
    	int py = (int)(posY + handler.getPlayer().getCenterY() + handler.getMap().getOfsmap().y - handler.getPlayer().getPosY());
    	px = px + (int)size/2 - boomsize;
    	py = py + (int)size/2 - boomsize;
		return new Ellipse2D.Double(px, py, (int)boomsize*2, (int)boomsize*2);
	}
	
	public Rectangle getBounds() {
		int ile = 3;
		return new Rectangle((int)posX + ile, (int)posY + ile, (int)size - ile*2, (int)size - ile*2);
	}
	
	public Rectangle getBounds(float posX, float posY, float x, float y) {
		int ile = 3;
		x+=ile;
		y+=ile;
		return new Rectangle((int)(posX + x), (int)(posY + y), (int)size-ile*2, (int)size-ile*2);
	}

	public void die() {
		die = true;
	}
	public boolean getDie() {
		return die;
	}
}
