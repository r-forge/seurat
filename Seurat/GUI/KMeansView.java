package GUI;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.*;
import GUI.KmeansPanel.Block;
import Settings.*;
import Tools.Tools;

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

	Vector<Cluster> Experiments;
	Vector<Cluster> Genes;

	int oldPixelCount;


    int abstandUnten = 2;
	
	long timeResized;
	
	
	
	
	

	JLabel infoLabel;
	
	JPanel infoPanel;
	
	
	public void applyNewPixelSize(int pixelW, int pixelH) {
		
	
		  this.panel.pixelW = pixelW; 
		  this.panel.pixelH = pixelH; 
		  
		  
		  int col = 0;
		  
		  int shiftX = panel.abstandLinks;
		  
		  for (int i = 0; i < this.Experiments.size(); i++) {
				Vector<ISelectable> exps = this.Experiments.elementAt(i).items;
				
				shiftX = shiftX
				+ exps.size() * pixelW + 1;
		  }
		  
		  
		  int shiftY = panel.abstandOben;
		  
		  
		  /*
		  for (int j = 0; j < Genes.size(); j++) {
			  
				shiftY = shiftY + (Genes.elementAt(j).size() * pixelH/ panel.Aggregation + 1);
		  
		  }
		  */
		  
		 
		  
		  
		  shiftY = shiftY
			+ this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * pixelH + 2);
		  
		  
		  plot.infoLabel.setText("Aggregation: 1 : " + panel.Aggregation);
		  
		  
		  
		
		
		  
		  
		 updateSelection(); 
		 
		 for (int i = 0; i < panel.blocks [0].length; i++) {
			  Block b = panel.blocks [0] [i];
			  
			  
			  int pixelC = b.Genes.items.size() /panel.Aggregation;
	            if (pixelC*panel.Aggregation < b.Genes.items.size()) pixelC++; 
	            
	            
	            
				shiftY += pixelC * pixelH + 1;
				  
		  }
		  
		 panel.setPreferredSize(new Dimension(shiftX+2 , shiftY));
			
		 
		 if (seurat.SYSTEM == seurat.WINDOWS) {
		  
		  this .setSize( shiftX + 14, (int)Math.min(800,shiftY + + infoPanel.getHeight()  + 41 +abstandUnten));
		   } else this .setSize(shiftX + 7,(int)Math.min(800,shiftY + abstandUnten + infoPanel.getHeight()  + 28));
		 
		repaint();
		 
		 
		 

	}

	public KMeansView(Seurat seurat, String name,
			Clustering c1, Clustering c2) {
		super(name);
		System.out.println("KMeansView");
		item = new JMenuItem(name);

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.Experiments = c1.clusters;
		this.Genes = c2.clusters;
		
		
		this.getContentPane().setLayout(new BorderLayout());
		panel = new KmeansPanel(seurat, this, c1, c2);

		this.addMouseListener(panel);

		


		this.setBounds(200, 200, 700, 700);
		//this.applyNewPixelSize(seurat.settings.PixelW, seurat.settings.PixelH);

		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
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
			
				   panel.image = null;
                   panel.repaint();
                   
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
				s+=Experiments.elementAt(i).items.size();
				if (i != Experiments.size()-1) s+=",";
			}
			s+="}";
			
			
			s = c1.name;
			JLabel label = new JLabel("Columns: " + s);
			label.setFont(myFont);
			infoPanel.add(label);
			
			
			
			s = "{";
			for (int i = 0; i < Genes.size(); i++) {
				s+=Genes.elementAt(i).items.size();
				if (i != Experiments.size()-1) s+=",";
			}
			s+="}";
			
			
			s = c2.name;
			label = new JLabel("   Rows: "+s+"   ");
			label.setFont(myFont);
			infoPanel.add(label);
			
			
			
			
			infoLabel.setFont(myFont);
			infoPanel.add(infoLabel);
            this.setVisible(true); 
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

	public void print() {
		// TODO Auto-generated method stub
		panel.print();
	}

}

class KmeansPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener, ColorListener, KeyListener {
	DataManager dataManager;

	Seurat seurat;
	
	int Model;

	int pixelW;
	int pixelH;

	int abstandLinks = 2;

	int abstandOben = 2;

	Clustering Genes;

	Clustering Experiments;

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

	//double[] Min, Max;

	KMeansView plot;

	Color SelectionColor = Color.black;

	Color[][] cellColor;

	int upShift;

	boolean[][] isCellSelected;

	Block[][] blocks;
	
	boolean updateBlocks = false;
	
	

	
	public boolean exclusiveSelection = true;
	
	boolean SelectionView = false;
	
	int Aggregation = 1;
	
