import java.util.ArrayList;
/**
* Representasi Flock Pattern yang terdiri dari rangkaian flock-flock
* Flock Pattern adalah kumpulan flock-flock yang berisi minimal \mu buah lintasan
* yang terletak saling berdekatan selama periode waktu tertentus
**/

public class FlockPattern{
	//pertanyaan pertama... yang jadi id itu apa ya..
	private ArrayList<Flock> flocks;
	private ArrayList<Integer> entityIDList;
	public FlockPattern(){
		this.flocks=new ArrayList<>();
		this.entityIDList = new ArrayList<>();
	}
	
	public void addFlock(Flock flock){
		this.flocks.add(flock);
	}
	
	public int countEntityIDIntersection(){
		
	}
}