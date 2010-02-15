package Tools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.PrintJob;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Settings.*;

import Data.DataManager;
import Data.ISelectable;

public class Tools {
	
	
	public static boolean isPointInRect(int x, int y, int Rx1, int Ry1, int Rx2,
			int Ry2) {
		if ((Rx1 <= x) && (Rx2 >= x) && (Ry1 <= y) && (Ry2 >= y))
			return true;
		else
			return false;
	}
	
	
public static boolean containsLineInRect(int lx1,int ly1,int lx2,int ly2,int rx1,int ry1,int rx2,int ry2) {
		
		//Vertikale Linie 
		if (lx1 == lx2) {
			if (lx1 < rx1) return false;
			if (lx1 > rx2) return false;
			if (ly1 > ry2) return false;
			if (ly2 < ry1) return false;
			return true;
		}
		
		
		if (ly1 == ly2) {
			if (lx2 < rx1) return false;
			if (lx1 > rx2) return false;
			if (ly1 > ry2) return false;
			if (ly2 < ry1) return false;
			return true;
		}
		
		
		
		
		return false;
	}
	
	
	
/**
 * 
 *  Sort a vector using IDs
 * */
public static Vector<ISelectable> sortIDs(Vector<ISelectable> objects) {
	Vector<ISelectable> res = new Vector();
	for (int i = 0; i<objects.size(); i++) {
		ISelectable obj = objects.elementAt(i);
		System.out.print(obj.getID() +" ");
		boolean insert = false;
		for (int j = 0; j < res.size(); j++) {
			ISelectable o = res.elementAt(j);
			if (obj.getID() < o.getID()) {
				res.insertElementAt(obj,j);
				insert = true;
				break;
			}
		}
		
		if (!insert) res.add(obj);
		
	}
	
	System.out.println();
	for (int j = 0; j < res.size(); j++) {
		ISelectable o = res.elementAt(j);
		System.out.println(o.getID() + " ");
	}
		
	
	
	return res;
} 







public static boolean isPointInRect(int x, int y, Point point1, Point point2) {
	if ((point1.x <= x) && (point2.x >= x) && (point1.y <= y)
			&& (point2.y >= y))
		return true;
	else
		return false;
}

public static boolean containsRectInRect(int x1, int y1, int x2, int y2, int Sx1,
		int Sy1, int Sx2, int Sy2) {
	if (isLineInRect(x1, y1, x2, y2, Sx1, Sy1, Sx2, Sy2))
		return true;
	if (isLineInRect(x1, y2, x2, y1, Sx1, Sy1, Sx2, Sy2))
		return true;
	if (isLineInRect(x2, y1, x1, y2, Sx1, Sy1, Sx2, Sy2))
		return true;
	if (isLineInRect(x2, y2, x1, y1, Sx1, Sy1, Sx2, Sy2))
		return true;
	if (isLineInRect(Sx1, Sy1, Sx2, Sy2, x1, y1, x2, y2))
		return true;
	if (isLineInRect(Sx2, Sy1, Sx1, Sy2, x1, y1, x2, y2))
		return true;
	if (isLineInRect(Sx1, Sy2, Sx2, Sy1, x1, y1, x2, y2))
		return true;
	if (isLineInRect(Sx2, Sy2, Sx1, Sy1, x1, y1, x2, y2))
		return true;

	return false;
}

public static boolean isLineInRect(int x1, int y1, int x2, int y2, int Rx1,
		int Ry1, int Rx2, int Ry2) {
	for (int i = x1; i <= x2; i++) {
		if (x1 != x2)
			if (isPointInRect(i, y1 + (y2 - y1) * (i - x1) / (x2 - x1),
					Rx1, Ry1, Rx2, Ry2))
				return true;

	}
	if (x1 == x2)
		if (isPointInRect(x1, y2, Rx1, Ry1, Rx2, Ry2))
			return true;

	return false;
}








	
	
	
	
	public static double fPos(double xx) {
		double a = Settings.aPos;
		double b = Settings.bPos;
		double value = 0;

		if (xx < Settings.posMin)
			return 0;
		if (xx > Settings.posMax)
			return 1;

		double x = (xx - Settings.posMin)
				/ (Settings.posMax - Settings.posMin);

		if (x <= a) {
			value = a * Math.pow(x / a, b);
		} else {
			value = 1 - (1 - a) * Math.pow((1 - x) / (1 - a), b);
		}

        return value;

	}

	public static double fNeg(double xx) {
		double a = Settings.aNeg;
		double b = Settings.bNeg;
		double value = 0;

		if (xx < Settings.negMin)
			return 0;
		if (xx > Settings.negMax)
			return 1;

		double x = (xx - Settings.negMin)
				/ (Settings.negMax - Settings.negMin);

		if (x <= a) {
			value = a * Math.pow(x / a, b);
		} else {
			value = 1 - (1 - a) * Math.pow((1 - x) / (1 - a), b);
		}

		return value;

	}
	
	
	
	
	
	
	
	
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
	
	
	
	public static int getStringWidth(String s, Graphics g) {
		

		int Width = 0;
		for (int i = 0; i < s.length(); i++)
			Width += g.getFontMetrics().charWidth(s.charAt(i));
		return Width;

	};

	
	
	
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
	
	
	public static boolean compareLexicoChr(String a, String b) {
		int i = 0;

		String tA = a.replace("\"","");
		String tB = b.replace("\"","");
		if (tA.equals("X") || tA.equals("x")) tA = "23";
		if (tB.equals("X") || tB.equals("x")) tB = "23";

		if (tA.equals("Y") || tA.equals("y")) tA = "24";
		if (tB.equals("Y") || tB.equals("y")) tB = "24";
		
		if (tA.equals("NA")) return true;
		if (tB.equals("NA")) return false;
		
		
		int aa = Integer.parseInt(tA);
		int bb = Integer.parseInt(tB);
		
		if (aa < bb) return false;
		return true;
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
	
	
	
	
	
	public static void print(JFrame frame, JPanel panel) {

		try {
			PrintJob prjob = frame.getToolkit().getPrintJob(frame, null, null);
			Graphics pg = prjob.getGraphics();
			panel.paint(pg);
			pg.dispose();
			prjob.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
}
