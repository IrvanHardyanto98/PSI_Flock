import java.util.ArrayList;
import java.util.HashSet;
import java.util.BitSet;
import java.awt.geom.Point2D;

//asumsi yang dibuat sejauh ini:
//-tidak ada dua titik yang koordinat nya sama persis
//-jumlah titik pada setiap waktu (ti) tidak lebih dari 100 buah
/**
* Kelas yang merepresentasikan sebuah Flock pada Flock Pattern
* Flock adalah himpunan yang terdiri dari minimal \mu buah lintasan yang terletak dalam radius tertentu (\varepsilon).
**/
public class Flock{
	private static final int BITSTRING_LENGTH=2000;//error rate -> 0.009055
	private ArrayList<Location> locations;
	private HashSet<Integer> entitiyIDSet;
	private int timestamp;
	private double radius;
	private final Point2D.Double centerPoint;
	private BitSet binarySignature;
	private Hasher hasher;
	
	/**
	* @param timestamp atribut waktu flock
	* @param radius jari-jari flock
	* @param x koordinat sumbu x titik pusat flock
	* @param y koordinat sumbu y titik pusat flock
	**/
	public Flock(Hasher hasher,int timestamp,double radius,double x,double y){
		this.timestamp=timestamp;
		this.radius=radius;
		this.binarySignature=new BinarySignature(this.BITSTRING_LENGTH);//keyword new alokasi memori di heapSpace
		this.centerPoint = new Point2D.Double(x,y);
		this.locations = new ArrayList<>();
		this.entitiyIDSet = new HashSet<>();
		this.hasher=hasher;
	}
	
	//jangan lupa hitung binary signature nya;
	public void addLocation(Location loc){
		this.locations.add(loc);
		long spookyHashVal = this.hasher.doSpookyHash(loc.getEntityID());
		int murHashVal = this.hasher.doMurMurHash(loc.getEntityID());
		this.binarySignature.set(spookyHashVal%this.BITSTRING_LENGTH);
		this.binarySignature.set(murHashVal%this.BITSTRING_LENGTH);
	}
	
	public void countSignature(){
		for(int i = 0 ;i<this.locations.size();i++){
			long spookyHashVal = this.hasher.doSpookyHash(this.locations[i].getEntityID());
			int murHashVal = this.hasher.doMurMurHash(this.locations[i].getEntityID());
			this.binarySignature.set(spookyHashVal%this.BITSTRING_LENGTH);
			this.binarySignature.set(murHashVal%this.BITSTRING_LENGTH);
		}
	}
	
	public int countIntersections(ArrayList<Location> loc){
		//kalau pakai array list, pastiin ga ada duplikat
		ArrayList<Location> temp;
		if(this.locations.size()>loc.size()){
			temp= new ArrayList<>(this.locations);
			temp.retainAll(new ArrayList<>(loc));
		}else{
			temp= new ArrayList<>(loc);
			temp.retainAll(new ArrayList<>(this.locations));
		}
		return temp.size();
	}
	
	public HashSet<Integer> getEntityIDSet(){
		return this.entitiyIDSet;
	}
	//hitung irisan ID entitas, (bedakan dengan method countIntersections yg berbasis posisi)
	public int countEntityIDIntersection(Flock other){
		HashSet<Integer> a;
		if(this.entitiyIDSet.size()>other.getEntityIDSet().size()){
			a = new HashSet<>(this.entitiyIDSet);
			a.retainAll(new HashSet<>(other.getEntityIDSet()));
		}else{
			a = new HashSet<>(other.getEntityIDSet());
			a.retainAll(new HashSet<>(this.entitiyIDSet));
		}
		return a.size();
	}
	
	public Point2D getCenterPoint(){
		return this.centerPoint;
	}
	
	/**
	* hitung titik-titik yang beririsan dengan flock lain
	* @param other flock lainnya
	**/
	public ArrayList<Location> intersect(Flock other){//O(l1.length)
		ArrayList<Location> l1;
		ArrayList<Location> l2;
		if(this.locations.size()>other.getAllLocation().size()){
			l1 = new ArrayList<>(this.locations);
			l2 = new ArrayList<>(other.getAllLocation());
		}else{
			l1 = new ArrayList<>(other.getAllLocation());
			l2 = new ArrayList<>(this.locations);
		}
		
		return l1.retainAll(l2);
	}
	
	/**
	* Hitung jarak flock ini dengan flock lainnya
	* @param other flock lain
	**/
	public double dist(Flock other){
		return Math.sqrt(Math.pow(this.centerPoint.getX()-other.getCenterPoint().getX(),2)+Math.pow(this.centerPoint.getY()-other.getCenterPoint().getY(),2));
	}
	public void addLocations(ArrayList<Location> locations){
		this.locations=locations;
		this.countSignature();
		for(int i=0;i<locations.size();i++){
			Location loc = locations.get(i);
			this.entitiyIDSet.add(loc.getEntityID());
		}
	}
	
	public ArrayList<Location> getAllLocation(){
		return this.locations;
	}
	
	public boolean isInFlock(Point2D pos){
		double dist = Math.pow(pos.getX()-this.x,2)+Math.pow(pos.getY()-this.y,2);
		double r_2 = this.radius*this.radius;
		return dist < r_2;
	}
	
	/**
	* kembalikan binary signature milik flock
	**/
	public BitSet getBinarySignature(){
		return BitSet.valueOf(this.binarySignature.toByteArray());
	}
}