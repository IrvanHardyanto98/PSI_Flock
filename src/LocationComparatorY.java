import java.util.Comparator;
public class LocationComparatorY implements Comparator<Location>{
	@Override
	public int compare(Location o1, Location o2)
    {
        if(o1.getY() < o2.getY()){
			return -1;
		}else if(o1.getY() > o2.getY()){
			return 1;
		}else{
			if(o1.getX()<o2.getX())return -1;
			if(o1.getX()>o2.getX())return 1;
			return 0;
		}
    }
}