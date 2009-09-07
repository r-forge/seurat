package GUI;


import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.*;

class GlobalView extends JFrame implements MatrixWindow, IPlot {
	

	Seurat seurat;

	GlobalViewAbstractPanel gPanel;

	// int[] orderZeilen;

	// int[] orderSpalten;

	JMenuItem item;

	GlobalView globalView = this;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	DataManager dataManager;

	Vector<ISelectable> Experiments;
	Vector<ISelectable> Genes;
	
	JLabel infoLabel;
	
	boolean resize = true;
	
	int abstandUnten = 2;
	
	
	long timeResized;
	
	
	JPanel infoPanel;

	int oldPixelCount;

	public void setInfo(String info) {
		this.gPanel.setInfo(info);
	}
	
	
	
	
	public void applyNewPixelSize(int pixelW, int pixelH) {
		
		this.gPanel.pixelW = pixelW;
		this.gPanel.pixelH = pixelH;
		
		int col = Experiments.size();

		int row = Genes.size();
		
		
		int panelW = gPanel.abstandLinks + col* pixelW;
		
		
		int colorsHight = 0;

		
		for (int i = 0; i < gPanel.Columns.size(); i++) {
			ISelectable var = gPanel.Columns.elementAt(i);
            if (var.getColors() == null) break;
			
			for (int j = var.getColors().size() - 1; j >= 0; j--) {

				colorsHight = var.getColors().size() * (2 * this.gPanel.pixelH + 1) + 4;

				
			}
		}
		
		
		

		
		
		gPanel.upShift = gPanel.abstandOben + colorsHight;
		
		
		
		int panelH =  pixelH*( row/ gPanel.Aggregation )
		+ gPanel.upShift;
		
		

		gPanel.setPreferredSize(new Dimension(panelW, panelH));
		
		
		
		infoLabel.setText("Aggregation: 1 : " + gPanel.Aggregation);
	
	    
		
		int newWidth = 0,newHeight = 0;
		int oldWidth = this.getWidth();
		int oldHeight = this.getHeight();
		
		if (seurat.SYSTEM == seurat.WINDOWS) {

			   int scrollbarSpace = 0;	
			   if (panelH>840) scrollbarSpace = 16;
			   
			newWidth = 	panelW + 5+14 + scrollbarSpace;
			newHeight = 	panelH+ 26+8	+ infoPanel.getHeight() + abstandUnten;
			
			  newHeight = (int)Math.min(newHeight,830);
			   newWidth = (int)Math.min(newWidth,1100);

		} else {
			
		   int scrollbarSpace = 0;	
		   if (panelH>840) scrollbarSpace = 16;	 
		   newWidth = 	5+	panelW + 5 + scrollbarSpace;
		   newHeight = 	5+	panelH+ 26 + infoPanel.getHeight() + abstandUnten;
		   newHeight = (int)Math.min(newHeight,830);
		   newWidth = (int)Math.min(newWidth,1100);
		   
		//   newHeight = 		panelH+ 16 + infoPanel.getHeight() + abstandUnten;
		}
		
		
	
			this.setSize(newWidth,newHeight);
		
		
		
		
	    gPanel.calculateTree();
		updateSelection();
	   
		
	}
	
	
	
	
	
	

	public GlobalView(Seurat seurat, String name, Vector Experiments,
			Vector Genes, boolean clustering) {
		super(name);

		item = new JMenuItem(name);
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.Experiments = Experiments;
		this.Genes = Genes;
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEtchedBorder());
		p.setLayout(new BorderLayout());
			
		
		
		GlobalViewAbstractPanel panel = new GlobalViewAbstractPanel(seurat, this,
				Experiments, Genes);

		
		this.addKeyListener(panel);
		this.setFocusTraversalKeysEnabled(false );
		
		
		
		p.add(panel,BorderLayout.CENTER);
		
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		infoPanel = new JPanel();
	//	if (Experiments.size() < 116) infoPanel.setPreferredSize(new Dimension(300,45));
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		infoPanel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(infoPanel,BorderLayout.SOUTH);
		
		
		
		this.addMouseListener(panel);

		int count = Genes.size();
		/*
		 * int height = Math.min(700, count);
		 * 
		 * panel.PixelCount = (height- panel.abstandOben) /
		 * globalView.dataManager.PixelSize;
		 * 
		 * 
		 * panel.Width = count / panel.PixelCount;
		 * 
		 * if ( (count - panel.PixelCount * panel.Width)>panel.Width) {
		 * panel.Width++; panel.PixelCount = count / panel.Width; }
		 * 
		 */
		int Width = 1;
		while ((count / Width) * panel.pixelH > 700) {
			Width++;
		}

		panel.PixelCount = count / Width;
		if (count % Width > 0)
			panel.PixelCount++;
		panel.Aggregation = Width;

		int col = Experiments.size();

		int row = Genes.size();

		oldPixelCount = panel.PixelCount;

		panel.setPreferredSize(new Dimension(panel.abstandLinks + col
				* panel.pixelW, panel.abstandOben 
				+ row
				* panel.pixelH
				/ panel.Aggregation
			));
		gPanel = panel;

		if (clustering) {
			gPanel.clustering = true;
			panel.abstandLinks = 150;
			panel.abstandOben = 150;
            panel.upShift = panel.abstandOben;
            
            panel.calculateIndexes();
            
            
          //  this.addKeyListener(gPanel);
		}
		
		
		JScrollPane sPane = new JScrollPane(p);
		sPane.addKeyListener(panel);
		
this.getContentPane().add(sPane, BorderLayout.CENTER);
		
		
		
		
		
		infoLabel = new JLabel("Aggregation: 1 : " + gPanel.Aggregation);
		Font myFont = new Font("SansSerif", 0, 10);

		
		JLabel label = new JLabel("Columns: " + Experiments.size());
		label.setFont(myFont);
		infoPanel.add(label);
		
		
		
		label = new JLabel(" Rows: "+Genes.size()+"  ");
		label.setFont(myFont);
		infoPanel.add(label);
		
		
		
		
		infoLabel.setFont(myFont);
		infoPanel.add(infoLabel);
		
		
		
		
		
		
		
/*
		if (seurat.SYSTEM == seurat.WINDOWS)
			this
					.setBounds(
							350,
							0,
							col * pixelSize + panel.abstandLinks + 19,
							panel.abstandOben+ 30
									+ row
									* pixelSize
									/ panel.Width
									+ 38
									);
		else
			this
					.setBounds(
							350,
							0,
							col * pixelSize + panel.abstandLinks + 5,
							panel.abstandOben+ 30
									+ row
									* pixelSize
									/ panel.Width
									+ 23
								);

		*/
		
		
		this.setLocation(550,0);
		
		this.setVisible(true);

		seurat.windows.add(this);

		seurat.windowMenu.add(item);

		panel.calculateMatrixValues();

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				globalView.setVisible(true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				globalView.seurat.windowMenu.remove(item);
			}
		});

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {

				/*
				long newTimeResized = System.currentTimeMillis();
				if (newTimeResized - timeResized > 200) {
					
					
					timeResized = newTimeResized;
				
					
				
				int count = globalView.Genes.size();
				
				
				int colors = 0;
				if (globalView.Experiments
						.elementAt(0) instanceof Variable) colors = ((Variable)globalView.Experiments
						.elementAt(0)).getBarchartToColors().size()
						* (2 * globalView.pixelSize + 2);
				

				gPanel.PixelCount = Math
				.min(
						(gPanel.getHeight() - globalView.gPanel.abstandOben - colors)
								/ globalView.seurat.settings.PixelSize,
						Math.min(700 / globalView.pixelSize, count));

		        gPanel.Width = count / gPanel.PixelCount;

		if ((count - gPanel.PixelCount * gPanel.Width) > gPanel.Width) {
			gPanel.Width++;
			gPanel.PixelCount = count / gPanel.Width;
		}
				
				
				
		gPanel.calculateMatrixValues();
			

				// Apply New PixelSize

				globalView.applyNewPixelSize(globalView.pixelSize);
				
				
				globalView.repaint();
				gPanel.repaint();
				
				

				}*/
				
				
			
			
		}
			
				
				
				
		});

		
	
	}

	public void updateSelection() {
		// TODO Auto-generated method stub
		gPanel.updateSelection();
		
		
	}

	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}




	public void applyNewPixelSize() {
		// TODO Auto-generated method stub
		this.applyNewPixelSize(gPanel.pixelW,gPanel.pixelH);
	}




	public void setModel(int model) {
		// TODO Auto-generated method stub
		gPanel.Model = model;
		
	}




	public void print() {
		// TODO Auto-generated method stub
		gPanel.print();
	}

}

class GlobalViewAbstractPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener, KeyListener, ColorListener {
	DataManager dataManager;

	Seurat seurat;

	//int pixelSize = 1;
	
	int pixelW = 1;
	int pixelH = 1;
	

	int abstandLinks = 2;

	int abstandOben = 1;

	Vector<ISelectable> Rows;

	Vector<ISelectable> Columns;
	
	
	
	Vector<ISelectable> originalRows;

	Vector<ISelectable> originalColumns;
	
	
	

	int[] originalOrderSpalten;

	ClusterNode nodeZeilen;

	ClusterNode nodeSpalten;
	
	boolean updateTree = true;

	Point point1, point2;

	boolean clustering = true;
	
	boolean paintDendrCols = true;
	boolean paintDendrRows = true;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	String info, infoRows, infoColumns;

	int PixelCount = 10;

	int Aggregation = 1;

	double[][] data;

	double [] Min, Max;

	GlobalView globalView;

	Color SelectionColor = Color.black;

	Vector<CoordinateNode> nodesR;

	Vector<CoordinateNode> nodesC;

	Color[][] cellColor;

	int upShift;
	
	

	boolean[][] isCellSelected;
	
	int [] IndexCols, IndexRows;
	
	public int Model;
	


	public GlobalViewAbstractPanel(Seurat seurat, GlobalView globalView, Vector Experiments, Vector Genes) {


		
		
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.globalView = globalView;
		//this.pixelSize = pixelSize;

		//this.pixelW = pixelSize;
		//this.pixelH = pixelSize;

		Model = seurat.settings.Model;
		
		
		clustering = false;

		this.Columns = Experiments;
		this.Rows = Genes;
		this.originalColumns = Experiments;
		this.originalRows = Genes;

		// this.originalOrderSpalten = orderSpalten;

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		
		this.addKeyListener(this);
		this.setFocusTraversalKeysEnabled(false );
		
		//if (globalView.clustering) calculateIndexes();
		
		// createRowsAndColumns();
	}

	
	public void calculateIndexes() {
		
		int max = 0;
		for (int i = 0; i < Columns.size(); i++) {
			if (max < Columns.elementAt(i).getID()) max = Columns.elementAt(i).getID();
		}
		
		IndexCols = new int [max+1];
		for (int i = 0; i < Columns.size(); i++) {
	       IndexCols [Columns.elementAt(i).getID()]  = i;		
		}
		
		
	    max = 0;
		for (int i = 0; i < Rows.size(); i++) {
			if (max < Rows.elementAt(i).getID()) max = Rows.elementAt(i).getID();
		}
		
		IndexRows = new int [max+1];
		for (int i = 0; i < Rows.size(); i++) {
	       IndexRows [Rows.elementAt(i).getID()] = i;		
		}
		
		
		
	}
	
	
	
	
	
	public void setInfo(String info) {
		this.info = info;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void mouseExited(MouseEvent e) {
	}

	public void updateSelection() {

		cellColor = new Color[this.Columns.size()][this.data[0].length];

		boolean selection = false;

		for (int i = 0; i < this.Columns.size(); i++) {
			if (this.Columns.elementAt(i).isSelected())
				selection = true;
		}
		for (int i = 0; i < this.Rows.size(); i++) {
			if (this.Rows.elementAt(i).isSelected())
				selection = true;
		}

		if (Model == 1) {

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < PixelCount; j++) {

					if (data[i][j] > 0) {

						double koeff = data[i][j] / Max[i];

						Color c = Color.getHSBColor(0, (float) fPos(koeff), 1);

						if (PixelCount == Rows.size()
								&& Columns.elementAt(i).getRealValue(
										Rows.elementAt(j).getID()) == dataManager.NA)
							c = Color.WHITE;

						if (selection) {
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Aggregation; k < (j + 1) * Aggregation; k++) {
							if (k < Rows.size()) {
								if (Columns.elementAt(i).isSelected() && Rows.elementAt(k).isSelected()) {
									selected = true;
								}

							}
						}

						if (selected) {
							c = c.brighter();
							c = c.brighter();
						}

						cellColor[i][j] = c;

					} else {
						double koeff = data[i][j] / Min[i];
						
						if (Min [i] == 0) koeff = 0;

						Color c = (Color.getHSBColor((float) 0.33,
								(float) fNeg(koeff), 1));

						if (selection) {
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Aggregation; k < (j + 1) * Aggregation; k++) {
							if (k < Rows.size()) {
								if (Columns.elementAt(i).isSelected() && Rows.elementAt(k).isSelected()) {
									selected = true;
								}
							}
						}

						if (selected) {
							c = c.brighter();
							c = c.brighter();
						}

						// c = Color.WHITE;
						cellColor[i][j] = c;
					}

				}

			}

		}

