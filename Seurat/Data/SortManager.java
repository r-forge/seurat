package Data;
import java.util.*;

public class SortManager {

	public static int[] sort(String[] data) {

		int[] order = new int[data.length];
		Vector<Vector> dataToSort = new Vector();
		for (int i = 0; i < data.length; i++) {
			Vector v = new Vector();
			v.add(data[i]);
			v.add(i);
			dataToSort.add(v);
		}

		Collections.sort(dataToSort, new StringComparator());

		for (int i = 0; i < data.length; i++) {
			order[new Integer((Integer) dataToSort.elementAt(i).elementAt(1))] = i;
		}
		return order;
	}

	public static int[] sort(double[] data) {

		int[] order = new int[data.length];
		Vector<Vector> dataToSort = new Vector();
		for (int i = 0; i < data.length; i++) {
			Vector v = new Vector();
			v.add(data[i]);
			v.add(i);
			dataToSort.add(v);
		}

		Collections.sort(dataToSort, new DoubleComparator());

		for (int i = 0; i < data.length; i++) {
			order[new Integer((Integer) dataToSort.elementAt(i).elementAt(1))] = i;
		}
		return order;
	}
	
	
	
	public static int[] sort(int [] data) {

		int[] order = new int[data.length];
		Vector<Vector> dataToSort = new Vector();
		for (int i = 0; i < data.length; i++) {
			Vector v = new Vector();
			v.add(data[i]);
			v.add(i);
			dataToSort.add(v);
		}

		Collections.sort(dataToSort, new IntegerComparator());

		for (int i = 0; i < data.length; i++) {
			order[new Integer((Integer) dataToSort.elementAt(i).elementAt(1))] = i;
		}
		return order;
	}
	
	

}

class StringComparator implements Comparator {

	public int compare(Object a, Object b) {
		return ((String) (((Vector) a).elementAt(0)))
				.compareTo((String) (((Vector) b).elementAt(0)));
	}
}

class DoubleComparator implements Comparator {
	public int compare(Object a, Object b) {
		return (((Double) ((Vector) a).elementAt(0)))
				.compareTo(((Double) ((Vector) b).elementAt(0)));

	}
}

class IntegerComparator implements Comparator {
	public int compare(Object a, Object b) {
		if ((((Integer) ((Vector) a).elementAt(0)))
				>(((Integer) ((Vector) b).elementAt(0)))) return -1;
		else return 0;

	}
}
