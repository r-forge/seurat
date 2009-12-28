package GUI;

import java.util.*;
import Data.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.Variable; //import org.rosuda.JRclient.*;



import Data.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.Variable; //import org.rosuda.JRclient.*;

class CorrelationFrame extends JFrame implements MatrixWindow, IPlot {
	int pixelSize = 1;

	Seurat seurat;

	JMenuItem item = new JMenuItem("");

	CorrelationFrame correlationFrame = this;

	boolean isVariables;

	// int[] order;

	double[][] data;

	CorrelationPanel panel;

	DataManager dataManager;

	Vector<ISelectable> Genes;
	Vector<ISelectable> Experiments;

	public CorrelationFrame(Seurat seurat, Vector Genes,
			Vector Experiments, int Width, boolean isColumns,
			String name, int size) {

		super(name);

		this.seurat = seurat;
		seurat.windows.add(this);
		item = new JMenuItem(name);
		this.isVariables = isColumns;

		this.Genes = Genes;
		this.Experiments = Experiments;

		this.pixelSize = size;

		this.dataManager = seurat.dataManager;

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				correlationFrame.setVisible(true);
			}
		});

		this.getContentPane().setLayout(new BorderLayout());
		
		
		panel = new CorrelationPanel(seurat, this,Genes, Experiments, Width,
				isColumns,size);

		
		int length = 0;
		if (isColumns)
			length = Experiments.size() * pixelSize;
		else
			{
			     length = (Genes.size()  / Width) * pixelSize;
			     if (Genes.size()%Width!=0) length+=pixelSize;
			}

		
		
		
		if (seurat.SYSTEM == seurat.WINDOWS) {

			this.setSize( length  + 17,
					      length + 38);

		} else
			this.setSize(length +1,
					     length + 23);

		
		
		
		panel.setSize(length, length);
		panel.setPreferredSize(new Dimension(length, length));

		this.getContentPane().add(panel, BorderLayout.CENTER);

		
		int Aggregation = Width;
		length = Genes.size()/Aggregation*pixelSize;
		if (isVariables) length = Experiments.size()/Aggregation * pixelSize;
		
		panel.PixelCount = Math.max(Math.min(Math.min(this.getWidth(), this.getHeight())
				/ pixelSize, length),1);

		// System.out.println(count + " " + PixelCount);
 
		panel.Aggregation = Aggregation;
		
		panel.calculateCorrs();
		
		
		panel.calculateCorrs();

		for (int i = 0; i < panel.data.length; i++) {
			for (int j = 0; j < panel.data.length; j++) {
				if (panel.data[i][j] != dataManager.NA
						&& panel.data[i][j] > panel.max)
					panel.max = panel.data[i][j];
				if (panel.data[i][j] < panel.min)
					panel.min = panel.data[i][j];
			}
		}

		this.setVisible(true);

		seurat.windows.add(this);

		seurat.windowMenu.add(item);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				correlationFrame.seurat.windowMenu.remove(item);
			}
		});
		
		this.addKeyListener(panel);

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {

				
				
				
				
				//panel.calculateCorrs();
				//repaint();
				//panel.repaint();

			}
		});
	}

	public void applyNewPixelSize(int size) {

		this.pixelSize = size;
		panel.pixelSize = size;
	}

	public void updateSelection() {
		// TODO Auto-generated method stub
		panel.calculateSelection();
		this.repaint();
	}

	public void brush() {
		// TODO Auto-generated method stub
		
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		
	}

	public void applyNewPixelSize(int pixelW, int PixelH) {
		// TODO Auto-generated method stub
		
	}

	public void applyNewPixelSize() {
		// TODO Auto-generated method stub
		this.applyNewPixelSize(pixelSize);
	}

	public void setModel(int model) {
		// TODO Auto-generated method stub
		
	}

	public void print() {
		// TODO Auto-generated method stub
		try {
			   PrintJob prjob = getToolkit().getPrintJob( this,null, null );
			   Graphics pg = prjob.getGraphics();
			   panel.paint(pg);
			   pg.dispose();
			   prjob.end();
			   }
			   catch (Exception e) {
				   e.printStackTrace();
			   } 
	}

}

