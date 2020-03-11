package gameObject;

public class Backpack {
	
	private int health = 0;
	private int grenades = 0;
	
	public Backpack() {
		
	}

	public void addHealthPack() {
		health++;
	}
	
	public void removeHealthPack() {
		if(health > 0)
			health--;
	}
	
	public int getHealthPack() {
		return health;
	}
	public void addGrenades() {
		grenades++;
	}
	
	public void removeGrenades() {
		if(grenades > 0)
			grenades--;
	}
	
	public int getGrenades() {
		return grenades;
	}

}
