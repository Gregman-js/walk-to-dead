package net;


public enum PacketType {
	Disconnect("000"),
	Disconnected("001"),
	Connect("002"),
	Connected("003"),
	SetPPos("004"),
	PlayerMove("005"),
	StartGame("006"),
	SetMyPos("007"),
	NewBullet("008"),
	NewGrenade("009"),
	PlayerHeal("010"),
	RestartMe("011"),
	ResolveServer("012"),
	EnemyStat("013"),
	SpawnStuff("014"),
	ClearStuff("015")
	
	
	;
	
	private String str;
	
	PacketType(String str) {
		this.str = str;
	}
	
	public static PacketType findByStr(String str) {
		for(PacketType type : PacketType.values())
			if(type.str.equalsIgnoreCase(str))
				return type;
		return null;
	}
	
	public String getStr() {
		return this.str;
	}
}