class CorrelationPanel extends JPanel implements MouseListener,
		MouseMotionListener,ColorListener,KeyListener {

	Seurat seurat;

	double[][] data;

	int abstandLinks = 1;

	int abstandOben = 1;

	double max = 0, min = 0;

	boolean isVariables;

	Point point1, point2;

	boolean[] selection;

	int Aggregation;

	// double [][] dataG;

	double[][] corr;

	DataManager dataManager;

	Vector<ISelectable> Genes;
	Vector<ISelectable> Experiments;

	int pixelSize;
	
	int PixelCount;
	
	CorrelationFrame frame;

	public CorrelationPanel(Seurat seurat, CorrelationFrame frame, Vector Genes,
			Vector Experiments, int Width, boolean isColumns, int size) {

		this.seurat = seurat;
		this.frame = frame;
		this.Aggregation = Width;
		this.Genes = Genes;
		this.dataManager = seurat.dataManager;
		this.isVariables = isColumns;
		this.pixelSize = size;

		this.Experiments = Experiments;

		dataManager = seurat.dataManager;

		this.isVariables = isColumns;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

	}

	public void calculateCorrs() {

		double data[][] = null;

		if (isVariables) {

			data = new double[Experiments.size()][Genes.size()];

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					data[i][j] = Experiments.elementAt(i).getValue(
							Genes.elementAt(j).getID());
				}
			}

		} else {
			data = new double[Genes.size()][Experiments.size()];

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					data[i][j] = Experiments.elementAt(j).getValue(
							Genes.elementAt(i).getID());
				}
			}
		}

		if (corr == null) {
			corr = new double[data.length][data.length];
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
		}

		int count = data.length;

		
		/*
		int PixelCount = Math.max(Math.min(Math.min(this.getWidth(), this.getHeight())
				/ pixelSize, count),1);

		// System.out.println(count + " " + PixelCount);
 
		Aggregation = data.length / PixelCount;
		if (Aggregation * PixelCount < data.length) Aggregation++;
		PixelCount = data.length / Aggregation;
		
		
		  if ( (count - PixelCount * Aggregation)> Aggregation) { Aggregation++; PixelCount =
		  count / Aggregation; }
		 */
		
		
        System.out.println("PixelCount  " + PixelCount);
		
		
		double[][] correlations = new double[PixelCount][PixelCount];
		for (int i = 0; i < correlations.length; i++) {
			for (int j = 0; j < correlations.length; j++) {
				count = 0;
				for (int ii = i * this.Aggregation; ii < Math.min((i + 1) * this.Aggregation, data.length); ii++) {
					for (int jj = j * this.Aggregation; jj < Math.min((j + 1)	* this.Aggregation, data.length); jj++) {
						correlations[i][j] += corr[ii][jj];
						count++;
					}
				}
				correlations[i][j] /= count;

			}

		}

		this.data = correlations;

		this.calculateSelection();
	}

	public void calculateSelection() {
		
		
		
		

		this.selection = new boolean[PixelCount];

		for (int i = 0; i < selection.length; i++) {
			selection[i] = false;
		}

		if (this.isVariables) {

			for (int i = 0; i < Experiments.size(); i++) {
				ISelectable var = Experiments.elementAt(i);
				// if (var.isSelected()) selection [order [i-2]] = true;
				if (Experiments.elementAt(i).isSelected())
					selection[i/Aggregation] = true;
				
			}
		} else {
			for (int i = 0; i < Genes.size(); i++) {
				// if (amlTool.isRowSelected(i)) selection [order [i]] = true;
				if (Genes.elementAt(i).isSelected())
					selection[i / Aggregation] = true;

			}
		}

		this.repaint();
	}

	public void addSelection(int start, int end) {
		this.selection = new boolean[data.length];
		this.dataManager.deleteSelection();

		
		

		if (this.isVariables) {
			
			
			for (int i = 0; i < Experiments.size(); i++) {
				Experiments.elementAt(i).unselect(true);
			}

			for (int i = 0; i < Experiments.size(); i++) {
				ISelectable var = Experiments.elementAt(i);
				if (i < end* Aggregation && start* Aggregation <= i) {
					var.select(true);
				} else
					var.unselect(true);
			}
		} else {
			
			
			for (int i = 0; i < Genes.size(); i++) {
				Genes.elementAt(i).unselect(true);
			}

			for (int i = start * Aggregation; i < Math.min(end * Aggregation, Genes.size()); i++) {
				int row = Genes.elementAt(i).getID();
				if (i < Genes.size()) {
					Genes.elementAt(i).select(true);

					
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		point2 = e.getPoint();
		if (point1 != null && point2 != null) {

			int start = Math.min(point1.x, point1.x) / pixelSize;
			int end = Math.min(point2.x, point2.x) / pixelSize;
			if (start == end) end++;
			if (start<=PixelCount) addSelection(start, end);

		}
		point1 = null;
		point2 = null;
		seurat.repaintWindows();
	

	}

	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {
		int x1 = Math.max(0, xx1 - abstandLinks) / this.pixelSize;
		int x2 = Math.max(0, xx2 - abstandLinks) / this.pixelSize;
		int y1 = Math.max(0, yy1 - abstandOben) / this.pixelSize;
		int y2 = Math.max(0, yy2 - abstandOben) / this.pixelSize;

		this.repaint();
	}

	public void mouseClicked(MouseEvent e) {
		 if (e.getClickCount() == 2) {
		    	dataManager.deleteSelection();
		        seurat.repaintWindows();
		    }
		 
		 
		 if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

				JPopupMenu menu = new JPopupMenu();
				JMenuItem item;
				
				
				
				item = new JMenuItem("set pixel dimension");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// createCorrelationExperiments();

					    
					    ColorDialog dialog =  new ColorDialog(seurat, frame.panel, pixelSize);
					  //  dialog.pixelWField.addKeyListener(globalView.gPanel);
					  //  dialog.pixelHField.addKeyListener(globalView.gPanel);
					    
					}
				});
				menu.add(item);
				
				
				
				item = new JMenuItem("set aggregation");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// createCorrelationExperiments();

					    
					    AggregationDialog dialog =  new AggregationDialog(seurat,frame.panel, Aggregation);
					  //  dialog.field.addKeyListener(globalView.gPanel);
					   
					    
					}
				});
				menu.add(item);
				
				
				menu.addSeparator();
				

			    item = new JMenuItem("Print");
			    item.addActionListener(new ActionListener() {
				
			    	 public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					frame.print();
					
				    }
		    	});
			    menu.add(item);
			    menu.show(this, e.getX(), e.getY());
			   
		
		  }
		 
			
		point1 = e.getPoint();
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		point2 = e.getPoint();
		this.repaint();

	}

	@Override
	public String getToolTipText(MouseEvent e) {
		if (e.isControlDown()) {

			String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";
			if (this.isVariables) {

				int i = (e.getX() - abstandLinks) / this.pixelSize;
				ISelectable var = Experiments.elementAt(i);
				s += "<FONT FACE = 'Arial'><TR><TD>"
				// + var.name
						// + "</TD><TD></TR><TR> "
						+ data[(e.getX() - abstandLinks)/ this.pixelSize][(e.getY()-abstandLinks)
								/ this.pixelSize] + "</TD></TR>";

			} else {
				
				

				s += data[(e.getX() - abstandLinks) / this.pixelSize][(e.getY() - abstandLinks)
						/ this.pixelSize]
						+ "</TD></TR>";

			}

			return s;
		} else
			return null;

	}

	@Override
	public void paint(Graphics g) {

		g.setColor(Color.white);

		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		// this.calculateSelection();
		

		boolean selection = false;
		
	
		for (int i = 0; i < this.Experiments.size(); i++) {
			if (this.Experiments.elementAt(i).isSelected())
				selection = true;
		}
		for (int i = 0; i < this.Genes.size(); i++) {
			if (this.Genes.elementAt(i).isSelected())
				selection = true;
		}
		

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {

				double koeff = 0;

				if (data[i][j] > 0) {
					koeff = Math.pow(data[i][j],
							seurat.settings.colorParam);

					Color c = (Color.getHSBColor(0, (float) koeff, 1));
					
					
					
					
					
					if (this.selection[i] || this.selection[j]) {
						c = c.darker();
						c = c.darker();
						
					}
					

					g.setColor(c);

				} else {
					koeff = Math.pow(-data[i][j],
							seurat.settings.colorParam);
					Color c = Color.getHSBColor((float) 0.66, (float) koeff, 1);

if (this.selection[i] || this.selection[j]) {
	c = c.darker();
	c = c.darker();
					}
					
					g.setColor(c);
				}

				g.fillRect(abstandLinks + pixelSize * i, abstandOben + j
						* pixelSize, pixelSize, pixelSize);
			}

		}

		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), 0, Math.abs(point2.x
					- point1.x), this.getHeight());
			g.drawRect(0, Math.min(point1.x, point2.x), this.getWidth(), Math
					.abs(point2.x - point1.x));

		}

	}

	public void applyNewPixelSize(int pixelW, int pixelH) {
		// TODO Auto-generated method stub
		
		this.pixelSize = pixelW;
		
		
		
		
		
		int length = Genes.size()/Aggregation*pixelSize;
		if (isVariables) length = Experiments.size()/Aggregation * pixelSize;
		
		
		if (seurat.SYSTEM == seurat.WINDOWS) {

			frame.setSize( length  + 17,
					      length + 38);

		} else
			frame.setSize(length +1,
					     length + 23);

		
		
		
	}

	public void applyNewPixelSize() {
		// TODO Auto-generated method stub
		repaint();
	}

	public void setAggregation(int aggr) {
		// TODO Auto-generated method stub
		Aggregation = aggr;
		
		
		PixelCount = Genes.size()/Aggregation;
		if (isVariables) PixelCount = Experiments.size()/Aggregation;
		
		
		int length = PixelCount * pixelSize;
		
	
		
		
		if (seurat.SYSTEM == seurat.WINDOWS) {

			frame.setSize( length  + 17,
					      length + 38);

		} else
			frame.setSize(length +1,
					     length + 23);

		
		
		calculateCorrs();
		
		
	}

	public void setModel(int model) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
		
		  int size = Genes.size();
	   		if (isVariables) size = Experiments.size();
		
       if (e.getKeyCode() == 38) {
    	   
    	   
    	 
			
			if (Aggregation < size) {
				Aggregation++;
				setAggregation(Aggregation);
			}
			
			  
			
			
			
		}
		
		
        if (e.getKeyCode() == 40) {
        	
        	
        	if (Aggregation > 1) {
				Aggregation--;
				setAggregation(Aggregation);
			}
			
		}
        
        
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}