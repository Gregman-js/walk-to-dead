package gun;

import loader.ImageLoader;
import main.Handler;

public class RifleGun extends Gun {
	
	protected float scale = 0.5f;
	private static int loadTime = 10;
	private static int reloadTime = 100;
	protected static int tickX = 18, tickY = -28;
	protected static int gunX = 24, gunY = -21;
	protected static float decreaseHeal = 15;
	protected static final int ammo = 25;
	
	public RifleGun(Handler handler) {
		super(handler, ImageLoader.loadImage("/karabin.png"), ImageLoader.loadImage("/rifleside.png"), loadTime, reloadTime, tickX, tickY, gunX, gunY, decreaseHeal, ammo);
	}

}
