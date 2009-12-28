package GUI;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.GeneVariable;
import Data.ISelectable;
import Data.Variable;
import GUI.BarchartPanel.Balken;

public class ListBarchart extends JFrame implements IPlot {

	Seurat seurat;

	ListBarchartPanel panel;

	JMenuItem item = new JMenuItem("");

	public ListBarchart(Seurat seurat, Data.GeneVariable var,Vector variables) {
		super(var.name);
		this.seurat = seurat;

		panel = new ListBarchartPanel(seurat,this, var,variables);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		this.setBounds(400, 0, 250, Math.min(panel.balken.size()
				* panel.BinHeigth * 2 + panel.abstandOben + 40, 600));

		this.setVisible(true);

		seurat.windows.add(this);

		item = new JMenuItem(var.name);
		seurat.windowMenu.add(item);

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
			}
		});

		panel.calculateAbstandLinks();
		this.setVisible(true);

	}

	public void updateSelection() {
		panel.updateSelection();

	}

	public void brush() {
		// TODO Auto-generated method stub
		
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		
	}

	public void print() {
		// TODO Auto-generated method stub
		panel.print();
	}

}

class ListBarchartPanel extends JPanel implements KeyListener, MouseListener,
		IPlot, MouseMotionListener {
	

	//double[] balken;

	//double[] selectedBalken;


	Seurat seurat;


	ListBarchart barChart;
	
	JMenuItem item = new JMenuItem("");

	ListBarchartPanel barchartPanel = this;

	Point point1, point2;

	Image image;

	int abstandLinks = 80;

	int abstandOben = 20;

	int BinHeigth= 20;

	int abstandString = 5;


    JPopupMenu menu;
	
	
	//double[] koeff;

	//int[] balkenWidth;

	//int[] balkenSelectedWidth;

//	String [] binStrings;
    
	Vector<Balken> balken;
	
	
	boolean updateSelection = true;
	
	
	Color GRAY = new Color(192, 192, 192);

	Vector<ISelectable> variables;
	
	
	GeneVariable geneVar;
	
	
	boolean[] selectedBalken;
	
	boolean firstPaint = true;
	

	public ListBarchartPanel(Seurat seurat, ListBarchart bar,GeneVariable var,Vector<ISelectable> variables) {

	
		this.seurat = seurat;
		this.variables = variables;
        this.geneVar = var;
        this.barChart = bar;
      

		// this.descriptionVariable = descriptionVariable;
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
		selectedBalken = new boolean[balken.size()];

		for (int i = 0; i < balken.size(); i++) {
			if (this.containsRectInRect(

			abstandLinks,

			abstandOben + i * 2 * BinHeigth,

			abstandLinks
					+ (int) Math.round((this.getWidth() - abstandLinks - 20)
							* balken.elementAt(i).balkenRel),

			abstandOben + i * 2 * BinHeigth + BinHeigth,

			point1.x, point1.y, point2.x, point2.y))
				selectedBalken[i] = true;

		}

		//seurat.dataManager.deleteSelection();
		
		 for (int i = 0; i < variables.size(); i++) {
		    	variables.elementAt(i).unselect(true);
		    }
		
		
		boolean selected = false;
		
		for (int i = 0; i < selectedBalken.length; i++) {

			
			int ID = balken.elementAt(i).ID;
			
			
			if (selectedBalken[i]) {
				selected = true;
				for (int j = 0; j < geneVar.bufferCount [ID]; j++) {
					  int geneVarIndex = geneVar.geneMitListValue.elementAt(ID).elementAt(j);
					
						if (getVariable(geneVarIndex) != null)  getVariable(geneVarIndex).select(true);
							
					
				}
				
				
			}
		}
		
		
		/*
		if (selected) {
			Vector<Variable> vars = seurat.dataManager.Experiments;
			for (int i = 0; i < vars.size();i++) {
				vars.elementAt(i).select(true);
			}
		}
*/
		
		
		
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		point2 = e.getPoint();
		
		
		if (!(e.getButton() == MouseEvent.BUTTON3 || e.isControlDown())) {

		if (point1 != null && point2 != null) {

			addSelection(point1, point2);
			
			
			for (int i = 0; i < balken.size(); i++) {

				
				
				
				
				int selectedCount = 0, gesamtExperInBalken = 0;
				
				int ID = balken.elementAt(i).ID;

				

				if (selectedBalken [i]) { 
					balken.elementAt(i).koeff = 1;
				    balken.elementAt(i).selectedCount = balken.elementAt(i).balkenAbs;
				balken.elementAt(i).balkenSelectedWidth = balken.elementAt(i).balkenWidth;
				}
				else {
					balken.elementAt(i).koeff = 0;
				    balken.elementAt(i).selectedCount = 0;
				balken.elementAt(i).balkenSelectedWidth = 0;
					
					
				}
				
				if ((this.getWidth() - abstandLinks - 20) * balken.elementAt(i).balkenRel * balken.elementAt(i).koeff>0 && (this.getWidth() - abstandLinks - 20) * 
						balken.elementAt(i).balkenRel * balken.elementAt(i).koeff<1) {
					balken.elementAt(i).balkenSelectedWidth = 1;
				}

			}
			
			
			this.repaint();
			
			this.updateSelection = false;
			

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
			
			
			JMenu sortMenu = new JMenu("Sort by ...");
			


			
			
			
	item = new  JMenuItem("Count");
			
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByCount();
				}
			});
			sortMenu.add(item);
			
			
			
	item = new  JMenuItem("Absolute selected");
			
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByAbsolutSelected();
				}
			});
			sortMenu.add(item);
			
			
			
	item = new  JMenuItem("Relative selected");
			
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByRelativeSelected();
				}
			});
			sortMenu.add(item);
			
			
	item = new  JMenuItem("Lexicographic");
			
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByLexico();
				}
			});
			sortMenu.add(item);
			
			sortMenu.addSeparator();
			
	item = new  JMenuItem("Reverse");
			
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sortByReverse();
				}
			});
			sortMenu.add(item);
			
			
			menu.add(sortMenu);
			
			
			
			/*
	item = new  JMenuItem("Dismiss");
			
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					menu.setVisible(false);
				}
			});
			menu.add(item);
			*/
			
			menu.addSeparator();
			
			 item = new JMenuItem("Print");
			    item.addActionListener(new ActionListener() {
				
			    	 public void actionPerformed(ActionEvent e) {
					// createCorrelationGenes();
					
					
					
					print();
					
				    }
		    	});
			    menu.add(item);
		
			
			menu.show(this,e.getX(), e.getY());
			
			}
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void sortByCount() {
		Vector<Balken>balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while(j < balkenTemp.size() &&  balk.balkenAbs < balkenTemp.elementAt(j).balkenAbs) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);
			
		}
		
		
		this.balken = balkenTemp;
		repaint();
	}
	
	
	
	public void sortByAbsolutSelected() {
		Vector<Balken>balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while(j < balkenTemp.size() &&  balk.selectedCount < balkenTemp.elementAt(j).selectedCount) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);
			
		}
		
		
		this.balken = balkenTemp;
		repaint();
	}
	
	
	
	public void sortByRelativeSelected() {
		Vector<Balken>balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while(j < balkenTemp.size() &&  (double)balk.selectedCount/balk.balkenAbs < (double)balkenTemp.elementAt(j).selectedCount/balkenTemp.elementAt(j).balkenAbs) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);
			
		}
		
		
		this.balken = balkenTemp;
		repaint();
	}
	
	
	public void sortByReverse() {
		Vector<Balken>balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			balkenTemp.insertElementAt(balk,0);
			
		}
		
		
		this.balken = balkenTemp;
		repaint();
	}
	
	
	
	public void sortByLexico() {
		
		
		//System.out.println(compareLexico("asdg","bsdg"));
		
		
		
		Vector<Balken>balkenTemp = new Vector();
		for (int i = 0; i < balken.size(); i++) {
			Balken balk = balken.elementAt(i);
			int j = 0;
			while(j < balkenTemp.size() &&  compareLexico(balk.name,balkenTemp.elementAt(j).name)) {
				j++;
			}
			balkenTemp.insertElementAt(balk, j);
			
		}
		
		
		this.balken = balkenTemp;
		repaint();
	}
	
	
	
	public boolean compareLexico(String a, String b) {
		int i = 0;
		
		while (a.length() > i && b.length() > i) {
			if (a.charAt(i) > b.charAt(i)) return true;
			if (a.charAt(i) < b.charAt(i)) return false;
			i++;
			
		}
		
		
		
		return false;
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
	}

	public void calculateBalken() {
		
		

		balken = new Vector();
	

		for (int i = 0; i < geneVar.bufferCount.length; i++) {
			
			Balken balk = new Balken(this,i,geneVar.stringBuffer.elementAt(i));
			balken.add(balk);
			
			balk.balkenRel =  geneVar.bufferCount [i];
			balk.balkenAbs =  geneVar.bufferCount [i];
		}

		double max = 0;
		for (int i = 0; i < balken.size(); i++) {
			if (max < balken.elementAt(i).balkenRel)
				max = balken.elementAt(i).balkenRel;
		}

		for (int i = 0; i < balken.size(); i++) {
			balken.elementAt(i).balkenRel = balken.elementAt(i).balkenRel / max;
		}

	}

	public void calculateAbstandLinks() {
		int length = 0;
		for (int i = 0; i < geneVar.bufferCount.length; i++) {
			String s = geneVar.stringBuffer.elementAt(i);
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

		if (updateSelection) {
	
		for (int i = 0; i < balken.size(); i++) {

		
			
		//	if (balken.elementAt(i).name == null) {
				
		//		String s = geneVar.stringBuffer.elementAt(i);
		//		balken.elementAt(i).name = s;
		//	}
				
				balken.elementAt(i).nameKurz = this.cutLabels(balken.elementAt(i).name, abstandLinks
					- abstandString - 3, this.getGraphics());
			
			   //  System.out.println(balken.elementAt(i).nameKurz + "_"+balken.elementAt(i).name);
			
			
			
			int selectedCount = 0, gesamtExperInBalken = 0;
			
			int ID = balken.elementAt(i).ID;

			for (int j = 0; j < geneVar.bufferCount [ID]; j++) {
				  int geneVarIndex = geneVar.geneMitListValue.elementAt(ID).elementAt(j);
				
					if (getVariable(geneVarIndex) != null && getVariable(geneVarIndex).isSelected())
						selectedCount++;
					gesamtExperInBalken++;
				
			}

			balken.elementAt(i).koeff = (double) selectedCount / gesamtExperInBalken;
			balken.elementAt(i).selectedCount = selectedCount;
			
			
			
			balken.elementAt(i).balkenWidth = (int) Math.round((this.getWidth()
					- abstandLinks - 20)
					* balken.elementAt(i).balkenRel);

			balken.elementAt(i).balkenSelectedWidth = (int) Math
					.round((this.getWidth() - abstandLinks - 20) * balken.elementAt(i).balkenRel
							* balken.elementAt(i).koeff);
			
			if ((this.getWidth() - abstandLinks - 20) * balken.elementAt(i).balkenRel * balken.elementAt(i).koeff>0 && (this.getWidth() - abstandLinks - 20) * 
					balken.elementAt(i).balkenRel * balken.elementAt(i).koeff<1) {
				balken.elementAt(i).balkenSelectedWidth = 1;
			}

		}
		
		this.repaint();
		
		
		}
		
		updateSelection = true;

	}

	@Override
	public void paint(Graphics g) {
	
		if (this.balken == null || firstPaint) {
			firstPaint = false;
			this.updateSelection(); 
		}

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

	

		for (int i = 0; i < balken.size(); i++) {

			g.setColor(Color.BLACK);
			
			
	        g.drawString( balken.elementAt(i).nameKurz,

			abstandString, abstandOben + i * 2 * BinHeigth + BinHeigth * 3 / 4

			);
	        
	        
			g.setColor(GRAY);

			g.fillRect(abstandLinks, abstandOben + i * 2 * BinHeigth, balken.elementAt(i).balkenWidth, BinHeigth);

		    g.setColor(Color.RED);

			g.fillRect(abstandLinks, abstandOben + i * 2 * BinHeigth, balken.elementAt(i).balkenSelectedWidth, BinHeigth);

			g.setColor(Color.BLACK);

			g.drawRect(abstandLinks, abstandOben + i * 2 * BinHeigth,balken.elementAt(i).balkenWidth ,	BinHeigth);
			
			if (balken.elementAt(i).balkenSelectedWidth  == 1) {
			    g.setColor(Color.RED);

				g.fillRect(abstandLinks, abstandOben + i * 2 * BinHeigth,balken.elementAt(i). balkenSelectedWidth, BinHeigth);
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
		
//		abstandString, abstandOben + i * 2 * BinHeigth + BinHeigth * 3 / 4


		int i = (y - abstandOben + BinHeigth /2) / 2 / BinHeigth;
	
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
        if (variables == null) return null;
		return variables.elementAt(i);
	}

	public String cutLabels(String s, int availablePlace, Graphics g) {
		s = s.replaceAll("\"", "");
		if (s.equals("")) return "";
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
		/*String[] split = s.split(" ");
		String cutS = "";
		if (split.length > 1) {

			for (int i = 0; i < split.length; i++)
				if (split[i].length() > 1)
					cutS += split[i].substring(0, 1);
				else
					cutS += split[i];
			for (int i = 0; i < cutS.length(); i++)
				Width += g.getFontMetrics().charWidth(cutS.charAt(i));
			if (Width < availablePlace)
				return cutS;
		}*/

		s = s.replaceAll("ck", "c");
		String cutS = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
		//	if (c != 'e' && c != 'u' && c != 'i' && c != 'o' && c != 'ü'
			//		&& c != 'a' && c != 'ö' && c != 'ä' && c != 'y')
				cutS += c;
		}

		return cutS;

	}

	@Override
	public String getToolTipText(MouseEvent e) {
		if (e.isControlDown()) {
			int i = this.getBalken(e);

			if (i != -1) {
				String s = this.balken.elementAt(i).name;
				

			    int ID = balken.elementAt(i).ID;

				return "<HTML><BODY BGCOLOR = 'WHITE'><FONT FACE = 'Verdana'><STRONG>"
						+ this.balken.elementAt(i).name
						+ "<FONT FACE = 'Arial'><br>"
						+ "</STRONG>"
						+ (int) Math.round(geneVar.bufferCount [ID] * balken.elementAt(i).koeff)
						+ "/"
						+ geneVar.bufferCount [ID]
						+ "	"
						+ "("
						+ round(balken.elementAt(i).koeff* 100)
						+ "%)</HTML>";

			}
		}
		return null;
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void brush() {
		// TODO Auto-generated method stub
		
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

	class Balken {
		ListBarchartPanel panel;
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
		
		int ID;
		
		
		public Balken(ListBarchartPanel panel,int ID, String name) {
			this.panel = panel;
			this.name = name;
			this.ID = ID;
		}
		
	}





	public void print() {
		// TODO Auto-generated method stub
		try {
			   PrintJob prjob = getToolkit().getPrintJob( barChart,null, null );
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