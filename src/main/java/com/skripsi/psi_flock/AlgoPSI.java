package com.skripsi.psi_flock;

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
import com.skripsi.psi_flock.model.*;
import com.skripsi.psi_flock.kdtree.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
public class AlgoPSI{
	private int FLOCK_PATTERN_ID;
	private static final int P_SIZE=1000;//mengatasi overhead kalau ukuran awal terlalu kecil
	private int PRECISION=3;
	private int minTimeInstance;
	private double distTreshold;
	private int minEntityNum;
	private int seedHash;
	private int startTime;
	private int endTime;
	private HashMap<Integer,FlockPattern> patterns;
	
	public AlgoPSI(int startTime,int endTime,int minEntityNum,double distTreshold,int minTimeInstance,int seedHash){
	
		this.FLOCK_PATTERN_ID = 1;
		this.minEntityNum=minEntityNum;
		this.distTreshold=distTreshold;
		this.minTimeInstance=minTimeInstance;
		this.seedHash=seedHash;
		this.patterns = new HashMap<>();
		this.startTime=startTime;
		this.endTime=endTime;
	}
	
	public AlgoPSI(int minEntityNum,double distTreshold,int minTimeInstance,int seedHash){
		this.FLOCK_PATTERN_ID = 1;
		this.minEntityNum=minEntityNum;
		this.distTreshold=distTreshold;
		this.minTimeInstance=minTimeInstance;
		this.seedHash=seedHash;
		this.patterns = new HashMap<>();
		this.startTime=0;
	}
	
	public HashMap<Integer,FlockPattern>getAllFlockPattern(){
		return new HashMap<>(this.patterns);
	}
	
	//Asumsi semua lintasan mulai di waktu yang sama
	
	public void findAllFlockPattern(Trajectory[] trajectories){
		ArrayList<Location> locs = new ArrayList<>(trajectories.length);
		this.createDebugDir();
		//for(int i = this.startTime;i <= (this.minTimeInstance+this.startTime-1);i++){
		for(int i = this.startTime;i <= this.endTime;i++){
			locs.clear();
			long start = System.currentTimeMillis();
			for(int j=0;j<trajectories.length;j++){
				Location l = trajectories[j].getLocation(i);
				if(l!=null){
					locs.add(l);
				}
			}
			if(locs.isEmpty()){
				System.out.println("Error! tidak ada catatan posisi pada waktu : "+i);
				break;
			}
			this.findAllFlockPattern(i, locs);
			System.out.println("time: "+i+" done");
		}
		this.countFlockPatternsByDuration(patterns);
		//cari flock pattern yg durasinya gak kurang dari nilai tertentu
		this.groupFlockPatternsByDuration(this.patterns);
		filterFlockPatterns(this.patterns);
	}
	
	public void findAllFlockPattern(Trajectory[] trajectories,int maxTime){
		//cari lokasi pada setiap waktu..
		//untuk file IPE
		ArrayList<Location> locs = new ArrayList<>(trajectories.length);
		this.createDebugDir();
		//setelah baca jurnal, bagian ini kayaknya keliru
		//for(int i = this.startTime;i <= (this.minTimeInstance+this.startTime-1);i++){
		for(int i = 1;i <= maxTime;i++){
			locs.clear();
			long start = System.currentTimeMillis();
			for(int j=0;j<trajectories.length;j++){
				Location l = trajectories[j].getLocation(i);
				if(l!=null){
					locs.add(l);
				}
			}
			if(locs.isEmpty()){
				System.out.println("Error! tidak ada catatan posisi pada waktu : "+i);
				break;
			}
			this.findAllFlockPattern(i, locs);
			System.out.println("time: "+i+" done");
		}
		this.countFlockPatternsByDuration(patterns);
		//cari flock pattern yg durasinya gak kurang dari nilai tertentu
		this.groupFlockPatternsByDuration(this.patterns);
		filterFlockPatterns(this.patterns);
		
	}
	
