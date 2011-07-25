package GUI;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import Data.Bicluster;
import Data.Biclustering;
import Data.Clone;
import Data.Gene;
import Data.ISelectable;
import Data.MyColor;
import Settings.Settings;
import Tools.Tools;


public class BiHeatmap extends JFrame{
	
	String name;
	BiPanel panel;
	Biclustering biclust;
	int maxWidth = 450,maxHeight = 450;
	Seurat seurat;
	
	public BiHeatmap(Seurat seurat,String name,Biclustering biclust) {
		super("Biheatmap: " + name);
		
		this.seurat = seurat;
	    this.biclust = biclust;
		panel = new BiPanel(this,biclust);
		
	    setPlotSize();
	    this.getContentPane().add(new JScrollPane(panel));

	    this.addKeyListener(panel);
		this.addMouseListener(panel);

	    setLocation(760,0);
		this.setVisible(true);
		
		
	}
	
	
	

	public void setPlotSize() {
		int width = panel.columns.size();
		int height = panel.rows.size();
		
	
		int pixelW = maxWidth /width;
	    int pixelH = maxHeight/height;
	    
	    if (pixelW == 0) pixelW = 1;
	    if (pixelH == 0) pixelH = 1;

	    
	    panel.pixelW = pixelW;
	    panel.pixelH = pixelH;
	    
		
		updatePlot();
	}

	
	public void updatePlot() {
		int width = panel.columns.size();
		int height = panel.rows.size();
		
		
		
		width = panel.abstandLinks + panel.pixelW*width ;
		height = panel.abstandOben +panel.pixelH*height;
		 panel.setPreferredSize(new Dimension(width,height));
		 setSize(width+6,height+35);
			
			
		setVisible(true);
	}
	
	
	
}
	
	
class BiPanel extends JPanel implements MouseListener, KeyListener{
	
	
	public Biclustering biclust;
	public double Max, Min;
    int abstandOben = 5;
    int abstandLinks = 2;
	int biwidth = 2;
	
	int pixelW = 10;
	int pixelH = 6;
	
	Vector<Bielement> columns;
	Vector<Bielement> rows;
	
	Vector<Bielement> origColumns;
	Vector<Bielement> origRows;
	
	boolean showColors = true;
	
	
	// Number of Biclsuter
	

	BiHeatmap biheatmap;
	
	
	public BiPanel(BiHeatmap biheatmap,Biclustering biclust) {

		this.biheatmap = biheatmap;
		this.biclust = biclust;
		columns = unionC(biclust);
		rows =  unionR(biclust);
		
		origColumns = unionC(biclust);
		origRows =  unionR(biclust);
		
		
		calculateMinMax();
		
		System.out.println("Anfangskriterium:  " + calculateCriterium(columns,rows));

		
		this.setBounds(750,550,columns.size()*pixelW + 6,rows.size()*pixelH + 35);
		this.setVisible(true);
		
		this.addMouseListener(this);
		this.addKeyListener(this);
	}
	
	
	
	
	
	
	
