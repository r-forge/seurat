package GUI;

import java.util.*;
import Data.*;

import java.io.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;

import javax.swing.table.*;
import javax.swing.tree.TreePath;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.datatransfer.*;
import javax.imageio.*;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import Data.ClusterNode;


import java.util.zip.*;
import java.util.jar.*;

public class NewConfusionDialog extends JFrame {

	JTextField field = new JTextField("      ");

	JButton okBtn = new JButton("Visualise");


	JList box1;

	JList box2;

	Seurat seurat;
	
	Vector<Clustering> Rows = new Vector();
	Vector<Clustering> Columns = new Vector();
	
	NewConfusionDialog dialog;
	
	
	JRadioButton experimentsBtn;
	
	JRadioButton genesBtn;
	
	JPanel bPanel,mainPanel;
	

	public NewConfusionDialog(Seurat seurat) {
		super("New Confusion Dialog");
		this.seurat = seurat;
		this.dialog = this;
		
		
		
		for (int i = 0; i < seurat.dataManager.ExpClusters.size(); i++) {
          Columns.add(seurat.dataManager.ExpClusters.elementAt(i));			
		}
		
		
		for (int i = 0; i < seurat.dataManager.GeneClusters.size(); i++) {
	          Rows.add(seurat.dataManager.GeneClusters.elementAt(i));			
		}
		
		
		okBtn.setEnabled(false);
		
		
		this.setBounds(100, 300, 500, 320);
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		
		
		panel.setLayout(new GridLayout(1,2));
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		 ButtonGroup gr = new ButtonGroup();
	     experimentsBtn = new JRadioButton("Samples");
	     gr.add(experimentsBtn);
		 genesBtn = new JRadioButton("Genes");
		 
		 gr.add(genesBtn);
		 experimentsBtn.setSelected(true);
			
		 panel.add(this.experimentsBtn);
		 panel.add(this.genesBtn);
		 this.getContentPane().add(panel,BorderLayout.NORTH);
		 
		genesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
           
            	
            	
	Vector names = new Vector();
				for (int i = 0; i < Rows.size(); i++) {
		          names.add(Rows.elementAt(i).name);			
				}
				
				
				createLists(names);
				 valueChanged();
				
			}
			
		});
		
		
		
		
		experimentsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector names = new Vector();
				for (int i = 0; i < Columns.size(); i++) {
		          names.add(Columns.elementAt(i).name);			
				}
				
			
				
				createLists(names);
				 valueChanged();
			}
			
		});
		
			
			
		
		
		
		Vector names = new Vector();
		
		
		for (int i = 0; i < Columns.size(); i++) {
          names.add(Columns.elementAt(i).name);			
		}
		
		createLists(names);
	
		
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		//this.getContentPane().add(panel,BorderLayout.CENTER);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(okBtn,BorderLayout.EAST);

		this.getContentPane().add(panel,BorderLayout.SOUTH);
		
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				int i = box1.getSelectedIndex();
				int j = box2.getSelectedIndex();
				
				if (experimentsBtn.isSelected())
				new ConfusionsPlot2(dialog.seurat,(String)box1.getSelectedValue(), (String)box2.getSelectedValue(), Columns.elementAt(i),Columns.elementAt(j)); 
				else	
				new ConfusionsPlot2(dialog.seurat,(String)box1.getSelectedValue(), (String)box2.getSelectedValue(), Rows.elementAt(i),Rows.elementAt(j)); 
				
				dialog.setVisible(false);
			}
		});

		this.setVisible(true);
	}

	
	
	
	public void createLists(Vector names) {
		
		if (bPanel != null) {
		mainPanel.remove(bPanel);
		} else {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			this.getContentPane().add(mainPanel,BorderLayout.CENTER);;
		}
		
		
		
		Iterator iter = names.iterator();
		Vector v = new Vector();
		while (iter.hasNext()) v.add(iter.next());
		box1 = new JList(v);
		
		iter = names.iterator();
		v = new Vector();
		while (iter.hasNext()) v.add(iter.next());
		box2 = new JList(v);
		
		
		box1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		box2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		bPanel = new JPanel();
		bPanel.setLayout(new GridLayout(1,2));

		bPanel.add(new JScrollPane(box1));
		bPanel.add(new JScrollPane(box2));

		mainPanel.add(bPanel,BorderLayout.CENTER);	
		dialog.update(dialog.getGraphics());
		dialog.setVisible(true);
		
		
		
		
		
		box1.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				dialog.valueChanged();
			}
			
		});
		
		
		box2.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				dialog.valueChanged();
			}
			
		});
		
		
		
		
		
		
	}
	
	
	
	
	public void valueChanged(){
		int i = box1.getSelectedIndex();
		int j = box2.getSelectedIndex();
		if (i == -1 || j == -1) okBtn.setEnabled(false);
		else {
			
			Clustering c1 = null;
			Clustering c2 = null; 
			
			if (experimentsBtn.isSelected()) {
			c1 = Columns.elementAt(i);
			c2 = Columns.elementAt(j);
			}else {
				c1 = Rows.elementAt(i);
				c2 = Rows.elementAt(j);
			}
			
			
			
			
			if (areClusteringsComparable(c1,c2)) okBtn.setEnabled(true);
			else okBtn.setEnabled(false);
			
		}
		
		
	}
	
	
	public boolean areClusteringsComparable(Clustering c1, Clustering c2) {
	      int size1 = 0;
	      for (int i = 0; i < c1.clusters.size(); i++) {
	    	  size1+=c1.clusters.elementAt(i).items.size();	  
	    	  
	      }
	      int size2 = 0;
	      for (int i = 0; i < c2.clusters.size(); i++) {
	    	  size2+=c2.clusters.elementAt(i).items.size();	  
	    	  
	      }
	      
	      if (size1 != size2) return false;
	      
	      
	      for (int i = 0; i < c1.clusters.size(); i++) {
	    	  for (int j = 0; j < c1.clusters.elementAt(i).items.size(); j++) {
	    		  if (!isObjectInClustering(c1.clusters.elementAt(i).items.elementAt(j),c2)) return false;
	    	  }	  
	    	  
	      }
	      
	      
	      return true;
	      
	      
	}
	
	
	public boolean isObjectInClustering(ISelectable o, Clustering c) {
		for (int i = 0; i < c.clusters.size(); i++) {
	    	  for (int j = 0; j < c.clusters.elementAt(i).items.size(); j++) {
	    		  if (o == c.clusters.elementAt(i).items.elementAt(j)) return true;
	    	  }	  
	    	  
	      }
		return false;
	}
	
	
	
	
	
	

}
