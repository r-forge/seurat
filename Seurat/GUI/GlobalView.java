package GUI;

import java.util.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.ClusterNode;
import Data.CoordinateNode;
import Data.DescriptionVariable;
import Data.*;
import Data.Variable; //import org.rosuda.JRclient.*;

class GlobalView extends JFrame implements MatrixWindow, IPlot {
	int pixelSize = 2;

	Seurat seurat;

	GlobalViewPanel gPanel;

	// int[] orderZeilen;

	// int[] orderSpalten;

	JMenuItem item = new JMenuItem("Clustering");

	GlobalView globalView = this;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	DataManager dataManager;

	Vector<Variable> Experiments;
	Vector<Gene> Genes;
	
	JLabel infoLabel;
	
	boolean resize = true;
	
	
	JPanel infoPanel;

	int oldPixelCount;

	public void setInfo(String info) {
		this.gPanel.setInfo(info);
	}
	
	
	
	
	public void applyNewPixelSize(int size) {
		this.pixelSize = size;
		this.gPanel.pixelSize = size;
		int col = Experiments.size();

		int row = Genes.size();
		
		
		int panelW = gPanel.abstandLinks + col* pixelSize;
		int panelH = gPanel.upShift+ row* pixelSize / gPanel.Width 
		+ this.dataManager.Experiments.elementAt(0).barchartsToColors
		.size() * (2 * this.pixelSize + 2);
		

		gPanel.setPreferredSize(new Dimension(panelW, panelH));
		
		
		
		int colorsHight = 0;

		for (int i = 0; i < Experiments.size(); i++) {
			Variable var = Experiments.elementAt(i);

			for (int j = var.colors.size() - 1; j >= 0; j--) {

				colorsHight = var.colors.size() * (2 * this.pixelSize + 1) + 4;

			
			}
		}

		
		int upShift = gPanel.abstandOben + colorsHight;
		
		infoLabel.setText("Aggregation: 1 : " + gPanel.Width);
	
		
		resize = false;

		
		
		if (seurat.SYSTEM == seurat.WINDOWS) {

			this.setSize(
					panelW + 5+14,
					panelH+ 26+15
					+ infoPanel.getHeight()
					- this.dataManager.Experiments.elementAt(0).barchartsToColors
							.size() * (2 * this.pixelSize + 2)
							);

		} else
			this.setSize(
							panelW + 5,
							panelH+ 26
							+ infoPanel.getHeight()
							- this.dataManager.Experiments.elementAt(0).barchartsToColors
							.size() * (2 * this.pixelSize + 2)
									);
		
		
	

		updateSelection();
		
		
	}
	
	
	
	
	
	
	/*

	public void applyNewPixelSize(int size) {
		this.pixelSize = size;
		this.gPanel.pixelSize = size;
		int col = Experiments.size();

		int row = Genes.size();

		gPanel.setPreferredSize(new Dimension(gPanel.abstandLinks + col
				* pixelSize, gPanel.upShift
				+ row
				* pixelSize
				/ gPanel.Width
				+ this.dataManager.Experiments.elementAt(0).barchartsToColors
						.size() * (2 * this.pixelSize + 2)));
		
		
		
		int colorsHight = 0;

		for (int i = 0; i < Experiments.size(); i++) {
			Variable var = Experiments.elementAt(i);

			for (int j = var.colors.size() - 1; j >= 0; j--) {

				colorsHight = var.colors.size() * (2 * this.pixelSize + 1) + 4;

			
			}
		}

		
		int upShift = gPanel.abstandOben + colorsHight;
		
		
		
		

		if (seurat.SYSTEM == seurat.WINDOWS) {

			this
					.setSize(
							col * pixelSize + gPanel.abstandLinks + 17,
							upShift
									+ row
									* pixelSize
									/ gPanel.Width
									+ 38
									);

		} else
			this
					.setSize(
							col * pixelSize + gPanel.abstandLinks + 2,
							upShift
									+ row
									* pixelSize
									/ gPanel.Width
									+ 23
									);

		updateSelection();

	}
	
	
	*/
	
	
	
	

	public GlobalView(Seurat seurat, String name, Vector<Variable> Experiments,
			Vector<Gene> Genes, boolean clustering) {
		super(name);

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.Experiments = Experiments;
		this.Genes = Genes;
		this.pixelSize = seurat.settings.PixelSize;
		this.getContentPane().setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEtchedBorder());
		p.setLayout(new BorderLayout());
			
		
		
		GlobalViewPanel panel = new GlobalViewPanel(seurat, this, pixelSize,
				Experiments, Genes);

		
		p.add(panel,BorderLayout.CENTER);
		
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		infoPanel = new JPanel();
		if (Experiments.size() < 116) infoPanel.setPreferredSize(new Dimension(300,45));
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		infoPanel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(infoPanel,BorderLayout.SOUTH);
		
		
		
		this.addMouseListener(panel);

		int count = globalView.dataManager.RowCount;
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
		while ((count / Width) * this.pixelSize > 700) {
			Width++;
		}

		panel.PixelCount = count / Width;
		if (count % Width > 0)
			panel.PixelCount++;
		panel.Width = Width;

		int col = Experiments.size();

		int row = Genes.size();

		oldPixelCount = panel.PixelCount;

		panel.setPreferredSize(new Dimension(panel.abstandLinks + col
				* pixelSize, panel.abstandOben 
				+ row
				* pixelSize
				/ panel.Width
				+ this.dataManager.Experiments.elementAt(0).barchartsToColors
						.size() * (2 * this.pixelSize + 2)));
		gPanel = panel;

		if (clustering) {
			gPanel.clustering = true;
			panel.abstandLinks = 150;
			panel.abstandOben = 150;

		}
		
		
this.getContentPane().add(p, BorderLayout.CENTER);
		
		
		
		
		
		infoLabel = new JLabel("Aggregation: 1 : " + gPanel.Width);
		Font myFont = new Font("SansSerif", 0, 10);

		
		JLabel label = new JLabel("Columns: " + Experiments.size());
		label.setFont(myFont);
		infoPanel.add(label);
		
		
		
