package GUI;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.ClusterNode;
import Data.Variable;

import Data.*;
//import org.rosuda.JRclient.*;

class ZoomView extends JFrame implements MatrixWindow,IPlot {
	int pixelSize = 2;

	Seurat seurat;

	ZoomViewPanel gPanel;


	JMenuItem item = new JMenuItem("Clustering");

	ZoomView zoomView = this;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;
	
	DataManager dataManager;
	
	Vector<ISelectable> Experiments;
	Vector<ISelectable> Genes;


	
	
	JPanel infoPanel;
	
	
	

	public void setInfo(String info) {
		this.gPanel.setInfo(info);
	}

	public void applyNewPixelSize(int size) {
		this.pixelSize = size;
		this.gPanel.pixelSize = size;
		
		
		

		int colorsHight = 0;

		for (int i = 0; i < Experiments.size(); i++) {
		
			try {
				
				Variable var = (Variable)Experiments.elementAt(i);
				
			for (int j = var.getColors().size() - 1; j >= 0; j--) {

				colorsHight = var.getColors().size() * (2 * this.pixelSize + 1) + 4;

			
			}
			}
			catch (Exception e ){
				
			}
			
			
			
			
		}

		
		int upShift = gPanel.abstandOben + colorsHight;
		
		
		
		
		
		if (seurat.SYSTEM == seurat.WINDOWS) {

			this
					.setSize(
							Experiments.size() * pixelSize + gPanel.abstandLinks +  18, 30+
							upShift + 40 + Genes.size()* pixelSize);

		} else
			this
			.setSize(
					Experiments.size() * pixelSize + gPanel.abstandLinks + 8,30+
					upShift  + Genes.size()* pixelSize + 27);
		
		
		
		
		
		this.repaint();
		
		
		
		
	}

	public ZoomView(Seurat seurat, String name, Vector Experiments, Vector Genes) {
		super(name);

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.pixelSize = seurat.settings.PixelSize;
		this.getContentPane().setLayout(new BorderLayout());
		ZoomViewPanel panel = new ZoomViewPanel(seurat, pixelSize,
				Experiments, Genes);
		
		
		
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEtchedBorder());
		p.setLayout(new BorderLayout());
		
		
		
		p.add(panel,BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEtchedBorder());
		infoPanel = new JPanel();
		
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		infoPanel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(infoPanel,BorderLayout.SOUTH);
		
		this.Experiments = Experiments;
		this.Genes = Genes;

		panel.setPreferredSize(new Dimension(Experiments.size() * pixelSize, panel.abstandOben+Genes.size() * pixelSize));
		gPanel = panel;
	
		
		this.setLocation(200,200);
		
		
		
		
		Font myFont = new Font("SansSerif", 0, 10);

		
		JLabel label = new JLabel("Columns: " + Experiments.size());
		label.setFont(myFont);
		infoPanel.add(label);
		
		
		
		label = new JLabel(" Rows: "+Genes.size()+"  ");
		label.setFont(myFont);
		infoPanel.add(label);
		
		
		
		
	
		
		
		
		
		
		
		
    	this.applyNewPixelSize(this.pixelSize);
		
		
		
		
		
		this.getContentPane().add(new JScrollPane(p), BorderLayout.CENTER);
		this.setVisible(true);

		seurat.windows.add(this);

