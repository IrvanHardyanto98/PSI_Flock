package com.skripsi.psi_flock;

import com.skripsi.psi_flock.pdf.StringTreeNode;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import com.skripsi.psi_flock.kdtree.CircularRegion;
import com.skripsi.psi_flock.kdtree.KDTree;
import com.skripsi.psi_flock.model.Flock;
import com.skripsi.psi_flock.model.FlockPattern;
import com.skripsi.psi_flock.model.Location;
import com.skripsi.psi_flock.model.Trajectory;
import com.skripsi.psi_flock.pdf.PDFWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

//Asumsi yang dibuat (18-08-2021)
public class Main {

	private static final int FIELD_NUM = 4;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("PENCARIAN FLOCK PATTERN MENGGUNAKAN ALGORITMA PSI");

		//Baca Parameter Pencarian
		int minEntityNum = 0;
		int minDuration = 0;
		int seed = 0;
		double distTreshold = 0.0;
		int startTime = 0;
		System.out.println("Masukkan Parameter Pencarian");
		System.out.print("Masukkan jumlah entitas minimal: ");
		minEntityNum = sc.nextInt();
		System.out.print("Masukkan Interval waktu mulai: ");
		startTime = sc.nextInt();
		System.out.print("Masukkan durasi minimal flock: ");
		minDuration = sc.nextInt();
		System.out.print("Masukkan batasan jarak: ");
		distTreshold = sc.nextDouble();
		System.out.print("Masukkan nilai seed: ");
		seed = sc.nextInt();
		sc.nextLine();

		AlgoPSI problemInstance = new AlgoPSI(startTime,minEntityNum, distTreshold, minDuration, seed);
		int entityID;
		int timestamp = 0;
		double x;
		double y;
		long totalTime = 0;

		String fileName;
		System.out.print("\nMasukkan nama file: ");

		fileName = sc.nextLine();
		System.out.print("\nPencarian flock pada interval waktu ["+startTime+","+(startTime+minDuration-1)+"] dimulai");

		

		long start = System.currentTimeMillis();
		//baca data lintasan
		Trajectory[] trajectories = readCSV(fileName);
		//XMLReader reader = new XMLReader(fileName);
		//Trajectory[] trajectories = reader.readTrajectory();

		
		LocalDateTime startDate = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));

		problemInstance.findAllFlockPattern(trajectories);
		long end = System.currentTimeMillis();
		totalTime += (end - start);
		
		HashMap<Integer, FlockPattern> patterns = problemInstance.getAllFlockPattern();
		
		TXTWriter tw = new TXTWriter(fileName.split("\\.")[0]+".txt");
		tw.addLine(Integer.toString(patterns.size()));
		
		PDFWriter pw = new PDFWriter("FlockPatterns-REV02"+fileName+"-"+minEntityNum+",("+startTime+","+(startTime+minDuration-1)+"),"+distTreshold+".pdf", "Pencarian Flock Pattern Menggunakan Algortima PSI");
		pw.addParagraph("Ringkasan Hasil Pencarian");
		String[][] runAttr = new String[4][2];
		
		runAttr[0][0] = "Waktu mulai";
		runAttr[0][1] = Integer.toString(startTime);
		runAttr[1][0] = "Waktu akhir";
		runAttr[1][1] = Integer.toString(startTime+minDuration-1);
		runAttr[2][0] = "Durasi Pencarian";
		runAttr[2][1] = Long.toString(totalTime) + " milisekon";
		runAttr[3][0] = "Jumlah Flock Pattern yang ditemukan";
		runAttr[3][1] = Integer.toString(patterns.size());// disini dia ga bisa bedain mana yang memenuhi syarat
		pw.addTable(runAttr);
		pw.addBlankLine();
		pw.addParagraph("Parameter yang digunakan");
		String[][] params = new String[4][2];
		params[0][0] = "Jumlah entitas minimal";
		params[0][1] = Integer.toString(minEntityNum);
		params[1][0] = "Batasan jarak";
		params[1][1] = Double.toString(distTreshold);
		params[2][0] = "Durasi flock minimal";
		params[2][1] = Integer.toString(minDuration);
		params[3][0] = "Nilai seed yang digunakan";
		params[3][1] = Integer.toString(seed);
		pw.addTable(params);
		pw.addBlankLine();

		pw.addParagraph("Flock Pattern yang ditemukan: ");
		Set<Integer> keys = patterns.keySet();
		Iterator<Integer> iter = keys.iterator();

		String[] arrFlockPattern = new String[patterns.size()];
		int idx1 = 0;

