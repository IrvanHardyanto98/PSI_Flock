package com.skripsi.psi_flock.kdtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import com.skripsi.psi_flock.model.Location;
import com.skripsi.psi_flock.model.LocationComparatorX;
import com.skripsi.psi_flock.model.LocationComparatorY;
/**
* Update 5 Agustus 2021: Masih ada masalah di Build Kd-Tree, kalau elemen yang dimasukin ada yang berulang,
* Entah di sumbu X atau sumbu Y
* Masalah nya: - cari median dgn angka berulang
*            : - pecah array 'titik' kedalam dua sub array.
**/
public class KDTree{
	private KDTreeNode root;
	
	public KDTree(){
		this.root=null;
	}
	
	private KDTreeNode buildKDTreeRec(Location[] locations,int depth,int left,int right){
		Location v;
		if(left==right){
			return new KDTreeNode(locations[left]);
		}else{
			int median=left;
			v = this.findMedian(depth%2,locations,left,right);//sekaligus nge sort si arraynya
			if(right-left>1){
				for(int i=right;i>=left;i--){
					if(depth%2==0){
						if(locations[i].getX()==v.getX()){
							median=i;
							break;
						}
					}else{
						if(locations[i].getY()==v.getY()){
							median=i;
							break;
						}
					}
				}
			}
			
			
			KDTreeNode leftNode=buildKDTreeRec(locations,depth+1,left,median);//atau 'bawah'
			KDTreeNode rightNode=buildKDTreeRec(locations,depth+1,median+1,right);//atau 'atas'
			
			KDTreeNode curr = new KDTreeNode(v);
			curr.left=leftNode;
			curr.right=rightNode;
			
			return curr;
		}
	}
	
	/**
	*@param medianPtr pointer ke median
	**/
	public Location findMedian(int mode,Location[] locs,int left,int right){
		if(left>=right)return null;
		//if(right-left==1)return locs[left];
		TreeSet<Double> ts=new TreeSet<>();
		if(mode==0){//sumbuX
			Arrays.sort(locs,left,right+1,new LocationComparatorX());
			for(int i=left;i<=right;i++){
				ts.add(locs[i].getX());
			}
		}else{
			Arrays.sort(locs,left,right+1,new LocationComparatorY());//terurut menaik (kiri -> paling kecil,kanan-> paling gede)
			for(int i=left;i<=right;i++){
				ts.add(locs[i].getY());
			}
		}

		Double[] unique=ts.toArray(new Double[ts.size()]);
		double medianAxis=unique[(ts.size()-1)/2];
		
		Location res=null;
		for(int i=right;i>=left;i--){
			if(mode==0){
				if(locs[i].getX()==medianAxis){
					res= locs[i];
					break;
				}
			}else{
				if(locs[i].getY()==medianAxis){
					res= locs[i];
					break;
				}
			}
		}
		return res;
	}
	
	private void DFSRec(Region region,ArrayList<Location> result,KDTreeNode currentNode,int depth){
		if(currentNode!=null){
			if(currentNode.left==null&&currentNode.right==null){
				if(region.containsPoint(currentNode.loc)){
				result.add(currentNode.loc);
				}
			}else{
			//subtree kiri atau bawah
						
				if(depth%2==0){//sumbu x
					if(region.getLeft()<=currentNode.getX())DFSRec(region,result,currentNode.left,depth+1);
				}else{//sumbu y
					if(region.getBottom()<=currentNode.getY())DFSRec(region,result,currentNode.left,depth+1);
				}

				//subtree kanan atau atas
				if(depth%2==0){//sumbu x
					if(region.getRight()>=currentNode.getX())DFSRec(region,result,currentNode.right,depth+1);
				}else{//sumbu y
					if(region.getUpper()>=currentNode.getY())DFSRec(region,result,currentNode.right,depth+1);
				}
				
			}
		}
	}
	
	
	
	public void DFSTraversal(KDTreeNode curr,int depth){
		if(curr!=null){

				System.out.println("traversing LEFT subtree, curent depth: "+depth);
				DFSTraversal(curr.left,depth+1);
				
			
			if(curr.left==null&&curr.right==null){
				System.out.println("current node is a leaf: ("+curr.getX()+","+curr.getY()+"), at depth "+depth);
			}else{
				System.out.println("current node: ("+curr.getX()+","+curr.getY()+"), at depth "+depth);
			}

			System.out.println("traversing ROGHT subtree, curent depth: "+depth);
			DFSTraversal(curr.right,depth+1);	
		}
	}
	
	public void traverse(){
		this.DFSTraversal(this.root,0);
	}
	
	public void buildKDTree(Location[] locations){
		this.root=buildKDTreeRec(locations,0,0,locations.length-1);
	}
	
	public ArrayList<Location> rangeQuery(Region region){
		ArrayList<Location> result = new ArrayList<>();
		this.DFSRec(region,result,this.root,0);
		return result;
	}
}