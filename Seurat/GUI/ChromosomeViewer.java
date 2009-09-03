package GUI;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import Data.*;

class ChromosomeView extends JFrame implements MatrixWindow, IPlot, MouseListener {
	

	Seurat seurat;

	ChromosomePanel panel;

	JMenuItem item;

	ChromosomeView globalView = this;

	Point point1,point2;

	DataManager dataManager;

	Vector<Chromosome> Chromosomes;
	Vector<CGHVariable> Cases;
	
	JPanel infoPanel;
	
	double MaxLength;
	double MaxFreq;
	
	
	
	
	public void applyNewPixelSize() {
	
	

		updateSelection();
		
		
	}
	
	
	
	
	
	

	public ChromosomeView(Seurat seurat, String name, Vector<Chromosome> chr,
			Vector<CGHVariable> Cases) {
		super(name + " ");
		item = new JMenuItem(name);

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.Chromosomes = chr;
		this.Cases = Cases;
		
		
	
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel p = new JPanel();
		
		p.addMouseListener(this);
		
		
		p.setBorder(BorderFactory.createEtchedBorder());
		p.setLayout(new GridLayout(1,1));
			
		/*
		for (int i = 0; i < chr.size(); i++) {
		ChromosomePanel panel = new ChromosomePanel(seurat,this, chr.elementAt(i),Cases);
        panel.setPreferredSize(new Dimension(800,30));
        panels.add(panel);
		p.add(panel,BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEtchedBorder());
		}*/
		
		ChromosomePanel panel = new ChromosomePanel(seurat,this, chr,Cases);
	      p.add(panel);
	      
	     
	     this.addKeyListener(panel); 
	      
	      this.panel = panel;
		
		infoPanel = new JPanel();
		infoPanel.addKeyListener(panel); 
	
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JLabel label = new JLabel("Zoom: ");
		infoPanel.add(label);
		JButton btn = new JButton("+");
		btn.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			      int width = globalView.panel.getWidth();
			     
			      globalView.panel.setPreferredSize(new Dimension((int)Math.round(width*1.33),globalView.panel.getHeight()));
			      globalView.panel.setSize(new Dimension((int)Math.round(width*1.33),globalView.panel.getHeight()));
			      
			      
			      
			      if ((int)Math.round(width*1.33) < 1200) {
			    	  setSize(new Dimension((int)Math.round(width*1.33)+40,getHeight()));
			      }
			      else setSize(new Dimension(1200,getHeight()));
			      
			      
			      
			      
			      
			      updateSelection();
			      setVisible(true);
			}
		}
		);
		
		infoPanel.add(btn);
		
		
		btn = new JButton("-");
		btn.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 int width = globalView.panel.getWidth();
			
				 globalView.panel.setPreferredSize(new Dimension((int)Math.round(width*0.66),globalView.panel.getHeight()));
				 globalView.panel.setSize(new Dimension((int)Math.round(width*0.66),globalView.panel.getHeight()));
				      
				    
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
		
        this.getContentPane().add(new JScrollPane(p), BorderLayout.CENTER);
        
       
		
			this.setBounds(360,0,1000,95+30*Chromosomes.size());

		// this.getContentPane().setLayout(new BorderLayout());
		
		updateSelection();
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

	
	
	
	
	public void brush() {
		// TODO Auto-generated method stub

	}

	public void removeColoring() {
		// TODO Auto-generated method stub

	}







	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		point1 = e.getPoint();
		
	}







	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}







	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}







	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		point1 = e.getPoint();
	}

     
	public void paint(Graphics g) {
		super.paint(g);
		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			
			g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
						point2.y), Math.abs(point2.x - point1.x), Math
						.abs(point2.y - point1.y));
		
		}
	}





	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		point2 = e.getPoint();
		
		
		
		
		
		
		
	}







	public void updateSelection() {
		// TODO Auto-generated method stub
	        panel.updateSelection();	
	}







	public void applyNewPixelSize(int pixelW, int PixelH) {
		// TODO Auto-generated method stub
		
	}







	public void setModel(int model) {
		// TODO Auto-generated method stub
		
	}

}






class ChromosomePanel extends JPanel implements MouseListener, IPlot, MouseMotionListener, KeyListener {
	DataManager dataManager;

	Seurat seurat;

	int abstandLinks = 23;

	int abstandOben = 0;
	
	int pixelForCytoBand = 0;
	
	int CytoWidth = 0;

	public Vector<Chromosome> chrs;
	Vector<CGHVariable> Cases;

	

	Point point1, point2;
	Color SelectionColor = Color.black;
	
	double MaxLength;
	

	ChromosomeView cView;

	
	int [][] posHist;
	
	Image image;
	
