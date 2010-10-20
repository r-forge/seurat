package GUI;

import java.util.*;

import javax.swing.*;

import Tools.Tools;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.awt.event.*;
import java.awt.*;

import Data.Cluster;
import Data.ClusterNode;
import Data.Clustering;
import Data.CoordinateNode;
import Data.Gene;
import Data.GeneVariable;
import Data.ISelectable;
import Data.Line;
import Data.Permutation;

public class ConfusionsPlot2 extends JFrame implements IPlot {

	Seurat seurat;

	ConfusionsPanel2 panel;

	JMenuItem item;

	public ConfusionsPlot2(Seurat seurat, String method1, String method2,
			Clustering Experiments1, Clustering Experiments2) {
		super("Confusion Matrix: ( " + method1 + " , " + method2 + " )");
		System.out.println("ConfusionsPlot2");
		this.seurat = seurat;

		panel = new ConfusionsPanel2(seurat, this, method1, method2,
				Experiments1, Experiments2);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);

		this.setBounds(400, 0, (int) Math.min(800, Experiments1.clusters.size()
				* 80 + panel.spaceLeft + panel.spaceRight), (int) Math
				.min(800, Experiments2.clusters.size() * 80 + 30)+30);

		this.setVisible(true);

		this.addKeyListener(panel);

		seurat.windows.add(this);

		item = new JMenuItem("Confusions Matrix");
		seurat.windowMenu.add(item);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				panel.seurat.windowMenu.remove(item);
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
		panel.print();
	}

}

class ConfusionsPanel2 extends JPanel implements KeyListener, MouseListener,
		IPlot, MouseMotionListener {



	Seurat seurat;

	JMenuItem item = new JMenuItem("");

	ConfusionsPanel2 confusionsPanel = this;

	Point point1, point2;

	Image image;

	int spaceLeft = 20;
	int spaceRight = 5;
	
	int spaceAbove = 20;
	int spaceBelow = 5;
	
	int spaceClusteringC = 0;	
	int spaceClusteringR = 0;
	
	
	int spaceString = 5;

	
	double groupParam = 0.2;

	JPopupMenu menu;


	Clustering Experiments1;
	Clustering Experiments2;
	
	
	Clustering originalExperiments1;
	Clustering originalExperiments2;
	
	

	int[][] matrix;
	
	int Max; 

	int[][] matrixS;

	ConfusionsPlot2 plot;

	//int count = -1;

	
	Vector<CoordinateNode> nodesR;

	Vector<CoordinateNode> nodesC;
	
	
	
	
	public ConfusionsPanel2(Seurat seurat, ConfusionsPlot2 plot, String method1,
			String method2, Clustering Experiments1, Clustering Experiments2) {
		
		this.seurat = seurat;

		
		this.plot = plot;

		this.Experiments1 = Experiments1.copy();
		this.Experiments2 = Experiments2.copy();
		
		this.originalExperiments1 = Experiments1.copy();
		this.originalExperiments2 = Experiments2.copy();
		

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

	
	void calculateSpaces() {
		if (Experiments1.node != null) {
			spaceClusteringR = 100;
		} 
		
		if (Experiments2.node != null) {
			spaceClusteringC = 100;
		} 
		
		int maxNameLength = 0;

		for (int i = 0; i < Experiments2.clusters.size(); i++) {
    		int len = getLength(Experiments2.clusters.elementAt(i).name, this.getGraphics());
			if (len > maxNameLength)
				maxNameLength = len;
		}

		this.spaceLeft = (int) Math.min(maxNameLength + 10, 120);
	}
	

	
	public void calculateTree() {
		this.nodesC = new Vector();
		this.nodesR = new Vector();
        
		
		
		if (Experiments2.node != null)    calculateClusteringRows(Experiments2.node);
		if (Experiments1.node != null)    calculateClusteringColumns(Experiments1.node);
	}	
	

	public void addSelection(Point point1, Point point2) {

		int width = this.getWidth() - spaceLeft - spaceRight - spaceClusteringC;
		int height = this.getHeight() - spaceAbove - spaceBelow - spaceClusteringR;

	
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
			for (int j = 0; j < (Experiments1.clusters.elementAt(i).items).size(); j++) {
				((ISelectable) Experiments1.clusters.elementAt(i).items.elementAt(j)).unselect(true);
			}

		}

		for (int i = 0; i < Experiments2.clusters.size(); i++) {
			for (int j = 0; j < Experiments2.clusters.elementAt(i).items.size(); j++) {
				((ISelectable) Experiments2.clusters.elementAt(i).items.elementAt(j)).unselect(true);
			}

		}

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix [0].length; j++) {

				int x = width * i / matrix.length + spaceLeft+spaceClusteringC;
				int y = height * j / matrix[0].length + spaceAbove+spaceClusteringR;
				int w = (int) Math.round((double) 3 / 4 * width / matrix.length
						* Math.sqrt(((double) matrix[i][j] / Max)));
				int h = (int) Math.round((double) 3 / 4 * height
						/ matrix[0].length
						* Math.sqrt((double) matrix[i][j] / Max));

				int b = width / matrix.length;
				int c = height / matrix[0].length;

				if (Tools.isPointInRect(
						x + (b - w) / 2, 
						y + (c - h) / 2, 
						point1, 
						point2)
		                
						&& 
		                
						Tools.isPointInRect(
						x + (b + w) / 2, 
						y + (c + h) / 2, 
						point1, 
						point2)) {
					selectRect(i, j);
				}
				
				

			}
		}

		seurat.repaintWindows();

	}

	public void selectRect(int col, int row) {

		Vector cluster1 = Experiments1.clusters.elementAt(col).items;

		Vector cluster2 = Experiments2.clusters.elementAt(row).items;

		for (int ii = 0; ii < cluster1.size(); ii++) {
			if (cluster2.indexOf(cluster1.elementAt(ii)) != -1)
				((ISelectable) cluster1.elementAt(ii)).select(true);

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

					permuteMatrix();

				}
			});
			menu.add(item);
