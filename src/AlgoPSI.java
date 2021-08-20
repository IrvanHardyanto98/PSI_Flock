import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.BitSet;
import java.util.Iterator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.awt.geom.Point2D;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.primitives.Ints;
import model.*;
import kdtree.*;
import org.streaminer.util.hash.*;
public class AlgoPSI{
	private int FLOCK_PATTERN_ID;
	private static final int P_SIZE=1000;//mengatasi overhead kalau ukuran awal terlalu kecil
	private int PRECISION=3;
	private int minTimeInstance;
	private double distTreshold;
	private int minEntityNum;
	private int seedHash;
	private HashMap<Integer,FlockPattern> patterns;
	
	public AlgoPSI(int minEntityNum,double distTreshold,int minTimeInstance,int seedHash){
		this.FLOCK_PATTERN_ID = 1;
		this.minEntityNum=minEntityNum;
		this.distTreshold=distTreshold;
		this.minTimeInstance=minTimeInstance;
		this.seedHash=seedHash;
		this.patterns = new HashMap<>();
	}
	
	public HashMap<Integer,FlockPattern>getAllFlockPattern(){
		return new HashMap<Integer,FlockPattern>(this.patterns);
	}
	/**
	* Method utama kelas AlgoPSI
	**/
	public void findAllFlockPattern(int timestamp,ArrayList<Location> locations){
		Location[] loc = new Location[locations.size()];
		locations.toArray(loc);
		ArrayList<MinimumBoundingRectangle> mbr = this.findCandidateFlock(timestamp,loc);
		ArrayList<Flock> finalFlocks = this.filterFlocks(mbr);
		this.joinFlock(this.patterns,timestamp,finalFlocks);
		//System.out.println(mbr.toString());
//		for(MinimumBoundingRectangle x: mbr){
//			System.out.println(x.toString());
//			System.out.println(x.getAllFlock().toString());
//		}
		//System.out.println("Final flocks are");
		//System.out.println(finalFlocks.toString());
	}
	/**
	* Cari kandidat flock pada waktu ti, dengan menggunakan metode plane sweep
	* INPUT: positions in timestamp ti, ordered by x-axis
	**/
	public ArrayList<MinimumBoundingRectangle> findCandidateFlock(int timestamp, Location[] locations){
		ArrayList<MinimumBoundingRectangle> activeBoxes = new ArrayList<>();
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
			x1=Double.MAX_VALUE;
			y1=Double.MAX_VALUE;
			x2=Double.MIN_VALUE;
			y2=Double.MIN_VALUE;
			//System.out.println("curent pr, :("+pr.getX()+","+pr.getY()+")");
			for(Location ps: locations){//O(N)
				if(Math.abs(ps.getX()-pr.getX())<=this.distTreshold){
					if(Math.abs(ps.getY()-pr.getY())<=this.distTreshold){
						//System.out.println("FOUND A POINT, :("+ps.getX()+","+ps.getY()+")");
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
			
			MinimumBoundingRectangle mbr = new MinimumBoundingRectangle(pr.getPosition(),timestamp,new Point2D.Double(x1,y1),new Point2D.Double(x2,y2));
			for(int i = 0 ; i < P.size();i++){
				Location p = P.get(i);
				if (p.equals(pr)){
					continue;
				}else if(dist(p.getX(),p.getY(),pr.getX(),pr.getY())<=this.distTreshold){
					//System.out.println("pr: ("+pr.getX()+","+pr.getY()+"), p: ("+p.getX()+","+p.getY()+")");
					//hitung dua buah flock c1 dan c2 yang menyinggung titik pr dan p, dari titik pada waktu ti
					Flock[] flocks=this.countFlock(timestamp,pr.getPosition(),p.getPosition(),tree);
					for(Flock c: flocks){
						int intersectNum = c.countIntersections(P);
						//System.out.println("intersectNum :"+intersectNum);
						if(intersectNum >= this.minEntityNum){
							
							//this.candidateFlocks.add(c);
							mbr.addFlock(c);
						}
					}
				}
			}
			activeBoxes.add(mbr);
		}
		return activeBoxes;
	}
	
	/**
	* 
	* hitung dua flock yang menyinggung pr dan ps
	**/
	private Flock[] countFlock(int timestamp,Point2D pr,Point2D p,KDTree tree){// butuh akses ke seluruh 'titik' pada waktu ti
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
			
			double dist =Math.sqrt((flockRadius*flockRadius)-Math.pow((xa-xb)/2.0,2));
			
			xc1=(xa-xb)/2.0+xb;
			yc1=ya+dist;
			
			xc2=xc1;
			yc2=ya-dist;
		}else{
			xa=pr.getX();
			ya=pr.getY();
			
			xb=p.getX();
			yb=p.getY();
			
			//System.out.println("xa: "+xa);
			//System.out.println("ya: "+ya);
			//System.out.println("xb: "+xb);
			//System.out.println("yb: "+yb);
			
			//dCD kuadrat (BELUM diakar kuadrat)
			//double dAB = this.dist(xa,ya,xb,yb);
			//double dCD= (flockRadius*flockRadius)-Math.pow(dAB/2.0,2);
			double dCD=(flockRadius*flockRadius)-this.quadraticDist(xa,ya,xb,yb)/4.0;
			
			double mCD=(xa-xb)/(yb-ya);
			
			xd = (Math.abs(xb-xa)/2.0)+Math.min(xa,xb);
			yd = (Math.abs(yb-ya)/2.0)+Math.min(ya,yb);
			xd = this.roundValue(xd);
			yd = this.roundValue(yd);
			//System.out.println("xd: "+xd);
			//System.out.println("yd: "+yd);
			double A=1.0+(mCD*mCD);
			double B=-2.0*xd*A;
			double C=A*xd*xd-(dCD);
			
			//System.out.println("mCD: "+mCD);
			//System.out.println("dCD: "+dCD);
			//System.out.println("A: "+A);
			//System.out.println("B: "+B);
			//System.out.println("C: "+C);
			//rumusABC
			
			//hitung determinan (B^2-4AC)
			double det=Math.sqrt((B*B)-(4.0*A*C));
			//System.out.println("det: "+det);
			//X1,2=(-b +- sqrt(D))/2a
			xc1=(-1.0*B+det)/(2.0*A);
			xc2=-1.0*(B+det)/(2.0*A);
			
			//hitung yc1 dan yc2 dari xc1 dan xc2
			yc1=mCD*(xc1-xd)+yd;
			yc2=mCD*(xc2-xd)+yd;
		}
		Flock[] result = new Flock[2];
		//Buat dua buah flock
		//setiap flock punya signature -> dihitung dari ID_Entitas
		//artinya setiap nambahin satu 'titik' ke flock -> hitung binary signature.
		xc1 = this.roundValue(xc1);
		yc1 = this.roundValue(yc1);

		xc2= this.roundValue(xc2);
		yc2= this.roundValue(yc2);
		//System.out.println("xc1: "+xc1);
		//System.out.println("yc1: "+yc1);
		//System.out.println("xc2: "+xc2);
		//System.out.println("yc2: "+yc2);
		result[0]=new Flock(Hasher.getInstance(this.seedHash),timestamp,flockRadius,xc1,yc1);
		result[1]=new Flock(Hasher.getInstance(this.seedHash),timestamp,flockRadius,xc2,yc2);
		CircularRegion q1 = new CircularRegion(xc1,yc1,this.distTreshold/2.0);
		CircularRegion q2 = new CircularRegion(xc2,yc2,this.distTreshold/2.0);
		
		//cari titik yang terletak dalam radius flock.
		ArrayList<Location> queryResult = tree.rangeQuery(q1);
		//System.out.println("query size is: "+queryResult.size());
		result[0].addLocations(queryResult);

		queryResult = tree.rangeQuery(q2);
		//System.out.println("query size is: "+queryResult.size());
		result[1].addLocations(queryResult);
		return result;
	}

	private double roundValue(double input){
		BigDecimal bd = new BigDecimal(input).setScale(this.PRECISION, RoundingMode.HALF_EVEN);
     	return bd.doubleValue();
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
					return;
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

	/**
	* Gabungkan flock-flock pada waktu sebelumnya dengan flock pada waktu saat ini
	* @param flockPatterns variabel reference
	**/
	public void joinFlock(HashMap<Integer,FlockPattern> flockPatterns,int currTime,ArrayList<Flock> currFlocks){
		//
		HashMap<Integer,ArrayList<Flock>> invertedIndex;
		
		//flock pattern tidak harus selalu mulai di t1, bisa saja di t3, t4
		//yang penting memenuhi syarat MINIMAL \delta buah timestamp YANG BERUNTUN (tidak putus ditengah-tengah).
		
		//kasus khusus flock pada waktu pertama banget
		if(flockPatterns.isEmpty()){
			//instansiasi bagian awal dari flock pattern nya
			for(Flock f: currFlocks){
				//satu flock-> satu pattern yg berbeda
				this.addNewFlockPattern(flockPatterns,f,currTime);
			}
		}else{
			//inverted index pada waktu sebelumnya
			invertedIndex=this.buildInvertedIndex(flockPatterns,currTime-1);
			//System.out.println(invertedIndex.toString());
			//untuk setiap flock pada waktu saat ini...
			for(int i=0;i<currFlocks.size();i++){
				Flock curr = currFlocks.get(i);
				boolean noMatch = true;

				//udah ga tau lagi harus pake nama apa
				//untuk menyimpan flock hasil kueri (flock pada waktu sblmnya)
				HashSet<Flock> s1 = new HashSet<>();//untuk semuanya
				HashSet<Flock> s2 = new HashSet<>();//untuk per satu 'titik'

				//iterasi setiap 'titik' pada flock-flock saaat ini
				for(Location loc: curr.getAllLocation()){
					s2.clear();
					//kuerikan setiap id benda ke inverted index
					//cari flock pada waktu sebelumnya yang mencakup id entitas
					ArrayList<Flock> flocks=invertedIndex.get(loc.getEntityID());
					if(flocks==null){
						//kalau nggak ada id entitas di flock sebelumnya
						continue;
					}
					//periksa join condition pada setiap flock hasil kueri
					for(Flock f: flocks){
						int similar=curr.countEntityIDIntersection(f).size();
						if(similar>=this.minEntityNum){
							s2.add(f);//Perhatikan method equals() dan hashCode() di kelas Flock
						}
					}

					if(s1.isEmpty()){
						//Perhatikan method equals() dan hashCode() di kelas Flock
						s1.addAll(s2);
					}else{
						//Perhatikan method equals() dan hashCode() di kelas Flock
						s1.retainAll(s2);
					}
				}

				//untuk setiap flock pada wkt sebelumnya 
				//yang memenuhi syarat n elemen yang sama dengan flock saat ini
				if(!s1.isEmpty()){
					//iterasi flock hasil kueri (flock pada waktu sebelumnya)
					Iterator<Flock> iter = s1.iterator();
					while(iter.hasNext()){
						//qf itu singkatan dari 'query flock'
						Flock qf=iter.next();

						int patternID = qf.getPatternID();
						//anggap patternID nggak nol
						FlockPattern fp=flockPatterns.get(patternID);

						//tambahkan flock saat ini ke dalam flock pattern yang sudah ada
						fp.addFlock(curr);
					}
				}
			}
		}
	}

	private void addNewFlockPattern(HashMap<Integer,FlockPattern> list,Flock flock,int timestamp){
		FlockPattern flockPattern = new FlockPattern(this.FLOCK_PATTERN_ID,timestamp);
		flockPattern.addFlock(flock);
		list.put(this.FLOCK_PATTERN_ID,flockPattern);
		this.FLOCK_PATTERN_ID++;
	}

	//array list-> urutan di array itu sesuai urutan masuk nya
	/**
	* Cari flock pattern yang 'ujung' nya berada di time stamp saat ini
	* @param timestamp waktu saat inverted index dibuat
	* @param flockPatterns flock pattern
	**/
	private HashMap<Integer,ArrayList<Flock>> buildInvertedIndex(HashMap<Integer,FlockPattern> flockPatterns,int timestamp){
		HashMap<Integer,ArrayList<Flock>> invertedIndex=new HashMap<>();		
		
		Set<Integer> keySet = flockPatterns.keySet();
		Iterator<Integer> iter = keySet.iterator();
		while(iter.hasNext()){
			FlockPattern curr = flockPatterns.get(iter.next());

			Flock tail = curr.getLastFlock();
			//System.out.println(tail.toString());
			if(tail.getTimestamp()==timestamp){
				//cari semua titik di dalam flock
				ArrayList<Location> locs = tail.getAllLocation();
				
				//untuk setiap titik di dalam flock
				for(int i=0;i<locs.size();i++){
					Location currPoint = locs.get(i);
					if(invertedIndex.get(currPoint.getEntityID())==null){
						ArrayList<Flock> list = new ArrayList<>();
						list.add(tail);
						invertedIndex.put(currPoint.getEntityID(),list);
					}else{
						invertedIndex.get(currPoint.getEntityID()).add(tail);
					}
				}
			}
			
		}
		return invertedIndex;
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