package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import loader.ImageLoader;
import main.Window;

public class FieldDrawer {
	
	private int waterFrame = 0, maxWaterFrame = 24, maxwaterskip = 30, waterskip = maxwaterskip;
	private int waterSize = 64;
	private BufferedImage soilImage = ImageLoader.loadImage("/soil.jpg");
	private BufferedImage grassImage = ImageLoader.loadImage("/grass.jpg");
	private BufferedImage rockImage = ImageLoader.loadImage("/rock.jpg");
	private BufferedImage woodImage = ImageLoader.loadImage("/wood.jpg");
	private BufferedImage chestImage = ImageLoader.loadImage("/chest.png");
	private BufferedImage floorImage = ImageLoader.loadImage("/floor.jpg");
	private BufferedImage wallImage = ImageLoader.loadImage("/wall.jpg");
	private BufferedImage water = ImageLoader.loadImage("/watersprite.jpg");;
	private BufferedImage[] waterImage = new BufferedImage[maxWaterFrame+1];
	private BufferedImage getPistolImage = ImageLoader.loadImage("/getpistol.png");
	private BufferedImage getRifleImage = ImageLoader.loadImage("/getrifle.png");
	private BufferedImage ammoPack = ImageLoader.loadImage("/ammopack.png");
	private BufferedImage healthPack = ImageLoader.loadImage("/healthpack.png");
	private BufferedImage grenadePack = ImageLoader.loadImage("/getgrenade.png");
	
	
	private boolean mapDebug = false;
	
	public FieldDrawer() {
	}

	public FieldDrawer(boolean mapDebug) {
		this.mapDebug = mapDebug;
		for(int i = 0; i <= maxWaterFrame; i++)
			waterImage[i] = water.getSubimage(0, i*waterSize, waterSize, waterSize);
	}
	
	public void tick() {
		if(waterskip <= 0) {
			waterskip = maxwaterskip;
			waterFrame++;
			if(waterFrame > maxWaterFrame) {
				waterFrame = 0;
			}
		} else {
			waterskip--;
		}
	}

	public void drawField(Field field, int offsetX, int offsetY, int fieldSize, Graphics g) {
		if(field == null)
			return;
		if(Window.isInWindow(field.getX() + offsetX, field.getY() + offsetY, fieldSize))
			return;
		Graphics2D g2d = (Graphics2D)g;
		
		Rectangle react = new Rectangle(field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize);
		
		if(field.getType() == FieldType.Grass) {
			g.drawImage(grassImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.Soil) {
			g.drawImage(soilImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.Rock) {
			g.drawImage(rockImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.Wood) {
			g.drawImage(woodImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.Chest) {
			g.drawImage(chestImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.Water) {
			g.drawImage(waterImage[waterFrame], field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.Floor) {
			g.drawImage(floorImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.Wall) {
			g.drawImage(wallImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.GetPistol) {
			g.drawImage(getPistolImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.GetRifle) {
			g.drawImage(getRifleImage, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.GetAmmoPack) {
			g.drawImage(ammoPack, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.GetHealthPack) {
			g.drawImage(healthPack, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getType() == FieldType.GetGrenade) {
			g.drawImage(grenadePack, field.getX() + offsetX, field.getY() + offsetY, fieldSize, fieldSize, null);
		} else if(field.getColor() != null) {
			g.setColor(field.getColor());
			g2d.fill(react);
		}
		if(mapDebug) {
			g.setColor(Color.gray);
			g2d.draw(field.getOffsetBounds(offsetX, offsetY));
		}
	}

}