//		StringTreeNode root = new StringTreeNode("Ringkasan Flock Pattern yang ditemukan: ");
		pw.addParagraph("Ringkasan Flock Pattern yang ditemukan: ");
		long startPDF = System.currentTimeMillis();
		while (iter.hasNext()) {
			//int idx2 = 0;
			FlockPattern p = patterns.get(iter.next());
			String s = p.toString();

			HashSet<Integer> temp=p.getLastFlock().getEntityIDSet();
			Iterator<Integer> iter2 = temp.iterator();
			
			
			String txtOutput="";
			s+="Entitas-entitas yang berada di dalam flock pattern adalah: ";
			boolean first=true;
			while(iter2.hasNext()){
				if(first){
					first=false;
				}else{
					s+=",";
					txtOutput+=" ";
				}
				Integer eID = iter2.next();
				s+=eID;
				txtOutput+=eID;
			}
			tw.addLine(txtOutput);
			tw.addLine(p.getStartTime()+" "+p.getEndTime());
			arrFlockPattern[idx1]=s+"\n";
			idx1++;
//			root.setChildren(p.toString());
//			for (Flock f : p.getAllFlock()) {
//				String flockData = f.toString();
//				root.getChildren(idx1).setChildren(flockData);
//				for (Location l : f.getAllLocation()) {
//					root.getChildren(idx1).getChildren(idx2).setChildren(l.toString());
//				}
//				idx2++;
//			}
//			idx1++;
			//System.out.println("s: "+s);
		}
//		pw.addNestedList(root);
		pw.addBasicList(true, arrFlockPattern);
		pw.closeDocument();
		tw.closeFile();
		System.out.println("durasi menulis pdf: "+(System.currentTimeMillis()-startPDF));
	}
	
	public static Trajectory[] readCSV(String fileName){
		Trajectory[] trajectories = new Trajectory[1000];
		int idx=-1;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			boolean first = true;
			int entityID=0;
			int timestamp;
			double x;
			double y;
			while ((line = br.readLine()) != null){
				if(first){
					first=false;
					continue;
				}
				String[] cols=line.split(",");
				timestamp = Integer.parseInt(cols[2]);
				x = Double.parseDouble(cols[3]);
				y = Double.parseDouble(cols[4]);
				if(Integer.parseInt(cols[1])!=entityID){
					entityID= Integer.parseInt(cols[1]);
					idx++;
					trajectories[idx]=new Trajectory(entityID, timestamp);
					trajectories[idx].addLocation(x,y,timestamp);
				}else{
					trajectories[idx].addLocation(x,y,timestamp);
				}
			}
			br.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error! File tidak ditemukan");
			ex.printStackTrace();
		}catch(IOException ex){
			System.out.println("Error! File tidak bisa dibaca!");
			ex.printStackTrace();
		}
		return Arrays.copyOf(trajectories, (idx+1));
	}

	public static void testCoord() {
		Location[] arr = new Location[13];
		arr[0] = new Location(1, -1, 4, 1);
		arr[1] = new Location(2, 4, 2, 1);
		arr[2] = new Location(3, 1, 3, 1);
		arr[3] = new Location(4, -3, 1, 1);
		arr[4] = new Location(5, 2, -1, 1);
		arr[5] = new Location(6, -2, -2, 1);
		arr[6] = new Location(7, -2, 2, 1);
		arr[7] = new Location(8, 2, 2, 1);
		arr[8] = new Location(9, 1, -1, 1);
		arr[9] = new Location(10, 4, 1, 1);
		arr[10] = new Location(11, -1, -1, 1);
		arr[11] = new Location(12, 1, -4, 1);
		arr[12] = new Location(13, 1, -4.02, 1);

		//Arrays.sort(arr,new LocationComparatorX());
		int k = 5;
		//Location kLoc = QuickSelect.select(1,arr,0,arr.length-1,k);
		//System.out.println("k is at point: ("+kLoc.getX()+","+kLoc.getY()+")");
		ArrayList<Location> al1 = new ArrayList<>(Arrays.asList(arr));
		ArrayList<Location> al2 = new ArrayList<>();
		al2.add(new Location(8, 2, 2, 1));
		al2.add(new Location(9, 1, -1, 1));
		al2.add(new Location(10, 4, 1, 1));

		ArrayList<Location> al3 = new ArrayList<>();
		al3.add(new Location(8, 2, 2, 1));
		al3.add(new Location(9, 1, -1, 1));
		al3.add(new Location(10, 4, 1, 1));
		System.out.println("al2 equals al3 is: " + al2.equals(al3));

		//al2.retainAll(al1);
		//System.out.println("al2 size is: "+al2.size()+"\n");
		//for(Location x: al2){
		//System.out.print(x.toString());
		//}
		//KDTree tree = new KDTree();
		//long start = System.currentTimeMillis();
		//tree.buildKDTree(arr);
		//long end = System.currentTimeMillis();
		//System.out.println("BUILD TREE TIME: "+(end-start));
		//tree.traverse();
		//CircularRegion cR = new CircularRegion(3,1,2);
		//OrthogonalRegion oR = new OrthogonalRegion(-3,-2,-1,4);
		//OrthogonalRegion oR2 = new OrthogonalRegion(1,-1,4,3);
		//OrthogonalRegion oR3 = new OrthogonalRegion(1,-4,2,-1);
		//System.out.println(cR.toString());
		//ArrayList<Location> res = tree.rangeQuery(cR);
		//System.out.println("res len: "+res.size());
		//for(Location loc: res){
		//System.out.print("("+loc.getX()+","+loc.getY()+") ");
		//}
	}
}
