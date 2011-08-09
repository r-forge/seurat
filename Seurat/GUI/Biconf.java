package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

import Data.Bicluster;
import Data.Biclustering;
import Data.ClusterNode;
import Data.Clustering;
import Data.ISelectable;
import Data.MyColor;
import Data.Permutation;
import Settings.Settings;
import Tools.Tools;

public class Biconf extends JFrame implements IPlot{
	
	
Biclustering biclust1,biclust2;
	
BiPanel panel;

Seurat seurat;

int MaxWidth = 700, MaxHeight = 700;
	
double [][] matrix; 

int [] orderCols,orderRows;


int maxC = -1, maxR = -1;

JMenuItem item;


	public Biconf(Seurat seurat,Biclustering biclust1,Biclustering biclust2) {
		super("Biconfusionsmatrix");
		this.biclust1 = biclust1;
		this.biclust2 = biclust2;
		this.seurat = seurat;
		
		
		
		 item = new JMenuItem("Confmatrix:  "+ biclust1.name + "   " + biclust2.name);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
				}
			});
			
			seurat.windows.add(this);

			seurat.windowMenu.add(item);
		
	
		
		
		//permuteMatrix();
		
		panel = new BiPanel(this,biclust1,biclust2);
		
		this.getContentPane().add(new JScrollPane(panel));
		
		setLocation(400,0);
		setSize();
	    panel.calculateAbstande();
		this.addKeyListener(panel);
		this.addMouseListener(panel);
