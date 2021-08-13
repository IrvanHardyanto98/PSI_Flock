import java.awt.geom.Point2D;
import java.util.ArrayList;

//mencakup seluruh titik yang terletak tidak lebih dari varepsilon dari titik p_r
public class MinimumBoundingRectangle{
	private ArrayList<Flock> flocks;
	private boolean isActive;
	private int timestamp;
	private Point2D p1;
	private Point2D p2;
	private Point2D pr;
	
	
	//ada beberapa titik, cari  
	/**
	* @param timestamp atribut temporal MBR
	* @param flocks Flock-flock pada MBR saat ini
	* @param location semua "titik" yang terletak tidak jauh dari titik pr
	**/
	public MinimumBoundingRectangle(Point2D pr,int timestamp,Point2D p1,Point2D p2){
		this.pr=pr;
		this.timestamp=timestamp;
		this.p1=p1;
		this.p2=p2;
	}
	
	public Point2D getP1(){
		return this.p1;
	}
	
	public Point2D getP2(){
		return this.p2;
	}
	
	
	public void addFlock(Flock flock){
		this.flocks.add(flock);
	}
	
	public ArrayList<Flock> getAllFlock(){
		return this.flocks;
	}
	
	public void setAsActive(){
		this.isActive=true;
	}
	
	public boolean doesOverlap(MinimumBoundingRectangle other) {
		if (other.getP2().getX() < this.p1.getX()) {
			return false;
		}
		if (other.getP1().getX() > this.p2.getX()) {
			return false;
		}
		if (other.getP1().getY() > this.p2.getY()) {
			return false;
		}
		if (other.getP2().getY() < this.p1.getY()) {
			return false;
		}
		return true;
	}
}