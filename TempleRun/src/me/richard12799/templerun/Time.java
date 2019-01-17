package me.richard12799.templerun;

import java.util.UUID;

public class Time implements Comparable<Time>{
	
	UUID uuid;
	double time;
	
	public Time(UUID id, double t) {
		uuid=id;
		time=t;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public double getTime() {
		return time;
	}
	
	public void setTime(double d) {
		time=d;
	}

	@Override
	public int compareTo(Time o) {
		if(this.time<o.time) return -1;
		else return 1;
	}
}
