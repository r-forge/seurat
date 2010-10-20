package GUI;

import java.util.*;
import javax.swing.*;

import Tools.Tools;

import java.awt.event.*;
import java.awt.*;

import Data.AnnGene;
import Data.CGHVariable;
import Data.Clone;
import Data.DescriptionVariable;
import Data.Gene;
import Data.GeneVariable;
import Data.ISelectable;
import Data.Variable;

public class Barchart extends JFrame implements IPlot {

	Seurat seurat;

	BarchartPanel panel;

	JMenuItem item = new JMenuItem("");
	String name;
	
	boolean isChr = false;

	public Barchart(Seurat seurat, String name, Vector variables,
			String[] data) {
		super(name);
		this.seurat = seurat;
		this.name = name;
		panel = new BarchartPanel(this, seurat, name, variables, data);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		this.setBounds(350, 0, 250, Math.min(panel.bars.size() * (panel.BarHeigth + panel.BarSpace) 
				+ 2*panel.abstandOben-panel.BarSpace+30, 600));

		this.setVisible(true);
		this.addKeyListener(panel);

		seurat.windows.add(this);

		item = new JMenuItem(name);
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
				panel.removeColoring();
			
			}
		});
		
		
		panel.calculateAbstandLinks();
		this.setVisible(true);

	}
	
	
	public Barchart(Seurat seurat,ISelectable object) {
		super(object.getName());
		this.seurat = seurat;
		this.name = object.getName();
		panel = new BarchartPanel(this, seurat, name, object.getVariables(), object.getStringData());

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		this.setBounds(350, 0, 250, Math.min(panel.bars.size() * (panel.BarHeigth + panel.BarSpace) 
				+ 2*panel.abstandOben-panel.BarSpace+30, 600));

		this.setVisible(true);
		this.addKeyListener(panel);

		seurat.windows.add(this);

		item = new JMenuItem(name);
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
				panel.removeColoring();
			}
		});
		

	
		
		panel.calculateAbstandLinks();
		
		/**Test für die Sortinerung des Chromosomes*/
		if (object instanceof GeneVariable) isChr = ((GeneVariable)object).isChromosome;
		if (object instanceof CGHVariable) isChr = ((CGHVariable)object).isChromosome;
		
		
		if (isChr) panel.sortCHR();
		
		this.setVisible(true);

	}
	
	
	

	public void updateSelection() {
		panel.updateSelection();

	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void brush() {
		// TODO Auto-generated method stub
		panel.brush();
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		panel.removeColoring();
	}


	public void print() {
		// TODO Auto-generated method stub
		panel.print();
	}

}

