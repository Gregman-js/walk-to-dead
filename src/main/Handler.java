package main;

import java.awt.Graphics;
import java.util.LinkedList;

import gameObject.Enemy;
import gameObject.Player;
import gameObject.PlayerMp;
import gun.Bullet;
import gun.Grenade;
import loader.ImageLoader;
import map.Map;
import net.User;

public class Handler {
	
	private Player player;
	private Map map;
	private Gui gui;
	private LinkedList<Enemy> enemies = new LinkedList<Enemy>();
	public LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	public LinkedList<Grenade> grenades = new LinkedList<Grenade>();
	private int enemgen = 2;
	private float grengentime = 700, togrengen = grengentime;
	private float healgentime = 3000, tohealgen = healgentime;
	private boolean spawnEnemies = true;
	private Game game;
	
	public Handler(Game game) {
		this.game = game;
		gui = new Gui(this);
		map = new Map(this);
		player = new Player(this, Game.generateUuid(), "");
	}
	
	private void addEnemies(int pula) {
		for(int i = 0; i < pula; i++) {
			Enemy en = new Enemy(this);
			enemies.add(en);
			en.sendPosToAll();
			
		}
	}
	
	public void tick() {
		map.tick();
		player.tick();
		if(game.getServer().running)
			for(User user : game.getServer().getUsers()) {
				if(user.getPlayermp().getHeal() > 0)
					user.getPlayermp().tick();
			}
		else if(game.getClient().running)
			for(PlayerMp playermp : game.getClient().getPlayersMp()) {
				if(playermp.getHeal() > 0)
					playermp.tick();
			}
		
		for(int i = 0; i < grenades.size(); i++) {
			Grenade grenade = grenades.get(i);
			grenade.tick();
		}
		for(int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			if(bullet.tick())
				bullet.die();
		}
		if(!map.getEditable() && spawnEnemies) {
			for(Enemy enemy : new LinkedList<Enemy>(enemies)) {
					enemy.tick();
			}
			if(!game.getClient().running)
				enemyGen();
		}
		if(!map.getEditable() && !game.getClient().running) {
			grenadeGen();
			healGen();
		}
		gui.tick();
	}
	
	private void enemyGen() {
//		if(toenemgen <= 0) {
//			enemies.add(new Enemy(this));
//			enemgentime-= 25;
//			if(enemgentime < 50)
//				enemgentime = 50;
//			toenemgen = enemgentime;
//		} else {
//			toenemgen--;
//		}
		
		
//		int killed = player.getKill();
//		int level = Game.getLevel();
//		int reqkill = 0;
//		for (int x = 1; x <= level; x++) 
//			reqkill += x;
//		if(killed >= reqkill * enemgen) {
//			Game.addLevel();
//			gui.addLevel();
//			addEnemies(Game.getLevel()*enemgen);
//		}
		
		int ile = enemies.size();
		if(ile <= 0) {
			Game.addLevel();
			addEnemies(Game.getLevel()*enemgen);
		}
	}
	
	private void grenadeGen() {
		if(togrengen <= 0) {
			map.spawnGrenade(1);
			togrengen = grengentime;
		} else {
			togrengen--;
		}
	}
	private void healGen() {
		if(tohealgen <= 0) {
			map.spawnHeal(1);
			tohealgen = healgentime;
		} else {
			tohealgen--;
		}
	}

	public void render(Graphics g) {
		map.render(g);
		renderGrenades(g, player.getCenterX() + map.getOfsmap().x - player.getPosX(), player.getCenterY()  + map.getOfsmap().y - player.getPosY());
		renderBullets(g, player.getCenterX() + map.getOfsmap().x - player.getPosX(), player.getCenterY()  + map.getOfsmap().y - player.getPosY());
		player.render(g);
		if(game.getServer().running)
			for(User user : game.getServer().getUsers()) {
				if(user.getPlayermp().getHeal() > 0)
					user.getPlayermp().render(g);
			}
		else if(game.getClient().running)
			for(PlayerMp playermp : game.getClient().getPlayersMp()) {
				if(playermp.getHeal() > 0)
					playermp.render(g);
			}
		for(Enemy enemy : enemies) {
			enemy.render(g);
		}
		map.renderSecLayer(g);
		gui.render(g);
	}
	
	public Player getPlayer() {
		return player;
	}
	public Map getMap() {
		return map;
	}
	
	public Gui getGui() {
		return gui;
	}
	
	public LinkedList<Enemy> getEnemies() {
		return enemies;
	}
	
	public int getEnemiesSize() {
		LinkedList<Enemy> sizeing = new LinkedList<Enemy>(enemies);
		sizeing.removeIf(e -> e.getDie() == true);
		return sizeing.size();
	}
	
	public void removeEnemy(Enemy enemy) {
		enemies.remove(enemy);
	}
	
	public void renderBullets(Graphics g, float posX,float posY) {
		for(int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			
			bullet.render(g, posX, posY);
		}
	}
	
	public void renderGrenades(Graphics g, float posX,float posY) {
		for(int i = 0; i < grenades.size(); i++) {
			Grenade grenade = grenades.get(i);
			
			grenade.render(g, posX, posY);
		}
	}
	
	public void removeBullet(Bullet bullet) {
		bullets.remove(bullet);
	}
	public void removeGrenade(Grenade grenade) {
		grenades.remove(grenade);
	}

	public ImageLoader getImageLoader() {
		return getGame().getImageLoader();
	}

	public Game getGame() {
		return game;
	}

}
