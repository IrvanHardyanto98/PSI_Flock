package com.skripsi.psi_flock.model;

import java.util.Comparator;
import java.awt.geom.Point2D;
public class Point2DCompX implements Comparator<Point2D>{
	@Override
	public int compare(Point2D o1, Point2D o2){
		if(o1.getX() < o2.getX()){
			return -1;
		}else if(o1.getX() > o2.getX()){
			return 1;
		}else{
			return 0;
		}
	}
}