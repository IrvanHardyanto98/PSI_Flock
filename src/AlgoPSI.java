import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.primitives.Ints;
import org.streaminer.util.hash.*;
public class AlgoPSI{
	private static final int P_SIZE=1000;//mengatasi overhead kalau ukuran awal terlalu kecil
	private ArrayList<Flock> candidateFlocks;
	//private invertedIndex;
	private ArrayList<MinimumBoundingRectangle> activeBoxes;
	private ArrayList<FlockPattern> flockPattern;
	private int minTimeInstance;
	private double distTreshold;
	private int minEntityNum;
	private int seedHash;
	
	public AlgoPSI(int minEntityNum,double distTreshold,int minTimeInstance,int seedHash){
		this.minEntityNum=minEntityNum;
		this.distTreshold=distTreshold;
		this.minTimeInstance=minTimeInstance;
		this.activeBoxes = new ArrayList<>();
		this.candidateFlocks = new ArrayList<>();
		this.flockPattern=new ArrayList<>();
		this.seedHash=seedHash;
	}
	
	/**
	* Cari kandidat flock pada waktu ti, dengan menggunakan metode plane sweep
	* INPUT: positions in timestamp ti, ordered by x-axis
	**/
	public void findCandidateFlock(int timestamp, Location[] locations){
		double x1=Double.MAX_VALUE;
		double y1=Double.MAX_VALUE;
		double x2=Double.MIN_VALUE;
		double y2=Double.MIN_VALUE;
		KDTree tree = new KDTree();
		
		tree.buildKDTree(locations);
		ArrayList<Location> P=new ArrayList(this.P_SIZE);
		
		//urutkan berdasarkan sumbu x
		Arrays.sort(locations,new LocationComparatorX());
		
		for(Location pr: locations){//O(N)
			
			P.clear();
			for(Location ps: locations){//O(N)
				if(Math.abs(ps.getX()-pr.getX())<=this.distTreshold){
					if(Math.abs(ps.getY()-pr.getY())<=this.distTreshold){
						//cari batas-batas MBR yang mencakup titik-titik pada P
						x1=Math.min(x1,ps.getX());
						y1=Math.min(y1,ps.getY());
						
						x2=Math.max(x2,ps.getX());
						y2=Math.max(y2,ps.getY());
						//add element to P
						P.add(ps);
					}
				}
			}
			
			MinimumBoundingRectangle mbr = new MinimumBoundingRectangle(pr,timestamp,new Point2D.Double(x1,y1),new Point2D.Double(x2,y2));
			for(int i = 0 ; i < P.size();i++){
				Location p = P.get(i);
				if (p.equals(pr)){
					continue;
				}else if(dist(p.getX(),p.getY(),pr.getX(),pr.getY())<=this.distTreshold){
					//hitung dua buah flock c1 dan c2 yang menyinggung titik pr dan p, dari titik pada waktu ti
					Flock[] flocks=this.countFlock(timestamp,pr,p,locations);
					for(Flock c: flocks){
						int intersectNum = c.countIntersections(P);
						if(intersectNum >= this.minEntityNum){
							
							this.candidateFlocks.add(c);
							mbr.addFlock(c);
						}
					}
				}
			}
			activeBoxes.add(mbr);
		}
	}
	
