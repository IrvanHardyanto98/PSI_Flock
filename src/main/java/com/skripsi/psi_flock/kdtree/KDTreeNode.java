package com.skripsi.psi_flock.kdtree;
import com.skripsi.psi_flock.model.Location;
public class KDTreeNode{
		Location loc;
		KDTreeNode left;//atau 'bottom'
		KDTreeNode right;//atau 'upper'
		public KDTreeNode(Location loc){
			this.loc=loc;
			this.left=null;
			this.right=null;
		}
		
		public double getX(){
			return this.loc.getX();
		}
		
		public double getY(){
			return this.loc.getY();
		}
	}