	// Type of the global view 1 == Genexpression + SNP, 2 == CGH
	int Type;
	public final int GEXP = 1, CGH = 2;
	
	
	Vector<CoordinateNode> nodesR;

	Vector<CoordinateNode> nodesC;
	
	Image image;
    int oldWidth, oldHeight;
	
    
    

	public KmeansPanel(Seurat seurat, KMeansView plot, Clustering Experiments, Clustering Genes) {

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.plot = plot;
		

		this.Experiments = Experiments;
		this.Genes = Genes;
        if (Experiments.node != null) {
        	abstandOben = 100;
        } 
        if (Genes.node != null) {
        	
        	abstandLinks = 100;
        } 
		
		
		this.Type = GEXP; 
	    if (Genes.clusters.elementAt(0).items.elementAt(0) instanceof Clone) Type = CGH;
	    System.out.println("--> "+Type); 

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);

	}
	
	
	
	
	public boolean selected(boolean a, boolean b) {
		if (exclusiveSelection) return (a || b);
		return a&&b;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void mouseExited(MouseEvent e) {
	}

	public void updateSelection() {
		
		image = null;

		if (blocks == null || updateBlocks) {
			blocks = new Block[this.Experiments.clusters.size()][this.Genes.clusters.size()];

			for (int i = 0; i < this.Experiments.clusters.size(); i++) {
				Cluster exps = this.Experiments.clusters.elementAt(i);

				for (int j = 0; j < Genes.clusters.size(); j++) {

					Cluster genes = this.Genes.clusters.elementAt(j);

					blocks[i][j] = new Block(seurat, exps, genes);

				}

			}

			updateBlocks = false;
		}
		
		
		
		
		
       exclusiveSelection = true;
		
		boolean cols = true;

		for (int i = 0; i < this.Experiments.clusters.size(); i++) {
			Vector<ISelectable> Cluster = Experiments.clusters.elementAt(i).items;
			for (int j = 0; j < Cluster.size(); j++) {
				 if (Cluster.elementAt(j).isSelected()) cols = false;
			}	 
		}
		
		boolean rows = true;
		
		for (int i = 0; i < this.Genes.clusters.size(); i++) {
			Vector<ISelectable> Cluster = Genes.clusters.elementAt(i).items;
		    for (int j = 0; j < Cluster.size(); j++) {
			      if (Cluster.elementAt(j).isSelected()) 
				rows = false;
		    }
		}
		
		exclusiveSelection = cols || rows;
		
		
		
	
		int shiftX = abstandLinks+1;

		boolean isSelection = seurat.dataManager.isSomethingSelected();
		
		
		double min =0, max = 0;
		
		for (int i = 0; i < this.Experiments.clusters.size(); i++) {
			Vector<ISelectable> exps = this.Experiments.clusters.elementAt(i).items;
			
			
			boolean isMinMaxSet = false;
			//double min =0, max = 0;
            if (!seurat.globalScaling) {
            	min = 0;
            	max = 0;
            }
			
			int shiftY = abstandOben+1;
			
			 shiftY = shiftY
				+ this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * this.pixelH + 2);
			

			for (int j = 0; j < Genes.clusters.size(); j++) {

				Vector<ISelectable> genes = this.Genes.clusters.elementAt(j).items;

				blocks[i][j].x = shiftX;
				blocks[i][j].y = shiftY;
				
				
				int a = 0;
				if ((blocks[i][j].Genes.items.size()-1) % this.Aggregation ==0) a =1;
				
				
				int pixelC = genes.size() /Aggregation;
                if (pixelC*Aggregation < genes.size()) pixelC++; 
                
                
				
				blocks[i][j].values = new double [blocks[i][j].Experiments.items.size()]   [pixelC];
				blocks[i][j].isSelected = new boolean [blocks[i][j].Experiments.items.size()]   [pixelC];
				
			
				
				
				
				for (int ii = 0; ii < blocks[i][j].Experiments.items.size(); ii++) {
					for (int jj = 0; jj < blocks[i][j].Genes.items.size(); jj++) {
						blocks[i][j].values [ii][jj/Aggregation] = 0;
						//if (blocks[i][j].Experiments.elementAt(ii).isSelected [blocks[i][j].Genes.elementAt(jj).ID]  )  {
						if (selected(blocks[i][j].Experiments.items.elementAt(ii).isSelected() ,blocks[i][j].Genes.items.elementAt(jj).isSelected()  ))  {
							
						blocks[i][j].isSelected [ii][jj/Aggregation] = true;
						//	isSelection = true;
						}
					}
				}
				
				
				
				int [][] counts = new  int [blocks[i][j].Experiments.items.size()][blocks[i][j].Genes.items.size() / this.Aggregation+2]; 
				
				for (int ii = 0; ii < blocks[i][j].Experiments.items.size(); ii++) {
					for (int jj = 0; jj < blocks[i][j].Genes.items.size(); jj++) {
						
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
							
							//if (isMinMaxSet) {
								
							if (min > blocks[i][j].values [ii] [jj]) min = blocks[i][j].values [ii] [jj];
							if (max < blocks[i][j].values [ii] [jj]) max = blocks[i][j].values [ii] [jj];
							//}
							//else {
								//min = blocks[i][j].values [ii] [jj];
								//max = blocks[i][j].values [ii] [jj];
								//isMinMaxSet = true;
							//}
							
							
						}
						else blocks[i][j].values [ii] [jj] = seurat.dataManager.NA;
					}
					
					
				}
				
				
				
				
				
				
				
				shiftY = shiftY + pixelC * pixelH + 1;
			}
			
			
			
			
			
			
			
			
			
			if (!seurat.globalScaling) { 
			for (int j = 0; j < Genes.clusters.size(); j++) {
				blocks[i][j].min = min;
				blocks[i][j].max = max;
			
			}
			}
			

			shiftX = shiftX	+ exps.size() * pixelW + 1;

		}
		
		
		if (seurat.globalScaling) {
			for (int i = 0; i < Experiments.clusters.size(); i++) {
			for (int j = 0; j < Genes.clusters.size(); j++) {
				blocks[i][j].min = min;
				blocks[i][j].max = max;
			
			}
			}
		}
		
		
		
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks [i].length; j++) {
				blocks [i][j].isSelection = isSelection;
				
			}
			
			
		}
		
	
