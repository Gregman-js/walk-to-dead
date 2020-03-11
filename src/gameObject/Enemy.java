package gameObject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import main.Game;
import main.Handler;
import main.Window;
import map.FieldType;
import map.Map;
import net.PacketType;
import net.User;

public class Enemy {
	
	private Handler handler;

	private Random r = new Random();
	private int size = 40;
	private float posX, posY, centerX, centerY, velX = 0, velY = 0, angle;
	private float speed = 2;
	private float heal = 100, decHealer = 0.1f;
	private int dieFrame = 0, dieMaxFrame = 7, dieskips = 3, diegoes = dieskips;
	private boolean die = false;
	private String uuid;
	
	
	public Enemy(Handler handler) {
		this.handler = handler;
		this.setUuid(Game.generateUuid());
		centerX = Window.getWindowSize().width / 2 - size / 2;
		centerY = Window.getWindowSize().height / 2 - size / 2;
		int min = handler.getMap().getMapView()[0][0][0].getX();
		int x = r.nextInt(Map.fieldNum);
		int y = r.nextInt(Map.fieldNum);
		do {
			x = r.nextInt(Map.fieldNum);
			y = r.nextInt(Map.fieldNum);
		} while(!isGoodSpawnPlace(x,y));
		posX = min + x*Map.fieldSize;
		posY = min + y*Map.fieldSize;
		angle = (float)Math.toRadians(270);
	}
	
	public boolean isGoodSpawnPlace(int x, int y) {
		if(!handler.getMap().getMapView()[0][x][y].getType().isCollide() &&
				handler.getMap().getMapView()[0][x][y].getType() != FieldType.Floor)
			return true;
		else
			return false;
	}

