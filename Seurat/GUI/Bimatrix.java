package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import Data.Bicluster;
import Data.Biclustering;
import Data.ISelectable;
import Data.MyColor;
import Settings.Settings;
import Tools.Tools;



public class Bimatrix extends JFrame{
	
	BiConfPanel panel;
	
	Biclustering biclust;
	
	int maxWidth = 400, maxHeight = 400;
	
	public Bimatrix(Biclustering biclust) {
		super("Bimatrix " + biclust.name);
	    this.biclust = biclust;
		panel = new BiConfPanel(this,biclust);
		
	    setPlotSize();
	    this.getContentPane().add(new JScrollPane(panel));

	    this.addKeyListener(panel);
	    setLocation(330,0);
		this.setVisible(true);
	}
	
	
	public void sortBiclusters() {
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			
			sortBicluster(i); 
			
		}
	}
	
	
	public void sortBicluster(int i) {
		Bicluster bi = biclust.biclusters.elementAt(i);
	}
	
	public int calculateCrit(int j) {
		int crit = 0;
		Bicluster cl = biclust.biclusters.elementAt(j);

		
	
		return crit;
	}
	
	
	
	
	
	
	public void setPlotSize() {
		int width = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			width+= biclust.biclusters.elementAt(i).colums.size();
		}
		
		int height = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			height+= biclust.biclusters.elementAt(i).rows.size();
		}
		
	    int pixelW = maxWidth /width;
	    int pixelH = maxHeight/height;
	    
	    if (pixelW == 0) pixelW = 1;
	    if (pixelH == 0) pixelH = 1;

	    
	    panel.pixelW = pixelW;
	    panel.pixelH = pixelH;
	    
		
		updatePlot();
	}

	
	public void updatePlot() {
		int width = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			width+= biclust.biclusters.elementAt(i).colums.size() ;
		}
		
		int height = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
			height+= biclust.biclusters.elementAt(i).rows.size() ;
		}
		
		
		width = panel.abstandLinks + panel.pixelW*width + biclust.biclusters.size()*panel.bidist;
		height = panel.abstandOben +panel.pixelH*height + biclust.biclusters.size()*panel.bidist;
		 panel.setPreferredSize(new Dimension(width,height));
		 setSize(width+6,height+35);
			
			
		setVisible(true);
	}
	
	
	
	
	
	
}





class BiConfPanel extends JPanel implements KeyListener{
	
	public Biclustering biclust;
	public double Max, Min;
    int abstandOben = 3;
    int abstandLinks = 3;

	int bidist = 6;
	
	int pixelW = 5;
	int pixelH = 5;
	
	Bimatrix biconf;
	
	public BiConfPanel(Bimatrix biconf,Biclustering biclust) {
	
		this.biconf = biconf;
		this.biclust = biclust;
		calculateMinMax();
	    this.addKeyListener(this);
	}
	
	
	
