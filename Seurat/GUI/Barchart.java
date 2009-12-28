package GUI;

import java.util.*;
import javax.swing.*;

import Tools.Tools;

import java.awt.event.*;
import java.awt.*;

import Data.CGHVariable;
import Data.DescriptionVariable;
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
		this.setBounds(350, 0, 250, Math.min(panel.balken.size()
				* panel.BinHeigth * 2 + panel.abstandOben + 40, 600));

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
		this.setBounds(350, 0, 250, Math.min(panel.balken.size()
				* panel.BinHeigth * 2 + panel.abstandOben + 40, 600));

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

	int abstandOben = 20;

	int BinHeigth = 20;

	int abstandString = 5;

	boolean firstPaint = true;
	
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

	Vector<Balken> balken;
	
	

	public BarchartPanel(Barchart barchart, Seurat seurat, String name,
			Vector<ISelectable> variables, String[] data) {

		this.data = data;
		this.seurat = seurat;
		this.variables = variables;
		this.barchart = barchart;
		
		
		

		this.calculateBalken();

		this.setPreferredSize(new Dimension(200, balken.size() * BinHeigth * 2
				+ abstandOben));
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
		for (int i = 0; i < balken.size(); i++) {
			if (balken.elementAt(i).name.equals(s))
				return i;
		}
		return -1;
	}

	

	public void brush() {

		for (int i = 0; i < balken.size(); i++) {

			float value = ((float) i / balken.size() + colorShift);
			if (value > 1)
				value -= 1;

			balken.elementAt(i).color = Color
					.getHSBColor(value, 1, (float) 0.7);

			for (int j = 0; j < data.length; j++) {
				int num = indexOf(data[j]);
				if (i == num && this.getVariable(j) != null) {
					if (((Variable) this.getVariable(j)).getBarchartToColors()
							.indexOf(barchart) == -1) {

						((Variable) this.getVariable(j)).getColors().add(balken
								.elementAt(i).color);
						((Variable) this.getVariable(j)).getColorNames().add(balken
								.elementAt(i).name);
						((Variable) this.getVariable(j)).getBarchartToColors()
								.add(barchart);
					} else {

						int index = ((Variable) this.getVariable(j)).getBarchartToColors()
								.indexOf(barchart);
						((Variable) this.getVariable(j)).getColors().set(index,
								balken.elementAt(i).color);

					}

				}
			}

		}

	}

	public void removeColoring() {

		for (int i = 0; i < this.variables.size(); i++) {
			int index = ((Variable) this.getVariable(i)).getBarchartToColors()
					.indexOf(barchart);
			if (index != -1) {
				((Variable) this.getVariable(i)).getColors().remove(index);
				((Variable) this.getVariable(i)).getColorNames().remove(index);
				((Variable) this.getVariable(i)).getBarchartToColors()
						.remove(index);
			}
		}

		for (int i = 0; i < balken.size(); i++) {
			balken.elementAt(i).color = GRAY;
			this.colorShift = 0;

		}

	}

	public void addSelection(Point point1, Point point2) {
		boolean[] selectedBalken = new boolean[balken.size()];

		for (int i = 0; i < balken.size(); i++) {
			if (Tools.containsRectInRect(

			abstandLinks,

			abstandOben + i * 2 * BinHeigth,

			abstandLinks
					+ (int) Math.round((this.getWidth() - abstandLinks - 20)
							* balken.elementAt(i).balkenRel),

			abstandOben + i * 2 * BinHeigth + BinHeigth,

			point1.x, point1.y, point2.x, point2.y))
				selectedBalken[i] = true;

		}

		seurat.dataManager.deleteSelection();
	    /*for (int i = 0; i < variables.size(); i++) {
	    	variables.elementAt(i).unselect(true);
	    }*/
	
			
		
		boolean selected = false;	
			
		for (int i = 0; i < selectedBalken.length; i++) {

			if (selectedBalken[i]) {
				for (int j = 0; j < data.length; j++) {
					int num = indexOf(data[j]);
					if (i == num) {
						this.getVariable(j).select(true);
						selected = true;
					}
				}
			}
		}
		
		
		/*
		
		if (selected) { 
		if (variables.elementAt(0).isGene() || variables.elementAt(0).isClone()) {
			seurat.dataManager.selectExperiments(); 
		}
		else {
			seurat.dataManager.selectGenesClones(); 
	    }
		
		
		
		
		}*/

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		point2 = e.getPoint();

		if (!(e.getButton() == MouseEvent.BUTTON3 || e.isControlDown())) {

			
			//seurat.dataManager.deleteSelection();
			if (point1 != null && point2 != null) {

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
		point2 = e.getPoint();
		this.repaint();

	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
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

		balken = new Vector();

		Vector<String> barStrings = new Vector();

		for (int i = 0; i < data.length; i++) {
			boolean add = true;
			for (int j = 0; j < barStrings.size(); j++) {
				if (barStrings.elementAt(j).equals(data[i])) {
					add = false;
				}
			}
			if (add)
				barStrings.add(data[i]);
		}

		Collections.sort(barStrings);

		for (int i = 0; i < barStrings.size(); i++) {
			balken.add(new Balken(this, barStrings.elementAt(i)));
		}

		for (int i = 0; i < data.length; i++) {
			int num = barStrings.indexOf(data[i]);
			balken.elementAt(num).balkenRel++;
			balken.elementAt(num).balkenAbs++;
		}

		double max = 0;
		for (int i = 0; i < balken.size(); i++) {
			if (max < balken.elementAt(i).balkenRel)
				max = balken.elementAt(i).balkenRel;
		}

		for (int i = 0; i < balken.size(); i++) {
			balken.elementAt(i).balkenRel /= max;
		}

		for (int i = 0; i < balken.size(); i++) {
			balken.elementAt(i).color = GRAY;
		}

	}

	public void calculateAbstandLinks() {
		int length = 0;
		for (int i = 0; i < this.balken.size(); i++) {
			String s = this.balken.elementAt(i).name;
			int Width = 0;
			for (int j = 0; j < s.length(); j++)
				Width += this.getGraphics().getFontMetrics().charWidth(
						s.charAt(j));

			if (length < Width)
				length = Width;

		}

		this.abstandLinks = Math.min(length + 10, 80);
	}

	public void updateSelection() {

		// if (binStrings == null) this.binStrings = new String [balken.length];

		for (int i = 0; i < balken.size(); i++) {

			String s = balken.elementAt(i).name;

			if (balken.elementAt(i).nameKurz == null)
				balken.elementAt(i).nameKurz = this.cutLabels(s, abstandLinks
						- abstandString - 3, this.getGraphics());

			int selectedCount = 0, gesamtExperInBalken = 0;

			for (int j = 0; j < this.data.length; j++) {
				if (this.data[j].equals(s)) {
					if (getVariable(j).isSelected())
						selectedCount++;
					gesamtExperInBalken++;
				}
			}

			balken.elementAt(i).koeff = (double) selectedCount
					/ gesamtExperInBalken;
			balken.elementAt(i).selectedCount = selectedCount;

			balken.elementAt(i).balkenWidth = (int) Math.round((this.getWidth()
					- abstandLinks - 20)
					* balken.elementAt(i).balkenRel);

			balken.elementAt(i).balkenSelectedWidth = (int) Math
					.round((this.getWidth() - abstandLinks - 20)
							* balken.elementAt(i).balkenRel
							* balken.elementAt(i).koeff);

			if ((this.getWidth() - abstandLinks - 20)
					* balken.elementAt(i).balkenRel * balken.elementAt(i).koeff > 0
					&& (this.getWidth() - abstandLinks - 20)
							* balken.elementAt(i).balkenRel
							* balken.elementAt(i).koeff < 1) {
				balken.elementAt(i).balkenSelectedWidth = 1;
			}

		}

		this.repaint();

	}

	public void sortByCount() {
		Vector<Balken> balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size()
					&& balk.balkenAbs < balkenTemp.elementAt(j).balkenAbs) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);

		}

		this.balken = balkenTemp;
		repaint();
	}

	public void sortByAbsolutSelected() {
		Vector<Balken> balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size()
					&& balk.selectedCount < balkenTemp.elementAt(j).selectedCount) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);

		}

		this.balken = balkenTemp;
		repaint();
	}

	public void sortByRelativeSelected() {
		Vector<Balken> balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size()
					&& (double) balk.selectedCount / balk.balkenAbs < (double) balkenTemp
							.elementAt(j).selectedCount
							/ balkenTemp.elementAt(j).balkenAbs) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);

		}

		this.balken = balkenTemp;
		repaint();
	}

	public void sortByReverse() {
		Vector<Balken> balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			balkenTemp.insertElementAt(balk, 0);

		}

		this.balken = balkenTemp;
		repaint();
	}

	public void sortByLexico() {
		
		if (barchart.isChr) {
			sortCHR();
			return;
		}

		Vector<Balken> balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while (j < balkenTemp.size()
					&& compareLexico(balk.name, balkenTemp.elementAt(j).name)) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);

		}

		this.balken = balkenTemp;
		repaint();
	}
	
	
