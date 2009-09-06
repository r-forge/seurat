package GUI;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.*;

class ChrView extends JFrame implements MatrixWindow, IPlot {
	

	Seurat seurat;

	ChrPanel panel;

	JMenuItem item;

	ChrView globalView = this;


	DataManager dataManager;

	Vector<Chromosome> Chromosomes;
	Vector<CGHVariable> Cases;
	
	JPanel infoPanel;
	
	
	
	
	
	
	
	
	
	
	

	public ChrView(Seurat seurat, String name, Vector<Chromosome> chr,
			Vector<CGHVariable> Cases) {
		super(name + " ");
		
		System.out.println("ChrView  ");
		
		item = new JMenuItem(name);

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.Chromosomes = chr;
		this.Cases = Cases;
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEtchedBorder());
		p.setLayout(new GridLayout(chr.size(),1));
			
		
		for (int i = 0; i < chr.size(); i++) {
		panel = new ChrPanel(seurat,this, chr.elementAt(i),Cases);
        panel.setPreferredSize(new Dimension(800,140));
		p.add(panel,BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEtchedBorder());
		}
		
		infoPanel = new JPanel();
		
		
		addKeyListener(panel); 
		infoPanel.addKeyListener(panel); 
	
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JLabel label = new JLabel("Zoom: ");
		infoPanel.add(label);
		JButton btn = new JButton("+");
		btn.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			      int width = panel.getWidth();
			      panel.setPreferredSize(new Dimension((int)Math.round(width*1.33),panel.getHeight()));
			      panel.setSize(new Dimension((int)Math.round(width*1.33),panel.getHeight()));
			      
			      
			      if ((int)Math.round(width*1.66) < 1200) {
			    	  setSize(new Dimension((int)Math.round(width*1.33)+40,getHeight()));
				     
			      }
			      
			      else   setSize(new Dimension(1200,getHeight()));
			      
			      
			      updateSelection();
			      setVisible(true);
			}
		}
		);
		
		infoPanel.add(btn);
		
		
		btn = new JButton("-");
		btn.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			      int width = panel.getWidth();
			      panel.setPreferredSize(new Dimension((int)Math.round(width*0.66),panel.getHeight()));
			      panel.setSize(new Dimension((int)Math.round(width*0.66),panel.getHeight()));
			      
			      if ((int)Math.round(width*0.66) < getWidth()) {
			    	  setSize(new Dimension((int)Math.round(width*0.66)+40,getHeight()));
				     
			      }
			      updateSelection();
			      setVisible(true);
			}
		}
		);
		
		infoPanel.add(btn);
		
		
		
		
		
		
		infoPanel.setBorder(BorderFactory.createEtchedBorder());
		//this.getContentPane().add(infoPanel,BorderLayout.SOUTH);
		
		
		
		this.addMouseListener(panel);
		JScrollPane pane = new JScrollPane(p);
		pane.addKeyListener(panel);
        this.getContentPane().add(pane, BorderLayout.CENTER);
		
			this.setBounds(200,200,1020,230);

		// this.getContentPane().setLayout(new BorderLayout());
		
		
		this.setVisible(true);

		seurat.windows.add(this);

		seurat.windowMenu.add(item);

	

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

				updateSelection();
				
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

	public void applyNewPixelSize(int pixelW, int PixelH) {
		// TODO Auto-generated method stub
		
	}

	public void applyNewPixelSize() {
		// TODO Auto-generated method stub
		updateSelection();
	}

	public void setModel(int model) {
		// TODO Auto-generated method stub
		
	}

}