	public void calculateMinMax() {
		Max = 0;
		Min = 0;
		for (int i = 0; i < biclust.biclusters.size(); i++) {
	        Bicluster b = biclust.biclusters.elementAt(i);
	        for (int j = 0; j < b.colums.size(); j++) {
	        	ISelectable col = b.colums.elementAt(j);
	        	
	        	
	        	for (int ii = 0; ii < biclust.biclusters.size(); ii++) {
	    	        Bicluster bb = biclust.biclusters.elementAt(ii);
	    	        for (int jj = 0; jj < b.rows.size(); jj++) {
	    	        	ISelectable row = b.rows.elementAt(jj);
	    	        	
	    	        	double value  = col.getValue(row.getID());
	    	        	if (value>Max) Max = value;
	    	        	if (value < Min) Min = value;
	    	        }
	        	}
	        }
		}
	}
	
	
	
	
	public void paint(Graphics g) {
	    g.setColor(Color.WHITE);
	  //  g.setColor(new Color(235,235,235));
	    g.fillRect(0,0,this.getWidth(), this.getHeight());
		
	    Vector<Bicluster> biclusters = biclust.biclusters;
	    Vector<Integer> xStart = new Vector();
	    Vector<Integer> yStart = new Vector();
        int maxX = 0;
        int maxY = 0;
	    
	    
	    for (int i = 0; i < biclusters.size(); i++) {
	    
	    	xStart.add(maxX);
	    	yStart.add(maxY);
	    	
	    	
	    	Bicluster b = biclusters.elementAt(i);
	        maxX += b.colums.size()*pixelW+bidist;
	        maxY += b.rows.size()*pixelH+bidist;
	    
	    }
	    
	    
	    
	    
	    for (int i = 0; i < biclusters.size(); i++) {
	    for (int j = 0; j < biclusters.size(); j++) {
	    	
	    	  g.setColor(new Color(249,249,249));
	  	    
	    	g.fillRect(
						abstandLinks+( xStart.elementAt(i))-1 , 
						 abstandOben+(yStart.elementAt(j))-1 , 
						pixelW*biclusters.elementAt(i).colums.size() +2, 
						pixelH*biclusters.elementAt(j).rows.size()+2
					  ); 
	    	
	    	 g.setColor(new Color(212,212,212));
					
	    	 g.drawLine(
	    			 abstandLinks+( xStart.elementAt(i)) + pixelW*biclusters.elementAt(i).colums.size() +1, 
					 abstandOben+(yStart.elementAt(j)) , 
					 abstandLinks+( xStart.elementAt(i)) + pixelW*biclusters.elementAt(i).colums.size() +1, 
					 abstandOben+(yStart.elementAt(j))-1+pixelH*biclusters.elementAt(j).rows.size()+2
	    	 
	    	 );
	    	 
	    	 
	    	 g.drawLine(
	    			 abstandLinks+( xStart.elementAt(i)) +1, 
					 abstandOben+(yStart.elementAt(j)) +pixelH*biclusters.elementAt(j).rows.size()+1 , 
					 abstandLinks+( xStart.elementAt(i)) + pixelW*biclusters.elementAt(i).colums.size() +2, 
					 abstandOben+(yStart.elementAt(j))+pixelH*biclusters.elementAt(j).rows.size()+1
	    	 
	    	 );
	    	 
	    	 
	    	 
	    	 
	    	 
						
	    	
	    	    Bicluster  b = intersect(biclusters.elementAt(i),biclusters.elementAt(j));
	    	    
	    	    
	    	    if (b.colums.size()>0 && b.rows.size()>0) {
	    	    	
	    	    	
	    	    	
	    	    	 for (int ii = 0; ii < b.colums.size(); ii++) {
	    	    	 for (int jj = 0; jj < b.rows.size(); jj++) {
	    	    	
	    	         ISelectable col = b.colums.elementAt(ii);
	    	         ISelectable row = b.rows.elementAt(jj);
	 
	    	    		 
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
	  					
	  					
	  					g.setColor(c);
	  					
	  					
	  					int shiftX = ii;
	  					int shiftY = jj;
	  					if (i != j)
	  					{
	  						shiftX = getPosition(col,biclusters.elementAt(i).colums);
	  						shiftY = getPosition(row,biclusters.elementAt(j).rows);

	  					}
	  					
	  						g.fillRect(
		  							abstandLinks + xStart.elementAt(i) + shiftX*pixelW , 
		  							abstandOben + yStart.elementAt(j) +shiftY*pixelH , 
		  							pixelW , 
		  							pixelH 
		  					
		  				
		  				
		  					
		  					
		  					);
	  						
	  					
	  					
	  					/*
	  					g.fillRect(
	  							abstandLinks+( xStart.elementAt(i)+ii)*pixelW , 
	  							 abstandOben+(yStart.elementAt(j)+jj)*pixelH , 
	  							pixelW , 
	  							pixelH 
	  					
	  					
	  					
	  					);
	  					*/
	  				
	  					
	  					
	  					
	  				
	  					
	  					g.setColor(Color.BLACK);
	  		    	    
		    	    	if (i == j) {
				        	 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*i/biclusters.size()),(float)0.95,(float)0.95));

				        	 /*
		    	    		g.drawRect(
		    	    	
		    	    			xStart.elementAt(i)*pixelW , 
	  							abstandOben+yStart.elementAt(j)*pixelH , 
	  							b.colums.size()*pixelW , 
	  							b.rows.size()*pixelH );
		    	    		*/
		    	    		g.drawRect(
		    		    	    	
		    	    				abstandLinks+ xStart.elementAt(i) -2, 
		  							abstandOben+yStart.elementAt(j)-2 , 
		  							b.colums.size()*pixelW +3, 
		  							b.rows.size()*pixelH+3);
		    	    		
				        	 g.setColor(Color.getHSBColor((float)(0.1 + 0.9*i/biclusters.size()),(float)0.75,(float)0.75));

		    	    	
				        	 g.drawLine(
			    		    	    	
			    	    				abstandLinks+ xStart.elementAt(i) -1 + b.colums.size()*pixelW +3, 
			  							abstandOben+yStart.elementAt(j)-1 , 
			  							abstandLinks+ xStart.elementAt(i) -1 + b.colums.size()*pixelW +3, 
			  							abstandOben+yStart.elementAt(j)-2  + b.rows.size()*pixelH+3);
				        	 
				        	 g.drawLine(
			    		    	    	
			    	    				abstandLinks+ xStart.elementAt(i) -1, 
			  							abstandOben+yStart.elementAt(j)-1 + b.rows.size()*pixelH+3 , 
			  							abstandLinks+ xStart.elementAt(i) -1+ b.colums.size()*pixelW +3, 
			  							abstandOben+yStart.elementAt(j)-1 + b.rows.size()*pixelH+3)
			  							;
				        	 
				        	 
		    	    	
		    	    	}
	  					
	  					
	  					}
	    	         }
	    	    
	    	    
	    	    
                  
	    	    
	    	    
	    	    }
	    	    