this.addMouseMotionListener(panel);
		
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
	
 //  System.out.println(" Vor  ->  " + criterion);

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
//	System.out.println(" Nach  ->  " + criterion);
	
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
	
	
	for (int j = 0; j < i; j++) {

		int [] o = new int [order.length];
		
		for (int k = 0; k < j; k++) o [k] = order [k];
		o [j] = order [i];
		for (int k = j+1; k < i+1; k++) o [k] = order [k-1];
		for (int k = i+1; k < order.length; k++) o [k] = order [k];
        
		
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







class BiPanel extends JPanel implements KeyListener,  MouseListener, MouseMotionListener{	


	Biclustering biclust1,biclust2;
	
	int abstandLinks = 5;
	int abstandOben = 5;
	int pixelW = 10;
	int pixelH = 1;
	int minDist = 10;
	double Min,Max;
	
	Biconf biconf;
	
	Bicluster [][] Intersect;
	
	int [] order;

	Point point1, point2;
 
	public BiPanel(Biconf biconf,Biclustering biclust1,Biclustering biclust2) {
		
		this.biclust1 = biclust1;
		this.biclust2 = biclust2;
		this.biconf = biconf;
		
		addKeyListener(this);
		 this.addMouseListener(this);
		    this.addMouseMotionListener(this);
		calculateAbstande();
		calculateMinMax();
		
	    Intersect = calculateIntersect(biclust1,biclust2);
	    
	    
	    
	    
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(150);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(150);

	
	}
	
	
	public String getToolTipText(MouseEvent e) {

	      String text = null;
	   
	      int shift = minDist;
			for (int k = 0; k < biclust1.biclusters.size(); k++) {
				
				Bicluster biclust = biclust1.biclusters.elementAt(k);
				
				for (int i = 0; i < biclust.columns.size(); i++) {
				for (int j = 0; j < biclust.rows.size(); j++) {
						
					
					 ISelectable col = biclust.columns.elementAt(i);
			         ISelectable row = biclust.rows.elementAt(j);
			         
			      
					 double value = col.getValue(row.getID());
			    	
		             if (
		            e.getX()>  abstandLinks - pixelW*biclust.columns.size() -minDist+ i*pixelW &&
		            e.getX() < abstandLinks - pixelW*biclust.columns.size() -minDist+ i*pixelW + pixelW &&
		            e.getY() >    abstandOben + shift + j*pixelH &&
		            e.getY() < abstandOben + shift + j*pixelH + pixelH
		             
		             
		             ) return value+"";
		              
		           
		   				
					
				}
				}
				
			  
	             
				
				
				shift += biclust.rows.size()*pixelH + minDist;
			}
			
			
			
			
			
			
			
			
			shift = minDist;
			
			for (int k = 0; k < biclust2.biclusters.size(); k++) {
				Bicluster biclust = biclust2.biclusters.elementAt(k);
				for (int i = 0; i < biclust.columns.size(); i++) {
				for (int j = 0; j < biclust.rows.size(); j++) {
						
					
					 ISelectable col = biclust.columns.elementAt(i);
			         ISelectable row = biclust.rows.elementAt(j);
			         
			         
			         
					 double value = col.getValue(row.getID());
			    
		             
		             
		             if(
		                 e.getX() >  abstandLinks + shift + i*pixelW &&
		                 e.getX() <  abstandLinks + shift + i*pixelW + pixelW&&
                         e.getY() >	abstandOben - biclust.rows.size()*pixelH - minDist+ j*pixelH&&
                         e.getY() <	abstandOben - biclust.rows.size()*pixelH - minDist+ j*pixelH + pixelH		                
		             ) return value + "";
		             
		             
		             
		             
		             

					
					
				}
				}
				
			
	         
	           	 
				
				shift += biclust.columns.size()*pixelW + minDist;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			int shiftY = minDist;
			
			
			for (int k = 0; k < biclust1.biclusters.size(); k++) {
				
			int shiftX = minDist;	
				
			for (int t = 0; t < biclust2.biclusters.size(); t++) {

				
				  
			    
				Bicluster biclust = biconf.intersect(biclust1.biclusters.elementAt(k),biclust2.biclusters.elementAt(t));
				
				for (int i = 0; i < biclust.columns.size(); i++) {
				for (int j = 0; j < biclust.rows.size(); j++) {
						
					
					 ISelectable col = biclust.columns.elementAt(i);
			         ISelectable row = biclust.rows.elementAt(j);
			         
			         
			         
			         

			    	 Color c = Color.WHITE;
					 double value = col.getValue(row.getID());
			    	
		             
		          
		             
		             	int shX = getPosition(col,biclust2.biclusters.elementAt(t).columns);
					    int shY = getPosition(row,biclust1.biclusters.elementAt(k).rows);
						
						
					    if (
				            e.getX() >        abstandLinks + shiftX + shX*pixelW &&
				            e.getX() <        abstandLinks + shiftX + shX*pixelW + pixelW &&
				            e.getY() >      abstandOben + shiftY + shY*pixelH &&
				            e.getY() <  abstandOben + shiftY + shY*pixelH + pixelH
				               ) return ""+ value;
					    
				
		             
		             
		             
		             

					
					
				}
				}
				          
				
			
			    
			    
	             
				
	 			shiftX += biclust2.biclusters.elementAt(t).columns.size()*pixelW + minDist;
				
			}
			shiftY += biclust1.biclusters.elementAt(k).rows.size()*pixelH + minDist;

			}
			
			
	      
	      
	      
	 return null;     
	}
	
	
	

   public boolean isPointInRect (double x, double y, int xx1, int yy1, int xx2, int yy2) {
	
	   if (x > xx1 && x < xx2 && y > yy1 && y < yy2) return true; 
	   
	   return false;
   }
	
	
	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {

		
		 int shift = minDist;
			for (int k = 0; k < biclust1.biclusters.size(); k++) {
				
				Bicluster biclust = biclust1.biclusters.elementAt(k);
				
				for (int i = 0; i < biclust.columns.size(); i++) {
				for (int j = 0; j < biclust.rows.size(); j++) {
						
					
					 ISelectable col = biclust.columns.elementAt(i);
			         ISelectable row = biclust.rows.elementAt(j);
			         
			      
					 double value = col.getValue(row.getID());
			    	
		             if (isPointInRect(
		          
		             (double)abstandLinks - pixelW*biclust.columns.size() -minDist+ i*pixelW + pixelW/2 ,
		          
		            (double)abstandOben + shift + j*pixelH + pixelH/2, xx1,yy1,xx2,yy2))
		             
		             
		              {
		            	 col.select(true);
		            	 row.select(true);
		             }
		              
		           
		   				
					
				}
				}
				
			  
	             
				
				
				shift += biclust.rows.size()*pixelH + minDist;
			}
			
			
			
			
			
			
			
			
			shift = minDist;
			
			for (int k = 0; k < biclust2.biclusters.size(); k++) {
				Bicluster biclust = biclust2.biclusters.elementAt(k);
				for (int i = 0; i < biclust.columns.size(); i++) {
				for (int j = 0; j < biclust.rows.size(); j++) {
						
					
					 ISelectable col = biclust.columns.elementAt(i);
			         ISelectable row = biclust.rows.elementAt(j);
			         
			         
			         
					 double value = col.getValue(row.getID());
			    
					 if (isPointInRect(
					          
				             (double)abstandLinks + shift + i*pixelW + pixelW/2 ,
				          
				            (double)abstandOben - biclust.rows.size()*pixelH - minDist+ j*pixelH + pixelH/2, xx1,yy1,xx2,yy2))
				             
				             
				              {
				            	 col.select(true);
				            	 row.select(true);
				             }
		             
		           
		             
		             
		             
		             

					
					
				}
				}
				
			
	         
	           	 
				
				shift += biclust.columns.size()*pixelW + minDist;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			int shiftY = minDist;
			
			
			for (int k = 0; k < biclust1.biclusters.size(); k++) {
				
			int shiftX = minDist;	
				
			for (int t = 0; t < biclust2.biclusters.size(); t++) {

				
				  
			    
				Bicluster biclust = biconf.intersect(biclust1.biclusters.elementAt(k),biclust2.biclusters.elementAt(t));
				
				for (int i = 0; i < biclust.columns.size(); i++) {
				for (int j = 0; j < biclust.rows.size(); j++) {
						
					
					 ISelectable col = biclust.columns.elementAt(i);
			         ISelectable row = biclust.rows.elementAt(j);
			         
			         
			         
			         

			    	 Color c = Color.WHITE;
					 double value = col.getValue(row.getID());
			    	
		             
		          
		             
		             	int shX = getPosition(col,biclust2.biclusters.elementAt(t).columns);
					    int shY = getPosition(row,biclust1.biclusters.elementAt(k).rows);
						
					    
					    if (isPointInRect(
						          
					             (double)  abstandLinks + shiftX + shX*pixelW + pixelW/2 ,
					          
					            (double)abstandOben + shiftY + shY*pixelH + pixelH/2, xx1,yy1,xx2,yy2))
					             
					             
					              {
					            	 col.select(true);
					            	 row.select(true);
					             }
			             
			           
			            
		             
		             
		             
		             

					
					
				}
				}
				          
				
			
			    
			    
	             
				
	 			shiftX += biclust2.biclusters.elementAt(t).columns.size()*pixelW + minDist;
				
			}
			shiftY += biclust1.biclusters.elementAt(k).rows.size()*pixelH + minDist;

			}
		
		
	}
	
	
	public Bicluster [][] calculateIntersect(Biclustering biclust1,Biclustering biclust2) {
		Bicluster [][] inter = new Bicluster [biclust1.biclusters.size()][biclust2.biclusters.size()];
	    for (int i = 0; i < biclust1.biclusters.size(); i++) {
	    	 for (int j = 0; j < biclust2.biclusters.size(); j++) {
	 	    	inter [i][j]= biconf.intersect(biclust1.biclusters.elementAt(i),biclust2.biclusters.elementAt(j));
	 	    }
	    }
	    return inter;
	
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
						if (col.isSelected() && row.isSelected()) c = Tools.convertHLCtoRGB(new MyColor(
					    		(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection),
					    		Tools.fPos(koeff)*Settings.Cmax*Settings.Selection, 
					    		biconf.seurat.PosColor 
					    )).getRGBColor();	
	             
	             
	             }
				 else {
							
						double koeff = value/Min;
						if (Min == 0) koeff = 0;
						c = Tools.convertHLCtoRGB(new MyColor(
								              Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), 
								              Tools.fNeg(koeff)*Settings.Cmax, 
								              120
								)).getRGBColor();
						
						if (col.isSelected() && row.isSelected())  c = Tools.convertHLCtoRGB(new MyColor((
					               Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection), 
					               Tools.fNeg(koeff)*Settings.Cmax*Settings.Selection, 
					               biconf.seurat.NegColor
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
						if (col.isSelected() && row.isSelected()) c = Tools.convertHLCtoRGB(new MyColor(
					    		(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection),
					    		Tools.fPos(koeff)*Settings.Cmax*Settings.Selection, 
					    		biconf.seurat.PosColor 
					    )).getRGBColor();	
	             
	             }
				 else {
							
						double koeff = value/Min;
						if (Min == 0) koeff = 0;
						c = Tools.convertHLCtoRGB(new MyColor(
								              Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), 
								              Tools.fNeg(koeff)*Settings.Cmax, 
								              120
								)).getRGBColor();
						
						if (col.isSelected() && row.isSelected())  c = Tools.convertHLCtoRGB(new MyColor((
					               Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection), 
					               Tools.fNeg(koeff)*Settings.Cmax*Settings.Selection, 
					               biconf.seurat.NegColor
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
		    
		    
		    
		    
			Bicluster biclust = biconf.intersect(biclust1.biclusters.elementAt(k),biclust2.biclusters.elementAt(t));
			
			for (int i = 0; i < biclust.columns.size(); i++) {
			for (int j = 0; j < biclust.rows.size(); j++) {
					
				
				 ISelectable col = biclust.columns.elementAt(i);
		         ISelectable row = biclust.rows.elementAt(j);
		         
		         
		         
		         

		    	 Color c = Color.WHITE;
				 double value = col.getValue(row.getID());
		    	
	             if (value > 0) {
	           			double koeff = value/Max;			
						c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin),Tools.fPos(koeff)*Settings.Cmax, 360 )).getRGBColor();	    	 
						if (col.isSelected() && row.isSelected()) c = Tools.convertHLCtoRGB(new MyColor(
					    		(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection),
					    		Tools.fPos(koeff)*Settings.Cmax*Settings.Selection, 
					    		biconf.seurat.PosColor 
					    )).getRGBColor();	
	             
	             }
				 else {
							
						double koeff = value/Min;
						if (Min == 0) koeff = 0;
						c = Tools.convertHLCtoRGB(new MyColor(
								              Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), 
								              Tools.fNeg(koeff)*Settings.Cmax, 
								              120
								)).getRGBColor();
						if (col.isSelected() && row.isSelected())  c = Tools.convertHLCtoRGB(new MyColor((
					               Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection), 
					               Tools.fNeg(koeff)*Settings.Cmax*Settings.Selection, 
					               biconf.seurat.NegColor
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
		
		
		 
		if (point1 != null && point2 != null) {
			g.setColor(Color.RED);
			
			
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math
					.abs(point2.y - point1.y));
			
			
		}
	    
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	public void mouseClicked(MouseEvent e) {

		point1 = e.getPoint();

		
		
if (e.getClickCount() == 2) {
	
	
	
	if (e.getX()<abstandLinks && e.getY()>abstandOben) { 
		
		

		int shift = minDist;
		for (int k = 0; k < biclust1.biclusters.size(); k++) {
			Bicluster biclust = biclust1.biclusters.elementAt(k);
			
			 
		    	
			
			shift += biclust.rows.size()*pixelH + minDist;
			
			
			if (abstandOben + shift > e.getY()) {
				
				
				
				GlobalView g = new GlobalView(biconf.seurat,biclust.name, biclust.columns,biclust.rows);
				g.applyNewPixelSize(pixelW,pixelH);
				break;
				
				}      
			
			
		}
		
	}
	
	
	
if (e.getX()>abstandLinks && e.getY()<abstandOben) { 

		
		

		int shift = minDist;
		for (int k = 0; k < biclust2.biclusters.size(); k++) {
			Bicluster biclust = biclust2.biclusters.elementAt(k);
			
			     
		    	
			
			shift += biclust.columns.size()*pixelW + minDist;
			
			
			if (abstandLinks + shift > e.getX()) {
				
				
				
				GlobalView g = new GlobalView(biconf.seurat,biclust.name, biclust.columns,biclust.rows);
				g.applyNewPixelSize(pixelW,pixelH);
				break;
				
				}  
			
			
		}
		
	}






if (e.getX()>abstandLinks && e.getY()>abstandOben) { 
	
	
	
	

	
	
	int shiftY = minDist+abstandOben;
	
	boolean br = false;
	

	
	for (int k = 0; k < biclust1.biclusters.size(); k++) {

	shiftY += biclust1.biclusters.elementAt(k).rows.size()*pixelH + minDist;

	int shiftX = minDist+abstandLinks;	
		
	for (int t = 0; t < biclust2.biclusters.size(); t++) {

		shiftX += biclust2.biclusters.elementAt(t).columns.size()*pixelW + minDist;

		
		Bicluster biclust = biconf.intersect(biclust1.biclusters.elementAt(k),biclust2.biclusters.elementAt(t));
		
	
	    if (shiftX>e.getX() && shiftY>e.getY()){
			

			GlobalView g = new GlobalView(biconf.seurat,biclust.name, biclust.columns,biclust.rows);
			g.applyNewPixelSize(pixelW,pixelH);
			br = true;
			break;
	    } 
	    
         
		
		
	}
	if (br) break;

	}
	
	
	
	
	
		
	
		
		
}



	
}


		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			JPopupMenu menu = new JPopupMenu();

			JMenuItem item = new JMenuItem("Sort");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					sortBiclusters();
				}
			});
			menu.add(item);
			
			item = new JMenuItem("Vorsort");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					vorsort();
				}
			});
			menu.add(item);
			
			
			
			item = new JMenuItem("Confsort ");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					biconf.permuteMatrix();
					repaint();
				}
			});
			menu.add(item);
			
			
			
			
			

			menu.show(this, e.getX(), e.getY());
		}

	}
	
	

	
	public void sortBiclusters() {
		for (int i = 0; i < biclust1.biclusters.size(); i++) {
			
			sortBicluster(biclust1.biclusters.elementAt(i),biclust2,false); 
			
		}
		
       for (int i = 0; i < biclust2.biclusters.size(); i++) {
			
			sortBicluster(biclust2.biclusters.elementAt(i),biclust1,true); 
			
		}

		repaint();
	}
	
	
	
	
	
	
	public void vorsort() {
        for (int i = 0; i < biclust1.biclusters.size(); i++) {
			
			vorsort(biclust1.biclusters.elementAt(i),biclust2, false); 
			
		}
		
       for (int i = 0; i < biclust2.biclusters.size(); i++) {
			
			vorsort(biclust2.biclusters.elementAt(i),biclust1,true); 
			
		}
		repaint();
	}
	
	
	
	
	
	
	
	public void vorsort(Bicluster bi,Biclustering biclust, boolean isColumns) {
		

	
	    
		Vector<ISelectable> items = bi.columns;
		if (!isColumns) items = bi.rows;
			
			
			
		

		
        int [] countC  = new int [items.size()];
 

		
		for (int k = 0; k < biclust.biclusters.size(); k++) {
			if (bi!=biclust.biclusters.elementAt(k)) {
		     Bicluster inter = biconf.intersect(bi,biclust.biclusters.elementAt(k));
		    
		     Vector<ISelectable> itm = inter.columns;
				if (!isColumns) itm = inter.rows;
		     
		     Vector<ISelectable> weight = inter.rows;
		     if (!isColumns) weight = inter.columns;
		    	 
		     for (int t = 0; t < items.size(); t++) {
		    	 if (itm.indexOf(items.elementAt(t))!=-1 && weight.size()!=0) countC [t]+= weight.size();
		     }
		     
		     
			}
		}
		
		
		
		
		
		
		order = new int [items.size()];
		for (int  k= 0; k < order.length; k++) order [k] = k;	
		

		for (int j = 0; j < order.length; j++) {
			for (int jj = j+1; jj < order.length; jj++) {
				if (countC [order[j]] < countC [order[jj]]) {
					int temp = order [j];
					order [j] = order [jj];
					order [jj] = temp;
				}
			}
		}
		
		Vector<ISelectable> cols = new Vector();
		for (int  k = 0; k<items.size(); k++) {
			cols.add(items.elementAt(order [k]));
		}
	
		
	
		
		if (isColumns) bi.columns = cols;
		if (!isColumns)bi.rows = cols;

		
		
		
	
	}
	
	
	
	
	public void sortBicluster(Bicluster bi,Biclustering biclust, boolean isColumns) {

		   
		Vector<ISelectable> items = bi.columns;
		if (!isColumns) items = bi.rows;
		
		
		
		order = new int [items.size()];
		for (int  k= 0; k < order.length; k++) order [k] = k;
	    

		Vector<Vector<ISelectable>> columns = new Vector();

		Vector<Integer> sizeC = new Vector();

		for (int k = 0; k < biclust.biclusters.size(); k++) {
			Bicluster bicluster = biconf.intersect(bi,biclust.biclusters.elementAt(k));
		
			Vector<ISelectable> cols = bicluster.columns; 
			Vector<ISelectable> rows = bicluster.rows; 

			if (isColumns) columns.add(cols);
			else columns.add(rows);
			
			
		    if (isColumns) sizeC.add(rows.size());
		    else sizeC.add(cols.size());

		}

		
		double crit = calcCrit(items,columns,sizeC,order);
		double newCrit = crit;
	    System.out.println(" Bicluster  " + bi.name + "  isCols " + isColumns + "  Startcrit " + crit);

	
		while ((newCrit = permute(items,columns,sizeC))<crit) {
			crit = newCrit;
			System.out.println("   "+crit);
		} 
		System.out.println("   ");

		
		Vector<ISelectable> cols = new Vector();
		for (int  k = 0; k< items.size(); k++) {
			cols.add(items.elementAt(order [k]));
		}
	
		if (isColumns) bi.columns = cols;
		if (!isColumns)bi.rows = cols;
		
		
		
	
	}
	
	
	
	
	
	public double  permute(Vector<ISelectable> b, Vector<Vector<ISelectable>> bis, Vector<Integer> size) {
		Vector<ISelectable> newb = new Vector();
	    
	    int [][] orders = new int [bis.size()][];
	    for (int i = 0; i < bis.size(); i++) {
	    	Vector<ISelectable> items = bis.elementAt(i);
	    	orders [i] = new int [items.size()];
	    	for (int k = 0; k < items.size(); k++) {
	    		int index = b.indexOf(items.elementAt(k));
	    	
	    		orders [i] [k] = index;

	    	}
	    }
		
		
		Vector<int []> colPermutations = getAllPermutations(order,orders);
		int maxJ = colPermutations.size()-1;
		
		
				
		double criterion = calcCrit(b, bis, size, order);
		
	 //  System.out.println(" Vor  ->  " + criterion);

		for (int i = 0; i < colPermutations.size(); i++) {
			
				int [] pC = colPermutations.elementAt(i);
			    
				double crit = calcCrit(b, bis, size,pC);
				
				if (crit < criterion) {
					criterion = crit;
					maxJ = i;
				}
		}	
		
		order =  colPermutations.elementAt(maxJ); 
	    return criterion; 
		
	}
	
	
	
	
	public double calcCrit(Vector<ISelectable> b, Vector<Vector<ISelectable>> bis, Vector<Integer> size, int [] order) {
	double crit = 0;
		
		for (int i = 0; i < bis.size(); i++) {
			Vector<ISelectable> cols = bis.elementAt(i);
			
			int ind = -2;
			
			
			for (int k  = 0 ; k < b.size(); k++) {
				
				ISelectable col1 = b.elementAt(order [k]);
			    int index = cols.indexOf(col1);
		
				
			    if (index != -1 && ind != -2 && k - ind >1) {
			    	crit+= size.elementAt(i);
				}

			    if (index != -1) {
		        ind = k;
			    }
			    
			    
			}	
				
			
			
			
			
		
		}
				
	
		return crit;
	}
	
	
	
	
	
	
	
	
	

	public Vector<int []> getAllPermutations(int [] order, int [] [] bi) {
		Vector per = new Vector();
		/*
		for (int i = 0; i < bi.length; i++) {
			int [] o = bi [i];
			int start = -1;
			for (int k = 0; k < o.length; k++) {
			    if (start == -1 && o [order [k]] !=-1)	start = k;
			
			    if ()
			}
		}
		*/
	   
		for (int i = 0; i < order.length; i++) {
		for (int j = i+1; j < order.length; j++) {

			int [] o = new int [order.length];
			
			for (int k = 0; k < i; k++) o [k] = order [k];
			for (int k = i; k < j; k++) o [k] = order [k+1];
			o [j] = order [i];
			for (int k = j+1; k < order.length; k++) o [k] = order [k];
	        
			
			per.add(o);
			
			
			}
		
		
		for (int j = 0; j < i; j++) {

			int [] o = new int [order.length];
			
			for (int k = 0; k < j; k++) o [k] = order [k];
			o [j] = order [i];
			for (int k = j+1; k < i+1; k++) o [k] = order [k-1];
			for (int k = i+1; k < order.length; k++) o [k] = order [k];
	        
			
			per.add(o);
			
			
			}
		
		
		
		}
		
		int [] o = new int [order.length];
		
		for (int k = 0; k < order.length; k++) o [k] = order [k];
		per.add(o);
		
		
		return per;
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




	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}







	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}







	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		point1 = e.getPoint();

		

		this.repaint();

	}







	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
		
		point2 = e.getPoint();

		if (point1 != null	&& point2 != null && (point1.getX() - point2.getX())
						* (point1.getY() - point2.getY()) < 0) {
			Point p = point1;
			point1 = point2;
			point2 = p;

		}
		
		

		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
			return;
		}

		if (point1 != null && point2 != null) {

			if (!e.isShiftDown()) biconf.seurat.dataManager.deleteSelection();
			
			selectRectangle(point1.x, point1.y, point2.x, point2.y);
				

			point1 = null;
			point2 = null;
			biconf.seurat.repaintWindows();
		}

		this.repaint();
		
		
		
		
		
	}	



	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		point2 = e.getPoint();

		

		this.repaint();

	}







	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		repaint();
	}


	
	
	
	


}







public void brush() {
	// TODO Auto-generated method stub
	
}


public void print() {
	// TODO Auto-generated method stub
	
}


public void removeColoring() {
	// TODO Auto-generated method stub
	
}


public void updateSelection() {
	// TODO Auto-generated method stub
    repaint();	
}








}