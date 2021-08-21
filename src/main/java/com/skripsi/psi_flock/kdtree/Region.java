package com.skripsi.psi_flock.kdtree;
import com.skripsi.psi_flock.model.Location;

public interface Region{
	public boolean containsPoint(Location loc);
	public double getLeft();
	public double getBottom();
	public double getRight();
	public double getUpper();
}