	public void paint(Graphics g) {
		 g.setColor(Color.WHITE);
		 g.fillRect(0,0,this.getWidth(), this.getHeight());
			
		

		 int Links = abstandLinks;

		
		 
			 
		 for (int i = 0; i < columns.size(); i++) {
			 
			
		 
			 if (i > 0 ) {
					Bielement pcol = columns.elementAt(i-1);
				
					Bielement currcol = columns.elementAt(i);
				
					
					//abstandLinks += calcDist(pcol,currcol)*pixelW;					
				}
			 
			 int Oben = abstandOben;
			 
			 
	     for (int j = 0; j < rows.size(); j++) {
	    	 
	    	 
	    	 
	    	 if (j > 0) {
					Bielement prow =rows.elementAt(j-1);
					Bielement currrow =rows.elementAt(j);
					
				//	abstandOben  += calcDist(prow,currrow)*pixelH;

					
				}
	    	 
	    	 
	    	 
	    	 
	    	 
	    	
	         ISelectable col = columns.elementAt(i).isel;
	         ISelectable row = rows.elementAt(j).isel;
	         
	         
	         
	         

	    	 Color c = Color.WHITE;
			 double value = col.getValue(row.getID());
	    	
             if (value > 0) {
           			double koeff = value/Max;			
					c = Tools.convertHLCtoRGB(new MyColor(Settings.Lmax -  Tools.fPos(koeff)*(Settings.Lmax-Settings.Lmin),Tools.fPos(koeff)*Settings.Cmax, 360 )).getRGBColor();	    	 
	    	 }
			 else {
						
					double koeff = value/Min;
					if (Min == 0) koeff = 0;
					c = Tools.convertHLCtoRGB(new MyColor(
							              Settings.Lmax- Tools.fNeg(koeff)*(Settings.Lmax-Settings.Lmin), 
							              Tools.fNeg(koeff)*Settings.Cmax, 
							              120
							)).getRGBColor();
					
					
			}
					
					
					
					if (!showColors) c = Color.WHITE;
					g.setColor(c);
					
					
					
					
					
					g.fillRect(
							Links + i*pixelW , 
							Oben+j*pixelH , 
							pixelW , 
							pixelH 
					
					
					
					);
					
					
					
					g.setColor(Color.BLACK);
		    	    
    	    	
    	    	
					
					
					
					}
	         }
	    
	    
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
         Links = abstandLinks;
		 
		 
		 
		 for (int i = 0; i < columns.size(); i++) {
			 int Oben = abstandOben;
			 
			 
			 if (i > 0 ) {
					Bielement pcol = columns.elementAt(i-1);
				
					Bielement currcol = columns.elementAt(i);
				
					
				//	abstandLinks += calcDist(pcol,currcol)*pixelW;					
				}

			 
		     for (int j = 0; j < rows.size(); j++) {

		    	 

		    	 if (j > 0) {
						Bielement prow =rows.elementAt(j-1);
						Bielement currrow =rows.elementAt(j);
						
						//abstandOben  += calcDist(prow,currrow)*pixelH;

						
					}
		    	 
		    	 
		    	     ISelectable col = columns.elementAt(i).isel;
			         ISelectable row = rows.elementAt(j).isel;
			         
			         
			         Vector<Integer> biclu = findBiclusters(columns.elementAt(i),rows.elementAt(j));
			        					 
			         for (int t = 0; t < biclu.size(); t++) {
		    	     
			        	 int bi = biclu.elementAt(t);
			        	// System.out.println(bi + "    "  + biclust.biclusters.size());
			        	 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*bi/biclust.biclusters.size()),(float)0.95,(float)0.95));
		    	      //   g.setColor(Color.BLUE);
		    	 
			        if (i+1 < columns.size()) {
			        	
			        	 ISelectable c = columns.elementAt(i+1).isel;
				         ISelectable r = rows.elementAt(j).isel; 
				         
				         Vector<Integer> biclu2 = findBiclusters(columns.elementAt(i+1),rows.elementAt(j));

				         if (!contain(bi,biclu2))				         
				         g.fillRect(
									Links + (i+1)*pixelW-biwidth, 
									Oben+(j)*pixelH, 
									biwidth, 
									pixelH 
							
							
							
							);
			        	
			        }else {
			        	 g.fillRect(
			        			 Links + (i+1)*pixelW-biwidth, 
									Oben+(j)*pixelH, 
									biwidth, 
									pixelH 
							
							
							
							);
			        }	 
			        
			        
			        if (j+1 < rows.size()) {
			        	
			        	 ISelectable c = columns.elementAt(i).isel;
				         ISelectable r = rows.elementAt(j+1).isel; 
				         
				         Vector<Integer> biclu2 = findBiclusters(columns.elementAt(i),rows.elementAt(j+1));

				         if (!contain(bi,biclu2))				         
				         g.fillRect(
									Links + (i)*pixelW, 
									Oben+(j+1)*pixelH-biwidth, 
									pixelW, 
									biwidth 
							
							
							
							);
			        	
			        }	else {
			        	   g.fillRect(
									Links + (i)*pixelW, 
									Oben+(j+1)*pixelH-biwidth, 
									pixelW, 
									biwidth 
							);
			        } 
			        	
			        
			        
			        
			        if (j >0) {
			        	
			        	 ISelectable c = columns.elementAt(i).isel;
				         ISelectable r = rows.elementAt(j-1).isel; 
				         
				         Vector<Integer> biclu2 = findBiclusters(columns.elementAt(i),rows.elementAt(j-1));

				         if (!contain(bi,biclu2))				         
				         g.fillRect(
									Links + (i)*pixelW, 
									Oben+(j)*pixelH, 
									pixelW, 
									biwidth 
							
							
							
							);
			        	
			        }	 else {
			        	g.fillRect(
								Links + (i)*pixelW, 
								Oben+(j)*pixelH, 
								pixelW, 
								biwidth 
						
						
						
						);
			        }
			        	
			        
			        
			        
			        if (i >0) {
			        	
			        	 ISelectable c = columns.elementAt(i-1).isel;
				         ISelectable r = rows.elementAt(j).isel; 
				         
				         Vector<Integer> biclu2 = findBiclusters(columns.elementAt(i-1),rows.elementAt(j));

				         if (!contain(bi,biclu2))				         
				         g.fillRect(
									Links + (i)*pixelW, 
									Oben+(j)*pixelH, 
									biwidth, 
									pixelH 
							
							
							
							);
			        	
			        }	else {
			        	g.fillRect(
								Links + (i)*pixelW, 
								Oben+(j)*pixelH, 
								biwidth, 
								pixelH 
						
						
						
						);
			        } 
			        
			        
			        
			        
			        
			        
			        	 
					
					
					
					
					
					
			         }
					
		    
		 }
		 }
		 
		 
		 
		 
		 
	}
	
	
	
	
	public void presort() {
		columns = new Vector();
		rows = new Vector();
		
		for(int i = 0; i < biclust.biclusters.size(); i++) {
			Bicluster cl = biclust.biclusters.elementAt(i);
			addBicluster(cl,i);
		}
		
		repaint();
	}
	
	
	public void addBicluster(Bicluster cl, int bi) {
		
		Vector<ISelectable> newcols = cl.columns; 
		Vector<ISelectable> newrows = cl.rows; 

		for(int i = 0; i < newcols.size(); i++) {
			ISelectable c = newcols.elementAt(i);
			Bielement biel = null;
			
			if ((biel = contains(c,columns)) == null) {
			     	biel = new Bielement(c);
			     	biel.bi.add(bi);
			     	columns.add(biel);
			}
			else {
				biel.bi.add(bi);
			}
			
			sort();
			
			
		}
		
		
		
		for(int i = 0; i < newrows.size(); i++) {
			ISelectable c = newrows.elementAt(i);
			Bielement biel = null;
			
			if ((biel = contains(c,rows)) == null) {
			     	biel = new Bielement(c);
			     	biel.bi.add(bi);
			     	rows.add(biel);
			}
			else {
				biel.bi.add(bi);
			}
			
			sort();
			
			
		}
		
		
		System.out.println("Kriterium nach Presort:  " + calculateCriterium(columns,rows));
		repaint();
		
		
		
	}
	
	
	public Bielement contains(ISelectable el, Vector<Bielement> v) {
		for (int i = 0; i < v.size(); i++) {
			if (v.elementAt(i).isel.getID() == el.getID()) return v.elementAt(i);
		}
		
		return null;
	}
	
	
	
	
	
	public Vector<Integer> findBiclusters(Bielement col, Bielement row) {
		Vector<Integer> clusters = new Vector();
		
		
		for(int i = 0; i < col.bi.size(); i++) {
		       for(int j = 0; j < row.bi.size(); j++) {
		   	         if (col.bi.elementAt(i) == row.bi.elementAt(j)) {
		   	        	 clusters.add(row.bi.elementAt(j)); 	
		   	        	 break;
		   	         }
		       }
		       }
		
		
		
		return clusters;
	}
	
	
	public int calcDist(Bielement e1, Bielement e2) {
		int dist = 0;
		
		for (int i = 0; i < e1.bi.size(); i++) {
			boolean contains = false;
			for (int j = 0; j < e2.bi.size(); j++) {
				
				if (e1.bi.elementAt(i) == e2.bi.elementAt(j)) contains = true;
				
			}
			
			if (!contains) dist++;
		}
		
		
		
		for (int i = 0; i < e2.bi.size(); i++) {
			boolean contains = false;
			for (int j = 0; j < e1.bi.size(); j++) {
				
				if (e2.bi.elementAt(i) == e1.bi.elementAt(j)) contains = true;
				
			}
			
			if (!contains) dist++;
		}
		
		
		
		
		return dist;
	}
	
	
	
	
	public void mouseClicked(MouseEvent e) {

	

		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			JPopupMenu menu = new JPopupMenu();

			JMenuItem item = new JMenuItem("Sort");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					sort();
				}
			});
			menu.add(item);
			
			
			/*
			item = new JMenuItem("Presort");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					presort();
				}
			});
			menu.add(item);
			*/
			
			item = new JMenuItem("Original order");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();

					columns = origColumns;
					rows = origRows;
					repaint();
					
				}
			});
			menu.add(item);
			
			
			
			
			JCheckBoxMenuItem box = new JCheckBoxMenuItem(
			"show colors");
	if (showColors) box.setSelected(true);
	else box.setSelected(false);
	menu.add(box);

	box.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JCheckBoxMenuItem box = (JCheckBoxMenuItem) e.getSource();
			if (box.isSelected()) {
				showColors  = true;
			} else
				showColors  = false;
			repaint();

		}

	});
	
			

			menu.show(this, e.getX(), e.getY());
		}

	}
	
	
	
	public void sort() {
	
		sortColumns();
		sortColumns2();
		sortColumns3();


		sortRows();
		sortRows2();
		sortRows3();

		
	    repaint();
	    
		System.out.println("Kriterium nach Sort:  " + calculateCriterium(columns,rows));

	    
	}
	
	
	public void sortColumns() {
		
		
		
		
		Vector<Bielement> copyCol = copy(columns);
		
		
		
		int oldMax = calculateCriterium(copyCol,rows);;
		int Max = oldMax;
		System.out.println("Startcrit " + oldMax);
		
		do {
		
	    oldMax = calculateCriterium(copyCol,rows);

    	Max = oldMax;
        System.out.println("Max " + Max);
    	
    	int toJ = -1;
        int fromI = -1;
		
		
		for (int i = 0; i < copyCol.size(); i++) {
		   
             Bielement el = copyCol.elementAt(i);

             
		     for (int j = 0; j < copyCol.size(); j++) {

    		    	 Vector<Bielement> temp = copy(copyCol);	
		            temp.removeElementAt(i);
		            temp.insertElementAt(el,j);
		            
		            int newCrit = calculateCriterium(temp,rows);
		            
		            if (newCrit<Max) {
		            	Max = newCrit;
		            	toJ = j;
		            	fromI = i;
		            }
		     }	     
		     
		}
		
		
		
		System.out.println(toJ + "  " + fromI + "  " +Max);
		
		
		
		
		if (fromI != -1) {
			  Bielement temp = copyCol.elementAt(fromI);	
	          copyCol.removeElementAt(fromI);
	          copyCol.insertElementAt(temp,toJ);
	          
		}
		
		System.out.println(oldMax + "  "  +calculateCriterium(copyCol,rows));
		
		}
		while(oldMax != calculateCriterium(copyCol,rows)) ;
		
		this.columns = copyCol;
		
		
		
	}
	
	
	
	
	
	
	
