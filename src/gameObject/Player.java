package gameObject;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import gun.FlintGun;
import gun.Grenade;
import gun.Gun;
import gun.PistolGun;
import gun.RifleGun;
import listener.KeyInput;
import loader.ImageLoader;
import main.Game;
import main.GameState;
import main.Handler;
import main.Window;
import map.Field;
import map.FieldType;
import map.Map;
import net.PacketType;

public class Player {
	
	protected Handler handler;

	protected int size = 40;
	protected float posX, posY, centerX, centerY, velX = 0, velY = 0, angle, speed = 3;
	protected Random r = new Random();
	
	protected float heal = 100;
	private int killed = 0;
	protected Backpack backpack;
	protected Point inMapPosition = new Point(49, 49);
	
	protected int timeToLoad = 0, grenadeLoadTime = 30;
	
	public Integer[][] maze = new Integer[Map.fieldNum][Map.fieldNum];
	protected int mazeRefreshRate = 15,currentMazeRefresh = mazeRefreshRate;
	
	protected Gun gun;
	
	protected BufferedImage playerImage;

	protected String uuid;

	protected String username;
	
	public Player(Handler handler, String uuid, String username) {
		this.handler = handler;
		this.uuid = uuid;
		this.username = username;
		
		this.gun = new FlintGun(handler);
		this.backpack = new Backpack();
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
//		System.out.println("Player " + x + " - " + y);
		angle = 270;
		playerImage = ImageLoader.loadImage("/player.png");
	}
	
	protected boolean isGoodSpawnPlace(int x, int y) {
		if(!handler.getMap().getMapView()[0][x][y].getType().isCollide())
			return true;
		else
			return false;
	}
	
	public void serverRestartMe() {
		int min = handler.getMap().getMapView()[0][0][0].getX();
		int x = r.nextInt(Map.fieldNum);
		int y = r.nextInt(Map.fieldNum);
		do {
			x = r.nextInt(Map.fieldNum);
			y = r.nextInt(Map.fieldNum);
		} while(!isGoodSpawnPlace(x,y));
		posX = min + x*Map.fieldSize;
		posY = min + y*Map.fieldSize;
		angle = 270;
		setKilled(0);
		heal = 100;
		handler.getGame().getServer().sendToAll(PacketType.PlayerMove, getSerialInfo());
		handler.getGame().getServer().sendToAll(PacketType.PlayerHeal, getHealInfo());
		
	}
	
	public void clientRestartMe() {
		int min = handler.getMap().getMapView()[0][0][0].getX();
		int x = r.nextInt(Map.fieldNum);
		int y = r.nextInt(Map.fieldNum);
		do {
			x = r.nextInt(Map.fieldNum);
			y = r.nextInt(Map.fieldNum);
		} while(!isGoodSpawnPlace(x,y));
		posX = min + x*Map.fieldSize;
		posY = min + y*Map.fieldSize;
		angle = 270;
		setKilled(0);
		heal = 100;
		String serinfo = handler.getGame().getClient().pack(PacketType.SetMyPos, getSerialInfo());
		handler.getGame().getClient().sendData(serinfo);
		handler.getGame().getClient().sendData(handler.getGame().getClient().pack(PacketType.RestartMe));
	}
	
	private void getStuff(int l, int x, int y, FieldType type) {
		if(handler.getGame().getServer().running) {
			String sp = handler.getGame().getServer().datasplitter;
			handler.getGame().getServer().sendToAll(PacketType.ClearStuff, l + sp + x + sp + y + sp + uuid + sp + type.toString());
		} else {
			String sp = handler.getGame().getServer().datasplitter;
			handler.getGame().getClient().sendData(handler.getGame().getClient().pack(PacketType.ClearStuff, l + sp + x + sp + y + sp + uuid + sp + type.toString()));
		}
		
	}
	
	private void getStuff(int l, int x, int y) {
		if(handler.getGame().getServer().running) {
			String sp = handler.getGame().getServer().datasplitter;
			handler.getGame().getServer().sendToAll(PacketType.ClearStuff, l + sp + x + sp + y);
		} else {
			String sp = handler.getGame().getServer().datasplitter;
			handler.getGame().getClient().sendData(handler.getGame().getClient().pack(PacketType.ClearStuff, l + sp + x + sp + y));
		}
		
	}