class BarchartPanel extends JPanel implements KeyListener, MouseListener,
		IPlot, MouseMotionListener {
	String[] data;

	// double[] balken;

	// double[] selectedBalken;

	// Vector<String> barStrings;

	Seurat seurat;

	// DescriptionVariable descriptionVariable;

	JMenuItem item = new JMenuItem("");

	BarchartPanel barchartPanel = this;

	Point point1, point2;

	Barchart barchart;

	Image image;

	int abstandLinks = 80;
	
	int abstandRechts = 20;

	int abstandOben = 20;

	int BarHeigth = 25;
	
	int BarSpace = 20;

	int abstandString = 5;

	boolean firstPaint = true;
	
	boolean labelResizing = false;
	
	boolean barResizing = false;
	
    boolean heightResizing = false;
	
	boolean spaceResizing = false;
	
	Bar resizingBar;
	
	// int[] balkenAbs;

	// double[] koeff;

	// int[] balkenWidth;

	// int[] balkenSelectedWidth;

	// String [] binStrings;

	Color GRAY = new Color(192, 192, 192);

	Vector<ISelectable> variables;

	// public Color [] color;

	float colorShift = 0;

	JPopupMenu menu;

	/*Bars to the categories of given data*/
	Vector<Bar> bars;
	
	

	public BarchartPanel(Barchart barchart, Seurat seurat, String name,
			Vector<ISelectable> variables, String[] data) {

		this.data = data;
		this.seurat = seurat;
		this.variables = variables;
		this.barchart = barchart;
		
		
		

		this.calculateBalken();

		this.setPreferredSize(new Dimension(200, bars.size() * (BarHeigth+BarSpace) 
				+ abstandOben- BarSpace));
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

	public int indexOf(String s) {
		for (int i = 0; i < bars.size(); i++) {
			if (bars.elementAt(i).name.equals(s))
				return i;
		}
		return -1;
	}

	

	public void brush() {

		for (int i = 0; i < bars.size(); i++) {

			float value = ((float) i / bars.size() + colorShift);
			if (value > 1)
				value -= 1;

			bars.elementAt(i).color = Color
					.getHSBColor(value, 1, (float) 0.7);

			for (int j = 0; j < data.length; j++) {
				int num = indexOf(data[j]);
				if (i == num && this.getVariable(j) != null) {
					if (((Variable) this.getVariable(j)).getBarchartToColors()
							.indexOf(barchart) == -1) {

						((Variable) this.getVariable(j)).getColors().add(bars
								.elementAt(i).color);
						((Variable) this.getVariable(j)).getColorNames().add(bars
								.elementAt(i).name);
						((Variable) this.getVariable(j)).getBarchartToColors()
								.add(barchart);
					} else {

						int index = ((Variable) this.getVariable(j)).getBarchartToColors()
								.indexOf(barchart);
						((Variable) this.getVariable(j)).getColors().set(index,
								bars.elementAt(i).color);

					}

				}
			}

		}

	}

	public void removeColoring() {

		
		boolean removed = false;
		for (int i = 0; i < this.variables.size(); i++) {
			if (this.getVariable(i) instanceof Variable) {
			
			int index = ((Variable) this.getVariable(i)).getBarchartToColors()
					.indexOf(barchart);
			if (index != -1) {
				((Variable) this.getVariable(i)).getColors().remove(index);
				((Variable) this.getVariable(i)).getColorNames().remove(index);
				((Variable) this.getVariable(i)).getBarchartToColors()
						.remove(index);
				removed =  true;
			}
			}
		}

		for (int i = 0; i < bars.size(); i++) {
			bars.elementAt(i).color = GRAY;
			this.colorShift = 0;

		}
		seurat.repaintWindows();
		
	

	}

	public void addSelection(Point point1, Point point2) {

		
		
		boolean selected = false;	

		for (int i = 0; i < bars.size(); i++) {
		
			
			if (Tools.containsRectInRect(

			abstandLinks,

			abstandOben + i * (BarHeigth+BarSpace),

			abstandLinks+ (int) Math.round((this.getWidth() - abstandLinks - abstandRechts)
							* bars.elementAt(i).barRel),

			abstandOben + i * (BarSpace+ BarHeigth) + BarHeigth,

			point1.x, point1.y, point2.x, point2.y))
		
			{
				selected = bars.elementAt(i).select() || selected;
			}

	
		}		
		
		
		
		if (selected) { 
		if (variables.elementAt(0) instanceof Gene || variables.elementAt(0) instanceof AnnGene || variables.elementAt(0) instanceof Clone) {
			seurat.dataManager.selectExperiments(); 
		}
		if (variables.elementAt(0) instanceof Variable || variables.elementAt(0) instanceof CGHVariable) {
			seurat.dataManager.selectGenesClones(); 
	    }
		
		
		
		
		}

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		
		if (labelResizing) {
			labelResizing = false;
			resizingBar = null;
			return;
		}
		if (barResizing) {
			barResizing = false;
			resizingBar = null;
			return;
		}
		if (heightResizing) {
			heightResizing = false;
			resizingBar = null;
			return;
		}

		if (spaceResizing) {
			spaceResizing = false;
			resizingBar = null;
			return;
		}


		point2 = e.getPoint();
		
		if (!(e.getButton() == MouseEvent.BUTTON3 || e.isControlDown())) {

			if (point1 != null && point2 != null) {
				if (!e.isShiftDown()) seurat.dataManager.deleteSelection();
				addSelection(point1, point2);
			}

			point1 = null;
			point2 = null;
			seurat.repaintWindows();
		}
	}

	public void mouseClicked(MouseEvent e) {
		
		point1 = e.getPoint();
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			menu = new JPopupMenu();
			JMenuItem item;

			JMenu sortMenu = new JMenu("Sort by ...");

			item = new JMenuItem("Count");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByCount();
				}
			});
			sortMenu.add(item);

			item = new JMenuItem("Absolute selected");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByAbsolutSelected();
				}
			});
			sortMenu.add(item);

			item = new JMenuItem("Relative selected");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByRelativeSelected();
				}
			});
			sortMenu.add(item);

			item = new JMenuItem("Lexicographic");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByLexico();
				}
			});
			sortMenu.add(item);

			sortMenu.addSeparator();

			item = new JMenuItem("Reverse");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByReverse();
				}
			});
			sortMenu.add(item);

			menu.add(sortMenu);
			
			
			
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
			if (variables.elementAt(0) instanceof Variable) {
				menu.addSeparator();
				menu.add(item);
			}
			
			
			
			item = new JMenuItem("Remove Colors");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                removeColoring();	
                seurat.applyNewPixelSize();
                seurat.applyNewPixelSize();
				seurat.repaintWindows();
            }
			});
			if (variables.elementAt(0) instanceof Variable) menu.add(item);
			
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

	public void mouseDragged(MouseEvent e) {
		
		
		if (labelResizing) {
			this.abstandLinks = e.getX();
			updateSelection();
			return;
		}
		
		if (barResizing) {
			
				if (resizingBar != null) {
				Bar bar = getMaxBar();	
				this.abstandRechts = this.getWidth() - abstandLinks - (e.getX()- abstandLinks)*bar.variables.size()/resizingBar.variables.size();
			    updateSelection();
			}
			return;
		}
		if (heightResizing) {
			
				if (resizingBar != null && e.getY()>=abstandOben && bars.indexOf(resizingBar)==0 ) {
				this.BarHeigth = (e.getY() - abstandOben);
				this.setPreferredSize(new Dimension(
						this.getWidth(), bars.size() * (BarHeigth+BarSpace) + abstandOben- BarSpace));
			    updateSelection();
			}
			return;
		}
		if (spaceResizing) {
			
				if (resizingBar != null && bars.indexOf(resizingBar)!= 0) {
				this.BarSpace =  (e.getY() - abstandOben)/(bars.indexOf(resizingBar))-BarHeigth;
				this.setPreferredSize(new Dimension(this.getWidth(), bars.size() * (BarHeigth+BarSpace) 
						+ abstandOben- BarSpace));
				updateSelection();
			}
				
				
				if (resizingBar != null && bars.indexOf(resizingBar)== 0) {
					abstandOben =  e.getY();
					this.setPreferredSize(new Dimension(this.getWidth(), bars.size() * (BarHeigth+BarSpace) 
							+ abstandOben- BarSpace));
					updateSelection();
				}
				
				
			return;
		}
		
		
		point2 = e.getPoint();
		
		
		this.repaint();

	}
	
    public int getMaxBarWidth() {
    	int max = 0;
    	for (int i = 0; i < bars.size(); i++) {
    		Bar bar = bars.elementAt(i);
    		if (bar.barWidth>max) max = bar.barWidth;
    		
    	}
    	
    	return max;
    }	
    
    
    public Bar getMaxBar() {
    	int max = 0;
    	int j = 0;
    	for (int i = 0; i < bars.size(); i++) {
    		Bar bar = bars.elementAt(i);
    		if (bar.barWidth>max){
    			max = bar.barWidth;
    			j = i;
    		}
    		
    	}
    	
    	return bars.elementAt(j);
    }	
	
	
	

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
		System.out.println("Pressed");
		if (isLabelResizing(e)) {
			labelResizing = true;
			resizingBar = getBar(e);
			return;
		}
		if (isBarWidthResizing(e)) {
			barResizing = true;
			resizingBar = getBar(e);
			return;
		}
		if (isHeightResizing(e)) {
			heightResizing = true;
			resizingBar = getBar(e);
			return;
		}
		if (isSpaceResizing(e)) {
			spaceResizing = true;
			resizingBar = getBar(e);
			return;
		}
		
	}

	public void keyPressed(KeyEvent e) {
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
				seurat.applyNewPixelSize();
				seurat.repaintWindows();
			}

			if (e.getKeyChar() == 'r') {

				this.removeColoring();
				seurat.applyNewPixelSize();
				seurat.repaintWindows();
			}
		}

	}

	public void calculateBalken() {

		System.out.println("Calc");
		bars = new Vector();

		Vector<String> barStrings = new Vector();

		for (int i = 0; i < data.length; i++) {
			boolean add = true;
			for (int j = 0; j < barStrings.size(); j++) {
				if (barStrings.elementAt(j).equals(data[i])) {
					add = false;
					bars.elementAt(j).variables.add(getVariable(i));
				}
			}
			if (add) {
				barStrings.add(data[i]);
				bars.add(new Bar(this, data [i],getVariable(i)));
			}
		}



		for (int i = 0; i < bars.size(); i++) {
			Bar bar = bars.elementAt(i);
			bar.barRel = bar.variables.size();
			bar.barAbs = bar.variables.size();
			bars.elementAt(i).color = GRAY;
		}
		

		double max = 0;
		for (int i = 0; i < bars.size(); i++) {
			if (max < bars.elementAt(i).barRel) max = bars.elementAt(i).barRel;
		}

		for (int i = 0; i < bars.size(); i++) {
			bars.elementAt(i).barRel /= max;
		}
		
		//Collections.sort(barStrings);
        sortByLexico();
	}

	public void calculateAbstandLinks() {
		int length = 0;
		for (int i = 0; i < this.bars.size(); i++) {
			String s = this.bars.elementAt(i).name;
			int Width = 0;
			for (int j = 0; j < s.length(); j++)
				Width += this.getGraphics().getFontMetrics().charWidth(
						s.charAt(j));

			if (length < Width)
				length = Width;

		}

		this.abstandLinks = Math.min(length + 12, 80);
	}

	
	
	
	public void updateSelection() {

		for (int i = 0; i < bars.size(); i++) {

			Bar bar = bars.elementAt(i);
            bar.nameShort = Tools.cutLabels(bar.name, abstandLinks- abstandString - 3, this.getGraphics());
			int selectedCount = 0;

			
			for (int j = 0; j < bar.variables.size(); j++) {
				if (bar.variables.elementAt(j).isSelected()) selectedCount++;
			}

			bar.koeff = (double) selectedCount / bar.variables.size();
			bar.selectedCount = selectedCount;
			bar.barWidth = (int) Math.round((this.getWidth() - abstandLinks - abstandRechts)* bar.barRel);
			bar.barSelectedWidth = (int) Math.round((this.getWidth() - abstandLinks - abstandRechts)* bar.barRel * bar.koeff);

			if (   (this.getWidth() - abstandLinks - abstandRechts) * bar.barRel * bar.koeff > 0
				&& (this.getWidth() - abstandLinks - abstandRechts) * bar.barRel * bar.koeff < 1) 
				bar.barSelectedWidth = 1;

		}

		this.repaint();

	}
	
	
	
	
	
	
	

	public void sortByCount() {
		Vector<Bar> balkenTemp = new Vector();
		for (int i = 0; i < bars.size(); i++) {
			Bar bar = bars.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size()
					&& bar.barAbs < balkenTemp.elementAt(j).barAbs) {
				j++;
			}
			balkenTemp.insertElementAt(bar, j);

		}

		this.bars = balkenTemp;
		repaint();
	}

	public void sortByAbsolutSelected() {
		Vector<Bar> balkenTemp = new Vector();
		for (int i = 0; i < bars.size(); i++) {
			Bar bar = bars.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size()
					&& bar.selectedCount < balkenTemp.elementAt(j).selectedCount) {
				j++;
			}
			balkenTemp.insertElementAt(bar, j);

		}

		this.bars = balkenTemp;
		repaint();
	}

	public void sortByRelativeSelected() {
		Vector<Bar> balkenTemp = new Vector();
		for (int i = 0; i < bars.size(); i++) {
			Bar bar = bars.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size() && 
					(double) bar.selectedCount / bar.barAbs < (double) balkenTemp.elementAt(j).selectedCount
					/ balkenTemp.elementAt(j).barAbs) {
				j++;
			}
			balkenTemp.insertElementAt(bar, j);

		}

		this.bars = balkenTemp;
		repaint();
	}

	public void sortByReverse() {
		Vector<Bar> balkenTemp = new Vector();
		for (int i = 0; i < bars.size(); i++) {
			Bar bar = bars.elementAt(i);
			balkenTemp.insertElementAt(bar, 0);

		}

		this.bars = balkenTemp;
		repaint();
	}

	public void sortByLexico() {
		
		if (barchart.isChr) {
			sortCHR();
			return;
		}

		Vector<Bar> balkenTemp = new Vector();
		for (int i = 0; i < bars.size(); i++) {
			Bar bar = bars.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size()
					&& compareLexico(bar.name, balkenTemp.elementAt(j).name)) {
				j++;
			}
			balkenTemp.insertElementAt(bar, j);

		}

		this.bars = balkenTemp;
		repaint();
	}
	
	