public void sortColumns2() {
		
		
		
		
		Vector<Bielement> copyCol = copy(columns);
		
		
		
		int oldMax = calculateCriterium2(copyCol,rows);;
		int Max = oldMax;
		System.out.println("Startcrit " + oldMax);
		
		do {
		
	    oldMax = calculateCriterium2(copyCol,rows);

    	Max = oldMax;
        System.out.println("Max " + Max);
    	
    	int toJ = -1;
        int fromI = -1;
		
		
		for (int i = 0; i < copyCol.size(); i++) {
		   
             Bielement el = copyCol.elementAt(i);

             
		     for (int j = 0; j < copyCol.size(); j++) {

    		    	 Vector<Bielement> temp = copy(copyCol);	
		            temp.removeElementAt(i);
		            temp.insertElementAt(el,j);
		            
		            int newCrit = calculateCriterium2(temp,rows);
		            
		            if (newCrit<Max) {
		            	Max = newCrit;
		            	toJ = j;
		            	fromI = i;
		            }
		     }	     
		     
		}
		
		
		
		System.out.println(toJ + "  " + fromI + "  " +Max);
		
		
		
		
		if (fromI != -1) {
			  Bielement temp = copyCol.elementAt(fromI);	
	          copyCol.removeElementAt(fromI);
	          copyCol.insertElementAt(temp,toJ);
	          
		}
		
		System.out.println(oldMax + "  "  +calculateCriterium2(copyCol,rows));
		
		}
		while(oldMax != calculateCriterium2(copyCol,rows)) ;
		
		this.columns = copyCol;
		
		
		
	}
	
	
	
	
	
	
	



	
	
	
	
