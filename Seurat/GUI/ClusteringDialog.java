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

public class ClusteringDialog extends JFrame {

	JTextField field = new JTextField("      ");

	JButton okBtn = new JButton("Ok");

	String[] ClusteringMethods = { "ward", "single", "complete", "average",
			"mcquitty", "median", "centroid"};

	String[] Distance = { "euclidean", "maximum", "manhattan", "canberra",
			"binary", "minkowski" };

	JComboBox boxColumns = new JComboBox(ClusteringMethods);

	JComboBox boxDColumns = new JComboBox(Distance);

	JComboBox boxRows = new JComboBox(ClusteringMethods);

	JComboBox boxDRows = new JComboBox(Distance);

	String[] SeriationControl = {};

	String[] SeriationDistance = {};

	Seurat seurat;

	ClusteringDialog dialog = this;

	int[] orderZeilen;

	int[] orderSpalten;

	ClusterNode nodeZeilen;

	ClusterNode nodeSpalten;
	
	
	DataManager dataManager;

	public ClusteringDialog(Seurat seurat) {
		super("Clustering");
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.setBounds(100, 270, 350, 155);

		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3,3));

		panel.add(new JLabel(""));

		panel.add(new JLabel("Columns:"));

		panel.add(new JLabel("Rows:"));

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
				calculateClustersZeilen(methodRows, DistanceRows);
				calculateClustersSpalten(methodColumns, DistanceColumns);
				
				
				Vector<Gene> Genes = new Vector();
				int[] order = new int[orderZeilen.length];
				for (int i = 0; i < orderZeilen.length; i++) {
					order[orderZeilen[i]] = i;
				}

				for (int i = 0; i < order.length; i++) {
					Genes.add(dataManager.Genes.elementAt(order[i]));
				}
				
				
				
				Vector<Variable> Experiments = new Vector();
				order = new int[orderSpalten.length];
				for (int i = 0; i < orderSpalten.length; i++) {
					order[orderSpalten[i]] = i;
				}

				for (int i = 0; i < order.length; i++) {
					Experiments.add(dataManager.Experiments.elementAt(order[i]));
				}
				
				
				
				

				GlobalViewAbstract globalView = new GlobalViewAbstract(dialog.seurat,
						"Clustering", Experiments,Genes, true);
				globalView.gPanel.nodeSpalten = nodeSpalten;
				globalView.gPanel.nodeZeilen = nodeZeilen;

				globalView.setLocation(350, 0);
                
				dialog.setVisible(false);
			}
		});

		this.setVisible(true);
	}

	public void calculateClustersZeilen(String method, String distance) {
		int[][] clusterArray = new int[1][1];
		int[] geneClusters = new int[dataManager.getRowCount()];
		double [] height = null;
		
		
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();
			
			Vector<Variable> Experiments = dataManager.getExperiments();
			
			rConnection.voidEval("library(stats)");
			rConnection.assign("tempData", dataManager.getExperiments()
					.elementAt(0).getColumn());

			for (int i = 1; i < Experiments.size(); i++) {
				rConnection.assign("x",
			Experiments.elementAt(i).getColumn());
				rConnection.voidEval("tempData <- cbind(tempData, x)");

			}
			
			

			rConnection.voidEval("h <- hclust(dist(tempData, method = '"
					+ distance + "', p = " + 2 + " ), method = '" + method
					+ "', members=NULL)"); 
			
		    height = rConnection.eval("h$height").asDoubles();
		    
		   // for (int i = 0; i < height.length; i++) System.out.println(height [i]);

			clusterArray = new int[2][dataManager.getRowCount()];

			nodeZeilen = new ClusterNode(dataManager, false, dataManager.Experiments, dataManager.Genes);
			nodeZeilen.cases = new Vector();
			for (int i = 0; i < dataManager.getRowCount(); i++) {
				nodeZeilen.cases.add(new Integer(i));
			}

			for (int count = 1; count < dataManager.getRowCount() - 1; count++) {

				rConnection.voidEval("c <- cutree(h,k = " + count + ")");

				for (int j = 0; j < dataManager.getRowCount(); j++)
					clusterArray[0][j] = rConnection.eval(
							"c [[" + (j + 1) + "]]").asInteger();

				rConnection.voidEval("c <- cutree(h,k = " + (count + 1)
						+ ")");

				for (int j = 0; j < dataManager.getRowCount(); j++)

					clusterArray[1][j] = rConnection.eval(
							"c [[" + (j + 1) + "]]").asInteger();

				int k = 0;
				int geteilterNode = 0;
				int newNode = 0;

				for (int j = 0; j < dataManager.getRowCount(); j++) {
					if (clusterArray[0][j] != clusterArray[1][j]) {
						k = j;
						geteilterNode = clusterArray[0][j];
						newNode = clusterArray[1][j];
						break;
					}
				}
				ClusterNode oldNode = nodeZeilen.getClusterNode(k);

				Vector<Integer> nodeR = new Vector();
				Vector<Integer> nodeL = new Vector();

				for (int j = 0; j < dataManager.getRowCount(); j++) {
					if (clusterArray[0][j] == geteilterNode) {
						if (clusterArray[1][j] == geteilterNode)
							nodeR.add(j);
						else
							nodeL.add(j);
					}
				}
				oldNode.nodeL = new ClusterNode(nodeL, dataManager, false, dataManager.Experiments, dataManager.Genes);
				oldNode.nodeR = new ClusterNode(nodeR, dataManager, false, dataManager.Experiments, dataManager.Genes);
                oldNode.currentHeight = height [height.length-count];
				
				
			}

			int count = dataManager.getRowCount() - 1;

			rConnection.voidEval("c <- cutree(h,k = " + count + ")");

			for (int j = 0; j < dataManager.getRowCount(); j++)
				clusterArray[0][j] = rConnection.eval(
						"c [[" + (j + 1) + "]]").asInteger();

			for (int j = 0; j < dataManager.getRowCount(); j++)
				clusterArray[1][j] = j + 1;

			int k = 0;
			int geteilterNode = 0;
			int newNode = 0;

			for (int j = 0; j < dataManager.getRowCount(); j++) {
				if (clusterArray[0][j] != clusterArray[1][j]) {
					k = j;
					geteilterNode = clusterArray[0][j];
					newNode = clusterArray[1][j];
					break;
				}
			}

			ClusterNode oldNode = nodeZeilen.getClusterNode(k);

			Vector<Integer> nodeR = new Vector();
			Vector<Integer> nodeL = new Vector();

			for (int j = 0; j < dataManager.getRowCount(); j++) {
				if (clusterArray[0][j] == geteilterNode) {
					if (clusterArray[1][j] == geteilterNode)
						nodeR.add(j);
					else
						nodeL.add(j);
				}
			}
			oldNode.nodeL = new ClusterNode(nodeL, dataManager, false, dataManager.Experiments, dataManager.Genes);
			oldNode.nodeR = new ClusterNode(nodeR, dataManager, false, dataManager.Experiments, dataManager.Genes);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}
		
		
		
		
		

		
		Vector<Integer> order = nodeZeilen.getOrder();
		orderZeilen = new int[order.size()];
		for (int i = 0; i < order.size(); i++) {
			orderZeilen[order.elementAt(i)] = i;
		}
		
		
		nodeZeilen.calculateHeight(height [height.length-1]);
		

	}

	public void calculateClustersSpalten(String method, String distance) {

		int[][] clusterArray = new int[1][1];
		int spaltenCount = dataManager.getExperiments().size();

		int[] geneClusters = new int[spaltenCount];
		double [] height = null;
		boolean weiter = true;

		try {

			
			RConnection rConnection = dataManager.getRConnection();
			
			rConnection.assign("tempData", dataManager.getRowData(0));

			for (int i = 1; i < dataManager.getRowCount(); i++) {
				rConnection.assign("x", dataManager.getRowData(i));
				rConnection.voidEval("tempData <- cbind(tempData, x)");
			}

			rConnection.voidEval("h <- hclust(dist(tempData, method = '"
					+ distance + "', p = " + 2 + " ), method = '" + method
					+ "', members=NULL)");

			
			  height = rConnection.eval("h$height").asDoubles();
			  
			  
			  
			clusterArray = new int[spaltenCount][spaltenCount];

			for (int count = spaltenCount; count > 0; count--) {

				rConnection.voidEval("c <- cutree(h,k = " + count + ")");

				
				for (int i = 1; i < spaltenCount + 1; i++)
					geneClusters[i - 1] = rConnection.eval(
							"c [[" + i + "]]").asInteger();
				for (int i = 0; i < spaltenCount; i++) {
					clusterArray[i][count - 1] = geneClusters[i];
				}

					}
			} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}

		nodeSpalten = new ClusterNode(dataManager, true, dataManager.Experiments, dataManager.Genes);
		nodeSpalten.cases = new Vector();
		for (int i = 0; i < spaltenCount; i++) {
			nodeSpalten.cases.add(new Integer(i));
		}

		for (int i = 1; i < spaltenCount; i++) {
			int k = 0;
			int geteilterNode = 0;
			int newNode = 0;
			for (int j = 0; j < spaltenCount; j++) {
				if (clusterArray[j][i] != clusterArray[j][i - 1]) {
					k = j;
					geteilterNode = clusterArray[j][i - 1];
					newNode = clusterArray[j][i];
					break;
				}
			}

		
			ClusterNode oldNode = nodeSpalten.getClusterNode(k);
			Vector<Integer> nodeR = new Vector();
			Vector<Integer> nodeL = new Vector();
			for (int j = 0; j < spaltenCount; j++) {
				if (clusterArray[j][i - 1] == geteilterNode) {
					if (clusterArray[j][i] == geteilterNode)
						nodeR.add(j);
					else
						nodeL.add(j);
				}
			}
			oldNode.nodeL = new ClusterNode(nodeL, dataManager, true, dataManager.Experiments, dataManager.Genes);
			oldNode.nodeR = new ClusterNode(nodeR, dataManager, true, dataManager.Experiments, dataManager.Genes);
		     oldNode.currentHeight = height [height.length-i];
	}

		Vector<Integer> order = nodeSpalten.getOrder();
		orderSpalten = new int[order.size()];
		for (int i = 0; i < order.size(); i++) {
			orderSpalten[order.elementAt(i)] = i;
		}
		
		
		nodeSpalten.calculateHeight(height [height.length-1]);

	}

}
