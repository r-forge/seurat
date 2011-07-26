package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Data.Bicluster;
import Data.Biclustering;
import Data.ClusterNode;
import Data.Clustering;
import Data.ISelectable;
import Data.MyColor;
import Data.Permutation;
import Settings.Settings;
import Tools.Tools;

public class Biconf extends JFrame{
	
	
Biclustering biclust1,biclust2;
	
BiPanel panel;

Seurat seurat;

int MaxWidth = 700, MaxHeight = 700;
	
double [][] matrix; 

int [] orderCols,orderRows;


int maxC = -1, maxR = -1;

	public Biconf(Seurat seurat,Biclustering biclust1,Biclustering biclust2) {
		super("Biconfusionsmatrix");
		this.biclust1 = biclust1;
		this.biclust2 = biclust2;
		this.seurat = seurat;
		
	
		
		
		permuteMatrix();
		
		panel = new BiPanel(this,biclust1,biclust2);
		
		this.getContentPane().add(new JScrollPane(panel));
		
		setLocation(400,0);
		setSize();
	    panel.calculateAbstande();
		this.addKeyListener(panel);
	    
		this.setVisible(true);
	}
	
	
public void permuteMatrix() {
	matrix = new double [biclust1.biclusters.size()][biclust2.biclusters.size()];
	
	for (int i = 0; i < matrix.length; i++){
	for (int j = 0; j < matrix [0].length; j++){
	
		Bicluster inter = intersect(biclust1.biclusters.elementAt(i),biclust2.biclusters.elementAt(j));
		Bicluster union = union(biclust1.biclusters.elementAt(i),biclust2.biclusters.elementAt(j));
		
		matrix [i][j] = (double)(inter.columns.size()*inter.rows.size())/(union.columns.size()*union.rows.size()); 
	//	System.out.print(matrix [i][j]+ " ");
	}	
	//System.out.println();
	}
	
	orderCols = new int [matrix.length];
	for (int i = 0; i < orderCols.length; i++) {
		orderCols [i] = i;
	}
	
	
	orderRows = new int [matrix[0].length];
	for (int i = 0; i < orderRows.length; i++) {
		orderRows [i] = i;
	}
    	
	  
	
	
	
	double Crit = calcCrit(orderCols,orderRows);
	double temp;
	System.out.println("Start Crit: " + Crit);
	while (Crit<(temp = permute())) {
		Crit = temp;
	    
	}
	System.out.println("End Crit: " + Crit);
	

	
	Vector<Bicluster> b1 = new Vector();
	for (int i = 0; i<orderCols.length; i++) {
		b1.add(biclust1.biclusters.elementAt(orderCols [i]));
	}
	biclust1.biclusters = b1;
	
	
	Vector<Bicluster> b2 = new Vector();
	for (int i = 0; i<orderRows.length; i++) {
		b2.add(biclust2.biclusters.elementAt(orderRows [i]));
	}
	biclust2.biclusters = b2;
	

	repaint();
	
	
	
	paint(this.getGraphics());
	repaint();
	
	
	
	
	
	
	
	
}	







public double permute() {
	
	Vector<int []> colPermutations = getAllPermutations(orderCols);
	Vector<int []> rowPermutations = getAllPermutations(orderRows);
	int maxI = colPermutations.size()-1;
	int maxJ = rowPermutations.size()-1;
	
	
			
	double criterion = calcCrit(orderCols,orderRows);
	
   System.out.println(" Vor  ->  " + criterion);

	for (int i = 0; i < colPermutations.size(); i++) {
	for (int j = 0; j < rowPermutations.size(); j++) {
		
			int [] pC = colPermutations.elementAt(i);
			int [] pR = rowPermutations.elementAt(j);
		    
			double crit = calcCrit(pC,pR);
			
			if (crit > criterion) {
				criterion = crit;
				maxI = i;
				maxJ = j;
			}
	}	
	}
	
	
	orderCols = colPermutations.elementAt(maxI); 
	orderRows = rowPermutations.elementAt(maxJ); 
	System.out.println(" Nach  ->  " + criterion);
	
	return criterion;

}







public Vector<int []> getAllPermutations(int [] order) {
	Vector per = new Vector();
   
	for (int i = 0; i < order.length; i++) {
	for (int j = i+1; j < order.length; j++) {

		int [] o = new int [order.length];
		
		for (int k = 0; k < i; k++) o [k] = order [k];
		for (int k = i; k < j; k++) o [k] = order [k+1];
		o [j] = order [i];
		for (int k = j+1; k < order.length; k++) o [k] = order [k];
        
		
		per.add(o);
		
		
		}
	}
	
	int [] o = new int [order.length];
	
	for (int k = 0; k < order.length; k++) o [k] = order [k];
	per.add(o);
	
	
	return per;
}











public double calcCrit(int [] orderCols, int [] orderRows) {
	double crit = 0;
	/*
	
	
	int [] sum = new int  [matrix.length]; 
	
	for (int j = 0; j < matrix [0].length-1; j++) {
	int temp  = 0;
	for (int i = matrix.length-1; i >0; i--) {
		temp+= matrix [i][j];
		sum  [i] +=  temp;
		
		crit+= sum [i] * matrix [i-1][j+1];
	}
	}
	//System.out.println(crit2 + "   ");
	*/
	
	for (int i = 0; i < matrix.length; i++) {
	for (int j = 0; j < matrix[0].length; j++) {
			for (int ii = i; ii < matrix.length; ii++) {
			for (int jj = j; jj < matrix[0].length; jj++) {
					crit+=matrix [orderCols[i]][orderRows[j]]*matrix [orderCols[ii]][orderRows[jj]];
				}
			}
		}
	}
	
	
	return crit;
	
	
	
}








public Bicluster union(Bicluster b1, Bicluster b2) {
     Vector<ISelectable> cols = new Vector();	
     Vector<ISelectable> rows = new Vector();	

     for (int i = 0; i<b1.columns.size(); i++) {
    	 cols.add(b1.columns.elementAt(i));
     }
     
     
     for (int i = 0; i<b2.columns.size(); i++) {
    	 if (cols.indexOf(b2.columns.elementAt(i))==-1) cols.add(b2.columns.elementAt(i));
     }
     
     
     

     for (int i = 0; i<b1.rows.size(); i++) {
    	 rows.add(b1.rows.elementAt(i));
     }
     
     
     for (int i = 0; i<b2.rows.size(); i++) {
    	 if (rows.indexOf(b2.rows.elementAt(i))==-1) rows.add(b2.rows.elementAt(i));
     }
     
     
   

     return new Bicluster(b1.name + " " + b2.name,cols,rows); 
     
}

	
	
	
	
	
	