public void sortRows() {
		
		
		
		
		Vector<Bielement> copyRow = copy(rows);
		
		
		
		int oldMax = calculateCriterium(columns,copyRow);;
		int Max = oldMax;
		System.out.println("Startcrit " + oldMax);
		
		do {
		
	    oldMax = calculateCriterium(columns,copyRow);

    	Max = oldMax;
        System.out.println("Max " + Max);
    	
    	int toJ = -1;
        int fromI = -1;
		
		
		for (int i = 0; i < copyRow.size(); i++) {
		   
             Bielement el = copyRow.elementAt(i);

             
		     for (int j = 0; j < copyRow.size(); j++) {

    		    	 Vector<Bielement> temp = copy(copyRow);	
		            temp.removeElementAt(i);
		            temp.insertElementAt(el,j);
		            
		            int newCrit = calculateCriterium(columns,temp);
		            
		            if (newCrit<Max) {
		            	Max = newCrit;
		            	toJ = j;
		            	fromI = i;
		            }
		     }	     
		     
		}
		
		
		
		System.out.println(toJ + "  " + fromI + "  " +Max);
		
		
		
		
		if (fromI != -1) {
			  Bielement temp = copyRow.elementAt(fromI);	
	          copyRow.removeElementAt(fromI);
	          copyRow.insertElementAt(temp,toJ);
	          
		}
		
		System.out.println(oldMax + "  "  +calculateCriterium(columns,copyRow));
		
		}
		while(oldMax != calculateCriterium(columns,copyRow)) ;
		
		this.rows = copyRow;
		
		
		
	}



	