		label = new JLabel(" Rows: "+Genes.size()+"  ");
		label.setFont(myFont);
		infoPanel.add(label);
		
		
		
		
		infoLabel.setFont(myFont);
		infoPanel.add(infoLabel);
		
		
		
		
		
		
		

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
									+ this.dataManager.Experiments.elementAt(0).barchartsToColors
											.size() * (2 * this.pixelSize + 2));
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
									+ this.dataManager.Experiments.elementAt(0).barchartsToColors
											.size() * (2 * this.pixelSize + 2));

		/*
		 * if (clustering) { gPanel.clustering = true; panel.abstandLinks = 50;
		 * panel.abstandOben = 40; panel.setPreferredSize(new
		 * Dimension(panel.abstandLinks + col pixelSize, panel.abstandOben + row *
		 * pixelSize / panel.Width+
		 * this.dataManager.Experiments.elementAt(0).barchartsToColors.size() *
		 * (2 * this.pixelSize + 2))); this.setBounds(350, 0, col * pixelSize +
		 * 17 + panel.abstandLinks, panel.abstandOben + row * pixelSize /
		 * panel.Width + 37+
		 * this.dataManager.Experiments.elementAt(0).barchartsToColors.size() *
		 * (2 * this.pixelSize + 2)); }
		 */

		// this.getContentPane().setLayout(new BorderLayout());
		
		
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

				
				//if (resize) {
				
				
					
				
				int count = globalView.Genes.size();

				gPanel.PixelCount = Math
						.min(
								(gPanel.getHeight()
										- globalView.gPanel.abstandOben - globalView.Experiments
										.elementAt(0).barchartsToColors.size()
										* (2 * globalView.pixelSize + 2))
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
				
				

				//}
			//	else resize = true;
				
				
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

}

class GlobalViewPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener {
	DataManager dataManager;

	Seurat seurat;

	int pixelSize = 1;

	int abstandLinks = 1;

	int abstandOben = 1;

	Vector<Gene> Genes;

	Vector<Variable> Experiments;

	int[] originalOrderSpalten;

	ClusterNode nodeZeilen;

	ClusterNode nodeSpalten;

	Point point1, point2;

	boolean clustering = true;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	String info, infoRows, infoColumns;

	int PixelCount = 10;

	int Width = 1;

	double[][] data;

	double[] Min, Max;

	GlobalView globalView;

	Color SelectionColor = Color.black;

	Vector<CoordinateNode> nodesR;

	Vector<CoordinateNode> nodesC;

	Color[][] cellColor;

	int upShift;

	boolean[][] isCellSelected;

	/*
	 * 
	 * 
	 * public GlobalViewPanel(Seurat seurat, GlobalView globalView, int
	 * pixelSize, String methodColumns, String distanceColumns, String
	 * methodRows, String distanceRows) {
	 * 
	 * this.seurat = seurat; this.dataManager = seurat.dataManager;
	 * this.globalView = globalView; this.pixelSize = pixelSize;
	 * 
	 * this.methodColumns = methodColumns; this.distanceColumns =
	 * distanceColumns; this.methodRows = methodRows; this.distanceRows =
	 * distanceRows;
	 * 
	 * calculateClustersZeilen(methodRows, distanceRows);
	 * calculateClustersSpalten(methodColumns, distanceColumns);
	 *  // this.originalOrderSpalten = orderSpalten;
	 * 
	 * ToolTipManager.sharedInstance().registerComponent(this);
	 * ToolTipManager.sharedInstance().setInitialDelay(0);
	 * ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
	 * ToolTipManager.sharedInstance().setReshowDelay(0);
	 * 
	 * this.addMouseListener(this); this.addMouseMotionListener(this);
	 * 
	 * info = this.createInfo(); // createRowsAndColumns();
	 *  }
	 */

	public GlobalViewPanel(Seurat seurat, GlobalView globalView, int pixelSize,
			Vector<Variable> Experiments, Vector<Gene> Genes) {

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.globalView = globalView;
		this.pixelSize = pixelSize;

		clustering = false;

		this.Experiments = Experiments;
		this.Genes = Genes;

		// this.originalOrderSpalten = orderSpalten;

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.createInfo();
		// createRowsAndColumns();
	}

	/*
	 * public void createRowsAndColumns() {
	 * 
	 * this.Rows = new Vector(); int[] order = new int[orderZeilen.length]; for
	 * (int i = 0; i < orderZeilen.length; i++) { order[orderZeilen[i]] = i; }
	 * 
	 * for (int i = 0; i < order.length; i++) { Rows.add(order[i]); }
	 * 
	 * 
	 * 
	 * this.Experiments = new Vector(); order = new int[orderSpalten.length];
	 * for (int i = 0; i < orderSpalten.length; i++) { order[orderSpalten[i]] =
	 * i; }
	 * 
	 * for (int i = 0; i < order.length; i++) {
	 * Experiments.add(dataManager.Experiments.elementAt(order[i])); }
	 *  }
	 */

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

		cellColor = new Color[this.Experiments.size()][this.data[0].length];

		boolean selection = false;

		for (int i = 0; i < this.Experiments.size(); i++) {
			if (this.Experiments.elementAt(i).isSelected())
				selection = true;
		}
		for (int i = 0; i < this.Genes.size(); i++) {
			if (this.Genes.elementAt(i).isSelected())
				selection = true;
		}

	//	System.out.println("something selected " + selection);
		
