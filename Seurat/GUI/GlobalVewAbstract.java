package GUI;


import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.*;

class GlobalViewAbstract extends JFrame implements MatrixWindow, IPlot {
	int pixelSize = 2;

	Seurat seurat;

	GlobalViewAbstractPanel gPanel;

	// int[] orderZeilen;

	// int[] orderSpalten;

	JMenuItem item = new JMenuItem("Clustering");

	GlobalViewAbstract globalView = this;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	DataManager dataManager;

	Vector<ISelectable> Experiments;
	Vector<ISelectable> Genes;
	
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
		
		

		
		int upShift = gPanel.abstandOben;
		
		infoLabel.setText("Aggregation: 1 : " + gPanel.Width);
	
		
		resize = false;

		
		
		if (seurat.SYSTEM == seurat.WINDOWS) {

			this.setSize(
					panelW + 5+14,
					panelH+ 26+15
					+ infoPanel.getHeight()
					
							);

		} else
			this.setSize(
							panelW + 5,
							panelH+ 26
							+ infoPanel.getHeight()
							
									);
		
		
	

		updateSelection();
		
		
	}
	
	
	
	
	
	

	public GlobalViewAbstract(Seurat seurat, String name, Vector Experiments,
			Vector Genes, boolean clustering) {
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
			
		
		
		GlobalViewAbstractPanel panel = new GlobalViewAbstractPanel(seurat, this, pixelSize,
				Experiments, Genes);

		
		p.add(panel,BorderLayout.CENTER);
		
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		infoPanel = new JPanel();
		if (Experiments.size() < 116) infoPanel.setPreferredSize(new Dimension(300,45));
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
			));
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
								gPanel.getHeight()
										- globalView.gPanel.abstandOben ,
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

class GlobalViewAbstractPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener {
	DataManager dataManager;

	Seurat seurat;

	int pixelSize = 1;

	int abstandLinks = 1;

	int abstandOben = 1;

	Vector<ISelectable> Genes;

	Vector<ISelectable> Experiments;

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

	GlobalViewAbstract globalView;

	Color SelectionColor = Color.black;

	Vector<CoordinateNode> nodesR;

	Vector<CoordinateNode> nodesC;

	Color[][] cellColor;

	int upShift;

	boolean[][] isCellSelected;


