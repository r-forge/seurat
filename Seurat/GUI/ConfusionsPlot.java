package GUI;


import java.util.*;

import javax.swing.*;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.awt.event.*;
import java.awt.*;

import Data.Clustering;
import Data.Gene;
import Data.GeneVariable;
import Data.ISelectable;


public class ConfusionsPlot extends JFrame implements IPlot {

	Seurat seurat;

	ConfusionsPanel panel;
	
	JMenuItem item;

	public ConfusionsPlot(Seurat seurat, String method1, String method2, Clustering Experiments1, Clustering Experiments2) {
		super("ComparePlot: ( " + method1 + " , " + method2 + " )");
		this.seurat = seurat;

		panel = new ConfusionsPanel(seurat, method1, method2, Experiments1, Experiments2);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		this.setBounds(400, 400, 300, 300);

		this.setVisible(true);
		
		this.addKeyListener(panel);

		seurat.windows.add(this);

		item = new JMenuItem("ComparePlot");
		seurat.windowMenu.add(item);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
			}
		});

		
		this.setVisible(true);

	}

	public void updateSelection() {
		panel.updateSelection();

	}

	public void brush() {
		// TODO Auto-generated method stub
		
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		
	}

}

class ConfusionsPanel extends JPanel implements KeyListener, MouseListener,
		IPlot, MouseMotionListener {
	

	//double[] balken;

	//double[] selectedBalken;


	Seurat seurat;


	JMenuItem item = new JMenuItem("");

	ConfusionsPanel confusionsPanel = this;

	Point point1, point2;

	Image image;

	int abstandLinks = 80;

	int abstandOben = 20;

	int BinHeigth= 20;

	int abstandString = 5;


    JPopupMenu menu;
	
    
    int [][] dist1;
    int [][] dist2;
    
    Vector<Point> points;
    
    boolean  VIEW = true;
    
    
    double gen = 4;
    
    
    Vector<Integer> Columns = new Vector();
	Vector<Integer> Rows = new Vector();
	
	
	
	//double[] koeff;

	//int[] balkenWidth;

	//int[] balkenSelectedWidth;

//	String [] binStrings;
    
    
    Clustering Experiments1;
    Clustering Experiments2;

	
    int [][] matrix;
    
    
    int sortMethod = 1;
    
    
    int [][]isSelected;
    
    Vector<Vector<Integer>> ClusterRows;
    Vector<Vector<Integer>> ClusterColumns;
    
    
    int count=-1;

	public ConfusionsPanel(Seurat seurat, String method1, String method2, Clustering Experiments1, Clustering Experiments2) {

	
		this.seurat = seurat;
	
        this.Experiments1 = Experiments1;
        this.Experiments2 = Experiments2;
		
	
		
    	for (int i = 0; i < Experiments1.clusters.size(); i++) {
			Columns.add(new Integer(i));
		}
		
		
		
		for (int i = 0; i < Experiments2.clusters.size(); i++) {
			Rows.add(new Integer(i));
		}
		
		//System.out.println(Columns.size() + "  "+ Rows.size());
		
		isSelected = new int [Columns.size()][Rows.size()];
		

		
		
		this.setVisible(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);
		
		this.addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				updateSelection();
				
			}

			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});


	}
	
	
	
	
	
	
	int calcCount() {
		int res = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix [0].length; j++){
				res+=matrix [i][j];
			}
		}
		return res;
	}
	
	
	
	
	
	

	public boolean isPointInRect(int x, int y, Point point1, Point point2) {
		if ((point1.x <= x) && (point2.x >= x) && (point1.y <= y)
				&& (point2.y >= y))
			return true;
		else
			return false;
	}

	public boolean containsRectInRect(int x1, int y1, int x2, int y2, int Sx1,
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

	public boolean isLineInRect(int x1, int y1, int x2, int y2, int Rx1,
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

	public boolean isPointInRect(int x, int y, int Rx1, int Ry1, int Rx2,
			int Ry2) {
		if ((Rx1 <= x) && (Rx2 >= x) && (Ry1 <= y) && (Ry2 >= y))
			return true;
		else
			return false;
	}

	public void addSelection(Point point1, Point point2) {
		
		
	    
        int width = this.getWidth() - 25;
        int height = this.getHeight() - 25;
        
       
        
        
        
        int [][] counts = new int [matrix.length][matrix [0].length];
        for (int i = 0; i < matrix.length; i++) {
        	 for (int j = 0; j < matrix [0].length; j++) {
        
        	counts [i] [j] = matrix [Columns.elementAt(i)][Rows.elementAt(j)];
        	
        	 }
        }
        
        int max = 0;
        
        for (int i = 0; i < counts.length; i++) {
       	 for (int j = 0; j < counts [0].length; j++) {
       		if (max < counts [i][j]) max = counts [i][j];
       	 }
       	 }
     
        
    	seurat.dataManager.selectAll();
        for (int i = 0; i< Experiments1.clusters.size(); i++) {
        	for (int j = 0; j < ((Vector)Experiments1.clusters.elementAt(i)).size(); j++) {
        		((ISelectable)((Vector)Experiments1.clusters.elementAt(i)).elementAt(j)).unselect(true);
        	}
        	
        }
        
        
        for (int i = 0; i< Experiments2.clusters.size(); i++) {
        	for (int j = 0; j < ((Vector)Experiments2.clusters.elementAt(i)).size(); j++) {
        		((ISelectable)((Vector)Experiments2.clusters.elementAt(i)).elementAt(j)).unselect(true);
        	}
        	
        }
        
      
		
	
		for (int i = 0; i < Columns.size(); i++) {
			for (int j = 0; j < Rows.size(); j++) {
			   
				
				
		         
       		 int x = width* i/counts.length;
       		 int y = height * j/counts [0].length + 20;
       		 int w = (int)Math.round((double) 3/4 *width/counts.length * Math.sqrt(((double)counts [i][j] / max)));
       		 int h = (int)Math.round((double) 3/4 *height/counts [0].length * Math.sqrt((double)counts [i][j] / max));
             	
       		 int b = width/counts.length;
       	     int c = height/counts [0].length;
       	
       	     
       	     if (isPointInRect(x + (b-w)/2 +22,y + (c-h)/2,point1,point2) &&
       	    		isPointInRect(x + (b-w)/2 +22 + w,y + (c-h)/2 + h,point1,point2)) {
       	    	 selectRect(i,j);
       	     }
       	     
       	     
					
                
				
			}
		}
		
		
		seurat.repaintWindows();

	}
	
	
	
	public void selectRect(int col, int row) {
		
		
		
	
		
		Vector cluster1 = (Vector)Experiments1.clusters.elementAt(Columns.elementAt(col));
			
		Vector cluster2 = (Vector)Experiments2.clusters.elementAt(Rows.elementAt(row));
			
		for (int ii = 0; ii < cluster1.size(); ii++) {
				if (cluster2.indexOf(cluster1.elementAt(ii))!=-1) ((ISelectable)cluster1.elementAt(ii)).select(true);
				
		}
			
		
		
		
		
	}
	
	
	
	
	

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		point2 = e.getPoint();

		if (!e.isShiftDown()) seurat.dataManager.deleteSelection();
		
		this.addSelection(point1, point2);
		point1 = null;
		point2 = null;
	
	}

	public void mouseClicked(MouseEvent e) {
		point1 = e.getPoint();
		
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			JPopupMenu menu = new JPopupMenu();
			
			
			JMenuItem item = new JMenuItem("Original Order");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					originalOrder();
				}
			});
			menu.add(item);
			
			
			
			
			item = new JMenuItem("Permute Matrix");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					permute();
					
				}
			});
			menu.add(item);
			
			
			item = new JMenuItem("find Group");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					findGroup();
					
				}
			});
			menu.add(item);
			
			
			
			item = new JMenuItem("delete Groups");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					deleteGroups();
					
				}
			});
			menu.add(item);
			
			
			
			
			
			
			
		

			menu.show(this, e.getX(), e.getY());
			return;
		}
		
		
		
		
		
		
		int width = this.getWidth() - 25;
        int height = this.getHeight() - 25;
        
        int [][] counts = new int [matrix.length][matrix [0].length];
        for (int i = 0; i < matrix.length; i++) {
        	 for (int j = 0; j < matrix [0].length; j++) {
        
        	counts [i] [j] = matrix [Columns.elementAt(i)][Rows.elementAt(j)];
        	
        	 }
        }
		
		for(int i = 0; i < isSelected.length; i++) {
			for (int j = 0; j < isSelected [0].length; j++) {
				if (!e.isShiftDown()) isSelected [i][j] = 0;
			}
		}

		
		     
		int i = counts.length * (e.getX()-22)/ width;
		int j = (e.getY() - 20) * counts [0].length / height;
		
		isSelected [Columns.elementAt(i)][Rows.elementAt(j)] = counts [i][j];
		
     	
		if (i<0 || j<0 || i >= counts.length || j >= counts [0].length) return;
		
		
		if (!e.isShiftDown()) seurat.dataManager.deleteSelection();
		
		
		seurat.dataManager.selectAll();
        for (int ii = 0; ii< Experiments1.clusters.size(); ii++) {
        	for (int jj = 0; jj < ((Vector)Experiments1.clusters.elementAt(ii)).size(); jj++) {
        		((ISelectable)((Vector)Experiments1.clusters.elementAt(ii)).elementAt(jj)).unselect(true);
        	}
        	
        }
        
        
        for (int ii = 0; ii< Experiments2.clusters.size(); ii++) {
        	for (int jj = 0; jj < ((Vector)Experiments2.clusters.elementAt(ii)).size(); jj++) {
        		((ISelectable)((Vector)Experiments2.clusters.elementAt(ii)).elementAt(jj)).unselect(true);
        	}
        	
        }
        
        
        
        
		
			Vector cluster1 = (Vector)Experiments1.clusters.elementAt(Columns.elementAt(i));
				
			Vector cluster2 = (Vector)Experiments2.clusters.elementAt(Rows.elementAt(j));
				
			for (int ii = 0; ii < cluster1.size(); ii++) {
					if (cluster2.indexOf(cluster1.elementAt(ii))!=-1) ((ISelectable)cluster1.elementAt(ii)).select(true);
					
			}
				
				
	   seurat.repaintWindows();
		
	
		
		
		
		
	}
	
	
	
	
	
	public void originalOrder() {
		Columns = new Vector();
		Rows = new Vector();
		
		for (int i = 0; i < matrix.length; i++) {
			Columns.add(new Integer(i));
		}
		
		
		
		for (int i = 0; i < matrix [0].length; i++) {
			Rows.add(new Integer(i));
		}
		
		ClusterColumns = null;
		ClusterRows = null;
	}
	
	public void permute() {
		   VIEW = false; 		
      	 
	double crit = -1;
	System.out.println("Permute Matrix");
	   while (crit != calculateCriterium(Columns,Rows)) { 
		   crit = calculateCriterium(Columns,Rows);
       	Matrix4 m = new Matrix4(Columns,Rows,false); 
       	Columns = m.Columns;
       	Rows = m.Rows;
       System.out.println("--->  "+crit);
       	
	   }
	   repaint();
	   
	}
	
	public void findGroup() {
		calculateGroups();
		repaint();
	}
	
	public void deleteGroup() {
		repaint();
	}
	
	
	
	
	public void deleteGroups() {
		ClusterColumns = null;
		ClusterRows = null;
		repaint();
	}
	
	
	
	
	
	

	public void mouseDragged(MouseEvent e) {
		point2 = e.getPoint();
		this.repaint();

	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyChar() == '1') sortMethod = 1;
		if (e.getKeyChar() == '2') sortMethod = 2;
		

		if (e.getKeyChar() == '0') sortMethod = 0;
		if (e.getKeyChar() == '3') sortMethod = 3;
		
		
		
		
		
		
		if (e.getKeyChar() == 'c') {
			VIEW = !VIEW;
			
		
	        if (!VIEW) { 		
	        	
	        	
	        	
	     
	        	 
	        	 
	        	Matrix1 m = new Matrix1(Columns,Rows); 
	        	Columns = m.perColumns;
	        	Rows = m.perRows;
	        	System.out.println("Pfad  " + calculateCriterium(Columns,Rows));
	        	
	        }
	        else {
	        	
	        	Columns = new Vector();
	    		Rows = new Vector();
	    		
	    		for (int i = 0; i < matrix.length; i++) {
	    			Columns.add(new Integer(i));
	    		}
	    		
	    		
	    		
	    		for (int i = 0; i < matrix [0].length; i++) {
	    			Rows.add(new Integer(i));
	    		}
	    		
	    		ClusterColumns = null;
	    		ClusterRows = null;
	    		
	    		
	    		
	        }
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if (e.getKeyChar() == 'x') {
			VIEW = !VIEW;
			
		
	        if (!VIEW) { 		
	        	
	        	
	        	
	     
	        	 
	        	 
	        	Matrix m = new Matrix(Columns,Rows); 
	        	Columns = m.perColumns;
	        	Rows = m.perRows;
	        	
	        }
	        else {
	        	
	        	Columns = new Vector();
	    		Rows = new Vector();
	    		
	    		for (int i = 0; i < matrix.length; i++) {
	    			Columns.add(new Integer(i));
	    		}
	    		
	    		
	    		
	    		for (int i = 0; i < matrix [0].length; i++) {
	    			Rows.add(new Integer(i));
	    		}
	    		
	    		ClusterColumns = new Vector();
	    		ClusterRows = new Vector();
	    		
	    		
	    		
	        }
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		if (e.getKeyChar() == 'r') {
			
		
	        VIEW = false; 		
	        	 
	        	Matrix3 m = new Matrix3(Columns,Rows,false); 
	        	Columns = m.Columns;
	        	Rows = m.Rows;
	        	
	        
	 
			
			
		}
		
		
		
		
		
		if (e.getKeyChar() == 'q') {
			
			
	         this.calculateGroups();
	        	
	        
	 
			
			
		}
		
		
		
		
		
		
		
		
		if (e.getKeyChar() == 't') {
			
			
	        VIEW = false; 		
	        	 
	        	Matrix3 m = new Matrix3(Columns,Rows,true); 
	        	Columns = m.Columns;
	        	Rows = m.Rows;
	        	
	        
	 
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if (e.getKeyChar() == 'z') {
			
		
	        VIEW = false; 		
	        	 
	        	Matrix4 m = new Matrix4(Columns,Rows,false); 
	        	Columns = m.Columns;
	        	Rows = m.Rows;
	        	
	        
	 
			
			
		}
		
		
		
		
		if (e.getKeyChar() == 'u') {
			
			
	        VIEW = false; 		
	        	 
	        	Matrix4 m = new Matrix4(Columns,Rows,true); 
	        	Columns = m.Columns;
	        	Rows = m.Rows;
	        	
	        
	 
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		
	   	 if (!VIEW) { 	
			    
			
			 
			    if (e.getKeyCode() == 38) {
			    	
			    	gen = gen+0.5;
			    }
			    else if (e.getKeyCode() == 40) gen = gen-0.5;
				   
			    System.out.println(gen);
			    
			    Columns = new Vector();
	    		Rows = new Vector();
	    		
	    		for (int i = 0; i < matrix.length; i++) {
	    			Columns.add(new Integer(i));
	    		}
	    		
	    		
	    		
	    		for (int i = 0; i < matrix [0].length; i++) {
	    			Rows.add(new Integer(i));
	    		}
			 
			 
	        	Matrix1 m = new Matrix1(Columns,Rows); 
	        	Columns = m.perColumns;
	        	Rows = m.perRows;
	        	
	        	
	        
	        }*/
    	 
	   	 
	   	 
	   	 

	   	 
	   	 
		this.repaint();

	}

	public void keyTyped(KeyEvent e) {
		
		
		
		 
		 
	}

	public void keyReleased(KeyEvent e) {
	}

	
	
	
	
	
	
	

	public void calculateGroups() {
		
		int col=-1, row = -1;
		double max = 0;
		int aa = -1;
		
		 count = this.calcCount();
		 
		 
		if (ClusterColumns == null || ClusterColumns.size() < 1) {
			ClusterColumns = new Vector();
			ClusterRows = new Vector();
			
			
			
			Vector v = new Vector();
			
			for (int i = 0; i < Columns.size(); i++) {
				
				v.add(Columns.elementAt(i));
			}
			
			ClusterColumns.add(v);
			
			v = new Vector();
			
			for (int i = 0; i < Rows.size(); i++) {
				
				v.add(Rows.elementAt(i));
				
			}
			ClusterRows.add(v);
			
		} 
		 
		
		for (int a = 0; a < ClusterColumns.size(); a++) {
			
			System.out.println(" a:  " + a);
		
		Vector<Integer> ClusterCol = ClusterColumns.elementAt(a);
		Vector<Integer> ClusterRow = ClusterRows.elementAt(a);
		
		
		for (int i = 1; i < ClusterCol.size(); i++) {
			for (int j =1;j < ClusterRow.size(); j++) {
				
				Vector ClusterRowsNew = new Vector();
				Vector ClusterColumnsNew = new Vector();
				
				
				Vector<Integer> Col1 = new Vector();
				for (int ii = 0; ii < i ; ii++){
					Col1.add(ClusterCol.elementAt(ii));
				}
				
				Vector<Integer> Col2 = new Vector();
				for (int ii = i; ii < ClusterCol.size() ; ii++){
					Col2.add(ClusterCol.elementAt(ii));
				}
				
				
				
				
				
				Vector<Integer> Row1 = new Vector();
				for (int ii = 0; ii < j ; ii++){
					Row1.add(ClusterRow.elementAt(ii));
				}
				
				Vector<Integer> Row2 = new Vector();
				for (int ii = j; ii < ClusterRow.size() ; ii++){
					Row2.add(ClusterRow.elementAt(ii));
				}
				
				
				for (int k  =0; k < ClusterColumns.size(); k++) {
					if (k!=a){
						ClusterColumnsNew.add(ClusterColumns.elementAt(k));
						ClusterRowsNew.add(ClusterRows.elementAt(k));
					}
					else {
						ClusterColumnsNew.add(Col1);
						ClusterColumnsNew.add(Col2);
						
						ClusterRowsNew.add(Row1);
						ClusterRowsNew.add(Row2);
					}
				}
				
			
				
				
				double crit = groupsKriterium(ClusterColumnsNew,ClusterRowsNew);
				System.out.println(max + " ____ " + crit + " i: " + i +   "  j:  "+ j);
				
				if (crit>max) {
					max = crit;
					col = i;
					row = j;
					aa = a;
				}
				
				
				
				
			}
		}
		
		
		
		
		
		
		
		
		}
		
		
		
		
		
		
		
		Vector ClusterRowsNew = new Vector();
		Vector ClusterColumnsNew = new Vector();
		
		
		
		Vector<Integer> ClusterCol = ClusterColumns.elementAt(aa);
		Vector<Integer> ClusterRow = ClusterRows.elementAt(aa);
		
		Vector<Integer> Col1 = new Vector();
		for (int ii = 0; ii < col ; ii++){
			Col1.add(ClusterCol.elementAt(ii));
		}
		
		Vector<Integer> Col2 = new Vector();
		for (int ii = col; ii < ClusterCol.size() ; ii++){
			Col2.add(ClusterCol.elementAt(ii));
		}
		
		
		
		
		
		Vector<Integer> Row1 = new Vector();
		for (int ii = 0; ii < row ; ii++){
			Row1.add(ClusterRow.elementAt(ii));
		}
		
		Vector<Integer> Row2 = new Vector();
		for (int ii = row; ii < ClusterRow.size() ; ii++){
			Row2.add(ClusterRow.elementAt(ii));
		}
		
		
		for (int k  =0; k < ClusterColumns.size(); k++) {
			if (k!=aa){
				ClusterColumnsNew.add(ClusterColumns.elementAt(k));
				ClusterRowsNew.add(ClusterRows.elementAt(k));
			}
			else {
				ClusterColumnsNew.add(Col1);
				ClusterColumnsNew.add(Col2);
				
				ClusterRowsNew.add(Row1);
				ClusterRowsNew.add(Row2);
			}
		}
		
		
		
		
		ClusterColumns = ClusterColumnsNew;
		ClusterRows = ClusterRowsNew;
		
		
		
		
		
		
	}
	

	
	
	
	
	public double groupsKriterium(Vector<Vector<Integer>> Columns, Vector<Vector<Integer>> Rows) {
		
		int in = 0;
		for (int i = 0; i < Columns.size(); i++) {
			Vector<Integer> ColCluster = Columns.elementAt(i);
			Vector<Integer> RowCluster = Rows.elementAt(i);
			for (int ii = 0; ii < ColCluster.size(); ii++){

				for (int j  = 0; j < RowCluster.size(); j++) {
					in+= matrix [ColCluster.elementAt(ii)][RowCluster.elementAt(j)];
				}
				
			}
		}
		
		return in;
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	

	public void updateSelection() {

		for(int i = 0; i < isSelected.length; i++) {
			for (int j = 0; j < isSelected [0].length; j++) {
			     isSelected [i][j] = 0;
			}
		}
		
		matrix = new int [Experiments1.clusters.size()][Experiments2.clusters.size()];
		for (int i =0; i < Experiments1.clusters.size(); i++) {
			Vector cluster1 = (Vector)Experiments1.clusters.elementAt(i);
			
			for (int j =0; j < Experiments2.clusters.size(); j++) {
			
				
				Vector cluster2 = (Vector)Experiments2.clusters.elementAt(j);
				
				for (int ii = 0; ii < cluster1.size(); ii++) {
					if (cluster2.indexOf(cluster1.elementAt(ii))!=-1) {
						matrix [i][j]++;
						if (((ISelectable)cluster1.elementAt(ii)).isSelected()) isSelected [i][j]++;
					}
					
				}
				
				
			}
		}
	
			
		
		
	
		
		
		
		
		
		this.repaint();

	}

	@Override
	public void paint(Graphics g) {
	
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (matrix == null) {this.updateSelection();
        count = this.calcCount();
        
        }
        
        g.setColor(Color.black);
        
        
        int width = this.getWidth() - 25;
        int height = this.getHeight() - 25;
        
        
        g.drawLine(20, 22, this.getWidth()-3 , 22);
	    
	    g.drawLine(20, 22 , 20,   this.getHeight()-5);
		
	    
	
	 
        
	    
	    
        
        
            
            int [][] counts = new int [matrix.length][matrix [0].length];
            for (int i = 0; i < matrix.length; i++) {
            	 for (int j = 0; j < matrix [0].length; j++) {
            
            	counts [i] [j] = matrix [Columns.elementAt(i)][Rows.elementAt(j)];
            	
            	 }
            }
            
            int max = 0;
            
            for (int i = 0; i < counts.length; i++) {
           	 for (int j = 0; j < counts [0].length; j++) {
           		if (max < counts [i][j]) max = counts [i][j];
           	 }
           	 }
            
            
            
           
            
           
            for (int i = 0; i < counts.length; i++) {
            	g.setColor(Color.black);
            	
            	g.drawString(""+(Columns.elementAt(i)+1) + "", width* i/counts.length+width/counts.length/2 + 22, 20);
            	
            	 for (int j = 0; j < counts [0].length; j++) {
            		 
            		 
            		 
            		 	g.setColor(Color.black);
                    	g.drawString((Rows.elementAt(j)+1) + "", 5, height * j/counts [0].length + 20 + height/counts [0].length/2);
                    	
                    
            		 int x = width* i/counts.length;
            		 int y = height * j/counts [0].length + 20;
            		 int w = (int)Math.round((double) 3/4 *width/counts.length * Math.sqrt(((double)counts [i][j] / max)));
            		 int h = (int)Math.round((double) 3/4 *height/counts [0].length * Math.sqrt((double)counts [i][j] / max));
                  	
            		 int b = width/counts.length;
            	     int c = height/counts [0].length;
            		 
            	     
            	     g.drawLine(width* (i+1)/counts.length+22, 22, width* (i+1)/counts.length+22, this.getHeight()-5);
            	     
            	     g.drawLine(20, height * (j+1)/counts [0].length + 20, this.getWidth()-3, height * (j+1)/counts [0].length + 20);
                  	
            		 
            	     
            	     
            	 
            		 g.setColor(Color.GRAY);
            	//	 else g.setColor(new Color(144,155,222));
            		 
            		 
                 	g.fillRect(x + (b-w)/2 +22,y + (c-h)/2,w,h);
                 	
                 	
                 	g.setColor(Color.RED);
                 	if (isSelected [Columns.elementAt(i)][Rows.elementAt(j)] > 0) {
                 		double koeff = (double)isSelected [Columns.elementAt(i)][Rows.elementAt(j)] / counts [i][j];
                 		
                 		g.fillRect(x + (b-w)/2 +22,(int)Math.round(y + (c+h)/2 - h*koeff),w,(int)Math.round(h*koeff));
                        
                 	}
                 	
                 	
                 	 g.setColor(Color.black);
             		
                  	if (w != 0) g.drawRect(x + (b-w)/2 +22,y + (c-h)/2,w,h);
            	     
            	 
            	 /*
            	     else {
            	    	 g.setColor(Color.black);
            	    	 g.drawString(matrix [i][j]+"", x + b/2+22, y+c/2);
            	    	 
            	     }
            	     
            	     */
                 	
                 //	if (counts [i][j] * 100 / points.size()>=1) g.setColor(Color.red);
                 //	else g.setColor(Color.black);
            	//	g.drawString((double)Math.round((double)counts [i][j] * 10000 / points.size())/100 +  " ", this.getWidth()* i/11+ 3 +(int)Math.round((double) 3/4 *this.getWidth()/11 * counts [i][j] / max), (this.getHeight() - this.getHeight() * j/11)- this.getHeight() /22);
            		 
                 }
            }
            
            
            
        
        
        
        
        
            int colShift = 0;
            int rowShift = 0;
            if (ClusterColumns != null) {
            	
            	
            
            for (int i = 0; i < ClusterColumns.size(); i++ ){
            	int shiftCol = ClusterColumns.elementAt(i).size();
            	int shiftRows = ClusterRows.elementAt(i).size();
            	
            
            	
            	g.setColor(Color.RED);
            	
            	g.drawRect(
            			width* colShift/counts.length+22,
            			
            			height * rowShift/counts [0].length + 20,
            			
            			width* shiftCol/counts.length,
            			
            			height * shiftRows/counts [0].length 
            			);
            			
            	g.drawRect(
            			width* colShift/counts.length+22+1,
            			
            			height * rowShift/counts [0].length + 20+1,
            			
            			width* shiftCol/counts.length,
            			
            			height * shiftRows/counts [0].length 
            			);
            	
            	g.drawRect(
            			width* colShift/counts.length+22-1,
            			
            			height * rowShift/counts [0].length + 20-1,
            			
            			width* shiftCol/counts.length,
            			
            			height * shiftRows/counts [0].length 
            			);
            	
            	
            	
            			
            	colShift+= shiftCol;
            	rowShift+=shiftRows;
         		 
            }
            	
            	
            }
            
        
        
        
        
		
		
		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math.abs(point2.y
					- point1.y));
		}


	}

	
	
	
	
	
	/**
	 * 
	 *    Bestrafung falscher Ausrichtung
	 * 
	 * 
	 * */
	
	public double calculateCriterium(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
		double crit = 0;
		
		for (int i =1; i < ClusterColumns.size(); i++) {
		    for (int j =0; j < ClusterRows.size()-1; j++) {
				
				
				
		    	for (int ii =0; ii < i; ii++) {
		    	for (int jj =j+1; jj < ClusterRows.size(); jj++) {
		    	    	
		    	    	
		    	    
						int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
						int b = matrix [ClusterColumns.elementAt(ii)][ClusterRows.elementAt(jj)];
						
						crit+= a*b;
						
					}
					
					
					
				
				}}
				
			}
	   
			
			
			
			
			
	        
	        
			return crit;
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public String getToolTipText(MouseEvent e) {
		
		if (!e.isControlDown()) return null;
		
		int width = this.getWidth() - 25;
        int height = this.getHeight() - 25;
        
        int [][] counts = new int [matrix.length][matrix [0].length];
        for (int i = 0; i < matrix.length; i++) {
        	 for (int j = 0; j < matrix [0].length; j++) {
        
        	counts [i] [j] = matrix [Columns.elementAt(i)][Rows.elementAt(j)];
        	
        	 }
        }
		
		
		
		int i = counts.length * e.getX()/ width;
		int j = (e.getY() - 20) * counts [0].length / height;
     	
		if (i<0 || j<0 || i >= counts.length || j >= counts [0].length) return null;
		
		
		if (e.isControlDown()) return counts [i][j]+"";
		return null;
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void brush() {
		// TODO Auto-generated method stub
		
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	class Matrix {
	
		
		
		Vector<Integer> perColumns = new Vector();
		Vector <Integer> perRows = new Vector();
	
		
		Vector<Integer> Columns;
		Vector <Integer> Rows;
		
		
		Vector<Integer> TempColumns = new Vector();
		Vector<Integer> TempRows = new Vector();
		
		
		Vector sortInClusterColumns = new Vector();
		Vector sortInClusterRows = new Vector();
		
		
		public Matrix(Vector<Integer> Columns, Vector <Integer> Rows) {
		    ClusterRows = new Vector();
		    ClusterColumns = new Vector();
			
		    
		    this.Columns = Columns;
		    this.Rows = Rows;
		    
			newCluster();
			
			if (sortMethod == 1) sortClusters1();
			if (sortMethod == 2) sortClusters2();
				
			
			/*
			if (sortMethod==1 || sortMethod == 2) {
				
				for (int i = 0; i < ClusterColumns.size(); i++) {
					
					Vector colCluster = new Vector();
					Vector rowCluster = new Vector();
					sortInClusters(ClusterColumns.elementAt(i),ClusterRows.elementAt(i), colCluster, rowCluster, gen/2);
				   
					Vector resColCluster = new Vector();
					Vector resRowCluster = new Vector();
					for (int ii = 0; ii< colCluster.size(); ii++) {
						for (int j =0; j < ((Vector)colCluster.elementAt(ii)).size(); j++) {
							resColCluster.add(((Vector)colCluster.elementAt(ii)).elementAt(j));
						}
						
						
					}
					
					
					for (int ii = 0; ii< rowCluster.size(); ii++) {
						for (int j =0; j < ((Vector)rowCluster.elementAt(ii)).size(); j++) {
							resRowCluster.add(((Vector)rowCluster.elementAt(ii)).elementAt(j));
						}
						
						
					}
					
					
					
					ClusterColumns.set(i,resColCluster);
				    ClusterRows.set(i,resRowCluster);
				
				}
				
				
				
				
				
			
			
			}	*/
			
			for (int i = 0; i < ClusterColumns.size(); i++) {
				for (int j = 0; j < ClusterColumns.elementAt(i).size(); j++) {
					perColumns.add(ClusterColumns.elementAt(i).elementAt(j));
				}
			}
			
			
			
			for (int i = 0; i < ClusterRows.size(); i++) {
				for (int j = 0; j < ClusterRows.elementAt(i).size(); j++) {
					perRows.add(ClusterRows.elementAt(i).elementAt(j));
				}
			}
			
			/*
			if (sortMethod==1 || sortMethod == 0) {
				
				
			perColumns = new Vector();
			perRows = new Vector();
			for (int i = 0; i < sortInClusterColumns.size(); i++) {
				perColumns.add((Integer)sortInClusterColumns.elementAt(i));
			}
			
			
			
			for (int i = 0; i < sortInClusterRows.size(); i++) {
				perRows.add((Integer)sortInClusterRows.elementAt(i));
			}
		}*/
			
			
			
		}
		
		
		
		
		
		public void sortInClusters(Vector<Integer> columnsToSort, Vector<Integer> rowsToSort,Vector resColumns,Vector resRows, double param) {
			
		    Columns = columnsToSort;
		    Rows = rowsToSort;
			
		    
		    System.out.println(Columns.size() + " " + Rows.size() +  " " + param);
			
		    
		    while (columnsToSort.size() != 0 && rowsToSort.size() != 0) {
		    	
		    	
		    	int maxC=-1;
				int maxR=-1;
				int max = -1;
				
			
			/**find max*/
			for (int i = 0; i < columnsToSort.size(); i++) {
				for (int j = 0; j < rowsToSort.size(); j++) {
					if (max < matrix [columnsToSort.elementAt(i)][rowsToSort.elementAt(j)]) {
					   max = matrix [columnsToSort.elementAt(i)][rowsToSort.elementAt(j)];
					   maxC = i;
					   maxR = j;
					}
					
				}
			}
			
			
			

			TempColumns = new Vector();
			TempRows = new Vector();
			
			
			
			
			TempColumns.add(columnsToSort.elementAt(maxC));
			columnsToSort.remove(maxC);
			
			
			
			
			TempRows.add(rowsToSort.elementAt(maxR));
		    rowsToSort.remove(maxR);
			
			
		    Rows = rowsToSort;
		    Columns = columnsToSort;
			
			
		    
		    Vector<Integer> newColCluster = new Vector();
		    Vector<Integer> newRowCluster = new Vector();
		    
			searchNew(newColCluster,newRowCluster);
			
			
			
			 rowsToSort = Rows;
			 columnsToSort = Columns;
				
				
			
			
			resColumns.add(newColCluster);
			resRows.add(newRowCluster);
			
			
			
		//	System.out.println();
		//	System.out.println();
			
		    }
			
				
				
				
				for (int i = 0; i < columnsToSort.size(); i++) {
					 
					int col = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < resColumns.size(); j++) {
						   Vector<Integer> clusterC = (Vector)resColumns.elementAt(j);
						   Vector<Integer> clusterR = (Vector)resRows.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterR.size();k++) {
							   schnittT+=matrix [columnsToSort.elementAt(i)][clusterR.elementAt(k)];
						   }
						   schnittT/= clusterR.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   col = j;  
						   }
						   
					   }
		
					
		               ((Vector)resColumns.elementAt(col)).add(columnsToSort.elementAt(i));
				}
				
				
                for (int i = 0; i < rowsToSort.size(); i++) {
                	int row = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < resRows.size(); j++) {
						 
						   Vector<Integer> clusterR = (Vector)resRows.elementAt(j);
						   Vector<Integer> clusterC = (Vector)resColumns.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterC.size();k++) {
							   schnittT+=matrix [clusterC.elementAt(k)] [rowsToSort.elementAt(i)];
						   }
						   schnittT/= clusterC.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   row = j;  
						   }
						   
					   }
		
					
		               ((Vector)resRows.elementAt(row)).add(rowsToSort.elementAt(i));
				}


/*
				
			    for (int i = 0; i < resColumns.size(); i++) {
			    	Vector cols = ((Vector)resColumns.elementAt(i));
			    	Vector rows = ((Vector)resRows.elementAt(i));
			    	if (cols.size()<=2 || rows.size() <= 2 || param <= 1) {
			    		for (int j = 0; j < cols.size(); j++) {
			    			sortInClusterColumns.add(cols.elementAt(j));
			    		}
			    		
			    		for (int j = 0; j < rows.size(); j++) {
			    			sortInClusterRows.add(rows.elementAt(j));
			    		}
			    		
			    		
			    	}
			    	else sortInClusters(cols,rows,new Vector(), new Vector(), param/2);
			    	
			    	
			    	
			    	
			    }
			*/
			
			
		}

		
		
		
		public void sortClusters1() {
			Vector colClusters = new Vector();
			Vector rowClusters = new Vector();
			
			colClusters.add(ClusterColumns.firstElement());
			rowClusters.add(ClusterRows.firstElement());
			
			ClusterColumns.remove(0);
			ClusterRows.remove(0);
			
			while (ClusterRows.size() != 0) {
				   Vector<Integer> clusterC = (Vector)colClusters.lastElement();
			        Vector<Integer> clusterR = (Vector)rowClusters.lastElement();
			 	
			        
			        double schnitt = -1;
				
			        int row  = 0;
			    for (int i = 0; i < ClusterRows.size(); i++) {
			        Vector<Integer> clusterR2 = ClusterRows.elementAt(i);
			        Vector<Integer> clusterC2 = ClusterColumns.elementAt(i);
			      
			        int a = 0;
			        double schnittT = 0; 
			        for (int r= 0; r < clusterR.size(); r++) {
			            for (int c = 0; c < clusterC2.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC2.elementAt(c)][clusterR.elementAt(r)];
				        }
			        }
			        
			        for (int r= 0; r < clusterR2.size(); r++) {
			            for (int c = 0; c < clusterC.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC.elementAt(c)][clusterR2.elementAt(r)];
				        }
			        }
			        
			        
			        schnittT/=a;
			        
			        
			        if (schnittT > schnitt) {
			        	row = i;
			        	schnitt = schnittT;
			        } 
			    
		     	}
			    
			    
			    colClusters.add(ClusterColumns.elementAt(row));
			    rowClusters.add(ClusterRows.elementAt(row));
			    ClusterColumns.remove(row);
			    ClusterRows.remove(row);
				    
			    
			    
			
			}
			
			ClusterColumns = colClusters;
			ClusterRows = rowClusters;
			
			
		};
		
		
		
		

		public void sortClusters2() {
			Vector colClusters = new Vector();
			Vector rowClusters = new Vector();
			
			colClusters.add(ClusterColumns.firstElement());
			rowClusters.add(ClusterRows.firstElement());
			
			ClusterColumns.remove(0);
			ClusterRows.remove(0);
			
			while (ClusterRows.size() != 0) {
				   Vector<Integer> clusterC = (Vector)colClusters.lastElement();
			        Vector<Integer> clusterR = (Vector)rowClusters.lastElement();
			 	
			        
			        double schnitt = -1;
				
			        int row  = 0;
			    for (int i = 0; i < ClusterRows.size(); i++) {
			        Vector<Integer> clusterR2 = ClusterRows.elementAt(i);
			        Vector<Integer> clusterC2 = ClusterColumns.elementAt(i);
			      
			        int a = 0;
			        double schnittT = 0; 
			        for (int r= 0; r < clusterR.size(); r++) {
			            for (int c = 0; c < clusterC2.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC2.elementAt(c)][clusterR.elementAt(r)];
				        }
			        }
			        
			        for (int r= 0; r < clusterR2.size(); r++) {
			            for (int c = 0; c < clusterC.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC.elementAt(c)][clusterR2.elementAt(r)];
				        }
			        }
			        
			        
			         
			        
			        if (schnittT > schnitt) {
			        	row = i;
			        	schnitt = schnittT;
			        } 
			    
		     	}
			    
			 
			    colClusters.add(ClusterColumns.elementAt(row));
			    rowClusters.add(ClusterRows.elementAt(row));
			    ClusterColumns.remove(row);
			    ClusterRows.remove(row);
				    
			    
			    
			
			}
			
			ClusterColumns = colClusters;
			ClusterRows = rowClusters;
			
			
		};
		
		
		
		
		
		

		
		/**
		 * 
		 */
		public void newCluster() {
			
			int maxC=-1;
			int maxR=-1;
			int max = -1;
			
			
		    ClusterRows.add(new Vector());
		    ClusterColumns.add(new Vector());
		    
		    
		    
			
			/**find max*/
			for (int i = 0; i < Columns.size(); i++) {
				for (int j = 0; j < Rows.size(); j++) {
					if (max < matrix [Columns.elementAt(i)][Rows.elementAt(j)]) {
					   max = matrix [Columns.elementAt(i)][Rows.elementAt(j)];
					   maxC = i;
					   maxR = j;
					}
					
				}
			}
			
			
		
		
			
			
			/*erster Cluster**/
			
			
			
			
			/*
			Integer maxCol = Columns.elementAt(maxC);
			Integer maxRow = Rows.elementAt(maxR);
			
			

			  
			ClusterColumns.lastElement().add(maxCol);
		    ClusterRows.lastElement().add(maxRow);
			  
			  
			Columns.remove(maxC);
			Rows.remove(maxR);
			
			/*
			for (int i = 0; i < Columns.size(); i++) {
			      if (max/gen < matrix [Columns.elementAt(i)][maxRow]) {
			
   	//  System.out.print(Columns.elementAt(i) + " ");
			    	  ClusterColumns.lastElement().add(Columns.elementAt(i));
			      }
			      else restColumns.add(Columns.elementAt(i));
			
			}
			
		//	System.out.println();
			
			
			for (int i = 0; i < Rows.size(); i++) {
			      if (max/gen < matrix [maxCol][Rows.elementAt(i)]) {
			
    //	  System.out.print(Rows.elementAt(i));
			    	  ClusterRows.lastElement().add(Rows.elementAt(i));
			      }
			      else restRows.add(Rows.elementAt(i));
			
			}
			*/
			

		//	TempColumns = new Vector();
		//	TempRows = new Vector();
			
			
			
			
			ClusterColumns.lastElement().add(Columns.elementAt(maxC));
			Columns.remove(maxC);
			
			ClusterRows.lastElement().add(Rows.elementAt(maxR));
		    Rows.remove(maxR);
			
			
			
			
			searchNew(ClusterColumns.lastElement(),ClusterRows.lastElement());
			
			
			
			
			
			
			
			
		//	System.out.println();
		//	System.out.println();
			
			
			if (Columns.size() != 0 && Rows.size()!= 0) 
			
			newCluster();
			
			else {
				
				
				
				for (int i = 0; i < Columns.size(); i++) {
					 
					int col = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < ClusterColumns.size(); j++) {
						   Vector<Integer> clusterC = (Vector)ClusterColumns.elementAt(j);
						   Vector<Integer> clusterR = (Vector)ClusterRows.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterR.size();k++) {
							   schnittT+=matrix [Columns.elementAt(i)][clusterR.elementAt(k)];
						   }
						   schnittT/= clusterR.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   col = j;  
						   }
						   
					   }
		
					
		               ClusterColumns.elementAt(col).add(Columns.elementAt(i));
				}
				
				
                for (int i = 0; i < Rows.size(); i++) {
                	int row = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < ClusterRows.size(); j++) {
						   Vector<Integer> clusterR = (Vector)ClusterRows.elementAt(j);
						   Vector<Integer> clusterC = (Vector)ClusterColumns.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterC.size();k++) {
							   schnittT+=matrix [clusterC.elementAt(k)] [Rows.elementAt(i)];
						   }
						   schnittT/= clusterC.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   row = j;  
						   }
						   
					   }
		
					
		               ClusterRows.elementAt(row).add(Rows.elementAt(i));
				}



				
			}
			
			
			
			}
			
			
		
		
		
	
	
	
	
	
	/*
	
	
	
	
	public void searchNew(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows, int max) {
		
		
		
		if (TempColumns.size() == 0 && TempRows.size() == 0) return;
		
	
		if (TempColumns.size() != 0) {
			
			
			Integer col = TempColumns.elementAt(0);
			Vector newRestRows = new Vector();
			
			for (int i = Rows.size()-1; i>=0; i--) {
			      if (max/gen < matrix [col][Rows.elementAt(i)]) {
			
			    	  TempRows.add(Rows.elementAt(i));
			      }
			      else newRestRows.add(Rows.elementAt(i));
			
			}
			
			
			Rows = newRestRows;
			
			ClusterColumns.add(col);
			TempColumns.remove(0);
			
			searchNew(ClusterColumns,ClusterRows, max);
			return;
			
		}
		
	
		Integer row = TempRows.elementAt(0);
		
	
		Vector newRestColumns = new Vector();
	
		
		
		for (int i = Columns.size()-1; i >= 0; i--) {
		      if (max/gen < matrix [Columns.elementAt(i)][row]) {
		
		    	  TempColumns.add(Columns.elementAt(i));
		      }
		      else newRestColumns.add(Columns.elementAt(i));
		
		}
		
		Columns = newRestColumns;
		
		
		
		
		
		ClusterRows.add(row);
		
		
		TempRows.remove(0);
		
		
		
		
		searchNew(ClusterColumns,ClusterRows, max);
		
		
		
		
		
	};
	
		*/
		
		
		
		
		
		public void searchNew(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
			
			
			    if (Columns.size() == 0 || Rows.size() == 0) return;
				
				
				double criterium = calculateCriterium(ClusterColumns,ClusterRows);
			
				double maxColCrit = -1000000000;
				int maxCol = -1;
				
				for (int i = 0; i < Columns.size() ; i++) {
					double crit = calculateCriterium(ClusterColumns,Columns.elementAt(i),ClusterRows);
					if (maxColCrit < crit) {
						maxColCrit = crit;
						maxCol = i;
					}
				}
				
				
				
				
				double maxRowCrit = -1000000000;
				int maxRow = -1;
				
				
				for (int i = 0; i < Rows.size() ; i++) {
					double crit = calculateCriterium(ClusterColumns,ClusterRows,Rows.elementAt(i));
					if (maxRowCrit < crit) {
						maxRowCrit = crit;
						maxRow = i;
					}
				}
				
				
				
			   if (maxRowCrit <= maxColCrit) {
				   if (criterium < maxColCrit) {
					   ClusterColumns.add(Columns.elementAt(maxCol));
					   Columns.remove(maxCol);
				   }
			   }	
			   
			   if (maxRowCrit > maxColCrit) {
				   if (criterium < maxRowCrit) {
					   ClusterRows.add(Rows.elementAt(maxRow));
					   Rows.remove(maxRow);
				   }
			   }	
			   
			   if (criterium >= maxColCrit  &&  criterium >= maxRowCrit) return;
			
			   
			   
			   
			
			searchNew(ClusterColumns,ClusterRows);
			
			
			
			
			
		};
		
		
		
		
		
		public double calculateCriterium(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
			double crit = 0;
			for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
				}
				
			}
          //  crit/=(ClusterColumns.size()*ClusterRows.size());
			
            
            double minus = 0;
			
        	for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < matrix [0].length; j++) {
					minus+= matrix [ClusterColumns.elementAt(i)][j];
				}
				
			}
        	for (int i =0; i < matrix.length; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					minus+= matrix [i][ClusterRows.elementAt(j)];
				}
				
			}
            
        	
        //	minus/=(matrix.length+matrix [0].length);
            
            
			return gen*crit-minus;
			
		}
		
		
		
		
		public double calculateCriterium(Vector<Integer>ClusterColumns ,Integer col,Vector<Integer> ClusterRows) {
			double crit = 0;
			for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
				}
				
				
			}
			
			for (int j = 0; j < ClusterRows.size(); j++) {
				crit+= matrix [col][ClusterRows.elementAt(j)];
			}
			
			
			
         //   crit/=((ClusterColumns.size()+1)*ClusterRows.size());
			
            
            double minus = 0;
			
        	for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < matrix [0].length; j++) {
					minus+= matrix [ClusterColumns.elementAt(i)][j];
				}
				
			}
        	
        	for (int j = 0; j < matrix [0].length; j++) {
				minus+= matrix [col][j];
			}
			
        	
        	
        	for (int i =0; i < matrix.length; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					minus+= matrix [i][ClusterRows.elementAt(j)];
				}
				
			}
            
        	
        //	minus/=(matrix.length+matrix [0].length+1);
            
        
			return gen*crit-minus;
			
		}
		
		
		
		
		public double calculateCriterium(Vector<Integer>ClusterColumns,Vector<Integer> ClusterRows, Integer row) {
			double crit = 0;
			for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
				}
				crit+= matrix [ClusterColumns.elementAt(i)][row];
				
			}
			
			
			
			
			
           // crit/=((ClusterColumns.size())*(ClusterRows.size()+1));
			
            
            double minus = 0;
			
        	for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < matrix [0].length; j++) {
					minus+= matrix [ClusterColumns.elementAt(i)][j];
				}
				
			}
        	
        	
			
        	
        	
        	for (int i =0; i < matrix.length; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					minus+= matrix [i][ClusterRows.elementAt(j)];
				}
				minus+= matrix [i][row];
			}
            
        	
        	//minus/=(matrix.length+matrix [0].length+1);
            
            
			return gen*crit-minus;
			
		}
		
		
		
		
		
		
		
		
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
class Matrix1 {
	
		
		
