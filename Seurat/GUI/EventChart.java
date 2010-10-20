package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

import Data.AnnGene;
import Data.CGHVariable;
import Data.Clone;
import Data.DescriptionVariable;
import Data.ISelectable;
import Data.Variable;
import GUI.BarchartPanel.Bar;
import Tools.Tools;

public class EventChart extends JFrame implements IPlot{

	EventPanel panel;
    JMenuItem item;
    
    
	
	public EventChart(Seurat seurat, DescriptionVariable time, DescriptionVariable status) {
		super("EventChart (" +time.name + " , " + status.name +")");
		seurat.windows.add(this);

		item = new JMenuItem("EventChart");
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
		
		
		/*
		if (seurat.dataManager.descriptionVariables == null) {
			JOptionPane.showMessageDialog(this, " Clinical data not opened!");
			return;
		}

		
		DescriptionVariable var = seurat.dataManager
				.getDescriptionVariable(seurat.dataManager.SurvivalDays);

		if (var == null) {
			JOptionPane.showMessageDialog(this, "Variable "
					+ seurat.dataManager.SurvivalDays + " not found");
			return;
		}
		
		*/

		panel = new EventPanel(seurat, this, time, status);
		
		
		panel.setPreferredSize(new Dimension(500,2*panel.abstandOben + panel.h*panel.patients.size()));
		this.addKeyListener(panel);
		
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		this.setBounds(350, 0, 530, Math.min(750, panel.h*(panel.patients.size()+1)+ 2*panel.abstandOben+20));
		this.setVisible(true);

	}

	public void brush() {
		// TODO Auto-generated method stub
		
	}

	public void print() {
		// TODO Auto-generated method stub
		
	}

	public void removeColoring() {
		// TODO Auto-generated method stub
		
	}

	public void updateSelection() {
		// TODO Auto-generated method stub
	    repaint();	
	}

}

class EventPanel extends JPanel implements KeyListener,MouseListener, MouseMotionListener{

	DescriptionVariable time;
	DescriptionVariable status;
	Seurat seurat;
	Vector<Patient> patients;
	double maxValue;
	
	int abstandLinks = 100;
	int abstandOben = 15;
    int abstandLabel = 7;
	
	int h = 13;
    Point point1,point2;
    
    EventChart chart;
    
    Vector<Integer> groupBy;
    Vector<String>  groupByStrings;
	
	public EventPanel(Seurat seurat,EventChart chart, DescriptionVariable time, DescriptionVariable status) {
		this.seurat = seurat;
		this.chart = chart;
		
       
		calculatePatients(seurat, time, status);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        
    	ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setReshowDelay(0);
		
	}
	
	
	public int calcLabelWidth(Graphics g) {
		
		int Width = 0;
		for (int i = 0; i < patients.size(); i++){
		     if (Width < Tools.getStringWidth(patients.elementAt(i).name,g)) Width = Tools.getStringWidth(patients.elementAt(i).name,g);	
		}
		return Width;
	}
	
	
	public String getToolTipText(MouseEvent e) {
		
		if (e.isControlDown()) { 
		for (int i = 0; i < patients.size(); i++) {
			if ( Math.abs(abstandOben+ h/4 + h*i - e.getY())<h/2) {
		
		
		if (i >= 0 && i < patients.size()) {
		  Patient pat = patients.elementAt(i);
		  String s = "<html>";
		  for (int k = 0; k < seurat.dataManager.descriptionVariables.size(); k++) {
			  DescriptionVariable var = seurat.dataManager.descriptionVariables.elementAt(k); 
			 s += var.getName() + ": " + pat.patient.getStringValue(var)+"<br>";
			  
			  
		  }
		  
		  /*
		  pat.name+"<br>" + "survival days: "+pat.value + "<br>";
		  if (pat.isAlive == 0) s+="status: alive";
		  if (pat.isAlive == 1) s+="status: dead";
		  */
		  
		  return s;
		}
			}
		
		}
		}
		
		return null;
	}