	public GlobalViewAbstractPanel(Seurat seurat, GlobalViewAbstract globalView, int pixelSize,
			Vector Experiments, Vector Genes) {

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
		
		// createRowsAndColumns();
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

		if (seurat.settings.Model == 1) {

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < PixelCount; j++) {

					if (data[i][j] > 0) {

						double koeff = data[i][j] / Max[i];

						Color c = Color.getHSBColor(0, (float) fPos(koeff), 1);

						if (PixelCount == Genes.size()
								&& Experiments.elementAt(i).getRealValue(
										Genes.elementAt(j).getID()) == dataManager.NA)
							c = Color.WHITE;

						if (selection) {
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Width; k < (j + 1) * Width; k++) {
							if (k < Genes.size()) {
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) {
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
						double koeff = Math.pow(data[i][j] / Min[i], 1);

						Color c = (Color.getHSBColor((float) 0.33,
								(float) fNeg(koeff), 1));

						if (selection) {
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Width; k < (j + 1) * Width; k++) {
							if (k < Genes.size()) {
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) {
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

						if (PixelCount == Genes.size()
								&& Experiments.elementAt(i).getRealValue(
										Genes.elementAt(j).getID()) == dataManager.NA)
							c = Color.WHITE;

						if (selection) {
							c = c.darker();
							c = c.darker();
							c = c.darker();
						}

						boolean selected = false;

						for (int k = j * Width; k < (j + 1) * Width; k++) {
							if (k < Genes.size()) {
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) {
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

							if (PixelCount == Genes.size()
									&& Experiments.elementAt(i).getRealValue(
											Genes.elementAt(j).getID()) == dataManager.NA)
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
							if (k < Genes.size()) {
								if (Experiments.elementAt(i).isSelected() && Genes.elementAt(k).isSelected()) {
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

					if (Genes.elementAt(i).isSelected()) {
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

		dataManager.deleteSelection();
	

		for (int i = 0; i < Experiments.size(); i++) {
			for (int j = 0; j < Genes.size(); j++) {
				if (i <= x2 && i >= x1 && j <= y2 && j >= y1) {

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

			for (int i = 0; i < Experiments.size(); i++) {
				Experiments.elementAt(i).unselect(true);

			}

			for (int i = 0; i < Genes.size(); i++) {
				Genes.elementAt(i).unselect(true);

			}

		}

		

		if (Math.max(yy1, yy2) < this.upShift) {
			for (int i = 0; i < Experiments.size(); i++) {
					if (i < x2 && i >= x1) {

				this.Experiments.elementAt(i).select(true);
			

					

				}
			}
		}

		if (Math.max(xx1, xx2) < this.abstandLinks) {
				for (int j = 0; j < Genes.size(); j++) {
					if (j < y2 && j >= y1) {

					this.Genes.elementAt(j).select(true);

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

		data = new double[Experiments.size()][this.PixelCount];

		for (int i = 0; i < Experiments.size(); i++) {
			for (int j = 0; j < PixelCount; j++) {
				int count = 0;
				for (int k = j * Width; k < (j + 1) * Width; k++) {
					if (k < Genes.size()) {
						data[i][j] += Experiments.elementAt(i).getValue(k);
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

				//	new CorrelationFrame(seurat, Genes, Experiments, Width,
					//		false, "Correlation Rows");
				}
			});
			menu.add(item);
			
			
			
			item = new JMenuItem("Kmeans");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					new KMeansDialog(seurat,Genes, Experiments);
				//	new CorrelationFrame(seurat, Genes, Experiments, Width,
					//		false, "Correlation Rows");
				}
			});
			menu.add(item);
			
			
					

			item = new JMenuItem("Columns Correlation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

//					new CorrelationFrame(seurat, Genes, Experiments, 1, true,
	//						"Correlation Columns");

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

	@Override
	public String getToolTipText(MouseEvent e) {

		if (e.isControlDown()) {
			int exp = this.getExpAtPoint(e.getPoint());
			int gene = this.getGeneAtPoint(e.getPoint());

			if (exp != -1 && gene != -1) {
				ISelectable var = Experiments.elementAt(exp);

				String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";
				// s += "<FONT FACE = 'Arial'><TR><TD>"+var.doubleData []+ "
				// </TD><TD> " + glyph.getElementAsString(i) + "</TD></TR>";
				s += "<FONT FACE = 'Arial'><TR><TD>" + var.getName()
						+ "  </TD><TD> ";
/*
				if (this.Width == 1) {
					for (int i = 0; i < dataManager.ExperimentDescr.size(); i++) {
						s += "<FACE = 'Arial'><TR><TD>"
								+ dataManager.ExperimentDescr.elementAt(i).stringData[gene]
								+ " </TD><TD> ";
					}
				}
*/
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
										Genes.elementAt(j).getID()) != dataManager.NA) {
									valueD += Experiments.elementAt(i)
											.getValue(Genes.elementAt(j).getID());
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

					ISelectable var = this.Experiments.elementAt(exp);
				

					String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";

					String name = var.getName();
					
					
				
                    
                    
					
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

		return Experiments.elementAt(x).getID();
	}

	public int getColumnOrig(int col) {
		int exp = -1;
		return Experiments.elementAt(col).getID();
	}

	public ISelectable getExperimentAtIndex(int index) {

		return Experiments.elementAt(index);
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
		return Genes.elementAt(y).getID();
	}

	public int getRowIndexOrigin(int row) {

		return Genes.elementAt(row).getID();
	}

	



	@Override
	public void paint(Graphics g) {
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		

		

		
		
		upShift = abstandOben;

		
		if (cellColor == null) this.updateSelection();
		
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < PixelCount; j++) {

					g.setColor(cellColor [i][j]);
					

					g.fillRect(abstandLinks + i * this.pixelSize, upShift + j
							* this.pixelSize, pixelSize, pixelSize);
				

			}
			}
		
			
			
			
			

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
			if (Experiments.elementAt(i).getID() == ID)
				return i;
		}
		return -1;
	}

	public int getIndexOfGeneInHeatMap(int ID) {
		for (int i = 0; i < Genes.size(); i++) {
			if (Genes.elementAt(i).getID() == ID)
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
						* this.Width, Genes.size()); ii++) {
					for (int jj = j * this.Width; jj < Math.min((j + 1)
							* this.Width, Genes.size()); jj++) {
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

	

}