	public void setSize() {
		int columnsHeight = 0;
		for (int i = 0; i < biclust2.biclusters.size(); i++) {
			if (columnsHeight < biclust2.biclusters.elementAt(i).rows.size()) columnsHeight = biclust2.biclusters.elementAt(i).rows.size();
		}
		
		int rowWidth = 0;
		for (int i = 0; i < biclust1.biclusters.size(); i++) {
			if (rowWidth < biclust1.biclusters.elementAt(i).columns.size()) rowWidth = biclust1.biclusters.elementAt(i).columns.size();
		}
		
		
		int columnsWidth = 0;
		for (int i = 0; i < biclust2.biclusters.size(); i++) {
			columnsWidth += biclust2.biclusters.elementAt(i).columns.size();
		}
	  
		
		int rowsHeight = 0;
		for (int i = 0; i < biclust1.biclusters.size(); i++) {
			rowsHeight += biclust1.biclusters.elementAt(i).rows.size();
		}
		
		panel.pixelW = (MaxWidth - 3*panel.minDist - biclust2.biclusters.size()*panel.minDist)/(columnsWidth+rowWidth);
		if (panel.pixelW < 1) panel.pixelW = 1;
	  
		panel.pixelH = (MaxHeight - 3*panel.minDist - biclust1.biclusters.size()*panel.minDist)/(rowsHeight+columnsHeight);
		if (panel.pixelH < 1) panel.pixelH = 1;
		
		
		Dimension d = new Dimension(panel.pixelW*(columnsWidth+rowWidth) + 3*panel.minDist + biclust2.biclusters.size()*panel.minDist,
				               panel.pixelH*(rowsHeight+columnsHeight) + 3*panel.minDist + biclust1.biclusters.size()*panel.minDist);
		
		panel.setPreferredSize(d);
		setSize(new Dimension((int)d.getWidth()+10,(int)d.getHeight()+25));
		setVisible(true);
		
	}
	
	
	