public void sortCHR() {
		

	
		for (int i = 0; i < balken.size(); i++) {
			for (int j = 0; j < balken.size(); j++) {
			
			Balken balk1 = balken.elementAt(i);
			Balken balk2 = balken.elementAt(j);
		
			if ( compareCHR(balk1.name, balk2.name)) {
				if (i<j) {
					balken.set(j,balk1);
					balken.set(i,balk2);
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

		if (this.balken == null || firstPaint) {
			this.updateSelection();
			firstPaint = false;
		}

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for (int i = 0; i < balken.size(); i++) {

			g.setColor(Color.BLACK);

			g.drawString(balken.elementAt(i).nameKurz,

			abstandString, abstandOben + i * 2 * BinHeigth + BinHeigth * 3 / 4

			);

			g.setColor(balken.elementAt(i).color);

			g.fillRect(abstandLinks, abstandOben + i * 2 * BinHeigth, balken
					.elementAt(i).balkenWidth, BinHeigth);

			g.setColor(Color.RED);

			g.fillRect(abstandLinks, abstandOben + i * 2 * BinHeigth, balken
					.elementAt(i).balkenSelectedWidth, BinHeigth);

			g.setColor(Color.BLACK);

			g.drawRect(abstandLinks, abstandOben + i * 2 * BinHeigth, balken
					.elementAt(i).balkenWidth, BinHeigth);

			if (balken.elementAt(i).balkenSelectedWidth == 1) {
				g.setColor(Color.RED);

				g.fillRect(abstandLinks, abstandOben + i * 2 * BinHeigth,
						balken.elementAt(i).balkenSelectedWidth, BinHeigth);
			}

		}

		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
					point2.y), Math.abs(point2.x - point1.x), Math.abs(point2.y
					- point1.y));
		}

	}

	public int getBalken(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		int i = (y - abstandOben) / 2 / BinHeigth;

		if (x > abstandLinks && x < this.getWidth() - 20)
			return i;

		return -1;

	}

	public String round(double zahl) {
		if (zahl == Math.round(zahl))
			return "" + zahl;
		return "" + (double) Math.round(zahl * 100) / 100;
	}

	public ISelectable getVariable(int i) {
		if (variables == null)
			return null;
		return variables.elementAt(i);
	}

	public String cutLabels(String s, int availablePlace, Graphics g) {
		s = s.replaceAll("\"", "");
		String ss = this.cutLabelsHelp(s, availablePlace, g);
		if (ss.length() < 5)
			return ss;

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
		/*
		 * String[] split = s.split(" "); String cutS = ""; if (split.length >
		 * 1) {
		 * 
		 * for (int i = 0; i < split.length; i++) if (split[i].length() > 1)
		 * cutS += split[i].substring(0, 1); else cutS += split[i]; for (int i =
		 * 0; i < cutS.length(); i++) Width +=
		 * g.getFontMetrics().charWidth(cutS.charAt(i)); if (Width <
		 * availablePlace) return cutS; }
		 */

		s = s.replaceAll("ck", "c");
		String cutS = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// if (c != 'e' && c != 'u' && c != 'i' && c != 'o' && c != '¸'
			// && c != 'a' && c != 'ˆ' && c != '‰' && c != 'y')
			cutS += c;
		}

		return cutS;

	}

	@Override
	public String getToolTipText(MouseEvent e) {
		if (e.isControlDown()) {
			int i = this.getBalken(e);

			if (i != -1) {
				String s = balken.elementAt(i).name;
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
						+ balken.elementAt(i).name
						+ "<FONT FACE = 'Arial'><br>"
						+ "</STRONG>"
						+ (int) Math.round(balken.elementAt(i).balkenAbs
								* koeff)
						+ "/"
						+ balken.elementAt(i).balkenAbs
						+ "	" + "(" + round(koeff * 100) + "%)</HTML>";

			}
		}
		return null;
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	class Balken {
		BarchartPanel panel;
		String name;
		double koeff;
		int balkenWidth;
		double balkenRel;
		double selectedBalken;
		int balkenSelectedWidth;
		int balkenAbs;
		String nameKurz;
		Color color;
		int selectedCount;

		public Balken(BarchartPanel panel, String name) {
			this.panel = panel;
			this.name = name;
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
