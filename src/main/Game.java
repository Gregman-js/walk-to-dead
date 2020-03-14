package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.swing.JOptionPane;

import gameObject.PlayerMp;
import listener.KeyInput;
import listener.Mouse;
import listener.MouseMotion;
import loader.HighScoreLoader;
import loader.ImageLoader;
import loader.MapLoader;
import loader.Score;
import map.Map;
import menu.MyMenu;
import net.Client;
import net.PacketType;
import net.Server;
import net.User;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -6680875776510531796L;

	public static final int WIDTH = 1200, HEIGHT = WIDTH / 16 * 9;
	
	private static int fps;
	
	private Thread thread;
	private boolean running = false;
	
	private static Handler handler;
	private MyMenu menu;
	private ImageLoader imageLoader;
	private static int level = 0;
	private static Score highScore;
	
	private Server server;
	
	private Client client;
	private boolean serverToStop = false;
	
	private String userName;
	
	public static GameState state = GameState.Menu;
	
	public Game() {
//		highScore = HighScoreLoader.readFromFile();
		highScore = new Score("user", 0, 0);
//		System.exit(1);
		server = new Server(this, false);
		client = new Client(this, false);
		imageLoader = new ImageLoader();
		new Window(WIDTH, HEIGHT, "Walk to Dead", this);
		handler = new Handler(this);
		menu = new MyMenu(this);
		this.addKeyListener(new KeyInput(this));
		this.addMouseListener(new Mouse(this));
		this.addMouseMotionListener(new MouseMotion(this));
		start();
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
		this.requestFocus();
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running)
				render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				fps = frames;
				frames = 0;
			}
		}
		stop();
	}
	
	private void tick() {
		if(state == GameState.Game) {
			handler.tick();
		}
		else if(state == GameState.Menu)
		{
			if(serverToStop)
				setClientEnabled(false);
			serverToStop = false;
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, Window.getWindowSize().width, Window.getWindowSize().height);
		
		if(state == GameState.Menu) {
			getMenu().render(g);
		} else if(state == GameState.Game || state == GameState.Pause || state == GameState.GameOver) {
			handler.render(g);
		}
		if(state == GameState.Pause)
			menu.renderPause(g);
		else if(state == GameState.GameOver)
			menu.renderGameOver(g);
		Toolkit.getDefaultToolkit().sync();
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new Game();
	}

	public static int getFPS() {
		return fps;
	}

	public MyMenu getMenu() {
		return menu;
	}

	public static int getLevel() {
		return level;
	}

	public static void addLevel() {
		level++;
		handler.getGui().addLevel();
	}
	
	public void restartGame() {
		handler = new Handler(this);
		state = GameState.Game;
	}
	public void restartGame(GameState type) {
		state = type;
		handler = new Handler(this);
	}
	public void restartClientGame() {
		handler.getPlayer().clientRestartMe();
		state = GameState.Game;
	}

	public void restartServerGame() {
		handler.getPlayer().serverRestartMe();
		state = GameState.Game;
	}

	public Handler getHandler() {
		return handler;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	public void exit() {
		readyToClose();
		System.exit(1);
	}
	
	public void readyToClose() {
		MapLoader.saveMapToFile(handler.getMap().getMapView());
		HighScoreLoader.saveScoreToFile(userName, level, handler.getPlayer().getKill());
		disconnectAll();
	}

	public static Score getHighScore() {
		return highScore;
	}

	public void saveScore() {
		highScore = new Score(userName, level > highScore.level ? level : highScore.level,
				handler.getPlayer().getKill() > highScore.killed ? handler.getPlayer().getKill() : highScore.killed);
		HighScoreLoader.saveScoreToFile(userName, level, handler.getPlayer().getKill());
	}

	public void setServerEnabled(boolean seren) {
		if(seren && !server.running) {
			if(client.running) {
				client.stop();
			}
			if(userName == null || userName.isEmpty())
				setUserName(JOptionPane.showInputDialog(Window.frame, "Enter your username?", highScore.username));
			if(userName != null && !userName.isEmpty())
				server.start();
		} else if(!seren && server.running) {
			server.stop();
		}
		
	}

	public Server getServer() {
		return server;
	}

	public void setClientEnabled(boolean clientEnabled) {
		if(!clientEnabled && client.running) {
			client.disconnect();
			client.stop();
		} else if(clientEnabled) {
			if(server.running) {
				server.stop();
			}
			if(!client.running) {
				if(userName == null || userName.isEmpty())
					setUserName(JOptionPane.showInputDialog(Window.frame, "Enter your username?", highScore.username));
				if(userName != null && !userName.isEmpty()) {
					client.start();
				}
				if(client.running) {
					client.resolveServer();
					client.sendData(client.pack(PacketType.Connect, userName));
				}
			}
		}
	}

	public Client getClient() {
		return client;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		menu.username.setTitle("Username: " + userName);
		handler.getPlayer().setUsername(userName);
	}

	public void disconnectAll() {
		if(client.running) {
			client.disconnect();
			client.stop();
		}
		if(server.running) {
			server.stop();
		}
	}

	public void setClientToStop() {
		serverToStop = true;
	}
	
	private boolean isGoodSpawnPlace(int x, int y) {
		if(!handler.getMap().getMapView()[0][x][y].getType().isCollide())
			return true;
		else
			return false;
	}
	
	public void startMultiServer() {
		Random r = new Random();
		List<User> users = server.getUsers();
		int min = handler.getMap().getMapView()[0][0][0].getX();
		int x = r.nextInt(Map.fieldNum);
		int y = r.nextInt(Map.fieldNum);
		for(User user : users) {
			do {
				x = r.nextInt(Map.fieldNum);
				y = r.nextInt(Map.fieldNum);
			} while(!isGoodSpawnPlace(x,y));
//			System.out.println(x + " - " + y);
			float posX = min + x*Map.fieldSize;
			float posY = min + y*Map.fieldSize;
			user.setPlayerMp(handler, posX, posY);
			user.send(PacketType.SetPPos, posX + server.datasplitter + posY);
			server.sendToAll(PacketType.PlayerMove, user.getSerialInfo(), user);
		}
		server.sendToAll(PacketType.StartGame);
		state = GameState.Game;
		
	}
	
	public void setClientPlayerPos(String packetuuid, float posX, float posY) {
		handler.getPlayer().setUuid(packetuuid);
		handler.getPlayer().setPosX(posX);
		handler.getPlayer().setPosY(posY);
		
	}

	public void startClientGame() {
		state = GameState.Game;
	}

	public void setClientPlayersMove(String[] pos) {
		String uuid = pos[0];
		String username = pos[1];
		float posX = Float.parseFloat(pos[2]);
		float posY = Float.parseFloat(pos[3]);
		float angle = Float.parseFloat(pos[4]);
		boolean ok = false;
		if(handler.getPlayer().getUuid().equals(uuid)) {
			ok = true;
			handler.getPlayer().setPosX(posX);
			handler.getPlayer().setPosY(posY);
		}
		else for(PlayerMp pl : client.getPlayersMp()) {
			if(pl.getUuid().equals(uuid)) {
				ok = true;
				pl.setUsername(username);
				pl.setPosX(posX);
				pl.setPosY(posY);
				pl.setStaticAngle(angle);
			}
		}
		if(!ok) {
			PlayerMp newp = new PlayerMp(handler, posX, posY, uuid, username);
			newp.setStaticAngle(angle);
			client.getPlayersMp().add(newp);
			
		}
	}
	
	public void setServerPlayersMove(String[] pos) {
		String uuid = pos[0];
		String username = pos[1];
		float posX = Float.parseFloat(pos[2]);
		float posY = Float.parseFloat(pos[3]);
		float angle = Float.parseFloat(pos[4]);
		for(User user : server.getUsers()) {
			if(user.getUuid().equals(uuid)) {
				user.setUsername(username);
				user.getPlayermp().setPosX(posX);
				user.getPlayermp().setPosY(posY);
				user.getPlayermp().setStaticAngle(angle);
				server.sendToAll(PacketType.PlayerMove, user.getSerialInfo(), user);
			}
		}
	}
	
	public void newBullet(float posx, float posy, float angle, float decreaseHeal) {
		if(server.running) {
			server.sendToAll(PacketType.NewBullet, posx + server.datasplitter + posy + server.datasplitter + angle + server.datasplitter + decreaseHeal);
		} else if(client.running) {
			String serinfo = client.pack(PacketType.NewBullet, posx + server.datasplitter + posy + server.datasplitter + angle + server.datasplitter + decreaseHeal);
			client.sendData(serinfo);
		}
	}
	
	public void newGrenade(float posx, float posy, float angle) {
		if(server.running) {
			server.sendToAll(PacketType.NewGrenade, posx + server.datasplitter + posy + server.datasplitter + angle);
		} else if(client.running) {
			String serinfo = client.pack(PacketType.NewGrenade, posx + server.datasplitter + posy + server.datasplitter + angle);
			client.sendData(serinfo);
		}
		
	}
	
	public static String generateUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public void restartPlayer(String packetuuid) {
		for(User user : server.getUsers()) {
			if(user.getUuid().equals(packetuuid)) {
				user.getPlayermp().setKilled(0);
				user.getPlayermp().setHeal(100);
				handler.getGame().getServer().sendToAll(PacketType.PlayerHeal, user.getPlayermp().getHealInfo(), user);
			}
		}
	}
	
	public boolean isSinglePlayer() {
		return !server.running && !client.running;
	}


}
