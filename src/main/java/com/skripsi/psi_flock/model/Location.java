package com.skripsi.psi_flock.model;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import com.google.common.math.DoubleMath;
import java.util.Objects;

public class Location{
	private Point2D position;
	private int timestamp;
	private final int entityID;//karena dibutuhkan pada method countFlock di kelas AlgoPSI
	
	public Location(int entityID,double x,double y,int timestamp){
		this.entityID=entityID;
		this.position = new Point2D.Double(x,y);
		this.timestamp=timestamp;
	}
	
	/**
	* Ubah koordinat sumbu x dan y
	* @param x koordinat sumbu x
	* @param y koordinat sumbu y
	**/
	public void setPosition(double x,double y){
		this.position.setLocation(x,y);
	}
	
	public Point2D getPosition(){
		return this.position;
	}
	
	/**
	* Koordinat sumbu y 
	* @return koordinat sumbu y
	**/
	public double getX(){
		return this.position.getX();
	}
	
	/**
	* Koordinat sumbu x 
	* @return koordinat sumbu x
	**/
	public double getY(){
		return this.position.getY();
	}
	
	public int getTimestamp(){
		return this.timestamp;
	}
	
	public int getEntityID(){
		return this.entityID;
	}
	@Override
    public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof Location){
			Location other = (Location) o;
			boolean a = DoubleMath.fuzzyEquals(this.getX(), other.getX(), 0.01);
			boolean b = DoubleMath.fuzzyEquals(this.getY(), other.getY(), 0.01);
			return (a&&b);
		}
		return false;
	}
	@Override
	public int hashCode(){
		return Objects.hash(this.position.getX(),this.position.getY());
	}
	@Override
	public String toString(){
		String s="ID entitas: "+this.entityID+"\n";
		s+= "waktu: "+this.timestamp+"\n";
		s+= "posisi ("+this.position.getX()+","+this.position.getY()+")\n\n";
		return s;
	}
	
	public String getSimpleString(){
		return this.entityID+","+this.timestamp+","+this.position.getX()+","+this.position.getY();
	}
}