	public void calculatePatients(Seurat seurat, DescriptionVariable time, DescriptionVariable status) {
		patients = new Vector();
		Vector<Variable> exps = seurat.dataManager.Experiments;
		
		Vector<String> buffer = Tools.sortBuffer(status.stringBuffer);
		
		for (int i = 0; i < exps.size(); i++) {
			Variable exp = exps.elementAt(i);
			double value = exp.getValue(time);
			
			int s = -1;
			
			if (status != null) {
				String ss =  exp.getStringValue(status);
				if (ss.equals(buffer.elementAt(1))) s = 1;
				if (ss.equals(buffer.elementAt(0))) s = 0;
			} 
			
			Patient pat = new Patient(exp,value,s);
			patients.add(pat);
			
			if (value != seurat.dataManager.NA && value > maxValue) {
				maxValue = value;
			}
			
		}
		
		
		
		
		
	}
	
	
	
	
	public void paint(Graphics g) {
		
		g.setColor(Color.WHITE);
		g.fillRect(0,0,this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);
		
	
		
		if (groupBy != null) {
			  g.setColor(Color.BLUE);
			  
			  /*
			  g.drawLine(1,
					     
				       (int)Math.round( abstandOben - h/2),
			          this.getWidth()-1,
			          (int)Math.round( abstandOben  - h/2)
		     );
			  
			  g.drawLine(1,
					     
				       (int)Math.round( abstandOben + h*patients.size() - h/2),
			          this.getWidth()-1,
			          (int)Math.round( abstandOben + h*patients.size() - h/2)
		     );
			  
			  g.drawLine(1,
					     
					  (int)Math.round( abstandOben - h/2),
			          1,
			          (int)Math.round( abstandOben + h*patients.size() - h/2)
		     ); 
			  
			  
			  
			  g.drawLine(this.getWidth()-1,
					     
					  (int)Math.round( abstandOben - h/2),
					  this.getWidth()-1,
			          (int)Math.round( abstandOben + h*patients.size() - h/2)
		     ); 
			  
			  
			  */
			  
			  int index = 0;
			 
			  g.setFont( new Font("Arial",Font.PLAIN,h*3/4));
			  
			  
			  for (int i = 0; i < groupBy.size(); i++) {
				  int k = groupBy.elementAt(i);
				  String s = groupByStrings.elementAt(i);
				  int len = Tools.getStringWidth(s,g);
				  
				  
				  g.drawLine(2,
						     
					       (int)Math.round( abstandOben + h*k + h/2),
				          this.getWidth()-2,
				          (int)Math.round( abstandOben + h*k + h/2)
				          );
				  
				
				  
				  g.drawString(s,abstandLabel+getWidth()-abstandLinks, abstandOben + h*(k+index)/2 + h/4);
				  index = k+1;
				  
		      }
			  
			  
			  String s = groupByStrings.lastElement();
			  int len = Tools.getStringWidth(s,g);
			  
			  g.drawString(s,abstandLabel+getWidth()-abstandLinks, abstandOben + h*(patients.size()+index-1)/2+ h/4);
				
			  
			  
			  
			  
		}
		
		 g.setFont( new Font("Arial",Font.PLAIN,h*3/4));
		abstandLinks = 2*abstandLabel + calcLabelWidth(g);
		
		
		for (int i = 0; i < patients.size(); i++) {
		  Patient p = patients.elementAt(i);
		  String name = p.name;
		  double value = p.value;
		  
		  
		  if (value != seurat.dataManager.NA) { 
			  
			  
			  g.setColor(Color.BLACK);
			 
		     g.drawString(name, abstandLabel, abstandOben+ h/4 + h*i);
		     
		     if (p.patient.isSelected()) {
		    	 g.setColor(Color.RED);
		     }
		     else g.setColor(Color.BLACK);
				 
		     
		     
		     g.fillRect(abstandLinks,
				       (int)Math.round( abstandOben + h*i ),
			           (int) Math.round( (this.getWidth() - 2*abstandLinks)*value/maxValue),
			   		   1
		     );
		     if (p.isAlive == 1) {
		    	 g.fillRect(abstandLinks + (int) Math.round( (this.getWidth() - 2*abstandLinks)*value/maxValue),
					       (int)Math.round( abstandOben + h*i )-2,
				           3,
				   		   6
			     );
		     }
		  
		  }
		  else {
			  g.drawString(name + "   NA", abstandLabel, abstandOben+5 + h*i);
				
		  }
		  
		}
		
		
		g.setColor(Color.BLACK);
		
		 g.drawLine(abstandLinks,
			       (int)Math.round( abstandOben + h*patients.size() ),
		           this.getWidth()-abstandLinks,
		           (int)Math.round( abstandOben + h*patients.size() )
	     );
		 
		 g.drawLine(abstandLinks,
			       (int)Math.round( abstandOben + h*patients.size() ),
		           abstandLinks,
		           (int)Math.round( abstandOben + h*patients.size() )+4
	     );
		 
		 
		 g.drawLine(this.getWidth()-abstandLinks,
			       (int)Math.round( abstandOben + h*patients.size() ),
		           this.getWidth()-abstandLinks,
		           (int)Math.round( abstandOben + h*patients.size() )+4
	     );
		 
		 g.drawString("0", abstandLinks, (int)Math.round( abstandOben + h*patients.size() )+ h);
		
		 g.drawString(""+maxValue, getWidth()- abstandLinks - Tools.getStringWidth(""+maxValue,g), (int)Math.round( abstandOben + h*patients.size() )+h);
		
		
		
		
		
		
		
		
		
		
		if (point1 != null && point2 != null) {
			g.setColor(Color.BLACK);
			
			
				g.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y,
						point2.y), Math.abs(point2.x - point1.x), Math
						.abs(point2.y - point1.y));
			

			
		}
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

	public void keyPressed(KeyEvent arg0) {
		
		if (arg0.getKeyCode() == 38) {

		
			
			if (h > 1) {
				h--;
				setPreferredSize(new Dimension(this.getWidth(),2*abstandOben + h*patients.size()));
				
				repaint();
				chart.repaint();
				
			}

		}

		if (arg0.getKeyCode() == 40) {
			
			h++;
			setPreferredSize(new Dimension(this.getWidth(),2*abstandOben + h*patients.size()));
			
			repaint();
			chart.repaint();
			
			
			
		
		}

		

	}

	public void keyReleased(KeyEvent arg0) {
		}

	public void keyTyped(KeyEvent arg0) {
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		point1 = e.getPoint();
		
		if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {

			
		JPopupMenu menu = new JPopupMenu();

		
		
		
		JMenuItem item = new JMenuItem("original");
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// createCorrelationExperiments();
				groupBy = null;
				groupByStrings = null;
				
                 calculatePatients(seurat,time, status);
                 repaint();
			}
		});
		menu.add(item);
		
		
		
		JMenu m = new JMenu("Sort by ...");
		
		
		item = new JMenuItem("reverse");
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// createCorrelationExperiments();
                reverse();
			}
		});
		m.add(item);
		
		
		
		
		for (int i = 0; i < seurat.dataManager.descriptionVariables.size(); i++) {
			DescriptionVariable var = seurat.dataManager.descriptionVariables.elementAt(i);
			
			if (var.isDouble && !var.isDiscrete) {
				
			item = new JMenuItem(var.name);
			item.setActionCommand(""+i);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// createCorrelationExperiments();
                    int i = Integer.parseInt(e.getActionCommand());
					sortBy(seurat.dataManager.descriptionVariables.elementAt(i));
				}
			});
			m.add(item);
			
			}
			
		}
		
		menu.add(m);
		
		
		 m = new JMenu("Group by ...");
			
			for (int i = 0; i < seurat.dataManager.descriptionVariables.size(); i++) {
				DescriptionVariable var = seurat.dataManager.descriptionVariables.elementAt(i);
				
				if (var.isDiscrete) {
				
				item = new JMenuItem(var.name);
				item.setActionCommand(""+i);
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// createCorrelationExperiments();
	                    int i = Integer.parseInt(e.getActionCommand());
						groupBy(seurat.dataManager.descriptionVariables.elementAt(i));
					}
				});
				m.add(item);
				
				}
				
			}
			
			
		menu.add(m);	
		
		
		
		menu.show(this, e.getX(), e.getY());
		}
		
		
		
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

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		point2 = e.getPoint();

		if (point1 != null	&& point2 != null && (point1.getX() - point2.getX())
						* (point1.getY() - point2.getY()) < 0) {
			Point p = point1;
			point1 = point2;
			point2 = p;

		}
		
		
		
		if (point1 != null && point2 != null) {

			if (!e.isShiftDown()) seurat.dataManager.deleteSelection();
		    applySelection((int)point1.getX(),(int)point1.getY(),(int)point2.getX(),(int)point2.getY());	
		 
		 	
		}
		
		
		point1 = null;
		point2 = null;
	}
	
	
	public void applySelection(int x1,int y1, int x2, int y2) {
		boolean selected = false;
		for (int i = 0; i < patients.size(); i++) {
			if (Tools.containsRectInRect(x1,y1,x2,y2, 
					abstandLinks,
				       (int)Math.round( abstandOben + h*i ),
			           abstandLinks + (int) Math.round( (this.getWidth() - 2*abstandLinks)*patients.elementAt(i).value/maxValue),
			           (int)Math.round( abstandOben + h*i )+ 2
			   		   
			)) {
				
				patients.elementAt(i).patient.select(true);
				selected = true;
			}
		}
		
		
		if (selected) { 
				seurat.dataManager.selectGenesClones(); 
		    }
			
			
			
		
		seurat.repaintWindows();
	}
	
	
	
	public void sortBy(DescriptionVariable var) {
	     Vector<Patient> newPat = new Vector();
	     
	     groupBy = null;
	     groupByStrings = null;
	     
	     for (int i = 0; i < patients.size(); i++) {
				Patient p = patients.elementAt(i);
				int j = 0;
				while (j < newPat.size()
						&& compare(p.patient,newPat.elementAt(j).patient,var)) {
					j++;
				}
				newPat.insertElementAt(p, j);

			}
	     

	   patients = newPat;
			
			repaint();
			
	     
	}
	
	
	public void reverse() {
         Vector<Patient> newPat = new Vector();
	     
	     groupBy = null;
	     groupByStrings = null;
	     
	     for (int i = 0; i < patients.size(); i++) {
				newPat.add(patients.elementAt(patients.size()-i-1));

			}
	     

	   patients = newPat;
			
			repaint();
	}
	
	
	
	
	public void groupBy(DescriptionVariable var) {
	     Vector<Patient> newPat = new Vector();
	     
	     groupBy = new Vector();
	     groupByStrings = new Vector();
	     
	     for (int i = 0; i < patients.size(); i++) {
				Patient p = patients.elementAt(i);
				int j = 0;
				while (j < newPat.size()
						&& compare(p.patient,newPat.elementAt(j).patient,var)) {
					j++;
				}
				newPat.insertElementAt(p, j);
				

			}
	     
	     patients = newPat;
	     
	     
	     for (int i = 0; i < patients.size()-1; i++) {
	    	 if (!patients.elementAt(i).patient.getStringValue(var).equals(patients.elementAt(i+1).patient.getStringValue(var))) {
	    		 groupBy.add(i);
	    		 groupByStrings.add(patients.elementAt(i).patient.getStringValue(var));
	    	 }
	     }
	     
	     groupByStrings.add(patients.lastElement().patient.getStringValue(var));
		    
	     

	  
			
			repaint();
			
	     
	}
	
	
	
	
	public boolean compare(Variable p1,Variable p2, DescriptionVariable var) {
		if (var.isDouble) {
		    return (p1.getValue(var) < p2.getValue(var));
				
		}
		
		return Tools.compareLexico(p1.getStringValue(var) , p2.getStringValue(var));
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
		point2 = e.getPoint();
		this.repaint();
		
		
		
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	

}

class Patient {

	String name;
	Variable patient;
	double value;
	int isAlive;  /*0 alive, 1 dead -1 not avalilable*/

	public Patient(Variable var, double value, int isAlive) {
		this.patient = var;
		this.value = value;
		this.isAlive = isAlive;
		name = var.name.replace("\"","");
	}
}