		if (seurat.settings.Model == 1) {

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < PixelCount; j++) {

					if (data[i][j] > 0) {

						double koeff = data[i][j] / Max[i];

						Color c = Color.getHSBColor(0, (float) fPos(koeff), 1);

						if (PixelCount == dataManager.RowCount
								&& Experiments.elementAt(i).getRealValue(
										Genes.elementAt(j).ID) == dataManager.NA)
							c = Color.WHITE;

						if (selection) {
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Width; k < (j + 1) * Width; k++) {
							if (k < dataManager.RowCount) {
								
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) selected = true;
								
								
								//if (dataManager.Experiments.elementAt(this
								//		.getColumnOrig(i)).isSelected[this
								//		.getRowIndexOrigin(k)]) {
								//	selected = true;
								//}

							}
						}

						if (selected) {
							c = c.brighter();
							c = c.brighter();
						}

						cellColor[i][j] = c;

					} else {
						double koeff = Math.pow(data[i][j] / Min[i], 1);

						Color c = (Color.getHSBColor((float) 0.33,
								(float) fNeg(koeff), 1));

						if (selection) {
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Width; k < (j + 1) * Width; k++) {
							
							
							
							
							if (k < dataManager.RowCount) {
								
								
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) selected = true;
								
							//	if (dataManager.Experiments.elementAt(this
							//			.getColumnOrig(i)).isSelected[this
							//			.getRowIndexOrigin(k)]) {
							//		selected = true;
							//	}

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

		if (seurat.settings.Model == 2) {

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

						if (PixelCount == dataManager.RowCount
								&& Experiments.elementAt(i).getRealValue(
										Genes.elementAt(j).ID) == dataManager.NA)
							c = Color.WHITE;

						if (selection) {
							c = c.darker();
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Width; k < (j + 1) * Width; k++) {
							if (k < dataManager.RowCount) {
							
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) selected = true;
								
								
								
								//if (dataManager.Experiments.elementAt(this
								//		.getColumnOrig(i)).isSelected[this
								//		.getRowIndexOrigin(k)]) {
								//	selected = true;
								//}

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

							if (PixelCount == dataManager.RowCount
									&& Experiments.elementAt(i).getRealValue(
											Genes.elementAt(j).ID) == dataManager.NA)
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

						for (int k = j * Width; k < (j + 1) * Width; k++) {
							if (k < dataManager.RowCount) {
								
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) selected = true;
								
								
								
								//if (dataManager.Experiments.elementAt(this
								//		.getColumnOrig(i)).isSelected[this
								//		.getRowIndexOrigin(k)]) {
								//	selected = true;
								//}

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

		this.repaint();
	}

	public void mouseReleased(MouseEvent e) {

		point2 = e.getPoint();
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
		     return;
		}

		if (point1 != null && point2 != null) {

			if (e.isShiftDown()) {
				selectRectangle(0, point1.y, this.getWidth(), point2.y);
				Vector cases = new Vector();
				for (int i = 0; i < Genes.size(); i++) {

					if (Genes.elementAt(i).selected) {
						cases.add(Genes.elementAt(i));
					}

				}

				// dataManager.clearSelection();

				new ZoomView(seurat, "ZoomView", Experiments, cases);

				// view.gPanel.abstandLinks = 9;
				// view.gPanel.abstandOben = 9;

			} else {

				if (Math.max(point1.x, point2.x) < this.abstandLinks
						&& Math.max(point1.y, point2.y) < this.abstandOben) {
					dataManager.clearSelection();
				} else {
					if (!clustering) selectRectangle(point1.x, point1.y, point2.x, point2.y);
					if (clustering) {
						dataManager.deleteSelection();
						if (point1.x == point2.x && point1.y == point2.y) {
							if (point1.x < abstandLinks
									|| point2.y < abstandOben) {
       
								this.selectBranch(point1);
							} else {
						
								selectRectangle(point1.x, point1.y,
										point2.x + 1, point2.y + 1);

							}
						}

						else {
							
							selectRectangle(point1.x, point1.y, point2.x, point2.y);
							selectTree(point1.x, point1.y, point2.x, point2.y);

						}
					}
				}

			}

			point1 = null;
			point2 = null;
			seurat.repaintWindows();
		}

		this.repaint();
	}

	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelSize;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelSize;
		int y1 = Math.max(0, yy1 - upShift) * Width / this.pixelSize;
		int y2 = Math.max(0, yy2 - upShift) * Width / this.pixelSize;
		
		/*
		if (y1 == y2)
			y2 += Math.max(0,Width-1);
		if (x1 == x2)
			x2 += 1;*/

		for (int i = 0; i < Experiments.size(); i++) {
			Experiments.elementAt(i).unselect(true);

		//	for (int j = 0; j < dataManager.Genes.size(); j++) {

//				this.dataManager.Experiments.elementAt(i).isSelected[j] = false;
	//		}

		}

		for (int i = 0; i < Genes.size(); i++) {
			Genes.elementAt(i).unselect(true);

		}
		/*
		 * for (int i = 0; i < dataManager.Experiments.size(); i++) { for (int j =
		 * 0; j < globalView.dataManager.RowCount; j++) { if
		 * (this.orderSpalten[i] < x2 && this.orderSpalten[i] >= x1 &&
		 * this.orderZeilen[j] < y2 && this.orderZeilen[j] >= y1) {
		 * 
		 * this.dataManager.Experiments.elementAt(i).isSelected[j] = true;
		 * dataManager.selectedVariables[i] = true; dataManager.selectedRows[j] =
		 * true; } else this.dataManager.Experiments.elementAt(i).isSelected[j] =
		 * false; } }
		 */

		for (int i = 0; i < Experiments.size(); i++) {
			for (int j = 0; j < Genes.size(); j++) {
				if (i <= x2 && i >= x1 && j <= y2 && j >= y1) {

					//this.Experiments.elementAt(i).isSelected[Genes.elementAt(j).ID] = true;
					this.Experiments.elementAt(i).select(true);
					this.Genes.elementAt(j).select(true);

				}

			}
		}

		this.repaint();

	}

	public void selectTree(int xx1, int yy1, int xx2, int yy2) {
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelSize;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelSize;
		int y1 = Math.max(0, yy1 - upShift) * Width / this.pixelSize;
		int y2 = Math.max(0, yy2 - upShift) * Width / this.pixelSize;
		if (y1 == y2)
			y2 += Width;
		if (x1 == x2)
			x2 += 1;

		if (Math.max(yy1, yy2) < this.upShift
				|| Math.max(xx1, xx2) < this.abstandLinks) {

			for (int i = 0; i < dataManager.Experiments.size(); i++) {
				dataManager.Experiments.elementAt(i).selected = false;

			//	for (int j = 0; j < dataManager.Genes.size(); j++) {

				//	this.dataManager.Experiments.elementAt(i).isSelected[j] = false;
				//}

			}

			for (int i = 0; i < dataManager.Genes.size(); i++) {
				dataManager.Genes.elementAt(i).selected = false;

			}

		}

		

		if (Math.max(yy1, yy2) < this.upShift) {
			for (int i = 0; i < Experiments.size(); i++) {
				for (int j = 0; j < Genes.size(); j++) {
					if (i < x2 && i >= x1) {

						//this.Experiments.elementAt(i).isSelected[Genes
						//		.elementAt(j).ID] = true;
						this.Experiments.elementAt(i).selected = true;
						this.Genes.elementAt(j).selected = true;

					}

				}
			}
		}

		if (Math.max(xx1, xx2) < this.abstandLinks) {
			for (int i = 0; i < Experiments.size(); i++) {
				for (int j = 0; j < Genes.size(); j++) {
					if (j < y2 && j >= y1) {

					//	this.Experiments.elementAt(i).isSelected[Genes
						//		.elementAt(j).ID] = true;
						this.Experiments.elementAt(i).selected = true;
						this.Genes.elementAt(j).selected = true;

					}

				}
			}
		}

		this.repaint();

	}

	public void selectBranch(Point p) {
		CoordinateNode node = null;
		int distance = 100000;

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

		selectNode(node);

	}

	public void selectNode(CoordinateNode nodeC) {
		ClusterNode node = nodeC.node;
		node.selectNode();
	}

	public void calculateMatrixValues() {

		data = new double[this.dataManager.Experiments.size()][this.PixelCount];

		for (int i = 0; i < this.dataManager.Experiments.size(); i++) {
			for (int j = 0; j < PixelCount; j++) {
				int count = 0;
				for (int k = j * Width; k < (j + 1) * Width; k++) {
					if (k < dataManager.RowCount) {
						data[i][j] += dataManager.Experiments.elementAt(
								this.getColumnOrig(i)).getValue(
								this.getRowIndexOrigin(k));
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
	    	dataManager.clearSelection();
	        seurat.repaintWindows();
	    }
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			JPopupMenu menu = new JPopupMenu();
			JMenuItem item = new JMenuItem("Rows Correlation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();

					new CorrelationFrame(seurat, Genes, Experiments, Width,
							false, "Correlation Rows");
				}
			});
			menu.add(item);

			item = new JMenuItem("Columns Correlation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					new CorrelationFrame(seurat, Genes, Experiments, 1, true,
							"Correlation Columns");

				}
			});
			menu.add(item);

/*			JMenu menuSort = new JMenu("Sort Variables by");

			if (seurat.descriptionFrame != null) {

				item = new JMenuItem("Original");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						// orderSpalten = originalOrderSpalten;
						repaint();

					}
				});
				menuSort.add(item);

				for (int i = 0; i < seurat.descriptionFrame.descriptionVariables
						.size(); i++) {
					DescriptionVariable tempVar = seurat.descriptionFrame.descriptionVariables
							.elementAt(i);
					item = new JMenuItem(tempVar.name);
					item.setActionCommand("" + i);

					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

							int i = Integer.parseInt(e.getActionCommand());
							DescriptionVariable tempVar = seurat.descriptionFrame.descriptionVariables
									.elementAt(i);

							if (nodeSpalten == null) {

								if (!tempVar.isDiscrete) {

									// orderSpalten = SortManager
									// .sort(tempVar.doubleData);

								} else {
									// orderSpalten = SortManager
									// .sort(tempVar.stringData);

								}
							} else {
								sortClusters(tempVar);
							}
							repaint();
						}
					});

					menuSort.add(item);
				}

			}

			menu.add(menuSort);

			item = new JMenuItem("Info");
			item.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JFrame infoFrame = new JFrame("Info") {

					};
					infoFrame.setBounds(400, 400, 200, 450);
					infoFrame.add(new JScrollPane(new JTextArea(info)));
					infoFrame.setVisible(true);

				}

			});
			menu.add(item);
*/
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

	@Override
	public String getToolTipText(MouseEvent e) {

		if (e.isControlDown()) {
			int exp = this.getExpAtPoint(e.getPoint());
			int gene = this.getGeneAtPoint(e.getPoint());

			if (exp != -1 && gene != -1) {
				Variable var = dataManager.Experiments.elementAt(exp);

				String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";
				// s += "<FONT FACE = 'Arial'><TR><TD>"+var.doubleData []+ "
				// </TD><TD> " + glyph.getElementAsString(i) + "</TD></TR>";
				s += "<FONT FACE = 'Arial'><TR><TD>" + var.name
						+ "  </TD><TD> ";

				if (this.Width == 1) {
					for (int i = 0; i < dataManager.ExperimentDescr.size(); i++) {
						s += "<FACE = 'Arial'><TR><TD>"
								+ dataManager.ExperimentDescr.elementAt(i).stringData[gene]
								+ " </TD><TD> ";
					}
				}

				int x = Math.max(0, e.getPoint().x - abstandLinks)
						/ this.pixelSize;
				int y = Math.max(0, e.getPoint().y - upShift)
						/ (this.pixelSize);

				double valueD = 0;
				boolean isNA = true;

				for (int i = 0; i < Experiments.size(); i++) {
					if (i == x) {

						for (int j = 0; j < Genes.size(); j++) {
							if (j == y) {

								if (Experiments.elementAt(i).getRealValue(
										Genes.elementAt(j).ID) != dataManager.NA) {
									valueD += Experiments.elementAt(i)
											.getValue(Genes.elementAt(j).ID);
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

				if (Width != 1) {

					value = this.data[x][y] + "";
				}

				s += "<FONT FACE = 'Arial'><TR><TD>" + value + "  </TD><TD> ";

				s += "</P></FONT></STRONG>";

				return s;
			}

			if (e.getX() >= this.abstandLinks
					&& e.getX() <= this.abstandLinks + this.Experiments.size()
							* this.pixelSize && e.getY() <= this.upShift
					&& e.getY() >= this.abstandOben) {

				exp = (e.getX() - this.abstandLinks) / this.pixelSize;

				// System.out.println("svdsv " + exp);

				if (exp >= 0) {

					Variable var = this.Experiments.elementAt(exp);
					int index = var.colors.size() - 1
							- (this.upShift - 2 - e.getY())
							/ (2 * this.pixelSize + 1);

					String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";

					String name = "";
					
					
					try {
						name = ((Barchart)var.barchartsToColors
								.elementAt(index)).name;
					}
                    catch (Exception e1) {
                    	
                    } 			
                    
                    try {
						name = ((Histogram)var.barchartsToColors
								.elementAt(index)).name;
					}
                    catch (Exception e2) {
                    	
                    } 		
                    
                    
                    
					
					s += "<FONT FACE = 'Arial'><TR><TD>"
							+ name + "<TR><TD>"
									+ var.colorNames.elementAt(index) 
							+ "  </TD><TD> ";

					return s;

				}

			}

			return null;

		}
		return null;
	}

	public int getExpAtPoint(Point p) {
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());

		if (x < abstandLinks)
			return -1;
		if (y < upShift)
			return -1;

		x = (x - abstandLinks) / this.pixelSize;
		y = (y - upShift) / this.pixelSize;

		/*
		 * int exp = 0; for (int i = 0; i < this.orderSpalten.length; i++) { if
		 * (x == this.orderSpalten[i]) exp = i; }
		 */

		return Experiments.elementAt(x).ID;
	}

	public int getColumnOrig(int col) {
		int exp = -1;
		return Experiments.elementAt(col).ID;
	}

	public Variable getExperimentAtIndex(int index) {

		return Experiments.elementAt(index);
	}

	public void sortClusters(DescriptionVariable dVar) {
		/*
		 * 
		 * Vector<ClusterNode> List = nodeSpalten.getLeafList();
		 * 
		 * double w = 0; for (int i = 0; i <
		 * this.dataManager.Experiments.size(); i++) { double value =
		 * this.getVariabeAtIndex(i).getValue(dVar); List.elementAt(i).Weight =
		 * value; w += value; } //
		 * System.out.println(w/this.Experiments.size());
		 * 
		 * nodeSpalten.calculateWeights();
		 * 
		 * nodeSpalten.sort();
		 * 
		 * nodeSpalten.printWeights(" ");
		 * 
		 * for (int i = 0; i < orderSpalten.length; i++) {
		 * System.out.print(orderSpalten[i] + " "); } // System.out.println();
		 * 
		 * Vector<Integer> order = nodeSpalten.getOrder(); orderSpalten = new
		 * int[order.size()]; for (int i = 0; i < order.size(); i++) {
		 * orderSpalten[order.elementAt(i)] = i; }
		 * 
		 * for (int i = 0; i < orderSpalten.length; i++) {
		 * System.out.print(orderSpalten[i] + " "); } System.out.println();
		 */

	}

	public int getGeneAtPoint(Point p) {
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());

		if (x < abstandLinks)
			return -1;
		if (y < upShift)
			return -1;

		x = (x - abstandLinks) / this.pixelSize;
		y = (y - upShift) / this.pixelSize;

		int gene = 0;

		/*
		 * for (int i = 0; i < this.orderZeilen.length; i++) { if (y ==
		 * this.orderZeilen[i]) gene = i; }
		 */
		return Genes.elementAt(y).ID;
	}

	public int getRowIndexOrigin(int row) {

		return Genes.elementAt(row).ID;
	}

	/*
	 * 
	 * public CorrelationFrame createCorrelationExperiments() { double[][] data =
	 * new double[this.Experiments.size()][Genes.size()];
	 * 
	 * for (int i = 0; i < data.length; i++) { for (int j = 0; j <
	 * data[i].length; j++) { data[i][j] = Experiments
	 * .elementAt(i).getValue(Genes.elementAt(j).ID); } }
	 * 
	 * 
	 * 
	 * return new CorrelationFrame(seurat, "Correlation Columns", Genes,
	 * Experiments);
	 *  }
	 */
	/*
	 * public CorrelationFrame createCorrelationGenes() {
	 * 
	 * double[][] data = new double[Genes.size()][Experiments.size()];
	 * 
	 * for (int i = 0; i < data.length; i++) { for (int j = 0; j <
	 * data[i].length; j++) { data[i][j] = dataManager.Experiments
	 * .elementAt(j).getValue(Genes.elementAt(i).ID); } }
	 * 
	 * 
	 * int [] order = new int [Experiments.size()];
	 * 
	 * for (int i = 0; i < Experiments.size(); i++){ order [i] =
	 * Experiments.elementAt(i).ID; }
	 * 
	 * 
	 * return new CorrelationFrame(seurat, this.calculateCorrs(data),
	 * "Correlation Rows", false, order, Width, data);
	 *  }
	 */

	public String createInfo() {
		info = "";
		info += "Order: \n";
		info += "Columns: ";
		for (int i = 0; i < this.Experiments.size(); i++) {
			info += this.Experiments.elementAt(i).ID + " ";

		}
		info += "\nRows: ";
		for (int i = 0; i < this.Genes.size(); i++) {
			info += Genes.elementAt(i).ID + " ";
		}

		return info;
	}

	/*
	 * public String calculateCriterion(String arg) { try {
	 * 
	 * if (dataManager.rConnection == null) dataManager.rConnection = new
	 * Rconnection(); dataManager.rConnection.voidEval("library(seriation)");
	 * 
	 * dataManager.rConnection.assign("tempData",
	 * this.dataManager.Experiments.elementAt(orderSpalten[0]) .getColumn());
	 * 
	 * for (int i = 1; i < this.dataManager.Experiments.size(); i++) {
	 * dataManager.rConnection.assign("x",
	 * this.dataManager.Experiments.elementAt(orderSpalten[i]) .getColumn());
	 * dataManager.rConnection .voidEval("tempData <- cbind(tempData, x)");
	 *  }
	 * 
	 * dataManager.rConnection.voidEval("d<-dist(tempData,method=\"" +
	 * "euclidean" + "\")"); dataManager.rConnection.voidEval("koeff<-criterion(d,
	 * method = \"" + arg + "\")");
	 * 
	 * String koeff = dataManager.rConnection.eval("koeff").asDouble() + "";
	 * return koeff; /* dataManager.rConnection.assign("tempData",
	 * dataManager.getRowData(0));
	 * 
	 * for (int i = 1; i < dataManager.variables.elementAt(0).doubleData.length;
	 * i++) { dataManager.rConnection.assign("x", dataManager.getRowData(i));
	 * dataManager.rConnection.voidEval("tempData <- cbind(tempData, x)"); }
	 * 
	 * dataManager.rConnection.voidEval("d<-dist(tempData,method=\""+Distance+"\")");
	 * dataManager.rConnection.voidEval("order<-seriate(d,method=\"" + method +
	 * "\")");
	 * 
	 * int[] orderZeilen = dataManager.rConnection.eval("get_order(order,1)")
	 * .asIntArray(); int[] zeilen = new int[orderZeilen.length];
	 * 
	 * for (int i = 0; i < orderZeilen.length; i++) { zeilen[orderZeilen[i] - 1] =
	 * i; }
	 * 
	 *  } catch (Exception e) { e.printStackTrace();
	 * JOptionPane.showMessageDialog(this, "Calculation failed."); } return
	 * null; }
	 */

	@Override
	public void paint(Graphics g) {
		/*
		 * if (!clustering) { for (int i = 0; i < Experiments.size(); i++) { if
		 * (dataManager.selectedVariables [i]) { g.setColor(Color.RED);
		 * g.fillRect(abstandLinks + orderSpalten[i] * this.pixelSize,
		 * abstandOben - 2*pixelSize, this.pixelSize, this.pixelSize); } }
		 * 
		 * for (int i = 0; i < dataManager.selectedRows.length; i++) { if
		 * (dataManager.selectedRows [i]) { g.setColor(Color.RED);
		 * g.fillRect(abstandLinks - 2*this.pixelSize, abstandOben +
		 * orderZeilen[i]*pixelSize, this.pixelSize, this.pixelSize); } } }
		 */

		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		

		int colorsHight = 0;

		for (int i = 0; i < Experiments.size(); i++) {
			Variable var = Experiments.elementAt(i);

			for (int j = var.colors.size() - 1; j >= 0; j--) {

				colorsHight = var.colors.size() * (2 * this.pixelSize + 1) + 4;

				g.setColor(var.colors.elementAt(j));

				g.fillRect(abstandLinks + i * this.pixelSize, 2 + abstandOben
						+ j * (2 * this.pixelSize + 1), Math.max(pixelSize, 2),
						2 * pixelSize + 1);
			}
		}

		
		
		upShift = abstandOben + colorsHight;

		
		if (cellColor == null) this.updateSelection();
		
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < PixelCount; j++) {

					g.setColor(cellColor [i][j]);
					

					g.fillRect(abstandLinks + i * this.pixelSize, upShift + j
							* this.pixelSize, pixelSize, pixelSize);
				

			}
			}
		
			
			
			
			
			
			

		/*
		 * 
		 * 
		 * 
		 * 
		 * public void paint(Graphics g) {
		 * 
		 * 
		 * g.setColor(Color.white); g.fillRect(0, 0, this.getWidth(),
		 * this.getHeight());
		 * 
		 * boolean selection = false;
		 * 
		 * for (int i = 0; i < this.Experiments.size(); i++) { if
		 * (this.Experiments.elementAt(i).isSelected()) selection = true; } for
		 * (int i = 0; i < this.Genes.size(); i++) { if
		 * (this.Genes.elementAt(i).isSelected()) selection = true; }
		 * 
		 * 
		 * int colorsHight = 0;
		 * 
		 * for (int i = 0; i < Experiments.size(); i++) { Variable var =
		 * Experiments.elementAt(i);
		 * 
		 * for (int j = var.colors.size()-1; j >= 0; j--) {
		 * 
		 * colorsHight = var.colors.size() * (2*this.pixelSize+1)+4;
		 * 
		 * g.setColor(var.colors.elementAt(j) );
		 * 
		 * g.fillRect(abstandLinks + i * this.pixelSize, 2 + abstandOben + j *
		 * (2*this.pixelSize + 1), Math.max( pixelSize, 2), 2*pixelSize+1); } }
		 * 
		 * upShift = abstandOben + colorsHight;
		 * 
		 * 
		 * 
		 * 
		 * if (seurat.settings.Model == 1) {
		 * 
		 * for (int i = 0; i < data.length; i++) { for (int j = 0; j <
		 * PixelCount; j++) {
		 * 
		 * if (data[i][j] > 0) { double koeff = data[i][j] / Max[i];
		 * 
		 * Color c = Color.getHSBColor(0, (float) fPos(koeff), 1);
		 * 
		 * if (PixelCount == dataManager.RowCount &&
		 * Experiments.elementAt(i).getRealValue( Genes.elementAt(j).ID) ==
		 * dataManager.NA) c = Color.WHITE;
		 * 
		 * if (selection) { c = c.darker(); c = c.darker(); }
		 * 
		 * boolean selected = false;
		 * 
		 * for (int k = j * Width; k < (j + 1) * Width; k++) { if (k <
		 * dataManager.RowCount) { if (dataManager.Experiments.elementAt(this
		 * .getColumnOrig(i)).isSelected[this .getRowIndexOrigin(k)]) { selected =
		 * true; }
		 *  } }
		 * 
		 * if (selected) { c = c.brighter(); c = c.brighter(); } g.setColor(c); }
		 * else { double koeff = Math.pow(data[i][j] / Min[i], 1);
		 * 
		 * Color c = (Color.getHSBColor((float) 0.33, (float) fNeg(koeff), 1));
		 * 
		 * if (selection) { c = c.darker(); c = c.darker(); }
		 * 
		 * boolean selected = false;
		 * 
		 * for (int k = j * Width; k < (j + 1) * Width; k++) { if (k <
		 * dataManager.RowCount) { if (dataManager.Experiments.elementAt(this
		 * .getColumnOrig(i)).isSelected[this .getRowIndexOrigin(k)]) { selected =
		 * true; }
		 *  } }
		 * 
		 * if (selected) { c = c.brighter(); c = c.brighter(); }
		 *  // c = Color.WHITE; g.setColor(c); }
		 * 
		 * g.fillRect(abstandLinks + i * this.pixelSize, upShift + j *
		 * this.pixelSize, pixelSize, pixelSize); }
		 *  }
		 *  }
		 * 
		 * if (seurat.settings.Model == 2) {
		 * 
		 * for (int i = 0; i < data.length; i++) { for (int j = 0; j <
		 * PixelCount; j++) {
		 * 
		 * if (data[i][j] > 0) {
		 * 
		 * double koeff = data[i][j] / Max[i];
		 * 
		 * Color c = new Color((float) fPos(koeff), 0, 0);
		 * 
		 * if (PixelCount == dataManager.RowCount &&
		 * Experiments.elementAt(i).getRealValue( Genes.elementAt(j).ID) ==
		 * dataManager.NA) c = Color.WHITE;
		 * 
		 * if (selection) { c = c.darker(); c = c.darker(); c = c.darker(); }
		 * 
		 * boolean selected = false;
		 * 
		 * for (int k = j * Width; k < (j + 1) * Width; k++) { if (k <
		 * dataManager.RowCount) { if (dataManager.Experiments.elementAt(this
		 * .getColumnOrig(i)).isSelected[this .getRowIndexOrigin(k)]) { selected =
		 * true; }
		 *  } }
		 * 
		 * if (selected) { c = c.brighter(); c = c.brighter(); c = c.brighter();
		 * c = c.brighter();
		 * 
		 *  // c = new Color((float) //
		 * (Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt((float)fPos(koeff)))))), //
		 * 0, 0); // c = new Color( 255,211,211);
		 * 
		 * if (PixelCount == dataManager.RowCount &&
		 * Experiments.elementAt(i).getRealValue( Genes.elementAt(j).ID) ==
		 * dataManager.NA) c = Color.WHITE;
		 *  }
		 * 
		 * g.setColor(c); } else {
		 *  // Color c = new Color(0,(float)(0.5*koeff),0); double koeff =
		 * data[i][j] / Min[i];
		 * 
		 * Color c = new Color(0, (float) fNeg(koeff), 0);
		 * 
		 * if (selection) { c = c.darker(); c = c.darker(); c = c.darker(); }
		 * 
		 * boolean selected = false;
		 * 
		 * for (int k = j * Width; k < (j + 1) * Width; k++) { if (k <
		 * dataManager.RowCount) { if (dataManager.Experiments.elementAt(this
		 * .getColumnOrig(i)).isSelected[this .getRowIndexOrigin(k)]) { selected =
		 * true; }
		 *  } }
		 * 
		 * if (selected) { c = c.brighter(); c = c.brighter(); c = c.brighter();
		 * c = c.brighter(); // c = new Color(0,(float) //
		 * Math.sqrt(Math.sqrt((float)fNeg(koeff))), 0);
		 *  } g.setColor(c); }
		 * 
		 * g.fillRect(abstandLinks + i * this.pixelSize, upShift + j *
		 * this.pixelSize, pixelSize, pixelSize); }
		 *  }
		 *  }
		 * 
		 * 
		 * 
		 */

		if (clustering) {
			this.nodesC = new Vector();
			this.nodesR = new Vector();

			paintClusteringRows(g, nodeZeilen);
			paintClusteringColumns(g, nodeSpalten);
		}

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

	public int getYCoordinate(Vector<Integer> Cases) {
		int pos = 0;
		for (int i = 0; i < Cases.size(); i++) {
			pos += this.getIndexOfGeneInHeatMap(Cases.elementAt(i))
					* this.pixelSize / this.Width;
		}

		return (upShift + pos / Cases.size());
	}

	public int getXCoordinate(Vector<Integer> Cases) {
		int pos = 0;
		for (int i = 0; i < Cases.size(); i++) {
			pos += getIndexOfExperimentInHeatMap(Cases.elementAt(i).intValue())
					* this.pixelSize;
		}

		return (this.abstandLinks + pos / Cases.size());
	}

	public int getIndexOfExperimentInHeatMap(int ID) {
		for (int i = 0; i < Experiments.size(); i++) {
			if (Experiments.elementAt(i).ID == ID)
				return i;
		}
		return -1;
	}

	public int getIndexOfGeneInHeatMap(int ID) {
		for (int i = 0; i < Genes.size(); i++) {
			if (Genes.elementAt(i).ID == ID)
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
				for (int ii = i * this.Width; ii < Math.min((i + 1)
						* this.Width, dataManager.RowCount); ii++) {
					for (int jj = j * this.Width; jj < Math.min((j + 1)
							* this.Width, dataManager.RowCount); jj++) {
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

	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}

	/*
	 * 
	 * public void calculateClustersZeilen(String method, String distance) {
	 * int[][] clusterArray = new int[1][1]; int[] geneClusters = new
	 * int[dataManager.Experiments.elementAt(0).stringData.length];
	 * 
	 * try {
	 * 
	 * if (dataManager.rConnection == null) dataManager.rConnection = new
	 * Rconnection();
	 * 
	 * dataManager.rConnection.voidEval("library(stats)");
	 * dataManager.rConnection.assign("tempData", dataManager.Experiments
	 * .elementAt(0).getColumn());
	 * 
	 * for (int i = 1; i < dataManager.Experiments.size(); i++) {
	 * dataManager.rConnection.assign("x", dataManager.Experiments
	 * .elementAt(i).getColumn()); dataManager.rConnection .voidEval("tempData <-
	 * cbind(tempData, x)");
	 *  }
	 * 
	 * dataManager.rConnection .voidEval("h <- hclust(dist(tempData, method = '" +
	 * distance + "', p = " + 2 + " ), method = '" + method + "',
	 * members=NULL)");
	 * 
	 * clusterArray = new int[dataManager.RowCount][dataManager.RowCount];
	 * 
	 * for (int count = dataManager.RowCount; count > 0; count--) {
	 * 
	 * dataManager.rConnection.voidEval("c <- cutree(h,k = " + count + ")");
	 * 
	 * 
	 * for (int i = 1; i < dataManager.RowCount + 1; i++) geneClusters[i - 1] =
	 * dataManager.rConnection.eval( "c [[" + i + "]]").asInt(); for (int i = 0;
	 * i < dataManager.RowCount; i++) { // System.out.print(this.geneClusters
	 * [i] + " "); clusterArray[i][count - 1] = geneClusters[i]; }
	 *  // System.out.println(""); }
	 *  } catch (Exception e) { e.printStackTrace();
	 * JOptionPane.showMessageDialog(this, "Calculation failed."); }
	 * 
	 * nodeZeilen = new ClusterNode(dataManager, false); nodeZeilen.cases = new
	 * Vector(); for (int i = 0; i < dataManager.RowCount; i++) {
	 * nodeZeilen.cases.add(new Integer(i)); }
	 * 
	 * for (int i = 1; i < dataManager.RowCount; i++) { int k = 0; int
	 * geteilterNode = 0; int newNode = 0; for (int j = 0; j <
	 * dataManager.RowCount; j++) { if (clusterArray[j][i] != clusterArray[j][i -
	 * 1]) { k = j; geteilterNode = clusterArray[j][i - 1]; newNode =
	 * clusterArray[j][i]; break; } }
	 *  // aus geteilterNode -> geteilterNode + newNode // System.out.println("
	 * k : " + k); // System.out.println(" geteilterNOde : " + geteilterNode); //
	 * System.out.println(" newNode : " + newNode);
	 * 
	 * ClusterNode oldNode = nodeZeilen.getClusterNode(k); Vector<Integer>
	 * nodeR = new Vector(); Vector<Integer> nodeL = new Vector(); for (int j =
	 * 0; j < dataManager.RowCount; j++) { if (clusterArray[j][i - 1] ==
	 * geteilterNode) { if (clusterArray[j][i] == geteilterNode) nodeR.add(j);
	 * else nodeL.add(j); } } oldNode.nodeL = new ClusterNode(nodeL,
	 * dataManager, false); oldNode.nodeR = new ClusterNode(nodeR, dataManager,
	 * false);
	 *  // System.out.println("NNNOOODDEEE"); // node.output(""); }
	 * 
	 * Vector<Integer> order = nodeZeilen.getOrder(); orderZeilen = new
	 * int[order.size()]; for (int i = 0; i < order.size(); i++) {
	 * orderZeilen[order.elementAt(i)] = i; }
	 * 
	 *  }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public void calculateClustersSpalten(String method, String distance) {
	 * 
	 * int[][] clusterArray = new int[1][1]; int spaltenCount =
	 * this.dataManager.Experiments.size();
	 * 
	 * int[] geneClusters = new int[spaltenCount];
	 * 
	 * boolean weiter = true;
	 * 
	 * try {
	 * 
	 * dataManager.rConnection.assign("tempData", dataManager .getRowData(0));
	 * 
	 * for (int i = 1; i < dataManager.RowCount; i++) {
	 * dataManager.rConnection.assign("x", dataManager.getRowData(i));
	 * dataManager.rConnection .voidEval("tempData <- cbind(tempData, x)"); }
	 * 
	 * dataManager.rConnection .voidEval("h <- hclust(dist(tempData, method = '" +
	 * distance + "', p = " + 2 + " ), method = '" + method + "',
	 * members=NULL)");
	 * 
	 * clusterArray = new int[spaltenCount][spaltenCount];
	 * 
	 * for (int count = spaltenCount; count > 0; count--) {
	 * 
	 * dataManager.rConnection.voidEval("c <- cutree(h,k = " + count + ")");
	 *  // System.out.println(this.variables.size() + " " + //
	 * this.variables.elementAt(0).stringData.length);
	 * 
	 * for (int i = 1; i < spaltenCount + 1; i++) geneClusters[i - 1] =
	 * dataManager.rConnection.eval( "c [[" + i + "]]").asInt(); for (int i = 0;
	 * i < spaltenCount; i++) { // System.out.print(this.geneClusters [i] + "
	 * "); clusterArray[i][count - 1] = geneClusters[i]; }
	 * 
	 *  }
	 *  } catch (Exception e) { e.printStackTrace();
	 * JOptionPane.showMessageDialog(this, "Calculation failed."); }
	 * 
	 * nodeSpalten = new ClusterNode(dataManager, true); nodeSpalten.cases = new
	 * Vector(); for (int i = 0; i < spaltenCount; i++) {
	 * nodeSpalten.cases.add(new Integer(i)); }
	 * 
	 * for (int i = 1; i < spaltenCount; i++) { int k = 0; int geteilterNode =
	 * 0; int newNode = 0; for (int j = 0; j < spaltenCount; j++) { if
	 * (clusterArray[j][i] != clusterArray[j][i - 1]) { k = j; geteilterNode =
	 * clusterArray[j][i - 1]; newNode = clusterArray[j][i]; break; } }
	 *  // aus geteilterNode -> geteilterNode + newNode // System.out.println("
	 * k : " + k); // System.out.println(" geteilterNOde : " + geteilterNode); //
	 * System.out.println(" newNode : " + newNode);
	 * 
	 * ClusterNode oldNode = nodeSpalten.getClusterNode(k); Vector<Integer>
	 * nodeR = new Vector(); Vector<Integer> nodeL = new Vector(); for (int j =
	 * 0; j < spaltenCount; j++) { if (clusterArray[j][i - 1] == geteilterNode) {
	 * if (clusterArray[j][i] == geteilterNode) nodeR.add(j); else nodeL.add(j); } }
	 * oldNode.nodeL = new ClusterNode(nodeL, dataManager, true); oldNode.nodeR =
	 * new ClusterNode(nodeR, dataManager, true);
	 *  // System.out.println("NNNOOODDEEE"); // node.output(""); }
	 * 
	 * Vector<Integer> order = nodeSpalten.getOrder(); orderSpalten = new
	 * int[order.size()]; for (int i = 0; i < order.size(); i++) {
	 * orderSpalten[order.elementAt(i)] = i; }
	 *  }
	 */

}