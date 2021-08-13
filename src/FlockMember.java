import java.awt.geom.Point2D;
public class FlockMember{
	private int entityID;
	private Point2D position;
	
	public FlockMember(int entityID,Point2D position){
		this.entityID=entityID;
		this.position= position;
	}
	
	public void setEntityID(int entityID){
		this.entityID=entityID;
	}
	public int getEntityID(){
		return this.entityID;
	}
	
	public void setPosition(double x,double y){
		this.position.setX(x);
		this.position.setY(y);
	}
	
	public Point2D getPosition(){
		return this.position;
	}
}