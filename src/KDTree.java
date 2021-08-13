import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
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
				System.out.println("Testing point: "+currentNode.getX()+","+currentNode.getY());
				if(region.containsPoint(currentNode.loc)){
				System.out.println("ADDED point: "+currentNode.getX()+","+currentNode.getY());
				result.add(currentNode.loc);
				}
			}else{
				
				//System.out.println("ADDED point: "+currentNode.getX()+","+currentNode.getY());
				
				
				System.out.println("at node: "+currentNode.getX()+","+currentNode.getY()+" depth: "+depth);
				
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

class KDTreeNode{
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

/**
* sebuah Region yang dibatasi oleh titik (x1,y1) di kiri bawah dan (x2,y2) di kanan atas
* PENTING! saat instansiasi, pastikan x1 < x2 dan y1 < y2
* @author Mohan Sundaraju, pada laman https://www.baeldung.com/java-range-search
* dengan penyesuaian oleh Irvan Hardyanto (2 Agustus 2021)
**/

class OrthogonalRegion implements Region{
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

class CircularRegion implements Region{
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