package GUI;

import java.util.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.ISelectable;
import Data.Variable;

public class Histogram extends JFrame implements IPlot {

	Seurat amlTool;

	Histogram histogram = this;

	HistogramPanel panel;

	String name;

	public Histogram(Seurat amlTool, Vector variables, String name,
			double[] data) {
		super(name);
		this.name = name;

		this.amlTool = amlTool;

		panel = new HistogramPanel(this, amlTool, variables, name, data);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);

		this.setBounds(400, 400, 200, 200);
		this.setVisible(true);

		amlTool.windows.add(this);

		JMenuItem item = new JMenuItem(name);
		amlTool.windowMenu.add(item);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				histogram.setVisible(true);
			}
		});

		this.addKeyListener(panel);

	}

	
	
	
	
	public Histogram(Seurat amlTool, ISelectable object) {
		super(object.getName());
		this.name = object.getName();

		this.amlTool = amlTool;

		panel = new HistogramPanel(this, amlTool,  object.getVariables(),name, object.getDoubleData());

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);

		this.setBounds(400, 400, 200, 200);
		this.setVisible(true);

		amlTool.windows.add(this);

		JMenuItem item = new JMenuItem(name);
		amlTool.windowMenu.add(item);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				histogram.setVisible(true);
			}
		});

		this.addKeyListener(panel);

	}
	
	
	public void updateSelection() {
		// TODO Auto-generated method stub
		panel.updateSelection();

	}

	public void brush() {
		// TODO Auto-generated method stub
		panel.brush();
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		panel.removeColoring();

	}

}

