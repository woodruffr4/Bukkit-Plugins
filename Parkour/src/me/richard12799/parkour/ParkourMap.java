package me.richard12799.parkour;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParkourMap {

	private String name;
	private Location start;
	private Location end;
	private double prizeMoney;
	private double cost;
	private boolean buycraftOnly;
	private int difficulty; // 1=easy, 2=medium, 3=hard
	private Location point1;
	private Location point2;
	public List<Time> times;
	
	public ParkourMap(String name, Location start, double prizeMoney, double cost, boolean buycraft, int diff) {
		this.times=new ArrayList<Time>();
		this.name=name;
		this.start=start;
		this.prizeMoney=prizeMoney;
		this.cost=cost;
		this.buycraftOnly=buycraft;
		this.difficulty=diff;
	}
	
	public void addTime(Time t) {
		times.add(t);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name=n;
	}
	
	public Location getStart() {
		return start;
	}
	
	public Location getFinish() {
		return end;
	}
	
	public void setStart(Location l) {
		start=l;
	}
	
	public void setFinish(Location l) {
		end=l;
	}
	
	public double getPrize() {
		return prizeMoney;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setPrize(double p) {
		prizeMoney=p;
	}
	
	public void setCost(double c) {
		cost=c;
	}
	
	public boolean isBuycraftOnly() {
		return buycraftOnly;
	}
	
	public void setBuycraftOnly(boolean b) {
		buycraftOnly=b;
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(int d) {
		difficulty=d;
	}
	
	public Location getPoint1() {
		return point1;
	}
	
	public void setPoint1(Location d) {
		point1=d;
	}
	
	public Location getPoint2() {
		return point2;
	}
	
	public void setPoint2(Location d) {
		point2=d;
	}
	
	public void sendLeaderboards(Player player) {
		int counter=1;
		for(Time t: times) {
			if(counter==11) break;
			player.sendMessage("§7"+counter+": §b"+t.getTime()+" - "+Bukkit.getPlayer(t.getUUID()).getName());
			counter++;
		}
	}
	
}
