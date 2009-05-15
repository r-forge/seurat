package GUI;

import java.util.*;
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

import org.rosuda.REngine.Rserve.RConnection;

import Data.ClusterNode;


import java.util.zip.*;
import java.util.jar.*;

import Data.*;

public class KMeansDialog extends JFrame {

	JTextField fieldC = new JTextField(5);
	JTextField fieldR = new JTextField(5);
	
	
	
	JButton okBtn = new JButton("Ok");

	String[] ClusteringMethods = { "ward", "single", "complete", "average",
			"mcquitty", "median", "centroid", "kmeans"};

	String[] Distance = { "euclidean", "maximum", "manhattan", "canberra",
			"binary", "minkowski" };

	JComboBox boxColumns = new JComboBox(ClusteringMethods);

	JComboBox boxDColumns = new JComboBox(Distance);

	JComboBox boxRows = new JComboBox(ClusteringMethods);

	JComboBox boxDRows = new JComboBox(Distance);

	String[] SeriationControl = {};

	String[] SeriationDistance = {};

	Seurat seurat;

	KMeansDialog dialog = this;

	int[] orderZeilen;

	int[] orderSpalten;

	ClusterNode nodeZeilen;

	ClusterNode nodeSpalten;
	
	
	Vector<ISelectable> Experiments;
	
	Vector<ISelectable> Genes;
	
	
	
	
	DataManager dataManager;

