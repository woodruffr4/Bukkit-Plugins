package me.richard12799.templerun;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Arena {
	
	private String name;
	private Location start;
	private Location end;
	private List<Location> checkpoints;
	private boolean enabled;
	private double prize;
	public List<Time> times;
	private Scoreboard sb;
	
	public Arena(String n) {
		name=n;
		checkpoints=new ArrayList<Location>();
		enabled=false;
		times=new ArrayList<Time>();
	}
	
	public Scoreboard getScoreboard() {
		sb=Bukkit.getScoreboardManager().getNewScoreboard();
		int count=1;
		int countDown=10;
		Objective o=sb.registerNewObjective("leaders", "dummy");
		o.setDisplayName("§6§lLeaderboards");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		String pre="§8§l> ";
		Score l=o.getScore(pre+" §a");
		l.setScore(countDown);
		countDown--;
		for(Time t: times) {
			if(count==4) break;
			if(count==1) {
				Score s1=o.getScore(pre+"§a§l1st");
				s1.setScore(countDown);
				
				countDown--;
				
				Score s=o.getScore(pre+"§a"+t.getTime()+"§a - "+Bukkit.getOfflinePlayer(t.getUUID()).getName());
				s.setScore(countDown);
				
				countDown--;
				
				Score s2=o.getScore(pre+" §b");
				s2.setScore(countDown);
				
				countDown--;
			}
			else if(count==2) {
				Score s1=o.getScore(pre+"§6§l2nd");
				s1.setScore(countDown);
				
				countDown--;
				
				Score s=o.getScore(pre+"§6"+t.getTime()+"§6 - "+Bukkit.getOfflinePlayer(t.getUUID()).getName());
				s.setScore(countDown);
				
				countDown--;
				
				Score s2=o.getScore(pre+" §c");
				s2.setScore(countDown);
				
				countDown--;
			}
			else if(count==3) {
				Score s1=o.getScore(pre+"§c§l3rd");
				s1.setScore(countDown);
				
				countDown--;
				
				Score s=o.getScore(pre+"§c"+t.getTime()+"§c - "+Bukkit.getOfflinePlayer(t.getUUID()).getName());
				s.setScore(countDown);
				
				countDown--;
				
				Score s2=o.getScore(pre+" §d");
				s2.setScore(countDown);
				
				countDown--;
			}
			count++;
		}
		
//		Stack<Time> rev=new Stack<Time>();
//		for(Time t: times) {
//			rev.add(t);
//		}
//		int counter=rev.size();
//		for(Time t: rev) {
//			if(counter==0) break;
//			String pre="";
//			if(counter==1) pre="§a§l1st §a"+Bukkit.getOfflinePlayer(t.getUUID()).getName();
//			else if(counter==2) pre="§6§l2nd §6"+Bukkit.getOfflinePlayer(t.getUUID()).getName();
//			else if(counter==3) pre="§c§l3rd §c"+Bukkit.getOfflinePlayer(t.getUUID()).getName();
//			else pre="§e§l"+counter+"th §e"+Bukkit.getOfflinePlayer(t.getUUID()).getName();
//			Score s=o.getScore(pre);
//			s.setScore((int)t.getTime());
//			counter--;
//		}
		
		
		
		return sb;
	}
	
	public void addTime(Time t) {
		times.add(t);
	}
	
	public String getName() {
		return name;
	}
	
	public void setStart(Location l) {
		start=l;
	}
	
	public void setEnabled(boolean b) {
		enabled=b;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public Location getStart() {
		return start;
	}
	
	public void setFinish(Location l) {
		end=l;
	}
	
	public Location getFinish() {
		return end;
	}
	
	public void addCheckpoint(Location l) {
		checkpoints.add(l);
	}
	
	public boolean removeLastCheckpoint() {
		if(checkpoints.size()>0) {
			checkpoints.remove(checkpoints.size()-1);
			return true;
		}
		return false;
	}
	
	public List<Location> getCheckpoints() {
		return checkpoints;
	}
	
	public Location getNthCheckpoint(int k) {
		if(checkpoints.size()>=k) {
			return checkpoints.get(k-1);
		}
		return null;
	}
	
	public boolean setCheckpoint(int k, Location l) {
		if(getNthCheckpoint(k)!=null) {
			checkpoints.set(k-1, l);
			return true;
		}
		return false;
	}
	
	public boolean removeCheckpoint(int k) {
		if(getNthCheckpoint(k)!=null) {
			checkpoints.remove(k-1);
			return true;
		}
		return false;
	}
	
	public void setPrize(double d) {
		prize=d;
	}
	
	public double getPrize() {
		return prize;
	}
	
}
