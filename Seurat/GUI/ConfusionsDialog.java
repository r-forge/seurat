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

public class ConfusionsDialog extends JFrame {

	JTextField field = new JTextField("      ");

	JButton okBtn = new JButton("Ok");


	JComboBox box1;

	JComboBox box2;

	Seurat seurat;
	
	DataManager dataManager;
	
	ConfusionsDialog dialog;
	
	
	JRadioButton experimentsBtn;
	
	JRadioButton genesBtn;
	

	public ConfusionsDialog(Seurat seurat) {
		super("Confusion Matrix");
		this.seurat = seurat;
		this.dialog = this;
		
		
		TreePath [] paths = seurat.tree.getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			Object o = paths [i].getLastPathComponent();
			if (o != null) {
			DataTreeNode node = (DataTreeNode)o;
			System.out.println(o);
			}
		}
		
		this.dataManager = seurat.dataManager;
		
		this.setBounds(100, 300, 380, 210);
         this.setResizable(false);
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		
		
		panel.setLayout(new GridLayout(2,1));
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		 ButtonGroup gr = new ButtonGroup();
	     experimentsBtn = new JRadioButton("Samples");
	     gr.add(experimentsBtn);
		    genesBtn = new JRadioButton("Genes");
		 
			gr.add(genesBtn);
			experimentsBtn.setSelected(true);
			
		panel.add(this.experimentsBtn);
		panel.add(this.genesBtn);
		
		
		
		genesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector names = new Vector();
				for (int i = 0; i < dataManager.GeneClusters.size(); i++) {
		          names.add(dataManager.GeneClusters.elementAt(i).name);			
				}
				
			
				box1.removeAllItems();
				Iterator iter = names.iterator();
				while (iter.hasNext()) box1.addItem(iter.next());
				
				
				box2.removeAllItems();
				iter = names.iterator();
				while (iter.hasNext()) box2.addItem(iter.next());
				
				
			}
			
		});
		
		
		
		
		experimentsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector names = new Vector();
				for (int i = 0; i < dataManager.ExpClusters.size(); i++) {
		          names.add(dataManager.ExpClusters.elementAt(i).name);			
				}
				
			
				box1.removeAllItems();
				Iterator iter = names.iterator();
				while (iter.hasNext()) box1.addItem(iter.next());
				
				
				box2.removeAllItems();
				iter = names.iterator();
				while (iter.hasNext()) box2.addItem(iter.next());
				
				
			}
			
		});
		
		
		
		
		this.getContentPane().add(panel,BorderLayout.NORTH);	
			
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(2,2));
		
		
		Vector names = new Vector();
		for (int i = 0; i < dataManager.ExpClusters.size(); i++) {
          names.add(dataManager.ExpClusters.elementAt(i).name);			
		}
		
	
		box1 = new JComboBox(names);
		box2 = new JComboBox(names);
		
		
		panel.add(new JLabel("Method1:"));
		JPanel panel2 = new JPanel();
		panel2.add(box1);
		panel.add(panel2);
		panel.add(new JLabel("Method2:"));
		panel2 = new JPanel();
		panel2.add(box2);
		panel.add(panel2);
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		this.getContentPane().add(panel,BorderLayout.CENTER);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(okBtn,BorderLayout.EAST);

		this.getContentPane().add(panel,BorderLayout.SOUTH);
		
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				int i = box1.getSelectedIndex();
				int j = box2.getSelectedIndex();
				if (experimentsBtn.isSelected())
				new ConfusionsPlot(dialog.seurat,(String)box1.getSelectedItem(), (String)box2.getSelectedItem(), dataManager.ExpClusters.elementAt(i),dataManager.ExpClusters.elementAt(j)); 
				else	
				new ConfusionsPlot(dialog.seurat,(String)box1.getSelectedItem(), (String)box2.getSelectedItem(), dataManager.GeneClusters.elementAt(i),dataManager.GeneClusters.elementAt(j)); 
				
				
				
				
				
				dialog.setVisible(false);
			}
		});

		this.setVisible(true);
	}

	

}