public void sortRows2() {
		
		
		
		
		Vector<Bielement> copyRow = copy(rows);
		
		
		
		int oldMax = calculateCriterium2(columns,copyRow);;
		int Max = oldMax;
		System.out.println("Startcrit " + oldMax);
		
		do {
		
	    oldMax = calculateCriterium2(columns,copyRow);

    	Max = oldMax;
        System.out.println("Max " + Max);
    	
    	int toJ = -1;
        int fromI = -1;
		
		
		for (int i = 0; i < copyRow.size(); i++) {
		   
             Bielement el = copyRow.elementAt(i);

             
		     for (int j = 0; j < copyRow.size(); j++) {

    		    	 Vector<Bielement> temp = copy(copyRow);	
		            temp.removeElementAt(i);
		            temp.insertElementAt(el,j);
		            
		            int newCrit = calculateCriterium2(columns,temp);
		            
		            if (newCrit<Max) {
		            	Max = newCrit;
		            	toJ = j;
		            	fromI = i;
		            }
		     }	     
		     
		}
		
		
		
		System.out.println(toJ + "  " + fromI + "  " +Max);
		
		
		
		
		if (fromI != -1) {
			  Bielement temp = copyRow.elementAt(fromI);	
	          copyRow.removeElementAt(fromI);
	          copyRow.insertElementAt(temp,toJ);
	          
		}
		
		System.out.println(oldMax + "  "  +calculateCriterium2(columns,copyRow));
		
		}
		while(oldMax != calculateCriterium2(columns,copyRow)) ;
		
		this.rows = copyRow;
		
		
		
	}









	
	
	
	
	
	
	public void sortColumns3() {
		
		
		
		
		Vector<Bielement> copyCol = copy(columns);
		
		
		
		int oldMax = calculateCriterium2(copyCol,rows);;
		int Max = oldMax;
		
		do {
		
	    oldMax = calculateCriterium(copyCol,rows);

        
        Max = oldMax;
		int from = -1;
		int too = -1;
		int dest = -1;
        
		
		for (int bi = 0; bi < biclust.biclusters.size(); bi++) {
			
			Vector<Block> blocks = identifyBlocks(copyCol,bi);
			
			for (int b = 0; b < blocks.size(); b++){
				for (int to = 0; to < blocks.size(); to++) {
					Block block = blocks.elementAt(b);
					
					if (blocks.elementAt(to).from != block.from) {
					Vector<Bielement> rep = move(copyCol,block.from,block.to,blocks.elementAt(to).from);
                    int crit = calculateCriterium(rep,rows);
                    if (crit<Max) {
                    	Max = crit;
                    	from = block.from;
                    	too = block.to;
                    	dest = blocks.elementAt(to).from;
                    }
					}
                    
					if (blocks.elementAt(to).to != block.to) {

                    Vector rep = move(copyCol,block.from,block.to,blocks.elementAt(to).to+1);
                    int crit = calculateCriterium(rep,rows);
                    if (crit<Max) {
                    	Max = crit;
                    	from = block.from;
                    	too = block.to;
                    	dest = blocks.elementAt(to).to+1;
                    }
					
					}
				}
			}
			
			
			
			
		}
		
		


		
		
		if (from != -1) {
			  copyCol = move(copyCol,from,too,dest);
		} 
		
		this.columns = copyCol;
		
		System.out.println(oldMax + "  "  +calculateCriterium(copyCol,rows));
		
	}
	while(oldMax != calculateCriterium(copyCol,rows)) ;
	
	
	
		
	}
	
	
	

	
	
	