		Vector<Integer> perColumns = new Vector();
		Vector <Integer> perRows = new Vector();
	
		
		Vector<Integer> Columns;
		Vector <Integer> Rows;
		
		
		Vector<Integer> TempColumns = new Vector();
		Vector<Integer> TempRows = new Vector();
		
		
		Vector sortInClusterColumns = new Vector();
		Vector sortInClusterRows = new Vector();
		
		
		public Matrix1(Vector<Integer> Columns, Vector <Integer> Rows) {
		    ClusterRows = new Vector();
		    ClusterColumns = new Vector();
			
		    
		    this.Columns = Columns;
		    this.Rows = Rows;
		    
			newCluster();
			
			if (sortMethod == 1) sortClusters1();
			if (sortMethod == 2) sortClusters2();
				
			
			
			if (sortMethod==1 || sortMethod == 2) {
				
				for (int i = 0; i < ClusterColumns.size(); i++) {
					
					Vector colCluster = new Vector();
					Vector rowCluster = new Vector();
					sortInClusters(ClusterColumns.elementAt(i),ClusterRows.elementAt(i), colCluster, rowCluster, gen/2);
				   
					Vector resColCluster = new Vector();
					Vector resRowCluster = new Vector();
					for (int ii = 0; ii< colCluster.size(); ii++) {
						for (int j =0; j < ((Vector)colCluster.elementAt(ii)).size(); j++) {
							resColCluster.add(((Vector)colCluster.elementAt(ii)).elementAt(j));
						}
						
						
					}
					
					
					for (int ii = 0; ii< rowCluster.size(); ii++) {
						for (int j =0; j < ((Vector)rowCluster.elementAt(ii)).size(); j++) {
							resRowCluster.add(((Vector)rowCluster.elementAt(ii)).elementAt(j));
						}
						
						
					}
					
					
					
					ClusterColumns.set(i,resColCluster);
				    ClusterRows.set(i,resRowCluster);
				
				}
				
				
				
				
				
			
			
			}	
			
			for (int i = 0; i < ClusterColumns.size(); i++) {
				for (int j = 0; j < ClusterColumns.elementAt(i).size(); j++) {
					perColumns.add(ClusterColumns.elementAt(i).elementAt(j));
				}
			}
			
			
			
			for (int i = 0; i < ClusterRows.size(); i++) {
				for (int j = 0; j < ClusterRows.elementAt(i).size(); j++) {
					perRows.add(ClusterRows.elementAt(i).elementAt(j));
				}
			}
			
			/*
			if (sortMethod==1 || sortMethod == 0) {
				
				
			perColumns = new Vector();
			perRows = new Vector();
			for (int i = 0; i < sortInClusterColumns.size(); i++) {
				perColumns.add((Integer)sortInClusterColumns.elementAt(i));
			}
			
			
			
			for (int i = 0; i < sortInClusterRows.size(); i++) {
				perRows.add((Integer)sortInClusterRows.elementAt(i));
			}
		}*/
			
			
			
		}
		
		
		
		
		
