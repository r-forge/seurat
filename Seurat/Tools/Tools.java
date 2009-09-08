package Tools;

import java.util.Vector;

import Data.DataManager;

public class Tools {
	public static boolean doesntContain(Vector<String> buffer, String word) {
		for (int i = 0; i < buffer.size(); i++) {
			if (buffer.elementAt(i).equals(word))
				return false;
		}
		return true;
	}
	
	
	public static Vector<String> sortBuffer(Vector<String> buffer) {
		if (isNumBuffer(buffer)) return sortByNum(buffer); 
		return sortByLexico(buffer);
	}
	
	
	
	public static boolean isNumBuffer(Vector<String> buffer) {
		for (int i = 0; i < buffer.size(); i++) {
			if (!buffer.elementAt(i).contains("NA")) {
				try {
					new Double(buffer.elementAt(i));
				}
				catch (Exception e){
					return false;
				}
			} 
		}
		return true;
	}
	
	
	public static Vector<String> sortByLexico(Vector<String> buffer) {

		Vector<String> Temp = new Vector();
		for (int i = 0; i < buffer.size(); i++) {
			String s = buffer.elementAt(i);
			int j = 0;
			while (j < Temp.size()
					&& compareLexico(s, Temp.elementAt(j))) {
				j++;
			}
			Temp.insertElementAt(s, j);

		}
        return Temp;
	}
	
	
	
	public static Vector<String> sortByNum(Vector<String> buffer) {

		Vector<String> Temp = new Vector();
		for (int i = 0; i < buffer.size(); i++) {
			String s = buffer.elementAt(i);
			int j = 0;
			while (j < Temp.size()
					&& compareNum(s, Temp.elementAt(j))) {
				j++;
			}
			Temp.insertElementAt(s, j);

		}
        return Temp;
	}
	
	

	public static boolean compareLexico(String a, String b) {
		int i = 0;

		while (a.length() > i && b.length() > i) {
			if (a.charAt(i) > b.charAt(i))
				return true;
			if (a.charAt(i) < b.charAt(i))
				return false;
			i++;

		}

		return false;
	}
	
	
	
	public static boolean compareNum(String a, String b) {
		int i = 0;
		
		 double aD = 0;  
	        double bD =0;
		
		
		if (a.contains("NA")) aD = DataManager.NA;
		else aD = new Double(a); 
		
		if (b.contains("NA")) bD = DataManager.NA;
		else bD = new Double(b); 
		
      

		return aD>bD;
	}
	
	
	
	
	
}
