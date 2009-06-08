package GUI;

import java.util.*;

import Data.*;

import java.io.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;

import javax.swing.table.*;
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

public class SeriationDialog extends JFrame {

	JTextField field = new JTextField("      ");

	JButton okBtn = new JButton("Ok");

	String[] SeriationMethods = { "ARSA", "BBURCG", "BBWRCG", "TSP", "Chen",
			"MDS", "HC", "GW", "OLO" };

	JComboBox box = new JComboBox(SeriationMethods);

	String[] Distance = { "euclidean", "maximum", "manhattan", "canberra",
			"binary", "minkowski" };

	JComboBox boxD = new JComboBox(Distance);

	String[] SeriationControl = {};

	String[] SeriationDistance = {};

	Seurat seurat;
	
	DataManager dataManager;
	
	SeriationDialog dialog;
	
Vector<ISelectable> Experiments;
	
	Vector<ISelectable> Genes;
	
	

	public SeriationDialog(Seurat seurat,Vector Genes, Vector Exps) {
		super("Seriation");
		this.seurat = seurat;
		this.dialog = this;
		
        this.Genes = Genes;
        
        this.dataManager = seurat.dataManager;
		
		this.Experiments = Exps;
		
		this.setBounds(100, 300, 240, 140);
         this.setResizable(false);
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2,2));
		
	
		panel.add(new JLabel("Method:"));
		JPanel panel2 = new JPanel();
		panel2.add(box);
		panel.add(panel2);
		panel.add(new JLabel("Distance:"));
		panel2 = new JPanel();
		panel2.add(boxD);
		panel.add(panel2);
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		this.getContentPane().add(panel,BorderLayout.CENTER);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(okBtn,BorderLayout.EAST);

		this.getContentPane().add(panel,BorderLayout.SOUTH);
		
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String method = box.getSelectedItem().toString();
				String Distance = boxD.getSelectedItem().toString();

				seriation(method, Distance);
				
				dialog.setVisible(false);
			}
		});

		this.setVisible(true);
	}

	public void seriation(String method, String Distance) {
		
		
		try {
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();	
			
			
			dataManager.rConnection.voidEval("library(stats)");
		
			dataManager.rConnection.voidEval("require(seriation)");
			
			
			rConnection.assign("tempData", Experiments
					.elementAt(0).getColumn(Genes));

			for (int i = 1; i < Experiments.size(); i++) {
				dataManager.rConnection.assign("x", Experiments
						.elementAt(i).getColumn(Genes));
				dataManager.rConnection.voidEval("tempData <- cbind(tempData, x)");
				

			}

			dataManager.rConnection.voidEval("d<-dist(tempData,method=\""
					+ Distance + "\")");
			// rConnection.voidEval("order<-seriate(d,method=NULL,control =
			// NULL)");
			dataManager.rConnection.voidEval("order<-seriate(d,method=\"" + method
					+ "\")");

			int[] orderZeilen = dataManager.rConnection.eval("get_order(order,1)")
					.asIntegers();
				
						
			
			dataManager.rConnection.assign("tempData", Genes.elementAt(0).getRow(Experiments));

			for (int i = 1; i < Genes.size(); i++) {
				dataManager.rConnection.assign("x", Genes.elementAt(i).getRow(Experiments));
				dataManager.rConnection.voidEval("tempData <- cbind(tempData, x)");

			}

			dataManager.rConnection.voidEval("d<-dist(tempData,method=\""
					+ Distance + "\")");
			
			dataManager.rConnection.voidEval("order<-seriate(d,method=\"" + method
					+ "\")");

			int[] orderSpalten = dataManager.rConnection.eval("get_order(order,1)")
					.asIntegers();
				
			
			Vector<ISelectable> G = new Vector();
			
			for (int i = 0; i < Genes.size(); i++) {
				G.add(Genes.elementAt(orderZeilen[i]-1));
			}
			
			seurat.dataManager.seriationsGenes.add(G);
			seurat.dataManager.seriationGeneNames.add(method + " "+Distance);
			
			
			
			Vector<ISelectable> E = new Vector();
			

			for (int i = 0; i < Experiments.size(); i++) {
				E.add(Experiments.elementAt(orderSpalten[i]-1));
			}
			
			seurat.dataManager.seriationsExperiments.add(E);
			seurat.dataManager.seriationExperimentNames.add(method + " " + Distance);
			
			

			GlobalViewAbstract globalView = new GlobalViewAbstract(seurat, method, E,G,false);
            globalView.applyNewPixelSize(globalView.pixelSize);
		
			globalView.setLocation(350, 0);
			globalView.setVisible(true);

		} catch (RserveException e) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}
	} catch (Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Calculation failed.");
	}

	}

}