	/**
	* 
	* hitung dua flock yang menyinggung pr dan ps
	**/
	private Flock[] countFlock(int timestamp,Point2D pr,Point2D p,Location[] locations){// butuh akses ke seluruh 'titik' pada waktu ti
		//pertama: hitung titik pusat c1 dan c2.
		double xc1,yc1,xc2,yc2;
		double flockRadius = this.distTreshold/2.0;
		double xa,ya,xb,yb;
		double xd,yd;

		if(pr.getX()==p.getX()&&pr.getY()!=p.getY()){
			//ya > yb
			if(pr.getY()>p.getY()){
				ya=pr.getY();
				yb=p.getY();
			}else{//p.getY()>pr.getY()
				ya=p.getY();
				yb=pr.getY();
			}
			
			xa=pr.getX();
			//xb=xa;
			
			//yd=Math.abs(pr.getY()-p.getY())+Math.min(p.getY(),pr.getY());
			
			double dist=Math.sqrt((flockRadius*flockRadius)-Math.pow((ya-yb)/2.0,2));
			xc1=xa-dist;
			yc1=(ya-yb)/2.0+yb;
			
			xc2=xa+dist;
			yc2=yc1;
			
		}else if(pr.getY()==p.getY()&&pr.getX()!=p.getX()){
			//xa > xb
			if(pr.getX()>p.getX()){
				xa=pr.getX();
				xb=p.getX();
			}else{
				xa=p.getX();
				xb=pr.getX();
			}
			ya=pr.getY();
			
			dist =Math.sqrt((flockRadius*flockRadius)-Math.pow((xa-xb)/2.0,2));
			
			xc1=(xa-xb)/2.0+xb;
			yc1=ya+dist;
			
			xc2=xc1;
			yc2=ya-dist;
		}else{
			xa=pr.getX();
			ya=pr.getY();
			
			xb=p.getX();
			yb=p.getY();
			
			//dCD kuadrat (BELUM diakar kuadrat)
			//double dAB = this.dist(xa,ya,xb,yb);
			//double dCD= (flockRadius*flockRadius)-Math.pow(dAB/2.0,2);
			double dCD=(flockRadius*flockRadius)-this.quadraticDist(xa,ya,xb,yb)/4.0;
			
			double mCD=(xa-xb)/(yb-ya);
			
			xd = (Math.abs(xb-xa)/2.0)+Math.min(xa,xb);
			yd = (Math.abs(yb-ya)/2.0)+Math.min(ya,yb);
			
			double A=1.0+(mCD*mCD);
			double B=-2.0*xd*A;
			double C=A*xd*xd*-(dCD);
			
			//rumusABC
			
			//hitung determinan (B^2-4AC)
			double det=Math.sqrt((B*B)-(4.0*A*C));
			//X1,2=(-b +- sqrt(D))/2a
			double xc1=(-1.0*B+det)/2.0*A;
			double xc2=-1.0*(B+det)/2.0*A;
			
			//hitung yc1 dan yc2 dari xc1 dan xc2
			yc1=mCD*(xc1-xd)+yd;
			yc2=mCD*(xc2-xd)+yd;
		}
		Flock[] result = new Flock[2];
		//Buat dua buah flock
		//setiap flock punya signature -> dihitung dari ID_Entitas
		//artinya setiap nambahin satu 'titik' ke flock -> hitung binary signature.
		result[0]=new Flock(Hasher.getInstance(this.seedHash),timestamp,flockRadius,xc1,yc1);
		result[1]=new Flock(Hasher.getInstance(this.seedHash),timestamp,flockRadius,xc2,yc2);
		CircularRegion q1 = new CircularRegion(xc1,yc1);
		CircularRegion q2 = new CircularRegion(xc2,yc2);
		
		//cari titik yang terletak dalam radius flock.
		ArrayList<Location> queryResult = tree.rangeQuery(q1);
		result[0].addLocations(queryResult);

		queryResult = tree.rangeQuery(q2);
		result[1].addLocations(queryResult);
		return result;
	}

	
	/**
	* Hapus flock-flock yang membentuk subset dengan flock lain di dalam MBR pada waktu tertentu.
	**/
	public ArrayList<Flock> filterFlocks(ArrayList<MinimumBoundingRectangle> activeBoxes){
		ArrayList<Flock> finalFlocks = new ArrayList<>(this.P_SIZE);
		for(int j=0;j<activeBoxes.size();j++){
			for(int k=j+1;k<activeBoxes.size();k++){
				MinimumBoundingRectangle mbr1=activeBoxes.get(j);
				if(mbr1.doesOverlap(activeBoxes.get(k))){
					for(Flock c:mbr1.getAllFlock()){
						insertFlock(finalFlocks,c);
					}
				}else{
					break;
				}
			}
		}
		return finalFlocks;
	}
	
	private void insertFlock(ArrayList<Flock> flocks,Flock currFlock){
		for(int i = 0 ; i < flocks.size();i++){
			Flock d=flocks.get(i);
			//hitung binary signature
			BitSet cSign=currFlock.getBinarySignature();
			BitSet dSign=d.getBinarySignature();
			
			BitSet temp = currFlock.getBinarySignature();
			temp.and(d.getBinarySignature());

			if(temp.equals(cSign) && currFlock.dist(d)<=this.distTreshold){
				if(d.intersect(currFlock).equals(currFlock.getAllLocation())){
					return flocks;
				}
			}else if(temp.equals(dSign)){
				if(currFlock.intersect(d).equals(d.getAllLocation())){
					flocks.remove(d);//hapus flock pada posisi ke-i
				}
			}
		}//kurang lebih O(N)
		
		if(!flocks.contains(currFlock)){//O(N)
			flocks.add(currFlock);//ditambahkan satu-satu di 'belakang'
		}
	}
	
