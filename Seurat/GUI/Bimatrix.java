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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

import org.rosuda.REngine.Rserve.RConnection;


import Data.Bicluster;
import Data.Biclustering;
import Data.ISelectable;
import Data.MyColor;
import Settings.Settings;
import Tools.Tools;
import Data.DataManager;



public class Bimatrix extends JFrame implements IPlot{
	
	BiConfPanel panel;
	
	Biclustering biclust;
	
	int maxWidth = 400, maxHeight = 400;
	
	Seurat seurat;
	
	DataManager dataManager;
	
	JMenuItem item;
	
	public Bimatrix(Seurat seurat,Biclustering biclust) {
		super("Bimatrix " + biclust.name);
	    this.biclust = biclust;
	    this.seurat = seurat;
		this.dataManager = seurat.dataManager;
	    
		item = new JMenuItem(biclust.name);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
			}
		});
		
		seurat.windows.add(this);

		seurat.windowMenu.add(item);
		
		
	    
	    //sortBiclustering();
    
	    panel = new BiConfPanel(this,this.biclust);
		
	    setPlotSize();
	    this.getContentPane().add(new JScrollPane(panel));

	
	    
	    this.addKeyListener(panel);
	    this.addMouseListener(panel);
	    this.addMouseMotionListener(panel);
	    setLocation(330,0);
		this.setVisible(true);
	}
	
	// Sort Biclusters with TSP Method
	public void sortBiclustering() {
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());
			
			RConnection rConnection = dataManager.getRConnection();
				
			
			dataManager.rConnection.voidEval("require(stats)");
			dataManager.rConnection.voidEval("require(seriation)");
			
			
			double [][]matrix = new double [biclust.biclusters.size()][biclust.biclusters.size()];
			for (int i = 0; i< matrix.length; i++) {
				System.out.println();
				for (int j = 0; j< matrix.length; j++) {
					
					Bicluster inter = intersect(biclust.biclusters.elementAt(i),biclust.biclusters.elementAt(j));
					Bicluster union = union(biclust.biclusters.elementAt(i),biclust.biclusters.elementAt(j));
					
					matrix [i][j] = 1- (double)(inter.columns.size()*inter.rows.size())/(union.columns.size()*union.rows.size()); 
				  
				//	matrix [i][j] = 1-inter.columns.size()*inter.rows.size()/30;
				}
				
			}
			System.out.println();
			
		
			rConnection.assign("m", matrix [0]);

			for (int i = 1; i < matrix.length; i++) {
				rConnection.assign("x",matrix [i]);
				rConnection.voidEval("m <- cbind(m, x)");
			}
			
			dataManager.rConnection.voidEval("order<-seriate(as.dist(m),method = \"TSP\")");
			
			int[] order = dataManager.rConnection.eval("get_order(order)").asIntegers();
			
			Vector<Bicluster> biclusters = new Vector();
			for (int i = 0 ; i < biclust.biclusters.size(); i++) {
				biclusters.add(biclust.biclusters.elementAt(order[i]-1));
			}
			
			biclust = new Biclustering(biclust.name,biclusters);
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public Bicluster intersect(Bicluster b1, Bicluster b2) {
	     Vector<ISelectable> cols1 = b1.columns;	
	     Vector<ISelectable> cols2 = b2.columns;	

	     Vector<ISelectable> rows1 = b1.rows;	
	     Vector<ISelectable> rows2 = b2.rows;	
  
	     Bicluster b =  new Bicluster(b1.name + " " + b2.name, intersectV(b1.rows,b2.rows), intersectV(b1.columns,b2.columns)); 
	     
	     
	     return b;
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
	
	
	
	
	
	public int getPosition(ISelectable el, Vector<ISelectable> v) {
		for (int i = 0; i < v.size(); i++) {
			if (el.getID() == v.elementAt(i).getID()) return i;
		}
		return -1;
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

	
	
	
	
	
	public void setPlotSize() {
		int width = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			width+= biclust.biclusters.elementAt(i).columns.size();
		}
		
		int height = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			height+= biclust.biclusters.elementAt(i).rows.size();
		}
		
	    int pixelW = maxWidth /width;
	    int pixelH = maxHeight/height;
	    
	    if (pixelW == 0) pixelW = 1;
	    if (pixelH == 0) pixelH = 1;

	    
	    panel.pixelW = pixelW;
	    panel.pixelH = pixelH;
	    
		
		updatePlot();
	}

	
	public void updatePlot() {
		int width = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			width+= biclust.biclusters.elementAt(i).columns.size() ;
		}
		
		int height = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			height+= biclust.biclusters.elementAt(i).rows.size() ;
		}
		
		
		width = panel.abstandLinks + panel.pixelW*width + biclust.biclusters.size()*panel.bidist;
		height = panel.abstandOben +panel.pixelH*height + biclust.biclusters.size()*panel.bidist;
		 panel.setPreferredSize(new Dimension(width,height));
		 setSize(width+6,height+35);
			
			
		setVisible(true);
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





