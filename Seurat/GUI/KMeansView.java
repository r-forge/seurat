package GUI;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.*;
import Settings.*;

class KMeansView extends JFrame implements MatrixWindow, IPlot {
	
	Seurat seurat;

	KmeansPanel panel;

	// int[] orderZeilen;

	// int[] orderSpalten;

	JMenuItem item ;

	KMeansView plot = this;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	DataManager dataManager;

	Vector<Vector<ISelectable>> Experiments;
	Vector<Vector<ISelectable>> Genes;

	int oldPixelCount;


int abstandUnten = 2;
	
	long timeResized;
	
	
	
	
	

	JLabel infoLabel;
	
	JPanel infoPanel;
	
	
	public void applyNewPixelSize(int pixelW, int pixelH) {
		
	
		  this.panel.pixelW = pixelW; 
		  this.panel.pixelH = pixelH; 
		  
		  
		  int col = 0;
		  
		  int shiftX = 2;
		  
		  for (int i = 0; i < this.Experiments.size(); i++) {
				Vector<ISelectable> exps = this.Experiments.elementAt(i);
				
				shiftX = shiftX
				+ exps.size() * pixelW + 1;
		  }
		  
		  
		  int shiftY = 2;
		  
		  for (int j = 0; j < Genes.size(); j++) {
			  
				shiftY = shiftY
				+ (Genes.elementAt(j).size() * pixelH/ panel.Aggregation + 1);
		  
		  }
		  shiftY = shiftY
			+ this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * pixelH + 2);
		  
		  int row = Genes.size();
		  
		 panel.setPreferredSize(new Dimension(shiftX+2, shiftY+2));
		  
		
		
		  
		  
		  
		  
		  
		 if (seurat.SYSTEM == seurat.WINDOWS) {
		  
		  this .setSize( shiftX + 17, shiftY + 38 +abstandUnten+15);
		   } else this .setSize(shiftX + 2,shiftY + 23 + abstandUnten +15);
		 
		 updateSelection();
		 

	}

	public KMeansView(Seurat seurat, String name,
			Vector<Vector<ISelectable>> Experiments, Vector<Vector<ISelectable>> Genes) {
		super(name);
		System.out.println("KMeansView");
		item = new JMenuItem(name);

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.Experiments = Experiments;
		this.Genes = Genes;
		
		this.getContentPane().setLayout(new BorderLayout());
		panel = new KmeansPanel(seurat, this, Experiments, Genes);

		this.addMouseListener(panel);

		


		this.setBounds(200, 200, 800, 800);
		this.applyNewPixelSize(seurat.settings.PixelW, seurat.settings.PixelH);

		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.setVisible(true);

		seurat.windows.add(this);

		seurat.windowMenu.add(item);
		
		  
		 this.addKeyListener(panel);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plot.setVisible(true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				plot.seurat.windowMenu.remove(item);
			}
		});

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				/*
				
				long newTimeResized = System.currentTimeMillis();
				if (newTimeResized - timeResized > 200) {
				
				timeResized = newTimeResized;
				
				
				  int Approx = 1;
				  boolean weiter = true;
				  
				  
				  while (weiter) {
				
					  int shiftY = 2;
					  shiftY = shiftY
						+ plot.Experiments.elementAt(0).elementAt(0).getColors().size() * (2 * panel.pixelH + 2);
					
					  
					  for (int j = 0; j < plot.Genes.size(); j++) {
						  
							shiftY = shiftY
							+ (plot.Genes.elementAt(j).size() * panel.pixelH / Approx + 1);
					  
					  }
                  
					  if (shiftY <= panel.getHeight()) weiter = false;
                  
					  else Approx++;
				  }
				  
				  panel.Approx = Approx;
				  
				  plot.applyNewPixelSize(panel.pixelW,panel.pixelH);
				  
				}*/
			}

		});
		
		
		
		infoPanel = new JPanel();
		//	if (Experiments.size() < 116) infoPanel.setPreferredSize(new Dimension(300,45));
			infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			infoPanel.setBorder(BorderFactory.createEtchedBorder());
			this.getContentPane().add(infoPanel,BorderLayout.SOUTH);
			
			infoLabel = new JLabel("Aggregation: 1 : " + panel.Aggregation);
			Font myFont = new Font("SansSerif", 0, 10);

			
			
			String s = "{";
			for (int i = 0; i < Experiments.size(); i++) {
				s+=Experiments.elementAt(i).size();
				if (i != Experiments.size()-1) s+=",";
			}
			s+="}";
			
			JLabel label = new JLabel("Columns: " + s);
			label.setFont(myFont);
			infoPanel.add(label);
			
			
			
			s = "{";
			for (int i = 0; i < Genes.size(); i++) {
				s+=Genes.elementAt(i).size();
				if (i != Experiments.size()-1) s+=",";
			}
			s+="}";
			
			
			
			label = new JLabel(" Rows: "+s+"  ");
			label.setFont(myFont);
			infoPanel.add(label);
			
			
			
			
			infoLabel.setFont(myFont);
			infoPanel.add(infoLabel);

	}

	public void updateSelection() {
		// TODO Auto-generated method stub
		panel.updateSelection();

	}

	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}

	public void applyNewPixelSize() {
		// TODO Auto-generated method stub
		this.applyNewPixelSize(panel.pixelW,panel.pixelH);
	}

	public void setModel(int model) {
		// TODO Auto-generated method stub
		panel.Model = model;
	}

}

class KmeansPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener, ColorListener, KeyListener {
	DataManager dataManager;

	Seurat seurat;
	
	int Model;

	int pixelW;
	int pixelH;

	int abstandLinks = 1;

	int abstandOben = 1;

	Vector<Vector<ISelectable>> Genes;

	Vector<Vector<ISelectable>> Experiments;

	int[] originalOrderSpalten;

	ClusterNode nodeZeilen;

	ClusterNode nodeSpalten;

	Point point1, point2;

	boolean clustering = true;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	String info, infoRows, infoColumns;

	//int Aggregation = 1;

	double[][] data;

	double[] Min, Max;

	KMeansView plot;

	Color SelectionColor = Color.black;

	Color[][] cellColor;

	int upShift;

	boolean[][] isCellSelected;

	Block[][] blocks;

	
	
	
	int Aggregation = 1;

	public KmeansPanel(Seurat seurat, KMeansView plot, Vector<Vector<ISelectable>> Experiments, Vector<Vector<ISelectable>> Genes) {

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.plot = plot;
		

		this.Experiments = Experiments;
		this.Genes = Genes;

		// this.originalOrderSpalten = orderSpalten;

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void mouseExited(MouseEvent e) {
	}

	public void updateSelection() {

		if (blocks == null) {
			blocks = new Block[this.Experiments.size()][this.Genes.size()];

			for (int i = 0; i < this.Experiments.size(); i++) {
				Vector<ISelectable> exps = this.Experiments.elementAt(i);

				for (int j = 0; j < Genes.size(); j++) {

					Vector<ISelectable> genes = this.Genes.elementAt(j);

					blocks[i][j] = new Block(seurat, exps, genes);

				}

			}

		}

		int shiftX = 2;

		boolean isSelection = seurat.dataManager.isSomethingSelected();
		
		
		
		for (int i = 0; i < this.Experiments.size(); i++) {
			Vector<ISelectable> exps = this.Experiments.elementAt(i);
			
			
			boolean isMinMaxSet = false;
			double min =0, max = 0;

			int shiftY = 2;
			
			 shiftY = shiftY
				+ this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * this.pixelH + 2);
			

			for (int j = 0; j < Genes.size(); j++) {

				Vector<ISelectable> genes = this.Genes.elementAt(j);

				blocks[i][j].x = shiftX;
				blocks[i][j].y = shiftY;
				
				
				int a = 0;
				if ((blocks[i][j].Genes.size()-1) % this.Aggregation ==0) a =1;
				
				blocks[i][j].values = new double [blocks[i][j].Experiments.size()]   [(blocks[i][j].Genes.size()-1) / this.Aggregation +1];
				blocks[i][j].isSelected = new boolean [blocks[i][j].Experiments.size()]   [(blocks[i][j].Genes.size()-1) / this.Aggregation +1];
				
			
				
				
				
				for (int ii = 0; ii < blocks[i][j].Experiments.size(); ii++) {
					for (int jj = 0; jj < blocks[i][j].Genes.size(); jj++) {
						blocks[i][j].values [ii][jj/Aggregation] = 0;
						//if (blocks[i][j].Experiments.elementAt(ii).isSelected [blocks[i][j].Genes.elementAt(jj).ID]  )  {
						if (blocks[i][j].Experiments.elementAt(ii).isSelected() &&  blocks[i][j].Genes.elementAt(jj).isSelected()  )  {
							
						blocks[i][j].isSelected [ii][jj/Aggregation] = true;
						//	isSelection = true;
						}
					}
				}
				
				
				
				int [][] counts = new  int [blocks[i][j].Experiments.size()][blocks[i][j].Genes.size() / this.Aggregation+2]; 
				
				for (int ii = 0; ii < blocks[i][j].Experiments.size(); ii++) {
					for (int jj = 0; jj < blocks[i][j].Genes.size(); jj++) {
						
						if (exps.elementAt(ii).getRealValue(genes.elementAt(jj).getID()) != seurat.dataManager.NA) {
							
						
							
						blocks[i][j].values [ii][jj/this.Aggregation]+= 
							exps.elementAt(ii).getRealValue(genes.elementAt(jj).getID()) ;
				counts [ii][jj/this.Aggregation]++;
						
						            
					}
					
				}
				}
				
				for (int ii = 0; ii < blocks[i][j].values.length; ii++) {
					
					for (int jj = 0; jj < blocks[i][j].values [ii].length; jj++) {
						if (counts [ii][jj]!=0) {
							blocks[i][j].values [ii] [jj] /= counts [ii][jj];
							if (isMinMaxSet) {
								if (min > blocks[i][j].values [ii] [jj]) min = blocks[i][j].values [ii] [jj];
								if (max < blocks[i][j].values [ii] [jj]) max = blocks[i][j].values [ii] [jj];
							}
							else {
								min = blocks[i][j].values [ii] [jj];
								max = blocks[i][j].values [ii] [jj];
								isMinMaxSet = true;
							}
						}
						else blocks[i][j].values [ii] [jj] = seurat.dataManager.NA;
					}
					
					
				}
				
				
				
				
				

				shiftY = shiftY + genes.size() * pixelH/Aggregation
						+ 1;
			}
			
			
			
			
			
			
			
			
			
			
			for (int j = 0; j < Genes.size(); j++) {
				blocks[i][j].min = min;
				blocks[i][j].max = max;
			
			}
			
			

			shiftX = shiftX
					+ exps.size() * pixelW + 1;

		}
		
		
		
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks [i].length; j++) {
				blocks [i][j].isSelection = isSelection;
				
			}
			
			
		}
		
		
		
