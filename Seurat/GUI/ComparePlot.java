package GUI;


import java.util.*;

import javax.swing.*;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.awt.event.*;
import java.awt.*;

import Data.GeneVariable;
import Data.ISelectable;
import GUI.BarchartPanel.Balken;

public class ComparePlot extends JFrame implements IPlot {

	Seurat seurat;

	ComparePanel panel;
	
	JMenuItem item;

	public ComparePlot(Seurat seurat, String method1, String method2, Vector Experiments1, Vector Experiments2) {
		super("ComparePlot: ( " + method1 + " , " + method2 + " )");
		this.seurat = seurat;

		panel = new ComparePanel(this,seurat, method1, method2, Experiments1, Experiments2);

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

	public void print() {
		// TODO Auto-generated method stub
		
	}

}

class ComparePanel extends JPanel implements KeyListener, MouseListener,
		IPlot,MouseMotionListener {
	

	//double[] balken;

	//double[] selectedBalken;


	Seurat seurat;


	JMenuItem item = new JMenuItem("");

	ComparePanel comparePanel = this;

	Point point1, point2;

	Image image;

	int abstandLinks = 80;

	int abstandOben = 20;

	int BinHeigth= 20;

	int abstandString = 5;


    JPopupMenu menu;
	
    
    int [][] dist1;
    int [][] dist2;
    
    Vector<ComparePoint> points;
    
    boolean  VIEW = true;
	
	//double[] koeff;

	//int[] balkenWidth;

	//int[] balkenSelectedWidth;

//	String [] binStrings;
    
    
    Vector<Data.ISelectable> Experiments1;
    Vector<Data.ISelectable> Experiments2;
    
    ComparePlot plot;

	

	public ComparePanel(ComparePlot plot,Seurat seurat, String method1, String method2, Vector<Data.ISelectable> Experiments1, Vector<Data.ISelectable> Experiments2) {

	
		this.seurat = seurat;
	    this.plot = plot;
        this.Experiments1 = Experiments1;
        this.Experiments2 = Experiments2;
		
	
		this.calculatePoints();
		
		
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
			//	updateSelection();
				
			}

			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});


	}
	
	
	
	
	public void calculatePoints() {
	
		dist1 = new int [Experiments1.size()][Experiments1.size()];
		dist2 = new int [Experiments2.size()][Experiments2.size()];
		
		for (int i = 0; i < Experiments1.size(); i++) {
			for (int j = 0; j < Experiments1.size(); j++) {
				int dist =Math.abs(i - j);
				//int dist = Math.min(Math.min(Math.abs(i - j), Math.abs(i+ Experiments1.size() - j)), Math.abs(j+ Experiments1.size() - i));
				dist1 [Experiments1.elementAt(i).getID()][Experiments1.elementAt(j).getID()] = dist;
			}
			
		}
		
		
		for (int i = 0; i < Experiments2.size(); i++) {
			for (int j = 0; j < Experiments2.size(); j++) {
				int dist = Math.abs(i - j);
				//int dist = Math.min(Math.min(Math.abs(i - j), Math.abs(i+ Experiments2.size() - j)), Math.abs(j+ Experiments2.size() - i));
				dist2 [Experiments2.elementAt(i).getID()][Experiments2.elementAt(j).getID()] = dist;
			}
			
		}
		
		points = new Vector();
		/*
		if (Experiments1.elementAt(0).isVariable() && CompareSelectionManager.expPoints == null) {
			CompareSelectionManager.expPoints = new ComparePoint [seurat.dataManager.Experiments.size()][seurat.dataManager.Experiments.size()];
			for (int i = 0; i < seurat.dataManager.Experiments.size(); i++) {
				for (int j = 0; j < seurat.dataManager.Experiments.size(); j++) {
					CompareSelectionManager.expPoints [i][j] = new ComparePoint(seurat.dataManager.Experiments.elementAt(i),seurat.dataManager.Experiments.elementAt(j));
				}
			}
		}
		
		
		if (!Experiments1.elementAt(0).isVariable() && CompareSelectionManager.genePoints == null) {
			CompareSelectionManager.genePoints = new ComparePoint [seurat.dataManager.Genes.size()][seurat.dataManager.Genes.size()];
			for (int i = 0; i < seurat.dataManager.Genes.size(); i++) {
				for (int j = 0; j < seurat.dataManager.Genes.size(); j++) {
					CompareSelectionManager.genePoints [i][j] = new ComparePoint(seurat.dataManager.Genes.elementAt(i),seurat.dataManager.Genes.elementAt(j));
				}
			}
		}
		
		
		
		*/
		
		
		for (int i = 0; i < Experiments1.size(); i++) {
			for (int j = 0; j < Experiments2.size(); j++) {
				points.add(new ComparePoint(Experiments1.elementAt(i), Experiments2.elementAt(j),new Point(
				dist1 [Experiments1.elementAt(i).getID()][Experiments2.elementAt(j).getID()],
				dist2 [Experiments1.elementAt(i).getID()][Experiments2.elementAt(j).getID()])));
					}
			
		}
		
		
		
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
		
		seurat.dataManager.deleteSelection();
		
		
		for (int i = 0; i < points.size(); i++) {
			ComparePoint point = points.elementAt(i);
		
		    point.setSelected(false);
		}
			
			
		for (int i = 0; i < points.size(); i++) {
			ComparePoint point = points.elementAt(i);
		    int x = 	(int)Math.round(point.getX() * this.getWidth() / Experiments1.size());
			int y = this.getHeight() - (int)Math.round(point.getY() * this.getHeight()/ Experiments1.size());  	
			
			
			
			if (this.isPointInRect(x, y, point1, point2)) {
				point.setSelected(true);
				
			}
			
			
			
			/*
			int id1 =-1, id2 = -1;
			if (this.isPointInRect(x, y, point1, point2)) {
				id1 = Experiments1.elementAt(i).getID();
				id2 = Experiments2.elementAt(i).getID();
				
			}
			
			if (Experiments1.elementAt(0).isVariable()) {
				CompareSelectionManager.expPoints [id1][id2].setSelected(true);
				CompareSelectionManager.expPoints [id1][id2].setSelected(false);
			}
			else {
				CompareSelectionManager.genePoints [id1][id2].setSelected(true);
				CompareSelectionManager.genePoints [id1][id2].setSelected(false);
				
			}
			*/
			
		
		}
		this.repaint();
		seurat.updateWithoutConfusionsPlot(plot);

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		point2 = e.getPoint();
		
		this.addSelection(point1, point2);
		point1 = null;
		point2 = null;
	
	}

	public void mouseClicked(MouseEvent e) {
		point1 = e.getPoint();
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	

	public void mouseDragged(MouseEvent e) {
		point2 = e.getPoint();
		this.repaint();

	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'c') VIEW = !VIEW;
		this.repaint();

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	

	

	public void  updateSelection() {
        System.out.println("uddd");
		
	   for (int i = 0; i < this.points.size(); i++ ) {
		   points.elementAt(i).updateSelection();
	   }
			
		
		
		this.repaint();

	}

	@Override
	public void paint(Graphics g) {
	
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

	
        
        
        if (VIEW) {
        for (int i = 0; i < points.size(); i++) {
        	ComparePoint p = points.elementAt(i);
        	/*
        	ComparePoint cp = null;
        	
        	
        	int id1 =-1, id2 = -1;
			id1 = Experiments1.elementAt(i).getID();
				id2 = Experiments2.elementAt(i).getID();
				
			
			
			
        	if (Experiments1.elementAt(0).isVariable()) {
        		cp = CompareSelectionManager.expPoints [id1][id2];
        	}
        	else cp = CompareSelectionManager.genePoints [id1][id2];
        	*/
        	
        	if (!p.isSelected()) {
        	
        		g.setColor(new Color(0,0,0,60));
        	
        	g.fillOval(
        			(int)Math.round(p.getX() * this.getWidth() / Experiments1.size() + (Math.random() - 1/2)*2), 
        			this.getHeight() - (int)Math.round(p.getY() * this.getHeight()/ Experiments1.size() + (Math.random() - 1/2)*2), 
        			3, 
        			3);
        	}
        }
        
        
        for (int i = 0; i < points.size(); i++) {
        	ComparePoint p = points.elementAt(i);
        	
        	/*ComparePoint cp = null;
        	
        	
        	
        	int id1 =-1, id2 = -1;
			id1 = Experiments1.elementAt(i).getID();
				id2 = Experiments2.elementAt(i).getID();
				
			
			
			
        	if (Experiments1.elementAt(0).isVariable()) {
        		cp = CompareSelectionManager.expPoints [id1][id2];
        	}
        	else cp = CompareSelectionManager.genePoints [id1][id2];
        	
        	
        	
        	*/
        	
        	
        	if (p.isSelected())  { g.setColor(new Color(255,0,0,60));
        
        	
        	g.fillOval(
        			(int)Math.round(p.getX() * this.getWidth() / Experiments1.size() + (Math.random() - 1/2)*2), 
        			this.getHeight() - (int)Math.round(p.getY() * this.getHeight()/ Experiments1.size() + (Math.random() - 1/2)*2), 
        			3, 
        			3);
        
        	}
        	
        }
        
        
        
        
        
        
        
        
        
        
        }
        
        
        if (!VIEW) {
        
        int [][] counts = new int [11][11];
        for (int i = 0; i < points.size(); i++) {
        	ComparePoint p = points.elementAt(i);
        	
        
        	counts [(int)Math.floor(p.getX() / Experiments1.size() * 10)]
        	       [(int)Math.floor(p.getY()/ Experiments1.size()  * 10)]
        	++;
        	
        }
        
        int max = 0;
        
        for (int i = 0; i < 11; i++) {
       	 for (int j = 0; j < 11; j++) {
       		if (max < counts [i][j]) max = counts [i][j];
       	 }
       	 }
        
        
        for (int i = 0; i < 11; i++) {
        	 for (int j = 0; j < 11; j++) {
        		 g.setColor(Color.GRAY);
        		 
        		 int x = this.getWidth()* i/11;
        		 int y = this.getHeight() - this.getHeight() * j/11 - this.getHeight() /11;
        		 int w = (int)Math.round((double) 3/4 *this.getWidth()/11 * counts [i][j] / max);
        		 int h = (int)Math.round((double) 3/4 *this.getHeight()/11 * counts [i][j] / max);
              	
        		 int b = this.getWidth()/11;
        	     int c = this.getHeight()/11;
        		 
        		 
             	g.fillRect(x + (b-w)/2 ,y + (c-h)/2,w,h);
             	
             	
             //	if (counts [i][j] * 100 / points.size()>=1) g.setColor(Color.red);
             //	else g.setColor(Color.black);
        	//	g.drawString((double)Math.round((double)counts [i][j] * 10000 / points.size())/100 +  " ", this.getWidth()* i/11+ 3 +(int)Math.round((double) 3/4 *this.getWidth()/11 * counts [i][j] / max), (this.getHeight() - this.getHeight() * j/11)- this.getHeight() /22);
        		 
             }
        }
        
        
        }
		
		
		
		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math.abs(point2.y
					- point1.y));
		}


	}

	
	
	@Override
	public String getToolTipText(MouseEvent e) {
		
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
	
	
	
	class ComparePoint {
		
	
		ISelectable var1,var2;
		
		Point point;
		
		boolean isSelected = false;
		
		public ComparePoint(ISelectable var1, ISelectable var2, Point point) {
		this.point = point;
			this.var1 = var1;
			this.var2 = var2;
		}
		
		
		
		public boolean isSelected() {
			return isSelected;
		}
		
		
		public void updateSelection() {
			if (var1.isSelected() && var2.isSelected()) isSelected = true;
			else isSelected = false;
		}
		
		
		public void setSelected(boolean isSel) {
		   isSelected = isSel;
		   if (isSel) {
			   var1.select(true);
			   var2.select(true);
		   }
		}
		
		
		public double getX() {return point.getX();}
		public double getY() {return point.getY();}
		
		
		
		
	}



	public void print() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
	
	
	
	