	public void updatePlot( ){
		
		int columnsHeight = 0;
		for (int i = 0; i < biclust2.biclusters.size(); i++) {
			if (columnsHeight < biclust2.biclusters.elementAt(i).rows.size()) columnsHeight = biclust2.biclusters.elementAt(i).rows.size();
		}
		
		int rowWidth = 0;
		for (int i = 0; i < biclust1.biclusters.size(); i++) {
			if (rowWidth < biclust1.biclusters.elementAt(i).columns.size()) rowWidth = biclust1.biclusters.elementAt(i).columns.size();
		}
		
		
		int columnsWidth = 0;
		for (int i = 0; i < biclust2.biclusters.size(); i++) {
			columnsWidth += biclust2.biclusters.elementAt(i).columns.size();
		}
	  
		
		int rowsHeight = 0;
		for (int i = 0; i < biclust1.biclusters.size(); i++) {
			rowsHeight += biclust1.biclusters.elementAt(i).rows.size();
		}
		
		Dimension d = new Dimension(panel.pixelW*(columnsWidth+rowWidth) + 3*panel.minDist + biclust2.biclusters.size()*panel.minDist,
            panel.pixelH*(rowsHeight+columnsHeight) + 3*panel.minDist + biclust1.biclusters.size()*panel.minDist);
	
	panel.setPreferredSize(d);
	setSize(new Dimension((int)d.getWidth()+10,(int)d.getHeight()+25));
	setVisible(true);
	}
	
	
	
	
	public int getPosition(ISelectable el, Vector<ISelectable> v) {
		for (int i = 0; i < v.size(); i++) {
			if (el.getID() == v.elementAt(i).getID()) return i;
		}
		return -1;
	}
	
	
	

	
	public Bicluster intersect(Bicluster b1, Bicluster b2) {
	     Vector<ISelectable> cols1 = b1.columns;	
	     Vector<ISelectable> cols2 = b2.columns;	

	     Vector<ISelectable> rows1 = b1.rows;	
	     Vector<ISelectable> rows2 = b2.rows;	
   
	     return new Bicluster(b1.name + " " + b2.name, intersectV(b1.rows,b2.rows), intersectV(b1.columns,b2.columns)); 
	     
	}
	
	
	public Vector<ISelectable> intersectV(Vector<ISelectable> v1, Vector<ISelectable> v2) {
	       Vector<ISelectable> v = new Vector();
	       
	       for(int i = 0; i < v1.size(); i++) {
	       for(int j = 0; j < v2.size(); j++) {
	   	         if (v1.elementAt(i).getID() == v2.elementAt(j).getID()) {
	   	        	 v.add(v1.elementAt(i)); 	
	   	        	 break;
	   	         }
	       }
	       }
	       
	       return v;
	}







class BiPanel extends JPanel implements KeyListener{	


	Biclustering biclust1,biclust2;
	
	int abstandLinks = 5;
	int abstandOben = 5;
	int pixelW = 10;
	int pixelH = 1;
	int minDist = 10;
	double Min,Max;
	
	Biconf biconf;
 
	public BiPanel(Biconf biconf,Biclustering biclust1,Biclustering biclust2) {
		
		this.biclust1 = biclust1;
		this.biclust2 = biclust2;
		this.biconf = biconf;
		
		addKeyListener(this);
		calculateAbstande();
		calculateMinMax();
	
	}
	
	