	    	    /*
	    		g.setColor(new Color(5,5,5));
					
					g.drawRect(
							( xStart.elementAt(i))*pixelW , 
							 abstandOben+(yStart.elementAt(j))*pixelH , 
							pixelW * biclusters.elementAt(i).colums.size() , 
							pixelH * biclusters.elementAt(j).rows.size()
					
					
					
					);
					*/
					
					
					
		}    
	    }
	    
	    
	    
	    
	    
	}
	
	
	
	public Bicluster intersect(Bicluster b1, Bicluster b2) {
	     Vector<ISelectable> cols1 = b1.colums;	
	     Vector<ISelectable> cols2 = b2.colums;	

	     Vector<ISelectable> rows1 = b1.rows;	
	     Vector<ISelectable> rows2 = b2.rows;	
   
	     return new Bicluster(b1.name + " " + b2.name, intersectV(b1.rows,b2.rows), intersectV(b1.colums,b2.colums)); 
	     
	}
	
	
	public int getPosition(ISelectable el, Vector<ISelectable> v) {
		for (int i = 0; i < v.size(); i++) {
			if (el.getID() == v.elementAt(i).getID()) return i;
		}
		return -1;
	}
	
	
	public Vector<ISelectable> intersectV(Vector<ISelectable> v1, Vector<ISelectable> v2) {
	       Vector<ISelectable> v = new Vector();
	       
	       for(int i = 0; i < v1.size(); i++) {
	       for(int j = 0; j < v2.size(); j++) {
	   	         if (v1.elementAt(i).getID() == v2.elementAt(j).getID()) {
	   	        	 v.add(v1.elementAt(i)); 	
	   	        	 break;
	   	         }
	       }
	       }
	       
	       return v;
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
		
		
		biconf.updatePlot();

	}





	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
}