	public void tick() {
		float oldposX = posX;
		float oldposY = posY;
		float velrelX = velX;
		float velrelY = velY;
//		if(velX != 0 && velY != 0 && Math.abs(velX) == speed && Math.abs(velY) == speed) {
//			velrelX = (float)Math.round(Math.sqrt(2)/2 * velX);
//			velrelY = (float)Math.round(Math.sqrt(2)/2 * velY);
//		}
		int xMin = (int)((posX - posX%Map.fieldSize) / Map.fieldSize - (posX < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
		int yMin = (int)((posY - posY%Map.fieldSize) / Map.fieldSize - (posY < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
		posX += velrelX;
		posY += velrelY;
		
		
		Field[][][] mapView = handler.getMap().getMapView();
		if(getPosX() < mapView[0][0][0].getX() || getPosX() + getSize() > mapView[0][99][99].getX() + mapView[0][99][99].getFieldSize())
			posX = oldposX;
		if(getPosY() < mapView[0][0][0].getY() || getPosY() + getSize() > mapView[0][99][99].getY() + mapView[0][99][99].getFieldSize())
			posY = oldposY;
		boolean requestEkey = false;
		
		for(int l = 0; l < mapView.length; l++)
			for(int i = (xMin - 3 < 0 ? 0 : xMin - 3); i < (xMin + 3 > mapView[l].length ? mapView[l].length : xMin + 3); i++)
				for(int j = (yMin - 3 < 0 ? 0 : yMin - 3); j < (yMin + 3 > mapView[l][i].length ? mapView[l][i].length : yMin + 3); j++) {
					
					if(getBounds().intersects(mapView[0][i][j].getBounds())) {
						if(mapView[0][i][j].getType().isCollide()) {
							if(getBounds(oldposX, oldposY, velrelX, 0).intersects(mapView[0][i][j].getBounds()))
								posX = oldposX;
							if(getBounds(oldposX, oldposY, 0, velrelY).intersects(mapView[0][i][j].getBounds()))
								posY = oldposY;
						} else if(mapView[l][i][j].getType().isReachable()) {
							if(mapView[l][i][j].getType() == FieldType.GetPistol) {
								requestEkey = true;
								if(KeyInput.isEPressed) {
									gun = new PistolGun(handler);
									mapView[l][i][j].setType(FieldType.Empty);
									getStuff(l, i, j, FieldType.GetPistol);
								}
							}
							if(mapView[l][i][j].getType() == FieldType.GetRifle) {
								requestEkey = true;
								if(KeyInput.isEPressed) {
									gun = new RifleGun(handler);
									mapView[l][i][j].setType(FieldType.Empty);
									getStuff(l, i, j, FieldType.GetRifle);
								}
							}
							if(mapView[l][i][j].getType() == FieldType.GetHealthPack) {
								backpack.addHealthPack();
								mapView[l][i][j].setType(FieldType.Empty);
								getStuff(l, i, j);
							}
							if(mapView[l][i][j].getType() == FieldType.GetGrenade) {
								backpack.addGrenades();
								mapView[l][i][j].setType(FieldType.Empty);
								getStuff(l, i, j);
							}
						}
					}
				}
		handler.getGui().requestEkey(requestEkey);
		if(gun != null) {
			gun.tick(posX, posY, size, angle);
		}
		if(currentMazeRefresh <= 0) {
			generateMaze(mapView);
			currentMazeRefresh = mazeRefreshRate;
		}
		currentMazeRefresh--;
		
		if(timeToLoad > 0)
			timeToLoad--;
		
		if(oldposX!= posX || oldposY != posY) {
			if(handler.getGame().getServer().running) {
				handler.getGame().getServer().sendToAll(PacketType.PlayerMove, getSerialInfo());
			} else if(handler.getGame().getClient().running) {
				String serinfo = handler.getGame().getClient().pack(PacketType.SetMyPos, getSerialInfo());
				handler.getGame().getClient().sendData(serinfo);
			}
			
		}
	}
	
	public String getSerialInfo() {
		String splitter = handler.getGame().getServer().datasplitter;
		String info = uuid + splitter + username;
		info += splitter + posX + splitter + posY + splitter + angle;
		return info;
	}
	
	public String getHealInfo() {
		String splitter = handler.getGame().getServer().datasplitter;
		String info = uuid + splitter + username;
		info += splitter + heal;
		return info;
	}

	protected void generateMaze(Field[][][] mapView) {
		maze = new Integer[Map.fieldNum][Map.fieldNum];
		float myX = posX + size/2;
		float myY = posY + size/2;
		int x = (int)((myX - myX%Map.fieldSize) / Map.fieldSize - (myX < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
		int y = (int)((myY - myY%Map.fieldSize) / Map.fieldSize - (myY < 0 ? 1 : 0) + Map.fieldNum/2 - 1);
		inMapPosition = new Point(x, y);
		int current = 0;
		maze[x][y] = current;
		
		for(int i = 0; i < 100; i++) {
			current++;
			for(int mazeX = 0; mazeX < Map.fieldNum; mazeX++)
				for(int mazeY = 0; mazeY < Map.fieldNum; mazeY++) {
					if(maze[mazeX][mazeY] != null && maze[mazeX][mazeY] == current - 1) {
						boolean[] axis = {false, false, false, false};
						if(checkMazeAvailable(mapView, mazeX+1, mazeY)) {
							maze[mazeX + 1][mazeY] = current;
							axis[1] = true;
						}
						if(checkMazeAvailable(mapView, mazeX-1, mazeY)) {
							maze[mazeX - 1][mazeY] = current;
							axis[3] = true;
						}
						if(checkMazeAvailable(mapView, mazeX, mazeY+1)) {
							maze[mazeX][mazeY + 1] = current;
							axis[2] = true;
						}
						if(checkMazeAvailable(mapView, mazeX, mazeY-1)) {
							maze[mazeX][mazeY - 1] = current;
							axis[0] = true;
						}
//						if(axis[0] && axis[1] && checkMazeAvailable(mapView, mazeX+1, mazeY-1))
//							maze[mazeX+1][mazeY-1] = current;
//						if(axis[1] && axis[2] && checkMazeAvailable(mapView, mazeX+1, mazeY+1))
//							maze[mazeX+1][mazeY+1] = current;
//						if(axis[2] && axis[3] && checkMazeAvailable(mapView, mazeX-1, mazeY+1))
//							maze[mazeX-1][mazeY+1] = current;
//						if(axis[3] && axis[0] && checkMazeAvailable(mapView, mazeX-1, mazeY-1))
//							maze[mazeX-1][mazeY-1] = current;
					}
				}
		}
//		int ile = 0;
//		for(int mazeX = 0; mazeX < Map.fieldNum; mazeX++)
//			for(int mazeY = 0; mazeY < Map.fieldNum; mazeY++)
//				if(maze[mazeX][mazeY] == null && !mapView[0][mazeX][mazeY].getType().isCollide())
//					ile++;
//		System.out.println(ile);
		
	}
	
	public boolean checkMazeAvailable(Field[][][] mapView, int mazeX, int mazeY) {
		if(mazeX < 100 && mazeX >= 0 && mazeY < 100 && mazeY >= 0 && maze[mazeX][mazeY] == null && !mapView[0][mazeX][mazeY].getType().isCollide())
			return true;
		else
			return false;
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
//		g.setColor(COLOR);
//		g2d.fill(getEllipse());
		double rotationRequired = Math.toRadians (angle - 270);
		AffineTransform backup = g2d.getTransform();
	    AffineTransform a = AffineTransform.getRotateInstance(rotationRequired, centerX  + handler.getMap().getOfsmap().x + size/2, centerY + handler.getMap().getOfsmap().y + size/2);
	    g2d.setTransform(a);
	    if(gun != null)
	    	gun.renderGun(g, (int)centerX + handler.getMap().getOfsmap().x, (int)centerY + handler.getMap().getOfsmap().y, size);
	    g.drawImage(playerImage, (int)centerX + handler.getMap().getOfsmap().x, (int)centerY + handler.getMap().getOfsmap().y, size, size, null);
	    g2d.setTransform(backup);
	    
    	
		g.setColor(Color.white);
		String yus = username;
		if(yus != null && !yus.isEmpty() && (handler.getGame().getServer().running || handler.getGame().getClient().running)) {
			g.setFont(handler.getGui().font);
			FontMetrics fontMetrics = g.getFontMetrics();
			g.drawString(yus, (int)centerX + handler.getMap().getOfsmap().x + size/2 - fontMetrics.stringWidth(yus)/2, (int)centerY + handler.getMap().getOfsmap().y - 10);
		}
		if(heal < 100) {
	    	int re = (int)(255 * (100 - heal))/100;
	    	int gr = (int)(255 * heal)/100;
	    	g.setColor(new Color(re, gr, 0));
	    	g.fillRect((int)centerX + handler.getMap().getOfsmap().x, (int)centerY + handler.getMap().getOfsmap().y, (int)(size*heal/100), 5);
	    	g.setColor(Color.darkGray);
	    	g.drawRect((int)centerX + handler.getMap().getOfsmap().x, (int)centerY + handler.getMap().getOfsmap().y, size, 5);
	    }
	}
	
	protected Ellipse2D getEllipse() {
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
		if(handler.getGame().getServer().running) {
			handler.getGame().getServer().sendToAll(PacketType.PlayerMove, getSerialInfo());
		} else if(handler.getGame().getClient().running) {
			String serinfo = handler.getGame().getClient().pack(PacketType.SetMyPos, getSerialInfo());
			handler.getGame().getClient().sendData(serinfo);
		}
	}
	public void setStaticAngle(float angle) {
		this.angle = angle;
	}
	public float getAngle() {
		return angle;
	}
	public Gun getGun() {
		return gun;
	}
	
	public void setGun(Gun gun) {
		this.gun = gun;
	}
	
	public float getHeal() {
		return heal;
	}
	
	public void setHeal(float hl) {
		this.heal = hl;
		if(heal <= 0) {
			heal = 0;
			if(Game.state == GameState.Game && uuid.equals(handler.getPlayer().getUuid())) {
				handler.getGame().saveScore();
				Game.state = GameState.GameOver;
			}
		}
	}
	
	public void decreaseHeal(float dec) {
		heal -= dec;
		if(heal <= 0) {
			heal = 0;
			if(Game.state == GameState.Game && uuid.equals(handler.getPlayer().getUuid())) {
				handler.getGame().saveScore();
				Game.state = GameState.GameOver;
			}
		}
		if(handler.getGame().getServer().running) {
			handler.getGame().getServer().sendToAll(PacketType.PlayerHeal, getHealInfo());
		}
//		else if(handler.getGame().getClient().running) {
//			String serinfo = handler.getGame().getClient().pack(PacketType.PlayerHeal, getHealInfo());
//			handler.getGame().getClient().sendData(serinfo);
//		}
	}
	
	public void addKill() {
		setKilled(getKilled() + 1);
	}
	public int getKill() {
		return getKilled();
	}
	
	public Backpack getBackpack() {
		return backpack;
	}

	public void heal() {
		if(backpack.getHealthPack() > 0) {
			heal += 25;
			if(heal > 100)
				heal = 100;
			backpack.removeHealthPack();
		}
	}
	
	public Point getInMapPosition() {
		return inMapPosition;
	}

	public void throwGranade() {
		if(timeToLoad <= 0) {
			if(backpack.getGrenades() > 0) {
				handler.grenades.add(new Grenade(handler, angle));
				handler.getGame().newGrenade(handler.getPlayer().getPosX() + handler.getPlayer().getSize()/2, handler.getPlayer().getPosY() + handler.getPlayer().getSize()/2, angle);
				timeToLoad = grenadeLoadTime;
				backpack.removeGrenades();
			}
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getKilled() {
		return killed;
	}

	public void setKilled(int killed) {
		this.killed = killed;
	}


	
	
}