		if (Model == 2) {

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < PixelCount; j++) {

					if (data[i][j] > 0) {
						/*
						 * float koeff = (float)Math.pow(
						 * Experiments.elementAt(i).doubleData[j] /
						 * Experiments.elementAt(i).max,
						 * 
						 * dataManager.colorParam);
						 */
						double koeff = data[i][j] / Max[i];

						Color c = new Color((float) fPos(koeff), 0, 0);

						if (PixelCount == Rows.size()
								&& Columns.elementAt(i).getRealValue(
										Rows.elementAt(j).getID()) == dataManager.NA)
							c = Color.WHITE;

						if (selection) {
							c = c.darker();
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Aggregation; k < (j + 1) * Aggregation; k++) {
							if (k < Rows.size()) {
								if (Columns.elementAt(i).isSelected() && Rows.elementAt(k).isSelected()) {
									selected = true;
								}

							}
						}

						if (selected) {
							c = c.brighter();
							c = c.brighter();
							c = c.brighter();
							c = c.brighter();

							// c = new Color((float)
							// (Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt((float)fPos(koeff)))))),
							// 0, 0);
							// c = new Color( 255,211,211);

							if (PixelCount == Rows.size()
									&& Columns.elementAt(i).getRealValue(
											Rows.elementAt(j).getID()) == dataManager.NA)
								c = Color.WHITE;

						}

						cellColor[i][j] = c;
					} else {
						/*
						 * double koeff = Math.pow(
						 * Experiments.elementAt(i).doubleData[j] /
						 * Experiments.elementAt(i).min,
						 * dataManager.colorParam);
						 */
						// Color c = new Color(0,(float)(0.5*koeff),0);
						double koeff = data[i][j] / Min[i];

						Color c = new Color(0, (float) fNeg(koeff), 0);

						if (selection) {
							c = c.darker();
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Aggregation; k < (j + 1) * Aggregation; k++) {
							if (k < Rows.size()) {
								if (Columns.elementAt(i).isSelected() && Rows.elementAt(k).isSelected()) {
									selected = true;
								}

							}
						}

						if (selected) {
							c = c.brighter();
							c = c.brighter();
							c = c.brighter();
							c = c.brighter();
							// c = new Color(0,(float)
							// Math.sqrt(Math.sqrt((float)fNeg(koeff))), 0);

						}
						cellColor[i][j] = c;
					}

				}
			}
		}

		if (clustering) {
			if (nodeZeilen != null) updateClustering(nodeZeilen); 
			if (nodeSpalten != null) updateClustering(nodeSpalten);
			
		}
		
		
		this.repaint();
	}
	
	
	
	
	

	public void mouseReleased(MouseEvent e) {

		point2 = e.getPoint();
		
		
		if (point1 != null && point2 != null && (point1.getX() - point2.getX())*(point1.getY() - point2.getY())<0) {
			Point p = point1;
			point1 = point2;
			point2 = p;
			
			
		}
		
		
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
		     return;
		}

		if (point1 != null && point2 != null) {
			
			seurat.dataManager.deleteSelection();

			if (e.isShiftDown()) {
				selectRectangle(0, point1.y, this.getWidth(), point2.y);
				Vector cases = new Vector();
				for (int i = 0; i < Rows.size(); i++) {

					if (Rows.elementAt(i).isSelected()) {
						cases.add(Rows.elementAt(i));
					}

				}

				// dataManager.clearSelection();

				new ZoomView(seurat, "ZoomView", Columns, cases);

				// view.gPanel.abstandLinks = 9;
				// view.gPanel.abstandOben = 9;

			} else {

				if (Math.max(point1.x, point2.x) < this.abstandLinks
						&& Math.max(point1.y, point2.y) < this.abstandOben) {
					dataManager.deleteSelection();
					seurat.repaintWindows();
					point1 = null;
					point2 = null;
					return;
				} 
				
				if (Math.max(point1.x, point2.x) < this.abstandLinks
						|| Math.max(point1.y, point2.y) < this.abstandOben){
					
					
					
					
					if (point1.x == point2.x && point1.y == point2.y)	this.selectBranch(point1);
				    else {
					
					selectInTree(point1.x, point1.y, point2.x, point2.y); 
					point1 = null;
					point2 = null;
			        }
					
					
					seurat.repaintWindows();
					return;
			       
				}
				
				
				dataManager.deleteSelection();
				selectRectangle(point1.x, point1.y, point2.x, point2.y);
				if (clustering) selectTree(point1.x, point1.y, point2.x, point2.y);

				
				

			}

			point1 = null;
			point2 = null;
			seurat.repaintWindows();
		}

		this.repaint();
	}

	
	
	
	public void selectInTree(int xx1, int yy1, int xx2, int yy2) {
		dataManager.deleteSelection();

		boolean Spalten = false;
		
		
		for (int i = 0; i < this.nodesC.size(); i++) {
			CoordinateNode nd = nodesC.elementAt(i);
			for (int j = 0; j < nd.Lines.size(); j++) {
				Line line = nd.Lines.elementAt(j);
				if (containsLineInRect(line.x1+abstandLinks,line.y1,line.x2+abstandLinks,line.y2,xx1, yy1, xx2, yy2)) {
				
					nd.node.selectNode();
					Spalten = true;
				}
			}
		}
		
		
		boolean Zeilen = false;
		
		for (int i = 0; i < this.nodesR.size(); i++) {
			CoordinateNode nd = nodesR.elementAt(i);
			for (int j = 0; j < nd.Lines.size(); j++) {
				Line line = nd.Lines.elementAt(j);
				if (containsLineInRect(line.x1,line.y1,line.x2,line.y2,xx1, yy1, xx2, yy2)) {
				
					nd.node.selectNode();
					
				    Zeilen = true;
				}
			}
		}
		
		
		
		
		if (Zeilen && !Spalten) {
			for (int i = 0; i < Columns.size(); i++) {
				Columns.elementAt(i).select(true);
			}
		} 
		
		
		
		if (!Zeilen && Spalten) {
			for (int i = 0; i < Rows.size(); i++) {
				Rows.elementAt(i).select(true);
			}
		} 
		
		
		
		this.updateSelection();
		repaint();
		
	}
	
	
	
	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {
		
		/*
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelSize;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelSize;
		int y1 = Math.max(0, yy1 - upShift) * Width / this.pixelSize;
		int y2 = Math.max(0, yy2 - upShift) * Width / this.pixelSize;
		*/
		
		
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelW;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelW;
		int y1 = Math.max(0, yy1 - upShift) * Aggregation / this.pixelH;
		int y2 = Math.max(0, yy2 - upShift) * Aggregation / this.pixelH;
		
		
		
		
		/*
		if (y1 == y2)
			y2 += Math.max(0,Width-1);
		if (x1 == x2)
			x2 += 1;*/

		dataManager.deleteSelection();
	

		for (int i = 0; i < Columns.size(); i++) {
			for (int j = 0; j < Rows.size(); j++) {
				if (i <= x2 && i >= x1 && j <= y2 && j >= y1) {

					this.Columns.elementAt(i).select(true);
				
			    	this.Rows.elementAt(j).select(true);

				}

			}
		}

		this.repaint();
		
		
		
	
		
		
		
		

	}
	
	
	public void calculateTree() {
		this.nodesC = new Vector();
		this.nodesR = new Vector();
		
        

		System.out.println(">Calculation Rows Tree starts...");
		if (nodeZeilen != null) calculateClusteringRows(nodeZeilen);
		System.out.println(">Calculation Columns Tree starts...");
		if (nodeSpalten != null) calculateClusteringColumns(nodeSpalten);
	}
	
	
	

	public void selectTree(int xx1, int yy1, int xx2, int yy2) {
		/*
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelSize;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelSize;
		int y1 = Math.max(0, yy1 - upShift) * Width / this.pixelSize;
		int y2 = Math.max(0, yy2 - upShift) * Width / this.pixelSize;
		*/
		
		
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelW;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelW;
		int y1 = Math.max(0, yy1 - upShift) * Aggregation / this.pixelH;
		int y2 = Math.max(0, yy2 - upShift) * Aggregation / this.pixelH;
		
		
		if (y1 == y2)
			y2 += Aggregation;
		if (x1 == x2)
			x2 += 1;
		
	
		
		
		
		if (Math.max(yy1, yy2) < this.upShift
				|| Math.max(xx1, xx2) < this.abstandLinks) {

			for (int i = 0; i < Columns.size(); i++) {
				Columns.elementAt(i).unselect(true);

			}

			for (int i = 0; i < Rows.size(); i++) {
				Rows.elementAt(i).unselect(true);

			}

		}

		

		if (Math.max(yy1, yy2) < this.upShift) {
			for (int i = 0; i < Columns.size(); i++) {
					if (i < x2 && i >= x1) {

				this.Columns.elementAt(i).select(true);
			

					

				}
			}
			
			
			
		}

		if (Math.max(xx1, xx2) < this.abstandLinks) {
				for (int j = 0; j < Rows.size(); j++) {
					if (j < y2 && j >= y1) {

					this.Rows.elementAt(j).select(true);

					}

				
			}
				
				
				
		}

		this.repaint();

	}
	
	
	
	public boolean containsLineInRect(int lx1,int ly1,int lx2,int ly2,int rx1,int ry1,int rx2,int ry2) {
		
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
	
	
	

	public void selectBranch(Point p) {
		CoordinateNode node = null;
		int distance = 100000;
		dataManager.deleteSelection();
		
/*
		if (p.y < this.abstandOben) {

			for (int i = 0; i < this.nodesC.size(); i++) {
				CoordinateNode nd = nodesC.elementAt(i);
				int dist = nd.getDistance(p.x, p.y);

				if (distance > dist) {
					node = nd;
					distance = dist;
				}
			}

		}

		if (p.x < this.abstandLinks) {

			for (int i = 0; i < this.nodesR.size(); i++) {
				CoordinateNode nd = nodesR.elementAt(i);
				int dist = nd.getDistance(p.x, p.y);

				if (distance > dist) {
					node = nd;
					distance = dist;
				}
			}

		}

		selectNode(node);*/

	}

	public void selectNode(CoordinateNode nodeC) {
		ClusterNode node = nodeC.node;
		nodeC.isSelected = true;
		node.selectNode();
		

		

			
		    
		
		
	}

	public void calculateMatrixValues() {

		data = new double[Columns.size()][this.PixelCount];

		for (int i = 0; i < Columns.size(); i++) {
			for (int j = 0; j < PixelCount; j++) {
				int count = 0;
				for (int k = j * Aggregation; k < (j + 1) * Aggregation; k++) {
					if (k < Rows.size()) {
						data[i][j] += Columns.elementAt(i).getValue(Rows.elementAt(k).getID());
						count++;
					}
				}
				data[i][j] = data[i][j] / count;
			}
		}

		Max = new double[this.data.length];
		for (int i = 0; i < this.data.length; i++) {
			for (int j = 0; j < this.PixelCount; j++) {
				if (dataManager.NA != data[i][j] && Max[i] < data[i][j])
					Max[i] = data[i][j];
			}

		}

		Min = new double[this.data.length];
		for (int i = 0; i < this.data.length; i++) {
			for (int j = 0; j < this.PixelCount; j++) {
				if (Min[i] > data[i][j])
					Min[i] = data[i][j];
			}
		}

	}

	public void mouseClicked(MouseEvent e) {

		point1 = e.getPoint();
		
	    if (e.getClickCount() == 2) {
	    	dataManager.deleteSelection();
	        seurat.repaintWindows();
	    }
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			JPopupMenu menu = new JPopupMenu();
			
			
			JMenuItem item = new JMenuItem("Open Selection");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					Vector<ISelectable> subGenes = new Vector();
					Vector<ISelectable> subExps = new Vector();
					
					for (int i = 0; i < Rows.size(); i++) {
						if (Rows.elementAt(i).isSelected()) subGenes.add(Rows.elementAt(i));
					}
					
					
					for (int i = 0; i < Columns.size(); i++) {
						if (Columns.elementAt(i).isSelected()) subExps.add(Columns.elementAt(i));
					}
					
					
					
					
					GlobalView globalView = new GlobalView(seurat, "Global View", subExps, subGenes,  false);
                   globalView.applyNewPixelSize(pixelW,pixelH);
				}
			});
			menu.add(item);
			
			
			menu.addSeparator();
			
			
			
			
			if (Rows.elementAt(0) instanceof Gene && dataManager.geneVariables != null) {
				
				JMenu m = new JMenu("Sort Genes by");
				
				item = new JMenuItem("Original Order");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// createCorrelationGenes();

						Rows = originalRows;
						paintDendrRows = true;
						if (nodeZeilen != null) {
							abstandLinks = 150;
							globalView.setSize(globalView.getWidth() + 149, globalView.getHeight());
						}
						calculateMatrixValues();
						updateSelection();
					}
				});
				m.add(item);
				
				item = new JMenuItem("Chromosome Position");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// createCorrelationGenes();

						sortGenesByChrPosition();
					}
				});
				m.add(item);
				
				menu.add(m);

			}
			
			
			
			/*
			
			
			if (dataManager.descriptionVariables != null) {
				
				
	            JMenu m = new JMenu("Sort Samples by");
				
				item = new JMenuItem("Original Order");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// createCorrelationGenes();

						Columns = originalColumns;
						calculateMatrixValues();
						updateSelection();
					}
				});
				m.add(item);
				
				
				for (int i = 0; i < dataManager.descriptionVariables.size(); i++) {
				DescriptionVariable var = dataManager.descriptionVariables.elementAt(i);	
				
				item = new JMenuItem(""+var.getName());
				item.setActionCommand("i");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// createCorrelationGenes();
                        int i = Integer.parseInt(e.getActionCommand());
                        DescriptionVariable var = dataManager.descriptionVariables.elementAt(i);	
            			
						//sortColumnsByVar(var);
					}
				});
				m.add(item);
				
				}
				
				menu.add(m);
				
			}
			*/
			
			
			
			
		
			
			
			
			
			
			
			
			
			item = new JMenuItem("Clustering");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					new ClusteringDialog(seurat,Rows, Columns);
					
				}
			});
			menu.add(item);
			
			item = new JMenuItem("Seriation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					new SeriationDialog(seurat,Rows, Columns);
					
				}
			});
			menu.add(item);
			
			
			menu.addSeparator();
			

			item = new JMenuItem("Rows Correlation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();

					new CorrelationFrame(seurat, Rows, Columns, Aggregation,
							false, "Correlation Rows",pixelH);
				}
			});
			menu.add(item);
			
					

			item = new JMenuItem("Columns Correlation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

				new CorrelationFrame(seurat, Rows, Columns, 1, true,
							"Correlation Columns",pixelW);

				}
			});
			menu.add(item);
			
			menu.addSeparator();
			
			
			JCheckBoxMenuItem box = new JCheckBoxMenuItem("invert color spectrum");
			if (Model == 2) box.setSelected(true);
			else box.setSelected(false);
			menu.add(box);
			
			box.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBoxMenuItem box = (JCheckBoxMenuItem)e.getSource();
					if (box.isSelected()) {
						Model = 2;
					}
					else Model = 1;
						globalView.applyNewPixelSize();
				
				}
				
			});
			
			
			item = new JMenuItem("set pixel dimension");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

				    
				    ColorDialog dialog =  new ColorDialog(seurat, globalView.gPanel, pixelW, pixelH);
				    dialog.pixelWField.addKeyListener(globalView.gPanel);
				    dialog.pixelHField.addKeyListener(globalView.gPanel);
				    
				}
			});
			menu.add(item);
			
			
			
			item = new JMenuItem("set aggregation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

				    
				    AggregationDialog dialog =  new AggregationDialog(seurat, globalView.gPanel, Aggregation);
				    dialog.field.addKeyListener(globalView.gPanel);
				   
				    
				}
			});
			menu.add(item);
			
			
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
		}

	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		point2 = e.getPoint();

		if (e.isShiftDown()) {
			this.SelectionColor = Color.YELLOW;

		} else
			this.SelectionColor = Color.BLACK;

		this.repaint();

	}

	
	
	
	public boolean isPointInRect(int x, int y, int Rx1, int Ry1, int Rx2,
			int Ry2) {
		if ((Rx1 <= x) && (Rx2 >= x) && (Ry1 <= y) && (Ry2 >= y))
			return true;
		else
			return false;
	}
	
	
	
	@Override
	public String getToolTipText(MouseEvent e) {
		
		int colorsHight = abstandOben;
		
		if (e.getY() < upShift) {
			for (int i = 0; i < Columns.size(); i++) {
				ISelectable var = Columns.elementAt(i);
	            if (var.getColors() == null) break;
				
				for (int j = var.getColors().size() - 1; j >= 0; j--) {

					colorsHight = var.getColors().size() * (2 * this.pixelH + 1) + 4;

					//g.setColor(var.getColors().elementAt(j));

					if (isPointInRect(e.getX(), e.getY(),
							abstandLinks + i * this.pixelW, 
							2 + abstandOben + j * (2 * this.pixelH + 1), 
							abstandLinks + i * this.pixelW+Math.max(pixelW, 2),
							2 + abstandOben + j * (2 * this.pixelH + 1)+2 * pixelH + 1)
					){
						return var.getColorNames().elementAt(j);
					}
					
				
				}
			}
		}
		
		

		if (e.isControlDown()) {
			ISelectable exp = this.getExpAtPoint(e.getPoint());
			ISelectable gene = this.getGeneAtPoint(e.getPoint());

			if (exp != null && gene != null) {
			
				String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";
			
				s += "<FONT FACE = 'Arial'><TR><TD>" + exp.getName()
						+ "  </TD><TD> ";

				
				if (Aggregation == 1) s+= gene.getName()
				+ "  </TD><TD> "; 
				int x = Math.max(0, e.getPoint().x - abstandLinks)
						/ this.pixelW;
				int y = Math.max(0, e.getPoint().y - upShift)
						/ (this.pixelH);

				double valueD = 0;
				boolean isNA = true;

				for (int i = 0; i < Columns.size(); i++) {
					if (i == x) {

						for (int j = 0; j < Rows.size(); j++) {
							if (j == y) {

								if (Columns.elementAt(i).getRealValue(
										Rows.elementAt(j).getID()) != dataManager.NA) {
									valueD += Columns.elementAt(i)
											.getValue(Rows.elementAt(j).getID());
									isNA = false;
								}

							}
						}
					}
				}

				String value = "";

				if (isNA) {
					value = "NA";
				} else {

					value = valueD + "";
				}

				if (Aggregation != 1) {

					value = this.data[x][y] + "";
				}

				s += "<FONT FACE = 'Arial'><TR><TD>" + value + "  </TD><TD> ";

				s += "</P></FONT></STRONG>";

				return s;
			}

			if (e.getX() >= this.abstandLinks
					&& e.getX() <= this.abstandLinks + this.Columns.size()
							* this.pixelW && e.getY() <= this.upShift
					&& e.getY() >= this.abstandOben) {

				
				if (exp != null) {


					String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";

					String name = exp.getName();
		          
					
					s += "<FONT FACE = 'Arial'><TR><TD>"
							+ name + "<TR><TD>"
							+ "  </TD><TD> ";

					return s;

				}

			}

			return null;

		}
		return null;
	}

	public ISelectable getExpAtPoint(Point p) {
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());

		if (x < abstandLinks)
			return null;
		if (y < upShift)
			return null;

		x = (x - abstandLinks) / this.pixelW;
		y = (y - upShift) / this.pixelH;


		return Columns.elementAt(x);
	}

	public int getColumnOrig(int col) {
		int exp = -1;
		return Columns.elementAt(col).getID();
	}

	public ISelectable getExperimentAtIndex(int index) {

		return Columns.elementAt(index);
	}

	
	public void sortGenesByChrPosition() {
	
		
		Vector<Vector<Gene>> Chromosomes = new Vector();
   
		
		
		
		for (int i = 0; i < Rows.size(); i++) {
			Gene gene = (Gene)Rows.elementAt(i);
			String chr = "NA";
			if (gene.chrName != null) chr = gene.chrName;
			          
			double pos = gene.nucleotidePosition;
			
			Vector<Gene> genes = findChrInList(Chromosomes,chr);
			
			if (genes == null) {
				genes = new Vector();
				genes.add(gene);
				Chromosomes.add(genes);
			}else {
				
				insertGene(gene,genes);
			}
			
		}
		
		
		Chromosomes = sortChromosomes(Chromosomes);
		
		
		Rows = new Vector();
		for (int i = 0; i < Chromosomes.size(); i++){
			for (int j = 0; j < Chromosomes.elementAt(i).size(); j++) {
				Rows.add(Chromosomes.elementAt(i).elementAt(j));
			//	System.out.println(Chromosomes.elementAt(i).elementAt(j).getName());
			}
		}
		
		//System.out.println("New Row Size " + Rows.size());
		
		
		paintDendrRows = false;
		if (nodeZeilen != null) {
			abstandLinks = 1;
			globalView.setSize(globalView.getWidth()-149,globalView.getHeight());
		}
		
		calculateMatrixValues();
		updateSelection();
		
	}
	
	
	
	
	
	public static Vector<Vector<Gene>> sortChromosomes(Vector<Vector<Gene>> stringBuffer) {

		Vector<Vector<Gene>> newBuffer = new Vector();
		for (int i = 0; i < stringBuffer.size(); i++) {
			String s = "NA";
			if (stringBuffer.elementAt(i).elementAt(0).chrName != null) s = stringBuffer.elementAt(i).elementAt(0).chrName;
			
			
			int j = 0;
			while (j < newBuffer.size()
					&& compareLexico(s, newBuffer.elementAt(j).elementAt(0).chrName)) {
				j++;
			}
			newBuffer.insertElementAt(stringBuffer.elementAt(i), j);

		}

		return newBuffer;
		
	}

	public static boolean compareLexico(String a, String b) {
		int i = 0;

		String tA = a.replace("\"","");
		String tB = b.replace("\"","");
		if (tA.equals("X") || tA.equals("x")) tA = "23";
		if (tB.equals("X") || tB.equals("x")) tB = "23";

		if (tA.equals("Y") || tA.equals("y")) tA = "24";
		if (tB.equals("Y") || tB.equals("y")) tB = "24";
		
		if (tA.equals("NA") || tB.equals("NA")) return true;
		
		
		int aa = Integer.parseInt(tA);
		int bb = Integer.parseInt(tB);
		
		if (aa < bb) return false;
		return true;
	}
	
	
	
	
	
	
	
	
	/*
	 *  setzt Gene in eine Liste, so dass die Gene der Liste nach Nucleotideposition sortiert sind
	 * */
	public void insertGene(Gene gene, Vector<Gene> genes) {
		boolean insert = true;
	     	for (int i = 0; i < genes.size(); i++) {
	     		if (gene.nucleotidePosition <= genes.elementAt(i).nucleotidePosition) {
	     			genes.insertElementAt(gene,i);
	     			insert = false;
	     			break;
	     		}
	     	}
	     	if (insert) genes.add(gene);
	}
	
	
	
	
	public Vector<Gene> findChrInList(Vector<Vector<Gene>> Chromosomes,String s) {
		for (int i = 0; i < Chromosomes.size(); i++) {
			String name = "NA";
		
			if (Chromosomes.elementAt(i).elementAt(0).chrName != null) name = Chromosomes.elementAt(i).elementAt(0).chrName;
			if (name.equals(s)) return Chromosomes.elementAt(i);
		}
		return null;
	}
	
	
	
	
	/**
	 * 
	 *  Hierarchical Clustering mit meiner Distanzfunktion
	 * 
	 * */
	public void NewAlgorithm() {
		
		 Vector<ClusterNode> Nodes = new Vector();
         for (int i = 0; i < Columns.size(); i++) {
        	 Vector<ISelectable> cases = new Vector();
        	 cases.add(Columns.elementAt(i));
        	 ClusterNode nd = new ClusterNode(cases);
        	 Nodes.add(nd);
         }
         
         
         
        while (Nodes.size() != 1) {
        	
        	double [][] similarity = new double [Nodes.size()][Nodes.size()];
        	
        	for (int i = 0; i < similarity.length; i++) {
        		for (int j = 0; j < similarity.length; j++) {
        			if (i != j) {
        				Vector cases = new Vector();
        				for (int ii = 0; ii < Nodes.elementAt(i).Cases.size(); ii++) {
        					cases.add(Nodes.elementAt(i).Cases.elementAt(ii));
        				}
        				
        				for (int ii = 0; ii < Nodes.elementAt(j).Cases.size(); ii++) {
        					cases.add(Nodes.elementAt(j).Cases.elementAt(ii));
        				}
        				
        			
            		    similarity [i][j] = similarity(cases,Columns);
        			}
        		}
        	}
        	
        	
        	int ii  = 0, jj = 1;
        	double max = 0;
        	for (int i = 0; i < similarity.length; i++) {
        		for (int j = 0; j < similarity.length; j++) {
        		
        		     if (similarity [i][j] > max ) {
        		    	 max = similarity [i][j];
        		         ii = i;
        		         jj = j;
        		     }
        		}
        	}
        	
        	
            Nodes = union(Nodes,ii,jj, Columns.size() - max);	
     
        }
         
         nodeZeilen = Nodes.firstElement();
		
     	nodeZeilen.calculateHeight(Columns.size());
         
         
         
         GlobalView globalView = new GlobalView(seurat,
					"Clustering", nodeZeilen.getOrder(), Rows,true);

			globalView.gPanel.nodeSpalten = nodeZeilen;
			//globalView.gPanel.nodeZeilen = nodeZeilen;
			
			
			
			globalView.applyNewPixelSize(pixelW,pixelH);

			globalView.setLocation(350, 0);
         
			
			
			
			
			
		
	}
	
	
	
	
	
	
	public Vector<ClusterNode> union(Vector<ClusterNode> Nodes, int c1, int c2, double height) {
		Vector<ClusterNode> newNodes = new Vector();
		ClusterNode c1Node= Nodes.elementAt(c1);
		ClusterNode c2Node = Nodes.elementAt(c2);
		
		for (int i = 0;i < Nodes.size(); i++) {
			ClusterNode node = Nodes.elementAt(i);
			if (i != c1 && i != c2) {
				newNodes.add(node);
			}
		}
		
		
			
		ClusterNode newNode = new ClusterNode(-1,c1Node,c2Node);
		newNode.currentHeight = height;
		newNodes.add(newNode);
	
		
		
		return newNodes;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 *  Mein €hnlichkeitesmass
	 * 
	 * **/
	
	public double similarity(Vector<ISelectable> objects, Vector<ISelectable> vars) {
		double e = 0.3;
		double res = 0;
		 for (int j = 0; j < vars.size(); j++) {
		     boolean gleich = true;	
		     double value = objects.elementAt(0).getRealValue(vars.elementAt(j).getID());
		     
		     for (int i = 0; i < objects.size(); i++) {
	             if (Math.abs(objects.elementAt(i).getRealValue(vars.elementAt(j).getID()) - value) > e)
	             {
	                 gleich = false;	 
	             }
	        	 
	         }
		     
		     if (gleich) res++;
		     
		 }
	    
		 
		return res;
		
	}
	
	
	

	public ISelectable getGeneAtPoint(Point p) {
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());

		if (x < abstandLinks)
			return null;
		if (y < upShift)
			return null;

		x = (x - abstandLinks) / this.pixelW;
		y = (y - upShift) / this.pixelH;

		int gene = 0;

		
		return Rows.elementAt(y);
	}

	public int getRowIndexOrigin(int row) {

		return Rows.elementAt(row).getID();
	}

	



	@Override
	public void paint(Graphics g) {
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		
		
		
		upShift = abstandOben;
		

		int colorsHight = 0;

		for (int i = 0; i < Columns.size(); i++) {
			ISelectable var = Columns.elementAt(i);
            if (var.getColors() == null) break;
			
			for (int j = var.getColors().size() - 1; j >= 0; j--) {

				colorsHight = var.getColors().size() * (2 * this.pixelH + 1) + 4;

				g.setColor(var.getColors().elementAt(j));

				g.fillRect(abstandLinks + i * this.pixelW, 2 + abstandOben
						+ j * (2 * this.pixelH + 1), Math.max(pixelW, 2),
						2 * pixelH + 1);
			}
			
		}

		
		
		upShift = abstandOben + colorsHight;
		
		

		
		if (cellColor == null) this.updateSelection();
		
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < PixelCount; j++) {

					g.setColor(cellColor [i][j]);
					

					g.fillRect(abstandLinks + i * this.pixelW, upShift + j
							* this.pixelH, pixelW, pixelH);
				

			}
			}
		
			
			if (clustering && nodesR == null) {
				System.out.println(">Calculation Tree starts...");
				calculateTree();
				System.out.println("Tree calculated.");
				
			} 

			

			
			

		
		

			paintClustering(g);
			
		
		

		if (point1 != null && point2 != null) {
			g.setColor(SelectionColor);
			if (SelectionColor == Color.BLACK) {

				g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
						point2.y), Math.abs(point2.x - point1.x), Math
						.abs(point2.y - point1.y));
			} else {

				g.drawRect(this.abstandLinks - 1, Math.min(point1.y, point2.y),
						this.getWidth() - abstandLinks - 2, Math.abs(point2.y
								- point1.y));
				g.drawRect(this.abstandLinks - 1, 1 + Math.min(point1.y,
						point2.y), this.getWidth() - abstandLinks - 2, Math
						.abs(point2.y - point1.y));

			}
		}

	}

	
	
	/*
	public void paintClusteringRows(Graphics g, ClusterNode node, int Tiefe,
			int startAbstand) {

		if (node.isSelectedG())
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);

		if (node.nodeL != null && node.nodeR != null) {

			int maxTiefe = node.getTiefe();
			int pos = getYCoordinate(node.cases);
			int endPointX = startAbstand + this.abstandLinks
					/ (Tiefe + maxTiefe);

			CoordinateNode nodeC = new CoordinateNode(node, startAbstand, pos,
					endPointX, pos);
			nodesR.add(nodeC);

			g.drawLine(startAbstand, pos, endPointX, pos);
			if (node.nodeL != null) {

				if (node.nodeL.isSelectedG())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int pos1 = getYCoordinate(node.nodeL.cases);
				int pos2 = getYCoordinate(node.nodeR.cases);
				g.drawLine(endPointX, pos1, endPointX, pos);
				nodeC.Lines.add(new Line(endPointX, pos1, endPointX, pos));
				paintClusteringRows(g, node.nodeL, Tiefe + 1, endPointX);

			}

			if (node.nodeR != null) {

				if (node.nodeR.isSelectedG())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int pos1 = getYCoordinate(node.nodeL.cases);
				int pos2 = getYCoordinate(node.nodeR.cases);
				g.drawLine(endPointX, pos, endPointX, pos2);
				nodeC.Lines.add(new Line(endPointX, pos, endPointX, pos2));

				paintClusteringRows(g, node.nodeR, Tiefe + 1, endPointX);
			}

		} else {

			if (node.isSelectedG())
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);

			int pos = getYCoordinate(node.cases);
			// CoordinateNode nodeC = new CoordinateNode(node,startAbstand, pos,
			// this.abstandLinks, pos);
			// nodesR.add(nodeC);

			g.drawLine(startAbstand, pos, this.abstandLinks, pos);

		}

	}
	
	
	
	
	 */
	
	/*
	
	
	public void paintClusteringRows(Graphics g, ClusterNode node) {

		if (node.isSelectedG())
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);

		if (node.nodeL != null && node.nodeR != null) {

			int pos = getYCoordinate(node.cases);
			int x = (int)Math.round(this.abstandLinks - this.abstandLinks * node.currentHeight);

			
			
			
			
		
			
		    if (node.nodeL != null) {

				if (node.nodeL.isSelectedG())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int posL = getYCoordinate(node.nodeL.cases);
				int xL = (int)Math.round(this.abstandLinks - this.abstandLinks * node.nodeL.currentHeight);
				
				g.drawLine(x,pos,x,posL);
				g.drawLine(x, posL, xL, posL);
				
				
				CoordinateNode nodeC = new CoordinateNode(node.nodeL, x, posL, xL, posL);
				nodeC.Lines.add(new Line(x,pos,x,posL));
				this.nodesR.add(nodeC);
			
				
				paintClusteringRows(g, node.nodeL);

			}
		    

			if (node.nodeR != null) {

				if (node.nodeR.isSelectedG())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int posR = getYCoordinate(node.nodeR.cases);
				int xR = (int)Math.round(this.abstandLinks - this.abstandLinks * node.nodeR.currentHeight);
				
				g.drawLine(x,pos,x,posR);
				g.drawLine(x, posR, xR, posR);
				
				
				CoordinateNode nodeC = new CoordinateNode(node.nodeR, x, posR, xR, posR);
				nodeC.Lines.add(new Line(x,pos,x,posR));
				this.nodesR.add(nodeC);
				
				

				paintClusteringRows(g, node.nodeR);
			}

		} else {

			if (node.isSelectedG())
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);

			int pos = getYCoordinate(node.cases);
			// CoordinateNode nodeC = new CoordinateNode(node,startAbstand, pos,
			// this.abstandLinks, pos);
			// nodesR.add(nodeC);

			//g.drawLine((int)Math.round(this.abstandLinks - this.abstandLinks * node.currentHeight), pos, this.abstandLinks, pos);

		}

	}
	
	public void paintClusteringColumns(Graphics g, ClusterNode node) {

		if (node.isSelectedV())
			g.setColor(Color.RED);
		else
			
			g.setColor(Color.BLACK);

		if (node.nodeL != null && node.nodeR != null) {

			int pos = getXCoordinate(node.cases);
			int y = (int)Math.round(this.abstandOben - this.abstandOben * node.currentHeight);


			//CoordinateNode nodeC = new CoordinateNode(node, pos, startAbstand,
				//	pos, endPointY);
		//	nodesC.add(nodeC);

			if (node.nodeL != null) {
				if (node.nodeL.isSelectedV())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int posL = getXCoordinate(node.nodeL.cases);
				int yL = (int)Math.round(this.abstandOben - this.abstandOben * node.nodeL.currentHeight);
				
				g.drawLine(pos, y, posL, y);
				g.drawLine(posL, y, posL, yL);
				
				CoordinateNode nodeC = new CoordinateNode(node.nodeL, pos, y, posL, y);
				nodeC.Lines.add(new Line(pos, y, posL, y));
				this.nodesC.add(nodeC);
				
				
				
				paintClusteringColumns(g, node.nodeL);
			}

			if (node.nodeR != null) {

				if (node.nodeR.isSelectedV())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int posR = getXCoordinate(node.nodeR.cases);
				int yR = (int)Math.round(this.abstandOben - this.abstandOben * node.nodeR.currentHeight);
				
				
				CoordinateNode nodeC = new CoordinateNode(node.nodeR,pos, y, posR, y);
				nodeC.Lines.add(new Line(posR, y, posR, yR));
				this.nodesC.add(nodeC);
				
				
				g.drawLine(pos, y, posR, y);
				g.drawLine(posR, y, posR, yR);
				
				
				//nodeC.Lines.add(new Line(pos, endPointY, pos2, endPointY));

				paintClusteringColumns(g, node.nodeR);
			}

		} else {
			if (node.isSelectedV())
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);

			int pos = getXCoordinate(node.cases);
			//nodesC.add(new CoordinateNode(node, pos, startAbstand, pos,
				//	this.abstandOben));

			g.drawLine(pos, (int)Math.round(this.abstandOben - this.abstandOben * node.currentHeight), pos, this.abstandOben);

		}

	}

	
	*/
	
	
	
	
	

	
	public void paintClustering(Graphics g) {

		

		if (nodesR != null && paintDendrRows) {
			
			
			for (int i = 0; i < nodesR.size(); i++) {
				CoordinateNode node = nodesR.elementAt(i);
				
				if (node.isSelected)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);
				
				for (int j = 0; j < node.Lines.size(); j++) {
					Line line = node.Lines.elementAt(j);
					g.drawLine(line.x1,line.y1,line.x2,line.y2);
				}
				
				
			}

			
		    
		}
		
		
