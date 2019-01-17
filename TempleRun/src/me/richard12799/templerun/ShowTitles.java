package me.richard12799.templerun;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShowTitles extends BukkitRunnable {
	
	private TempleRun plugin;
	private UUID id;
	private Player player;
	private int counter;
	private NoMove nm;
	
	public ShowTitles(NoMove n,TempleRun pl, UUID uuid) {
		plugin=pl;
		nm=n;
		id=uuid;
		player=Bukkit.getPlayer(id);
		counter=10;
		this.runTaskTimer(plugin, 0L, 20L);
	}

	@Override
	public void run() {
		if(counter==0) {this.cancel();nm.remove(id);Bukkit.getPlayer(id).teleport(plugin.getRunnerFromUUID(id).getArena().getStart());return;}
		player.sendTitle("§b"+counter, null);
		counter--;
	}
}
