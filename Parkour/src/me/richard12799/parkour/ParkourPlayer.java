package me.richard12799.parkour;

import java.util.UUID;

import org.bukkit.Bukkit;

public class ParkourPlayer {
	
	private Parkour plugin;
	 
	private UUID uuid;
	private ParkourGUI gui;
	private double money;
	private boolean isInGame;
	private ParkourGame game;
	
	public ParkourPlayer(Parkour pl, UUID id, double m) {
		plugin=pl;
		uuid=id;
		money=m;
		gui=new ParkourGUI(plugin,this);
		isInGame=false;
		game=null;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public double getMoney() {
		return money;
	}
	
	public void setMoney(int d) {
		money=d;
	}
	
	public void addMoney(double add) {
		money+=add;
	}
	
	public boolean hasMoney(double rem) {
		return (money-rem>=0.0);
	}
	
	public boolean takeMoney(double rem) {
		if(money-rem<0) return false;
		money-=rem;
		return true;
	}
	
	public ParkourGUI getGUI() {
		return gui;
	}
	
	public void openGUI() {
		gui.openInventory(Bukkit.getPlayer(getUUID()));
	}
	
	public void addMap(ParkourMap map) {
		gui.addMap(map);
	}
	
	public void removeMap(ParkourMap map) {
		gui.removeMap(map);
	}
		
	public boolean isInGame() {
		return isInGame;
	}
	
	public void setInGame(boolean b) {
		isInGame=b;
	}
	
	public void setCurrentGame(ParkourGame map) {
		game=map;
	}
	
	public ParkourGame getCurrentGame() {
		return game;
	}
}
