import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
/**
* Implementasi sebuah Point-based quad tree 
* @author Mohan Sundaraju, pada laman https://www.baeldung.com/java-range-search
* dengan penyesuaian oleh Irvan Hardyanto
* tanggal terakhir dilihat: (2 Agustus 2021)
* 
**/
public class QuadTree{
	private static final int MAX_POINTS=3;
	private Region area;
	private List<Point2D> points;
	private List<QuadTree> qTrees;//sub-quad-tree yang ukurannya lebih kecil dari quadtree sekarang
	
	public QuadTree(Region area){
		this.area=area;
		this.points = new ArrayList<>();
		this.qTrees = new ArrayList<>();
	}
	
	/**
	* Cari titik-titik yang terletak di dalam region, dengan menggunakan metode Divide and Conquer
	**/
	public List<Point2D> search(Region searchRegion, List<Point2D> matches) {
		if(matches==null){
			matches = new ArrayList<Point2D>();
		}
		if (!this.area.doesOverlap(searchRegion)) {
			return matches;//langkah pruningnya
		}else{
			for(Point2D point: this.points){
				if (searchRegion.containsPoint(point)) {
					matches.add(point);
				}
			}
			if (this.quadTrees.size() > 0) {
				for (int i = 0; i < 4; i++) {
					quadTrees.get(i).search(searchRegion, matches);
				}
			}
		}
		return matches;
	}
	
	/**
	* Method yang berfungsi untuk menambahkan titik-titik ke dalam quad tree secara rekursif
	**/
	public boolean addPoint(Point2D point) {
		if(this.area.containsPoint(point)){
			if(this.points.size()<this.MAX_POINTS){
				this.points.add(point);
				return true;
			}else{
				if(this.quadTrees.size()==0){
					this.createQuadrants();
				}
				return addPointToOneQuadrant(point);
			}
		}
		return false;
	}
	
	/**
	* Method pembantu method addPoint
	* Tambahkan sebuah titik kedalam kuadran tertentu
	**/
	private boolean addPointToOneQuadrant(Point2D point) {
		boolean isPointAdded;
		for (int i = 0; i < 4; i++) {
			isPointAdded = this.quadTrees.get(i).addPoint(point);
			if (isPointAdded)return true;
		}
		return false;
	}
	
	private void createQuadrants() {
		Region region;
		for (int i = 0; i < 4; i++) {
			region = this.area.getQuadrant(i);
			quadTrees.add(new QuadTree(region));
		}
	}
}