/*
			menu.addSeparator();

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
			menu.add(item);*/

			menu.addSeparator();

			item = new JMenuItem("Print");
			item.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();

					print();

				}
			});
			menu.add(item);

			menu.show(this, e.getX(), e.getY());
			return;
		}

		int width = this.getWidth() - spaceLeft- spaceRight - spaceClusteringC;
		int height = this.getHeight() - spaceAbove- spaceBelow - spaceClusteringR;

		int i = matrix.length * (e.getX() - spaceLeft-spaceClusteringC) / width;
		int j = (e.getY() - spaceAbove-spaceClusteringR) * matrix[0].length / height;

	

		if (i < 0 || j < 0 || i >= matrix.length || j >= matrix[0].length) return;

		if (!e.isShiftDown()) seurat.dataManager.deleteSelection();

		Vector cluster1 = (Vector) Experiments1.clusters.elementAt(i).items;

		Vector cluster2 = (Vector) Experiments2.clusters.elementAt(j).items;

		for (int ii = 0; ii < cluster1.size(); ii++) {
			if (cluster2.indexOf(cluster1.elementAt(ii)) != -1)
				((ISelectable) cluster1.elementAt(ii)).select(true);

		}

		seurat.repaintWindows();

	}

	public void originalOrder() {
		
		this.Experiments1 = originalExperiments1.copy();
		this.Experiments2 = originalExperiments2.copy();
		
		updateSelection();
		paint(this.getGraphics());
		repaint();
	}
	
	
	
	public void permuteMatrix() {
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
			Experiments1.clusters.elementAt(i).tempID = i;
		}
		for (int i = 0; i < Experiments2.clusters.size(); i++) {
			Experiments2.clusters.elementAt(i).tempID = i;
		}
		int Crit = calculateCriterion(Experiments1,Experiments2);
		int temp;
		
		while (Crit<(temp = permute())) {
			Crit = temp;
		}
		
		
		updateSelection();
		paint(this.getGraphics());
		repaint();
	}
	
	

	public int permute() {
		
		Vector<Permutation> colPermutations = Experiments1.getAllPermutations();
		Vector<Permutation> rowPermutations = Experiments2.getAllPermutations();
		int maxI = colPermutations.size()-1;
		int maxJ = rowPermutations.size()-1;
		
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
			Experiments1.clusters.elementAt(i).tempID = i;
		}
		for (int i = 0; i < Experiments2.clusters.size(); i++) {
			Experiments2.clusters.elementAt(i).tempID = i;
		}
				
		int criterion = calculateCriterion(Experiments1,Experiments2);
		
		for (int i = 0; i < colPermutations.size(); i++) {
		for (int j = 0; j < rowPermutations.size(); j++) {
				Permutation pC = colPermutations.elementAt(i);
				Permutation pR = rowPermutations.elementAt(j);
			    Clustering c = Experiments1.permute(pC);
			    Clustering r = Experiments2.permute(pR);
				
				int crit = calculateCriterion(c,r);
				
				if (crit > criterion) {
					criterion = crit;
					maxI = i;
					maxJ = j;
				}
		}	
		}
		
		
		Experiments1 = Experiments1.permute(colPermutations.elementAt(maxI)); 
		Experiments2 = Experiments2.permute(rowPermutations.elementAt(maxJ)); 
		System.out.println("   ->  " + criterion);
		
		
		
		
		matrix = new int[Experiments1.clusters.size()][Experiments2.clusters.size()];
		

		
		
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
		Vector cluster1 = (Vector) Experiments1.clusters.elementAt(i).items;

			for (int j = 0; j < Experiments2.clusters.size(); j++) {
            Vector cluster2 = (Vector) Experiments2.clusters.elementAt(j).items;

		           for (int ii = 0; ii < cluster1.size(); ii++) {
				   if (cluster2.indexOf(cluster1.elementAt(ii)) != -1) {
						matrix[i][j]++;
						
				   }
                   }
			}
		}

		
		return criterion;

	}
	
	
	
	
	
	
	
	public int calculateCriterion(Clustering Experiments1,Clustering Experiments2) {
		int crit = 0;
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
		for (int j = 0; j < Experiments2.clusters.size(); j++) {
			for (int ii = i; ii < Experiments1.clusters.size(); ii++) {
			for (int jj = j; jj < Experiments2.clusters.size(); jj++) {
			    crit+= 
			    matrix [Experiments1.clusters.elementAt(i).tempID][Experiments2.clusters.elementAt(j).tempID]*
			    matrix [Experiments1.clusters.elementAt(ii).tempID][Experiments2.clusters.elementAt(jj).tempID];
			   // intersect(Experiments1.clusters.elementAt(i),Experiments2.clusters.elementAt(j))*
			   // intersect(Experiments1.clusters.elementAt(ii),Experiments2.clusters.elementAt(jj));
			}
			}
		}
		}
		return crit;
		
	}
	
	
	public int intersect(Cluster c1, Cluster c2) {
		int count  =0;
		for (int i = 0; i < c1.items.size(); i++) 
				if (c2.items.indexOf(c1.items.elementAt(i))!=-1) count++;
		return count;
	}
	
	
	
	

	public void findGroup() {
		calculateGroups();
		repaint();
	}

	public void deleteGroup() {
		repaint();
	}

	public void deleteGroups() {
		
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

		
      //  permute();
	//	this.repaint();

	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
	}

	public void calculateGroups() {

	}

	public double groupsKriterium(Vector<Vector<Integer>> Columns,
			Vector<Vector<Integer>> Rows) {

	

		return -1;

	}

	public void updateSelection() {

		matrix = new int[Experiments1.clusters.size()][Experiments2.clusters.size()];
		matrixS = new int [Experiments1.clusters.size()][Experiments2.clusters.size()];
		
		for (int i = 0; i < matrixS.length; i++) {
			for (int j = 0; j < matrixS[0].length; j++) {
				matrixS[i][j] = 0;
			}
		}

		
		
		for (int i = 0; i < Experiments1.clusters.size(); i++) {
		Vector cluster1 = (Vector) Experiments1.clusters.elementAt(i).items;

			for (int j = 0; j < Experiments2.clusters.size(); j++) {
            Vector cluster2 = (Vector) Experiments2.clusters.elementAt(j).items;

		           for (int ii = 0; ii < cluster1.size(); ii++) {
				   if (cluster2.indexOf(cluster1.elementAt(ii)) != -1) {
						matrix[i][j]++;
						if (((ISelectable) cluster1.elementAt(ii)).isSelected())
							matrixS[i][j]++;
				   }
                   }
			}
		}
	

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (Max < matrix[i][j])  Max = matrix[i][j];
			}
		}


		this.repaint();

	}

	@Override
	public void paint(Graphics g) {

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		calculateSpaces();
		
		if (matrix == null) this.updateSelection();
		calculateTree();
			

		g.setColor(Color.black);

        /**draw gitter*/	
		
		int width = this.getWidth() - spaceLeft- spaceRight - spaceClusteringC;
		int height = this.getHeight() - spaceAbove- spaceBelow - spaceClusteringR;
		
		for (int i = 0; i <= matrix.length; i++) {
		for (int j = 0; j <= matrix[0].length; j++) {
				
			g.drawLine(width * i / matrix.length + spaceLeft + spaceClusteringC, 
					   spaceAbove+spaceClusteringR,
					   width * i / matrix.length + spaceLeft + spaceClusteringC, 
					   this.getHeight() - 5
			);

			g.drawLine(spaceLeft + spaceClusteringC, 
					   spaceAbove+spaceClusteringR +height * j / matrix[0].length, 
					   this.getWidth() - spaceRight, 
					   spaceAbove+spaceClusteringR + height * j/ matrix[0].length
			);

		}		
		}
		
		/**draw Strings*/
		
	
		for (int j = 0; j < matrix[0].length; j++) {

			String s = cutLabels(Experiments2.clusters.elementAt(j).name, spaceLeft - 10, g);
			g.drawString(s, spaceLeft + spaceClusteringC - getLength(s, g) - 5, 
					spaceAbove+spaceClusteringR+ height * j/matrix[0].length  + height / matrix[0].length / 2 + 5);

		}

		for (int i = 0; i < matrix.length; i++) {

			String s = cutLabels(Experiments1.clusters.elementAt(i).name, 
					(width - 10) / matrix.length, g);

			g.drawString(s, 
					spaceLeft + spaceClusteringC + width * i / matrix.length +  (width / matrix.length - getLength(s, g)) / 2,
					spaceAbove + spaceClusteringR - 5);

			
		}
		
			
		
		
		
		for (int i = 0; i < matrix.length; i++) {
		for (int j = 0; j < matrix[0].length; j++) {

		
			    g.setColor(Color.black);

				int x = spaceLeft+spaceClusteringC + width * i / matrix.length;
				int y = spaceAbove+spaceClusteringR+ height * j / matrix[0].length;
				int w = (int) Math.round((double) 3 / 4 * width / matrix.length
						* Math.sqrt(((double) matrix[i][j] / Max)));
				int h = (int) Math.round((double) 3 / 4 * height/ matrix[0].length
						* Math.sqrt((double) matrix[i][j] / Max));

				int b = width / matrix.length;
				int c = height / matrix[0].length;

		
				g.setColor(Color.GRAY);
				g.fillRect(x + (b - w) / 2, y + (c - h) / 2, w, h);

				
				g.setColor(Color.RED);
				if (matrixS[i][j] > 0) {
					double koeff = (double) matrixS[i][j] / matrix[i][j];
					g.fillRect(x + (b - w) / 2, 
							(int) Math.round(y + (c + h) / 2 - h * koeff), 
							w, 
							(int) Math.round(h * koeff));
				}

				g.setColor(Color.black);
            	if (w != 0) g.drawRect(x + (b - w) / 2, y + (c - h) / 2, w, h);
            	
			}
		}

		int colShift = 0;
		int rowShift = 0;
		
		/*
		if (ClusterColumns != null) {

			for (int i = 0; i < ClusterColumns.size(); i++) {
				int shiftCol = ClusterColumns.elementAt(i).size();
				int shiftRows = ClusterRows.elementAt(i).size();

				g.setColor(Color.RED);

				g.drawRect(width * colShift / counts.length + abstandLinks,

				height * rowShift / counts[0].length + 20,

				width * shiftCol / counts.length,

				height * shiftRows / counts[0].length);

				g.drawRect(width * colShift / counts.length + abstandLinks + 1,

				height * rowShift / counts[0].length + 20 + 1,

				width * shiftCol / counts.length,

				height * shiftRows / counts[0].length);

				g.drawRect(width * colShift / counts.length + abstandLinks - 1,

				height * rowShift / counts[0].length + 20 - 1,

				width * shiftCol / counts.length,

				height * shiftRows / counts[0].length);

				colShift += shiftCol;
				rowShift += shiftRows;

			}

		}
*/
		
		paintClustering(g);
		
		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math.abs(point2.y
					- point1.y));
		}

	}
	
	
	


	public void paintClustering(Graphics g) {

		if (nodesR != null) {

			for (int i = 0; i < nodesR.size(); i++) {
				CoordinateNode node = nodesR.elementAt(i);
				if (node.isSelected)   g.setColor(Color.RED);
				else  				   g.setColor(Color.BLACK);

				for (int j = 0; j < node.Lines.size(); j++) {
					Line line = node.Lines.elementAt(j);
					g.drawLine(line.x1, line.y1, line.x2, line.y2);
				}
			}

		}

		if (nodesC != null) {

			for (int i = 0; i < nodesC.size(); i++) {
				CoordinateNode node = nodesC.elementAt(i);

				if (node.isSelected) g.setColor(Color.RED);
				else  				 g.setColor(Color.BLACK);

				for (int j = 0; j < node.Lines.size(); j++) {
					Line line = node.Lines.elementAt(j);
					g.drawLine(spaceLeft + spaceClusteringC + line.x1, line.y1, spaceLeft + spaceClusteringC + line.x2, line.y2);
				}

			}

		}

	}

	
	
	
	public void calculateClusteringRows(ClusterNode node) {
		
       
		if (node.nodeL != null && node.nodeR != null) {
			
			
			int pos = getYCoordinate(node);
			int x = (int) Math.round(this.spaceClusteringC - this.spaceClusteringC * node.currentHeight);

			if (node.nodeL != null) {

				int posL = getYCoordinate(node.nodeL);
				int xL = (int) Math.round(this.spaceClusteringC - this.spaceClusteringC * node.nodeL.currentHeight);

				CoordinateNode nodeC = new CoordinateNode(node.nodeL, x, posL,xL, posL);
				nodeC.Lines.add(new Line(x, pos, x, posL));
				nodeC.Lines.add(new Line(x, posL, xL, posL));

				node.nodeL.cNode = nodeC;

				if (node.nodeL.isSelected()) nodeC.isSelected = true;
				this.nodesR.add(nodeC);

				calculateClusteringRows(node.nodeL);

			}

			if (node.nodeR != null) {

				int posR = getYCoordinate(node.nodeR);
				int xR = (int) Math.round(this.spaceClusteringC - this.spaceClusteringC * node.nodeR.currentHeight);

				CoordinateNode nodeC = new CoordinateNode(node.nodeR, x, posR, xR, posR);
				nodeC.Lines.add(new Line(x, pos, x, posR));
				nodeC.Lines.add(new Line(x, posR, xR, posR));
				this.nodesR.add(nodeC);

				node.nodeR.cNode = nodeC;

				if (node.nodeR.isSelected()) nodeC.isSelected = true;

				calculateClusteringRows(node.nodeR);
			}

		}
		else {
			int pos = getYCoordinate(node);
			int x = (int) Math.round(this.spaceClusteringC - this.spaceClusteringC * node.currentHeight);

			CoordinateNode nodeC = node.cNode;
			nodeC.Lines.add(new Line(x, pos, spaceClusteringC, pos));
			
		}
		

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void calculateClusteringColumns(ClusterNode node) {

		if (node.nodeL != null && node.nodeR != null) {

			int pos = getXCoordinate(node) - spaceLeft-spaceClusteringC;
			int y = (int) Math.round(this.spaceClusteringR - this.spaceClusteringR * node.currentHeight);

			if (node.nodeL != null) {

				int posL = getXCoordinate(node.nodeL) - spaceLeft-spaceClusteringC;
				int yL = (int) Math.round(this.spaceClusteringR - this.spaceClusteringR
						* node.nodeL.currentHeight);

				CoordinateNode nodeC = new CoordinateNode(node.nodeL, pos, y,
						posL, y);
				nodeC.Lines.add(new Line(pos, y, posL, y));
				nodeC.Lines.add(new Line(posL, y, posL, yL));
				this.nodesC.add(nodeC);

				node.nodeL.cNode = nodeC;

				if (node.nodeL.isSelected())
					nodeC.isSelected = true;

				calculateClusteringColumns(node.nodeL);
			}

			if (node.nodeR != null) {

				int posR = getXCoordinate(node.nodeR) - spaceLeft-spaceClusteringC;
				int yR = (int) Math.round(this.spaceClusteringR - this.spaceClusteringR * node.nodeR.currentHeight);

				CoordinateNode nodeC = new CoordinateNode(node.nodeR, pos, y, posR, y);
				nodeC.Lines.add(new Line(posR, y, posR, yR));
				this.nodesC.add(nodeC);
				node.nodeR.cNode = nodeC;

				if (node.nodeR.isSelected()) nodeC.isSelected = true;
				calculateClusteringColumns(node.nodeR);
			}

		}
		else {

			int pos = getXCoordinate(node) - spaceLeft-spaceClusteringC;
			int y = (int) Math.round(this.spaceClusteringR - this.spaceClusteringR * node.currentHeight);
			CoordinateNode nodeC = node.cNode;
			nodeC.Lines.add(new Line(pos, y, pos, spaceClusteringR));

		}

	}

	
	public int getLeafYCoordinate(ClusterNode node) {
	       return spaceAbove+ spaceClusteringR +  (2*Experiments2.node.getLeafList().indexOf(node)+1)*   (this.getHeight()- spaceAbove-spaceBelow - spaceClusteringR)/Experiments2.node.getLeafList().size()/2;
	}
	
	public int getLeafXCoordinate(ClusterNode node) {
	       return  spaceLeft +  spaceClusteringC + (2*Experiments1.node.getLeafList().indexOf(node)+1)*(this.getWidth() - spaceLeft-spaceRight - spaceClusteringC)/Experiments1.node.getLeafList().size()/2;
	}
	
	
	
	
	public int getYCoordinate(ClusterNode node) {
		
		  int y = 0;
		  Vector<ClusterNode> Leafs = node.getLeafList();
		  for (int i = 0; i < Leafs.size(); i++) {
			  y+=getLeafYCoordinate(Leafs.elementAt(i));
		  }
		  
	       return y/Leafs.size();
	}
	
	public int getXCoordinate(ClusterNode node) {
		 int x = 0;
		  Vector<ClusterNode> Leafs = node.getLeafList();
		  for (int i = 0; i < Leafs.size(); i++) {
			  x+=getLeafXCoordinate(Leafs.elementAt(i));
		  }
		
	      return x/Leafs.size();	
    }
	
	
	
	
	
	
	

	public String cutLabels(String s, int availablePlace, Graphics g) {
		s = s.replaceAll("\"", "");
		String ss = this.cutLabelsHelp(s, availablePlace, g);
		if (ss.length() < 5)
			return ss;

		int Width = 0;
		for (int i = 0; i < ss.length(); i++)
			Width += g.getFontMetrics().charWidth(ss.charAt(i));
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

	public int getLength(String s, Graphics g) {
		int Width = 0;
		for (int i = 0; i < s.length(); i++)
			Width += g.getFontMetrics().charWidth(s.charAt(i));

		return Width;
	}

	public String cutLabelsHelp(String s, int availablePlace, Graphics g) {
		int Width = 0;
		for (int i = 0; i < s.length(); i++)
			Width += g.getFontMetrics().charWidth(s.charAt(i));
		if (Width < availablePlace)
			return s;

		Width = 0;
		/*
		 * String[] split = s.split(" "); String cutS = ""; if (split.length >
		 * 1) {
		 * 
		 * for (int i = 0; i < split.length; i++) if (split[i].length() > 1)
		 * cutS += split[i].substring(0, 1); else cutS += split[i]; for (int i =
		 * 0; i < cutS.length(); i++) Width +=
		 * g.getFontMetrics().charWidth(cutS.charAt(i)); if (Width <
		 * availablePlace) return cutS; }
		 */

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


	@Override
	public String getToolTipText(MouseEvent e) {

		if (!e.isControlDown())
			return null;

		int width = this.getWidth() - spaceLeft-spaceRight - spaceClusteringC;
		int height = this.getHeight()  - spaceAbove-spaceBelow - spaceClusteringR;

		

		int i = matrix.length * (e.getX()  - spaceLeft- spaceClusteringC) / width;
		int j = (e.getY()  - spaceAbove - spaceClusteringR) * matrix[0].length / height;

		if (i < 0 || j < 0 || i >= matrix.length || j >= matrix[0].length)
			return null;

		if (e.isControlDown() && matrix[i][j]!=0) return "selected: "+ matrixS[i][j] + "/"+ matrix[i][j] + " (" + matrixS[i][j]*100/matrix[i][j] +"%)";
		
		
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

	


	public void print() {
		// TODO Auto-generated method stub
		try {
			PrintJob prjob = getToolkit().getPrintJob(plot, null, null);
			Graphics pg = prjob.getGraphics();
			paint(pg);
			pg.dispose();
			prjob.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}