	//memanfaatkan inverted index()
	//ID_entitas -> list flock-flock yg mencakup entitas1 tsb
	//1 -> flock1,flock2,flock 3
	//2 -> flock2,flock3
	//3 -> flock1,flock3
	//dst...
	//tapi list flock yg dipake udah di pisahin dari subset atau superset (hasil dari algoritma 2)
	/**
	* Gabungkan flock-flock pada waktu sebelumnya dengan flock pada waktu saat ini
	* @param flockPatterns variabel reference
	**/
	public void joinTwoFlock(HashMap<FlockPattern> flockPatterns,int prevTime,ArrayList<Flock> prevFlocks,int currTime,ArrayList<Flock> currFlocks){
		//
		HashMap<Integer,ArrayList<Flock>> invertedIndex;
		
		//flock pattern tidak harus selalu mulai di t1, bisa saja di t3, t4
		//yang penting memenuhi syarat MINIMAL \delta buah timestamp YANG BERUNTUN (tidak putus ditengah-tengah).
		
		//kasus khusus flock pada waktu pertama banget
		if(prevTime<0&&prevFlocks==null){
			//instansiasi bagian awal dari flock pattern nya
			for(Flock f: currFlocks){
				//satu flock-> satu pattern yg berbeda
				
				//antara buat baru atau ambil dari list
				FlockPattern flockPattern = new FlockPattern();
				flockPattern.addFlock(f);
			}
			
			
		}else{
			invertedIndex=this.buildInvertedIndex(prevFlocks);
			for(int i=0;i<currFlocks.size();i++){
				Flock curr = currFlocks.get(i);
				for(Location loc: curr.getAllLocation()){//iterasi setiap 'titik' pada flock-flock saaat ini
					
					//kuerikan setiap id benda pada flock curr
					ArrayList<Flock> flocks=invertedIndex.get(loc.getEntityID());
					
					//periksa join condition
					for(Flock f: flocks){
						int similar=curr.countEntityIDIntersection(f);
						if(similar>=this.minEntityNum){
							//ga hanya jumlah entitas yang sama yang harus di periksa.
							//gabungkan dgn flock sebelumnya....
						}
					}
				}
			}
		}
	}
	
	//document: -> flock
	//term -> ID entitas di dalam flock
	//yang jadi key itu ID entitas, kembaliannya daftar flock yang mencakup entitas tsb
	//beda waktu-> beda inverted index nya
	/**
	* Hitung Inverted Index pada waktu tertentu
	* @param flocks flock-flock pada waktu tertentu
	**/
	private HashMap<Integer,ArrayList<Flock>> buildInvertedIndex(ArrayList<Flock> flocks){
		HashMap<Integer,ArrayList<Flock>> invertedIndex=new HashMap<>();
		for(int i=0;i<flocks.size();i++){
			Flock c = flocks.get(i);
			for(int j=0;j<c.getAllLocation().size();j++){
				Location currPoint = c.getAllLocation().get(j);
				if(invertedIndex.get(currPoint.getEntityID())==null){
					//List nya masih kosong
					invertedIndex.put(currPoint.getEntityID(),new ArrayList<>());
				}else{
					invertedIndex.get(currPoint.getEntityID()).add(c);
				}
			}
		}
		return invertedIndex;
	}
	/**
	* Hitung jarak antara dua titik (x1,y1) dan (x2,y2), berdasarkan metrik Euclidean
	* @param x1 nilai x1
	* @param y1 nilai y1
	* @param x1 nilai x2
	* @param y1 nilai y2
	* @return jarak antara titik (x1,y1) dan (x2,y2)
	**/
	private double dist(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
	}
	
	private double quadraticDist(double x1, double y1, double x2, double y2){
		return Math.pow(x1-x2,2)+Math.pow(y1-y2,2);
	}
}