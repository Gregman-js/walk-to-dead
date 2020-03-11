package gun;

import loader.ImageLoader;
import main.Handler;

public class PistolGun extends Gun {
	
	protected float scale = 0.5f;
	private static int loadTime = 25;
	private static int reloadTime = 50;
	protected static int tickX = 18, tickY = -28;
	protected static int gunX = 8, gunY = -23;
	protected static float decreaseHeal = 25;
	protected static final int ammo = 9;
	
	public PistolGun(Handler handler) {
		super(handler, ImageLoader.loadImage("/pistol.png"), ImageLoader.loadImage("/pistolside.png"), loadTime, reloadTime, tickX, tickY, gunX, gunY, decreaseHeal, ammo);
	}


}
