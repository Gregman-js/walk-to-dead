package map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import loader.MapLoader;
import main.Handler;
import main.Window;
import net.PacketType;

public class Map {
	
	private Handler handler;
	private FieldDrawer fDrawer;
	private Field mapView[][][];
	public static int fieldSize = 42;
	public static int fieldNum = 100;
	public static int layerNum = 2;
	private Random r = new Random();
	private Point ofsmap = new Point(0,0);
	private Point mousePos = new Point(0,0);
	public static final boolean editable = false;
	private static final boolean mazeRender = false;
	
	private int mapCenter;
	private List<Point> liscie;
	private int offsetX;
	private int offsetY;
	private int woogrSize = 3;
	
	public Map(Handler handler) {
		this.handler = handler;
		if(editable)
			fieldSize = fieldSize/2;
		this.fDrawer = new FieldDrawer(editable);
		mapView = new Field[layerNum][fieldNum][fieldNum];
		mapCenter = fieldSize * fieldNum / 2;
		FieldType[][][] mapSets = MapLoader.readFromFile();
		for(int l = 0; l < layerNum; l++)
			for(int i = 0; i < fieldNum; i++)
				for(int j = 0; j < fieldNum; j++) {
					FieldType type = FieldType.Grass;
					type = mapSets[l][i][j];
					if(type == null || type.isReachable())
						type = FieldType.Empty;
					mapView[l][i][j] = new Field(fieldSize * (i+1) - mapCenter, fieldSize * (j+1)  - mapCenter, fieldSize, type);
				}
				
	}
	
	public void spawnPistol(int pula) {
		do {
			int x = r.nextInt(100), y = r.nextInt(100);
			if(pula > 0 && mapView[0][x][y].getType() == FieldType.Floor && mapView[1][x][y].getType() == FieldType.Empty) {
				mapView[1][x][y].setType(FieldType.GetPistol);
				if(handler.getGame().getServer().running)
					spawner(FieldType.GetPistol, x, y);
				pula--;
			}
		} while(pula != 0);
	}
	
	public void spawnRifle(int pula) {
		do {
			int x = r.nextInt(100), y = r.nextInt(100);
			if(pula > 0 && mapView[0][x][y].getType() == FieldType.Floor && mapView[1][x][y].getType() == FieldType.Empty) {
				mapView[1][x][y].setType(FieldType.GetRifle);
				if(handler.getGame().getServer().running)
					spawner(FieldType.GetRifle, x, y);
				pula--;
			}
		} while(pula != 0);
	}
	
	public void spawnGrenade(int pula) {
		do {
			int x = r.nextInt(100), y = r.nextInt(100);
			if(pula > 0 && !mapView[0][x][y].getType().isCollide() && mapView[1][x][y].getType() == FieldType.Empty) {
				mapView[1][x][y].setType(FieldType.GetGrenade);
				if(handler.getGame().getServer().running)
					spawner(FieldType.GetGrenade, x, y);
				pula--;
			}
		} while(pula != 0);
	}
	
	public void spawnHeal(int pula) {
		do {
			int x = r.nextInt(100), y = r.nextInt(100);
			if(pula > 0 && mapView[0][x][y].getType() == FieldType.Floor && mapView[1][x][y].getType() == FieldType.Empty) {
				mapView[1][x][y].setType(FieldType.GetHealthPack);
				if(handler.getGame().getServer().running)
					spawner(FieldType.GetHealthPack, x, y);
				pula--;
			}
		} while(pula != 0);
	}
	
	
	private void spawner(FieldType type, int x, int y) {
		String sp = handler.getGame().getServer().datasplitter;
		handler.getGame().getServer().sendToAll(PacketType.SpawnStuff, type.toString() + sp + x + sp + y);
		
	}

	public void tick() {
		if(!handler.getGame().getClient().running) {
			spawnThings();
		}
		fDrawer.tick();
	}

	public void render(Graphics g) {
		
		ofsmap = new Point(0,0);
		offsetX = (int)(handler.getPlayer().getCenterX() - handler.getPlayer().getPosX());
		offsetY = (int)(handler.getPlayer().getCenterY() - handler.getPlayer().getPosY());
		
		if(mapView[0][0][0].getX() + offsetX > 0) {
			ofsmap.x = -(mapView[0][0][0].getX() + offsetX);
			offsetX = -mapView[0][0][0].getX();
		}
		if(mapView[0][0][0].getY() + offsetY > 0) {
			ofsmap.y = -(mapView[0][0][0].getY() + offsetY);
			offsetY = -mapView[0][0][0].getY();
		}
		if(mapView[0][99][99].getX() + (offsetX - handler.getPlayer().getCenterX()*2) < 0) {
			ofsmap.x = -(int)(mapView[0][99][99].getX() + (offsetX - handler.getPlayer().getCenterX()*2));
			offsetX = -(int)(mapView[0][99][99].getX() - handler.getPlayer().getCenterX()*2);
		}
		if(mapView[0][99][99].getY() + (offsetY - handler.getPlayer().getCenterY()*2) < 0) {
			ofsmap.y = -(int)(mapView[0][99][99].getY() + (offsetY - handler.getPlayer().getCenterY()*2));
			offsetY = -(int)(mapView[0][99][99].getY() - handler.getPlayer().getCenterY()*2);
		}
		
		liscie = new ArrayList<Point>();
		
		for(int l = 0; l < mapView.length; l++)
			for(int i = 0; i < 100; i++)
				for(int j = 0; j < 100; j++) {
					if(l == 0 && mapView[l][i][j].getType() == FieldType.Wood && !Window.isInWindow(mapView[l][i][j].getX() + offsetX - fieldSize*woogrSize /2 + fieldSize/2, mapView[l][i][j].getY() + offsetY - fieldSize*woogrSize /2 + fieldSize/2, fieldSize*woogrSize))
						liscie.add(new Point(i, j));
					fDrawer.drawField(mapView[l][i][j], offsetX, offsetY, fieldSize, g);
					g.setColor(Color.black);
//					g.drawString(i + "."+ j, mapView[l][i][j].getX() + offsetX, mapView[l][i][j].getY() + offsetY + 25);
				}
		if(mazeRender)
			renderMaze(g, offsetX, offsetY);
	}
	