this.repaint();
	}
	
	
	
	public Block getBlockInThePoint(Point e) {
		
		int shiftX = abstandLinks + 1;
		 int ii =0, jj = 0;
		  
		  for (int i = 0; i < this.Experiments.clusters.size(); i++) {
				Vector<ISelectable> exps = this.Experiments.clusters.elementAt(i).items;
				
				shiftX = shiftX + (exps.size() * pixelW + 1);
				
				if (shiftX>e.getX()) {
					ii = i;
					break;
				}
				
		  }
		  
		  
		  int shiftY = abstandOben+1;
			
		 shiftY = shiftY
				+ this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * this.pixelH + 2);
		
		  
		  for (int j = 0; j < Genes.clusters.size(); j++) {
			  
				shiftY = shiftY + (Genes.clusters.elementAt(j).items.size() * pixelH/ Aggregation + 2);
				
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
			
			if (!e.isShiftDown()) seurat.dataManager.deleteSelection();
			
            for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                 blocks [i][j].applySelection(point1.x, point1.y, point2.x, point2.y);
                    	  
            }    	  
            }
             
            
            if (Math.max(point1.x, point2.x) < this.abstandLinks
 				   || Math.max(point1.y, point2.y) < this.abstandOben) {

 						selectInTree(point1.x, point1.y, point2.x, point2.y);
 						point1 = null;
 						point2 = null;
 					
 			}

              	point1 = null;
    			point2 = null;
                  
                  
            seurat.repaintWindows();
            
            
      			
		}

		this.repaint();
	}

	
	
	
	public void selectInTree(int xx1, int yy1, int xx2, int yy2) {

		boolean Spalten = false;

		for (int i = 0; i < this.nodesC.size(); i++) {
			CoordinateNode nd = nodesC.elementAt(i);
			for (int j = 0; j < nd.Lines.size(); j++) {
				Line line = nd.Lines.elementAt(j);
				if (Tools.containsLineInRect(line.x1, line.y1,
						line.x2, line.y2, xx1, yy1, xx2, yy2)) {

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
				if (Tools.containsLineInRect(line.x1, line.y1, line.x2,
						line.y2, xx1, yy1, xx2, yy2)) {

					nd.node.selectNode();

					Zeilen = true;
				}
			}
		}

		if (Zeilen && !Spalten) {
			for (int i = 0; i < Experiments.clusters.size(); i++) {
				Experiments.clusters.elementAt(i).select();
			}
		}

		if (!Zeilen && Spalten) {
			for (int i = 0; i < Genes.clusters.size(); i++) {
				Genes.clusters.elementAt(i).select();
			}
		}

		this.updateSelection();
		repaint();

	}
	
	
	

	public void selectNode(CoordinateNode nodeC) {
		ClusterNode node = nodeC.node;
		node.selectNode();
	}

	public void mouseClicked(MouseEvent e) {

		point1 = e.getPoint();
		
		
		if (!(e.getButton() == MouseEvent.BUTTON3) && e.getClickCount() == 2) {	
		GlobalView g = new GlobalView(seurat, "GlobalView", this.getBlockInThePoint(e.getPoint()).Experiments.items, this.getBlockInThePoint(e.getPoint()).Genes.items);
        g.applyNewPixelSize(pixelW,pixelH);
		}
		
	

		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
      
			
			
			
			
			
			
			
			
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
			
			
			box = new JCheckBoxMenuItem(
			"selection view");
	if (SelectionView)
		box.setSelected(true);
	else
		box.setSelected(false);
	menu.add(box);

	box.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JCheckBoxMenuItem box = (JCheckBoxMenuItem) e.getSource();
			if (box.isSelected()) {
				SelectionView = true;
			} else
				SelectionView = false;
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
			
			
			
			item = new JMenuItem("set aggregation");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

				    
				    AggregationDialog dialog =  new AggregationDialog(seurat, plot.panel, Aggregation);
				    dialog.field.addKeyListener(plot.panel);
				   
				    
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
		//	this.SelectionColor = Color.YELLOW;

		} else
			//this.SelectionColor = Color.BLACK;

		this.repaint();

	}

    public void paint(Graphics graphics) {

		
		
		if (image == null || oldWidth != this.getWidth() || oldHeight != this.getHeight() ) {

		oldWidth = this.getWidth();
		oldHeight = this.getHeight();			
	 	image = this.createImage(this.getWidth(),this.getHeight());
		
		
		Graphics g = image.getGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		int shiftX = 1;
		
		
		if (Experiments == null) return; 
		
		for (int i = 0; i < Experiments.clusters.size(); i++) {
			
		    Vector<ISelectable> Exps = Experiments.clusters.elementAt(i).items;
		    
		    for (int jj = 0; jj < Exps.size(); jj++) {
		
			ISelectable var = (ISelectable) Exps.elementAt(jj);
            if (var.getColors() == null) break;
			
			for (int j = var.getColors().size() - 1; j >= 0; j--) {

			//	colorsHight = var.getColors().size() * (2 * this.pixelH + 1) + 4;

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
		
		calculateTree();
		paintClustering(g);
		


		}
		graphics.drawImage(image,0,0,this.getWidth(), this.getHeight(),this);
		
		if (point1 != null && point2 != null) {
	        if (Model == 1) graphics.setColor(Color.BLACK);
	        else graphics.setColor(Color.WHITE);
			
	        graphics.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math
					.abs(point2.y - point1.y));
	
			
			
		}

	}

	
	
	public void paintClustering(Graphics g) {

		if (nodesR != null) {

			for (int i = 0; i < nodesR.size(); i++) {
				CoordinateNode node = nodesR.elementAt(i);

				if (node.isSelected)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				for (int j = 0; j < node.Lines.size(); j++) {
					Line line = node.Lines.elementAt(j);
					g.drawLine(line.x1, line.y1, line.x2, line.y2);
				}

			}

		}

		if (nodesC != null) {

		//	System.out.println("Paint Columns...");

			for (int i = 0; i < nodesC.size(); i++) {
				CoordinateNode node = nodesC.elementAt(i);

				if (node.isSelected)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);

				for (int j = 0; j < node.Lines.size(); j++) {
					Line line = node.Lines.elementAt(j);
					g.drawLine(line.x1, line.y1, line.x2, line.y2);
				}

			}

		}

	}

	
	
	
	
	
	
	public void calculateTree() {
		this.nodesC = new Vector();
		this.nodesR = new Vector();		
		nodeZeilen = Genes.node;
		nodeSpalten = Experiments.node;

		if (nodeZeilen != null)  calculateClusteringRows(nodeZeilen);
		if (nodeSpalten != null) calculateClusteringColumns(nodeSpalten);	
	}

	
	

	public void calculateClusteringRows(ClusterNode node) {

		if (node.nodeL != null && node.nodeR != null && !node.isLeaf) {

			int pos = getYCoordinate(node)-abstandOben;
			int x = (int) Math.round(this.abstandLinks - this.abstandLinks
					* node.currentHeight);

			if (node.nodeL != null) {

				int posL = getYCoordinate(node.nodeL)-abstandOben;
				int xL = (int) Math.round(this.abstandLinks - this.abstandLinks
						* node.nodeL.currentHeight);

				CoordinateNode nodeC = new CoordinateNode(node.nodeL, x, posL,
						xL, posL);
				nodeC.Lines.add(new Line(x, pos, x, posL));
				nodeC.Lines.add(new Line(x, posL, xL, posL));

				node.nodeL.cNode = nodeC;

				if (node.nodeL.isSelected())
					nodeC.isSelected = true;

				this.nodesR.add(nodeC);

				calculateClusteringRows(node.nodeL);

			}

			if (node.nodeR != null) {

				int posR = getYCoordinate(node.nodeR)-abstandOben;
				int xR = (int) Math.round(this.abstandLinks - this.abstandLinks
						* node.nodeR.currentHeight);

				CoordinateNode nodeC = new CoordinateNode(node.nodeR, x, posR,
						xR, posR);
				nodeC.Lines.add(new Line(x, pos, x, posR));
				nodeC.Lines.add(new Line(x, posR, xR, posR));
				this.nodesR.add(nodeC);

				node.nodeR.cNode = nodeC;

				if (node.nodeR.isSelected())
					nodeC.isSelected = true;

				calculateClusteringRows(node.nodeR);
			}

		}
		else {
			int pos = getYCoordinate(node) -abstandOben;
			int x = (int) Math.round(this.abstandLinks - this.abstandLinks * node.currentHeight);

			CoordinateNode nodeC = node.cNode;
			if(nodeC != null) nodeC.Lines.add(new Line(x, pos, abstandLinks, pos));
			
		}

	}

	public void updateClustering(ClusterNode node) {

		if (node.isSelected()) {
			if (node.cNode != null)
				node.cNode.isSelected = true;
		} else {
			if (node.cNode != null)
				node.cNode.isSelected = false;
		}

		if (node.nodeL != null && node.nodeR != null && !node.isLeaf) {

			if (node.nodeL != null) {
				updateClustering(node.nodeL);

			}

			if (node.nodeR != null) {
				updateClustering(node.nodeR);

			}

		}

	}



	public void calculateClusteringColumns(ClusterNode node) {

		if (node.nodeL != null && node.nodeR != null && !node.isLeaf) {

			int pos = getXCoordinate(node) - abstandLinks;
			int y = (int) Math.round(this.abstandOben - this.abstandOben
					* node.currentHeight);

			if (node.nodeL != null) {

				int posL = getXCoordinate(node.nodeL) - abstandLinks;
				int yL = (int) Math.round(this.abstandOben - this.abstandOben
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

				int posR = getXCoordinate(node.nodeR) - abstandLinks;
				int yR = (int) Math.round(this.abstandOben - this.abstandOben
						* node.nodeR.currentHeight);

				CoordinateNode nodeC = new CoordinateNode(node.nodeR, pos, y,
						posR, y);
				nodeC.Lines.add(new Line(posR, y, posR, yR));
				// nodeC.Lines.add(new Line(pos, y, posR, yR));
				this.nodesC.add(nodeC);

				node.nodeR.cNode = nodeC;

				if (node.nodeR.isSelected())
					nodeC.isSelected = true;

				calculateClusteringColumns(node.nodeR);
			}

		}
		else {

			int pos = getXCoordinate(node) - abstandLinks;
			int y = (int) Math.round(this.abstandOben - this.abstandOben * node.currentHeight);
			CoordinateNode nodeC = node.cNode;
			if(nodeC != null) nodeC.Lines.add(new Line(pos, y, pos, abstandOben));

		}

	}

	
	
	
	public int getLeafYCoordinate(ClusterNode node) {
	   	 Vector<ClusterNode> nodes = Genes.node.getLeafList();
		 int index = nodes.indexOf(node); 
		 return abstandOben + blocks [0][index].y + blocks [0][index].values [0].length* pixelH/2;
	}
	
	public int getLeafXCoordinate(ClusterNode node) {
		 Vector<ClusterNode> nodes = Experiments.node.getLeafList();
		 int index = nodes.indexOf(node);
		 return abstandLinks + blocks [index] [0].x + blocks [index] [0].values.length* pixelW/2;
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
		
		
		

		int shiftX = 1;
		
		
		
		//shiftX = shiftX
		//+ (exps.size() * seurat.settings.PixelSize + 1);
		if (Experiments == null) return null;
 		
		for (int i = 0; i < Experiments.clusters.size(); i++) {
			
		    Vector<ISelectable> Exps = Experiments.clusters.elementAt(i).items;
		    
		    for (int jj = 0; jj < Exps.size(); jj++) {
		
			ISelectable var = (ISelectable) Exps.elementAt(jj);
            if (var.getColors() == null) break;
			
			for (int j = var.getColors().size() - 1; j >= 0; j--) {

				if (isPointInRect(e.getX(),e.getY(),
						abstandLinks + shiftX, 
						abstandOben+ 1+j * (2 * this.pixelH + 1), 
						abstandLinks + shiftX+pixelW,
						abstandOben+ 1+j * (2 * this.pixelH + 1)+2 * pixelH + 1))
				
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
            
       
            
            ISelectable var = block.Experiments.items.elementAt(x);
           
				String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";
			
				s += "<FONT FACE = 'Arial'><TR><TD>" + var.getName()
						+ "  </TD><TD> ";
				
				Vector<DescriptionVariable> Vars = seurat.getSelectedDescriptionVariables();
				
				for (int i = 0; i < Vars.size(); i++) if (var instanceof Variable) s += "<FONT FACE = 'Arial'><TR><TD>"+Vars.elementAt(i).name+ " " + Vars.elementAt(i).getStringData() [var.getID()] + "  </TD><TD> ";
				
				if (Aggregation == 1) {
					 ISelectable gene = block.Genes.items.elementAt(x);
					 Vector<GeneVariable> geneVars = seurat.getSelectedGeneVariables();
					 for (int i = 0; i < geneVars.size(); i++) {
							if (gene instanceof Gene && ((Gene)gene).annGene != null) s += "<FONT FACE = 'Arial'><TR><TD>"+geneVars.elementAt(i).name+ " " + geneVars.elementAt(i).getStringData(((Gene)gene).annGene.ID) + "  </TD><TD> ";
					 }				
				}


				String value = "";
                double valueD = block.values [x][y]; 
				
				if (valueD == seurat.dataManager.NA) value = "NA";
				else value = valueD + "";
			
				s += "<FONT FACE = 'Arial'><TR><TD>value: " + value + "  </TD><TD> ";
				s += "</P></FONT></STRONG>";
				return s;
			}

		

		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	class Block {

		Cluster Experiments;
		Cluster Genes;
		Seurat seurat;

		int x, y;

		

		double min, max;
		
		double [][] values;
		
		
		boolean [][] isSelected;
		
		
		boolean isSelection;
		
		int pixelC;
		

		public Block(Seurat seurat, Cluster Experiments,
				Cluster Genes) {
			this.seurat = seurat;
			this.Experiments = Experiments;
			this.Genes = Genes;
		}
		
		
		
		
		public void applySelection(int xx1,int yy1, int xx2, int yy2) {
			int x1,x2,y1,y2;
			int width = Experiments.items.size() * pixelW;
			
			pixelC = Genes.items.size() /Aggregation;
            if (pixelC*Aggregation < Genes.items.size()) pixelC++; 
			
			
			int height = pixelC * pixelH;
			
			
			
			if (xx1>x+width || xx2<x || y + height < yy1 || y >yy2) return;
			
			x1 = Math.max(x, xx1);
			x2 = Math.min(x+width, xx2);
			y1 = Math.max(y, yy1);
			y2 = Math.min(y+height, yy2);
			
			
			
			for (int i = 0; i < Experiments.items.size(); i++) {
				for (int j =0; j < Genes.items.size(); j++) {
					
				//	if (   x1<=(x+ i*pixelW) && x2>=(x+ i*pixelW) &&  y1<=(y+ j*pixelH/Aggregation) && y2>=(y+ j*pixelH/Aggregation) ) {
						
						
					if	(Tools.containsRectInRect(x1, y1, x2, y2, 
							x+ i*pixelW,	y+ (j/Aggregation)*pixelH,  x+ (i+1)*pixelW,	y+ (j/Aggregation+1)*pixelH)) {	
						
			//			Experiments.elementAt(i).isSelected [Genes.elementAt(j).ID] = true;
				        Experiments.items.elementAt(i).select(true);
				        //   select = true .......
						Genes.items.elementAt(j).select(true);
						
					}
					
					
				}
			}
			
		
			
			
		}
		
		
		

		public void paint(Graphics g) {
			
			
			if (Model == 2)  g.setColor(Color.white);
			if (Model == 1)  g.setColor(Color.black);
			if (Model == 2 && SelectionView)  g.setColor(Color.YELLOW);
			
			g.fillRect(x-1, y-1
					, this.values.length*pixelW+2, this.values [0].length*pixelH+2);

			for (int i = 0; i < this.values.length; i++) {
				

				for (int j = 0; j < this.values [i].length; j++) {

		

					double koeff = 0;
					Color c = null;

					if (!SelectionView) {
					if (values [i][j] > 0) {

						koeff = values [i][j] / max;

						if (Model == 1) c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin),Tools.fPos(koeff)*Settings.Cmax, seurat.PosColor )).getRGBColor();
						if (Model == 2) c = Tools.convertHLCtoRGB(new MyColor(Settings.LSmin   +   Tools.fPos(koeff)*(Settings.Lmax-Settings.LSmin), Settings.Cmin + (Tools.fPos(koeff))*(Settings.Cmax-Settings.Cmin), seurat.PosColor )).getRGBColor();
	
						
						
					} else {
						koeff = values [i][j] / min;
						if (min == 0) koeff = 0;

						if (Model ==1) c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), Tools.fNeg(koeff)*Settings.Cmax, seurat.NegColor)).getRGBColor();
						if (Model == 2) c = Tools.convertHLCtoRGB(new MyColor(Settings.LSmin+ Tools.fNeg(koeff)*(Settings.Lmax-Settings.LSmin), Settings.Cmin+ (Tools.fNeg(koeff))*(Settings.Cmax-Settings.Cmin), seurat.NegColor)).getRGBColor();
						
														
								
					}
					} 
					else c = Color.WHITE;
					
					
					
					
					if (values [i][j] == dataManager.NA) c = seurat.NAColor;
					
					
					
					
					
					if (isSelected [i][j] && !SelectionView) {
						
						
						if (values [i][j] > 0) {
							if (Model == 1 && Type == GEXP) 	c = Tools.convertHLCtoRGB(new MyColor(
									(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection),
									Tools.fPos(koeff)*Settings.Cmax*Settings.Selection, 
									seurat.PosColor 
							)).getRGBColor();	
							
							
							if (Model == 1 && Type == CGH) 	
								 c = Tools.convertHLCtoRGB(new MyColor(
								    		(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin*2)),
								    		Tools.fPos(koeff)*Settings.Cmax, 
								    		seurat.CGHPosColor 
								    )).getRGBColor();	
							
							if (Model ==2) c = Tools.convertHLCtoRGB(new MyColor(Settings.LSmin   + Tools.fPos(koeff)*(Settings.Lmax-Settings.LSmin) + Settings.Selection*(Settings.Lmax-Settings.LSmin-Tools.fPos(koeff)*(Settings.Lmax-Settings.LSmin)), (100 - Tools.fPos(koeff)*Settings.Cmax)*Settings.Selection + Tools.fPos(koeff)*Settings.Cmax, seurat.PosColor )).getRGBColor();
							if (Model ==2 && Type == CGH) c = Tools.convertHLCtoRGB(new MyColor(
									Settings.LSmin   + Tools.fPos(koeff)*(Settings.Lmax-Settings.LSmin) + Settings.Selection*(Settings.Lmax-Settings.LSmin-Tools.fPos(koeff)*(Settings.Lmax-Settings.LSmin)), 
									(100 - Tools.fPos(koeff)*Settings.Cmax)*Settings.Selection + Tools.fPos(koeff)*Settings.Cmax, 
									seurat.CGHPosColorMode2
							)).getRGBColor();
							
						
						
						}
						else {
							if (Model == 1) 	c = Tools.convertHLCtoRGB(new MyColor((Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin))*(1-Settings.Selection), Tools.fNeg(koeff)*Settings.Cmax*Settings.Selection, seurat.NegColor)).getRGBColor();
							if (Model == 1  && Type == CGH)  
								 c = Tools.convertHLCtoRGB(new MyColor((
							               Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-2*Settings.Lmin)), 
							               Tools.fNeg(koeff)*Settings.Cmax, 
							               seurat.CGHNegColor
							         )).getRGBColor();
							
							if (Model ==2)      c = Tools.convertHLCtoRGB(new MyColor(Settings.LSmin+Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin)+Settings.Selection*(Settings.Lmax-Settings.LSmin-Tools.fNeg(koeff)*(Settings.Lmax-Settings.LSmin)), (100 - Tools.fNeg(koeff))*Settings.Cmax*Settings.Selection + Tools.fNeg(koeff)*Settings.Cmax, seurat.NegColor)).getRGBColor();
							
						}
						
						
						
					} 
					
					
					
					
                    if (isSelected [i][j] && SelectionView) {
						
						
                    	if (values [i][j] > 0) {

    						koeff = values [i][j] / max;

    						if (Model == 1) c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin),Tools.fPos(koeff)*Settings.Cmax, seurat.PosColor )).getRGBColor();
    						if (Model == 2) c = Tools.convertHLCtoRGB(new MyColor(Settings.LSmin   +   Tools.fPos(koeff)*(Settings.Lmax-Settings.LSmin), Settings.Cmin + (Tools.fPos(koeff))*(Settings.Cmax-Settings.Cmin), seurat.PosColor )).getRGBColor();
    	
    						
    						
    					} else {
    						koeff = values [i][j] / min;
    						if (min == 0) koeff = 0;

    						if (Model ==1) c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), Tools.fNeg(koeff)*Settings.Cmax, seurat.NegColor)).getRGBColor();
    						if (Model == 2) c = Tools.convertHLCtoRGB(new MyColor(Settings.LSmin+ Tools.fNeg(koeff)*(Settings.Lmax-Settings.LSmin), Settings.Cmin+ (Tools.fNeg(koeff))*(Settings.Cmax-Settings.Cmin), seurat.NegColor)).getRGBColor();
    						
    														
    								
    					}
						
						
					} 
					
					
					
					

					g.setColor(c);

					g.fillRect(x + i * pixelW, y + j
							* pixelH, pixelW, pixelH);

				}

			}

		}

		
		
		
		
		
		/*
		 * 
		

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

						if (Model == 1)  c = Color.getHSBColor(0, (float) Tools.fPos(koeff), 1);
						if (Model == 2)  c = new Color((float) Tools.fPos(koeff), 0, 0);
						
					} else {
						koeff = values [i][j] / min;
						if (min == 0) koeff = 0;

						if (Model == 1) c = (Color.getHSBColor((float) 0.33,
								(float) Tools.fNeg(koeff), 1));
						
						if (Model == 2)  c = new Color(0, (float) Tools.fNeg(koeff), 0);
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
		 * 
		 * **/
	}












	public void decreaseClustering(Clustering Experiments) {
		if (Experiments.node == null) return;
		if (Experiments.node.getLeafList().size()<=1) return;
		updateBlocks = true;
		Vector<ClusterNode> nodes = Experiments.node.getFathersOfLeafList();
		if (nodes.size() == 0) return;
		
		int max = -1;
		double maxHeight = -1;
		
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.elementAt(i).currentHeight > maxHeight) {
				maxHeight = nodes.elementAt(i).currentHeight;
				max = i;
			}		
		}
		
		//System.out.println(nodes.size() + "  ___  " + max);
		
		if (max != -1) {
			ClusterNode toClose = nodes.elementAt(max);
			toClose.isLeaf = true;
			
			nodes = Experiments.node.getLeafList();
			//System.out.println(nodes.size() + "  ___  " + max);
			Vector<Cluster> clusters = new Vector();
			for (int i = 0; i < nodes.size(); i++) {
				clusters.add(nodes.elementAt(i).cluster);
				nodes.elementAt(i).cluster.name = i+"";
			}
			Experiments.clusters = clusters;
		}
		
	} 
	
	
	
	
	
	public void increaseClustering(Clustering Experiments) {
		
		if (Experiments.node == null) return;
		updateBlocks = true;
		
		Vector<ClusterNode> nodes = Experiments.node.getLeafList();
		int max = -1;
		double maxHeight = -1;
		
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.elementAt(i).currentHeight > maxHeight && nodes.elementAt(i).nodeR != null) {
				maxHeight = nodes.elementAt(i).currentHeight;
				max = i;
			}		
		}
		
		if (max != -1) {
			ClusterNode toExpand = nodes.elementAt(max);
			toExpand.isLeaf = false;
			toExpand.nodeR.isLeaf = true;
			toExpand.nodeL.isLeaf = true;
			nodes = Experiments.node.getLeafList();
			Vector<Cluster> clusters = new Vector();
			for (int i = 0; i < nodes.size(); i++) {
				clusters.add(nodes.elementAt(i).cluster);
				nodes.elementAt(i).cluster.name = i+"";
			}
			Experiments.clusters = clusters;
			
		}
		
	} 
	
	
	





	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.isControlDown()) {
		
			if (arg0.getKeyCode() == 37) decreaseClustering(Genes); 
			if (arg0.getKeyCode() == 39) increaseClustering(Genes); 
			if (arg0.getKeyCode() == 38) increaseClustering(Experiments); 
			if (arg0.getKeyCode() == 40) decreaseClustering(Experiments); 
		
			updateSelection();	
			
			
		}
		else {
		
		
        int panelH = this.dataManager.Experiments.elementAt(0).getBarchartToColors().size() * (2 * this.pixelH + 2);
		
  

		if (arg0.getKeyCode() == 38) {
			
			 if (pixelH>1) pixelH--; plot.applyNewPixelSize(pixelW,pixelH);	           
			
		}
		
		
        if (arg0.getKeyCode() == 40) {
        	
        	pixelH++;
            plot.applyNewPixelSize(pixelW,pixelH);
            
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
		
		
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	

	public void setAggregation(int aggr) {
		// TODO Auto-generated method stub
		this.Aggregation = aggr;
		applyNewPixelSize();
		
	}

	public void print() {
		// TODO Auto-generated method stub
		try {
			   PrintJob prjob = getToolkit().getPrintJob( plot,null, null );
			   Graphics pg = prjob.getGraphics();
			   paint(pg);
			   pg.dispose();
			   prjob.end();
			   }
			   catch (Exception e) {
				   e.printStackTrace();
			   } 
	}

}