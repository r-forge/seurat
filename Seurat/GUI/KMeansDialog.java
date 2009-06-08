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
import Data.Gene;
import Data.Variable;


import java.util.zip.*;
import java.util.jar.*;

import Data.*;

public class KMeansDialog extends JFrame {

	JTextField fieldC = new JTextField(5);
	JTextField fieldR = new JTextField(5);
	
	
	
	JButton okBtn = new JButton("Ok");
	
	
	/*"euclidean", "maximum", "manhattan", "canberra" "binary" "pearson", "correlation", "spearman" or "kendall"
	 * */
	

	String[] ClusteringMethods = { "ward", "single", "complete", "average",
			"mcquitty", "kmeans"};

	String[] Distance = { "euclidean", "maximum", "manhattan", "canberra",
			"binary", "pearson",  "spearman" , "kendall"};

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
				
				
				int nR = 0;
				int nC = 0;
				
				
				if (!fieldR.getText().equals("")) nR= Integer.parseInt(fieldR.getText());
				
				if (!fieldC.getText().equals("")) nC= Integer.parseInt(fieldC.getText());
				
				
				if (nR == 0 && nC == 0) {
					
					
					calculateClustersZeilen(methodRows, DistanceRows);
					
					
					Vector<Gene> Genes1 = new Vector();
					int[] order = new int[orderZeilen.length];
					for (int i = 0; i < orderZeilen.length; i++) {
						order[orderZeilen[i]] = i;
					}

					for (int i = 0; i < order.length; i++) {
						Genes1.add((Gene)dialog.Genes.elementAt(order[i]));
					}
					
					
					
					
					calculateClustersSpalten(methodColumns, DistanceColumns);
					
				
					
					
					
					Vector<Variable> Experiments1 = new Vector();
					order = new int[orderSpalten.length];
					for (int i = 0; i < orderSpalten.length; i++) {
						order[orderSpalten[i]] = i;
					}

					for (int i = 0; i < order.length; i++) {
						Experiments1.add((Variable)dialog.Experiments.elementAt(order[i]));
					}
					
					
				
					GlobalViewAbstract globalView = new GlobalViewAbstract(dialog.seurat,
							"Clustering", Experiments1,Genes1, true);

					//GlobalView globalView = new GlobalView(dialog.seurat,
				//			"Clustering", Experiments1,Genes1, true);
					globalView.gPanel.nodeSpalten = nodeSpalten;
					globalView.gPanel.nodeZeilen = nodeZeilen;
					
					
					
					globalView.applyNewPixelSize(globalView.pixelSize);

					globalView.setLocation(350, 0);
	                
					dialog.setVisible(false);
					return;
				}
				
				
				
				
				
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
				
				
			 long time = System.currentTimeMillis();
				
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
				
				System.out.println((System.currentTimeMillis() - time)/1000);
				
					
				dialog.seurat.dataManager.GeneClusters.add(new Clustering(methodRows + " , " + DistanceRows + " , "+nR, Gens));
				dialog.seurat.dataManager.ExpClusters.add(new Clustering(methodColumns + " , " + DistanceColumns + " , "+nC, Exps));
				
				

