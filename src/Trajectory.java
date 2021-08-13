import java.util.ArrayList;
import java.util.HashMap;
public class Trajectory{
	private HashMap<Integer,Location> locations;
	private int entityID;//ID ENTITAS TIDAK BOLEH BERUBAH DARI AWAL SAMPAI AKHIR!, karena Id_entitas setiap 'titik' harus diubah satu-satu
	private int startTime;
	private int endTime;
	
	public Trajectory(int entitiyID,int startTime,int endTime){
		this.entitiyID=entitiyID;
		this.startTime=startTime;
		this.endTime=endTime;
		this.locations=new HashMap<>();
	}
	
	public void addLocation(double x,double y,int timestamp){
		this.locations.put(timestamp,new Location(this.entitiyID,x,y,timestamp));
	}
	
	public void addAllLocation(){
		
	}
	public void getLocation(int timestamp){
		return this.locations.get(timestamp);
	}
}