	public void tick() {
		if(!die && !handler.getGame().getClient().running) {
			float myX = posX + size/2;
			float myY = posY + size/2;
			int x = (int)((myX - myX%Map.fieldSize) / Map.fieldSize - (myX < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
			int y = (int)((myY - myY%Map.fieldSize) / Map.fieldSize - (myY < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
			Integer[][] maze = handler.getPlayer().maze;
			Integer current = maze[x][y];
			Player target = handler.getPlayer();
			
			if(handler.getGame().getServer().running)
				for(User usr : handler.getGame().getServer().getUsers()) {
					if(current != null && usr.getPlayermp().maze[x][y] != null) {
						if(usr.getPlayermp().maze[x][y] < current) {
							maze = usr.getPlayermp().maze;
							current = maze[x][y];
							target = usr.getPlayermp();
						}
					}
				}
			if(current != null) {
				if(current > 1) {
					int req = current - 1;
					
					boolean[] axis = {false, false, false, false, false, false, false, false};
					Point go = new Point(0,0);
					if(checkMazeAvailable(maze, x+1, y, req)) {
						axis[2] = true;
						go.x = 1;
						go.y = 0; 
					}
					if(checkMazeAvailable(maze, x-1, y, req)) {
						axis[6] = true;
						go.x = -1;
						go.y = 0; 
					}
					if(checkMazeAvailable(maze, x, y+1, req)) {
						axis[4] = true;
						go.x = 0;
						go.y = +1; 
					}
					if(checkMazeAvailable(maze, x, y-1, req)) {
						axis[0] = true;
						go.x = 0;
						go.y = -1; 
					}
					
					if(axis[0] && axis[2] && checkMazeAvailable(maze, x+1, y-1, req-1)) {
						axis[1] = true;
						go.x = 1;
						go.y = -1; 
					}
					if(axis[2] && axis[4] && checkMazeAvailable(maze, x+1, y+1, req-1)) {
						axis[3] = true;
						go.x = 1;
						go.y = 1;
					}
					if(axis[4] && axis[6] && checkMazeAvailable(maze, x-1, y+1, req-1)) {
						axis[5] = true;
						go.x = -1;
						go.y = 1;
					}
					if(axis[6] && axis[0] && checkMazeAvailable(maze, x-1, y-1, req-1)) {
						axis[7] = true;
						go.x = -1;
						go.y = -1;
					}
					
	//				System.out.println(x + " - " + y);
	//				System.out.println(Arrays.toString(axis) + " go: " + go);
					Point togo = new Point(handler.getMap().getMapView()[0][x+go.x][y+go.y].getX(), handler.getMap().getMapView()[0][x+go.x][y+go.y].getY());
					angle = (float)Math.atan2(togo.y - posY, togo.x - posX) + 1.5f;			
					velX = (float)Math.sin(angle) * speed;
					velY = -(float)Math.cos(angle) * speed;
				} else {
					float dis = (float)Math.sqrt(Math.pow(target.getPosX() - posX, 2) + Math.pow(target.getPosY() - posY, 2));
					angle = (float)Math.atan2(target.getPosY() - posY, target.getPosX() - posX) + 1.5f;			
					if(dis > target.getSize()/2 + size/2*0.8) {
						velX = (float)Math.sin(angle) * speed;
						velY = -(float)Math.cos(angle) * speed;
					} else {
						velX = 0;
						velY = 0;
						target.decreaseHeal(decHealer);
					}
				}
			} else {
//				posX += posX > 0 ? -speed : speed;
				float dis = (float)Math.sqrt(Math.pow(handler.getPlayer().getPosX() - posX, 2) + Math.pow(handler.getPlayer().getPosY() - posY, 2));
				angle = (float)Math.atan2(handler.getPlayer().getPosY() - posY, handler.getPlayer().getPosX() - posX) + 1.5f;			
				if(dis > handler.getPlayer().getSize()/2 + size/2*0.8) {
					velX = (float)Math.sin(angle) * speed;
					velY = -(float)Math.cos(angle) * speed;
				} else {
					velX = 0;
					velY = 0;
					handler.getPlayer().decreaseHeal(decHealer);
				}
			}
		
			posX += velX;
			posY += velY;
			if(handler.getGame().getServer().running) {
				sendPosToAll();
			}
		}
		
		
		
		
		if(die) {
			if(diegoes <= 0) {
				diegoes = dieskips;
				dieFrame++;
				if(dieFrame >= dieMaxFrame) {
					handler.removeEnemy(this);
				}
			} else {
				diegoes--;
			}
		}
		
	}
	
	public boolean checkMazeAvailable(Integer[][] maze, int mazeX, int mazeY, int req) {
		if(mazeX < 100 && mazeX >= 0 && mazeY < 100 && mazeY >= 0 && maze[mazeX][mazeY] != null && maze[mazeX][mazeY] == req)
			return true;
		else
			return false;
	}
	
	public void render(Graphics g) {
		int offsetX = (int)(handler.getPlayer().getCenterX() - handler.getPlayer().getPosX());
		int offsetY = (int)(handler.getPlayer().getCenterY() - handler.getPlayer().getPosY());
		if(Window.isInWindow(posX + offsetX + handler.getMap().getOfsmap().x, posY + offsetY + handler.getMap().getOfsmap().y, size))
			return;
		Graphics2D g2d = (Graphics2D) g;
	    AffineTransform backup = g2d.getTransform();
	    AffineTransform a = AffineTransform.getRotateInstance(angle, posX + offsetX + handler.getMap().getOfsmap().x + size/2, posY + offsetY + handler.getMap().getOfsmap().y + size/2);
	    g2d.setTransform(a);
	    if(!die)
	    	g.drawImage(handler.getImageLoader().enemyImage, (int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y), size, size, null);
	    g2d.setTransform(backup);
	    
	    if(heal < 100 && !die) {
	    	int re = (int)(255 * (100 - heal))/100;
	    	int gr = (int)(255 * heal)/100;
	    	g.setColor(new Color(re, gr, 0));
	    	g.fillRect((int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y - 10), (int)(size*heal/100), 5);
	    	g.setColor(Color.darkGray);
	    	g.drawRect((int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y - 10), size, 5);
	    }
	    if(die) {
			g.drawImage(handler.getImageLoader().enemyDieImage.getSubimage(dieFrame * 128, 0, 128, 128), (int)(posX + offsetX + handler.getMap().getOfsmap().x), (int)(posY + offsetY + handler.getMap().getOfsmap().y), size, size, null);
	    }
	}
	
	@SuppressWarnings("unused")
	private Ellipse2D getEllipse() {
		return new Ellipse2D.Double((int)centerX, (int)centerY, size, size);
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int)posX, (int)posY, size, size);
	}
	public Rectangle getBounds(float posX, float posY, float x, float y) {
		return new Rectangle((int)(posX + x), (int)(posY + y), size, size);
	}
	
	public void setVelX(float velmX) {
		this.velX = velmX * speed;
	}
	public void setVelY(float velmY) {
		this.velY = velmY * speed;
	}
	
	public float getHeal() {
		return heal;
	}
	
	public void decreasHeal(float decrease) {
		heal = heal - decrease;
		if(heal <= 0 && !die) {
			handler.getPlayer().addKill();
			die();
		}
		sendPosToAll();
	}
	
	public void setHeal(float hl) {
		this.heal = hl;
		if(heal <= 0) {
			heal = 0;
		}
		if(heal <= 0 && !die) {
			die();
		}
	}
	
	public float getCenterX() {
		return centerX;
	}
	
	public float getCenterY() {
		return centerY;
	}


	public float getPosX() {
		return posX;
	}
	public float getPosY() {
		return posY;
	}
	
	public float getVelX() {
		return velX;
	}
	public float getVelY() {
		return velY;
	}
	
	public void setPosX(float posX) {
		this.posX = posX;
	}
	public void setPosY(float posY) {
		this.posY = posY;
	}
	
	public int getSize() {
		return size;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public float getAngle() {
		return angle;
	}

	public void die() {
		die = true;
	}
	public boolean getDie() {
		return die;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void sendPosToAll() {
		if(handler.getGame().getServer().running) {
			String sp = handler.getGame().getServer().datasplitter;
			handler.getGame().getServer().sendToAll(PacketType.EnemyStat,
					getUuid() + sp + getPosX() + sp + getPosY() + sp + getAngle() + sp + getHeal() + sp + (getDie() ? "1" : "0"));
		}
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
}
