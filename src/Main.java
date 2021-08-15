import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
public class Main{
	
	public static void main(String[] args){
		
		//int[] array = new int[] { 10,50, 2,-20, 1, 15, 7,-3, 6,-1,1,1,1,1,1};
		//int[] array2 = Arrays.copyOf(array,array.length);
		//int k = 9;
		
		
		
		//int kval = QuickSelect.select(array,0,array.length-1,k);
		
		//Arrays.sort(array2);
		//for(int loc: array2){
			//System.out.print(loc+",");
		//}
		//System.out.println();
		
		//System.out.println("pivot value: "+kval);
		//for(int loc: array){
			//System.out.println(loc);
		//}
		//System.out.println();
		try{
		File file = new File("err.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setErr(ps);
		}catch(Exception e){
			
		}
		
		testCoord();
	}
	
	public static void testCoord(){
		Location[] arr = new Location[13];
		arr[0]=new Location(1,-1,4,1);
		arr[1]=new Location(2,4,2,1);
		arr[2]=new Location(3,1,3,1);
		arr[3]=new Location(4,-3,1,1);
		arr[4]=new Location(5,2,-1,1);
		arr[5]=new Location(6,-2,-2,1);
		arr[6]=new Location(7,-2,2,1);
		arr[7]=new Location(8,2,2,1);
		arr[8]=new Location(9,1,-1,1);
		arr[9]=new Location(10,4,1,1);
		arr[10]=new Location(11,-1,-1,1);
		arr[11]=new Location(12,1,-4,1);
		arr[12]=new Location(13,1,-4.02,1);
		
		
		//Arrays.sort(arr,new LocationComparatorX());
		int k = 5;
		//Location kLoc = QuickSelect.select(1,arr,0,arr.length-1,k);
		//System.out.println("k is at point: ("+kLoc.getX()+","+kLoc.getY()+")");
		ArrayList<Location> al1 = new ArrayList<>(Arrays.asList(arr));
		ArrayList<Location> al2 = new ArrayList<>();
		al2.add(new Location(8,2,2,1));
		al2.add(new Location(9,1,-1,1));
		al2.add(new Location(10,4,1,1));

		ArrayList<Location> al3 = new ArrayList<>();
		al3.add(new Location(8,2,2,1));
		al3.add(new Location(9,1,-1,1));
		al3.add(new Location(10,4,1,1));
		System.out.println("al2 equals al3 is: "+al2.equals(al3));

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