	private void countFlockPatternsByDuration(HashMap<Integer,FlockPattern> patterns){
		int[] arrayOfDurations = new int[500];
		Iterator<Integer> finalIterator = patterns.keySet().iterator();

		while(finalIterator.hasNext()){
			FlockPattern fp = patterns.get(finalIterator.next());
			arrayOfDurations[fp.getEndTime()-fp.getStartTime()+1]++;
		}
		
		TXTWriter tw3 = new TXTWriter("jumlah_flock_pattern_berdasarkan durasi.txt");
		for(int i = 0; i < arrayOfDurations.length;i++){
			tw3.addLine("Jml flock pattern dengan durasi : "+i+" adalah: "+arrayOfDurations[i]+" buah\n");
		}
		tw3.closeFile();
	}
	
	private void createDebugDir(){
		Path path = Paths.get("debugging"+File.separator);
		Path flocks = Paths.get("flocks"+File.separator);
		if(Files.exists(path)){
			File folder = new File(path.toString());
			File[] files = folder.listFiles();
			for(File f: files){
				f.delete();
			}
		}
		if(Files.exists(flocks)){
			File folder = new File(flocks.toString());
			File[] files = folder.listFiles();
			for(File f: files){
				f.delete();
			}
		}
		try {
			Files.createDirectories(flocks);
			Files.createDirectories(path);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void groupFlockPatternsByDuration(HashMap<Integer,FlockPattern> patterns){
		Path path = Paths.get("flocks-patterns-by-duration/");
		if(Files.exists(path)){
			File folder = new File(path.toString());
			File[] files = folder.listFiles();
			for(File f: files){
				f.delete();
			}
		}else{
			try {
				Files.createDirectories(path);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		HashMap<Integer,ArrayList<FlockPattern>> res = new HashMap<>();
		Iterator<Integer> iter = patterns.keySet().iterator();
		while(iter.hasNext()){
			FlockPattern fp = patterns.get(iter.next());
			int duration = fp.getEndTime()-fp.getStartTime()+1;
			
			//disini arraylist nya masih kosong
			if(res.get(duration)==null){
				ArrayList<FlockPattern> a = new ArrayList<>();
				a.add(fp);
				res.put(duration,a);
			}else{
				res.get(duration).add(fp);
			}
		}
		Iterator<Integer> iter2 = res.keySet().iterator();
		while(iter2.hasNext()){
			ArrayList<FlockPattern> t = res.get(iter2.next());
			Iterator<FlockPattern> iter3 = t.iterator();
			
			FlockPattern temp = iter3.next();
			int d = temp.getEndTime()-temp.getStartTime()+1;
			TXTWriter tw = new TXTWriter("flocks-patterns-by-duration"+File.separator+"durasi-"+d+".txt");
			tw.addLine(Integer.toString(t.size()));
			
			
			tw.addLine(temp.getSimpleString());
			while(iter3.hasNext()){
				tw.addLine(iter3.next().getSimpleString());
			}
			tw.closeFile();
		}
	}
	
	private void filterFlockPatterns(HashMap<Integer,FlockPattern> patterns){
		Set<Integer> ks = patterns.keySet();
		Iterator<Integer> iter = ks.iterator();
		HashMap<Integer,FlockPattern> temp = new HashMap<>();
		int maxDuration = Integer.MIN_VALUE;
		while(iter.hasNext()){
			int currID = iter.next();
			FlockPattern curr = patterns.get(currID);
			int duration = curr.getEndTime()-curr.getStartTime()+1;
			maxDuration = Math.max(maxDuration,duration);
			//if(duration >= this.minTimeInstance&&(curr.getEntityNum()>=2 && curr.getEntityNum()<=7)){
			if(duration == this.minTimeInstance){
				temp.put(currID,curr);
				//this.patterns.remove(currID);
			}
		}
		patterns.clear();
		patterns.putAll(temp);
	}
	/**
	* Method utama kelas AlgoPSI
	**/
	public void findAllFlockPattern(int timestamp,ArrayList<Location> locations){
		try{
		Location[] loc = new Location[locations.size()];
		locations.toArray(loc);
		ArrayList<MinimumBoundingRectangle> mbr = this.findCandidateFlock(timestamp,loc);
		ArrayList<Flock> finalFlocks = this.filterFlocks(mbr);
					
		this.joinFlock(this.patterns,timestamp,finalFlocks);
		
	TXTWriter tw2 = new TXTWriter("flocks"+File.separator+"flocks_"+timestamp+".txt");
		tw2.addLine("Final flocks at time: "+timestamp);
		tw2.addLine(Integer.toString(finalFlocks.size()));
		for(Flock f:finalFlocks){
			String s="";
			tw2.addLine(f.getSimpleString());
		}
		tw2.closeFile();
		TXTWriter tw = new TXTWriter("debugging"+File.separator+"fp_"+timestamp+".txt");
		
		
		tw.addLine(Integer.toString(this.patterns.size()));
		Iterator<Integer> iter = this.patterns.keySet().iterator();
		while(iter.hasNext()){
			tw.addLine(this.patterns.get(iter.next()).getSimpleString());
		}
		tw.closeFile();
		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}
		
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

		//KDTree tree = new KDTree();	
		//tree.buildKDTree(locations);
	
		ArrayList<Location> P=new ArrayList(this.P_SIZE);	
		Arrays.sort(locations,new LocationComparatorX());
		
		for(Location pr: locations){
			P.clear();
			x1=Double.MAX_VALUE;
			y1=Double.MAX_VALUE;
			x2=Double.MIN_VALUE;
			y2=Double.MIN_VALUE;
			for(Location ps: locations){//O(N)
				if(Math.abs(ps.getX()-pr.getX())<=this.distTreshold){
					if(Math.abs(ps.getY()-pr.getY())<=this.distTreshold){

						x1=Math.min(x1,ps.getX());
						y1=Math.min(y1,ps.getY());
						
						x2=Math.max(x2,ps.getX());
						y2=Math.max(y2,ps.getY());
						
						P.add(ps);
					}
				}
			}
			
			MinimumBoundingRectangle mbr = new MinimumBoundingRectangle(pr.getPosition(),timestamp,
					new Point2D.Double(x1,y1),new Point2D.Double(x2,y2));
			for(int i = 0 ; i < P.size();i++){
				Location p = P.get(i);
				if(p.equals(pr)||p.getX()<pr.getX()){
					continue;
				}else if(dist(p.getX(),p.getY(),pr.getX(),pr.getY())<=this.distTreshold){	
					//Flock[] flocks=this.countFlock(timestamp,pr.getPosition(),p.getPosition(),tree);
					Flock[] flocks=this.countFlock(timestamp,pr.getPosition(),p.getPosition(),locations);
					for(Flock c: flocks){
						int intersectNum = c.countIntersections(P);
						if(intersectNum >= this.minEntityNum){
							mbr.addFlock(c);
						}
					}
				}
			}
			//cek dulu mbr nya kosong atau nggak
			if(mbr.getAllFlock().size()>0){
				activeBoxes.add(mbr);
			}
		}
		return activeBoxes;
	}
	
	/**
	* 
	* hitung dua flock yang menyinggung pr dan ps
	**/
	//private Flock[] countFlock(int timestamp,Point2D pr,Point2D p,KDTree tree){// butuh akses ke seluruh 'titik' pada waktu ti
	private Flock[] countFlock(int timestamp,Point2D pr,Point2D p,Location[] locs){//pertama: hitung titik pusat c1 dan c2.
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
			
			//dCD kuadrat (BELUM diakar kuadrat)
			//double dAB = this.dist(xa,ya,xb,yb);
			//double dCD= (flockRadius*flockRadius)-Math.pow(dAB/2.0,2);
			double dCD=(flockRadius*flockRadius)-this.quadraticDist(xa,ya,xb,yb)/4.0;
			
			double mCD=(xa-xb)/(yb-ya);
			
			xd = (Math.abs(xb-xa)/2.0)+Math.min(xa,xb);
			yd = (Math.abs(yb-ya)/2.0)+Math.min(ya,yb);
			xd = this.roundValue(xd);
			yd = this.roundValue(yd);

			double A=1.0+(mCD*mCD);
			double B=-2.0*xd*A;
			double C=A*xd*xd-(dCD);
			
			//rumusABC
			
			//hitung determinan (B^2-4AC)
			double det=Math.sqrt((B*B)-(4.0*A*C));
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

		result[0]=new Flock(Hasher.getInstance(this.seedHash),timestamp,flockRadius,xc1,yc1);
		
		result[1]=new Flock(Hasher.getInstance(this.seedHash),timestamp,flockRadius,xc2,yc2);
		//CircularRegion q1 = new CircularRegion(xc1,yc1,this.distTreshold/2.0);
		//CircularRegion q2 = new CircularRegion(xc2,yc2,this.distTreshold/2.0);
		
		//cari titik yang terletak dalam radius flock.
		//ArrayList<Location> queryResult = tree.rangeQuery(q1);
		//System.out.println("query size is: "+queryResult.size());
		//result[0].addLocations(queryResult);

		//queryResult = tree.rangeQuery(q2);
		//System.out.println("query size is: "+queryResult.size());
		//result[1].addLocations(queryResult);
		for(Location l: locs){
			BigDecimal roundedDistFlock1 = new BigDecimal(this.quadraticDist(l.getX(), l.getY(), xc1, yc1)-(flockRadius*flockRadius)).setScale(5, RoundingMode.HALF_EVEN);
			BigDecimal roundedDistFlock2 = new BigDecimal(this.quadraticDist(l.getX(), l.getY(), xc2, yc2)-(flockRadius*flockRadius)).setScale(5, RoundingMode.HALF_EVEN);
			if(roundedDistFlock1.doubleValue()<=0.1){
				result[0].addLocation(l);
			}
			if(roundedDistFlock2.doubleValue()<=0.1){
				result[1].addLocation(l);
			}
		}
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
			MinimumBoundingRectangle mbr1=activeBoxes.get(j);
			if(mbr1.getAllFlock().isEmpty())continue;
			for(Flock c:mbr1.getAllFlock()){
				insertFlock(finalFlocks,c);
			}
			//System.out.println("mbr ke: "+j);
//			for(int k=j+1;k<activeBoxes.size();k++){
//				MinimumBoundingRectangle mbr1=activeBoxes.get(j);
//				System.out.println("mbr 1 is: "+mbr1.toString()+"size: "+mbr1.getAllFlock().size());
//				System.out.println("mbr 2 is: "+activeBoxes.get(k).toString()+"size: "+activeBoxes.get(k).getAllFlock().size());
//				if(mbr1.doesOverlap(activeBoxes.get(k))){
//					System.out.println("mbr1 overlaps with mbr2");
//					for(Flock c:mbr1.getAllFlock()){
//						insertFlock(finalFlocks,c);
//					}
//				}else{
//					System.out.println("mbr1 NOT overlap with mbr2");
//					break;
//				}
//			}
		}
		return finalFlocks;
	}
	
	private void insertFlock(ArrayList<Flock> flocks,Flock currFlock){
		//for(int i = 0 ; i < flocks.size();i++){//ini biang keroknya OMG!!
		Iterator<Flock> iter = flocks.iterator();
		while(iter.hasNext()){
			//Flock d=flocks.get(i);
			Flock d=iter.next();
			//hitung binary signature
			BitSet cSign=currFlock.getBinarySignature();
			BitSet dSign=d.getBinarySignature();
			
			BitSet temp = currFlock.getBinarySignature();
			temp.and(d.getBinarySignature());

			if(temp.equals(cSign) && currFlock.dist(d)<=this.distTreshold){
				if(d.intersect(currFlock).equals(currFlock.getAllLocation())){
				//if(customEquals(d.intersect(currFlock), currFlock.getAllLocation())){
					return;
				}
			}else if(temp.equals(dSign)){
				if(currFlock.intersect(d).equals(d.getAllLocation())){
				//if(customEquals(currFlock.intersect(d), d.getAllLocation())){
					//flocks.remove(d);//hapus flock pada posisi ke-i
					iter.remove();
				}
			}
		}//kurang lebih O(N)
		
		if(!flocks.contains(currFlock)){//O(N)
			flocks.add(currFlock);//ditambahkan satu-satu di 'belakang'
		}
	}
	
	/**
	 * dikutip dari laman https://stackoverflow.com/questions/13501142/java-arraylist-how-can-i-tell-if-two-lists-are-equal-order-not-mattering
	 **/
	private boolean customEquals(ArrayList<Location> listA,ArrayList<Location> listB){
		return listA.containsAll(listB)&&listB.containsAll(listA);
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
		HashMap<Integer,ArrayList<InvertedIndexValue>> invertedIndex;
		if(flockPatterns.isEmpty()){
			for(Flock f: currFlocks){
				this.addNewFlockPattern(flockPatterns,f,currTime);
			}
		}else{
			invertedIndex=this.buildInvertedIndex(flockPatterns,currTime-1);
			HashSet<InvertedIndexValue> s1 = new HashSet<>();
			//HashSet<InvertedIndexValue> s2 = new HashSet<>();
			//System.out.println("inverted index pada waktu: "+(currTime-1)+" adalah: "+invertedIndex.toString());
			HashMap<Integer,FlockPattern> latest=new HashMap<>();
			HashSet<HashSet<Integer>> flockEntities = new HashSet<>();
			for(int i=0;i<currFlocks.size();i++){
				s1.clear();
				Flock curr = currFlocks.get(i);
				boolean noMatch = true;
				//System.out.println("Flock saat ini yg (akan) disambung: "+curr.toString());
				for(int j=0;j<curr.getAllLocation().size();j++){
					Location loc = curr.getAllLocation().get(j);
					//s2.clear();
					ArrayList<InvertedIndexValue> flocks=invertedIndex.get(loc.getEntityID());
					
					if(flocks!=null){
					for(InvertedIndexValue f: flocks){
						//System.out.println("isi entityIDSet milik curr adalah: "+curr.getEntityIDSet().toString());
						//System.out.println("isi INTERSECTION "+f.countIntersection(curr.getEntityIDSet()));
						int similar=f.countIntersection(curr.getEntityIDSet()).size();
						if(similar>=this.minEntityNum){			
							s1.add(f);
						}
					}}else{
					}
					//System.out.println("s2 berisi: "+s2.toString());
					//if(j==0){
						//s1.addAll(s2);
					//}else{
						//s1.retainAll(s2);
					//}
				}
				//System.out.println("s1 berisi: "+s1.toString());
				
				if(!s1.isEmpty()){
					Iterator<InvertedIndexValue> iter = s1.iterator();
					while(iter.hasNext()){
						InvertedIndexValue qf=iter.next();
						int patternID = qf.getPatternID();
						FlockPattern fp=flockPatterns.get(patternID);
						FlockPattern fpCopy = new FlockPattern(fp);
						fpCopy.addFlock(new Flock(curr));
						HashSet<Integer> tmp = qf.countIntersection(curr.getEntityIDSet());
						if(flockEntities.contains(tmp)){
							continue;
						}else{
							flockEntities.add(tmp);
							if(latest.containsKey(patternID)){
								fpCopy.setID(this.FLOCK_PATTERN_ID);
								latest.put(this.FLOCK_PATTERN_ID,fpCopy);
								this.FLOCK_PATTERN_ID++;
							}else{
								latest.put(patternID,fpCopy);
							}
						}
					}
				}else{
					this.addNewFlockPattern(latest,curr,currTime);
				}
			}
			if(!latest.isEmpty()){
				//flockPatterns.clear();
				flockPatterns.putAll(latest);
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
	private HashMap<Integer,ArrayList<InvertedIndexValue>> buildInvertedIndex(HashMap<Integer,FlockPattern> flockPatterns,int timestamp){
		HashMap<Integer,ArrayList<InvertedIndexValue>> invertedIndex=new HashMap<>();		
		Set<Integer> keySet = flockPatterns.keySet();
		Iterator<Integer> iter = keySet.iterator();
		while(iter.hasNext()){
			FlockPattern curr = flockPatterns.get(iter.next());
			if(curr.getEndTime()==timestamp){
				Iterator<Integer> iter2 = curr.getEntityIDSet().iterator();
				while(iter2.hasNext()){
					int currEntityID = iter2.next();
					if(invertedIndex.get(currEntityID)==null){
						ArrayList<InvertedIndexValue> list = new ArrayList<>();
						list.add(new InvertedIndexValue(curr.getID(), new HashSet<>(curr.getEntityIDSet())));
						invertedIndex.put(currEntityID,list);
					}else{
						invertedIndex.get(currEntityID).add(new InvertedIndexValue(curr.getID(), new HashSet<>(curr.getEntityIDSet())));
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