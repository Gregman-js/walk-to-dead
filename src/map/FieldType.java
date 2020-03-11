package map;

public enum FieldType {
	Empty(Group.Static),
	Soil(Group.Static),
	Water(Group.Collide),
	Grass(Group.Static),
	Wood(Group.Collide),
	Rock(Group.Static),
	Chest(Group.Collide),
	Floor(Group.Static),
	Wall(Group.Collide),
	GetPistol(Group.Reachable),
	GetRifle(Group.Reachable),
	GetAmmoPack(Group.Reachable),
	GetHealthPack(Group.Reachable),
	GetGrenade(Group.Reachable);
	
	private Group group;
	
	FieldType(Group group) {
		this.group = group;
	}
	
	
	public boolean isReachable(){
		if(this.group == Group.Reachable) {
			return true;
		} else
			return false;
	}
	
	public boolean isCollide(){
		if(this.group == Group.Collide)
			return true;
		else
			return false;
	}
	
	public enum Group {
		Static,
		Collide,
		Reachable
	}
}