class ChrPanel extends JPanel implements MouseListener, IPlot,
		MouseMotionListener,KeyListener {
	DataManager dataManager;

	Seurat seurat;

	int abstandLinks = 10;

	int abstandOben = 4;
	
	int pixelForCytoBand = 44;
	
	int CytoWidth = 12;

	Chromosome chr;
	Vector<CGHVariable> Cases;

	

	Point point1, point2;
	Color SelectionColor = Color.black;
	

	ChrView cView;

	
	int [] posHist;
	
	Image image;
	
	int [] negHist;
	int [] negSelectionHist;
	int [] posSelectionHist;
	
	int [] Position;
	
	
	Vector <Double> selection;
	

	public ChrPanel(Seurat seurat, ChrView view, Chromosome chr,
			Vector<CGHVariable> Cases) {

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.cView = view;
		

		this.chr = chr;
		this.Cases = Cases;

		// this.originalOrderSpalten = orderSpalten;

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		addKeyListener(this);
		
	}

	
	public double getMax(int [] h1, int [] h2) {
		double max = -100;
		for (int i = 0; i < h1.length; i++) {
		      if (h1 [i]>max) max = h1 [i];	
		}
		for (int i = 0; i < h2.length; i++) {
		      if (h2 [i]>max) max = h2 [i];	
		}
		return max;
		
	}
	
	

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		point1 = e.getPoint();
	}

	public void mouseExited(MouseEvent e) {
	}

	public void updateSelection() {
    
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2;
		int len = chr.Clones.size();
	
		chr.calculateLength();
		
	//	System.out.println("-->>"+chr.length);
		
		
		double [] posDHist = new double [len];
		double [] negDHist = new double [len];

		double [] posSelectionDHist = new double [len];
		double [] negSelectionDHist = new double [len];
		
		Position = new int [len];
		
		
	    java.util.Iterator<Clone> iter = chr.Clones.iterator();
	    int i = 0;
		while (iter.hasNext()) {
			Clone clone = iter.next();
			Position [i] = (int)Math.round(clone.NucleoPosition *(this.getWidth()-2*abstandLinks)/chr.length); 
			  	
		    	for (int j = 0; j < Cases.size(); j++) {
		    		
		    		
		     		
		    		CGHVariable cghVar = Cases.elementAt(j);
		    		if (cghVar.getValue(clone.getID())>0)  {
		    			posDHist [i] = posDHist [i] + cghVar.getValue(clone.getID());
		    		    if (clone.isSelected() && cghVar.isSelected()) 	posSelectionDHist [i] = posSelectionDHist [i] + 1;
		    		
		    		}
		    		if (cghVar.getValue(clone.getID())<0)  {
		    			negDHist [i] = negDHist [i] - cghVar.getValue(clone.getID());
		    		    if (clone.isSelected() && cghVar.isSelected()) 	negSelectionDHist [i] = negSelectionDHist [i] - 1;
				    	
		    		}
				    
		    	}
		    	
		    i++;
		    
		}
		
		
		double max = getMax(posDHist,negDHist);
		
		posHist = new int [len];
		negHist = new int [len];
		posSelectionHist = new int [len];
		negSelectionHist = new int [len];

		
		
		for (i = 0; i < posDHist.length; i++) {
		      posHist [i] = (int) Math.round(posDHist [i] * width / max);
		      negHist [i] = (int) Math.round(negDHist [i] * width / max);
		      posSelectionHist [i] = (int) Math.round(posSelectionDHist [i] * width / max);
		      negSelectionHist [i] = -(int) Math.round(negSelectionDHist [i] * width / max);
		}
		 

		
		
			
	
	    
	    
	    selection = new Vector();
	    
	    for (i = 0; i < chr.CytoBands.size(); i++) {
	    	Vector<Clone> clones = chr.CytoBands.elementAt(i).Clones;
	        double count = 0;
	    	for (int j = 0; j < clones.size(); j++) {
	    		if (clones.elementAt(j).isSelected()) count++;
	    	}
	    	selection.add(new Double(count/clones.size()));
	    }
				
		
		
		
        this.setSize(this.getWidth(), this.getHeight());
		this.repaint();
	}

	
	
	public double getMax(double [] h1, double [] h2) {
		double max = -100;
		for (int i = 0; i < h1.length; i++) {
		      if (h1 [i]>max) max = h1 [i];	
		}
		for (int i = 0; i < h2.length; i++) {
		      if (h2 [i]>max) max = h2 [i];	
		}
		return max;
		
	}
	
	
	
	
	
	
	
	
	
	
	public void mouseReleased(MouseEvent e) {

		point2 = e.getPoint();
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
		     return;
		}

		if (point1 != null && point2 != null) {
			
			
            if (!e.isShiftDown()) {
			dataManager.deleteSelection();
			selection = null;
            }
			
			selectRectangle(point1.x, point1.y, point2.x, point2.y);
          //  seurat.dataManager.selectExperiments(); 
			
			
		}	
			

			point1 = null;
			point2 = null;
			seurat.repaintWindows();
		

		this.repaint();
	}

	public void selectRectangle(int xx1, int yy1, int xx2, int yy2) {
		
		
		int start = (xx1-abstandLinks);
		int end = (xx2-abstandLinks);
		
		
		
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2;
				
		int len = (this.getWidth()-2*abstandLinks);
		

		//public boolean containsRectInRect(int x1, int y1, int x2, int y2, int Sx1,
			//	int Sy1, int Sx2, int Sy2) {
		
		for (int k = 0; k < chr.Clones.size(); k++) {
		    	 Clone clone = chr.Clones.elementAt(k);
		    	 
		    	 
		    //	 int startC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				// int endC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				    
				
				
				    
				 if (containsRectInRect(abstandLinks + Position[k], abstandOben + width - posHist [k], abstandLinks + Position[k],  abstandOben + width, 
						 xx1, yy1, xx2, yy2)) {
					 clone.select(true);
					 selectPositiveCGHs(clone);
				 }
				 
				 if (containsRectInRect(abstandLinks + Position[k], abstandOben + width, abstandLinks + Position[k],  abstandOben + width + negHist [k], 
						 xx1, yy1, xx2, yy2)) {
					 clone.select(true);
					 selectNegativeCGHs(clone);
				 }
		     }	
		
		
		
		if (yy2 > this.getHeight() - pixelForCytoBand) {
			
			
			
			
			
			for (int i = start; i < end; i++) {
			     for (int k = 0; k < chr.CytoBands.size(); k++) {
			    		 
			    	 
			    	 int startC = (int)Math.round(chr.CytoBands.elementAt(k).Start*len/chr.length);
					 int endC = (int)Math.round(chr.CytoBands.elementAt(k).End*len/chr.length);
					    
					    
			    	 if (!chr.CytoBands.elementAt(k).name.equals("NA") &&  chr.CytoBands.elementAt(k).End!= seurat.dataManager.NA &&
			    			 (end >= startC  && end < endC) || (start >= startC  && start < endC) || (startC >= start  && startC < end) || (endC >= start  && endC < end)) {
			    
			    		// System.out.println("CytoBand Name: "+chr.CytoBands.elementAt(k));
			    			if (!chr.CytoBands.elementAt(k).name.equals("NA")) chr.CytoBands.elementAt(k).select(true,Cases);
			    		
			    	 }
			     }	
			}
			
			
			selectCGHs(Cases);
			
			
			
			
			
			
			
			
			
		}
		
		

		this.repaint();

	}
	
	
	
	
	