	int [][] negHist;
	int [][] negSelectionHist;
	int [][] posSelectionHist;
	
	int [][] Position;
	
	
	Vector <Double> selection;
	

	public ChromosomePanel(Seurat seurat, ChromosomeView view, Vector<Chromosome> chr,
			Vector<CGHVariable> Cases) {

		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.cView = view;
		

		this.chrs = chr;
		this.Cases = Cases;

		// this.originalOrderSpalten = orderSpalten;

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.addKeyListener(this);
		
		 calcLength();
		
	}

	
	
	
	public double getMax(int [] hist) {
		double max = -100;
		for (int i = 0; i < hist.length; i++) {
		      if (hist [i]>max) max = hist [i];	
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
	
	
	
	
	
	
/*
	public void updateSelection() {
    
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2;
		int len = chr.Clones.size();
	
		
		
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
			Position [i] = (int)Math.round(clone.NucleoPosition *(this.getWidth()-2*abstandLinks)/cView.MaxLength); 
			  	
		    	for (int j = 0; j < Cases.size(); j++) {
		    		
		    		
		     		
		    		CGHVariable cghVar = Cases.elementAt(j);
		    		if (cghVar.getValue(clone.getID())>0)  {
		    			posDHist [i] = posDHist [i] + cghVar.getValue(clone.getID());
		    		    if (clone.isSelected() && cghVar.isSelected()) 	posSelectionDHist [i] = posSelectionDHist [i] + cghVar.getValue(clone.getID());
		    		
		    		}
		    		if (cghVar.getValue(clone.getID())<0)  {
		    			negDHist [i] = negDHist [i] - cghVar.getValue(clone.getID());
		    		    if (clone.isSelected() && cghVar.isSelected()) 	negSelectionDHist [i] = negSelectionDHist [i] - cghVar.getValue(clone.getID());
				    	
		    		}
				    
		    	}
		    	
		    i++;
		    
		}
		
		
		double max = getMax(posDHist);
		double min = getMax(negDHist); 
		
		posHist = new int [len];
		negHist = new int [len];
		posSelectionHist = new int [len];
		negSelectionHist = new int [len];

		
		
		for (i = 0; i < posDHist.length; i++) {
		      posHist [i] = (int) Math.round(posDHist [i] * width / max);
		      negHist [i] = (int) Math.round(negDHist [i] * width / min);
		      posSelectionHist [i] = (int) Math.round(posSelectionDHist [i] * width / max);
		      negSelectionHist [i] = (int) Math.round(negSelectionDHist [i] * width / min);
		}
		 

		
		
			
	
	    
	    /*
	    selection = new Vector();
	    
	    for (i = 0; i < chr.CytoBands.size(); i++) {
	    	Vector<Clone> clones = chr.CytoBands.elementAt(i).Clones;
	        double count = 0;
	    	for (int j = 0; j < clones.size(); j++) {
	    		if (clones.elementAt(j).isSelected()) count++;
	    	}
	    	selection.add(new Double(count/clones.size()));
	    }
				
		
		*/
		/*
        this.setSize(this.getWidth(), this.getHeight());
		this.repaint();
	}*/

	
	
	public double getMax(double [] hist) {
		double max = -100;
		for (int i = 0; i < hist.length; i++) {
		      if (hist [i]>max) max = hist [i];	
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
		
		
		
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2/chrs.size();
				
		int len = (this.getWidth()-2*abstandLinks);
		

		//public boolean containsRectInRect(int x1, int y1, int x2, int y2, int Sx1,
			//	int Sy1, int Sx2, int Sy2) {
		
		for (int ii = 0; ii < chrs.size(); ii++) {
		
		Chromosome chr = chrs.elementAt(ii);	
		int shift = (this.getHeight()-2*abstandOben)*ii/chrs.size();
		
	
		
		
		
			
		for (int k = 0; k < chr.Clones.size(); k++) {
		    	 Clone clone = chr.Clones.elementAt(k);
		    	 
		    	 
		    //	 int startC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				// int endC = (int)Math.round(clone.NucleoPosition*len/chr.length);
				    
				
				
				    
				 if (containsRectInRect(abstandLinks + Position[ii][k], shift+abstandOben + width - posHist[ii] [k], abstandLinks + Position[ii][k]+1, shift+ abstandOben + width, 
						 xx1, yy1, xx2, yy2)) {
					 clone.select(true);
					 selectPositiveCGHs(clone);
				 }
				 
				 if (containsRectInRect(abstandLinks + Position[ii][k], shift+abstandOben + width, abstandLinks + Position[ii][k]+1,  shift+abstandOben + width + negHist[ii] [k], 
						 xx1, yy1, xx2, yy2)) {
					 clone.select(true);
					 selectNegativeCGHs(clone);
				 }
		     }	
		
		
		
		if (yy2 > this.getHeight() - pixelForCytoBand ) {
			
			
			
			
			
			
			
			
			
			
			for (int i = start; i < end; i++) {
			     for (int k = 0; k < chr.CytoBands.size(); k++) {
			    		 
			    	 
			    	 int startC = (int)Math.round(chr.CytoBands.elementAt(k).Start*len/cView.MaxLength);
					 int endC = (int)Math.round(chr.CytoBands.elementAt(k).End*len/cView.MaxLength);
					    
					    
			    	 if (!chr.CytoBands.elementAt(k).name.equals("NA") &&  chr.CytoBands.elementAt(k).End!= seurat.dataManager.NA &&
			    			 (end >= startC  && end < endC) || (start >= startC  && start < endC) || (startC >= start  && startC < end) || (endC >= start  && endC < end)) {
			    
			    			chr.CytoBands.elementAt(k).select(true,Cases);
			    		
			    	 }
			     }	
			}
			
			
			
		}
		}
		
		

		this.repaint();

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
	    	
	    	int x = e.getY()*chrs.size()/this.getHeight();
	    	
	    	Vector v = new Vector();
	    	v.add(chrs.elementAt(x));
	    	new ChrView(seurat, "Chromosome " + chrs.elementAt(x).name, v,
	    			Cases);
	    	//dataManager.clearSelection();
	        //seurat.repaintWindows();
	    }
		
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

		
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

		if (e.isControlDown()) {
		


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
		    
		    
		 

		g.setColor(Color.BLACK);
		
		
		for (int k = 0 ;  k<chrs.size(); k++) {
		Chromosome chr = chrs.elementAt(k);
		
		int shift = (this.getHeight()-2*abstandOben)*k/chrs.size();
		
		
		int width = (this.getHeight()-2*abstandOben-pixelForCytoBand)/2/chrs.size();
		int len = (this.getWidth()-2*abstandLinks);
	
		
	    if (posHist == null) updateSelection();
	    
		for (int j = 0; j < posHist [k].length; j++) {
				g.setColor(new Color(255,0,0, 105+posHist [k][j]*150/2/width));
			//	g.setColor(Color.BLUE);
				g.fillRect(abstandLinks + Position[k][j],
						   shift+abstandOben + width - posHist [k][j],
						   1,
				           posHist[k] [j]
				           );
				
				g.setColor(Color.BLUE);
				
				g.fillRect(abstandLinks + Position[k][j],
						shift+abstandOben + width - posSelectionHist [k][j],
						   1,
				           posSelectionHist[k] [j]
				           );
				
		}
			
		
		for (int j = 0; j < negHist[k].length; j++) {
			g.setColor(new Color(0,255,0,105+ negHist [k][j]*150/2/width));
		//	g.setColor(Color.RED);
			g.fillRect(abstandLinks + Position[k][j],
					shift+abstandOben + width,
					   1,
			           negHist [k][j]
			           );
			
			g.setColor(Color.BLUE);
			g.fillRect(abstandLinks + Position[k][j],
					shift+abstandOben + width,
					   1,
			           negSelectionHist[k] [j]
			           );
			
			
	    }
			
				
		g.setColor(Color.BLACK);
		g.setFont(new Font("sansserif", 0, 12));
			
		g.drawString(""+chr.getName().replace("\"",""),2,shift+abstandOben + width+6);
		
	
		
		
		
		
         // Paint Chromosome....			
			
				g.setColor(Color.BLACK);
				
			    g.drawRect(-2+ abstandLinks, 
			    		shift+abstandOben + width,
			            -1+(int)Math.round((this.getWidth()-2*abstandLinks) * (double)(chr.Center/MaxLength)),
			           1
			    		);	
			    
			    g.drawRect(1+3+ abstandLinks+ (int)Math.round((this.getWidth()-2*abstandLinks) * (double)(chr.Center/MaxLength)), 
			    		shift+abstandOben + width,
			    	    -2+(int)Math.round((this.getWidth()-2*abstandLinks) * ((double)((chr.length-chr.Center)/MaxLength))),
			    	    1
			    		);				    
			    
			
			
		
		
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




	public void calcLength() {
		
		for (int i = 0; i < chrs.size(); i++) {
			chrs.elementAt(i).calculateLength();
			double len = chrs.elementAt(i).length;
			if (len >MaxLength) MaxLength = len;
		}
		
	}
	
	
	public void updateSelection() {
		// TODO Auto-generated method stub
		
		
		double [] [] posDHist = new double [chrs.size()] [];
		double [] [] negDHist = new double [chrs.size()][];

		double [] [] posSelectionDHist = new double [chrs.size()][];
		double [] [] negSelectionDHist = new double [chrs.size()] [];
		
		
		posHist = new int [chrs.size()] [];
		negHist = new int [chrs.size()][];
		posSelectionHist = new int [chrs.size()][];
		negSelectionHist= new int [chrs.size()][];
		
		
		
		Position = new int [chrs.size()][];
		
		
		for (int ii = 0; ii < chrs.size(); ii++) {
			
			Chromosome chr = chrs.elementAt(ii);
			
			
			
			
			
			int width = (getHeight()-2*abstandOben)/2/chrs.size();
			int len = chr.Clones.size();
		
			posDHist [ii]= new double [len];
			negDHist [ii]= new double [len];

			posSelectionDHist [ii]= new double [len];
			negSelectionDHist [ii]= new double [len];
			
			
			
		//	System.out.println("-->>"+chr.length);
			
			
			
			
			Position [ii] = new int [len];
			
			
		    java.util.Iterator<Clone> iter = chr.Clones.iterator();
		    int i = 0;
			while (iter.hasNext()) {
				Clone clone = iter.next();
			    Position [ii] [i] = (int)Math.round(clone.NucleoPosition *(getWidth()-2*abstandLinks)/MaxLength); 
				  	
			    	for (int j = 0; j < Cases.size(); j++) {
			    		
			    		
			     		
			    		CGHVariable cghVar = Cases.elementAt(j);
			    		if (cghVar.getValue(clone.getID())>0)  {
			    			posDHist [ii][i] = posDHist [ii] [i] + cghVar.getValue(clone.getID());
			    		    if (clone.isSelected() && cghVar.isSelected()) 	posSelectionDHist[ii] [i] = posSelectionDHist [ii][i] + cghVar.getValue(clone.getID());
			    		
			    		}
			    		if (cghVar.getValue(clone.getID())<0)  {
			    			negDHist [ii][i] = negDHist [ii][i] - cghVar.getValue(clone.getID());
			    		    if (clone.isSelected() && cghVar.isSelected()) 	negSelectionDHist[ii] [i] = negSelectionDHist[ii] [i] - cghVar.getValue(clone.getID());
					    	
			    		}
					    
			    	}
			    	
			    i++;
			    
			}
			
		}
		
		
		
		
for (int ii = 0; ii < chrs.size(); ii++) {
			
			Chromosome chr = chrs.elementAt(ii);
			
			int width = (getHeight()-2*abstandOben)/2/chrs.size();
			int len = chr.Clones.size();
		
		
			double max = getMax(posDHist,negDHist);
			
			posHist [ii]= new int [len];
			negHist [ii]= new int [len];
			posSelectionHist[ii] = new int [len];
			negSelectionHist[ii] = new int [len];

			
			
			for (int i = 0; i < posHist[ii].length; i++) {
				posHist[ii] [i] = (int) Math.round(posDHist [ii] [i] * width / max);
				negHist[ii] [i] = (int) Math.round(negDHist [ii][i] * width / max);
			    posSelectionHist[ii] [i] = (int) Math.round(posSelectionDHist[ii] [i] * width / max);
				negSelectionHist [ii][i] = (int) Math.round(negSelectionDHist[ii] [i] * width / max);
			}
			 

			
			setSize(getWidth(), getHeight());
			repaint();
			
			
			
			
			
		}
		
		
	}

	
	
	public double getMax(double [][] h1,double [][] h2) {
		double res = 0;
		for (int i = 0; i < h1.length; i++) {
			for (int j = 0; j < h1 [i].length; j++) {
				if (res < h1 [i][j]) res = h1 [i][j];
			}
		}
		
		for (int i = 0; i < h2.length; i++) {
			for (int j = 0; j < h2 [i].length; j++) {
				if (res < h2 [i][j]) res = h2 [i][j];
			}
		}
		return res;
	}




	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		
		
		
		
		
if (arg0.getKeyCode() == 38) {
			
	int width = getWidth();
    
   setPreferredSize(new Dimension((int)Math.round(width*1.33),getHeight()));
    setSize(new Dimension((int)Math.round(width*1.33),getHeight()));
    
    
    
    if ((int)Math.round(width*1.33) < 1200) {
  	  cView.setSize(new Dimension((int)Math.round(width*1.33)+40,cView.getHeight()));
    }
    else cView.setSize(new Dimension(1200,getHeight()));
    
    
    
    
    
    cView.updateSelection();
    cView.setVisible(true);
			
			
			
		}
		
		
        if (arg0.getKeyCode() == 40) {
        	int width = getWidth();
			
			 setPreferredSize(new Dimension((int)Math.round(width*0.66),getHeight()));
			 setSize(new Dimension((int)Math.round(width*0.66),getHeight()));
			      
			    
		      if ((int)Math.round(width*0.66) < getWidth()) {
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