		public void sortInClusters(Vector<Integer> columnsToSort, Vector<Integer> rowsToSort,Vector resColumns,Vector resRows, double param) {
			
		    Columns = columnsToSort;
		    Rows = rowsToSort;
			
		    
		    System.out.println(Columns.size() + " " + Rows.size() +  " " + param);
			
		    
		    while (columnsToSort.size() != 0 && rowsToSort.size() != 0) {
		    	
		    	
		    	int maxC=-1;
				int maxR=-1;
				int max = -1;
				
			
			/**find max*/
			for (int i = 0; i < columnsToSort.size(); i++) {
				for (int j = 0; j < rowsToSort.size(); j++) {
					if (max < matrix [columnsToSort.elementAt(i)][rowsToSort.elementAt(j)]) {
					   max = matrix [columnsToSort.elementAt(i)][rowsToSort.elementAt(j)];
					   maxC = i;
					   maxR = j;
					}
					
				}
			}
			
			
			

			TempColumns = new Vector();
			TempRows = new Vector();
			
			
			
			
			TempColumns.add(columnsToSort.elementAt(maxC));
			columnsToSort.remove(maxC);
			
			
			
			
			TempRows.add(rowsToSort.elementAt(maxR));
		    rowsToSort.remove(maxR);
			
			
		    Rows = rowsToSort;
		    Columns = columnsToSort;
			
			
		    
		    Vector<Integer> newColCluster = new Vector();
		    Vector<Integer> newRowCluster = new Vector();
		    
			searchNew(newColCluster,newRowCluster,max);
			
			
			
			 rowsToSort = Rows;
			 columnsToSort = Columns;
				
				
			
			
			resColumns.add(newColCluster);
			resRows.add(newRowCluster);
			
			
			
		//	System.out.println();
		//	System.out.println();
			
		    }
			
				
				
				
				for (int i = 0; i < columnsToSort.size(); i++) {
					 
					int col = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < resColumns.size(); j++) {
						   Vector<Integer> clusterC = (Vector)resColumns.elementAt(j);
						   Vector<Integer> clusterR = (Vector)resRows.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterR.size();k++) {
							   schnittT+=matrix [columnsToSort.elementAt(i)][clusterR.elementAt(k)];
						   }
						   schnittT/= clusterR.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   col = j;  
						   }
						   
					   }
		
					
		               ((Vector)resColumns.elementAt(col)).add(columnsToSort.elementAt(i));
				}
				
				
                for (int i = 0; i < rowsToSort.size(); i++) {
                	int row = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < resRows.size(); j++) {
						 
						   Vector<Integer> clusterR = (Vector)resRows.elementAt(j);
						   Vector<Integer> clusterC = (Vector)resColumns.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterC.size();k++) {
							   schnittT+=matrix [clusterC.elementAt(k)] [rowsToSort.elementAt(i)];
						   }
						   schnittT/= clusterC.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   row = j;  
						   }
						   
					   }
		
					
		               ((Vector)resRows.elementAt(row)).add(rowsToSort.elementAt(i));
				}


