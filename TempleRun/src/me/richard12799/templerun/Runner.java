package me.richard12799.templerun;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Runner {
	
	private Arena arena;
	private UUID id;
	private int lastCheckpoint;
	private ItemStack[] items;
	private ItemStack[] armor;
	private TempleTimer timer;
	public String prefix="§a§lTotal. §8§l> §e";
	private TempleRun plugin;
	
	public Runner(TempleRun pl,UUID id, Arena a) {
		plugin=pl;
		this.id=id;
		arena=a;
	}
	
	public Arena getArena() {
		return arena;
	}
		
	public UUID getUUID() {
		return id;
	}
	
	public int getLastCheckpoint() {
		return lastCheckpoint;
	}
	
	public void setCheckpoint(int i) {
		lastCheckpoint=i;
	}
	
	public void increaseCheckpoint() {
		lastCheckpoint++;
	}
	
	public void setItems(ItemStack[] i) {
		items=i;
	}
	
	public void setArmor(ItemStack[] i) {
		armor=i;
	}
	
	public ItemStack[] getItems() {
		return items;
	}
	
	public ItemStack[] getArmor() {
		return armor;
	}
	
	public void setArena(Arena a) {
		arena=a;
	}
	
	public void start() {
		timer=new TempleTimer(plugin);
	}
	
	public TempleTimer getTimer() {
		return timer;
	}
	
	public void finish() {
		double time=timer.getCounter();
		timer.cancel();
		time=Math.round(time*10.0)/10.0;
		Time newTime=new Time(id,time);
		List<Time> list=arena.times;
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
			Bukkit.getPlayer(id).sendMessage(prefix+"Unfortunately your time was "+time+" seconds. "+difference+" seconds off of your record.");
			Player p=Bukkit.getPlayer(id);
			//p.teleport(p.getWorld().getSpawnLocation());
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
		Player p=Bukkit.getPlayer(id);
		p.sendMessage(prefix+"You are ranked "+pl+" for this Map with a time of "+time+" seconds.");
		//p.teleport(p.getWorld().getSpawnLocation());
	}
}
