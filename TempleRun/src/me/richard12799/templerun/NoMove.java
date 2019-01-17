package me.richard12799.templerun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NoMove extends BukkitRunnable {

	public List<UUID> noMove;
	private TempleRun plugin;
	public Map<UUID, ShowTitles> map;
	
	public NoMove(TempleRun pl) {
		plugin=pl;
		noMove=new ArrayList<UUID>();
		map=new HashMap<UUID, ShowTitles>();
		this.runTaskTimer(plugin, 0L, 10L);
	}

	@Override
	public void run() {
		
		for(UUID id: noMove) {
			if(!map.containsKey(id)) {
				map.put(id, new ShowTitles(this,plugin,id));
			}
		}
		
		for(Runner r: plugin.getRunners()) {
			if(!noMove.contains(r.getUUID())) {
				noMove.add(r.getUUID());
			}
		}
	}
	
	public void remove(UUID id) {
		if(map.containsKey(id)){
			map.remove(id);
		}
	}
}