/*
				
			    for (int i = 0; i < resColumns.size(); i++) {
			    	Vector cols = ((Vector)resColumns.elementAt(i));
			    	Vector rows = ((Vector)resRows.elementAt(i));
			    	if (cols.size()<=2 || rows.size() <= 2 || param <= 1) {
			    		for (int j = 0; j < cols.size(); j++) {
			    			sortInClusterColumns.add(cols.elementAt(j));
			    		}
			    		
			    		for (int j = 0; j < rows.size(); j++) {
			    			sortInClusterRows.add(rows.elementAt(j));
			    		}
			    		
			    		
			    	}
			    	else sortInClusters(cols,rows,new Vector(), new Vector(), param/2);
			    	
			    	
			    	
			    	
			    }
			*/
			
			
		}

		
		
		
		public void sortClusters1() {
			Vector colClusters = new Vector();
			Vector rowClusters = new Vector();
			
			colClusters.add(ClusterColumns.firstElement());
			rowClusters.add(ClusterRows.firstElement());
			
			ClusterColumns.remove(0);
			ClusterRows.remove(0);
			
			while (ClusterRows.size() != 0) {
				   Vector<Integer> clusterC = (Vector)colClusters.lastElement();
			        Vector<Integer> clusterR = (Vector)rowClusters.lastElement();
			 	
			        
			        double schnitt = -1;
				
			        int row  = 0;
			    for (int i = 0; i < ClusterRows.size(); i++) {
			        Vector<Integer> clusterR2 = ClusterRows.elementAt(i);
			        Vector<Integer> clusterC2 = ClusterColumns.elementAt(i);
			      
			        int a = 0;
			        double schnittT = 0; 
			        for (int r= 0; r < clusterR.size(); r++) {
			            for (int c = 0; c < clusterC2.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC2.elementAt(c)][clusterR.elementAt(r)];
				        }
			        }
			        
			        for (int r= 0; r < clusterR2.size(); r++) {
			            for (int c = 0; c < clusterC.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC.elementAt(c)][clusterR2.elementAt(r)];
				        }
			        }
			        
			        
			        schnittT/=a;
			        
			        
			        if (schnittT > schnitt) {
			        	row = i;
			        	schnitt = schnittT;
			        } 
			    
		     	}
			    
			    
			    colClusters.add(ClusterColumns.elementAt(row));
			    rowClusters.add(ClusterRows.elementAt(row));
			    ClusterColumns.remove(row);
			    ClusterRows.remove(row);
				    
			    
			    
			
			}
			
			ClusterColumns = colClusters;
			ClusterRows = rowClusters;
			
			
		};
		
		
		
		

		public void sortClusters2() {
			Vector colClusters = new Vector();
			Vector rowClusters = new Vector();
			
			colClusters.add(ClusterColumns.firstElement());
			rowClusters.add(ClusterRows.firstElement());
			
			ClusterColumns.remove(0);
			ClusterRows.remove(0);
			
			while (ClusterRows.size() != 0) {
				   Vector<Integer> clusterC = (Vector)colClusters.lastElement();
			        Vector<Integer> clusterR = (Vector)rowClusters.lastElement();
			 	
			        
			        double schnitt = -1;
				
			        int row  = 0;
			    for (int i = 0; i < ClusterRows.size(); i++) {
			        Vector<Integer> clusterR2 = ClusterRows.elementAt(i);
			        Vector<Integer> clusterC2 = ClusterColumns.elementAt(i);
			      
			        int a = 0;
			        double schnittT = 0; 
			        for (int r= 0; r < clusterR.size(); r++) {
			            for (int c = 0; c < clusterC2.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC2.elementAt(c)][clusterR.elementAt(r)];
				        }
			        }
			        
			        for (int r= 0; r < clusterR2.size(); r++) {
			            for (int c = 0; c < clusterC.size(); c++) {
				             a++;
				             schnittT+= matrix [clusterC.elementAt(c)][clusterR2.elementAt(r)];
				        }
			        }
			        
			        
			         
			        
			        if (schnittT > schnitt) {
			        	row = i;
			        	schnitt = schnittT;
			        } 
			    
		     	}
			    
			 
			    colClusters.add(ClusterColumns.elementAt(row));
			    rowClusters.add(ClusterRows.elementAt(row));
			    ClusterColumns.remove(row);
			    ClusterRows.remove(row);
				    
			    
			    
			
			}
			
			ClusterColumns = colClusters;
			ClusterRows = rowClusters;
			
			
		};
		
		
		
		
		
		

		
		/**
		 * 
		 */
		public void newCluster() {
			
			int maxC=-1;
			int maxR=-1;
			int max = -1;
			
			
		    ClusterRows.add(new Vector());
		    ClusterColumns.add(new Vector());
		    
		    
		    
			
			/**find max*/
			for (int i = 0; i < Columns.size(); i++) {
				for (int j = 0; j < Rows.size(); j++) {
					if (max < matrix [Columns.elementAt(i)][Rows.elementAt(j)]) {
					   max = matrix [Columns.elementAt(i)][Rows.elementAt(j)];
					   maxC = i;
					   maxR = j;
					}
					
				}
			}
			
			
		
		
			
			
			/*erster Cluster**/
			
			
			
			
			/*
			Integer maxCol = Columns.elementAt(maxC);
			Integer maxRow = Rows.elementAt(maxR);
			
			

			  
			ClusterColumns.lastElement().add(maxCol);
		    ClusterRows.lastElement().add(maxRow);
			  
			  
			Columns.remove(maxC);
			Rows.remove(maxR);
			
			/*
			for (int i = 0; i < Columns.size(); i++) {
			      if (max/gen < matrix [Columns.elementAt(i)][maxRow]) {
			
   	//  System.out.print(Columns.elementAt(i) + " ");
			    	  ClusterColumns.lastElement().add(Columns.elementAt(i));
			      }
			      else restColumns.add(Columns.elementAt(i));
			
			}
			
		//	System.out.println();
			
			
			for (int i = 0; i < Rows.size(); i++) {
			      if (max/gen < matrix [maxCol][Rows.elementAt(i)]) {
			
    //	  System.out.print(Rows.elementAt(i));
			    	  ClusterRows.lastElement().add(Rows.elementAt(i));
			      }
			      else restRows.add(Rows.elementAt(i));
			
			}
			*/
			

			TempColumns = new Vector();
			TempRows = new Vector();
			
			
			
			
			TempColumns.add(Columns.elementAt(maxC));
			Columns.remove(maxC);
			
			TempRows.add(Rows.elementAt(maxR));
		    Rows.remove(maxR);
			
			
			
			
			searchNew(ClusterColumns.lastElement(),ClusterRows.lastElement(),max);
			
			
			
			
			
			
			
			
		//	System.out.println();
		//	System.out.println();
			
			
			if (Columns.size() != 0 && Rows.size()!= 0) 
			
			newCluster();
			
			else {
				
				
				
				for (int i = 0; i < Columns.size(); i++) {
					 
					int col = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < ClusterColumns.size(); j++) {
						   Vector<Integer> clusterC = (Vector)ClusterColumns.elementAt(j);
						   Vector<Integer> clusterR = (Vector)ClusterRows.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterR.size();k++) {
							   schnittT+=matrix [Columns.elementAt(i)][clusterR.elementAt(k)];
						   }
						   schnittT/= clusterR.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   col = j;  
						   }
						   
					   }
		
					
		               ClusterColumns.elementAt(col).add(Columns.elementAt(i));
				}
				
				
                for (int i = 0; i < Rows.size(); i++) {
                	int row = -1;
					double schnitt = -1;
					
					   for (int j = 0; j < ClusterRows.size(); j++) {
						   Vector<Integer> clusterR = (Vector)ClusterRows.elementAt(j);
						   Vector<Integer> clusterC = (Vector)ClusterColumns.elementAt(j);
						   double schnittT = 0;
						   for (int k = 0; k < clusterC.size();k++) {
							   schnittT+=matrix [clusterC.elementAt(k)] [Rows.elementAt(i)];
						   }
						   schnittT/= clusterC.size();
						   if (schnitt<schnittT) {
							   schnitt = schnittT;
							   row = j;  
						   }
						   
					   }
		
					
		               ClusterRows.elementAt(row).add(Rows.elementAt(i));
				}



				
			}
			
			
			
			}
			
			
		
		
		
	
	
	
	
	
	
	
	
	
	
	public void searchNew(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows, int max) {
		
		
		
		if (TempColumns.size() == 0 && TempRows.size() == 0) return;
		
	
		if (TempColumns.size() != 0) {
			
			
			Integer col = TempColumns.elementAt(0);
			Vector newRestRows = new Vector();
			
			for (int i = Rows.size()-1; i>=0; i--) {
			      if (max/gen < matrix [col][Rows.elementAt(i)]) {
			
			    	  TempRows.add(Rows.elementAt(i));
			      }
			      else newRestRows.add(Rows.elementAt(i));
			
			}
			
			
			Rows = newRestRows;
			
			ClusterColumns.add(col);
			TempColumns.remove(0);
			
			searchNew(ClusterColumns,ClusterRows, max);
			return;
			
		}
		
	
		Integer row = TempRows.elementAt(0);
		
	
		Vector newRestColumns = new Vector();
	
		
		
		for (int i = Columns.size()-1; i >= 0; i--) {
		      if (max/gen < matrix [Columns.elementAt(i)][row]) {
		
		    	  TempColumns.add(Columns.elementAt(i));
		      }
		      else newRestColumns.add(Columns.elementAt(i));
		
		}
		
		Columns = newRestColumns;
		
		
		
		
		
		ClusterRows.add(row);
		
		
		TempRows.remove(0);
		
		
		
		
		searchNew(ClusterColumns,ClusterRows, max);
		
		
		
		
		
	};
	
		
		
		
		
		
		
	
		
		
		
		
		
		public double calculateCriterium(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
			double crit = 0;
			for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
				}
				
			}
          //  crit/=(ClusterColumns.size()*ClusterRows.size());
			
            
            double minus = 0;
			
        	for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < matrix [0].length; j++) {
					minus+= matrix [ClusterColumns.elementAt(i)][j];
				}
				
			}
        	for (int i =0; i < matrix.length; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					minus+= matrix [i][ClusterRows.elementAt(j)];
				}
				
			}
            
        	
        //	minus/=(matrix.length+matrix [0].length);
            
            
			return gen*crit-minus;
			
		}
		
		
		
		
		public double calculateCriterium(Vector<Integer>ClusterColumns ,Integer col,Vector<Integer> ClusterRows) {
			double crit = 0;
			for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
				}
				
				
			}
			
			for (int j = 0; j < ClusterRows.size(); j++) {
				crit+= matrix [col][ClusterRows.elementAt(j)];
			}
			
			
			
         //   crit/=((ClusterColumns.size()+1)*ClusterRows.size());
			
            
            double minus = 0;
			
        	for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < matrix [0].length; j++) {
					minus+= matrix [ClusterColumns.elementAt(i)][j];
				}
				
			}
        	
        	for (int j = 0; j < matrix [0].length; j++) {
				minus+= matrix [col][j];
			}
			
        	
        	
        	for (int i =0; i < matrix.length; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					minus+= matrix [i][ClusterRows.elementAt(j)];
				}
				
			}
            
        	
        //	minus/=(matrix.length+matrix [0].length+1);
            
        
			return gen*crit-minus;
			
		}
		
		
		
		
		public double calculateCriterium(Vector<Integer>ClusterColumns,Vector<Integer> ClusterRows, Integer row) {
			double crit = 0;
			for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
				}
				crit+= matrix [ClusterColumns.elementAt(i)][row];
				
			}
			
			
			
			
			
           // crit/=((ClusterColumns.size())*(ClusterRows.size()+1));
			
            
            double minus = 0;
			
        	for (int i =0; i < ClusterColumns.size() ; i++) {
				for (int j = 0; j < matrix [0].length; j++) {
					minus+= matrix [ClusterColumns.elementAt(i)][j];
				}
				
			}
        	
        	
			
        	
        	
        	for (int i =0; i < matrix.length; i++) {
				for (int j = 0; j < ClusterRows.size(); j++) {
					minus+= matrix [i][ClusterRows.elementAt(j)];
				}
				minus+= matrix [i][row];
			}
            
        	
        	//minus/=(matrix.length+matrix [0].length+1);
            
            
			return gen*crit-minus;
			
		}
		
		
		
		
		
		
		
		
	
	}
	
	
	
	















