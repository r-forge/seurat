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
import Data.MyColor;

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
	
	//System.out.println();
	for (int j = 0; j < res.size(); j++) {
		ISelectable o = res.elementAt(j);
	//	System.out.println(o.getID() + " ");
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

	// Merges two verctors
	public static Vector mergeVectors(Vector v1, Vector v2) {
		Vector v = new Vector();
		
		for (int i = 0; i < v1.size(); i++) {
			v.add(v1.elementAt(i));
		}
		
		for (int i = 0; i < v2.size(); i++) {
			v.add(v2.elementAt(i));
		}
		return v;
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
	
	
	
	
	public static MyColor convertXYZtoRGB(MyColor color) {
		
		double x = color.x;
		double y = color.y;
		double z = color.z;
		
		//double r = 3.240479*x -1.537150*y -0.498535*z; 
		//double g = -0.969256*x+ 1.875992*y+ 0.041556*z;
	    //double b = 0.055648*x -0.204043*y+ 1.057311*z; 
		
		
		double r = 3.24071 * x + (-1.53726) * y + (-0.498571) * z;
		double g =(-0.969258) * x + 1.87599 * y + 0.0415557 * z;
	double 	b = 0.0556352 * x + (-0.203996) * y + 1.05707 * z ;
		
		
		return new MyColor(r/100,g/100,b/100);
	}
	
	
	
	
	
	public static MyColor convertLUVtoXYZ(MyColor color) {
		/*double Yn = 100;
		
		double un = 0.2009;
		double vn = 0.4610;
	
		*/
		double L = color.x;
		double u = color.y;
		double v = color.z;
		
			/*
		double us = (u / 13) / L + un;
		double vs = (v / 13) / L + vn;
		
		double Y = Yn * L * 3*3*3/29/29/29;
		if (L>8)
			Y = Yn*(L+16)*(L+16)*(L+16)/116/116/116;
		
		double X = (-9)*Y*us/ (us*(us-4)- us*vs);
		double Z = (9*Y - 15*Y*vs -X*vs)/3/vs;
		*/
		
		
	
		double Xr = 95.047, Yr = 100.000, Zr = 108.883 ;
		
		double v0 = 9*Yr / (Xr + 15*Yr + 3*Zr);
		
		double u0 = 4*Xr /(Xr + 15*Yr + 3*Zr);
		
		double k = 903.3;
		
		double e = 0.008856;
		
		double Y = L/k;
		if (L > k*e) Y = (L+16)*(L+16)*(L+16)/116/116/116;
		
		double a = (52*L/(u + 13*L*u0) - 1)/3;
		
		double b = - 5 * Y;
		
		double c = - (double)1/3;
		
		double d = Y* (39*L/(v + 13*L*v0) - 5);
		
		double X = (d-b)/(a-c); 
		
		double Z = X*a + b;
		
		return new MyColor(X*100,Y*100,Z*100);
	}
	
	
	public static MyColor convertHLCtoLUV(MyColor c) {
	      return new MyColor(c.x, c.y*Math.cos(c.z*Math.PI/180), c.y*Math.sin(c.z*Math.PI/180));	
	}
	
	
	
	public static MyColor convertHLCtoRGB(MyColor c) {
          return convertXYZtoRGB(convertLUVtoXYZ(convertHLCtoLUV(c)));
	}
	
	
	
	
	
	
	
	
	public static MyColor rgb2luv(int R, int G, int B) {
		//http://www.brucelindbloom.com
		
		float rf, gf, bf;
		float r, g, b, X_, Y_, Z_, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float L;
		float eps = 216.f/24389.f;
		float k = 24389.f/27.f;
		 
	//	float Xr = 0.964221f;  // reference white D50
		//float Yr = 1.0f;
		//float Zr = 0.825211f;
		 
		
		float Xr = 95.047f;  // reference white D50
		float Yr = 100.000f;
		float Zr = 108.8831f;
		
		// RGB to XYZ
		 
		r = R/255.f; //R 0..1
		g = G/255.f; //G 0..1
		b = B/255.f; //B 0..1
		   
		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r/12;
		else
			r = (float) Math.pow((r+0.055)/1.055,2.4);
		
		if (g <= 0.04045)
			g = g/12;
		else
			g = (float) Math.pow((g+0.055)/1.055,2.4);
		 
		if (b <= 0.04045)
			b = b/12;
		else
			b = (float) Math.pow((b+0.055)/1.055,2.4);
		
		
		X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
		Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
		Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;
		
		// XYZ to Luv
		
		float u, v, u_, v_, ur_, vr_;
						
		u_ = 4*X / (X + 15*Y + 3*Z);
		v_ = 9*Y / (X + 15*Y + 3*Z);
				 
		ur_ = 4*Xr / (Xr + 15*Yr + 3*Zr);
		vr_ = 9*Yr / (Xr + 15*Yr + 3*Zr);
		  
		yr = Y/Yr;
		
		if ( yr > eps )
			L =  (float) (116*Math.pow(yr, 1/3.) - 16);
		else
			L = k * yr;
		 
		u = 13*L*(u_ -ur_);
		v = 13*L*(v_ -vr_);
		
		return new MyColor((2.55*L + .5),(u + .5),(v + .5));        
	} 
	
	
	
	
	public static void main(String [] args) {
	    MyColor c = convertHLCtoRGB(new MyColor(80,63,220));
	    System.out.println(c.x + "  " + c.y + "   "+ c.z);
		
		//	System.out.println(convertHLCtoXYZ(new MyColor(120,20,60)).x + "  "+convertHLCtoXYZ(new MyColor(120,20,60)).y+ "   "+convertHLCtoXYZ(new MyColor(120,20,60)).z);
	}
	
	
	
	
	
	
	//public static MyColor convertHLCtoRGB(MyColor color) {
		//return convertHLCtoXYZ(convertXYZtoRGB(color));
	//}
	
	
	
	
	
	
	
	
	
	
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	

	

	public static String round100(double zahl) {
		if (zahl == Math.round(zahl))
			return "" + zahl;
		return "" + (double) Math.round(zahl * 100) / 100;
	}

	

	public static String cutLabels(String s, int availablePlace, Graphics g) {
		s = s.replaceAll("\"", "");
		String ss = cutLabelsHelp(s, availablePlace, g);
		if (ss.length() < 5)
			return ss;

		int Width = getStringSpace(ss,g);
		if (Width < availablePlace)
			return ss;

		while (Width > availablePlace) {
			Width = 0;
			ss = ss.substring(0, ss.length() - 1);
			for (int i = 0; i < ss.length(); i++)
				Width += g.getFontMetrics().charWidth(ss.charAt(i));

		}

		return ss;

	};
	
	
	public static int getStringSpace(String s,Graphics g) {
		int Width = 0;
		for (int i = 0; i < s.length(); i++)
			Width += g.getFontMetrics().charWidth(s.charAt(i));
		return Width;
	}
	
	

	public static String cutLabelsHelp(String s, int availablePlace, Graphics g) {
		int Width = getStringSpace(s,g);
		if (Width < availablePlace)
			return s;

		Width = 0;

		s = s.replaceAll("ck", "c");
		String cutS = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// if (c != 'e' && c != 'u' && c != 'i' && c != 'o' && c != 'ü'
			// && c != 'a' && c != 'ö' && c != 'ä' && c != 'y')
			cutS += c;
		}

		return cutS;

	}
	
	
	
	
}
