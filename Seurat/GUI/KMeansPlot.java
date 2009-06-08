package GUI;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.*;
import Settings.*;

class KMeansPlot extends JFrame implements MatrixWindow, IPlot {
	int pixelSize = 2;

	Seurat seurat;

	KMeansPanel panel;

	// int[] orderZeilen;

	// int[] orderSpalten;

	JMenuItem item = new JMenuItem("KMeansPlot");

	KMeansPlot plot = this;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	DataManager dataManager;

	Vector<Vector<ISelectable>> Experiments;
	Vector<Vector<ISelectable>> Genes;

	int oldPixelCount;

	public void applyNewPixelSize(int size) {
		
		  this.pixelSize = size; 
		  this.panel.pixelSize = size; 
		  int col = 0;
		  
		  int shiftX = 2;
		  
		  for (int i = 0; i < this.Experiments.size(); i++) {
				Vector<ISelectable> exps = this.Experiments.elementAt(i);
				
				shiftX = shiftX
				+ (exps.size() * seurat.settings.PixelSize + panel.pixelSize*2);
		  }
		  
		  
		  int shiftY = 2;
		  
		  for (int j = 0; j < Genes.size(); j++) {
			  
				shiftY = shiftY
				+ (Genes.elementAt(j).size() * seurat.settings.PixelSize/ panel.Approx + panel.pixelSize*2);
		  
		  }
		  
		  
		  int row = Genes.size();
		  
		 panel.setPreferredSize(new Dimension(shiftX+2, shiftY+2));
		  
		  
		  
		
		  
		  
		  
		  
		  
		 if (seurat.SYSTEM == seurat.WINDOWS) {
		  
		  this .setSize( shiftX + 17, shiftY + 38 );
		   } 
		 else this .setSize(shiftX + 2,shiftY + 23 );
		 
		 updateSelection();
		 

	}

	public KMeansPlot(Seurat seurat, String name,
			Vector<Vector<ISelectable>> Experiments, Vector<Vector<ISelectable>> Genes) {
		super(name);

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.Experiments = Experiments;
		this.Genes = Genes;
		this.pixelSize = seurat.settings.PixelSize;
		this.getContentPane().setLayout(new BorderLayout());
		panel = new KMeansPanel(seurat, this, pixelSize,
				Experiments, Genes);

		this.addMouseListener(panel);
		
		
	

       		


		this.setBounds(200, 200, 800, 800);
		this.applyNewPixelSize(seurat.settings.PixelSize);

		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.setVisible(true);

		seurat.windows.add(this);

		seurat.windowMenu.add(item);

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
				
				
				  int Approx = 1;
				  boolean weiter = true;
				  
				  
				  while (weiter) {
				
					  int shiftY = 2;
					  
					  for (int j = 0; j < plot.Genes.size(); j++) {
						  
							shiftY = shiftY
							+ (plot.Genes.elementAt(j).size() * plot.seurat.settings.PixelSize / Approx + panel.pixelSize*2);
					  
					  }
                  
					  if (shiftY <= plot.getHeight()) weiter = false;
                  
					  else Approx++;
				  }
				  
				  panel.Approx = Approx;
				  
				  plot.applyNewPixelSize(panel.pixelSize);
				  

			}

		});

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

}

class KMeansPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener {
	DataManager dataManager;

	Seurat seurat;

	int pixelSize = 1;

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

	int Width = 1;

	double[][] data;

	double[] Min, Max;

	KMeansPlot plot;

	Color SelectionColor = Color.black;

	Color[][] cellColor;

	int upShift;

	boolean[][] isCellSelected;

	Block[][] blocks;

	
	
	
	int Approx = 1;

