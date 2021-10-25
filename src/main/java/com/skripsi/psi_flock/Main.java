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
import com.skripsi.psi_flock.model.Hasher;
import com.skripsi.psi_flock.model.InvertedIndexValue;
import com.skripsi.psi_flock.model.Location;
import com.skripsi.psi_flock.model.Trajectory;
import com.skripsi.psi_flock.pdf.PDFWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.BitSet;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

//Asumsi yang dibuat (18-08-2021)
public class Main {

	private static final int FIELD_NUM = 4;
	private static int maxTime;
	
	public static void main(String[] args) {
		try{
		File file = new File("err.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setErr(ps);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		Scanner sc = new Scanner(System.in);
		System.out.println("PENCARIAN FLOCK PATTERN MENGGUNAKAN ALGORITMA PSI");

		//Baca Parameter Pencarian
		int minEntityNum = 0;
		int minDuration = 0;
		int seed = 0;
		double distTreshold = 0.0;
		int startTime = 0;
		int endTime = 0;
		System.out.println("Masukkan Parameter Pencarian");
		System.out.print("Masukkan jumlah entitas minimal: ");
		minEntityNum = sc.nextInt();
		//System.out.print("Masukkan waktu mulai: ");
		//startTime = sc.nextInt();
		//System.out.print("Masukkan waktu akhir: ");
		//endTime = sc.nextInt();
		System.out.print("Masukkan durasi minimal flock: ");
		minDuration = sc.nextInt();
		System.out.print("Masukkan batasan jarak: ");
		distTreshold = sc.nextDouble();
		//System.out.print("Masukkan nilai seed: ");
		seed = 1546789124;
		sc.nextLine();

		//AlgoPSI problemInstance = new AlgoPSI(startTime,endTime,minEntityNum, distTreshold, minDuration, seed);
		AlgoPSI problemInstance = new AlgoPSI(minEntityNum, distTreshold, minDuration, seed);
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

		System.out.println("MAX TIME INSTANCE IS: "+maxTime);
		problemInstance.findAllFlockPattern(trajectories,maxTime);
		//problemInstance.findAllFlockPattern(trajectories);
		long end = System.currentTimeMillis();
		totalTime += (end - start);
		
		HashMap<Integer, FlockPattern> patterns = problemInstance.getAllFlockPattern();
		
		TXTWriter tw = new TXTWriter(fileName.split("\\.")[0]+"-"+minEntityNum+"-"+distTreshold+"-"+minDuration+".txt");
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

			HashSet<Integer> temp=p.getEntityIDSet();
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
	private static boolean customEquals(ArrayList<Location> listA,ArrayList<Location> listB){
		return listA.containsAll(listB)&&listB.containsAll(listA);
	}
	public static Trajectory[] readCSV(String fileName){
		maxTime = Integer.MIN_VALUE;
		Trajectory[] trajectories = new Trajectory[3000];
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
				line=line.trim();
				String[] cols=line.split(",");
				timestamp = Integer.parseInt(cols[1]);
				if(timestamp>maxTime)maxTime=timestamp;
				BigDecimal xRaw= new BigDecimal(Double.parseDouble(cols[2])).setScale(3, RoundingMode.HALF_EVEN);
				BigDecimal yRaw= new BigDecimal(Double.parseDouble(cols[3])).setScale(3, RoundingMode.HALF_EVEN);
				//x = Double.parseDouble(cols[2]);
				x = xRaw.doubleValue();
				//y = Double.parseDouble(cols[3]);
				y = yRaw.doubleValue();
				if(Integer.parseInt(cols[0])!=entityID){
					entityID= Integer.parseInt(cols[0]);
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
}