public void sortRows3() {
		
		
		
		
		Vector<Bielement> copyRow = copy(rows);
		
		
		
		int oldMax = calculateCriterium2(columns,copyRow);;
		int Max = oldMax;
		
		do {
		
	    oldMax = calculateCriterium(columns,copyRow);

    	Max = oldMax;
        System.out.println("Max " + Max);

        
        Max = oldMax;
		int from = -1;
		int too = -1;
		int dest = -1;
        
		
		for (int bi = 0; bi < biclust.biclusters.size(); bi++) {
			
			Vector<Block> blocks = identifyBlocks(copyRow,bi);
			
			for (int b = 0; b < blocks.size(); b++){
				for (int to = 0; to < blocks.size(); to++) {
					Block block = blocks.elementAt(b);
					
					Vector<Bielement> rep = move(copyRow,block.from,block.to,blocks.elementAt(to).from);
                    int crit = calculateCriterium(columns,rep);
                    if (crit<Max) {
                    	Max = crit;
                    	from = block.from;
                    	too = block.to;
                    	dest = blocks.elementAt(to).from;
                    }
					
                    
                    
                    rep = move(copyRow,block.from,block.to,blocks.elementAt(to).to+1);
                    crit = calculateCriterium(columns,rep);
                    if (crit<Max) {
                    	Max = crit;
                    	from = block.from;
                    	too = block.to;
                    	dest = blocks.elementAt(to).to+1;
                    }
					
					
				}
			}
			
			
			
			
		}
		
		


		
		
		if (from != -1) {
			  copyRow = move(copyRow,from,too,dest);
		} 
		
		this.rows = copyRow;
		
		System.out.println(oldMax + "  "  +calculateCriterium(columns,copyRow));
		
	}
	while(oldMax != calculateCriterium(columns,copyRow)) ;
	
	
	
		
	}
	
	




	
	
	public Vector<Bielement> move(Vector<Bielement> list,int from, int to, int dest) {
		Vector<Bielement> newlist = new Vector();
		
		for (int i = 0; i < from; i++) {
			newlist.add(list.elementAt(i));
		}  
		
		for (int i = to+1; i < dest; i++) {
			newlist.add(list.elementAt(i));
		}  
		
		
		for (int i = from; i <= to; i++) {
			newlist.add(list.elementAt(i));
		}
		
		for (int i = dest; i < list.size(); i++) {
			newlist.add(list.elementAt(i));
		}
		
		return newlist;
	}
	
	
	
	
	
	public Vector<Bielement> copy(Vector<Bielement> v) {
		Vector<Bielement> v2 = new Vector();
		for (int i= 0 ; i< v.size(); i++) {
			v2.add(v.elementAt(i));
			
		}
		return v2;
	}
	
	
	
	
	
	public int calculateCriterium2(Vector<Bielement> cols, Vector<Bielement> rows) {
		int crit=0;
		
		
		for (int bi  = 0; bi<biclust.biclusters.size(); bi++) {
			
		    int pos=-1;
		    for (int i = 0; i < cols.size(); i++) {
		          	Bielement c = cols.elementAt(i);
		    	
		    	    if (contain(bi,c.bi)) {
		          		if (pos == -1) pos = i;
		          		else {
		          			crit += i-pos;
		          			pos = i;
		          		}
		          	}
		    }
			
		}
	return crit;	
	}
	
	
	public int calculateCriterium(Vector<Bielement> cols, Vector<Bielement> rows) {
		int gescrit=0;
		
		
		for (int bi  = 0; bi<biclust.biclusters.size(); bi++) {
			
			int colcrit = 0; 
		    int pos=-1;
		    for (int i = 0; i < cols.size(); i++) {
		          	Bielement c = cols.elementAt(i);
		    	
		    	    if (contain(bi,c.bi)) {
		          		if (pos == -1) pos = i;
		          		else {
		          			if (i-pos > 1) colcrit += 1;
		          			pos = i;
		          		}
		          	}
		    }
			
		    gescrit+=colcrit*biclust.biclusters.elementAt(bi).rows.size();
		
		
		
		
		int rowcrit = 0; 

			
		    pos=-1;
		    for (int i = 0; i < rows.size(); i++) {
		          	Bielement c = rows.elementAt(i);
		    	
		    	    if (contain(bi,c.bi)) {
		          		if (pos == -1) pos = i;
		          		else {
		          			if (i-pos > 1) rowcrit += 1;
		          			pos = i;
		          		}
		          	}
		    }
		    
		    
		    gescrit+=rowcrit*biclust.biclusters.elementAt(bi).columns.size();

		    
		    
			
		}


    return gescrit;
	}
	
	
	
	
	public boolean contain(Integer id, Vector<Integer> ids) {
		boolean con = false;
		for (int i = 0; i < ids.size(); i++) {
			if (ids.elementAt(i) == id) return true;
		}
		return con;
	}
	
	
	
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void calculateMinMax() {
		Min = 0;
		Max = 0;
		
		for (int i = 0; i < columns.size(); i++) {
			for (int j = 0; j < rows.size(); j++) {
			    double value = columns.elementAt(i).isel.getValue(rows.elementAt(j).isel.getID());
			    if (Max < value) Max = value;
			    if (Min > value) Min = value;
			
			}
		}
	}
	
	
	
	
	
	

	
	public Vector<Bielement> unionC(Biclustering b) {
	   	     Vector<Bielement> u = new Vector();
	   	     
	   	     
	   	     for (int i = 0; i < b.biclusters.size(); i++) {
	   	    	 Bicluster bc = b.biclusters.elementAt(i);
	   	    	 
	   	    	 for (int j = 0;  j< bc.columns.size(); j++) {
	   	    		 ISelectable col = bc.columns.elementAt(j);
	   	    		 
	   	    		 boolean isIn = false;
	   	    		 for (int k = 0; k < u.size(); k++) {
	   	    			 if (u.elementAt(k).isel.getID() == col.getID()) {
	   	    				 isIn = true;
	   	    				u.elementAt(k).bi.add(i);
	   	    			 }
	   	    		 }
	   	    		 if (!isIn) {
	   	    			 Bielement bi = new Bielement(col);
	   	    			 bi.bi.add(i);
	   	    			 u.add(bi);
	   	    		 }
	   	    		 
	   	    		 
	   	    	 }
	   	     }
	   	     
	   	 
	   	     return u;
	}
	
	public Vector<Bielement> unionR(Biclustering b) {
  	     Vector<Bielement> u = new Vector();
  	     
  	     for (int i = 0; i < b.biclusters.size(); i++) {
  	    	 Bicluster bc = b.biclusters.elementAt(i);
  	    	 
  	    	 for (int j = 0;  j< bc.rows.size(); j++) {
  	    		 ISelectable col = bc.rows.elementAt(j);
  	    		 
  	    		 boolean isIn = false;
  	    		 for (int k = 0; k < u.size(); k++) {
  	    			 if (u.elementAt(k).isel.getID() == col.getID()) {
  	    				 isIn = true;
	   	    				u.elementAt(k).bi.add(i);
  	    			 }
  	    		 }
  	    		 if (!isIn) {
  	    			Bielement bi = new Bielement(col);
  	    			 bi.bi.add(i);
  	    			 u.add(bi);
  	    		 }
  	    		 
  	    		 
  	    	 }
  	     }
  	     
  	     
  	
	   	     return u;
}
	
	
	
	
	
	
	
	
class Bielement{
    
