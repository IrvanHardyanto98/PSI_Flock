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

//Asumsi yang dibuat (18-08-2021)
public class Main {

	private static final int FIELD_NUM = 4;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("PENCARIAN FLOCK PATTERN MENGGUNAKAN ALGORITMA PSI");

		int minEntityNum = 0;
		int minDuration = 0;
		int seed = 0;
		double distTreshold = 0.0;
		System.out.println("Masukkan Parameter");
		System.out.print("jumlah entitas minimal: ");
		minEntityNum = sc.nextInt();
		System.out.print("durasi minimal flock: ");
		minDuration = sc.nextInt();
		System.out.print("batasan jarak: ");
		distTreshold = sc.nextDouble();
		System.out.print("nilai seed: ");
		seed = sc.nextInt();
		sc.nextLine();
		ArrayList<Location> points = new ArrayList<>();
		AlgoPSI problemInstance = new AlgoPSI(minEntityNum, distTreshold, minDuration, seed);
		int entityID;
		int timestamp = 0;
		double x;
		double y;
		long totalTime = 0;
		while (true) {
			points.clear();

			System.out.println("Pastikan Format setiap baris pada file input adalah:<id_entitas>,<waktu>,<koordinat-x>,<koordinat-Y>");
			System.out.print("Enter path to file: ");
			String filePath = sc.nextLine();
			//String filePath = "t1_mini.txt";
			File inputFile = new File(filePath);

			try {
				//file reader -> khusus file yang isinya teks
				FileReader fr = new FileReader(inputFile);
				BufferedReader br = new BufferedReader(fr);

				//untuk pesan error (KALAU PAKE CMD)
				//File file = new File("err.txt");
				//FileOutputStream fos = new FileOutputStream(file);
				//PrintStream ps = new PrintStream(fos);
				//System.setErr(ps);
				//baca satu per satu
				String line;
				ArrayList<Trajectory> trajectories = new ArrayList<>();
				while ((line = br.readLine()) != null) {
					String[] s = line.split("\\,");
					//pastikan split pas jadi 4
					entityID = Integer.parseInt(s[0]);
					timestamp = Integer.parseInt(s[1]);
					x = Double.parseDouble(s[2]);
					y = Double.parseDouble(s[3]);

					Location currentPoint = new Location(entityID, x, y, timestamp);
					points.add(currentPoint);
				}
				long start = System.currentTimeMillis();
				LocalDateTime startDate = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));

				problemInstance.findAllFlockPattern(timestamp, points);
				long end = System.currentTimeMillis();
				totalTime += (end - start);

				System.out.println("durasi pencarian : " + (end - start) + " (milisekon)");;
				String txtOutput = "Flock pattern yang ditemukan: \n";
				txtOutput += "Flock pattern yang ditemukan: \n";
				txtOutput += "\n";
				txtOutput += "Parameter yang digunakan:\n";
				txtOutput += "Jumlah entitas minimal: " + minEntityNum + "\n";
				txtOutput += "Batasan jarak: " + distTreshold + "\n";
				txtOutput += "Durasi flock minimal: " + minDuration + "\n";

				System.out.print("hentikan program? Y/N: ");
				String stat = sc.nextLine();
				if (stat.equals("Y")) {
					HashMap<Integer, FlockPattern> patterns = problemInstance.getAllFlockPattern();
					PDFWriter pw = new PDFWriter("FlockPatterns.pdf", "Pencarian Flock Pattern Menggunakan Algortima PSI");
					pw.addParagraph("Ringkasan Hasil Pencarian");
					String[][] runAttr = new String[3][2];
					runAttr[0][0] = "Waktu mulai";
					runAttr[0][1] = startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy H:m:s"));
					runAttr[1][0] = "Durasi Pencarian";
					runAttr[1][1] = Long.toString(totalTime) + " milisekon";
					runAttr[2][0] = "Jumlah Flock Pattern yang ditemukan";
					runAttr[2][1] = Integer.toString(patterns.size());// disini dia ga bisa bedain mana yang memenuhi syarat
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
					
					StringTreeNode root = new StringTreeNode("Flock Pattern yang ditemukan: ");
					while (iter.hasNext()) {
						int idx2=0;
						FlockPattern p = patterns.get(iter.next());
						root.setChildren(p.toString());
						//txtOutput += (p.toString() + "\n");
						//txtOutput += ("Flocks:\n");
						for (Flock f : p.getAllFlock()) {						
							String flockData = f.toString();
							root.getChildren(idx1).setChildren(flockData);
							for(Location l: f.getAllLocation()){
								root.getChildren(idx1).getChildren(idx2).setChildren(l.toString());
							}
							idx2++;
						}
						idx1++;
					}
					pw.addNestedList(root);
					pw.closeDocument();
					System.out.println("output file is written at: ");
					break;
				}
			} catch (FileNotFoundException e) {
				System.out.println("ERROR! file not found");
			} catch (IOException e) {
				System.out.println("ERROR! could not read file");
			}
		}
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