class BiConfPanel extends JPanel implements KeyListener,MouseListener,MouseMotionListener{
	
	public Biclustering biclust;
	public double Max, Min;
    int abstandOben = 3;
    int abstandLinks = 3;

	int bidist = 6;
	
	int pixelW = 5;
	int pixelH = 5;
	
	Bimatrix biconf;
	
	int [] order;
	
	Point point1, point2;
	
	  Vector<Integer> xStart;
	    Vector<Integer> yStart;

	
	Bicluster [][] Intersect;
	
	public BiConfPanel(Bimatrix biconf,Biclustering biclust) {
	
		this.biconf = biconf;
		this.biclust = biclust;
		calculateMinMax();
	    this.addKeyListener(this);
	    this.addMouseListener(this);
	    this.addMouseMotionListener(this);
	    
	    Intersect = calculateIntersect(biclust);
	  //  vorsort();
	    
	    
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(150);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(150);
		
	}
	
	
	public Bicluster [][] calculateIntersect(Biclustering biclust) {
		Bicluster [][] inter = new Bicluster [biclust.biclusters.size()][biclust.biclusters.size()];
	    for (int i = 0; i < biclust.biclusters.size(); i++) {
	    	 for (int j = 0; j < biclust.biclusters.size(); j++) {
	 	    	inter [j][i]= biconf.intersect(biclust.biclusters.elementAt(i),biclust.biclusters.elementAt(j));
	 	    }
	    }
	    return inter;
	
	}
	
	
	