	public void calculateMinMax() {
		Max = 0;
		Min = 0;
		for (int i = 0; i < biclust1.biclusters.size(); i++) {
	        Bicluster b = biclust1.biclusters.elementAt(i);
	        for (int j = 0; j < b.columns.size(); j++) {
	        	ISelectable col = b.columns.elementAt(j);
	        	
	        	
	        	for (int ii = 0; ii < biclust1.biclusters.size(); ii++) {
	    	        Bicluster bb = biclust1.biclusters.elementAt(ii);
	    	        for (int jj = 0; jj < b.rows.size(); jj++) {
	    	        	ISelectable row = b.rows.elementAt(jj);
	    	        	
	    	        	double value  = col.getValue(row.getID());
	    	        	if (value>Max) Max = value;
	    	        	if (value < Min) Min = value;
	    	        }
	        	}
	        }
		}
		
		
		for (int i = 0; i < biclust2.biclusters.size(); i++) {
	        Bicluster b = biclust2.biclusters.elementAt(i);
	        for (int j = 0; j < b.columns.size(); j++) {
	        	ISelectable col = b.columns.elementAt(j);
	        	
	        	
	        	for (int ii = 0; ii < biclust2.biclusters.size(); ii++) {
	    	        Bicluster bb = biclust2.biclusters.elementAt(ii);
	    	        for (int jj = 0; jj < b.rows.size(); jj++) {
	    	        	ISelectable row = b.rows.elementAt(jj);
	    	        	
	    	        	double value  = col.getValue(row.getID());
	    	        	if (value>Max) Max = value;
	    	        	if (value < Min) Min = value;
	    	        }
	        	}
	        }
		}
		
		
	}
	
	
	
	
	public void calculateAbstande() {
		abstandLinks = 0;
		for (int i = 0; i < biclust1.biclusters.size(); i++) {
			if (abstandLinks < biclust1.biclusters.elementAt(i).columns.size()) abstandLinks = biclust1.biclusters.elementAt(i).columns.size();
		}
		abstandLinks = abstandLinks*pixelW + 2*minDist;
		
		
		abstandOben = 0;
		for (int i = 0; i < biclust2.biclusters.size(); i++) {
			if (abstandOben < biclust2.biclusters.elementAt(i).rows.size()) abstandOben = biclust2.biclusters.elementAt(i).rows.size();
		}
		abstandOben = abstandOben*pixelH + 2*minDist;
		
	}
	
	
	
	
	
	
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0,0,this.getWidth(),getHeight());
		
		g.setColor(Color.BLACK);
		g.drawRect(abstandLinks,abstandOben,this.getWidth()-minDist-abstandLinks,this.getHeight()-minDist-abstandOben);
	
		g.setColor(new Color(203,203,203));

		g.drawLine(abstandLinks+1,
				   this.getHeight()-minDist+1+1,
				  1+this.getWidth()-minDist,
				this.getHeight()-minDist+1+1);
		