public void selectPoint(int xx1, int yy1) {
		
		
	    int xx2 = xx1+1;
	    int yy2 = yy1;
	
		int start = (xx1-abstandLinks);
		int end = (xx2-abstandLinks);
		
		
		
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2;
				
		int len = (this.getWidth()-2*abstandLinks);
		

		//public boolean containsRectInRect(int x1, int y1, int x2, int y2, int Sx1,
			//	int Sy1, int Sx2, int Sy2) {
		
		for (int k = 0; k < chr.Clones.size(); k++) {
		    	 Clone clone = chr.Clones.elementAt(k);
		    	 
		    	 
		    //	 int startC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				// int endC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				    
				
				
				    
				 if (isPointInRect(xx1,yy1,abstandLinks + Position[k], abstandOben + width - posHist [k], abstandLinks + Position[k],  abstandOben + width)) {
					 clone.select(true);
					 selectPositiveCGHs(clone);
				 }
				 
				 if (isPointInRect(xx1,yy1,abstandLinks + Position[k], abstandOben + width, abstandLinks + Position[k],  abstandOben + width + negHist [k])) {
					 clone.select(true);
					 selectNegativeCGHs(clone);
				 }
		     }	
		
		
		
	
	    
	    
		if (yy2 > this.getHeight() - pixelForCytoBand) {
			
			
			
			
			
			for (int i = start; i < end; i++) {
			     for (int k = 0; k < chr.CytoBands.size(); k++) {
			    		 
			    	 
			    	 int startC = (int)Math.round(chr.CytoBands.elementAt(k).Start*len/chr.length);
					 int endC = (int)Math.round(chr.CytoBands.elementAt(k).End*len/chr.length);
					    
					    
			    	 if (!chr.CytoBands.elementAt(k).name.equals("NA") &&  chr.CytoBands.elementAt(k).End!= seurat.dataManager.NA &&
			    			 (end >= startC  && end < endC) || (start >= startC  && start < endC) || (startC >= start  && startC < end) || (endC >= start  && endC < end)) {
			    
			    		// System.out.println("CytoBand Name: "+chr.CytoBands.elementAt(k));
			    			if (!chr.CytoBands.elementAt(k).name.equals("NA")) chr.CytoBands.elementAt(k).select(true,Cases);
			    		
			    	 }
			     }	
			}
			
			
			selectCGHs(Cases);
			
			
			
			
			
			
			
			
			
		}
		
		

		this.repaint();

	}




	
	
	public void selectCGHs(Vector<CGHVariable> Cases) {
		for (int i = 0; i < Cases.size(); i++) {
			CGHVariable var = Cases.elementAt(i);
			if (true) {
				var.select(true);
			}
		}
	}
	
	
	
	
	
	
	public void selectPositiveCGHs(Clone clone) {
		for (int i = 0; i < Cases.size(); i++) {
			CGHVariable var = Cases.elementAt(i);
			if (var.getValue(clone.getID()) !=seurat.dataManager.NA &&  var.getValue(clone.getID())>0) {
				var.select(true);
			}
		}
	}
	
	
	
	public void selectNegativeCGHs(Clone clone) {
		
		//System.out.println("Select Clone: " + clone.getName());
		
		for (int i = 0; i < Cases.size(); i++) {
			CGHVariable var = Cases.elementAt(i);
			if (var.getValue(clone.getID()) !=seurat.dataManager.NA &&  var.getValue(clone.getID())<0) {
				var.select(true);
				
				
			}
		}
	}
	
	
	
	
	

	public void mouseClicked(MouseEvent e) {

		point1 = e.getPoint();
		
	    if (e.getClickCount() == 2) {
	    	dataManager.deleteSelection();
	        seurat.repaintWindows();
	    }else 
	    {
		
		   if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

		
		  }else {
			  System.out.println("Select Point");
			  dataManager.deleteSelection();
		      
			  selectPoint(e.getX(), e.getY());
			  seurat.repaintWindows();
		  }
		   
		   
		   
	    }   

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
	
	
	
	
	
	

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		point2 = e.getPoint();

		
		this.repaint();

	}

	@Override
	public String getToolTipText(MouseEvent e) {
		
		int x = e.getX();
		int y = e.getY();
		
		int start = (x-abstandLinks);
		int end = (x+1-abstandLinks);
		
		
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2;
		
		int len = (this.getWidth()-2*abstandLinks);
		



		if (e.isControlDown()) {
			
			
		Vector<Clone> Clones = new Vector();	
		
		

		for (int k = 0; k < chr.Clones.size(); k++) {
		    	 Clone clone = chr.Clones.elementAt(k);
		    	 
		    	 
		    //	 int startC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				// int endC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				    
				
				
				    
				 if (isPointInRect(x,y,abstandLinks + Position[k], abstandOben + width - posHist [k], abstandLinks + Position[k],  abstandOben + width)) {
					 Clones.add(clone);
					 
					 
					 
				 }
				 
				 if (isPointInRect(x,y,abstandLinks + Position[k], abstandOben + width, abstandLinks + Position[k],  abstandOben + width + negHist [k])) {
					 Clones.add(clone);
					
				 }
		}
		
		
		     int clonesCount = Clones.size();
		     if (clonesCount < 0) {
		     int selected = 0;
		     for (int i = 0; i < Clones.size(); i++) if (Clones.elementAt(i).isSelected()) selected++;
		     
		     String s = "";
		     int proz = (selected*10000/clonesCount);
		     s+= "Clones selected: " +selected + "/"+clonesCount+"\r"+((double)proz/100)+"%";
	         return null; 
		     }
	    
		     
	         
	         
		if (y > this.getHeight() - pixelForCytoBand) {
			
			
			
			
			
			for (int i = start; i < end; i++) {
			     for (int k = 0; k < chr.CytoBands.size(); k++) {
			    		 
			    	 
			    	 int startC = (int)Math.round(chr.CytoBands.elementAt(k).Start*len/chr.length);
					 int endC = (int)Math.round(chr.CytoBands.elementAt(k).End*len/chr.length);
					    
					    
			    	 if (!chr.CytoBands.elementAt(k).name.equals("NA") &&  chr.CytoBands.elementAt(k).End!= seurat.dataManager.NA &&
			    			 (end >= startC  && end < endC) || (start >= startC  && start < endC) || (startC >= start  && startC < end) || (endC >= start  && endC < end)) {
			    
			    		// System.out.println("CytoBand Name: "+chr.CytoBands.elementAt(k));
			    			if (!chr.CytoBands.elementAt(k).name.equals("NA")) {
			    				
			    				int selected = 0;
			    			     for (int ii = 0; ii < chr.CytoBands.elementAt(k).Clones.size(); ii++) if (chr.CytoBands.elementAt(k).Clones.elementAt(ii).isSelected()) selected++;
			    			    
			    				 int proz = (selected*10000/chr.CytoBands.elementAt(k).Clones.size());
			    			
			    				String s = "" + chr.CytoBands.elementAt(k).name+"\r Selected: "+  selected+"/"+chr.CytoBands.elementAt(k).Clones.size() +"   "+((double)proz/100)+"%";
			    				return s;
			    			}
			    		
			    	 
			    	 }}	
			}


		}
		}
		
		
		
		return null;
	}

	

	 

	@Override
	public void paint(Graphics gg) {
		
		
	 		
	    image = this.createImage(this.getWidth(),this.getHeight());
	    //Graphics gImage = image.getGraphics();//
	    //gImage.setColor(glyphs.StandardColor);
	    
	    Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
		                      RenderingHints.VALUE_ANTIALIAS_ON);
		
	 	
	 	
		
		 g.setColor(Color.WHITE);
		    g.fillRect(0,0,this.getWidth(),this.getHeight());
		    
		    
		    
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.BLACK);
		
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2;
		int len = (this.getWidth()-2*abstandLinks);
	
		
	    if (posHist == null) updateSelection();
	    
		for (int j = 0; j < posHist.length; j++) {
				g.setColor(new Color(255,0,0, 105+posHist [j]*150/2/width));
			//	g.setColor(Color.BLUE);
				g.fillRect(abstandLinks + Position[j],
						   abstandOben + width - posHist [j],
						   1,
				           posHist [j]
				           );
				
				g.setColor(Color.BLUE);
				
				g.fillRect(abstandLinks + Position[j],
						   abstandOben + width - posSelectionHist [j],
						   1,
				           posSelectionHist [j]
				           );
				
		}
			
		
		for (int j = 0; j < negHist.length; j++) {
			g.setColor(new Color(0,255,0,105+ negHist [j]*150/2/width));
		//	g.setColor(Color.RED);
			g.fillRect(abstandLinks + Position[j],
					   abstandOben + width,
					   1,
			           negHist [j]
			           );
			
		//	System.out.println(negSelectionHist [j]);
			
			g.setColor(Color.BLUE);
			g.fillRect(abstandLinks + Position[j],
					   abstandOben + width,
					   1,
			           negSelectionHist [j]
			           );
			
			
	    }
			
						
		g.setFont(new Font("sansserif", 0, 8));
			
		
		
		
		for (int i = 0; i < chr.CytoBands.size(); i++) {
				g.setColor(Color.BLACK);
			//	System.out.println(chr.CytoBands.elementAt(i).name + " " + chr.CytoBands.elementAt(i).Start + " "+chr.CytoBands.elementAt(i).End + " " + chr.length+ "   ");
			//	System.out.println((int)Math.round(abstandOben +  chr.CytoStart.elementAt(i)*(this.getHeight()-abstandOben)/chr.length)+ 
		//				"    "+
			//			(int)Math.round((chr.CytoEnd.elementAt(i)-chr.CytoStart.elementAt(i))*(this.getHeight()-abstandOben)/chr.length)
				//	);
				
			//	System.out.println(chr.CytoStart.elementAt(i) +  "   "+ chr.CytoEnd.elementAt(i));
				
				
				
		if (!chr.CytoBands.elementAt(i).name.contains("NA") && chr.CytoBands.elementAt(i).Start != seurat.dataManager.NA) {
			
			
			g.rotate(3*Math.PI/2);
			
	    	g.drawString(cutCytoBand(chr.CytoBands.elementAt(i).name),
					1-getHeight(),
				4+(int)Math.round(abstandLinks +  (chr.CytoBands.elementAt(i).Start+chr.CytoBands.elementAt(i).End)*(this.getWidth()-2*abstandLinks)/chr.length/2)
				);
		    
	    	g.rotate(Math.PI/2);
	    	
	    	
	    	
			
				
				g.setColor(Color.getHSBColor(0,0, (float)0.5));
				
	    	    g.fillRect(
						
						(int)Math.round(abstandLinks +  chr.CytoBands.elementAt(i).Start*(this.getWidth()-2*abstandLinks)/chr.length),
						this.getHeight() - pixelForCytoBand,
						(int)Math.round((chr.CytoBands.elementAt(i).End-chr.CytoBands.elementAt(i).Start)*(this.getWidth()-2*abstandLinks)/chr.length)
						,CytoWidth
						);
	    	    
	    	    g.setColor(Color.RED);
	    	    
	    	   // System.out.println("Selection " + selection.elementAt(i));
	    	    
                g.fillRect(
						
						 (int)Math.round(abstandLinks +  chr.CytoBands.elementAt(i).Start*(this.getWidth()-2*abstandLinks)/chr.length),
						 CytoWidth - (int)Math.round(CytoWidth * selection.elementAt(i)) + this.getHeight() - pixelForCytoBand,
						(int)Math.round((chr.CytoBands.elementAt(i).End-chr.CytoBands.elementAt(i).Start)*(this.getWidth()-2*abstandLinks)/chr.length)
						,(int)Math.round(CytoWidth * selection.elementAt(i))
						);
 
 
				 
			//	System.out.println(chr.CytoStart.elementAt(i) +  "   "+ chr.CytoEnd.elementAt(i)   +  "    "  +(float)chr.CytoCount.elementAt(i) + "    "+chr.CytoMaxCount);
				
				
				g.setColor(Color.BLACK);
	g.drawRect(  
						
						(int)Math.round(abstandLinks +  chr.CytoBands.elementAt(i).Start*(this.getWidth()-2*abstandLinks)/chr.length),
						this.getHeight() - pixelForCytoBand,
						(int)Math.round((chr.CytoBands.elementAt(i).End-chr.CytoBands.elementAt(i).Start)*(this.getWidth()-2*abstandLinks)/chr.length)
						,CytoWidth
						);
				
				
			
			
		
		
		}
	}
		
		
		
		
         // Paint Chromosome....			
			
				g.setColor(Color.BLACK);
		
			//	System.out.println("Chromosome Center  "+ chr.Center +  "  Chromosome Length  " +chr.length);
				
				
			    g.drawRect(-2+ abstandLinks, 
			    		this.getHeight() - pixelForCytoBand-2,
			            -1+(int)Math.round((this.getWidth()-2*abstandLinks) * (double)(chr.Center/chr.length)),
			            CytoWidth+4
			    		);	
			    
			    g.drawRect(1+3+ abstandLinks+ (int)Math.round((this.getWidth()-2*abstandLinks) * (double)(chr.Center/chr.length)), 
			    		this.getHeight() - pixelForCytoBand-2,
			    	    -2+(int)Math.round((this.getWidth()-2*abstandLinks) * ((double)((chr.length-chr.Center)/chr.length))),
			    	    CytoWidth+4
			    		);	
			    
			    

			    g.drawRect(-2+ abstandLinks, 
			    		(this.getHeight() - pixelForCytoBand)/2,
			            -1+(int)Math.round((this.getWidth()-2*abstandLinks) * (double)(chr.Center/chr.length)),
			           1
			    		);	
			    
			    g.drawRect(1+3+ abstandLinks+ (int)Math.round((this.getWidth()-2*abstandLinks) * (double)(chr.Center/chr.length)), 
			    		(this.getHeight() - pixelForCytoBand)/2,
			    	    -2+(int)Math.round((this.getWidth()-2*abstandLinks) * ((double)((chr.length-chr.Center)/chr.length))),
			    	    1
			    		);				    
			    
			
			
		
		
		 
		


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
		
		
		gg.drawImage(image,0,0,this.getWidth(),this.getHeight(),this);
		
		
	}


	public String cutCytoBand(String s) {
		s=s.replace('"',' ');
		int p = s.indexOf('p');
		int q = s.indexOf('q');
		
		int min = Math.max(p,q);
		
		
		if (min !=-1) s = s.substring(min,s.length());
		
		p = s.indexOf('|');
		if (p!=-1) s = s.substring(p,s.length());
		
		/*if (p!=-1 && q !=-1) {
			int max = Math.max(p,q);
			int k  = 1;
			
			if (s.charAt(max-2) != '|') k++;
			
			s = s.substring(max-k,s.length());
			
		}*/
		return s;
	}
	
	
	
	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}


	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
if (arg0.getKeyCode() == 39) {
	 int width = getWidth();
     setPreferredSize(new Dimension((int)Math.round(width*1.33),140));
     setSize(new Dimension((int)Math.round(width*1.33),getHeight()));
     
     
     if ((int)Math.round(width*1.66) < 1200) {
   	  cView.setSize(new Dimension((int)Math.round(width*1.33)+40,cView.getHeight()));
	     
     }
     
     else   cView.setSize(new Dimension(1200,cView.getHeight()));
     
     
     cView.updateSelection();
    cView. setVisible(true);
			
		}
		
		
        if (arg0.getKeyCode() == 37) {
        	
        	 int width = getWidth();
		      setPreferredSize(new Dimension((int)Math.round(width*0.66),140));
		      setSize(new Dimension((int)Math.round(width*0.66),getHeight()));
		      
		      if ((int)Math.round(width*0.66) < cView.getWidth()) {
		    	  cView.setSize(new Dimension((int)Math.round(width*0.66)+40,cView.getHeight()));
			     
		      }
		      cView.updateSelection();
		      cView.setVisible(true);
		      
		}
        
		
		
		
	}


	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}