	public KMeansPanel(Seurat seurat, KMeansPlot plot, int pixelSize,
			Vector<Vector<ISelectable>> Experiments, Vector<Vector<ISelectable>> Genes) {

		System.out.println("KMeans Window...");
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.plot = plot;
		this.pixelSize = pixelSize;

		this.Experiments = Experiments;
		this.Genes = Genes;

		// this.originalOrderSpalten = orderSpalten;

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

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

		boolean isSelection = false;
		
		
		for (int i = 0; i < this.Experiments.size(); i++) {
			Vector<ISelectable> exps = this.Experiments.elementAt(i);
			
			
			boolean isMinMaxSet = false;
			double min =0, max = 0;

			int shiftY = 2;

			for (int j = 0; j < Genes.size(); j++) {

				Vector<ISelectable> genes = this.Genes.elementAt(j);

				blocks[i][j].x = shiftX;
				blocks[i][j].y = shiftY;
				
				
				int a = 0;
				if ((blocks[i][j].Genes.size()-1) % this.Approx ==0) a =1;
				
				blocks[i][j].values = new double [blocks[i][j].Experiments.size()]   [(blocks[i][j].Genes.size()-1) / this.Approx +1];
				blocks[i][j].isSelected = new boolean [blocks[i][j].Experiments.size()]   [(blocks[i][j].Genes.size()-1) / this.Approx +1];
				
			
				
				
				
				for (int ii = 0; ii < blocks[i][j].Experiments.size(); ii++) {
					for (int jj = 0; jj < blocks[i][j].Genes.size(); jj++) {
						blocks[i][j].values [ii][jj/Approx] = 0;
				//		if (blocks[i][j].Experiments.elementAt(ii).isSelected [blocks[i][j].Genes.elementAt(jj).ID]  )  {
						if (blocks[i][j].Experiments.elementAt(ii).isSelected() &&  blocks[i][j].Genes.elementAt(jj).isSelected()  )  {
							blocks[i][j].isSelected [ii][jj/Approx] = true;
							isSelection = true;
						}
					}
				}
				
				
				
				int [][] counts = new  int [blocks[i][j].Experiments.size()][blocks[i][j].Genes.size() / this.Approx+2]; 
				
				for (int ii = 0; ii < blocks[i][j].Experiments.size(); ii++) {
					for (int jj = 0; jj < blocks[i][j].Genes.size(); jj++) {
						
						if (exps.elementAt(ii).getRealValue(genes.elementAt(jj).getID()) != seurat.dataManager.NA) {
							
						
							
						blocks[i][j].values [ii][jj/this.Approx]+= 
							exps.elementAt(ii).getRealValue(genes.elementAt(jj).getID()) ;
				counts [ii][jj/this.Approx]++;
						
						            
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
				
				
				
				
				

				shiftY = shiftY + genes.size() * seurat.settings.PixelSize/Approx
						+ 1;
			}
			
			
			
			
			
			
			
			
			
			
			for (int j = 0; j < Genes.size(); j++) {
				blocks[i][j].min = min;
				blocks[i][j].max = max;
			
			}
			
			

			shiftX = shiftX
					+ (exps.size() * seurat.settings.PixelSize + 1);

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
				+ (exps.size() * seurat.settings.PixelSize + 1);
				
				if (shiftX>e.getX()) {
					ii = i;
					break;
				}
				
		  }
		  
		  
		  int shiftY = 2;
		  
		  for (int j = 0; j < Genes.size(); j++) {
			  
				shiftY = shiftY
				+ (Genes.elementAt(j).size() * seurat.settings.PixelSize/ Approx + 1);
				
				if (shiftY>e.getY()) {
					jj = j;
					break;
				}
		  
		  }
		  
		  
		  
		  
		  return blocks [ii][jj];
		  
		  
		  
		  
		  
	}
	
	
	
	

	public void mouseReleased(MouseEvent e) {

		point2 = e.getPoint();

		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
			return;
		}

		if (point1 != null && point2 != null) {
			
			dataManager.deleteSelection();
			
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

	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelSize;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelSize;
		int y1 = Math.max(0, yy1 - upShift) * Width / this.pixelSize;
		int y2 = Math.max(0, yy2 - upShift) * Width / this.pixelSize;
		if (y1 == y2)
			y2 += Width;
		if (x1 == x2)
			x2 += 1;

		
		dataManager.deleteSelection();
		
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

		this.repaint();

	}

	
	public void mouseClicked(MouseEvent e) {

		point1 = e.getPoint();
		
		new ZoomView(seurat, "ZoomView", this.getBlockInThePoint(e.getPoint()).Experiments, this.getBlockInThePoint(e.getPoint()).Genes);

		if (e.getClickCount() == 2) {
			
		}

		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

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

		if (seurat.settings.Model == 2)  g.setColor(Color.white);
		if (seurat.settings.Model == 1)  g.setColor(Color.black);
		
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		int shiftX = 20;

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
	
	
	
	
	
	
	
	
	
	
	@Override
	public String getToolTipText(MouseEvent e) {

		if (e.isControlDown()) {
			
            Block block = this.getBlockInThePoint(e.getPoint());

            int x = (int)Math.floor((e.getPoint().getX()  - block.x)/this.pixelSize);
            int y = (int)Math.floor((e.getPoint().getY()  - block.y)/this.pixelSize);
            
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
			int width = Experiments.size() * pixelSize;
			int height = Genes.size() * pixelSize / Approx;
			
			
			
			if (xx1>x+width || xx2<x || y + height < yy1 || y >yy2) return;
			
			x1 = Math.max(x, xx1);
			x2 = Math.min(x+width, xx2);
			y1 = Math.max(y, yy1);
			y2 = Math.min(y+height, yy2);
			
			
			
			for (int i = 0; i < Experiments.size(); i++) {
				for (int j =0; j < Genes.size(); j++) {
					
					if (   x1<=(x+ i*pixelSize) && x2>=(x+ i*pixelSize) &&  y1<=(y+ j*pixelSize/Approx) && y2>=(y+ j*pixelSize/Approx) ) {
						
		//				Experiments.elementAt(i).isSelected [Genes.elementAt(j).ID] = true;
						Experiments.elementAt(i).select(true);
						Genes.elementAt(j).select(true);
						
					}
					
					
				}
			}
			
		
			
			
		}
		
		
		

		public void paint(Graphics g) {

			for (int i = 0; i < this.values.length; i++) {
				

				for (int j = 0; j < this.values [i].length; j++) {

		

					double koeff = 0;
					Color c = null;

					if (values [i][j] > 0) {

						koeff = values [i][j] / max;

						if (seurat.settings.Model == 1)  c = Color.getHSBColor(0, (float) fPos(koeff), 1);
						if (seurat.settings.Model == 2)  c = new Color((float) fPos(koeff), 0, 0);
						
					} else {
						koeff = values [i][j] / min;

						if (seurat.settings.Model == 1) c = (Color.getHSBColor((float) 0.33,
								(float) fNeg(koeff), 1));
						
						if (seurat.settings.Model == 2)  c = new Color(0, (float) fNeg(koeff), 0);
					}
					
					
					
					if (values [i][j] == dataManager.NA)
						   c = Color.WHITE;
					
					
					if (isSelection) {
					c = c.darker();
					c = c.darker();
					
					if (seurat.settings.Model == 2) {
						c = c.darker();
						c = c.darker();
					}
					
					}
					
					
					if (isSelected [i][j]) {
						c = c.brighter();
						c = c.brighter();
						
						
						if (seurat.settings.Model == 2) {
							c = c.brighter();
							c = c.brighter();
						}
					} 
					

					g.setColor(c);

					g.fillRect(x + i * seurat.settings.PixelSize, y + j
							* seurat.settings.PixelSize, seurat.settings.PixelSize, seurat.settings.PixelSize);

				}

			}

		}

	}

}