				KMeansView globalView = new KMeansView(dialog.seurat,
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
			rConnection.voidEval("require(amap)");
			rConnection.assign("tempData", Experiments
					.elementAt(0).getColumn(Genes));

			for (int i = 1; i < Experiments.size(); i++) {
				rConnection.assign("x",
			Experiments.elementAt(i).getColumn(Genes));
				rConnection.voidEval("tempData <- cbind(tempData, x)");

			}
			
			if (!method.equals("kmeans")) {

		//	rConnection.voidEval("h <- hclust(dist(tempData, method = '"
		//			+ distance + "', p = " + 2 + " ), method = '" + method
		//			+ "', members=NULL)"); 
				rConnection.voidEval("h <- hcluster(tempData, method = '"
						+ distance + "', link = '" + method + "',diag=FALSE," +
						"upper=FALSE, doubleprecision = TRUE, nbproc=2, members=NULL)"); 
		
			rConnection.voidEval("c <- cutree(h,k = " + n + ")");
			
			
			}
			else {
				  rConnection.voidEval("cl <- Kmeans(tempData,"+n+" ,method = '"
						+ distance + "'     )");
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

				
				
		//	rConnection.voidEval("h <- hclust(dist(tempData, method = '"
		//			+ distance + "', p = " + 2 + " ), method = '" + method
		//			+ "', members=NULL)");

			

					
			rConnection.voidEval("h <- hcluster(tempData, method = '"
			+ distance + "', link = '" + method + "',diag=FALSE," +
			"upper=FALSE, doubleprecision = TRUE, nbproc=2, members=NULL)");
			
			
			
			
			
			rConnection.voidEval("c <- cutree(h,k = " + n + ")");
           
			
			
		}
		else {
			rConnection.voidEval("cl <- Kmeans(tempData,"+n+" ,method = '"
					+ distance + "'     )");
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	public void calculateClustersZeilen(String method, String distance) {
		int[][] clusterArray = new int[1][1];
		int[] geneClusters = new int[Genes.size()];
		double [] height = null;
		
		
		System.out.println(method + "  " + distance);
		
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();
			
			
			
			rConnection.voidEval("library(stats)");
			rConnection.voidEval("library(amap)");
			
			rConnection.assign("tempData", ((Variable)Experiments.elementAt(0)).getColumn());

			for (int i = 1; i < Experiments.size(); i++) {
				rConnection.assign("x",
			((Variable)Experiments.elementAt(i)).getColumn());
				rConnection.voidEval("tempData <- cbind(tempData, x)");

			}
			
			
	//		rConnection.voidEval("h <- hclust(dist(tempData, method = '"
	//				+ distance + "', p = " + 2 + " ), method = '" + method
	//				+ "', members=NULL)"); 
			
			rConnection.voidEval("h <- hcluster(tempData, method = '"
					+ distance + "', link = '" + method + "',diag=FALSE," +
					"upper=FALSE, doubleprecision = TRUE, nbproc=2, members=NULL)"); 
			
		
		    height = rConnection.eval("h$height").asDoubles();
		    
		   // for (int i = 0; i < height.length; i++) System.out.println(height [i]);

			clusterArray = new int[2][Genes.size()];

			nodeZeilen = new ClusterNode(dataManager, false, Experiments, Genes);
			nodeZeilen.cases = new Vector();
			for (int i = 0; i < Genes.size(); i++) {
				nodeZeilen.cases.add(new Integer(i));
			}

			for (int count = 1; count < Genes.size() - 1; count++) {

				rConnection.voidEval("c <- cutree(h,k = " + count + ")");

				System.out.println(count);
				
				for (int j = 0; j < Genes.size(); j++)  clusterArray[0][j] = rConnection.eval("c [[" + (j + 1) + "]]").asInteger();

				rConnection.voidEval("c <- cutree(h,k = " + (count + 1)+ ")");

				for (int j = 0; j < Genes.size(); j++) clusterArray[1][j] = rConnection.eval("c [[" + (j + 1) + "]]").asInteger();

				int k = 0;
				int geteilterNode = 0;
				int newNode = 0;

				for (int j = 0; j < Genes.size(); j++) {
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

				for (int j = 0; j < Genes.size(); j++) {
					if (clusterArray[0][j] == geteilterNode) {
						if (clusterArray[1][j] == geteilterNode)
							nodeR.add(j);
						else
							nodeL.add(j);
					}
				}
				
				oldNode.nodeL = new ClusterNode(nodeL, dataManager, false, Experiments, Genes);
				oldNode.nodeR = new ClusterNode(nodeR, dataManager, false, Experiments, Genes);
                oldNode.currentHeight = height [height.length-count];
				
				
			}
			
			
		
			

			int count = Genes.size() - 1;

			rConnection.voidEval("c <- cutree(h,k = " + count + ")");

			for (int j = 0; j < Genes.size(); j++)
				clusterArray[0][j] = rConnection.eval(
						"c [[" + (j + 1) + "]]").asInteger();

			for (int j = 0; j < Genes.size(); j++)
				clusterArray[1][j] = j + 1;

			int k = 0;
			int geteilterNode = 0;
			int newNode = 0;

			for (int j = 0; j < Genes.size(); j++) {
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

			for (int j = 0; j <  Genes.size(); j++) {
				if (clusterArray[0][j] == geteilterNode) {
					if (clusterArray[1][j] == geteilterNode)
						nodeR.add(j);
					else
						nodeL.add(j);
				}
			}
			oldNode.nodeL = new ClusterNode(nodeL, dataManager, false, Experiments, Genes);
			oldNode.nodeR = new ClusterNode(nodeR, dataManager, false, Experiments, Genes);

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
		int spaltenCount = Experiments.size();

		int[] geneClusters = new int[spaltenCount];
		double [] height = null;
		boolean weiter = true;
		

		try {

			
			RConnection rConnection = dataManager.getRConnection();
			
			rConnection.assign("tempData", Genes.elementAt(0).getRow(Experiments));

			for (int i = 1; i < Genes.size(); i++) {
				rConnection.assign("x", Genes.elementAt(i).getRow(Experiments));
				rConnection.voidEval("tempData <- cbind(tempData, x)");
			}

			//rConnection.voidEval("h <- hclust(dist(tempData, method = '"
				//+ distance + "', p = " + 2 + " ), method = '" + method
					//+ "', members=NULL)");
			
			rConnection.voidEval("h <- hcluster(tempData, method = '"
					+ distance + "', link = '" + method + "',diag=FALSE," +
					"upper=FALSE, doubleprecision = TRUE, nbproc=2, members=NULL)"); 
			
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

		nodeSpalten = new ClusterNode(dataManager, true, Experiments, Genes);
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
			oldNode.nodeL = new ClusterNode(nodeL, dataManager, true, Experiments, Genes);
			oldNode.nodeR = new ClusterNode(nodeR, dataManager, true, Experiments, Genes);
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