class HistogramPanel extends JPanel implements KeyListener, MouseListener,
		IPlot, MouseMotionListener {
	double[] data;

	double[] balken;

	Seurat seurat;

	final double NA = 6.02E23;

	double NABalken = 0;

	int abstandLinks = 20;

	int abstandUnten = 25;

	double max = 0;

	double min = 0;

	double width;

	double maxCount;

	// DescriptionVariable descriptionVariable;

	Vector<ISelectable> variables;

	// ExperimentDescriptionFrame descriptionFrame;

	JMenuItem item = new JMenuItem("");

	HistogramPanel histogram = this;

	Point point1, point2;

	Image image;

	float colorShift = 0;

	Color GRAY = new Color(192, 192, 192);

	public Color[] color;

	Histogram hist;
	
	double anchor=-1;
	
	JPopupMenu menu;

	//double[] balkenHight;
	double[] balkenHightSelection;
	double NAHight;
	double NAHightSelection;

	public HistogramPanel(Histogram hist, Seurat amlTool,
			Vector<ISelectable> variables, String name, double[] data) {

		this.data = data;
		this.seurat = amlTool;
		this.variables = variables;
		this.hist = hist;

		double IQ = calculateInterQuartilAbstand();
		width = (int) Math
				.round(2 * IQ / Math.pow(data.length, (double) 1 / 3));
		if (width == 0)
			width = 1;

		System.out.println("Width " + width);

		this.calculateBalken(-1);
		this.setVisible(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

	}

	public double calculateInterQuartilAbstand() {

		ArrayList arrayList = new ArrayList();

		for (int i = 0; i < data.length; i++) {
			if (data[i] != NA)
				arrayList.add(data[i]);
		}

		Collections.sort(arrayList);
		return ((Double) arrayList.get(arrayList.size() * 3 / 4)).doubleValue()
				- ((Double) arrayList.get(arrayList.size() / 4)).doubleValue();

	}

	public void keyPressed(KeyEvent e) {
		 
		    if (e.getKeyCode() == 38) {
		    	
		    		
		    	
            int count = balken.length;
            this.width = (max - anchor)/count;
            
            if ((int) Math.floor((max - anchor) / width)+1 ==count) width = (max - anchor)/(balken.length+1);
            
            
            
            this.calculateBalken(width);
            removeColoring();	
            seurat.applyNewPixelSize();
			seurat.repaintWindows();
		    }
		    
		    
		    if (e.getKeyCode() == 40) {
	            int count = balken.length -1;
	            if (count == 0) return;
	            
	            this.width = (max - anchor)/count;
	            
	            if ((int) Math.floor((max - anchor) / width)+1 >count) width = (max - anchor)/(count-1);
	            
	            if (count == 1)  this.width = (max - anchor)*1.1;
	            
	            this.calculateBalken(width);
	            removeColoring();	
                seurat.applyNewPixelSize();
				seurat.repaintWindows();
			    }
		    
		    
		    
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {

		if (this.variables != null && variables.elementAt(0).isVariable()) {
			if (e.getKeyChar() == 'c') {
				colorShift += 0.1;
				if (colorShift > 1)
					colorShift -= 1;
				brush();
				seurat.repaintWindows();
			}

			if (e.getKeyChar() == 'r') {

				this.removeColoring();
				seurat.repaintWindows();
			}
		}

	}

	public ISelectable getVariable(int i) {
		if (variables == null)
			return null;
		return variables.elementAt(i);
	}

	public void updateSelection() {

		//this.balkenHight = new double[balken.length];
		this.balkenHightSelection = new double[balken.length];

		if (NABalken == 0) {

			int[] count = new int[balken.length];

			// for (int i = 0; i < balken.length; i++) {

			for (int j = 0; j < this.data.length; j++) {
				boolean selected = false;
                if (this.getVariable(j) != null) selected = this.getVariable(j).isSelected();

                if (selected) {
					int num = ((int) Math.floor((data[j] - anchor) / width));
					if (num>=0) count[num]++;
				}

			}

			for (int i = 0; i < balken.length; i++) {

				double koeff = count[i] / (balken [i] * maxCount);

				this.balkenHightSelection[i] = koeff
						*balken[i];

			}

		} else {

			int[] count = new int[balken.length];
			int countNA = 0;

			for (int j = 0; j < data.length; j++) {
				
				
				boolean selected = false;
                if (this.getVariable(j) != null) selected = this.getVariable(j).isSelected();

                if (selected) {

					if (data[j] != NA) {
						int num = ((int) Math.floor((data[j] - anchor) / width));
						if (num>=0) count[num]++;
					} else
						countNA++;
				}

			}

			for (int i = 0; i < balken.length; i++) {

				double koeff = count[i] / (balken [i] * maxCount);

			

				this.balkenHightSelection[i] = koeff*balken [i];

			}

			double koeff = countNA / (this.maxCount * NABalken);

			this.NAHightSelection = koeff* NABalken;

			this.NAHight =  NABalken;

		}

		this.repaint();
	}

	public void calculateBalken(double binwidth) {

		int k = 0;
		while (data[k] == NA) {
			k++;
		}

		max = data[k];
		min = data[k];
		
		

		for (int i = 0; i < data.length; i++) {
			// System.out.println(data [i]);
			if (data[i] != NA) {
				if (data[i] > max)
					max = data[i];
				if (data[i] < min)
					min = data[i];
			}
		}

		if (anchor==-1) anchor= min;
		
		
		

		if ((max - min) / width > 20)
			this.width = (max - min) / 20;
		
		if (binwidth != -1) width = binwidth;
		

		int anzahlBalken = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != NA) {
				int num = (int) Math.floor((data[i] - anchor) / width);
				if (num>=0) anzahlBalken = Math.max(anzahlBalken, num+1);

			}
		}

		balken = new double[anzahlBalken];

		int anzahlNA = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != NA) {

				int num = (int) Math.floor((data[i] - anchor) / width);
				if (num>=0) balken[num]++;

			} else
				anzahlNA++;
		}
		
		
		maxCount = 0;
		for (int i = 0; i < balken.length; i++) {
			if (maxCount < balken[i])
				maxCount = balken[i];

		}
		
		maxCount = Math.max(maxCount, anzahlNA);


		for (int i = 0; i < balken.length; i++) {
			balken[i] = balken[i] / maxCount;
		}

		
		NABalken = anzahlNA / maxCount;

		color = new Color[this.balken.length];
		for (int i = 0; i < color.length; i++) {
			color[i] = GRAY;
		}

	}

	@Override
	public void paint(Graphics g) {

		g.setColor(Color.WHITE);

		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);

		if (this.balkenHightSelection == null)
			this.updateSelection();

		if (NABalken == 0) {
			
		//	g.drawLine(abstandLinks, 	this.getHeight() - abstandUnten+3, this.getWidth() - abstandLinks, 	this.getHeight() - abstandUnten+3);
			

			for (int i = 0; i < balken.length; i++) {

				if (balken [i] != 0) { 
				
				g.setColor(color[i]);

				g.fillRect(abstandLinks + i
						* (this.getWidth() - 2 * abstandLinks) / balken.length,

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balken[i]),

				(this.getWidth() - 2 * abstandLinks) / balken.length,

				(int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balken[i])

				);

				g.setColor(Color.RED);
				g.fillRect(abstandLinks + i
						* (this.getWidth() - 2 * abstandLinks) / balken.length,

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balkenHightSelection[i]),

				(this.getWidth() - 2 * abstandLinks) / balken.length,

				(int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balkenHightSelection[i])

				);

				g.setColor(Color.black);

				g.drawRect(abstandLinks + i
						* (this.getWidth() - 2 * abstandLinks) / balken.length,

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten)  *this.balken[i]),

				(this.getWidth() - 2 * abstandLinks) / balken.length,

				(int) Math.round((this.getHeight() - 3 * abstandUnten) *this.balken[i]));
				}
				else {
					g.setColor(Color.red);
					g.drawLine(abstandLinks + i
							* (this.getWidth() - 2 * abstandLinks) / balken.length,

					this.getHeight() - abstandUnten,

					abstandLinks + (i+1)
					* (this.getWidth() - 2 * abstandLinks) / balken.length,

					this.getHeight() - abstandUnten

					);
				}
			}
			

			g.drawString(round(anchor), abstandLinks, this.getHeight() - 9);

			g.drawString(round(anchor + width*balken.length), (this.getWidth() - 2 * abstandLinks), this
					.getHeight() - 9);

		} else {

			///g.drawLine(abstandLinks, 	this.getHeight() - abstandUnten+3, abstandLinks + balken.length
			//		* (this.getWidth() - 3 * abstandLinks)
			//		/ (balken.length + 1), 	this.getHeight() - abstandUnten+3);
			
			
			for (int i = 0; i < balken.length; i++) {

				
				if (balken [i] != 0) { 
					
					
				g.setColor(color[i]);

				g.fillRect(abstandLinks + i
						* (this.getWidth() - 3 * abstandLinks)
						/ (balken.length + 1),

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balken[i]),

				(this.getWidth() - 3 * abstandLinks) / (balken.length + 1),

				(int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balken[i])

				);

				g.setColor(Color.RED);

				g.fillRect(abstandLinks + i
						* (this.getWidth() - 3 * abstandLinks)
						/ (balken.length + 1),

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balkenHightSelection[i]),

				(this.getWidth() - 3 * abstandLinks) / (balken.length + 1),
				(int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balkenHightSelection[i])

				);

				g.setColor(Color.black);

				g.drawRect(abstandLinks + i
						* (this.getWidth() - 3 * abstandLinks)
						/ (balken.length + 1),

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balken[i]),

				(this.getWidth() - 3 * abstandLinks) / (balken.length + 1),

				(int) Math
				.round((this.getHeight() - 3 * abstandUnten) *this.balken[i])

				);
				}
				else {
					g.setColor(Color.red);
					g.drawLine(abstandLinks + i
							* (this.getWidth() - 3 * abstandLinks)
							/ (balken.length + 1),

					this.getHeight() - abstandUnten,

					abstandLinks + (i+1)
					* (this.getWidth() - 3 * abstandLinks)
					/ (balken.length + 1),

					this.getHeight() - abstandUnten

					);
				}

			}
			// NA Balken
			g.setColor(Color.RED);

			g.fillRect(2 * abstandLinks + balken.length
					* (this.getWidth() - 3 * abstandLinks)
					/ (balken.length + 1),

			this.getHeight() - abstandUnten - (int) Math
			.round((this.getHeight() - 3 * abstandUnten)*this.NAHightSelection),

			(this.getWidth() - 3 * abstandLinks) / (balken.length + 1),
			(int) Math
			.round((this.getHeight() - 3 * abstandUnten)*this.NAHightSelection)

			);

			g.setColor(Color.black);

			g.drawRect(2 * abstandLinks + balken.length
					* (this.getWidth() - 3 * abstandLinks)
					/ (balken.length + 1),

			this.getHeight() - abstandUnten - (int) Math
			.round((this.getHeight() - 3 * abstandUnten)*this.NAHight),

			(this.getWidth() - 3 * abstandLinks) / (balken.length + 1),

			(int) Math
			.round((this.getHeight() - 3 * abstandUnten)*this.NAHight)

			);
			g.drawString(round(anchor), abstandLinks, this.getHeight() - 9);

			g.drawString(round(anchor + width*balken.length), balken.length
					* (this.getWidth() - 3 * abstandLinks)
					/ (balken.length + 1) - 7, this.getHeight() - 9);

			if (max < 10000)
				g.drawString("NA", 2 * abstandLinks + balken.length
						* (this.getWidth() - 3 * abstandLinks)
						/ (balken.length + 1), this.getHeight() - 9);

		}

		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math.abs(point2.y
					- point1.y));
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
		boolean[] selectedBalken = new boolean[balken.length];

		if (this.NABalken == 0) {

			for (int i = 0; i < balken.length; i++) {
				if (this.containsRectInRect(abstandLinks + i
						* (this.getWidth() - 2 * abstandLinks) / balken.length,

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten)*this.balken[i]),

				abstandLinks + (i + 1) * (this.getWidth() - 2 * abstandLinks)
						/ balken.length,

				this.getHeight() - abstandUnten,

				point1.x, point1.y, point2.x, point2.y))
					selectedBalken[i] = true;

			}

			seurat.dataManager.deleteSelection();
			
			if (variables.elementAt(0).isGene() || variables.elementAt(0).isClone()) {
				seurat.dataManager.selectExperiments(); 
			}
			if (variables.elementAt(0).isVariable() || variables.elementAt(0).isCGHVariable()) {
				seurat.dataManager.selectGenesClones(); 
		   }
		
			
			
			
			for (int i = 0; i < selectedBalken.length; i++) {

				if (selectedBalken[i]) {
					for (int j = 0; j < data.length; j++) {
						int num = (int) Math.floor((data[j] - anchor) / width);
						if (num == i && this.getVariable(j) != null)
							this.getVariable(j).select(true);
					}
				}
			}

		}

		if (this.NABalken != 0) {

			for (int i = 0; i < balken.length; i++) {
				if (this.containsRectInRect(abstandLinks + i
						* (this.getWidth() - 3 * abstandLinks)
						/ (balken.length + 1),

				this.getHeight() - abstandUnten - (int) Math
				.round((this.getHeight() - 3 * abstandUnten)*this.balken[i]),

				abstandLinks + (i + 1) * (this.getWidth() - 3 * abstandLinks)
						/ (balken.length + 1),

				this.getHeight() - abstandUnten,

				point1.x, point1.y, point2.x, point2.y))
					selectedBalken[i] = true;

			}

			boolean isNASelected = false;

			if (this.containsRectInRect(

			2 * abstandLinks + balken.length
					* (this.getWidth() - 3 * abstandLinks)
					/ (balken.length + 1),

			this.getHeight() - abstandUnten - (int) Math
			.round((this.getHeight() - 3 * abstandUnten)*this.NAHight),

			2 * abstandLinks + balken.length
					* (this.getWidth() - 3 * abstandLinks)
					/ (balken.length + 1)
					+ (this.getWidth() - 3 * abstandLinks)
					/ (balken.length + 1),

			this.getHeight() - abstandUnten,

			point1.x, point1.y, point2.x, point2.y))
				isNASelected = true;

			seurat.dataManager.deleteSelection();
			
			seurat.dataManager.selectAll();
			for (int i = 0; i < variables.size(); i++) {
				if (variables.elementAt(i)!=null) variables.elementAt(i).unselect(true); 
			}
		
			for (int j = 0; j < data.length; j++) {
				if (data[j] != NA) {
					int num = (int) Math.floor((data[j] - min) / width);
					if (selectedBalken[num]&& this.getVariable(j)!=null)
						this.getVariable(j).select(true);
				} else {
					if (isNASelected && this.getVariable(j)!=null)
						this.getVariable(j).select(true);

				}
			}

		}

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		point2 = e.getPoint();
		if (point1 != null && point2 != null) {

			addSelection(point1, point2);

		}
		point1 = null;
		point2 = null;
		seurat.repaintWindows();
	}

	public void mouseClicked(MouseEvent e) {
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			menu = new JPopupMenu();
			JMenuItem item = new JMenuItem("Width...");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    double binwidth = Double.parseDouble(JOptionPane.showInputDialog(null,"Set bin width to:",
                            width+""));
				    
				    calculateBalken(binwidth);
				    updateSelection();
				}
			});
			
			menu.add(item);

			item = new JMenuItem("Anchorpoint...");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					 anchor = Double.parseDouble(JOptionPane.showInputDialog(null,"Set anchor point to:",
	                            anchor+""));
					 
					 calculateBalken(width);
					 updateSelection();
				}
			});
			menu.add(item);

			
			
			
			item = new JMenuItem("Add Colors");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					colorShift += 0.1;
					if (colorShift > 1)
						colorShift -= 1;
					brush();
					seurat.applyNewPixelSize();
					seurat.repaintWindows();
				
				}
			});
			if (variables.elementAt(0) instanceof Variable) menu.add(item);
			
			
			
			item = new JMenuItem("Remove Colors");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                removeColoring();	
                seurat.applyNewPixelSize();
				seurat.repaintWindows();
            }
			});
			if (variables.elementAt(0) instanceof Variable) menu.add(item);
			
			
			
			
			
			

			item = new JMenuItem("Dismiss");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					menu.setVisible(false);
				}
			});
			menu.add(item);

			menu.show(this, e.getX(), e.getY());

		}

		
		
		
		
		
		
		
		
		point1 = e.getPoint();
	}

	public void mouseDragged(MouseEvent e) {
		point2 = e.getPoint();
		this.repaint();

	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public int getBalken(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		int i = -1;
		
		if (NABalken == 0) {
	       i = 		(x - abstandLinks) * balken.length
	
				/ (this.getWidth() - 2 * abstandLinks);
		}
		else i =  		(x - abstandLinks) * (balken.length+1)
		
		/ (this.getWidth() - 3 * abstandLinks);

		int maxY = this.getHeight() - abstandUnten;
		int minY = this.getHeight()
				- abstandUnten
				- (int) Math.round((this.getHeight() - 3 * abstandUnten)
						* balken[i]);

		if (y > minY && y < maxY)
			return i;

		return -222;

	}

	@Override
	public String getToolTipText(MouseEvent e) {
		if (e.isControlDown()) {
			int i = this.getBalken(e);

			int count = 0;

			for (int j = 0; j < this.data.length; j++) {
				if (this.getVariable(i) != null
						&& this.getVariable(j).isSelected()) {
					if ((int) Math.floor((data[j] - anchor) / width) == i)
						count++;
				}

			}

			double koeff = count / (this.maxCount);

			

			return "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>"
					+ "<FONT FACE = 'Arial'>"
					+ "["
					+ round((anchor + this.width * i))
					+ " - "
					+ round((anchor + this.width * (i + 1)))
					+ ")"
					+ "<BR><STRONG></STRONG>"
					+ (int) Math.round(this.maxCount * balkenHightSelection[i])
					+ "/"
					+ (int) Math.round(this.maxCount * balken[i])
					+ "	"
					+ "("
					+ Math.round(balkenHightSelection[i]/balken [i] * 10000) / (double) 100 + "%)";

		}
		return null;
	}

	public String round(double zahl) {
		if (zahl == Math.round(zahl))
			return "" + zahl;
		return "" + (double) Math.round(zahl * 100) / 100;
	}

	public String cutLabels(String s, int availablePlace, Graphics g) {
		s = s.replaceAll("\"", "");
		String ss = this.cutLabelsHelp(s, availablePlace, g);

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

	public String cutLabelsHelp(String s, int availablePlace, Graphics g) {
		int Width = 0;
		for (int i = 0; i < s.length(); i++)
			Width += g.getFontMetrics().charWidth(s.charAt(i));
		if (Width < availablePlace)
			return s;

		Width = 0;
		String[] split = s.split(" ");
		String cutS = "";
		if (split.length > 1) {

			for (int i = 0; i < split.length; i++)
				cutS += split[i].substring(0, 1);
			for (int i = 0; i < cutS.length(); i++)
				Width += g.getFontMetrics().charWidth(cutS.charAt(i));
			if (Width < availablePlace)
				return cutS;
		}

		s = s.replaceAll("ck", "c");
		cutS = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != 'e' && c != 'u' && c != 'i' && c != 'o' && c != 'ü'
					&& c != 'a' && c != 'ö' && c != 'ä' && c != 'y')
				cutS += c;
		}

		return cutS;

	}

	public void brush() {

		for (int i = 0; i < color.length; i++) {

			float value = ((float) i / color.length + colorShift);
			if (value > 1)
				value -= 1;

			color[i] = Color.getHSBColor(value, 1, (float) 0.7);

			for (int j = 0; j < data.length; j++) {
				int num = (int) Math.floor((data[j] - min) / width);
				if (num == i && this.getVariable(j) != null) {
					if (((Variable) this.getVariable(j)).getBarchartToColors()
							.indexOf(hist) == -1) {

						((Variable) this.getVariable(j)).getColors().add(color[i]);

						int count = 0;

						for (int jj = 0; jj < this.data.length; jj++) {
							if (this.getVariable(i) != null
									&& this.getVariable(jj).isSelected()) {
								if ((int) Math.floor((data[j] - min) / width) == i)
									count++;
							}

						}

						double koeff = count / (this.maxCount * balken[i]);

						String name = "[" + round((min + this.width * i))
								+ " - " + round((min + this.width * (i + 1)))
								+ ")";

						((Variable) this.getVariable(j)).getColorNames().add(name);
						((Variable) this.getVariable(j)).getBarchartToColors()
								.add(hist);
					} else {

						int index = ((Variable) this.getVariable(j)).getBarchartToColors()
								.indexOf(hist);
						((Variable) this.getVariable(j)).getColors().set(index,
								color[i]);

					}

				}
			}

		}

	}

	public void removeColoring() {

		for (int i = 0; i < this.variables.size(); i++) {
			int index = ((Variable) this.getVariable(i)).getBarchartToColors()
					.indexOf(hist);
			if (index != -1) {
				((Variable) this.getVariable(i)).getColors().remove(index);
				((Variable) this.getVariable(i)).getColorNames().remove(index);
				((Variable) this.getVariable(i)).getBarchartToColors()
						.remove(index);
			}
		}

		for (int i = 0; i < color.length; i++) {
			color[i] = GRAY;
			this.colorShift = 0;

		}

	}

}
