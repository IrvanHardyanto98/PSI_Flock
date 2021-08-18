package kdtree;
import model.Location;
/**
* Kelas yang merepresentasikan algoritma Quickselect, yatiu algoritma yang
* mencari elemen urutan k-terkecil pada kumpulan elemen yang tak terurut
* @author Irvan Hardyanto, dengan mengutip pseudocode milik Tony Hoare
**/
public class QuickSelect{
	private static int partition(int mode,Location[] loc,int left,int right,int pivotIdx){
		Location pivotValue =loc[pivotIdx];
		//int pivotValue = loc[pivotIdx];
		
		Location temp = loc[pivotIdx];
		//int temp = loc[pivotIdx];
		loc[pivotIdx] =loc[right];
		loc[right]=temp;
		
		int strIdx = left;
		for(int i = left;i<=right-1;i++){
			if(isLess(mode,loc[i],pivotValue)){
			//if(loc[i].getX()<pivotValue.getX()){
			//if(loc[i]<pivotValue){
				temp = loc[strIdx];
				loc[strIdx]=loc[i];
				loc[i]=temp;
				strIdx++;
			}
		}
		
		temp = loc[right];
		loc[right]=loc[strIdx];
		loc[strIdx]=temp;
		
		return strIdx;
	}
	
	/**
	*@param mode 0 untuk sumbu X, 1 untuk sumbu Y
	**/
	private static boolean isLess(int mode,Location l1,Location l2){
		if(mode==0){
			return l1.getX() < l2.getX();
		}else{
			return l1.getY() < l2.getY();
		}
	}
	
	public static Location select(int mode,Location[] loc,int left,int right,int k){
		if(left==right){
			return loc[left];
		}
		int pivotIdx= left +(int)Math.floor(Math.random()*(right-left+1));
		//int pivotIdx= (left+right)/2;
		pivotIdx = partition(mode,loc,left,right,pivotIdx);
		
		if(k==pivotIdx){
			return loc[k];
		}else if(k < pivotIdx){
			return select(mode,loc,left,pivotIdx-1,k);
		}else{
			return select(mode,loc,pivotIdx+1,right,k);
		}
	}
}