		seurat.windowMenu.add(item);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomView.setVisible(true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				zoomView.seurat.windowMenu.remove(item);
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

class ZoomViewPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener {
	Seurat seurat;

	int pixelSize = 1;

	int abstandLinks = 1;

	int abstandOben = 1;

	
	int[] originalOrderSpalten;

	ClusterNode nodeZeilen;

	ClusterNode nodeSpalten;

	Point point1, point2;

	boolean clustering = true;

	String methodColumns, methodRows;

	String distanceColumns, distanceRows;

	String info, infoRows, infoColumns;

	Color SelectionColor = Color.black;

	Vector<ISelectable> Genes;
	
	Vector<ISelectable> Experiments;
	
	DataManager dataManager;
	
	int upShift = abstandOben;
	
	int Width = 1;

	Color [][] cellColor;
	
	
	
	

	public ZoomViewPanel(Seurat seurat, int pixelSize, Vector Experiments, Vector Genes) {
		this.seurat = seurat;
		this.pixelSize = pixelSize;

		clustering = false;
		this.dataManager = seurat.dataManager;

/*		this.orderSpalten = orderSpalten;
		this.originalOrderSpalten = orderSpalten;
		this.orderZeilen = orderZeilen;*/
		this.Experiments = Experiments;
		this.Genes = Genes;

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
	}

	
	
	public double fPos(double xx) {
		double a = seurat.settings.aPos;
		double b = seurat.settings.bPos;
		double value = 0;
		
		
		if (xx < seurat.settings.posMin) return 0;
		if (xx > seurat.settings.posMax) return 1;
		
		
		double x = (xx - seurat.settings.posMin)/(seurat.settings.posMax- seurat.settings.posMin);
		
		
		
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
	
public void updateSelection() {
	cellColor = new Color[this.Experiments.size()][this.Genes.size()];

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

		for (int i = 0; i < Experiments.size(); i++) {
			for (int j = 0; j < Genes.size(); j++) {

				double value = Experiments.elementAt(i).getRealValue(Genes.elementAt(j).getID());
				
				if (value > 0) {

					double koeff = value / Experiments.elementAt(i).getMax();

					Color c = Color.getHSBColor(0, (float) fPos(koeff), 1);

					if (Experiments.elementAt(i).getRealValue(
									Genes.elementAt(j).getID()) == dataManager.NA)
						c = Color.WHITE;

					if (selection) {
						c = c.darker();
						c = c.darker();
					}

					boolean selected = false;

				//	if (Experiments.elementAt(i).isSelected[this.Genes.elementAt(j).ID]) {
					
					if (Experiments.elementAt(i).isSelected() && Genes.elementAt(j).isSelected()) {
								selected = true;
					}

						
					

					if (selected) {
						c = c.brighter();
						c = c.brighter();
					}

					cellColor[i][j] = c;

				} else {
					double koeff = value / Experiments.elementAt(i).getMin();
					if (Experiments.elementAt(i).getMin() == 0) koeff = 0;
					
					Color c = (Color.getHSBColor((float) 0.33,
							(float) fNeg(koeff), 1));

					if (selection) {
						c = c.darker();
						c = c.darker();
					}

					boolean selected = false;

				//	if (Experiments.elementAt(i).isSelected[Genes.elementAt(j).ID]) {
					if (Experiments.elementAt(i).isSelected() && Genes.elementAt(j).isSelected()) {
						
					selected = true;
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


	for (int i = 0; i < Experiments.size(); i++) {
		for (int j = 0; j < Genes.size(); j++) {

			double value = Experiments.elementAt(i).getRealValue(Genes.elementAt(j).getID());
			double koeff = 0;
			Color c;
			
			
			if (value > 0) {
				koeff = value / Experiments.elementAt(i).getMax();
				c = new Color((float) fPos(koeff), 0, 0);
			}
			else {
				koeff = value / Experiments.elementAt(i).getMin();
                c = new Color(0, (float) fNeg(koeff), 0);
			}




					if (Experiments.elementAt(i).getRealValue(
									Genes.elementAt(j).getID()) == dataManager.NA)
						c = Color.WHITE;

					if (selection) {
						c = c.darker();
						c = c.darker();
						c = c.darker();
					}

					boolean selected  = (Experiments.elementAt(i).isSelected() && Genes.elementAt(j).isSelected()) ;
						
					//boolean selected = Experiments.elementAt(i).isSelected[this.Genes.elementAt(j).ID]; 
				
			

					if (selected) {
						c = c.brighter();
						c = c.brighter();
						c = c.brighter();
						c = c.brighter();

						if (Experiments.elementAt(i).getRealValue(
								Genes.elementAt(j).getID()) == dataManager.NA)
					c = Color.WHITE;

			
					}

					cellColor[i][j] = c;
				

			
		}

			
		}
	}

	this.repaint();
	}
	
	public double fNeg(double xx) {
		double a = seurat.settings.aNeg;
		double b = seurat.settings.bNeg;
		double value = 0;
		
		
		if (xx < seurat.settings.negMin) return 0;
		if (xx > seurat.settings.negMax) return 1;
		
		
		double x = (xx - seurat.settings.negMin)/(seurat.settings.negMax- seurat.settings.negMin);
		
		
		
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

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			point2 = e.getPoint();
			if (point1 != null && point2 != null) {

				selectRectangle(point1.x, point1.y, point2.x, point2.y);

			}
			point1 = null;
			point2 = null;
			seurat.repaintWindows();
		}
	}

	public int convertCaseToIndex(int i) {

		return Genes.elementAt(i).getID();

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

		
	

		for (int i = 0; i < Experiments.size(); i++) {
			for (int j = 0; j < Genes.size(); j++) {
				if (i < x2 && i >= x1 && j < y2 && j >= y1) {

				//	this.Experiments.elementAt(i).isSelected[Genes.elementAt(j).ID] = true;
					this.Experiments.elementAt(i).select(true);
					this.Genes.elementAt(j).select(true);

				}

			}
		}

		this.repaint();

	}


	public void mouseClicked(MouseEvent e) {
		
		 if (e.getClickCount() == 2) {
		    	dataManager.deleteSelection();
		        seurat.repaintWindows();
		    }
		 
		 
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
			ISelectable exp = this.getExpAtPoint(e.getPoint());
			ISelectable gene = this.getGeneAtPoint(e.getPoint());

			if (exp != null && gene != null) {
				
				String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";
			
				s += "<FONT FACE = 'Arial'><TR><TD>" + exp.getName()
						+ "  </TD><TD> ";
				
				s += "<FONT FACE = 'Arial'><TR><TD>" + gene.getName()
				+ "  </TD><TD> ";

				
				
				String value = exp.getRealValue(gene.getID()) + "";
				if (exp.getRealValue(gene.getID()) == seurat.dataManager.NA)
					value = "NA";
				s += "<FONT FACE = 'Arial'><TR><TD>" + value + "  </TD><TD> ";

				s += "</P></FONT></STRONG>";
				return s;
			} 
			
			
			
			if (e.getX() >= this.abstandLinks
					&& e.getX() <= this.abstandLinks + this.Experiments.size()
							* this.pixelSize && e.getY() <= this.upShift
					&& e.getY() >= this.abstandOben) {

				
				// System.out.println("svdsv " + exp);

				if (exp != null) {
					
					
					try {
						
						

					Variable var = (Variable)exp;
					int index = var.getColors().size() - 1
							- (this.upShift - 2 - e.getY())
							/ (2 * this.pixelSize + 1);

					String s = "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>";

					String name = "";
					
					
					try {
						name = ((Barchart)var.getBarchartToColors()
								.elementAt(index)).name;
					}
                    catch (Exception e1) {
                    	
                    } 			
                    
                    try {
						name = ((Histogram)var.getBarchartToColors()
								.elementAt(index)).name;
					}
                    catch (Exception e2) {
                    	
                    } 		
                    
                    
                    
					
					s += "<FONT FACE = 'Arial'><TR><TD>"
							+ name + "<TR><TD>"
									+ var.getColorNames().elementAt(index) 
							+ "  </TD><TD> ";

					return s;
					
					}
					catch (Exception ee) {}
					return null;

				}

			}
			
			

		}
		return null;
	}

	
	
	public ISelectable getExpAtPoint(Point p) {
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());

		if (x < abstandLinks)
			return null;
		if (y < abstandOben)
			return null;

		x = (x - abstandLinks) / this.pixelSize;
		y = (y - abstandOben) / this.pixelSize;

		

		return Experiments.elementAt(x);
	}

	


	public ISelectable getGeneAtPoint(Point p) {
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());

		if (x < abstandLinks)
			return null;
		if (y < upShift)
			return null;

		x = (x - abstandLinks) / this.pixelSize;
		y = (y - upShift) / this.pixelSize;

		return Genes.elementAt(y);
	}

	

	

	

	

	@Override
	public void paint(Graphics g) {
	
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		
		
		int colorsHight = 0;

		for (int i = 0; i < Experiments.size(); i++) {
			
			try {
				
			Variable var = (Variable)Experiments.elementAt(i);

			for (int j = var.getColors().size() - 1; j >= 0; j--) {

				colorsHight = var.getColors().size() * (2 * this.pixelSize + 1) + 4;

				g.setColor(var.getColors().elementAt(j));

				g.fillRect(abstandLinks + i * this.pixelSize, 2 + abstandOben
						+ j * (2 * this.pixelSize + 1), Math.max(pixelSize, 2),
						2 * pixelSize + 1);
			}
			}
			catch (Exception e) {}
		}

		
		upShift = abstandOben + colorsHight;
		
		
		if (cellColor == null) this.updateSelection();
		
			for (int i = 0; i < Experiments.size(); i++) {
				for (int j = 0; j < Genes.size(); j++) {

					g.setColor(cellColor [i][j]);
					

					g.fillRect( i * this.pixelSize, upShift  + j
							* this.pixelSize, pixelSize, pixelSize);
				

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



	public void brush() {
		// TODO Auto-generated method stub
		
	}



	public void removeColoring() {
		// TODO Auto-generated method stub
		
	}

	

	

	

}
