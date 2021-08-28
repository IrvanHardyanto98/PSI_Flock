package com.skripsi.psi_flock.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
public class Trajectory{
	private HashMap<Integer,Location> locations;
	private final int entityID;//ID ENTITAS TIDAK BOLEH BERUBAH DARI AWAL SAMPAI AKHIR!, karena Id_entitas setiap 'titik' harus diubah satu-satu
	private int startTime;
	private int endTime;
	
	public Trajectory(int entityID,int startTime){
		this.entityID=entityID;
		this.startTime=startTime;
		this.endTime=0;
		this.locations=new HashMap<>();
	}
	
	public void addLocation(double x,double y,int timestamp){
		this.locations.put(timestamp,new Location(this.entityID,x,y,timestamp));
	}
	
	public int getEntityID(){
		return this.entityID;
	}
	
	public int getStartTime(){
		return this.startTime;
	}
	
	public void setEndTime(int endTime){
		this.endTime=endTime;
	}
	
	public int getEndTime(){
		return this.endTime;
	}
	
	public Location getLocation(int timestamp){
		return this.locations.get(timestamp);
	}
	
	public String iterateAllLocation(){
		String s = "Titik-titik pada lintasan adalah: \n";
		Set<Integer> keySet = this.locations.keySet();
		Iterator<Integer> iter = keySet.iterator();
		while(iter.hasNext()){
			int key = iter.next();
			Location l = this.locations.get(key);
			s+=l.toString();
		}
		return s;
	}
	
	public String toString(){
		String s="ID Benda: "+this.entityID+"\n";
		s+="Waktu mulai: "+this.startTime+"\n";
		s+="Waktu akhir: "+this.endTime+"\n";
		s+="Banyaknya titik pada lintasan: "+this.locations.size();
		return s;
	}
}