		g.drawLine(
				1+this.getWidth()-minDist+1,
				abstandOben+1,
				1+this.getWidth()-minDist+1,
				this.getHeight()-minDist-abstandOben+1
				);
		
		
		
		
		int shift = minDist;
		for (int k = 0; k < biclust1.biclusters.size(); k++) {
			
			Bicluster biclust = biclust1.biclusters.elementAt(k);
			
			for (int i = 0; i < biclust.columns.size(); i++) {
			for (int j = 0; j < biclust.rows.size(); j++) {
					
				
				 ISelectable col = biclust.columns.elementAt(i);
		         ISelectable row = biclust.rows.elementAt(j);
		         
		         
		         
		         

		    	 Color c = Color.WHITE;
				 double value = col.getValue(row.getID());
		    	
	             if (value > 0) {
	           			double koeff = value/Max;			
						c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin),Tools.fPos(koeff)*Settings.Cmax, 360 )).getRGBColor();	    	 
		    	 }
				 else {
							
						double koeff = value/Min;
						if (Min == 0) koeff = 0;
						c = Tools.convertHLCtoRGB(new MyColor(
								              Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), 
								              Tools.fNeg(koeff)*Settings.Cmax, 
								              120
								)).getRGBColor();
						
						
				}
	             
	             
	             g.setColor(c);
	             
	             g.fillRect(
	                   abstandLinks - pixelW*biclust.columns.size() -minDist+ i*pixelW,
	                   abstandOben + shift + j*pixelH,
	                   pixelW,
	                   pixelH
	             
	             
	             
	             
	             );
	             
	   				
				
			}
			}
			
			 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*k/biclust1.biclusters.size()),(float)0.95,(float)0.95));
             g.drawRect(
            		 abstandLinks - pixelW*biclust.columns.size() -minDist-2,
                   shift + abstandOben -2,
                   biclust.columns.size()*pixelW+3,
                   biclust.rows.size()*pixelH+3
             
             );
             
			 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*k/biclust1.biclusters.size()),(float)0.75,(float)0.75));
			   g.drawLine(
	            		 abstandLinks - pixelW*biclust.columns.size() -minDist-2+1,
	                   shift + abstandOben -2+ biclust.rows.size()*pixelH+3+1,
	                   abstandLinks - pixelW*biclust.columns.size() -minDist-2+1+  biclust.columns.size()*pixelW+3,
	                   shift + abstandOben -2+ biclust.rows.size()*pixelH+3+1
	             
	             );
			   g.drawLine(
	            		 abstandLinks - pixelW*biclust.columns.size() -minDist-2 +  biclust.columns.size()*pixelW+3+1,
	                   shift + abstandOben -2+1,
	                   abstandLinks - pixelW*biclust.columns.size() -minDist-2 +  biclust.columns.size()*pixelW+3+1,
	                   shift + abstandOben -2+ biclust.rows.size()*pixelH+3+1
	             
	             );
			   
			   
			   
             
			
			
			shift += biclust.rows.size()*pixelH + minDist;
		}
		
		
		
		
		
		
		
		
		shift = minDist;
		
		for (int k = 0; k < biclust2.biclusters.size(); k++) {
			Bicluster biclust = biclust2.biclusters.elementAt(k);
			for (int i = 0; i < biclust.columns.size(); i++) {
			for (int j = 0; j < biclust.rows.size(); j++) {
					
				
				 ISelectable col = biclust.columns.elementAt(i);
		         ISelectable row = biclust.rows.elementAt(j);
		         
		         
		         
		         

		    	 Color c = Color.WHITE;
				 double value = col.getValue(row.getID());
		    	
	             if (value > 0) {
	           			double koeff = value/Max;			
						c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin),Tools.fPos(koeff)*Settings.Cmax, 360 )).getRGBColor();	    	 
		    	 }
				 else {
							
						double koeff = value/Min;
						if (Min == 0) koeff = 0;
						c = Tools.convertHLCtoRGB(new MyColor(
								              Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), 
								              Tools.fNeg(koeff)*Settings.Cmax, 
								              120
								)).getRGBColor();
						
						
				}
	             
	             
	             g.setColor(c);
	             
	             g.fillRect(
	                   abstandLinks + shift + i*pixelW,
	                   abstandOben - biclust.rows.size()*pixelH - minDist+ j*pixelH,
	                   pixelW,
	                   pixelH
	                
	             );
	             
	             
	             
	             
	             

				
				
			}
			}
			
			
           	 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*k/biclust1.biclusters.size()),(float)0.95,(float)0.95));
             g.drawRect(
                   abstandLinks + shift-2,
                   abstandOben - biclust.rows.size()*pixelH-minDist-2,
                   biclust.columns.size()*pixelW+3,
                   biclust.rows.size()*pixelH+3
             
             );
           	 
             
          	 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*k/biclust1.biclusters.size()),(float)0.75,(float)0.75));
             g.drawLine(
                   abstandLinks + shift-2 + biclust.columns.size()*pixelW+3+1,
                   abstandOben - biclust.rows.size()*pixelH-minDist-2+1,
                   abstandLinks + shift-2 + biclust.columns.size()*pixelW+3+1,
                   abstandOben - biclust.rows.size()*pixelH-minDist-2+ biclust.rows.size()*pixelH+3+1
             
             );
           	 
             
             g.drawLine(
                     abstandLinks + shift-2+1,
                     abstandOben - biclust.rows.size()*pixelH-minDist-2 +  biclust.rows.size()*pixelH+3+1,
                     abstandLinks + shift-2+ biclust.columns.size()*pixelW+3+1,
                     abstandOben - biclust.rows.size()*pixelH-minDist-2 +  biclust.rows.size()*pixelH+3+1
               );
           	 
           	 
			
			shift += biclust.columns.size()*pixelW + minDist;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		int shiftY = minDist;
		
		
		for (int k = 0; k < biclust1.biclusters.size(); k++) {
			
		int shiftX = minDist;	
			
		for (int t = 0; t < biclust2.biclusters.size(); t++) {

			
			
			
	        g.setColor(new Color(247,247,247));
			
		    g.fillRect(
	                   abstandLinks + shiftX ,
	                   abstandOben + shiftY,
	                   pixelW*biclust2.biclusters.elementAt(t).columns.size(),
	                   pixelH *biclust1.biclusters.elementAt(k).rows.size()
	                                     
	             );
			
		    g.setColor(new Color(212,212,212));
			
	    	 g.drawLine(
	    			 abstandLinks + shiftX+1 ,
	                   abstandOben + shiftY +  pixelH *biclust1.biclusters.elementAt(k).rows.size()+1,
	                   abstandLinks + shiftX+1 + 	                   pixelW*biclust2.biclusters.elementAt(t).columns.size(),
	                   abstandOben + shiftY +  pixelH *biclust1.biclusters.elementAt(k).rows.size()+1
	    	 
	    	 );
	    	 
	    	 
	    	 g.drawLine(
	    			 abstandLinks + shiftX+1 + pixelW*biclust2.biclusters.elementAt(t).columns.size() ,
	                   abstandOben + shiftY+1,
	                   abstandLinks + shiftX+1 + pixelW*biclust2.biclusters.elementAt(t).columns.size() ,
	                   abstandOben + shiftY+1    +      pixelH *biclust1.biclusters.elementAt(k).rows.size()
	    	 
	    	 );
		    
		    
		    
		    
			Bicluster biclust = intersect(biclust1.biclusters.elementAt(k),biclust2.biclusters.elementAt(t));
			
			for (int i = 0; i < biclust.columns.size(); i++) {
			for (int j = 0; j < biclust.rows.size(); j++) {
					
				
				 ISelectable col = biclust.columns.elementAt(i);
		         ISelectable row = biclust.rows.elementAt(j);
		         
		         
		         
		         

		    	 Color c = Color.WHITE;
				 double value = col.getValue(row.getID());
		    	
	             if (value > 0) {
	           			double koeff = value/Max;			
						c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin),Tools.fPos(koeff)*Settings.Cmax, 360 )).getRGBColor();	    	 
		    	 }
				 else {
							
						double koeff = value/Min;
						if (Min == 0) koeff = 0;
						c = Tools.convertHLCtoRGB(new MyColor(
								              Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), 
								              Tools.fNeg(koeff)*Settings.Cmax, 
								              120
								)).getRGBColor();
						
						
				}
	             
	             
	             g.setColor(c);
	             
	             
	             	int shX = getPosition(col,biclust2.biclusters.elementAt(t).columns);
				    int shY = getPosition(row,biclust1.biclusters.elementAt(k).rows);
					
					
				    g.fillRect(
			                   abstandLinks + shiftX + shX*pixelW,
			                   abstandOben + shiftY + shY*pixelH,
			                   pixelW,
			                   pixelH
			                 );
				    
					/*
	             g.fillRect(
	                   abstandLinks + shiftX + i*pixelW,
	                   abstandOben + shiftY + j*pixelH,
	                   pixelW,
	                   pixelH
	                 );
	             */
	             
	             
	             
	             

				
				
			}
			}
			          
			
		
		    
		    
             
			
 			shiftX += biclust2.biclusters.elementAt(t).columns.size()*pixelW + minDist;
			
		}
		shiftY += biclust1.biclusters.elementAt(k).rows.size()*pixelH + minDist;

		}
		
		
		
		
		
		
		
	}
	
	
	

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

		if (arg0.getKeyCode() == 38) {

			
			
			if (pixelH > 1) {
				pixelH--;
				repaint();
			}

		}

		if (arg0.getKeyCode() == 40) {
			
			pixelH++;
			repaint();
			
			
			
			
	/*
			if (Aggregation > 1)
				Aggregation--;

			this.PixelCount = this.Rows.size() / Aggregation;
			this.calculateMatrixValues();

			globalView.infoLabel.setText("Aggregation: 1 : " + Aggregation);
			globalView.applyNewPixelSize();*/
		}

		if (arg0.getKeyCode() == 39) {

			pixelW++;
			repaint();

		}

		if (arg0.getKeyCode() == 37) {

			if (pixelW > 1)
				pixelW--;
				repaint();}
		
		
		biconf.updatePlot();
		
		
		
		
	}


	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	


}








}