package me.richard12799.templerun;

import org.bukkit.scheduler.BukkitRunnable;

public class TempleTimer extends BukkitRunnable {
	
	TempleRun plugin;
	double counter;

	public TempleTimer(TempleRun pl) {
		plugin=pl;
		counter=0;
		startTimer();
	}
	
	public void startTimer() {
		this.runTaskTimer(plugin, 0L, 2L);
	}
	
	public double getCounter() {
		return counter;
	}
	
	public void resetCounter() {
		counter=0;
	}

	@Override
	public void run() {
		counter+=0.1;
	}
}