public void sortCHR() {
		

	
		for (int i = 0; i < bars.size(); i++) {
			for (int j = 0; j < bars.size(); j++) {
			
			Bar bar1 = bars.elementAt(i);
			Bar bar2 = bars.elementAt(j);
		
			if ( compareCHR(bar1.name, bar2.name)) {
				if (i<j) {
					bars.set(j,bar1);
					bars.set(i,bar2);
				}
			}
			}
		}

		

		
		
		
		repaint();
	}
	




public boolean compareCHR(String a, String b) {
	int i = 0;

	String tA = a.replace("\"","");
	String tB = b.replace("\"","");
	if (tA.equals("X") || tA.equals("x")) tA = "23";
	if (tB.equals("X") || tB.equals("x")) tB = "23";

	if (tA.equals("Y") || tA.equals("y")) tA = "24";
	if (tB.equals("Y") || tB.equals("y")) tB = "24";
	
	if (tA.equals("NA")) return true;
	if (tB.equals("NA")) return false;
	
	
	int aa = Integer.parseInt(tA);
	int bb = Integer.parseInt(tB);
	
	if (aa < bb) return false;
	return true;
}



	

	public boolean compareLexico(String a, String b) {
		int i = 0;

		while (a.length() > i && b.length() > i) {
			if (a.charAt(i) > b.charAt(i))
				return true;
			if (a.charAt(i) < b.charAt(i))
				return false;
			i++;

		}

		return false;
	}

	@Override
	public void paint(Graphics g) {

		if (this.bars == null || firstPaint) {
			this.updateSelection();
			firstPaint = false;
		}

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for (int i = 0; i < bars.size(); i++) {

			g.setColor(Color.BLACK);
			
			Bar bar = bars.elementAt(i);

			g.drawString(bar.nameShort,

			abstandLinks - Tools.getStringSpace(bar.nameShort,g)-5, abstandOben + i * (BarSpace+ BarHeigth) + BarHeigth * 1 / 2 + 5

			);

			g.setColor(bar.color);

			g.fillRect(abstandLinks, abstandOben + i *(BarSpace+ BarHeigth), bar.barWidth, BarHeigth);

			g.setColor(Color.RED);

			g.fillRect(abstandLinks, abstandOben + i * (BarSpace+ BarHeigth), bar.barSelectedWidth, BarHeigth);

			g.setColor(Color.BLACK);

			g.drawRect(abstandLinks, abstandOben + i * (BarSpace+ BarHeigth), bar.barWidth, BarHeigth);

			if (bar.barSelectedWidth == 1) {
				g.setColor(Color.RED);
				g.fillRect(abstandLinks, abstandOben + i * (BarSpace+ BarHeigth), bar.barSelectedWidth, BarHeigth);
			}

		}

		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math.abs(point2.y
					- point1.y));
		}

	}

	public Bar getBar(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		int i = (y - abstandOben) / (BarSpace + BarHeigth);

		if (x >= abstandLinks && x <= this.getWidth() - abstandRechts+2 && i < bars.size())
			return bars.elementAt(i);

		return null;

	}
	
	public boolean isLabelResizing(MouseEvent e) {
		if ((e.getX() == abstandLinks || e.getX() == abstandLinks-1|| e.getX() == abstandLinks+2) && 
				(e.getY() - abstandOben)%(BarHeigth + BarSpace)<BarHeigth) {
			//System.out.println("Treffer");
			return true;
		}
		else return false;
	}
	
	
	public boolean isBarWidthResizing(MouseEvent e) {
		
		Bar bar = getBar(e);
		
		if (bar != null && (abstandLinks + bar.barWidth == e.getX() || abstandLinks + bar.barWidth == e.getX()-1||abstandLinks + bar.barWidth == e.getX()-1)
				&& 
				(e.getY() - abstandOben)%(BarHeigth + BarSpace)<BarHeigth) {
		
			return true;
		}
		else return false;
	}
	
	
    public boolean isHeightResizing(MouseEvent e) {
		
		Bar bar = getBar(e);
		
		if (bar != null && bars.indexOf(bar) == 0
				&& 
				((e.getY() - abstandOben)%(BarHeigth + BarSpace)==BarHeigth || (e.getY() - abstandOben)%(BarHeigth + BarSpace)==BarHeigth-1 ||(e.getY() - abstandOben)%(BarHeigth + BarSpace)==BarHeigth+1)) {
			//System.out.println("Treffer");
			return true;
		}
		else return false;
	}
    
    
    public boolean isSpaceResizing(MouseEvent e) {
		
		Bar bar = getBar(e);
		
		if (bar != null
				&& 
				(e.getY() - abstandOben)%(BarHeigth + BarSpace)==0 || (e.getY() - abstandOben)%(BarHeigth + BarSpace)==BarHeigth + BarSpace-1 ||(e.getY() - abstandOben)%(BarHeigth + BarSpace)==+1) {
			//System.out.println("Treffer");
			return true;
		}
		else return false;
	}

	
	
	
	
	public ISelectable getVariable(int i) {
		if (variables == null)
			return null;
		return variables.elementAt(i);
	}
	

	
	
	
	

	@Override
	public String getToolTipText(MouseEvent e) {
		if (e.isControlDown()) {
			Bar bar =  this.getBar(e);

			if (bar != null) {
				String s = bar.name;
				int selectedCount = 0, gesamtExperInBalken = 0;

				for (int j = 0; j < this.data.length; j++) {
					if (this.data[j].equals(s)) {
						if (getVariable(j) != null
								&& getVariable(j).isSelected())
							selectedCount++;
						gesamtExperInBalken++;
					}
				}

				double koeff = (double) selectedCount / gesamtExperInBalken;

				return "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>"
						+ bar.name
						+ "<FONT FACE = 'Arial'><br>"
						+ "</STRONG>"
						+ (int) Math.round(bar.barAbs*koeff)
						+ "/"
						+ bar.barAbs + "	" + "(" + Tools.round100(koeff * 100) + "%)</HTML>";

			}
		}
		return null;
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
        if (isLabelResizing(arg0) || isBarWidthResizing(arg0)) {
        	this.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR) );
        	return;
        }
  
        
        if (isHeightResizing(arg0) || isSpaceResizing(arg0)) {
        	this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR) );
        	return;
        }
       
        	this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR ) );
        
        
        
	}

	class Bar {
		BarchartPanel panel;
		String name;
		double koeff;
		int barWidth;
		double barRel;
		double selectedBalken;
		int barSelectedWidth;
		int barAbs;
		String nameShort;
		Color color;
		int selectedCount;
		Vector<ISelectable> variables = new Vector();

		public Bar(BarchartPanel panel, String name, ISelectable var) {
			this.panel = panel;
			this.name = name;
			variables.add(var);
		}
		
		public boolean select() {
			boolean selected = false;
			for (int i = 0; i < variables.size(); i++) {
				variables.elementAt(i).select(true);
				selected = true;
			}
			return selected;
		}
		

	}

	public void print() {
		// TODO Auto-generated method stub
		try {
			   PrintJob prjob = getToolkit().getPrintJob( barchart,null, null );
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
