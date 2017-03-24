package me.richard12799.parkour;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ParkourGame {
	
	private Parkour plugin;
	private ParkourPlayer player;
	private ParkourMap map;
	private ParkourTimer timer;
	private UUID id;
	
	public ParkourGame(Parkour pl, ParkourPlayer p, ParkourMap m) {
		plugin=pl;
		player=p;
		map=m;
		id=p.getUUID();
	}
	
	public void start() {
		timer=new ParkourTimer(plugin);
	}
	
	public UUID getUUID() {
		return id;
	}
	
	public ParkourMap getMap() {
		return map;
	}
	
	public ParkourTimer getTimer() {
		return timer;
	}
	
	public void finish() {
		double win=map.getPrize();
		player.addMoney(win);
		double time=timer.getCounter();
		time=Math.round(time*10.0)/10.0;
		Time newTime=new Time(id,time);
		List<Time> list=map.times;
		int place=-1;
		boolean foundSpot=false;
		boolean slowTime=false;
		double difference=0;
		for(Time t: list) {
			if(t.getUUID().equals(id)) {
				if(t.getTime()>time) {
					t.setTime(time);
					foundSpot=true;
					break;
				} else {
					slowTime=true;
					difference=Math.abs(t.getTime()-time);
					difference=Math.round(difference*10.0)/10.0;
					break;
				}
			}
		}
		
		if(slowTime) {
			Bukkit.getPlayer(player.getUUID()).sendMessage("§6You have successfully completed this parkour. Unfortunately your time was "+time+" seconds. "+difference+" seconds off of your record.");
			Player p=Bukkit.getPlayer(player.getUUID());
			p.teleport(p.getWorld().getSpawnLocation());
			return;
		}
		
		if(!foundSpot) list.add(newTime);
		Collections.sort(list);
		place=1;
		for(Time t: list) {
			if(t.getUUID().equals(id)) break;
			place++;
		}
		
		String pl=place+"";
		if(place==1) pl+="st";
		else if(place==2) pl+="nd";
		else if(place==3) pl+="rd";
		else pl+="th";
		Player p=Bukkit.getPlayer(player.getUUID());
		p.sendMessage("§6You have successfully completed this parkour. You are ranked "+pl+" for this parkour with a time of "+time+" seconds.");
		p.teleport(p.getWorld().getSpawnLocation());
	}
}
