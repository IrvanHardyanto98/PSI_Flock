import java.util.ArrayList;
import java.util.HashSet;
/**
* Representasi Flock Pattern yang terdiri dari rangkaian flock-flock
* Flock Pattern adalah kumpulan flock-flock yang berisi minimal \mu buah lintasan
* yang terletak saling berdekatan selama periode waktu tertentus
**/

public class FlockPattern{
	private final int flockPatternID;
	private final int startTime;
	private int endTime;
	private ArrayList<Flock> flocks;
	private HashSet<Integer> entityIDList;
	public FlockPattern(int flockPatternID,int startTime){
		this.startTime = startTime;
		this.endTime = 0;
		this.flockPatternID=flockPatternID;
		this.flocks=new ArrayList<>();
		this.entityIDList = new HashSet<>();
	}
	
	/**
	* Tambahkan flock di posisi paling belakang, sesuai urutan waktu nya
	**/
	public void addFlock(Flock flock){
		flock.setPatternID(this.flockPatternID);
		this.flocks.add(flock);
		//masih perlu diubah
		//this.entityIDList.addAll(flock.getEntityIDSet());
	}

	public int getStartTime(){
		return this.startTime;
	}

	public void setEndTime(int endTime){
		this.endTime;
	}

	public int getID(){
		return this.flockPatternID;
	}
	
	//
	public Flock getLastFlock(){
		return this.flocks.get(this.flocks.size()-1);
	}
}