this.repaint();
	}
	
	
	
	public Block getBlockInThePoint(Point e) {
		 int shiftX = 2;
		 int ii =0, jj = 0;
		  
		  for (int i = 0; i < this.Experiments.size(); i++) {
				Vector<ISelectable> exps = this.Experiments.elementAt(i);
				
				shiftX = shiftX
				+ (exps.size() * pixelW + 1);
				
				if (shiftX>e.getX()) {
					ii = i;
					break;
				}
				
		  }
		  
		  
		  int shiftY = 2;
		  
		  for (int j = 0; j < Genes.size(); j++) {
			  
				shiftY = shiftY
				+ (Genes.elementAt(j).size() * pixelH/ Aggregation + 2);
				
				if (shiftY>e.getY()) {
					jj = j;
					break;
				}
		  
		  }
		  
		  
		  
		  
		  return blocks [ii][jj];
		  
		  
		  
		  
		  
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
			
                  for (int i = 0; i < blocks.length; i++) {
                	  for (int j = 0; j < blocks[i].length; j++) {
                    	  blocks [i][j].applySelection(point1.x, point1.y, point2.x, point2.y);
                    	  
                      }
                	  
                  }
                  
              	point1 = null;
    			point2 = null;
                  
                  
            seurat.repaintWindows();
            
            
      			
		}

		this.repaint();
	}

	
	/*
	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelW;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelW;
		int y1 = Math.max(0, yy1 - upShift) * Aggregation / this.pixelH;
		int y2 = Math.max(0, yy2 - upShift) * Aggregation / this.pixelH;
		if (y1 == y2)
			y2 += Aggregation;
		if (x1 == x2)
			x2 += 1;

		for (int i = 0; i < dataManager.Experiments.size(); i++) {
			dataManager.Experiments.elementAt(i).selected = false;

		//	for (int j = 0; j < dataManager.Genes.size(); j++) {

			//	this.dataManager.Experiments.elementAt(i).isSelected[j] = false;
			//}

		}

		for (int i = 0; i < dataManager.Genes.size(); i++) {
			dataManager.Genes.elementAt(i).selected = false;

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

		
	
	
	/*
	this.repaint();

	}*/

	public void selectNode(CoordinateNode nodeC) {
		ClusterNode node = nodeC.node;
		node.selectNode();
	}

	public void mouseClicked(MouseEvent e) {

		point1 = e.getPoint();
		
		
		if (!(e.getButton() == MouseEvent.BUTTON3) && e.getClickCount() == 2) {
		
		GlobalView g = new GlobalView(seurat, "GlobalView", this.getBlockInThePoint(e.getPoint()).Experiments, this.getBlockInThePoint(e.getPoint()).Genes,false);
        g.applyNewPixelSize(pixelW,pixelH);
		}
		
	

		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
           /* Block block = this.getBlockInThePoint(e.getPoint());
			
			new ClusteringDialog(seurat,block.Genes,block.Experiments);*/
			
			
			
			
			
			
			
			
			
			
	JPopupMenu menu = new JPopupMenu();
			
			/*
			JMenuItem item = new JMenuItem("Open Selection");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					Vector<ISelectable> subGenes = new Vector();
					Vector<ISelectable> subExps = new Vector();
					
					
					for (int k = 0 ; k < Genes.size(); k++) {
					Vector<ISelectable> GeneCluster = Genes.elementAt(k);	
						
					for (int i = 0; i < GeneCluster.size(); i++) {
						if (GeneCluster.elementAt(i).isSelected()) subGenes.add(GeneCluster.elementAt(i));
					}
					}
					
					
					for (int k = 0 ; k < Experiments.size(); k++) {
						Vector<ISelectable> ExpCluster = Experiments.elementAt(k);	
						
					for (int i = 0; i < ExpCluster.size(); i++) {
						if (ExpCluster.elementAt(i).isSelected()) subExps.add(ExpCluster.elementAt(i));
					}
					
					}
					
					
					GlobalView globalView = new GlobalView(seurat, "Global View", subExps, subGenes,  false);
                   globalView.applyNewPixelSize(pixelW,pixelH);
				}
			});
			menu.add(item);
			
			
			menu.addSeparator();
			*/
			
			

			
			
			
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
						plot.applyNewPixelSize();
				
				}
				
			});
			
			
			JMenuItem item = new JMenuItem("set pixel dimension");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

				    
				    ColorDialog dialog =  new ColorDialog(seurat, (ColorListener)plot.panel, pixelW, pixelH);
				    dialog.pixelWField.addKeyListener(plot.panel);
				    dialog.pixelHField.addKeyListener(plot.panel);
				    
				    
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
		//	this.SelectionColor = Color.YELLOW;

		} else
			//this.SelectionColor = Color.BLACK;

		this.repaint();

	}

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

	//	if (seurat.settings.Model == 2)  g.setColor(Color.white);
	//	if (seurat.settings.Model == 1)  g.setColor(Color.black);
		
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		
		int colorsHight = 0;
		int shiftX = 1;
		
		
		
		//shiftX = shiftX
		//+ (exps.size() * seurat.settings.PixelSize + 1);
		
		for (int i = 0; i < Experiments.size(); i++) {
			
		    Vector<ISelectable> Exps = Experiments.elementAt(i);
		    
		    for (int jj = 0; jj < Exps.size(); jj++) {
		
			ISelectable var = (ISelectable) Exps.elementAt(jj);
            if (var.getColors() == null) break;
			
			for (int j = var.getColors().size() - 1; j >= 0; j--) {

				colorsHight = var.getColors().size() * (2 * this.pixelH + 1) + 4;

				g.setColor(var.getColors().elementAt(j));

				g.fillRect(abstandLinks + shiftX, abstandOben
						+ j * (2 * this.pixelH + 1), pixelW,
						2 * pixelH + 1);
				
			}
			shiftX += pixelW;
			
			
			
		    }
		    shiftX+= 1;
		}
		
		
		
		
		

		if (blocks == null)
			this.updateSelection();

		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j].paint(g);
			}
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

	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {

	}
	
	
	
	
	
	
	
	

	public boolean isPointInRect(int x, int y, int Rx1, int Ry1, int Rx2,
			int Ry2) {
		if ((Rx1 <= x) && (Rx2 >= x) && (Ry1 <= y) && (Ry2 >= y))
			return true;
		else
			return false;
	}	
	
	
	
	
	
	
	
	
	
	public void applyNewPixelSize(int pixelW,int pixelH) {
		this.pixelW = pixelW;
		this.pixelH = pixelH;
		plot.applyNewPixelSize(pixelW,pixelH);
	};

	public void setModel(int model) {
		Model = model;
		applyNewPixelSize();
	};
	
	public void applyNewPixelSize() {
	    applyNewPixelSize(pixelW,pixelH);	
	};
	
	
	
	
	
	
	
	
	
	@Override
	public String getToolTipText(MouseEvent e) {
		
		
		

		int colorsHight = 0;
		int shiftX = 1;
		
		
		
		//shiftX = shiftX
		//+ (exps.size() * seurat.settings.PixelSize + 1);
		
		for (int i = 0; i < Experiments.size(); i++) {
			
		    Vector<ISelectable> Exps = Experiments.elementAt(i);
		    
		    for (int jj = 0; jj < Exps.size(); jj++) {
		
			ISelectable var = (ISelectable) Exps.elementAt(jj);
            if (var.getColors() == null) break;
			
			for (int j = var.getColors().size() - 1; j >= 0; j--) {

				colorsHight = var.getColors().size() * (2 * this.pixelH + 1) + 4;

			//	return (var.getColorNames().elementAt(j));

				if (isPointInRect(e.getX(),e.getY(),
						abstandLinks + shiftX, 
						abstandOben+ j * (2 * this.pixelH + 1), 
						abstandLinks + shiftX+pixelW,
						abstandOben+ j * (2 * this.pixelH + 1)+2 * pixelH + 1))
				
				return (var.getColorNames().elementAt(j));

				
			}
			shiftX += pixelW;
			
			
			
		    }
		    shiftX+= 1;
		}
		
		
		
		

		if (e.isControlDown()) {
			
            Block block = this.getBlockInThePoint(e.getPoint());

            int x = (int)Math.floor((e.getPoint().getX()  - block.x)/pixelW);
            int y = (int)Math.floor((e.getPoint().getY()  - block.y)/pixelH);
            
            ISelectable var = block.Experiments.elementAt(x);
           
				String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";
			
				s += "<FONT FACE = 'Arial'><TR><TD>" + var.getName()
						+ "  </TD><TD> ";


				String value = "";

				double valueD = block.values [x][y]; 
				
				if (valueD == seurat.dataManager.NA) {
					value = "NA";
				} else {

					value = valueD + "";
				}

				
				s += "<FONT FACE = 'Arial'><TR><TD>" + value + "  </TD><TD> ";

				s += "</P></FONT></STRONG>";

				return s;
			}

		

		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	class Block {

		Vector<ISelectable> Experiments;
		Vector<ISelectable> Genes;
		Seurat seurat;

		int x, y;

		

		double min, max;
		
		double [][] values;
		
		
		boolean [][] isSelected;
		
		
		boolean isSelection;
		
		

		public Block(Seurat seurat, Vector<ISelectable> Experiments,
				Vector<ISelectable> Genes) {
			this.seurat = seurat;
			this.Experiments = Experiments;
			this.Genes = Genes;
		}
		
		
		
		
		public void applySelection(int xx1,int yy1, int xx2, int yy2) {
			int x1,x2,y1,y2;
			int width = Experiments.size() * pixelW;
			int height = Genes.size() * pixelH / Aggregation;
			
			
			if (xx1>x+width || xx2<x || y + height < yy1 || y >yy2) return;
			
			x1 = Math.max(x, xx1);
			x2 = Math.min(x+width, xx2);
			y1 = Math.max(y, yy1);
			y2 = Math.min(y+height, yy2);
			
			
			
			for (int i = 0; i < Experiments.size(); i++) {
				for (int j =0; j < Genes.size(); j++) {
					
					if (   x1<=(x+ i*pixelW) && x2>=(x+ i*pixelW) &&  y1<=(y+ j*pixelH/Aggregation) && y2>=(y+ j*pixelH/Aggregation) ) {
						
			//			Experiments.elementAt(i).isSelected [Genes.elementAt(j).ID] = true;
				        Experiments.elementAt(i).select(true);
				        //   select = true .......
						Genes.elementAt(j).select(true);
						
					}
					
					
				}
			}
			
		
			
			
		}
		
		
		

		public void paint(Graphics g) {
			
			
			if (Model == 2)  g.setColor(Color.white);
			if (Model == 1)  g.setColor(Color.black);
			
			g.fillRect(x-1, y-1
					, this.values.length*pixelW+2, this.values [0].length*pixelH+2);

			for (int i = 0; i < this.values.length; i++) {
				

				for (int j = 0; j < this.values [i].length; j++) {

		

					double koeff = 0;
					Color c = null;

					if (values [i][j] > 0) {

						koeff = values [i][j] / max;

						if (Model == 1)  c = Color.getHSBColor(0, (float) fPos(koeff), 1);
						if (Model == 2)  c = new Color((float) fPos(koeff), 0, 0);
						
					} else {
						koeff = values [i][j] / min;

						if (Model == 1) c = (Color.getHSBColor((float) 0.33,
								(float) fNeg(koeff), 1));
						
						if (Model == 2)  c = new Color(0, (float) fNeg(koeff), 0);
					}
					
					
					
					if (values [i][j] == dataManager.NA)
						   c = Color.WHITE;
					
					
					if (isSelection) {
					c = c.darker();
					c = c.darker();
					
					if (Model == 2) {
						c = c.darker();
						c = c.darker();
					}
					
					}
					
					
					if (isSelected [i][j]) {
						c = c.brighter();
						c = c.brighter();
						
						
						if (Model == 2) {
							c = c.brighter();
							c = c.brighter();
						}
					} 
					

					g.setColor(c);

					g.fillRect(x + i * pixelW, y + j
							* pixelH, pixelW, pixelH);

				}

			}

		}

	}


















	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		
		
int panelH = this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * this.pixelH + 2);
		
		if (arg0.getKeyCode() == 38) {
			
			int maxAggr = 0;
			for (int i = 0; i < Genes.size(); i++) {
				maxAggr = (int)Math.max(maxAggr,Genes.elementAt(i).size());
			}
			
			
			
			if (Aggregation < maxAggr) Aggregation++;
			
			
			
			
			
		    plot.infoLabel.setText("Aggregation: 1 : " + Aggregation);
		    plot.applyNewPixelSize(pixelW,pixelH);
			
			
			
		}
		
		
        if (arg0.getKeyCode() == 40) {
        	
        	
        	if (Aggregation > 1) Aggregation--;
        	
			
			plot.infoLabel.setText("Aggregation: 1 : " + Aggregation);
			plot.applyNewPixelSize();
		}
        
        
        if (arg0.getKeyCode() == 39) {
            
           pixelW++;
            plot.applyNewPixelSize(pixelW,pixelH);
        }	
        
        
        if (arg0.getKeyCode() == 37) {
            
           if (pixelW>1) pixelW--;
           plot.applyNewPixelSize(pixelW,pixelH);
         
        }
		
		
		
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}