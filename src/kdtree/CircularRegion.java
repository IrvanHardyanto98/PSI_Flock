package kdtree;
import model.Location;

public class CircularRegion implements Region{
	private double x;
	private double y;
	private double radius;
	
	public CircularRegion(double x,double y,double radius){
		this.x=x;
		this.y=y;
		this.radius=Math.abs(radius);
	}
	
	public double getRadius(){
		return this.radius;
	}
	
	public double getX(){
		return this.x;
	}
	
	public double getY(){
		return this.y;
	}
	
	public double getLeft(){
		return this.x-this.radius;
	}
	
	public double getRight(){
		return this.x+this.radius;
	}
	public double getBottom(){
		return this.y-this.radius;
	}
	
	public double getUpper(){
		return this.y+this.radius;
	}
	
	public boolean containsPoint(Location loc){
		return Math.pow(loc.getX()-this.x,2)+Math.pow(loc.getY()-this.y,2) <= this.radius*this.radius;
	}
	
	public String toString(){
		String s = "center point is at ("+this.x+","+this.y+"), r: "+this.radius+"\n";
		s+="left is: "+this.getLeft()+", right is: "+this.getRight()+"\n";
		s+="upper is: "+this.getUpper()+", bottom is: "+this.getBottom()+"\n";
		return s;
	}
	
	/**
	*@param mode 0 untuk garis x, 1 untuk garis y
	**/
	//public boolean isOverlapWithLine(int mode,Location loc){
		//if(mode==0){
			//return (loc.getX() <= this.x+this.radius)&&(loc.getX() >=this.x-this.radius);
		//}else{
			//return (loc.getY() <= this.y+this.radius)&&(loc.getY() >=this.y-this.radius);
		//}
	//}

	//public boolean doesOverlap(Region testRegion){
		//double dist = Math.sqrt(Math.pow(this.x-testRegion.getX(),2)+Math.pow(this.y-testRegion.getY(),2));
		//return (dist < loc.getRadius()+this.radius)||(dist==Math.abs(loc.getRadius()-this.radius));
	//}
}