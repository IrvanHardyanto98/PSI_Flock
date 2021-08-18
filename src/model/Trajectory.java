package model;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public void addAllLocation(){
		
	}
	public Location getLocation(int timestamp){
		return this.locations.get(timestamp);
	}
}