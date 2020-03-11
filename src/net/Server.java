package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import gun.Bullet;
import gun.Grenade;
import gun.PistolGun;
import gun.RifleGun;
import main.Game;
import map.FieldType;

public class Server implements Runnable {
	
	private Thread thread;
	private Game game;
	public boolean running = false;
	private DatagramSocket socket;
	private List<User> users = new ArrayList<User>();
	private boolean debug;
	public String splitter = "#";
	public String datasplitter = "^";
	
	public Server(Game game, boolean debug) {
		this.game = game;
		this.debug = debug;
	}
	
	public synchronized void start() {
		running = true;
		game.getMenu().getButtonByName("Play Singleplayer").setTitle("Start game");
		try {
			socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		thread = new Thread(this);
		thread.start();
		if(debug)
			System.out.println("Server started");
	}
	
	public synchronized void stop() {
		try {
			game.getMenu().getButtonByName("Start game").setTitle("Play Singleplayer");
			disconnectAll();
			running = false;
			socket.close();
			thread.join();
			if(debug)
				System.out.println("Server stopped");
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
				System.out.print("Server socket stopped..., ");
			}
			String message = (new String(packet.getData())).trim();
			if(message == null || !message.contains(splitter))
				continue;
			String[] mess = message.split("\\" + splitter);
			PacketType type = PacketType.findByStr(mess[0]);
			String packetuuid = "";
			packetuuid = mess[1];
			if(mess.length > 2)
				message = mess[mess.length-1];
			if(running) {
				switch(type) {
				case ResolveServer:
					sendData(pack(PacketType.ResolveServer, message.split("\\" + datasplitter)[0]), packet.getAddress(), packet.getPort());
					break;
				
				case Connect:
					User user = new User(this, packet.getAddress(), packet.getPort(), message);
					users.add(user);
					user.send(PacketType.Connected);
					break;
					
				case Disconnect:
					for(int i = 0; i < users.size(); i++) {
						if(users.get(i).getUuid().equals(packetuuid)) {
							sendToAll(PacketType.Disconnected);
							users.remove(users.get(i));
						}
					}
					break;
					
				case SetMyPos:
					String[] pmpos = message.split("\\" + datasplitter);
					game.setServerPlayersMove(pmpos);
					break;
					
				case NewBullet:
					String[] nbpos = message.split("\\" + datasplitter);
					game.getHandler().bullets.add(new Bullet(game.getHandler(), Float.parseFloat(nbpos[0]), Float.parseFloat(nbpos[1]), Float.parseFloat(nbpos[2]), Float.parseFloat(nbpos[3])));
					sendToAll(PacketType.NewBullet, nbpos[0] + datasplitter + nbpos[1] + datasplitter + nbpos[2] + datasplitter + nbpos[3], packetuuid);
					break;
					
				case NewGrenade:
					String[] ngpos = message.split("\\" + datasplitter);
					game.getHandler().grenades.add(new Grenade(game.getHandler(), Float.parseFloat(ngpos[0]), Float.parseFloat(ngpos[1]), Float.parseFloat(ngpos[2])));
					sendToAll(PacketType.NewGrenade, ngpos[0] + datasplitter + ngpos[1] + datasplitter + ngpos[2], packetuuid);
					break;
					
				case PlayerHeal:
					String phheal = message.split("\\" + datasplitter)[0];
					for(User usr : users) {
						if(usr.getUuid().equals(packetuuid)) {
							usr.getPlayermp().setHeal(Float.parseFloat(phheal));
							sendToAll(PacketType.PlayerHeal, phheal, usr);
						}
					}
					break;
					
				case ClearStuff:
					String[] csheal = message.split("\\" + datasplitter);
						game.getHandler().getMap().getMapView()[Integer.parseInt(csheal[0])][Integer.parseInt(csheal[1])][Integer.parseInt(csheal[2])].setType(FieldType.Empty);
					if(csheal.length > 3) {
						for(User usr : users) {
							if(usr.getUuid().equals(csheal[3])) {
								if(FieldType.valueOf(csheal[4]) == FieldType.GetPistol)
									usr.getPlayermp().setGun(new PistolGun(game.getHandler()));
								else if(FieldType.valueOf(csheal[4]) == FieldType.GetRifle)
									usr.getPlayermp().setGun(new RifleGun(game.getHandler()));
							}
						}
					}
					sendToAll(PacketType.ClearStuff, message, packetuuid);
					break;
					
				case RestartMe:
					game.restartPlayer(packetuuid);
					break;
					
				default:
					System.out.print("Client said > " + message);
					System.out.println(", ip: " + packet.getAddress() + ", port: " + packet.getPort());
					sendData("jak tam", packet.getAddress(), packet.getPort());
					break;
				}
			}
		}
	}
	
	public void sendToUser(User user, String str) {
		sendData(str, user.getAddress(), user.getPort());
	}
	
	public void sendToAll(PacketType type) {
		sendToAll(type, "");
	}
	
	public void sendToAll(PacketType type, String str) {
		for(User user : users) {
			user.send(type, str);
		}
	}
	
	public void sendToAll(PacketType type, String str, User ignoreUser) {
		for(User user : users) {
//			System.out.println(ignoreUser.getUuid() + " - " + user.getUuid() + " - " + ignoreUser.getUuid().equals(user.getUuid()));
			if(!ignoreUser.getUuid().equals(user.getUuid())) {
				user.send(type, str);
			}
		}
	}
	
	public void sendToAll(PacketType type, String str, String ignoreuuid) {
		for(User user : users) {
//			System.out.println(ignoreUser.getUuid() + " - " + user.getUuid() + " - " + ignoreUser.getUuid().equals(user.getUuid()));
			if(!user.getUuid().equals(ignoreuuid)) {
				user.send(type, str);
			}
		}
	}
	
	public void sendData(String str, InetAddress ipAddress, int port) {
		if(debug)
			System.out.println(str);
		byte[] data = str.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String pack(PacketType connect, String data) {
		return connect.getStr() + splitter + data;
	}
	
	public String pack(PacketType connect) {
		return connect.getStr() + splitter;
	}

	public List<User> getUsers() {
		return users;
	}

	public void disconnectAll() {
		for(int i = 0; i < users.size(); i++) {
			users.get(i).send(PacketType.Disconnected);
		}
		users.clear();
	}
}
