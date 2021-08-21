package com.skripsi.psi_flock.kdtree;
import com.skripsi.psi_flock.model.Location;
/**
* sebuah Region yang dibatasi oleh titik (x1,y1) di kiri bawah dan (x2,y2) di kanan atas
* PENTING! saat instansiasi, pastikan x1 < x2 dan y1 < y2
* @author Mohan Sundaraju, pada laman https://www.baeldung.com/java-range-search
* dengan penyesuaian oleh Irvan Hardyanto (2 Agustus 2021)
**/

public class OrthogonalRegion implements Region{
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	
	public OrthogonalRegion(double x1,double y1,double x2,double y2){
		this.x1=Math.min(x1,x2);
		this.y1=Math.min(y1,y2);
		this.x2=Math.max(x1,x2);
		this.y2=Math.max(y1,y2);
	}
	
	public void setX1(double x1){
		this.x1=x1;
	}
	public void setY1(double y1){
		this.y1=y1;
	}
	public void setX2(double x2){
		this.x2=x2;
	}
	public void setY2(double y2){
		this.y2=y2;
	}
	
	public double getX1(){
		return this.x1;
	}
	
	public double getY1(){
		return this.y1;
	}
	
	public double getX2(){
		return this.x2;
	}
	
	public double getY2(){
		return this.y2;
	}
	
	public double getLeft(){
		return this.x1;
	}
	public double getBottom(){
		return this.y1;
	}
	
	public double getRight(){
		return this.x2;
	}
	
	public double getUpper(){
		return this.y2;
	}
	
	
	
	public boolean containsPoint(Location loc){
		return loc.getX() >= this.x1 
        && loc.getX() <= this.x2 
        && loc.getY() >= this.y1 
        && loc.getY() <= this.y2;
	}
	
	/**
	*@param mode 0 untuk garis x, 1 untuk garis y
	**/
	//public boolean isOverlapWithLine(int mode,Location loc){
		//if(mode==0){
			//return this.x1<=loc.getX() && loc.getX()<=this.x2;
		//}else{
			//return this.y1<=loc.getY() && loc.getY()<=this.y2;
		//}
	//}
	
	//public boolean doesOverlap(Region testRegion) {
		//if (testRegion.getX2() < this.getX1()) {
			//return false;
		//}
		//if (testRegion.getX1() > this.getX2()) {
			//return false;
		//}
		//if (testRegion.getY1() > this.getY2()) {
			//return false;
		//}
		//if (testRegion.getY2() < this.getY1()) {
			//return false;
		//}
		//return true;
	//}
}