if (nodesC != null && paintDendrCols) {
			
	System.out.println("Paint Columns...");
			
			for (int i = 0; i < nodesC.size(); i++) {
				CoordinateNode node = nodesC.elementAt(i);
				
				if (node.isSelected)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);
				
				for (int j = 0; j < node.Lines.size(); j++) {
					Line line = node.Lines.elementAt(j);
					g.drawLine(abstandLinks+line.x1,line.y1,abstandLinks+line.x2,line.y2);
				}
				
				
			}

			
		    
		}




	}
	
	
	
	
	
	
	
	
	
	
	

	
	public void calculateClusteringRows(ClusterNode node) {

		

		if (node.nodeL != null && node.nodeR != null) {

			int pos = getYCoordinate(node.Cases);
			int x = (int)Math.round(this.abstandLinks - this.abstandLinks * node.currentHeight);

			
		    if (node.nodeL != null) {

	
				int posL = getYCoordinate(node.nodeL.Cases);
				int xL = (int)Math.round(this.abstandLinks - this.abstandLinks * node.nodeL.currentHeight);
						
				
				CoordinateNode nodeC = new CoordinateNode(node.nodeL, x, posL, xL, posL);
				nodeC.Lines.add(new Line(x,pos,x,posL));
				nodeC.Lines.add(new Line(x,posL,xL,posL));
				
				node.nodeL.cNode = nodeC;
				
				if (node.nodeL.isSelected()) nodeC.isSelected = true;
				
				this.nodesR.add(nodeC);
			
				
				calculateClusteringRows(node.nodeL);

			}
		    

			if (node.nodeR != null) {


				int posR = getYCoordinate(node.nodeR.Cases);
				int xR = (int)Math.round(this.abstandLinks - this.abstandLinks * node.nodeR.currentHeight);
				
				
				CoordinateNode nodeC = new CoordinateNode(node.nodeR, x, posR, xR, posR);
				nodeC.Lines.add(new Line(x,pos,x,posR));
				nodeC.Lines.add(new Line(x,posR,xR,posR));
				this.nodesR.add(nodeC);
				
				node.nodeR.cNode= nodeC;
				
				if (node.nodeR.isSelected()) nodeC.isSelected = true;

				calculateClusteringRows(node.nodeR);
			}

		} 

		

	}
	
	
	
	
	
	
	

	public void updateClustering(ClusterNode node) {

		

        if (node.isSelected()) {
            if (node.cNode != null) node.cNode.isSelected = true;	
        }
        else {
        	 if (node.cNode != null) node.cNode.isSelected = false;	
        }
		
		if (node.nodeL != null && node.nodeR != null) {

			
			
		    if (node.nodeL != null) {
		    	updateClustering(node.nodeL);

			}
		    

		    if (node.nodeR != null) {
		    	updateClustering(node.nodeR);

			}
		

		} 

		

	}
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	public void sortExperimentsByVar(DescriptionVariable var) {
	}
	
	
	
	
	
	
	
	
	
	public void calculateClusteringColumns(ClusterNode node) {

	

		if (node.nodeL != null && node.nodeR != null) {

			int pos = getXCoordinate(node.Cases)-abstandLinks;
			int y = (int)Math.round(this.abstandOben - this.abstandOben * node.currentHeight);


			if (node.nodeL != null) {
				

				int posL = getXCoordinate(node.nodeL.Cases)-abstandLinks;
				int yL = (int)Math.round(this.abstandOben - this.abstandOben * node.nodeL.currentHeight);
				
			
				CoordinateNode nodeC = new CoordinateNode(node.nodeL, pos, y, posL, y);
				nodeC.Lines.add(new Line(pos, y, posL, y));
				nodeC.Lines.add(new Line(posL, y, posL, yL));
				this.nodesC.add(nodeC);
				
				node.nodeL.cNode= nodeC;
				
				if (node.nodeL.isSelected()) nodeC.isSelected = true;
				
				calculateClusteringColumns(node.nodeL);
			}

			if (node.nodeR != null) {


				int posR = getXCoordinate(node.nodeR.Cases)-abstandLinks;
				int yR = (int)Math.round(this.abstandOben - this.abstandOben * node.nodeR.currentHeight);
				
				
			   CoordinateNode nodeC = new CoordinateNode(node.nodeR,pos, y, posR, y);
				nodeC.Lines.add(new Line(posR, y, posR, yR));
			//	nodeC.Lines.add(new Line(pos, y, posR, yR));
				this.nodesC.add(nodeC);
				
				node.nodeR.cNode = nodeC;
				
				if (node.nodeR.isSelected()) nodeC.isSelected = true;

				calculateClusteringColumns(node.nodeR);
			}

		} 
		

	}

	
	
	
	
	
	
	
	
	
	
	
	/*

	public void paintClusteringColumns(Graphics g, ClusterNode node, int Tiefe,
			int startAbstand) {

		if (node.isSelectedV())
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);

		if (node.nodeL != null && node.nodeR != null) {

			int maxTiefe = node.getTiefe();
			int pos = getXCoordinate(node.cases);
			int endPointY = startAbstand + this.abstandOben
					/ (Tiefe + maxTiefe);

			g.drawLine(pos, startAbstand, pos, endPointY);

			CoordinateNode nodeC = new CoordinateNode(node, pos, startAbstand,
					pos, endPointY);
			nodesC.add(nodeC);

			if (node.nodeL != null) {
				if (node.nodeL.isSelectedV())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int pos1 = getXCoordinate(node.nodeL.cases);
				g.drawLine(pos1, endPointY, pos, endPointY);
				nodeC.Lines.add(new Line(pos1, endPointY, pos, endPointY));
				paintClusteringColumns(g, node.nodeL, Tiefe + 1, endPointY);
			}

			if (node.nodeR != null) {

				if (node.nodeR.isSelectedV())
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				int pos2 = getXCoordinate(node.nodeR.cases);
				g.drawLine(pos, endPointY, pos2, endPointY);
				nodeC.Lines.add(new Line(pos, endPointY, pos2, endPointY));

				paintClusteringColumns(g, node.nodeR, Tiefe + 1, endPointY);
			}

		} else {
			if (node.isSelectedV())
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);

			int pos = getXCoordinate(node.cases);
			nodesC.add(new CoordinateNode(node, pos, startAbstand, pos,
					this.abstandOben));

			g.drawLine(pos, startAbstand, pos, this.abstandOben);

		}

	}*/

	public double fPos(double xx) {
		double a = seurat.settings.aPos;
		double b = seurat.settings.bPos;
		double value = 0;

		if (xx < seurat.settings.posMin)
			return 0;
		if (xx > seurat.settings.posMax)
			return 1;

		double x = (xx - seurat.settings.posMin)
				/ (seurat.settings.posMax - seurat.settings.posMin);

		if (x <= a) {
			value = a * Math.pow(x / a, b);
		} else {
			value = 1 - (1 - a) * Math.pow((1 - x) / (1 - a), b);
		}

		if (seurat.settings.invertShading)
			return 1 - value;
		else
			return value;

	}

	public double fNeg(double xx) {
		double a = seurat.settings.aNeg;
		double b = seurat.settings.bNeg;
		double value = 0;

		if (xx < seurat.settings.negMin)
			return 0;
		if (xx > seurat.settings.negMax)
			return 1;

		double x = (xx - seurat.settings.negMin)
				/ (seurat.settings.negMax - seurat.settings.negMin);

		if (x <= a) {
			value = a * Math.pow(x / a, b);
		} else {
			value = 1 - (1 - a) * Math.pow((1 - x) / (1 - a), b);
		}

		if (seurat.settings.invertShading)
			return 1 - value;
		else
			return value;

	}

	public int getYCoordinate(Vector<ISelectable> Cases) {
		int pos = 0;
		for (int i = 0; i < Cases.size(); i++) {
		//	pos += this.getIndexOfGeneInHeatMap(Cases.elementAt(i).getID());
			pos += this.IndexRows [Cases.elementAt(i).getID()];
		}
		int max = 	 upShift + (Rows.size() / this.Aggregation) * this.pixelH - this.pixelH/2;
		return Math.min(( upShift + pos * this.pixelH / (this.Aggregation * Cases.size()) + this.pixelH/2),max);
	}

	public int getXCoordinate(Vector<ISelectable> Cases) {
		int pos = 0;
		for (int i = 0; i < Cases.size(); i++) {
			//pos += getIndexOfExperimentInHeatMap(Cases.elementAt(i).getID())
				//	* this.pixelSize;
			
			System.out.println(Cases.elementAt(i));
			pos += IndexCols [Cases.elementAt(i).getID()]
			* this.pixelW;

		}

		return (this.abstandLinks + pos / Cases.size() + this.pixelW/2);
	}

	public int getIndexOfExperimentInHeatMap(int ID) {
		for (int i = 0; i < Columns.size(); i++) {
			if (Columns.elementAt(i).getID() == ID)
				return i;
		}
		return -1;
	}

	public int getIndexOfGeneInHeatMap(int ID) {
		for (int i = 0; i < Rows.size(); i++) {
			if (Rows.elementAt(i).getID() == ID)
				return i;
		}
		return -1;
	}

	public float[][] calculateCorrs(double[][] data) {

		float[][] corr = new float[data.length][data.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length; j++) {

				float m1 = 0, m2 = 0, s1 = 0, s2 = 0;
				float p = 0;

				for (int k = 0; k < data[i].length; k++) {
					if (data[i][k] != dataManager.NA)
						m1 += data[i][k];

					if (data[j][k] != dataManager.NA)
						m2 += data[j][k];
				}
				m1 /= data[i].length;
				m2 /= data[j].length;

				for (int k = 0; k < data[i].length; k++) {
					if (data[i][k] != dataManager.NA)
						s1 += (data[i][k] - m1) * (data[i][k] - m1);
					else
						s1 += m1 * m1;
					if (data[j][k] != dataManager.NA)
						s2 += (data[j][k] - m2) * (data[j][k] - m2);
					else
						s2 += m2 * m2;
				}

				s1 = (float) Math.sqrt(s1);
				s2 = (float) Math.sqrt(s2);

				double a, b;
				for (int k = 0; k < data[i].length; k++) {
					a = data[i][k];
					b = data[j][k];
					if (a == dataManager.NA)
						a = 0;
					if (b == dataManager.NA)
						b = 0;

					p += (a - m1) * (b - m2);
				}

				corr[i][j] = p / s1 / s2;

			}
		}

		float[][] correlations = new float[this.PixelCount][this.PixelCount];
		for (int i = 0; i < correlations.length; i++) {
			for (int j = 0; j < correlations.length; j++) {
				int count = 0;
				for (int ii = i * this.Aggregation; ii < Math.min((i + 1)
						* this.Aggregation, Rows.size()); ii++) {
					for (int jj = j * this.Aggregation; jj < Math.min((j + 1)
							* this.Aggregation, Rows.size()); jj++) {
						correlations[i][j] += corr[ii][jj];
						count++;
					}
				}
				correlations[i][j] /= count;

			}

		}
		return correlations;
	}

	public float[][] calculateCorrelations(double[][] data) {
		float[][] corr = new float[data.length][data.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length; j++) {

				float m1 = 0, m2 = 0, s1 = 0, s2 = 0;
				float p = 0;

				for (int k = 0; k < data[i].length; k++) {
					if (data[i][k] != dataManager.NA)
						m1 += data[i][k];

					if (data[j][k] != dataManager.NA)
						m2 += data[j][k];
				}
				m1 /= data[i].length;
				m2 /= data[j].length;

				for (int k = 0; k < data[i].length; k++) {
					if (data[i][k] != dataManager.NA)
						s1 += (data[i][k] - m1) * (data[i][k] - m1);
					else
						s1 += m1 * m1;
					if (data[j][k] != dataManager.NA)
						s2 += (data[j][k] - m2) * (data[j][k] - m2);
					else
						s2 += m2 * m2;
				}

				s1 = (float) Math.sqrt(s1);
				s2 = (float) Math.sqrt(s2);

				double a, b;
				for (int k = 0; k < data[i].length; k++) {
					a = data[i][k];
					b = data[j][k];
					if (a == dataManager.NA)
						a = 0;
					if (b == dataManager.NA)
						b = 0;

					p += (a - m1) * (b - m2);
				}

				corr[i][j] = p / s1 / s2;

			}
		}
		return corr;
	}

	
	   public void print() {
			  
		   try {
		   PrintJob prjob = getToolkit().getPrintJob( globalView,null, null );
		   Graphics pg = prjob.getGraphics();
		   paint(pg);
		   pg.dispose();
		   prjob.end();
		   }
		   catch (Exception e) {
			   e.printStackTrace();
		   } 
	   }
	
	
	
	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}


	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		
		int panelH = this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * this.pixelH + 2);
		
		if (arg0.getKeyCode() == 38) {
			
			if (Aggregation < this.Rows.size()) Aggregation++;
			
			
			this.PixelCount = this.Rows.size()/Aggregation;
		//	this.setPreferredSize(new Dimension(abstandLinks + this.Columns.size()*pixelW,abstandOben + panelH+PixelCount*pixelH));
			this.calculateMatrixValues();
			
			
		    globalView.infoLabel.setText("Aggregation: 1 : " + Aggregation);
		    globalView.applyNewPixelSize();
			
			
			
		}
		
		
        if (arg0.getKeyCode() == 40) {
        	
        	
        	if (Aggregation > 1) Aggregation--;
        	
			this.PixelCount = this.Rows.size()/Aggregation;
	//		this.setPreferredSize(new Dimension(abstandLinks + this.Columns.size()*pixelW,abstandOben + panelH+PixelCount*pixelH));
			
			this.calculateMatrixValues();
			
			globalView.infoLabel.setText("Aggregation: 1 : " + Aggregation);
			globalView.applyNewPixelSize();
		}
        
        
        if (arg0.getKeyCode() == 39) {
            
           pixelW++;
        //   this.setPreferredSize(new Dimension(abstandLinks + this.Columns.size()*pixelW,abstandOben + panelH+PixelCount*pixelH));
			
     //      this.setPreferredSize(new Dimension(abstandLinks + this.Columns.size()*pixelW,abstandOben + panelH+PixelCount*pixelH));
			
           globalView.applyNewPixelSize();
           
        //   this.repaint();
        }	
        
        
        if (arg0.getKeyCode() == 37) {
            
           if (pixelW>1) pixelW--;
         //  this.setPreferredSize(new Dimension(abstandLinks + this.Columns.size()*pixelW,abstandOben + panelH+PixelCount*pixelH));
			
           globalView.applyNewPixelSize();
          // this.repaint();
            }
        	
        	
        
		
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println(arg0.getKeyCode());
	}


	public void applyColorValues(int pos, int pos2, int neg, int neg2) {
		// TODO Auto-generated method stub
		
	}


	public void applyNewPixelSize(int pixelW, int pixelH) {
		// TODO Auto-generated method stub
		this.pixelW = pixelW;
		this.pixelH = pixelH;
	}


	public void inverColors(boolean invert) {
		// TODO Auto-generated method stub
		
	}


	public void setModel(int model) {
		// TODO Auto-generated method stub
		Model = model;
		
	}


	public void applyNewPixelSize() {
		// TODO Auto-generated method stub
		globalView.applyNewPixelSize();
	}


	public void setAggregation(int aggr) {
		// TODO Auto-generated method stub
		this.Aggregation = aggr;
		applyNewPixelSize();
	}

	

}