class Matrix2 {

	
	
	Vector<Integer> perColumns = new Vector();
	Vector <Integer> perRows = new Vector();

	
	Vector<Integer> Columns;
	Vector <Integer> Rows;
	
	
	Vector<Integer> TempColumns = new Vector();
	Vector<Integer> TempRows = new Vector();
	
	
	Vector sortInClusterColumns = new Vector();
	Vector sortInClusterRows = new Vector();
	
	
	public Matrix2(Vector<Integer> Columns, Vector <Integer> Rows) {
	    ClusterRows = new Vector();
	    ClusterColumns = new Vector();
		
	    
	    this.Columns = Columns;
	    this.Rows = Rows;
	    
		newCluster();
		
		if (sortMethod == 1) sortClusters1();
		if (sortMethod == 2) sortClusters2();
			
		
		/*
		if (sortMethod==1 || sortMethod == 2) {
			
			for (int i = 0; i < ClusterColumns.size(); i++) {
				
				Vector colCluster = new Vector();
				Vector rowCluster = new Vector();
				sortInClusters(ClusterColumns.elementAt(i),ClusterRows.elementAt(i), colCluster, rowCluster, gen/2);
			   
				Vector resColCluster = new Vector();
				Vector resRowCluster = new Vector();
				for (int ii = 0; ii< colCluster.size(); ii++) {
					for (int j =0; j < ((Vector)colCluster.elementAt(ii)).size(); j++) {
						resColCluster.add(((Vector)colCluster.elementAt(ii)).elementAt(j));
					}
					
					
				}
				
				
				for (int ii = 0; ii< rowCluster.size(); ii++) {
					for (int j =0; j < ((Vector)rowCluster.elementAt(ii)).size(); j++) {
						resRowCluster.add(((Vector)rowCluster.elementAt(ii)).elementAt(j));
					}
					
					
				}
				
				
				
				ClusterColumns.set(i,resColCluster);
			    ClusterRows.set(i,resRowCluster);
			
			}
			
			
			
			
			
		
		
		}	*/
		
		for (int i = 0; i < ClusterColumns.size(); i++) {
			for (int j = 0; j < ClusterColumns.elementAt(i).size(); j++) {
				perColumns.add(ClusterColumns.elementAt(i).elementAt(j));
			}
		}
		
		
		
		for (int i = 0; i < ClusterRows.size(); i++) {
			for (int j = 0; j < ClusterRows.elementAt(i).size(); j++) {
				perRows.add(ClusterRows.elementAt(i).elementAt(j));
			}
		}
		
		/*
		if (sortMethod==1 || sortMethod == 0) {
			
			
		perColumns = new Vector();
		perRows = new Vector();
		for (int i = 0; i < sortInClusterColumns.size(); i++) {
			perColumns.add((Integer)sortInClusterColumns.elementAt(i));
		}
		
		
		
		for (int i = 0; i < sortInClusterRows.size(); i++) {
			perRows.add((Integer)sortInClusterRows.elementAt(i));
		}
	}*/
		
		
		
	}
	
	
	
	
	
