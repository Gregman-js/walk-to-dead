package net;

import java.net.InetAddress;

import gameObject.PlayerMp;
import main.Game;
import main.Handler;

public class User {

	private InetAddress address;
	private int port;
	private String username;
	private String uuid;
	private Server server;
	private PlayerMp playermp;

	public User(Server server, InetAddress address, int port, String username) {
		this.server = server;
		this.setAddress(address);
		this.setPort(port);
		this.setUsername(username);
		this.uuid = Game.generateUuid();
	}
	
	public void setPlayerMp(Handler handler, float posX, float posY) {
		this.playermp = new PlayerMp(handler, posX, posY, uuid, username);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		if(this.playermp != null)
			this.playermp.setUsername(username);
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void send(PacketType type, String str) {
		server.sendToUser(this, type.getStr() + server.splitter + uuid + (str.isEmpty() ? "" : server.splitter + str));
	}
	
	public void send(PacketType type) {
		send(type, "");
	}

	public PlayerMp getPlayermp() {
		return playermp;
	}
	
	public String getSerialInfo() {
		String info = uuid + server.datasplitter + username;
		info += server.datasplitter + playermp.getPosX() + server.datasplitter + playermp.getPosY() + server.datasplitter + playermp.getAngle();
		return info;
	}

}
