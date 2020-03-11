package loader;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageLoader
{
	public BufferedImage enemyImage;
	public BufferedImage enemyDieImage;
	public BufferedImage bulletDieImage;
	public BufferedImage grenadeDieImage;
	public BufferedImage bulletImage;
	public BufferedImage grenadeImage;
	public BufferedImage healthPackSide;
	public BufferedImage grenadeSide;
	
	public ImageLoader() {
		enemyImage = ImageLoader.loadImage("/enemy.png");
		enemyDieImage = ImageLoader.loadImage("/enemydie.png");
		bulletDieImage = ImageLoader.loadImage("/bulletdie.png");
		grenadeDieImage = ImageLoader.loadImage("/grenadeboom.png");
		bulletImage = ImageLoader.loadImage("/bullet.png");
		grenadeImage = ImageLoader.loadImage("/grenade2.png");
		healthPackSide = ImageLoader.loadImage("/healthpackside.png");
		grenadeSide = ImageLoader.loadImage("/grenadeside.png");
	}

    public static BufferedImage loadImage(String fileName)
    {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(ImageLoader.class.getResource(fileName));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Image could not be read");
            System.exit(1);
        }

        return bi;
    }
}