	public KMeansDialog(Seurat seurat,Vector Genes, Vector Exps) {
		super("Clustering");
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.setBounds(100, 270, 350, 190);

		
		this.Genes = Genes;
		
		this.Experiments = Exps;
		
		
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,3));

		panel.add(new JLabel("   "));

		panel.add(new JLabel("Columns:"));

		panel.add(new JLabel("Rows:"));

		
		
		
		
		panel.add(new JLabel("Count:"));

		panel.add(fieldC);

		panel.add(fieldR);
		
		
		panel.add(new JLabel("Method:       "));
		
		
		
		JPanel panel2 = new JPanel();
		panel2.add(boxColumns);
		panel.add(panel2);
		
		
		panel2 = new JPanel();
		panel2.add(boxRows);
		panel.add(panel2);

		panel.add(new JLabel("Distance:    "));
		
		panel2 = new JPanel();
		panel2.add(boxDColumns);
		panel.add(panel2);
		
		
		panel2 = new JPanel();
		panel2.add(boxDRows);
		panel.add(panel2);

	

		panel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(panel,BorderLayout.CENTER);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
	    panel.add(okBtn, BorderLayout.EAST);
	    
	    this.setResizable(false);
		
		this.getContentPane().add(panel,BorderLayout.SOUTH);
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String methodColumns = boxColumns.getSelectedItem().toString();
				String DistanceColumns = boxDColumns.getSelectedItem()
						.toString();

				String methodRows = boxRows.getSelectedItem().toString();
				String DistanceRows = boxDRows.getSelectedItem().toString();
				int nR = Integer.parseInt(fieldR.getText());
				int nC = Integer.parseInt(fieldC.getText());
				
				
				/*
				 * HeatMap genesFrame = new HeatMap(dialog.seurat, "Clustering",
				 * methodColumns, DistanceColumns, methodRows, DistanceRows);
				 * genesFrame.setLocation(300, 0);
				 */
				/*
				 * GlobalView globalView = new GlobalView(dialog.seurat,
				 * "Clustering", methodColumns, DistanceColumns, methodRows,
				 * DistanceRows);
				 * 
				 */
				
				Vector<Vector<ISelectable>> Gens = new Vector();
				if (nR != 0) {
					Gens = calculateClustersZeilen(nR, methodRows, DistanceRows);
				}
				else {
					
					Gens.add(dialog.Genes);
				}
				
				
				Vector<Vector<ISelectable>> Exps = new Vector();
				if (nC != 0) {
					Exps = calculateClustersSpalten(nC, methodColumns, DistanceColumns);
				}
				else {
					
					Exps.add(Experiments);
				}
				
					
				dialog.seurat.dataManager.GeneClusters.add(new Clustering(methodRows + " , " + DistanceRows + " , "+nR, Gens));
				dialog.seurat.dataManager.ExpClusters.add(new Clustering(methodColumns + " , " + DistanceColumns + " , "+nC, Exps));
				
				

				Test globalView = new Test(dialog.seurat,
						"Clustering", Exps,Gens);
				
				globalView.setLocation(350, 0);
                
				dialog.setVisible(false);
			}
		});

		this.setVisible(true);
	}

	public Vector<Vector<ISelectable>> calculateClustersZeilen(int n, String method, String distance) {
		
		int[] geneClusters = new int[Genes.size()];
		int max = 1;
		
		int [] clusterArray = null;
		
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();
			
			//Vector<Variable> Experiments = dataManager.getExperiments();
			
			rConnection.voidEval("library(stats)");
			rConnection.assign("tempData", Experiments
					.elementAt(0).getColumn(Genes));

			for (int i = 1; i < Experiments.size(); i++) {
				rConnection.assign("x",
			Experiments.elementAt(i).getColumn(Genes));
				rConnection.voidEval("tempData <- cbind(tempData, x)");

			}
			
			if (!method.equals("kmeans")) {

			rConnection.voidEval("h <- hclust(dist(tempData, method = '"
					+ distance + "', p = " + 2 + " ), method = '" + method
					+ "', members=NULL)"); 
			
		
			rConnection.voidEval("c <- cutree(h,k = " + n + ")");
			
			
			}
			else {
				  rConnection.voidEval("cl <- kmeans(tempData,"+n+")");
				  rConnection.voidEval("c <- cl$cluster");
			}
			
			
            clusterArray = new int [Genes.size()];
			
			
				for (int j = 0; j < Genes.size(); j++)
					clusterArray [j] = rConnection.eval(
							"c [[" + (j + 1) + "]]").asInteger();
				
				
			
				for (int j = 0; j < Genes.size(); j++)
				if (max < clusterArray [j]) max = clusterArray [j];
				

	
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}

		
		
		Vector<Vector<ISelectable>> result = new Vector();
		
		for (int i = 0; i <= max; i++) {
			
			result.add(new Vector());
		}
		
		for (int i = 0; i < Genes.size(); i++) {
			result.elementAt(clusterArray [i]).add(Genes.elementAt(i));
		}
		
		
		result.remove(0);
		
		return result;
		

	}

	public Vector<Vector<ISelectable>> calculateClustersSpalten(int n, String method, String distance) {

	int max = 1;
		
		int [] clusterArray = null;
		int spaltenCount = Experiments.size();

		

		boolean weiter = true;

		try {

			
			
			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());
			
			
			RConnection rConnection = dataManager.getRConnection();
		
			rConnection.assign("tempData", Genes.elementAt(0).getRow(Experiments));

			for (int i = 1; i < Genes.size(); i++) {
				rConnection.assign("x", Genes.elementAt(i).getRow(Experiments));
				rConnection.voidEval("tempData <- cbind(tempData, x)");
			}

			
			
			if (!method.equals("kmeans")) {

				
				
			rConnection.voidEval("h <- hclust(dist(tempData, method = '"
					+ distance + "', p = " + 2 + " ), method = '" + method
					+ "', members=NULL)");

			
			
			
			
			
			
			
			rConnection.voidEval("c <- cutree(h,k = " + n + ")");
           
			
			
		}
		else {
			  rConnection.voidEval("cl <- kmeans(tempData,"+n+")");
			  rConnection.voidEval("c <- cl$cluster");
		}
			
			
			clusterArray = new int [Experiments.size()];
			
			
				for (int j = 0; j < Experiments.size(); j++)
					clusterArray [j] = rConnection.eval(
							"c [[" + (j + 1) + "]]").asInteger();
				
				
			
				for (int j = 0; j < Experiments.size(); j++)
				if (max < clusterArray [j]) max = clusterArray [j];
				
				
				
				
				Vector<Vector<ISelectable>> result = new Vector();
				
				for (int i = 0; i <= max; i++) {
					
					result.add(new Vector());
				}
				
				for (int i = 0; i < Experiments.size(); i++) {
					result.elementAt(clusterArray [i]).add(Experiments.elementAt(i));
				}
				
				
				result.remove(0);
				return result;
				
				
				
				
			
			} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}
         return null;
	}
			

		

}