	public void sortInClusters(Vector<Integer> columnsToSort, Vector<Integer> rowsToSort,Vector resColumns,Vector resRows, double param) {
		
	    Columns = columnsToSort;
	    Rows = rowsToSort;
		
	    
	    System.out.println(Columns.size() + " " + Rows.size() +  " " + param);
		
	    
	    while (columnsToSort.size() != 0 && rowsToSort.size() != 0) {
	    	
	    	
	    	int maxC=-1;
			int maxR=-1;
			int max = -1;
			
		
		/**find max*/
		for (int i = 0; i < columnsToSort.size(); i++) {
			for (int j = 0; j < rowsToSort.size(); j++) {
				if (max < matrix [columnsToSort.elementAt(i)][rowsToSort.elementAt(j)]) {
				   max = matrix [columnsToSort.elementAt(i)][rowsToSort.elementAt(j)];
				   maxC = i;
				   maxR = j;
				}
				
			}
		}
		
		
		

		TempColumns = new Vector();
		TempRows = new Vector();
		
		
		
		
		TempColumns.add(columnsToSort.elementAt(maxC));
		columnsToSort.remove(maxC);
		
		
		
		
		TempRows.add(rowsToSort.elementAt(maxR));
	    rowsToSort.remove(maxR);
		
		
	    Rows = rowsToSort;
	    Columns = columnsToSort;
		
		
	    
	    Vector<Integer> newColCluster = new Vector();
	    Vector<Integer> newRowCluster = new Vector();
	    
		searchNew(newColCluster,newRowCluster);
		
		
		
		 rowsToSort = Rows;
		 columnsToSort = Columns;
			
			
		
		
		resColumns.add(newColCluster);
		resRows.add(newRowCluster);
		
		
		
	//	System.out.println();
	//	System.out.println();
		
	    }
		
			
			
			
			for (int i = 0; i < columnsToSort.size(); i++) {
				 
				int col = -1;
				double schnitt = -1;
				
				   for (int j = 0; j < resColumns.size(); j++) {
					   Vector<Integer> clusterC = (Vector)resColumns.elementAt(j);
					   Vector<Integer> clusterR = (Vector)resRows.elementAt(j);
					   double schnittT = 0;
					   for (int k = 0; k < clusterR.size();k++) {
						   schnittT+=matrix [columnsToSort.elementAt(i)][clusterR.elementAt(k)];
					   }
					   schnittT/= clusterR.size();
					   if (schnitt<schnittT) {
						   schnitt = schnittT;
						   col = j;  
					   }
					   
				   }
	
				
	               ((Vector)resColumns.elementAt(col)).add(columnsToSort.elementAt(i));
			}
			
			
            for (int i = 0; i < rowsToSort.size(); i++) {
            	int row = -1;
				double schnitt = -1;
				
				   for (int j = 0; j < resRows.size(); j++) {
					 
					   Vector<Integer> clusterR = (Vector)resRows.elementAt(j);
					   Vector<Integer> clusterC = (Vector)resColumns.elementAt(j);
					   double schnittT = 0;
					   for (int k = 0; k < clusterC.size();k++) {
						   schnittT+=matrix [clusterC.elementAt(k)] [rowsToSort.elementAt(i)];
					   }
					   schnittT/= clusterC.size();
					   if (schnitt<schnittT) {
						   schnitt = schnittT;
						   row = j;  
					   }
					   
				   }
	
				
	               ((Vector)resRows.elementAt(row)).add(rowsToSort.elementAt(i));
			}


/*
			
		    for (int i = 0; i < resColumns.size(); i++) {
		    	Vector cols = ((Vector)resColumns.elementAt(i));
		    	Vector rows = ((Vector)resRows.elementAt(i));
		    	if (cols.size()<=2 || rows.size() <= 2 || param <= 1) {
		    		for (int j = 0; j < cols.size(); j++) {
		    			sortInClusterColumns.add(cols.elementAt(j));
		    		}
		    		
		    		for (int j = 0; j < rows.size(); j++) {
		    			sortInClusterRows.add(rows.elementAt(j));
		    		}
		    		
		    		
		    	}
		    	else sortInClusters(cols,rows,new Vector(), new Vector(), param/2);
		    	
		    	
		    	
		    	
		    }
		*/
		
		
	}

	
	
	
	public void sortClusters1() {
		Vector colClusters = new Vector();
		Vector rowClusters = new Vector();
		
		colClusters.add(ClusterColumns.firstElement());
		rowClusters.add(ClusterRows.firstElement());
		
		ClusterColumns.remove(0);
		ClusterRows.remove(0);
		
		while (ClusterRows.size() != 0) {
			   Vector<Integer> clusterC = (Vector)colClusters.lastElement();
		        Vector<Integer> clusterR = (Vector)rowClusters.lastElement();
		 	
		        
		        double schnitt = -1;
			
		        int row  = 0;
		    for (int i = 0; i < ClusterRows.size(); i++) {
		        Vector<Integer> clusterR2 = ClusterRows.elementAt(i);
		        Vector<Integer> clusterC2 = ClusterColumns.elementAt(i);
		      
		        int a = 0;
		        double schnittT = 0; 
		        for (int r= 0; r < clusterR.size(); r++) {
		            for (int c = 0; c < clusterC2.size(); c++) {
			             a++;
			             schnittT+= matrix [clusterC2.elementAt(c)][clusterR.elementAt(r)];
			        }
		        }
		        
		        for (int r= 0; r < clusterR2.size(); r++) {
		            for (int c = 0; c < clusterC.size(); c++) {
			             a++;
			             schnittT+= matrix [clusterC.elementAt(c)][clusterR2.elementAt(r)];
			        }
		        }
		        
		        
		        schnittT/=a;
		        
		        
		        if (schnittT > schnitt) {
		        	row = i;
		        	schnitt = schnittT;
		        } 
		    
	     	}
		    
		    
		    colClusters.add(ClusterColumns.elementAt(row));
		    rowClusters.add(ClusterRows.elementAt(row));
		    ClusterColumns.remove(row);
		    ClusterRows.remove(row);
			    
		    
		    
		
		}
		
		ClusterColumns = colClusters;
		ClusterRows = rowClusters;
		
		
	};
	
	
	
	

	public void sortClusters2() {
		Vector colClusters = new Vector();
		Vector rowClusters = new Vector();
		
		colClusters.add(ClusterColumns.firstElement());
		rowClusters.add(ClusterRows.firstElement());
		
		ClusterColumns.remove(0);
		ClusterRows.remove(0);
		
		while (ClusterRows.size() != 0) {
			   Vector<Integer> clusterC = (Vector)colClusters.lastElement();
		        Vector<Integer> clusterR = (Vector)rowClusters.lastElement();
		 	
		        
		        double schnitt = -1;
			
		        int row  = 0;
		    for (int i = 0; i < ClusterRows.size(); i++) {
		        Vector<Integer> clusterR2 = ClusterRows.elementAt(i);
		        Vector<Integer> clusterC2 = ClusterColumns.elementAt(i);
		      
		        int a = 0;
		        double schnittT = 0; 
		        for (int r= 0; r < clusterR.size(); r++) {
		            for (int c = 0; c < clusterC2.size(); c++) {
			             a++;
			             schnittT+= matrix [clusterC2.elementAt(c)][clusterR.elementAt(r)];
			        }
		        }
		        
		        for (int r= 0; r < clusterR2.size(); r++) {
		            for (int c = 0; c < clusterC.size(); c++) {
			             a++;
			             schnittT+= matrix [clusterC.elementAt(c)][clusterR2.elementAt(r)];
			        }
		        }
		        
		        
		         
		        
		        if (schnittT > schnitt) {
		        	row = i;
		        	schnitt = schnittT;
		        } 
		    
	     	}
		    
		 
		    colClusters.add(ClusterColumns.elementAt(row));
		    rowClusters.add(ClusterRows.elementAt(row));
		    ClusterColumns.remove(row);
		    ClusterRows.remove(row);
			    
		    
		    
		
		}
		
		ClusterColumns = colClusters;
		ClusterRows = rowClusters;
		
		
	};
	
	
	
	
	
	

	
	/**
	 * 
	 */
	public void newCluster() {
		
		int maxC=-1;
		int maxR=-1;
		int max = -1;
		
		
	    ClusterRows.add(new Vector());
	    ClusterColumns.add(new Vector());
	    
	    
	    
		
		/**find max*/
		for (int i = 0; i < Columns.size(); i++) {
			for (int j = 0; j < Rows.size(); j++) {
				if (max < matrix [Columns.elementAt(i)][Rows.elementAt(j)]) {
				   max = matrix [Columns.elementAt(i)][Rows.elementAt(j)];
				   maxC = i;
				   maxR = j;
				}
				
			}
		}
		
		
	
	
		
		
		/*erster Cluster**/
		
		
		
		
		/*
		Integer maxCol = Columns.elementAt(maxC);
		Integer maxRow = Rows.elementAt(maxR);
		
		

		  
		ClusterColumns.lastElement().add(maxCol);
	    ClusterRows.lastElement().add(maxRow);
		  
		  
		Columns.remove(maxC);
		Rows.remove(maxR);
		
		/*
		for (int i = 0; i < Columns.size(); i++) {
		      if (max/gen < matrix [Columns.elementAt(i)][maxRow]) {
		
	//  System.out.print(Columns.elementAt(i) + " ");
		    	  ClusterColumns.lastElement().add(Columns.elementAt(i));
		      }
		      else restColumns.add(Columns.elementAt(i));
		
		}
		
	//	System.out.println();
		
		
		for (int i = 0; i < Rows.size(); i++) {
		      if (max/gen < matrix [maxCol][Rows.elementAt(i)]) {
		
//	  System.out.print(Rows.elementAt(i));
		    	  ClusterRows.lastElement().add(Rows.elementAt(i));
		      }
		      else restRows.add(Rows.elementAt(i));
		
		}
		*/
		

	//	TempColumns = new Vector();
	//	TempRows = new Vector();
		
		
		
		
		ClusterColumns.lastElement().add(Columns.elementAt(maxC));
		Columns.remove(maxC);
		
		ClusterRows.lastElement().add(Rows.elementAt(maxR));
	    Rows.remove(maxR);
		
		
		
		
		searchNew(ClusterColumns.lastElement(),ClusterRows.lastElement());
		
		
		
		
		
		
		
		
	//	System.out.println();
	//	System.out.println();
		
		
		if (Columns.size() != 0 && Rows.size()!= 0) 
		
		newCluster();
		
		else {
			
			
			
			for (int i = 0; i < Columns.size(); i++) {
				 
				int col = -1;
				double schnitt = -1;
				
				   for (int j = 0; j < ClusterColumns.size(); j++) {
					   Vector<Integer> clusterC = (Vector)ClusterColumns.elementAt(j);
					   Vector<Integer> clusterR = (Vector)ClusterRows.elementAt(j);
					   double schnittT = 0;
					   for (int k = 0; k < clusterR.size();k++) {
						   schnittT+=matrix [Columns.elementAt(i)][clusterR.elementAt(k)];
					   }
					   schnittT/= clusterR.size();
					   if (schnitt<schnittT) {
						   schnitt = schnittT;
						   col = j;  
					   }
					   
				   }
	
				
	               ClusterColumns.elementAt(col).add(Columns.elementAt(i));
			}
			
			
            for (int i = 0; i < Rows.size(); i++) {
            	int row = -1;
				double schnitt = -1;
				
				   for (int j = 0; j < ClusterRows.size(); j++) {
					   Vector<Integer> clusterR = (Vector)ClusterRows.elementAt(j);
					   Vector<Integer> clusterC = (Vector)ClusterColumns.elementAt(j);
					   double schnittT = 0;
					   for (int k = 0; k < clusterC.size();k++) {
						   schnittT+=matrix [clusterC.elementAt(k)] [Rows.elementAt(i)];
					   }
					   schnittT/= clusterC.size();
					   if (schnitt<schnittT) {
						   schnitt = schnittT;
						   row = j;  
					   }
					   
				   }
	
				
	               ClusterRows.elementAt(row).add(Rows.elementAt(i));
			}



			
		}
		
		
		
		}
		
		
	
	
	





/*




public void searchNew(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows, int max) {
	
	
	
	if (TempColumns.size() == 0 && TempRows.size() == 0) return;
	

	if (TempColumns.size() != 0) {
		
		
		Integer col = TempColumns.elementAt(0);
		Vector newRestRows = new Vector();
		
		for (int i = Rows.size()-1; i>=0; i--) {
		      if (max/gen < matrix [col][Rows.elementAt(i)]) {
		
		    	  TempRows.add(Rows.elementAt(i));
		      }
		      else newRestRows.add(Rows.elementAt(i));
		
		}
		
		
		Rows = newRestRows;
		
		ClusterColumns.add(col);
		TempColumns.remove(0);
		
		searchNew(ClusterColumns,ClusterRows, max);
		return;
		
	}
	

	Integer row = TempRows.elementAt(0);
	

	Vector newRestColumns = new Vector();

	
	
	for (int i = Columns.size()-1; i >= 0; i--) {
	      if (max/gen < matrix [Columns.elementAt(i)][row]) {
	
	    	  TempColumns.add(Columns.elementAt(i));
	      }
	      else newRestColumns.add(Columns.elementAt(i));
	
	}
	
	Columns = newRestColumns;
	
	
	
	
	
	ClusterRows.add(row);
	
	
	TempRows.remove(0);
	
	
	
	
	searchNew(ClusterColumns,ClusterRows, max);
	
	
	
	
	
};

	*/
	
	
	
	
	
	public void searchNew(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
		
		
		    if (Columns.size() == 0 || Rows.size() == 0) return;
			
			
			double criterium = calculateCriterium(ClusterColumns,ClusterRows);
		
			double maxColCrit = -1000000000;
			int maxCol = -1;
			
			for (int i = 0; i < Columns.size() ; i++) {
				double crit = calculateCriterium(ClusterColumns,Columns.elementAt(i),ClusterRows);
				if (maxColCrit < crit) {
					maxColCrit = crit;
					maxCol = i;
				}
			}
			
			
			
			
			double maxRowCrit = -1000000000;
			int maxRow = -1;
			
			
			for (int i = 0; i < Rows.size() ; i++) {
				double crit = calculateCriterium(ClusterColumns,ClusterRows,Rows.elementAt(i));
				if (maxRowCrit < crit) {
					maxRowCrit = crit;
					maxRow = i;
				}
			}
			
			
			
		   if (maxRowCrit <= maxColCrit) {
			   if (criterium < maxColCrit) {
				   ClusterColumns.add(Columns.elementAt(maxCol));
				   Columns.remove(maxCol);
			   }
		   }	
		   
		   if (maxRowCrit > maxColCrit) {
			   if (criterium < maxRowCrit) {
				   ClusterRows.add(Rows.elementAt(maxRow));
				   Rows.remove(maxRow);
			   }
		   }	
		   
		   if (criterium >= maxColCrit  &&  criterium >= maxRowCrit) return;
		
		   
		   
		   
		
		searchNew(ClusterColumns,ClusterRows);
		
		
		
		
		
	};
	
	
	
	
	
	public double calculateCriterium(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
		double crit = 0;
		for (int i =0; i < ClusterColumns.size() ; i++) {
			for (int j = 0; j < ClusterRows.size(); j++) {
				crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
			}
			
		}
      //  crit/=(ClusterColumns.size()*ClusterRows.size());
		
        
        double minus = 0;
		
    	for (int i =0; i < ClusterColumns.size() ; i++) {
			for (int j = 0; j < matrix [0].length; j++) {
				minus+= matrix [ClusterColumns.elementAt(i)][j];
			}
			
		}
    	for (int i =0; i < matrix.length; i++) {
			for (int j = 0; j < ClusterRows.size(); j++) {
				minus+= matrix [i][ClusterRows.elementAt(j)];
			}
			
		}
        
    	
    //	minus/=(matrix.length+matrix [0].length);
        
        
		return gen*crit-minus;
		
	}
	
	
	
	
	public double calculateCriterium(Vector<Integer>ClusterColumns ,Integer col,Vector<Integer> ClusterRows) {
		double crit = 0;
		for (int i =0; i < ClusterColumns.size() ; i++) {
			for (int j = 0; j < ClusterRows.size(); j++) {
				crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
			}
			
			
		}
		
		for (int j = 0; j < ClusterRows.size(); j++) {
			crit+= matrix [col][ClusterRows.elementAt(j)];
		}
		
		
		
     //   crit/=((ClusterColumns.size()+1)*ClusterRows.size());
		
        
        double minus = 0;
		
    	for (int i =0; i < ClusterColumns.size() ; i++) {
			for (int j = 0; j < matrix [0].length; j++) {
				minus+= matrix [ClusterColumns.elementAt(i)][j];
			}
			
		}
    	
    	for (int j = 0; j < matrix [0].length; j++) {
			minus+= matrix [col][j];
		}
		
    	
    	
    	for (int i =0; i < matrix.length; i++) {
			for (int j = 0; j < ClusterRows.size(); j++) {
				minus+= matrix [i][ClusterRows.elementAt(j)];
			}
			
		}
        
    	
    //	minus/=(matrix.length+matrix [0].length+1);
        
    
		return gen*crit-minus;
		
	}
	
	
	
	
	public double calculateCriterium(Vector<Integer>ClusterColumns,Vector<Integer> ClusterRows, Integer row) {
		double crit = 0;
		for (int i =0; i < ClusterColumns.size() ; i++) {
			for (int j = 0; j < ClusterRows.size(); j++) {
				crit+= matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
			}
			crit+= matrix [ClusterColumns.elementAt(i)][row];
			
		}
		
		
		
		
		
       // crit/=((ClusterColumns.size())*(ClusterRows.size()+1));
		
        
        double minus = 0;
		
    	for (int i =0; i < ClusterColumns.size() ; i++) {
			for (int j = 0; j < matrix [0].length; j++) {
				minus+= matrix [ClusterColumns.elementAt(i)][j];
			}
			
		}
    	
    	
		
    	
    	
    	for (int i =0; i < matrix.length; i++) {
			for (int j = 0; j < ClusterRows.size(); j++) {
				minus+= matrix [i][ClusterRows.elementAt(j)];
			}
			minus+= matrix [i][row];
		}
        
    	
    	//minus/=(matrix.length+matrix [0].length+1);
        
        
		return gen*crit-minus;
		
	}
	
	
	
	
	
	
	
	

}
















