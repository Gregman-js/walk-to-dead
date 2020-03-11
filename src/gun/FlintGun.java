package gun;

import loader.ImageLoader;
import main.Handler;

public class FlintGun extends Gun {
	
	protected float scale = 0.5f;
	private static int loadTime = 40;
	private static int reloadTime = 20;
	protected static int tickX = 18, tickY = -28;
	protected static int gunX = 8, gunY = -23;
	protected static float decreaseHeal = 10;
	protected static final int ammo = 2;
	
	public FlintGun(Handler handler) {
		super(handler, ImageLoader.loadImage("/pistol.png"), ImageLoader.loadImage("/flintside.png"), loadTime, reloadTime, tickX, tickY, gunX, gunY, decreaseHeal, ammo);
	}


}