	public void renderSecLayer(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(new Color(0, 160, 0, 100));
		for(Point lisc : liscie) {
			g2d.fill(new Ellipse2D.Double(mapView[0][lisc.x][lisc.y].getX() + offsetX - fieldSize*woogrSize /2 + fieldSize/2, mapView[0][lisc.x][lisc.y].getY() + offsetY - fieldSize*woogrSize /2 + fieldSize/2, fieldSize*woogrSize , fieldSize*woogrSize ));
		}
	}
	
	private void renderMaze(Graphics g, int offsetX, int offsetY) {
		Integer[][] maze = handler.getPlayer().maze;
		Font currentFont = g.getFont();
		g.setFont(new Font("Courier New", Font.BOLD, 18));
		g.setColor(Color.black);
		for(int mazeX = 0; mazeX < Map.fieldNum; mazeX++)
			for(int mazeY = 0; mazeY < Map.fieldNum; mazeY++)
				if(maze[mazeX][mazeY] != null) {
					g.drawString(maze[mazeX][mazeY] + "", mapView[0][mazeX][mazeY].getX() + offsetX + 12, mapView[0][mazeX][mazeY].getY() + offsetY + 25);
				}
		g.setFont(currentFont);
	}


	public Field[][][] getMapView() {
		return mapView;
	}
	
	public Point getOfsmap() {
		return ofsmap;
	}
	
	public void setMousePoint(int x, int y) {
		mousePos.x = x;
		mousePos.y= y;
	}

	public void changeFieldType(int key) {
		int offsetX = (int)(handler.getPlayer().getCenterX() - handler.getPlayer().getPosX());
		int offsetY = (int)(handler.getPlayer().getCenterY() - handler.getPlayer().getPosY());
		
		if(mapView[0][0][0].getX() + offsetX > 0) {
			ofsmap.x = -(mapView[0][0][0].getX() + offsetX);
			offsetX = -mapView[0][0][0].getX();
		}
		if(mapView[0][0][0].getY() + offsetY > 0) {
			ofsmap.y = -(mapView[0][0][0].getY() + offsetY);
			offsetY = -mapView[0][0][0].getY();
		}
		if(mapView[0][99][99].getX() + (offsetX - handler.getPlayer().getCenterX()*2) < 0) {
			ofsmap.x = -(int)(mapView[0][99][99].getX() + (offsetX - handler.getPlayer().getCenterX()*2));
			offsetX = -(int)(mapView[0][99][99].getX() - handler.getPlayer().getCenterX()*2);
		}
		if(mapView[0][99][99].getY() + (offsetY - handler.getPlayer().getCenterY()*2) < 0) {
			ofsmap.y = -(int)(mapView[0][99][99].getY() + (offsetY - handler.getPlayer().getCenterY()*2));
			offsetY = -(int)(mapView[0][99][99].getY() - handler.getPlayer().getCenterY()*2);
		}
//		int fieldOffsetX = (int)(handler.getPlayer().getCenterX() - handler.getPlayer().getPosX());
//		int fieldOffsetY = (int)(handler.getPlayer().getCenterY() - handler.getPlayer().getPosY());
		Point fieldPos = new Point(mousePos.x - (mousePos.x - (mapView[0][0][0].getX() + offsetX))%fieldSize,
				mousePos.y - (mousePos.y - (mapView[0][0][0].getY() + offsetY))%fieldSize);
		int mapCenter = fieldSize * fieldNum / 2;
		int targeti = (fieldPos.x - offsetX + mapCenter)/fieldSize - 1;
		int targetj = (fieldPos.y - offsetY + mapCenter)/fieldSize - 1;
		if(key < FieldType.values().length) {
			FieldType type = FieldType.values()[key];
			if(type == FieldType.Empty) {
				for(int l = mapView.length - 1; l >= 0; l--) {
					if(mapView[l][targeti][targetj].getType() != FieldType.Empty) {
						mapView[l][targeti][targetj].setType(type);
						break;
					}
				}
			} else if(type.isReachable()) {
				mapView[1][targeti][targetj].setType(FieldType.values()[key]);
			} else {
				mapView[0][targeti][targetj].setType(FieldType.values()[key]);						
			}
		}
	}
	
	public boolean getEditable() {
		return editable;
	}

	public void spawnThings() {
		if(r.nextInt(1000) < 1) {
			spawnPistol(1);
		}
		if(r.nextInt(2000) < 1) {
			spawnRifle(1);
		}
		if(r.nextInt(2000) < 1) {
			spawnHeal(1);
		}
		if(r.nextInt(2000) < 5) {
			spawnGrenade(1);
		}
		
	}

	
}