	public void mouseClicked(MouseEvent e) {

		point1 = e.getPoint();
		
		
		if (e.getClickCount() == 2) {
			
			int i = 0;
			int j = 0;
			for (int k = 0; k < xStart.size(); k++) {
				if (xStart.elementAt(k)<e.getX()) i = k;
			}
			
			for (int k = 0; k < yStart.size(); k++) {
				if (yStart.elementAt(k)<e.getY()) j = k;
			}
			
			GlobalView g = new GlobalView(biconf.seurat,Intersect[i][j].name, Intersect [i][j].columns,Intersect [i][j].rows);
			g.applyNewPixelSize(pixelW,pixelH);
			
			
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
			
			
			
			item = new JMenuItem("Sort Biclusters");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					  biconf.sortBiclustering();
					  biclust = biconf.biclust;
					    Intersect = calculateIntersect(biclust);
					    repaint();


				}
			});
			menu.add(item);
			
			
			
			
			
			
			

			menu.show(this, e.getX(), e.getY());
		}

	}
	
	

	
	public void sortBiclusters() {
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			
			sortBicluster(i); 
			
		}
	    Intersect = calculateIntersect(biclust);

		repaint();
	}
	
	
	public void vorsort() {
        for (int i = 0; i < biclust.biclusters.size(); i++) {
			
			vorsortBicluster(i); 
			
		}
	    Intersect = calculateIntersect(biclust);
		repaint();
	}
	
	
	
	public void vorsortBicluster(int i) {
		Bicluster bi = biclust.biclusters.elementAt(i);

	
	    
		int [] newOrder = null;
		Vector<ISelectable> columns = bi.columns;
		Vector<ISelectable> rows = bi.rows;
        int [] countC  = new int [columns.size()];
        int [] countR  = new int [rows.size()];

		
		for (int k = 0; k < biclust.biclusters.size(); k++) {
			if (i!=k) {
		     Bicluster inter = Intersect [i][k];
		    
		    	 
		     for (int t = 0; t < columns.size(); t++) {
		    	 if (inter.columns.indexOf(columns.elementAt(t))!=-1 && inter.rows.size()!=0) countC [t]+=inter.rows.size();
		     }
		     for (int t = 0; t < rows.size(); t++) {
		    	 if (inter.rows.indexOf(rows.elementAt(t))!=-1 && inter.columns.size()!=0) countR [t]+=inter.columns.size();
		     }
		     
			}
		}
		
		System.out.println("Bicluster " + i);
		for (int ii = 0; ii < countC.length; ii++) System.out.print(countC [ii]); 
		System.out.println();
		for (int ii = 0; ii < countR.length; ii++) System.out.print(countR [ii]); 
        System.out.println();
		
		
		
		order = new int [bi.columns.size()];
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
		for (int  k = 0; k<bi.columns.size(); k++) {
			cols.add(bi.columns.elementAt(order [k]));
		}
	
		bi.columns = cols;
		
		
		
		
		order = new int [bi.rows.size()];
		for (int  k= 0; k < order.length; k++) order [k] = k;	
		

		for (int j = 0; j < order.length; j++) {
			for (int jj = j+1; jj < order.length; jj++) {
				if (countR [order[j]] < countR [order[jj]]) {
					int temp = order [j];
					order [j] = order [jj];
					order [jj] = temp;
				}
			}
		}
		
		rows = new Vector();
		for (int  k = 0; k<bi.rows.size(); k++) {
			rows.add(bi.rows.elementAt(order [k]));
		}
	
		bi.rows = rows;
		
		
	
	}
	
	
	
	
	public void sortBicluster(int i) {
		Bicluster bi = biclust.biclusters.elementAt(i);

		order = new int [bi.columns.size()];
		for (int  k= 0; k < order.length; k++) order [k] = k;
	    
		int [] newOrder = null;
		Vector<Vector<ISelectable>> columns = new Vector();
		Vector<Vector<ISelectable>> rows = new Vector();

		Vector<Integer> sizeC = new Vector();
		Vector<Integer> sizeR = new Vector();

		for (int k = 0; k < biclust.biclusters.size(); k++) {
			Bicluster bicluster = Intersect [i] [k];
			Vector<ISelectable> cols = bicluster.columns; 
			Vector<ISelectable> row = bicluster.rows; 

			columns.add(cols);
			rows.add(row);
		    sizeC.add(row.size());
		    sizeR.add(cols.size());

		}

		
		double crit = calcCrit(bi.columns,columns,sizeC,order);
		double newCrit = crit;
		System.out.println("Sort columns of bicluster " + i+"   " + newCrit);

	
		while ((newCrit = permute(bi.columns,columns,sizeC))<crit) {
			crit = newCrit;
			System.out.println(i+"   " + newCrit);
		} 
		System.out.println("   ");

		
		Vector<ISelectable> cols = new Vector();
		for (int  k = 0; k<bi.columns.size(); k++) {
			cols.add(bi.columns.elementAt(order [k]));
		}
	
		bi.columns = cols;
		
		
		
		order = new int [bi.rows.size()];
		for (int  k= 0; k < order.length; k++) order [k] = k;
	    
		
		
		crit = calcCrit(bi.rows,rows,sizeR,order);
		newCrit = crit;
		System.out.println("Sort rows of bicluster " + i+"   " + newCrit);

	
		while ((newCrit = permute(bi.rows,rows,sizeR))<crit) {
			crit = newCrit;
			System.out.println(i+"   " + newCrit);
		} 
		System.out.println("   ");

		
		Vector<ISelectable> row = new Vector();
		for (int  k = 0; k<bi.rows.size(); k++) {
			row.add(bi.rows.elementAt(order [k]));
		}
	
		bi.rows = row;
		
	
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
	
	
	
	
	
	
	
	public int [] getPermutation(int [] order,int dest, int from, int to) {
		int [] neworder = new int [order.length];
		
		Vector<Integer> v = new Vector();
		for (int i = 0; i < from; i++) {
			v.add(order [i]);
		}
		
		for (int i = to; i < order.length; i++) {
			v.add(order [i]);
		}
		
		int newdest = dest;
		if (dest > from) {
			newdest = dest - from + to;
		}
		
		System.out.println("getPermut:   " + order.length + " dest = " + dest + " from: " + from + " to: "+to);
		
		for (int i = 0; i < dest; i++) {
			neworder [i] = v.elementAt(i);
		}
		for (int i = dest; i < dest+to; i++) {
			neworder [i] = order [i-dest+from];
		}
		for (int i = dest+to; i < order.length; i++) {
			neworder [i] = v.elementAt(i-to);
		}
		
		
		return neworder;
	}
	
	
	
	

	public Vector<int []> getAllPermutations(int [] order, int [] [] bi) {
		Vector per = new Vector();
		
		
		/*
		for (int i = 0; i < bi.length; i++) {
			int [] o = bi [i];
			Vector<Integer> starts = new Vector();
			Vector<Integer> ends = new Vector();
			
			boolean start = false;
			

			
			for (int k = 0; k < o.length; k++) {
			    
				if (!start) {
					if (o [order [k]] !=-1)	{
						start = true;
						starts.add(k);
					}
					
				}
				else {
					if (o [order [k]] ==-1)	{
						start = false;
						ends.add(k-1);
					}
				}
				
			
			   
			}
			

			if (start) ends.add(o.length-1);

			
			
			for (int k = 0; k < starts.size(); k++) {
				for (int t = 0; t < starts.size(); t++) {
				per.add(getPermutation(order,starts.elementAt(t),starts.elementAt(k),ends.elementAt(k)));
				per.add(getPermutation(order,ends.elementAt(t),starts.elementAt(k),ends.elementAt(k)));

					
				}
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





	
	
	
	
	
	
	
	
	public void calculateMinMax() {
		Max = 0;
		Min = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
	        Bicluster b = biclust.biclusters.elementAt(i);
	        for (int j = 0; j < b.columns.size(); j++) {
	        	ISelectable col = b.columns.elementAt(j);
	        	
	        	
	        	for (int ii = 0; ii < biclust.biclusters.size(); ii++) {
	    	        Bicluster bb = biclust.biclusters.elementAt(ii);
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
	
	
	
	
	public void paint(Graphics g) {
	    g.setColor(Color.WHITE);
	  //  g.setColor(new Color(235,235,235));
	    g.fillRect(0,0,this.getWidth(), this.getHeight());
		
	    Vector<Bicluster> biclusters = biclust.biclusters;
	    xStart = new Vector();
	    yStart = new Vector();
        int maxX = 0;
        int maxY = 0;
	    
	    
	    for (int i = 0; i < biclusters.size(); i++) {
	    
	    	xStart.add(maxX);
	    	yStart.add(maxY);
	    	
	    	
	    	Bicluster b = biclusters.elementAt(i);
	        maxX += b.columns.size()*pixelW+bidist;
	        maxY += b.rows.size()*pixelH+bidist;
	    
	    }
	    
	    
	    
	    
	    for (int i = 0; i < biclusters.size(); i++) {
	    for (int j = 0; j < biclusters.size(); j++) {
	    	
	    	  g.setColor(new Color(249,249,249));
	  	    
	    	g.fillRect(
						abstandLinks+( xStart.elementAt(i))-1 , 
						 abstandOben+(yStart.elementAt(j))-1 , 
						pixelW*biclusters.elementAt(i).columns.size() +2, 
						pixelH*biclusters.elementAt(j).rows.size()+2
					  ); 
	    	
	    	 g.setColor(new Color(212,212,212));
					
	    	 g.drawLine(
	    			 abstandLinks+( xStart.elementAt(i)) + pixelW*biclusters.elementAt(i).columns.size() +1, 
					 abstandOben+(yStart.elementAt(j)) , 
					 abstandLinks+( xStart.elementAt(i)) + pixelW*biclusters.elementAt(i).columns.size() +1, 
					 abstandOben+(yStart.elementAt(j))-1+pixelH*biclusters.elementAt(j).rows.size()+2
	    	 
	    	 );
	    	 
	    	 
	    	 g.drawLine(
	    			 abstandLinks+( xStart.elementAt(i)) +1, 
					 abstandOben+(yStart.elementAt(j)) +pixelH*biclusters.elementAt(j).rows.size()+1 , 
					 abstandLinks+( xStart.elementAt(i)) + pixelW*biclusters.elementAt(i).columns.size() +2, 
					 abstandOben+(yStart.elementAt(j))+pixelH*biclusters.elementAt(j).rows.size()+1
	    	 
	    	 );
	    	 
	    	 
	    	 
	    	 
	    	 
						
	    	
	    	    Bicluster  b = Intersect [i][j];
	    	    
	    	    
	    	    if (b.columns != null && b.rows != null && b.columns.size()>0 && b.rows.size()>0) {
	    	    	
	    	    	
	    	    	
	    	    	 for (int ii = 0; ii < b.columns.size(); ii++) {
	    	    	 for (int jj = 0; jj < b.rows.size(); jj++) {
	    	    	
	    	         ISelectable col = b.columns.elementAt(ii);
	    	         ISelectable row = b.rows.elementAt(jj);
	 
	    	    		 
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

	  					
	  					int shiftX = ii;
	  					int shiftY = jj;
	  					if (i != j)
	  					{
	  						shiftX = biconf.getPosition(col,biclusters.elementAt(i).columns);
	  						shiftY = biconf.getPosition(row,biclusters.elementAt(j).rows);

	  					}
	  					
	  						g.fillRect(
		  							abstandLinks + xStart.elementAt(i) + shiftX*pixelW , 
		  							abstandOben + yStart.elementAt(j) +shiftY*pixelH , 
		  							pixelW , 
		  							pixelH 
		  					
		  				
		  				
		  					
		  					
		  					);
	  						
	  					
	  					
	  					/*
	  					g.fillRect(
	  							abstandLinks+( xStart.elementAt(i)+ii)*pixelW , 
	  							 abstandOben+(yStart.elementAt(j)+jj)*pixelH , 
	  							pixelW , 
	  							pixelH 
	  					
	  					
	  					
	  					);
	  					*/
	  				
	  					
	  					
	  					
	  				
	  					
	  					g.setColor(Color.BLACK);
	  		    	    
		    	    	if (i == j) {
				        	 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*i/biclusters.size()),(float)0.95,(float)0.95));

				        	 /*
		    	    		g.drawRect(
		    	    	
		    	    			xStart.elementAt(i)*pixelW , 
	  							abstandOben+yStart.elementAt(j)*pixelH , 
	  							b.colums.size()*pixelW , 
	  							b.rows.size()*pixelH );
		    	    		*/
		    	    		g.drawRect(
		    		    	    	
		    	    				abstandLinks+ xStart.elementAt(i) -2, 
		  							abstandOben+yStart.elementAt(j)-2 , 
		  							b.columns.size()*pixelW +3, 
		  							b.rows.size()*pixelH+3);
		    	    		
				        	 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*i/biclusters.size()),(float)0.75,(float)0.75));

		    	    	
				        	 g.drawLine(
			    		    	    	
			    	    				abstandLinks+ xStart.elementAt(i) -1 + b.columns.size()*pixelW +3, 
			  							abstandOben+yStart.elementAt(j)-1 , 
			  							abstandLinks+ xStart.elementAt(i) -1 + b.columns.size()*pixelW +3, 
			  							abstandOben+yStart.elementAt(j)-2  + b.rows.size()*pixelH+3);
				        	 
				        	 g.drawLine(
			    		    	    	
			    	    				abstandLinks+ xStart.elementAt(i) -1, 
			  							abstandOben+yStart.elementAt(j)-1 + b.rows.size()*pixelH+3 , 
			  							abstandLinks+ xStart.elementAt(i) -1+ b.columns.size()*pixelW +3, 
			  							abstandOben+yStart.elementAt(j)-1 + b.rows.size()*pixelH+3)
			  							;
				        	 
				        	 
		    	    	
		    	    	}
	  					
	  					
	  					}
	    	         }
	    	    
	    	    
	    	    
                  
	    	    
	    	    
	    	    }
	    	    
	    	    /*
	    		g.setColor(new Color(5,5,5));
					
					g.drawRect(
							( xStart.elementAt(i))*pixelW , 
							 abstandOben+(yStart.elementAt(j))*pixelH , 
							pixelW * biclusters.elementAt(i).colums.size() , 
							pixelH * biclusters.elementAt(j).rows.size()
					
					
					
					);
					*/
					
					
					
		}    
	    }
	    
	    
	    
		if (point1 != null && point2 != null) {
			g.setColor(Color.RED);
			
			
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math
					.abs(point2.y - point1.y));
			
			
		}
	    
	    
	    
	}
	
	
	
	
	
	public String getToolTipText(MouseEvent e) {

	      String text = null;
	   
	      
	      
	      
	      
		    Vector<Bicluster> biclusters = biclust.biclusters;

		    
		    for (int i = 0; i < biclusters.size(); i++) {
		    for (int j = 0; j < biclusters.size(); j++) {
		    	
		    	
		    	 
							
		    	
		    	    Bicluster  b = Intersect [i][j];
		    	    
		    	    
		    	    if (b.columns != null && b.rows != null && b.columns.size()>0 && b.rows.size()>0) {
		    	    	
		    	    	
		    	    	
		    	    	 for (int ii = 0; ii < b.columns.size(); ii++) {
		    	    	 for (int jj = 0; jj < b.rows.size(); jj++) {
		    	    	
		    	         ISelectable col = b.columns.elementAt(ii);
		    	         ISelectable row = b.rows.elementAt(jj);
		 
		    	    		 
		    	    		Color c = Color.WHITE;
		  					double value = col.getValue(row.getID());
		    	    	

		  				

		  					
		  					int shiftX = ii;
		  					int shiftY = jj;
		  					if (i != j)
		  					{
		  						shiftX = biconf.getPosition(col,biclusters.elementAt(i).columns);
		  						shiftY = biconf.getPosition(row,biclusters.elementAt(j).rows);

		  					}
		  					
		  					if (abstandLinks + xStart.elementAt(i) + shiftX*pixelW < e.getX() &&
		  						e.getX() < abstandLinks + xStart.elementAt(i) + shiftX*pixelW + pixelW &&
		  						abstandOben + yStart.elementAt(j) +shiftY*pixelH < e.getY() && 
		  					e.getY() < abstandOben + yStart.elementAt(j) +shiftY*pixelH + pixelH
		  					) 
		  						return value + "";
		    	    	 }
		    	    	 }
		    	    	 
		    	    	 
		    	    	 }

		    }}
	   
		  return null;
	      
		}
			




	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {


	      
	      
	    Vector<Bicluster> biclusters = biclust.biclusters;

	    
	    for (int i = 0; i < biclusters.size(); i++) {
	    for (int j = 0; j < biclusters.size(); j++) {
	    	
	    	
	    	 
						
	    	
	    	    Bicluster  b = Intersect [i][j];
	    	    
	    	    
	    	    if (b.columns != null && b.rows != null && b.columns.size()>0 && b.rows.size()>0) {
	    	    	
	    	    	
	    	    	
	    	    	 for (int ii = 0; ii < b.columns.size(); ii++) {
	    	    	 for (int jj = 0; jj < b.rows.size(); jj++) {
	    	    	
	    	         ISelectable col = b.columns.elementAt(ii);
	    	         ISelectable row = b.rows.elementAt(jj);
	 
	    	    		 
	    	    		Color c = Color.WHITE;
	  					double value = col.getValue(row.getID());
	    	    	

	  				

	  					
	  					int shiftX = ii;
	  					int shiftY = jj;
	  					if (i != j)
	  					{
	  						shiftX = biconf.getPosition(col,biclusters.elementAt(i).columns);
	  						shiftY = biconf.getPosition(row,biclusters.elementAt(j).rows);

	  					}
	  					
	  					double x = (double)abstandLinks + xStart.elementAt(i) + shiftX*pixelW + pixelW/2;
	  					double y =	(double)abstandOben + yStart.elementAt(j) +shiftY*pixelH + pixelH/2;
	  						
	  						
	  						if (xx1 < x && x < xx2 && yy1 < y && y < yy2) {
	  						 col.select(true);
	  						 row.select(true);

	  					  }
	  						
	    	    	 }
	    	    	 }
	    	    	 
	    	    	 
	    	    	 }

	    }}
   
	
		this.repaint();

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