class Matrix3 {

	
	

	
	Vector<Integer> Columns;
	Vector <Integer> Rows;
	
	Vector<Vector<Integer>> perCols;
	Vector<Vector<Integer>> perRows;
	
	
	public Matrix3(Vector<Integer> Columns, Vector <Integer> Rows, boolean optimum) {
	    
		
	    
	    this.Columns = Columns;
	    this.Rows = Rows;
	    
		//newCluster();
	if (optimum) {
		optimum();
		System.out.println("Optimum  " + calculateCriterium(this.Columns,this.Rows));
	}
	else {
		
		newCluster();
		System.out.println("Heuristik  " + calculateCriterium(this.Columns,this.Rows));
	}
	
	
		
	}
	
	
	
	public Vector<Vector<Integer>> CalcAllPemutations(Vector<Integer> Columns) {
		Vector<Vector<Integer>> permutations = new Vector();
		if (Columns.size() == 0) {
			permutations.add(new Vector());
			return permutations;
		}
		
		
		
		for (int i = 0; i < Columns.size(); i++) {
			Integer col = Columns.elementAt(i);
			Vector<Integer> rest = remove(Columns,i);
			Vector<Vector<Integer>> temp = CalcAllPemutations(rest);
			for (int j = 0; j < temp.size(); j++) {
				Vector<Integer> per = temp.elementAt(j);
				per.add(col);
				permutations.add(per);
			}
		}
		return permutations;
	}
	
	
	
	
	
