package gun;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Handler;

public class Gun {
	
	protected Handler handler;
	protected BufferedImage gunImage;
	protected BufferedImage gunSideImage;
	protected int karabinSizeX;
	protected int karabinSizeY;
	protected float scale = 0.5f, decreaseHeal;
	protected int loadTime;
	protected int reloadTime;
	protected int reloadTimeNow;
	protected boolean fire = false;
	protected int timeToLoad = 0;
	
	protected int tickX, tickY;
	protected int gunX, gunY;
	protected int ammo;
	protected int ammoNow;
	protected boolean isreloading = false;
	
//	protected LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	
	public Gun(Handler handler, BufferedImage gunImage, BufferedImage gunSideImage, int loadTime, int reloadTime, int tickX, int tickY, int gunX, int gunY, float decreaseHeal, int ammo) {
		this.handler = handler;
		this.gunImage = gunImage;
		this.gunSideImage = gunSideImage;
		this.decreaseHeal = decreaseHeal;
		this.loadTime = loadTime;
		this.reloadTime = reloadTime;
		this.reloadTimeNow = reloadTime;
		this.gunX = gunX;
		this.gunY = gunY;
		this.tickX = tickX;
		this.tickY = tickY;
		this.ammo = ammo;
		this.ammoNow = ammo;
		karabinSizeX = gunImage.getWidth();
		karabinSizeY = gunImage.getHeight();
	}

	public void tick(float posX,float posY, int size, float angle) {
		if(timeToLoad <= 0) {
			if(fire && !isReloading()) {
				if(ammoNow > 0) {
					double odl = Math.sqrt(Math.pow(tickX, 2) + Math.pow(-tickY, 2));
					double rotationRequired = Math.toRadians (angle + 15);
					double x = odl * Math.cos(rotationRequired);
					double y = -odl * Math.sin(rotationRequired);
					handler.bullets.add(new Bullet(handler, posX + size/2 + (float)x, posY + size/2 - (float)y, angle, decreaseHeal));
					handler.getGame().newBullet(posX + size/2 + (float)x, posY + size/2 - (float)y, angle, decreaseHeal);
					timeToLoad = loadTime;
					ammoNow--;
					if(ammoNow == 0)
						reload();
				} else {
					reload();
				}
			}
		} else {
			timeToLoad--;
		}
		if(isreloading)
			reload();
	}
	
	public void reload() {
		if(ammoNow != ammo) {
			isreloading = true;
			if(reloadTimeNow > 0) {
				reloadTimeNow--;
			} else {
				reloadTimeNow = reloadTime;
				isreloading = false;
				ammoNow = ammo;
			}
		}
	}

	public void renderGun(Graphics g, int centerX, int centerY, int size) {
		g.drawImage(gunImage, (int)centerX + gunX, (int)centerY + gunY, (int)(karabinSizeX * scale), (int)(karabinSizeY * scale), null);
	}
	
	public void setFire(boolean b) {
		fire = b;
	}
	
	public int[] getAmmo() {
		return new int[]{ammoNow, ammo};
	}
	public boolean isReloading() {
		return isreloading;
	}
	public int[] getReload() {
		return new int[] {reloadTimeNow, reloadTime};
	}
	
	public BufferedImage getGunSideImage() {
		return gunSideImage;
	}


}