	public Vector<Integer> bi = new Vector();
    public ISelectable isel;
	
	public Bielement(ISelectable isel) {
	     	this.isel = isel;
	}

}




public void mouseEntered(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}







public void mouseExited(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}







public void mousePressed(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}







public void mouseReleased(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}	
	
	
	

   public Vector<Block> identifyBlocks(Vector<Bielement> cols, int bi) {
	   Vector<Block> blocks = new Vector();
	   
	   int blockStart = -1;
	   
	   System.out.println("Identify Blocks :  " + bi);
	   
	   for (int i = 0; i < cols.size(); i++) {
		   Bielement el = cols.elementAt(i);
		   if (contain(bi,el.bi) && blockStart == -1) {
			   blockStart = i;
			 
		   }
		   
		   if (!contain(bi,el.bi) && blockStart != -1) {
			   blocks.add(new Block(blockStart,i-1));
			 

			   blockStart = -1;
		   }
		   
		   
		   if (cols.size()-1 == i && blockStart != -1) {
			   blocks.add(new Block(blockStart,i));
			   blockStart = -1;


		   }
		   
			  
	   }
	   System.out.println();
	   
	   return blocks;
   }

	
	class Block{
		public int from,to;
		public Block(int from,int to) {
			this.from = from;
			this.to = to;
		}
	}




	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

		

		if (arg0.getKeyCode() == 38) {

			
			
			if (pixelH > 1) {
				pixelH--;
				repaint();
			}

		}

		if (arg0.getKeyCode() == 40) {
			
			pixelH++;
			repaint();
			
			
			
			
/*
			if (Aggregation > 1)
				Aggregation--;

			this.PixelCount = this.Rows.size() / Aggregation;
			this.calculateMatrixValues();

			globalView.infoLabel.setText("Aggregation: 1 : " + Aggregation);
			globalView.applyNewPixelSize();*/
		}

		if (arg0.getKeyCode() == 39) {

			pixelW++;
			repaint();

		}

		if (arg0.getKeyCode() == 37) {

			if (pixelW > 1)
				pixelW--;
				repaint();}
		
		biheatmap.updatePlot();

	}







	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}







	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