	public void optimum() {
		Vector<Vector<Integer>> perC = CalcAllPemutations(Columns);
		Vector<Vector<Integer>> perR = CalcAllPemutations(Rows);
		
		
		Vector<Integer> bestPerC = null;
		Vector<Integer> bestPerR = null;
		double crit = calculateCriterium(Columns,Rows);
		
		System.out.println("Anzahl der Permutationen  :  " + perC.size() + "   " +  perR.size());
		
		
		
		for (int i = 0; i < perC.size(); i++) {
			System.out.println(i);
			
			for (int j = 0; j < perR.size(); j++) {
				Vector<Integer> C = perC.elementAt(i);
				Vector<Integer> R = perR.elementAt(j);
				
				double value = calculateCriterium(C,R);
				if (value < crit) {
					crit = value;
					bestPerC = C;
					bestPerR = R;
				}
			}
		}
		
		
		if (bestPerC != null) {
			Columns = bestPerC;
			Rows = bestPerR;
		}
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	public Vector<Integer> remove(Vector<Integer> Columns,int index) {
		Vector v  = new Vector();
		for (int i = 0; i < Columns.size(); i++) {
			if (i!=index) v.add(Columns.elementAt(i));
		}
		return v;
	}
	
	

	
	/**
	 * 
	 */
	public void newCluster() {
		
		 
	    
	    
	    
	    
	    int ColI = -2;
	    int ColJ = -2;
	    int RowI = -2;
	    int RowJ = -2;
	    
	    double colValue = calculateCriterium(Columns,Rows);;
	    double rowValue = colValue;
	    
	     
		
		//find max
		for (int i = -1; i < Columns.size(); i++) {
			for (int j = 0; j <= Columns.size(); j++) {
		              Vector<Integer> perColumns = vertausche(Columns, i , j);	
			          double crit = calculateCriterium(perColumns, Rows);
			          if (crit < colValue) {
			        	  colValue = crit;
			        	  ColI = i;
			        	  ColJ = j;
			          }
			
		}
		}
		
	
		for (int i = -1; i < Rows.size(); i++) {
			for (int j = 0; j <= Rows.size(); j++) {
		              Vector<Integer> perRows = vertausche(Rows, i , j);	
			          double crit = calculateCriterium(Columns, perRows);
			          if (crit < rowValue) {
			        	  rowValue = crit;
			        	  RowI = i;
			        	  RowJ = j;
			          }
			
		}
		}
	
		
		
		
		
		
		if (colValue <= rowValue && ColI != -2) {
			  Vector<Integer> perColumns = vertausche(Columns, ColI , ColJ);
			  Columns = perColumns;
			  System.out.println("Columns  " + ColI + "  "  + ColJ +  "   " + calculateCriterium(Columns,Rows));
		}
		
		
		
		if (colValue > rowValue) {
			  Vector<Integer> perRows = vertausche(Rows, RowI , RowJ);
			  Rows = perRows;
			  System.out.println("Rows  " + RowI + "  "  + RowJ +  "   " + calculateCriterium(Columns,Rows));
		}
	
	
		
		
		
		
		int I = -1;

	    int J = -1;
	    
	    double CValue = calculateCriterium(Columns,Rows);;
	    double RValue = colValue;
		
		
		
	
		for (int i = 0; i < Columns.size(); i++) {
			
		              Vector<Integer> perColumns = drehe(Columns, i);	
			          double crit = calculateCriterium(perColumns, Rows);
			          if (crit < CValue) {
			        	  CValue = crit;
			        	  I = i;
			        
			          }
			
		
		}
		
	
		for (int i = 0; i < Rows.size(); i++) {
			           Vector<Integer> perRows = drehe(Rows, i);	
			          double crit = calculateCriterium(Columns, perRows);
			          if (crit < RValue) {
			        	  RValue = crit;
			        	  J = i;
			          }
			
		}
		
	
		
		
		
		
	if (CValue <= RValue && I != -1) {
		  Vector<Integer> perColumns = drehe(Columns, I);
		  Columns = perColumns;
		  System.out.println("Columns Rotation  " + I + "  " + calculateCriterium(Columns,Rows));
	}
	
	
	
	if (CValue > RValue) {
		  Vector<Integer> perRows = drehe(Rows, J);
		  Rows = perRows;
		  System.out.println("Rows Rotation " + RowJ +  "   " + calculateCriterium(Columns,Rows));
	}
		
		
		
	
	
	
	/*Zeile oder Spalten von einer in eine andere Stelle infügen**/
	
	
	
	
	
	
	
	
	        ColI = -2;
	        ColJ = -2;
	        RowI = -2;
	        RowJ = -2;
	    
	        colValue = calculateCriterium(Columns,Rows);;
	        rowValue = colValue;
	    
	     
		
		/**find max*/
		for (int i = 0; i < Columns.size(); i++) {
			for (int j = 0; j < Columns.size(); j++) {
		              Vector<Integer> perColumns = insert(Columns, i , j);	
			          double crit = calculateCriterium(perColumns, Rows);
			          if (crit < colValue) {
			        	  colValue = crit;
			        	  ColI = i;
			        	  ColJ = j;
			          }
			
		}
		}
		
	
		for (int i = 0; i < Rows.size(); i++) {
			for (int j = 0; j < Rows.size(); j++) {
		              Vector<Integer> perRows = insert(Rows, i , j);	
			          double crit = calculateCriterium(Columns, perRows);
			          if (crit < rowValue) {
			        	  rowValue = crit;
			        	  RowI = i;
			        	  RowJ = j;
			          }
			
		}
		}
	
		
		
		
		
		
		if (colValue <= rowValue && ColI != -2) {
			  Vector<Integer> perColumns = insert(Columns, ColI , ColJ);
			  Columns = perColumns;
			  System.out.println("Insert Columns  " + ColI + "  "  + ColJ +  "   " + calculateCriterium(Columns,Rows));
		}
		
		
		
		if (colValue > rowValue) {
			  Vector<Integer> perRows = insert(Rows, RowI , RowJ);
			  Rows = perRows;
			  System.out.println("Insert Rows  " + RowI + "  "  + RowJ +  "   " + calculateCriterium(Columns,Rows));
		}
	
	
		
		
		
		
		
		

}


	
	
	public Vector<Integer> insert(Vector<Integer> Columns, int col , int pos) {
		   Vector<Integer> res = new Vector();
		   for (int i = 0; i < Columns.size(); i++) {
			   if (i != col) res.add(Columns.elementAt(i));
			   
		   }
		   int newpos = 0;
		   if (col >= pos) newpos = pos;
		   else newpos = pos-1;
		   res.add(newpos,Columns.elementAt(col));
		   return res;
		   
		   
	}
	
	
	public Vector<Integer> vertausche(Vector<Integer> Columns, int i , int j) {
		Vector<Integer> v = new Vector();
		
		if (i == j) return Columns;
	
		if (i == -1 && j != Columns.size()) {
			v.add(Columns.elementAt(j));
			for (int a = 0; a < Columns.size(); a++) {
				if (a != j) v.add(Columns.elementAt(a));
			}
			return v;
		}
		
		
		if (j == Columns.size() && i !=-1) {
			
			for (int a = 0; a < Columns.size(); a++) {
				if (a != i) v.add(Columns.elementAt(a));
			}
			v.add(Columns.elementAt(i));
			return v;
		}
		
		
		
		
		
		for (int a = 0; a < Columns.size(); a++) {
			if (a == i) v.add(Columns.elementAt(j));
			if (a == j) v.add(Columns.elementAt(i));
			if ((a != i) && (a != j)) v.add(Columns.elementAt(a));
			
		}
		
		return v;
	}
	
	
	
	public Vector<Integer> drehe(Vector<Integer> Columns, int j) {
		Vector<Integer> v = new Vector();	
		
		for (int a = j; a < Columns.size()+j; a++) {
			v.add(Columns.elementAt(a%Columns.size()));
			
		}
		
		return v;
	}
	
	
	
	
	
	/**
	 * 
	 *   Abstände in den Zeilen und Spalten
	 * 
	 * 
	 * */
	
	public double calculateCriterium2(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
		double crit = 0;
		
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
			for (int j = i+1; j < ClusterColumns.size(); j++) {
				for (int row = 0; row < ClusterRows.size(); row++) {
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(row)];
					int b = matrix [ClusterColumns.elementAt(j)][ClusterRows.elementAt(row)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
   
		
		
		for (int i =0; i < ClusterRows.size()-1; i++) {
			for (int j = i+1; j < ClusterRows.size(); j++) {
				for (int col = 0; col < ClusterColumns.size(); col++) {
					int a = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(i)];
					int b = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(j)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
		
		
        
        
		return crit;
		
	}
	
	

	/**
	 * 
	 *   Alle Abstände
	 * 
	 * 
	 * */
	
	public double calculateCriterium3(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
double crit = 0;
		
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
			for (int j = i+1; j < ClusterColumns.size(); j++) {
				for (int row = 0; row < ClusterRows.size(); row++) {
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(row)];
					int b = matrix [ClusterColumns.elementAt(j)][ClusterRows.elementAt(row)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
   
		
		
		for (int i =0; i < ClusterRows.size()-1; i++) {
			for (int j = i+1; j < ClusterRows.size(); j++) {
				for (int col = 0; col < ClusterColumns.size(); col++) {
					int a = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(i)];
					int b = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(j)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
	    for (int j =0; j < ClusterRows.size()-1; j++) {
			
			
			
	    	for (int ii =i+1; ii < ClusterColumns.size(); ii++) {
	    	for (int jj =j+1; jj < ClusterRows.size(); jj++) {
	    	    	
	    	    	
	    	    
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
					int b = matrix [ClusterColumns.elementAt(ii)][ClusterRows.elementAt(jj)];
					
					crit+= Math.min(a,b)*(ii+jj-i-j-1);
					
				}
				
				
				
			
			}}
			
		}
   
		
		
		
		
		
        
        
		return crit;
		
	}
	
	

	
	
	
	
	
	/**
	 * 
	 *    Bestrafung falscher Ausrichtung
	 * 
	 * 
	 * */
	
	public double calculateCriterium(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
		double crit = 0;
		
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
			for (int j = i+1; j < ClusterColumns.size(); j++) {
				for (int row = 0; row < ClusterRows.size(); row++) {
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(row)];
					int b = matrix [ClusterColumns.elementAt(j)][ClusterRows.elementAt(row)];
					
					crit+= a*b*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
   
		
		
		for (int i =0; i < ClusterRows.size()-1; i++) {
			for (int j = i+1; j < ClusterRows.size(); j++) {
				for (int col = 0; col < ClusterColumns.size(); col++) {
					int a = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(i)];
					int b = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(j)];
					
					crit+= a*b*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
		
		
		
		for (int i =1; i < ClusterColumns.size(); i++) {
	    for (int j =0; j < ClusterRows.size()-1; j++) {
			
			
			
	    	for (int ii =0; ii < i; ii++) {
	    	for (int jj =j+1; jj < ClusterRows.size(); jj++) {
	    	    	
	    	    	
	    	    
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
					int b = matrix [ClusterColumns.elementAt(ii)][ClusterRows.elementAt(jj)];
					
					crit+= a*b*(-ii+jj+i-j-1);
					
				}
				
				
				
			
			}}
			
		}
   
		
		
		
		
		
        
        
		return crit;
		
	}
	
	
	
	
	
	
	






}


	
	
	




















class Matrix4 {

	
	

	
	Vector<Integer> Columns;
	Vector <Integer> Rows;
	
	Vector<Vector<Integer>> perCols;
	Vector<Vector<Integer>> perRows;
	
	
	public Matrix4(Vector<Integer> Columns, Vector <Integer> Rows, boolean optimum) {
	    
		
	    
	    this.Columns = Columns;
	    this.Rows = Rows;
	    
		//newCluster();
	if (optimum) {
		optimum();
		System.out.println("Optimum  " + calculateCriterium(this.Columns,this.Rows));
	}
	else {
		
		newCluster();
		System.out.println("Heuristik  " + calculateCriterium(this.Columns,this.Rows));
	}
	
	
		
	}
	
	
	
	public Vector<Vector<Integer>> CalcAllPemutations(Vector<Integer> Columns) {
		Vector<Vector<Integer>> permutations = new Vector();
		if (Columns.size() == 0) {
			permutations.add(new Vector());
			return permutations;
		}
		
		
		
		for (int i = 0; i < Columns.size(); i++) {
			Integer col = Columns.elementAt(i);
			Vector<Integer> rest = remove(Columns,i);
			Vector<Vector<Integer>> temp = CalcAllPemutations(rest);
			for (int j = 0; j < temp.size(); j++) {
				Vector<Integer> per = temp.elementAt(j);
				per.add(col);
				permutations.add(per);
			}
		}
		return permutations;
	}
	
	
	
	
	
	public void optimum() {
		Vector<Vector<Integer>> perC = CalcAllPemutations(Columns);
		Vector<Vector<Integer>> perR = CalcAllPemutations(Rows);
		
		
		Vector<Integer> bestPerC = null;
		Vector<Integer> bestPerR = null;
		double crit = calculateCriterium(Columns,Rows);
		
		System.out.println("Anzahl der Permutationen  :  " + perC.size() + "   " +  perR.size());
		
		Vector<Double> values = new Vector(); 
		
		for (int i = 0; i < perC.size(); i++) {
			System.out.println(i);
			
			for (int j = 0; j < perR.size(); j++) {
				Vector<Integer> C = perC.elementAt(i);
				Vector<Integer> R = perR.elementAt(j);
				
				double value = calculateCriterium(C,R);
				values.add(value);
				
				if (value < crit) {
					crit = value;
					bestPerC = C;
					bestPerR = R;
				}
			}
		}
		
		
		if (bestPerC != null) {
			Columns = bestPerC;
			Rows = bestPerR;
		}
		
		
		double [] valuesD = new  double [values.size()];
		for (int i = 0; i < values.size(); i++){
			valuesD [i] = values.elementAt(i);
		}
		
	//	new Histogram(seurat,null,"Werteverteilung",valuesD);
		
		
		
	}
	
	
	
	
	
	
	
	public Vector<Integer> remove(Vector<Integer> Columns,int index) {
		Vector v  = new Vector();
		for (int i = 0; i < Columns.size(); i++) {
			if (i!=index) v.add(Columns.elementAt(i));
		}
		return v;
	}
	
	

	
	/**
	 * 
	 */
	public void newCluster() {
		
		 
	    
	    
	    
	    
	    int ColI = -2;
	    int ColJ = -2;
	    int RowI = -2;
	    int RowJ = -2;
	    
	    double colValue = calculateCriterium(Columns,Rows);;
	    double rowValue = colValue;
	    
	     
		
		//find max
		for (int i = -1; i < Columns.size(); i++) {
			for (int j = 0; j <= Columns.size(); j++) {
		              Vector<Integer> perColumns = vertausche(Columns, i , j);	
			          double crit = calculateCriterium(perColumns, Rows);
			          if (crit < colValue) {
			        	  colValue = crit;
			        	  ColI = i;
			        	  ColJ = j;
			          }
			
		}
		}
		
	
		for (int i = -1; i < Rows.size(); i++) {
			for (int j = 0; j <= Rows.size(); j++) {
		              Vector<Integer> perRows = vertausche(Rows, i , j);	
			          double crit = calculateCriterium(Columns, perRows);
			          if (crit < rowValue) {
			        	  rowValue = crit;
			        	  RowI = i;
			        	  RowJ = j;
			          }
			
		}
		}
	
		
		
		
		
		
		if (colValue <= rowValue && ColI != -2) {
			  Vector<Integer> perColumns = vertausche(Columns, ColI , ColJ);
			  Columns = perColumns;
			  System.out.println("Columns  " + ColI + "  "  + ColJ +  "   " + calculateCriterium(Columns,Rows));
		}
		
		
		
		if (colValue > rowValue) {
			  Vector<Integer> perRows = vertausche(Rows, RowI , RowJ);
			  Rows = perRows;
			  System.out.println("Rows  " + RowI + "  "  + RowJ +  "   " + calculateCriterium(Columns,Rows));
		}
	
	
		
		
		
		
		int I = -1;

	    int J = -1;
	    
	    double CValue = calculateCriterium(Columns,Rows);;
	    double RValue = colValue;
		
		
		
	
		for (int i = 0; i < Columns.size(); i++) {
			
		              Vector<Integer> perColumns = drehe(Columns, i);	
			          double crit = calculateCriterium(perColumns, Rows);
			          if (crit < CValue) {
			        	  CValue = crit;
			        	  I = i;
			        
			          }
			
		
		}
		
	
		for (int i = 0; i < Rows.size(); i++) {
			           Vector<Integer> perRows = drehe(Rows, i);	
			          double crit = calculateCriterium(Columns, perRows);
			          if (crit < RValue) {
			        	  RValue = crit;
			        	  J = i;
			          }
			
		}
		
	
		
		
		
		
	if (CValue <= RValue && I != -1) {
		  Vector<Integer> perColumns = drehe(Columns, I);
		  Columns = perColumns;
		  System.out.println("Columns Rotation  " + I + "  " + calculateCriterium(Columns,Rows));
	}
	
	
	
	if (CValue > RValue) {
		  Vector<Integer> perRows = drehe(Rows, J);
		  Rows = perRows;
		  System.out.println("Rows Rotation " + RowJ +  "   " + calculateCriterium(Columns,Rows));
	}
		
		
		
	
	
	
	/*Zeile oder Spalten von einer in eine andere Stelle infügen**/
	
	
	
	
	
	
	
	
	        ColI = -2;
	        ColJ = -2;
	        RowI = -2;
	        RowJ = -2;
	    
	        colValue = calculateCriterium(Columns,Rows);;
	        rowValue = colValue;
	    
	     
		
		//find max
		for (int i = 0; i < Columns.size(); i++) {
			for (int j = 0; j < Columns.size(); j++) {
		              Vector<Integer> perColumns = insert(Columns, i , j);	
			          double crit = calculateCriterium(perColumns, Rows);
			          if (crit < colValue) {
			        	  colValue = crit;
			        	  ColI = i;
			        	  ColJ = j;
			          }
			
		}
		}
		
	
		for (int i = 0; i < Rows.size(); i++) {
			for (int j = 0; j < Rows.size(); j++) {
		              Vector<Integer> perRows = insert(Rows, i , j);	
			          double crit = calculateCriterium(Columns, perRows);
			          if (crit < rowValue) {
			        	  rowValue = crit;
			        	  RowI = i;
			        	  RowJ = j;
			          }
			
		}
		}
	
		
		
		
		
		
		if (colValue <= rowValue && ColI != -2) {
			  Vector<Integer> perColumns = insert(Columns, ColI , ColJ);
			  Columns = perColumns;
			  System.out.println("Insert Columns  " + ColI + "  "  + ColJ +  "   " + calculateCriterium(Columns,Rows));
		}
		
		
		
		if (colValue > rowValue) {
			  Vector<Integer> perRows = insert(Rows, RowI , RowJ);
			  Rows = perRows;
			  System.out.println("Insert Rows  " + RowI + "  "  + RowJ +  "   " + calculateCriterium(Columns,Rows));
		}
	
	
		
		
		/***Gleichzeitiges Vertauchen zweier Zeilen und Spalten**/
		
		
		
		
		
		
		
		
		
		  ColI = -2;
		  ColJ = -2;
		  RowI = -2;
		  RowJ = -2;
		    
		  double Value = calculateCriterium(Columns,Rows);;
		  
		    
		     
			
			//find max
			for (int i = -1; i < Columns.size(); i++) {
				for (int j = 0; j <= Columns.size(); j++) {
			              Vector<Integer> perColumns = vertausche(Columns, i , j);	
			              
			              
			              
			          	for (int ii = -1; ii < Rows.size(); ii++) {
							for (int jj = 0; jj <= Rows.size(); jj++) {
						              Vector<Integer> perRows = vertausche(Rows, ii , jj);
						              
						              
			              
				          double crit = calculateCriterium(perColumns, perRows);
				          if (crit < Value) {
				        	  Value = crit;
				        	  ColI = i;
				        	  ColJ = j;
				        	  RowI = ii;
				        	  RowJ = jj;
				          }
				
			}
			}
			
		
				}
			}
			
			
			
			
			if (colValue <= rowValue && ColI != -2) {
				Vector<Integer> perColumns = vertausche(Columns, ColI , ColJ);
				  Vector<Integer> perRows = vertausche(Rows, RowI , RowJ);
				  Columns = perColumns;
				  Rows = perRows;
				  System.out.println("Columns + Row  " + ColI + "  "  + ColJ +  "   " + calculateCriterium(Columns,Rows));
			}
			
			
			
			
		
		
			
		
		
		
		
		
		
		
		

}


	
	
	public Vector<Integer> insert(Vector<Integer> Columns, int col , int pos) {
		   Vector<Integer> res = new Vector();
		   for (int i = 0; i < Columns.size(); i++) {
			   if (i != col) res.add(Columns.elementAt(i));
			   
		   }
		   int newpos = 0;
		   if (col >= pos) newpos = pos;
		   else newpos = pos-1;
		   res.add(newpos,Columns.elementAt(col));
		   return res;
		   
		   
	}
	
	
	public Vector<Integer> vertausche(Vector<Integer> Columns, int i , int j) {
		Vector<Integer> v = new Vector();
		
		if (i == j) return Columns;
	
		if (i == -1 && j != Columns.size()) {
			v.add(Columns.elementAt(j));
			for (int a = 0; a < Columns.size(); a++) {
				if (a != j) v.add(Columns.elementAt(a));
			}
			return v;
		}
		
		
		if (j == Columns.size() && i !=-1) {
			
			for (int a = 0; a < Columns.size(); a++) {
				if (a != i) v.add(Columns.elementAt(a));
			}
			v.add(Columns.elementAt(i));
			return v;
		}
		
		
		
		
		
		for (int a = 0; a < Columns.size(); a++) {
			if (a == i) v.add(Columns.elementAt(j));
			if (a == j) v.add(Columns.elementAt(i));
			if ((a != i) && (a != j)) v.add(Columns.elementAt(a));
			
		}
		
		return v;
	}
	
	
	
	public Vector<Integer> drehe(Vector<Integer> Columns, int j) {
		Vector<Integer> v = new Vector();	
		
		for (int a = j; a < Columns.size()+j; a++) {
			v.add(Columns.elementAt(a%Columns.size()));
			
		}
		
		return v;
	}
	
	
	
	
	
	/**
	 * 
	 *   Abstände in den Zeilen und Spalten
	 * 
	 * 
	 * */
	
	public double calculateCriterium2(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
		double crit = 0;
		
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
			for (int j = i+1; j < ClusterColumns.size(); j++) {
				for (int row = 0; row < ClusterRows.size(); row++) {
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(row)];
					int b = matrix [ClusterColumns.elementAt(j)][ClusterRows.elementAt(row)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
   
		
		
		for (int i =0; i < ClusterRows.size()-1; i++) {
			for (int j = i+1; j < ClusterRows.size(); j++) {
				for (int col = 0; col < ClusterColumns.size(); col++) {
					int a = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(i)];
					int b = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(j)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
		
		
        
        
		return crit;
		
	}
	
	

	/**
	 * 
	 *   Alle Abstände
	 * 
	 * 
	 * */
	
	public double calculateCriterium3(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
double crit = 0;
		
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
			for (int j = i+1; j < ClusterColumns.size(); j++) {
				for (int row = 0; row < ClusterRows.size(); row++) {
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(row)];
					int b = matrix [ClusterColumns.elementAt(j)][ClusterRows.elementAt(row)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
   
		
		
		for (int i =0; i < ClusterRows.size()-1; i++) {
			for (int j = i+1; j < ClusterRows.size(); j++) {
				for (int col = 0; col < ClusterColumns.size(); col++) {
					int a = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(i)];
					int b = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(j)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
	    for (int j =0; j < ClusterRows.size()-1; j++) {
			
			
			
	    	for (int ii =i+1; ii < ClusterColumns.size(); ii++) {
	    	for (int jj =j+1; jj < ClusterRows.size(); jj++) {
	    	    	
	    	    	
	    	    
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
					int b = matrix [ClusterColumns.elementAt(ii)][ClusterRows.elementAt(jj)];
					
					crit+= Math.min(a,b)*(ii+jj-i-j-1);
					
				}
				
				
				
			
			}}
			
		}
   
		
		
		
		
		
        
        
		return crit;
		
	}
	
	

	
	
	
	
	
	/**
	 * 
	 *    Bestrafung falscher Ausrichtung
	 * 
	 * 
	 * */
	
	public double calculateCriterium(Vector<Integer>ClusterColumns ,Vector<Integer> ClusterRows) {
		double crit = 0;
		/*
		
		for (int i =0; i < ClusterColumns.size()-1; i++) {
			for (int j = i+1; j < ClusterColumns.size(); j++) {
				for (int row = 0; row < ClusterRows.size(); row++) {
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(row)];
					int b = matrix [ClusterColumns.elementAt(j)][ClusterRows.elementAt(row)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}
   
		
		
		for (int i =0; i < ClusterRows.size()-1; i++) {
			for (int j = i+1; j < ClusterRows.size(); j++) {
				for (int col = 0; col < ClusterColumns.size(); col++) {
					int a = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(i)];
					int b = matrix [ClusterColumns.elementAt(col) ][ClusterRows.elementAt(j)];
					
					crit+= Math.min(a,b)*(j-i-1);
					
				}
				
				
				
			
			}
			
		}*/
		
		
		
		for (int i =1; i < ClusterColumns.size(); i++) {
	    for (int j =0; j < ClusterRows.size()-1; j++) {
			
			
			
	    	for (int ii =0; ii < i; ii++) {
	    	for (int jj =j+1; jj < ClusterRows.size(); jj++) {
	    	    	
	    	    	
	    	    
					int a = matrix [ClusterColumns.elementAt(i)][ClusterRows.elementAt(j)];
					int b = matrix [ClusterColumns.elementAt(ii)][ClusterRows.elementAt(jj)];
					
					crit+= a*b;
					
				}
				
				
				
			
			}}
			
		}
   
		
		
		
		
		
        
        
		return crit;
		
	}
	
	
	
	
	
	
	






}


	
	





	
	
	
}
	
	
	