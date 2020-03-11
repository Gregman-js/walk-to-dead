package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import gameObject.Enemy;
import gameObject.PlayerMp;
import gun.Bullet;
import gun.Grenade;
import gun.PistolGun;
import gun.RifleGun;
import main.Game;
import map.FieldType;

public class Client implements Runnable {
	
	private Game game;
	private Thread thread;
	private InetAddress ipAddress;
	private DatagramSocket socket;
	public boolean running = false;
	public boolean connected = false;
	public String uuid = "";
	private boolean debug;
	private String splitter = "#";
	private String datasplitter = "^";
	private List<PlayerMp> playersMp = new ArrayList<PlayerMp>();
	private boolean resolved = false;
	
	public Client(Game game, boolean debug) {
		this.game = game;
		this.debug = debug;
	}
	
	public synchronized void start() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		thread = new Thread(this);
		running = true;
		thread.start();
		if(debug)
			System.out.println("Client started");
	}
	
	public synchronized void stop() {
		try {
			running = false;
			socket.close();
			thread.join();
			if(debug)
				System.out.println("Client stopped");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(running) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				if(debug)
					System.out.print("Client socket stopped..., ");
			}
			if(running) {
				String message = (new String(packet.getData())).trim();
				if(message == null || !message.contains(splitter))
					continue;
				String[] mess = message.split("\\" + splitter);
				PacketType type = PacketType.findByStr(mess[0]);
				String packetuuid = "";
				packetuuid = mess[1];
				if(mess.length > 2)
					message = mess[2];
				if(type == PacketType.Connected || type == PacketType.ResolveServer || packetuuid.equals(uuid)) {
					switch(type) {
					case ResolveServer:
						try {
							if(debug)
								System.out.println("Server under: " + message.split("\\" + splitter)[1]);
							ipAddress = InetAddress.getByName(message.split("\\" + splitter)[1]);
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						resolved = true;
						break;
					case Connected:
						connected = true;
						uuid = packetuuid;
						break;
						
					case Disconnected:
						if(uuid.equals(packetuuid)) {
							game.getMenu().getButtonByName("Disconnect").setTitle("Connect as client");
							game.setClientToStop();
						}
						
						break;
						
					case SetPPos:
						String[] setppos = message.split("\\" + datasplitter);
						float setpposX = Float.parseFloat(setppos[0]);
						float setpposY = Float.parseFloat(setppos[1]);
						game.setClientPlayerPos(packetuuid, setpposX, setpposY);
						break;
						
					case PlayerMove:
						String[] pmpos = message.split("\\" + datasplitter);
						game.setClientPlayersMove(pmpos);
						break;
						
					case StartGame:
						game.startClientGame();
						break;
						
					case NewBullet:
						String[] nbpos = message.split("\\" + datasplitter);
						game.getHandler().bullets.add(new Bullet(game.getHandler(), Float.parseFloat(nbpos[0]), Float.parseFloat(nbpos[1]), Float.parseFloat(nbpos[2]), Float.parseFloat(nbpos[3])));
						break;
						
					case NewGrenade:
						String[] ngpos = message.split("\\" + datasplitter);
						game.getHandler().grenades.add(new Grenade(game.getHandler(), Float.parseFloat(ngpos[0]), Float.parseFloat(ngpos[1]), Float.parseFloat(ngpos[2])));
						break;
						
					case PlayerHeal:
						String[] phheal = message.split("\\" + datasplitter);
						if(uuid.equals(phheal[0])) {
							game.getHandler().getPlayer().setHeal(Float.parseFloat(phheal[2]));
						}
						for(PlayerMp pl : playersMp) {
							if(pl.getUuid().equals(phheal[0])) {
								pl.setHeal(Float.parseFloat(phheal[2]));
							}
						}
						break;
						
					case SpawnStuff:
						String[] ssheal = message.split("\\" + datasplitter);
						game.getHandler().getMap().getMapView()[1][Integer.parseInt(ssheal[1])][Integer.parseInt(ssheal[2])].setType(FieldType.valueOf(ssheal[0]));
						break;
						
					case ClearStuff:
						String[] csheal = message.split("\\" + datasplitter);
						game.getHandler().getMap().getMapView()[Integer.parseInt(csheal[0])][Integer.parseInt(csheal[1])][Integer.parseInt(csheal[2])].setType(FieldType.Empty);
						if(csheal.length > 3) {
							for(PlayerMp pl : playersMp) {
								if(pl.getUuid().equals(csheal[3])) {
									if(FieldType.valueOf(csheal[4]) == FieldType.GetPistol)
										pl.setGun(new PistolGun(game.getHandler()));
									else if(FieldType.valueOf(csheal[4]) == FieldType.GetRifle)
										pl.setGun(new RifleGun(game.getHandler()));
								}
							}
						}
						break;
						
					case EnemyStat:
						String[] esheal = message.split("\\" + datasplitter);
						boolean isen = false;
						for(Enemy pl : game.getHandler().getEnemies()) {
							if(pl.getUuid().equals(esheal[0])) {
								isen = true;
								pl.setPosX(Float.parseFloat(esheal[1]));
								pl.setPosY(Float.parseFloat(esheal[2]));
								pl.setAngle(Float.parseFloat(esheal[3]));
								pl.setHeal(Float.parseFloat(esheal[4]));
								break;
							}
						}
						if(!isen) {
							Enemy en = new Enemy(game.getHandler());
							en.setUuid(esheal[0]);
							en.setPosX(Float.parseFloat(esheal[1]));
							en.setPosY(Float.parseFloat(esheal[2]));
							en.setAngle(Float.parseFloat(esheal[3]));
							en.setHeal(Float.parseFloat(esheal[4]));
							game.getHandler().getEnemies().add(en);
						}
						break;
						
					default:
						break;
					}
					
				}
			}
		}
	}
	
	public void sendData(String str) {
		sendData(str, ipAddress);
	}
	
	public void sendData(String str, InetAddress ipA) {
		if(debug)
			System.out.println(str);
		byte[] data = str.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, ipA, 1331);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resolveServer() {
		resolveServer(1);
	}
	private void resolveServer(int x) {
		if(!resolved && x < 255) {
			InetAddress ipAddressloc = null;
			try {
				ipAddressloc = InetAddress.getByName("192.168.1."+x);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendData(pack(PacketType.ResolveServer, "192.168.1."+x), ipAddressloc);
			resolveServer(x+1);
		}
		
	}

	public String pack(PacketType connect, String msg) {
		return connect.getStr() + splitter + 
	(uuid == null ? "!" : uuid)  + (msg.isEmpty() ? "" : splitter + msg);
	}
	
	public String pack(PacketType connect) {
		return pack(connect, "");
	}
	
	public void disconnect() {
		if(connected)
			sendData(pack(PacketType.Disconnect));
		uuid = null;
		connected = false;
		
	}

	public List<PlayerMp> getPlayersMp() {
		return playersMp;
	}
}
