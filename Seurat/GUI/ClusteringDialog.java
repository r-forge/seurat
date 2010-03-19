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

public class ClusteringDialog extends JFrame {

	JTextField fieldC = new JTextField(5);
	JTextField fieldR = new JTextField(5);
	
	
	
	JButton okBtn1 = new JButton("Ok");
	JButton okBtn2 = new JButton("Ok");
	
	
	/*"euclidean", "maximum", "manhattan", "canberra" "binary" "pearson", "correlation", "spearman" or "kendall"
	 * */
	
	

	String[] ClusteringMethods1 = { "ward", "single", "complete", "average",
			"mcquitty","none"};
	
	String[] ClusteringMethods2 = { "ward", "single", "complete", "average",
			"mcquitty", "kmeans","none"};

	String[] Distance = { "euclidean", "maximum", "manhattan", "canberra",
			"binary", "pearson",  "spearman" , "kendall"};

	JComboBox boxColumns1 = new JComboBox(ClusteringMethods1);

	JComboBox boxDColumns1 = new JComboBox(Distance);

	JComboBox boxRows1 = new JComboBox(ClusteringMethods1);

	JComboBox boxDRows1 = new JComboBox(Distance);
	
	
	
	JComboBox boxColumns2 = new JComboBox(ClusteringMethods2);

	JComboBox boxDColumns2 = new JComboBox(Distance);

	JComboBox boxRows2 = new JComboBox(ClusteringMethods2);

	JComboBox boxDRows2 = new JComboBox(Distance);
	
	

//	String[] SeriationControl = {};

	//String[] SeriationDistance = {};

	Seurat seurat;

	ClusteringDialog dialog = this;

	int[] orderZeilen;

	int[] orderSpalten;

	ClusterNode nodeZeilen;

	ClusterNode nodeSpalten;
	
	
	Vector<ISelectable> Columns;
	
	Vector<ISelectable> Rows;
	
	int pixelW = 1, pixelH = 1, aggregation;
	
	
	DataManager dataManager;
	
	
	
	public ClusteringDialog(Seurat seurat,Vector Rows, Vector Columns, int pixelW, int pixelH, int aggr) {
		super("Clustering");
		
		System.out.println(">Clsutering Dialog  Rows: " + Rows.size() + " Columns: "+Columns.size());
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.setBounds(100, 270, 380, 240);
        this.pixelW = pixelW;
        this.pixelH = pixelH;
        this.aggregation = aggr;
        
			
		this.Rows = Rows;
		
		this.Columns = Columns;
		
		JTabbedPane tPanel = new JTabbedPane();
		
		
		this.getContentPane().setLayout(new BorderLayout());
		
		
		
		
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3,3));

		panel.add(new JLabel("   "));
        panel.add(new JLabel("Columns:"));
        panel.add(new JLabel("Rows:"));

				
		panel.add(new JLabel("Method:       "));
	
		JPanel panel2 = new JPanel();
		panel2.add(boxColumns1);
		panel.add(panel2);
		
		panel2 = new JPanel();
		panel2.add(boxRows1);
		panel.add(panel2);

		
		
		panel.add(new JLabel("Distance:    "));
		
		panel2 = new JPanel();
		panel2.add(boxDColumns1);
		panel.add(panel2);
		
		
		panel2 = new JPanel();
		panel2.add(boxDRows1);
		panel.add(panel2);

	

	//	panel.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(panel,BorderLayout.CENTER);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
	    panel.add(okBtn1, BorderLayout.EAST);
	    
	    p.add(panel,BorderLayout.SOUTH);
	    
	    this.setResizable(false);
		
		okBtn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String methodColumns = boxColumns1.getSelectedItem().toString();
				String DistanceColumns = boxDColumns1.getSelectedItem()
						.toString();

				String methodRows = boxRows1.getSelectedItem().toString();
				String DistanceRows = boxDRows1.getSelectedItem().toString();
				
				ClusteringManager cManager = dialog.seurat.getClusteringManager();
				
					
					
					if (!methodRows.equals("none")) {
						calculateClustersZeilen(methodRows, DistanceRows);
						
						nodeZeilen.name = " Clustering" +  "(\""+methodRows+","+DistanceRows+"\")";
						nodeZeilen.isRows = true;
						cManager.addRowsClustering(nodeZeilen);
					}
					else {
                        nodeZeilen = new ClusterNode(dialog.Rows);
                        nodeZeilen.getFirstOrder();
                        nodeZeilen.Cases = dialog.Rows;
						nodeZeilen.name = " Clustering: none";
						nodeZeilen.isRows = true;

					}
					
					
					if (!methodColumns.equals("none")) {
						calculateClustersSpalten(methodColumns, DistanceColumns);
						nodeSpalten.name = " Clustering"  + "(\""+methodColumns+","+DistanceColumns+"\")";
						nodeSpalten.isRows = false;
						cManager.addColumnsClustering(nodeSpalten);
						
					}
					else {
                        nodeSpalten = new ClusterNode(dialog.Columns);
                        nodeSpalten.getFirstOrder();
                        nodeSpalten.Cases = dialog.Columns;
						nodeSpalten.name = " Clustering: none";
						nodeSpalten.isRows = false;

					}
					
					
					
					
					
					GlobalView globalView = new GlobalView(dialog.seurat,
							"Clustering" + dataManager.ClusteringNumber, nodeSpalten,nodeZeilen);

					if (methodColumns.equals("none")) globalView.gPanel.abstandOben = 2;
					if (methodRows.equals("none")) globalView.gPanel.abstandLinks = 2;
					
					
					dataManager.ClusteringNumber++;
					
					globalView.applyNewPixelSize(dialog.pixelW,dialog.pixelH);
		            if (aggregation > 0) globalView.gPanel.setAggregation(aggregation);


					globalView.setLocation(350, 0);
	                
					dialog.setVisible(false);
					return;
				
				
			}
		});

		
		tPanel.addTab("Hierarchical clustering", p);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		panel = new JPanel();
		panel.setLayout(new GridLayout(4,3));

		panel.add(new JLabel("   "));

		panel.add(new JLabel("Columns:"));

		panel.add(new JLabel("Rows:"));

		
		
		
		
		panel.add(new JLabel("Count:"));

		panel.add(fieldC);

		panel.add(fieldR);
		
		
		panel.add(new JLabel("Method:       "));
		
		
		
		panel2 = new JPanel();
		panel2.add(boxColumns2);
		panel.add(panel2);
		
		
		panel2 = new JPanel();
		panel2.add(boxRows2);
		panel.add(panel2);

		panel.add(new JLabel("Distance:    "));
		
		panel2 = new JPanel();
		panel2.add(boxDColumns2);
		panel.add(panel2);
		
		
		panel2 = new JPanel();
		panel2.add(boxDRows2);
		panel.add(panel2);

	

	//	panel.setBorder(BorderFactory.createEtchedBorder());
		
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(panel,BorderLayout.CENTER);
		
		
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
	    panel.add(okBtn2, BorderLayout.EAST);
	    
	    p.add(panel,BorderLayout.SOUTH);
	    
	    this.setResizable(false);
		
		
		
		okBtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String methodColumns = boxColumns2.getSelectedItem().toString();
				String DistanceColumns = boxDColumns2.getSelectedItem()
						.toString();

				String methodRows = boxRows2.getSelectedItem().toString();
				String DistanceRows = boxDRows2.getSelectedItem().toString();
				
				
				int nR = 0;
				int nC = 0;
				
				
				if (!fieldR.getText().equals("")) nR= Integer.parseInt(fieldR.getText());
				
				if (!fieldC.getText().equals("")) nC= Integer.parseInt(fieldC.getText());
					
				if (methodRows.equals("none")) {
					nR = 1; 
					
				}
				if (methodColumns.equals("none")) {
					nC = 1; 
					
				}
				
				
				
				
			 long time = System.currentTimeMillis();
				
				Vector<Vector<ISelectable>> Gens = new Vector();
				if (nR != 0) {
					if (!methodRows.equals("none")) Gens = calculateClustersZeilen(nR, methodRows, DistanceRows);
					else Gens = calculateClustersZeilen(nR, "ward", DistanceRows);
				}
				else {
					
					Gens.add(dialog.Rows);
				}
				
				
				Vector<Vector<ISelectable>> Exps = new Vector();
				if (nC != 0) {
					if (!methodColumns.equals("none")) Exps = calculateClustersSpalten(nC, methodColumns, DistanceColumns);
					else Exps = calculateClustersSpalten(nC, "ward", DistanceColumns);
				}
				else {
					
					Exps.add(dialog.Columns);
				}
				
				System.out.println((System.currentTimeMillis() - time)/1000);
				
					
				Vector<String> GeneNames = new Vector();
				for (int i = 0; i < Gens.size(); i++) {
					GeneNames.add(""+i);
				}
				
				
				
				Vector<String> ExpsNames = new Vector();
				for (int i = 0; i < Gens.size(); i++) {
					ExpsNames.add(""+i);
				}
				
				Clustering cR = new Clustering(" Clustering"+dataManager.ClusteringNumber+"(\""+methodRows + " , " + DistanceRows + " , "+nR + "\")", Gens,GeneNames, true);
				Clustering cC = new Clustering(" Clustering"+dataManager.ClusteringNumber+"(\""+methodColumns + " , " + DistanceColumns + " , "+nC+ "\")", Exps,ExpsNames, false);
								dialog.seurat.dataManager.GeneClusters.insertElementAt(cR,0);
				dialog.seurat.dataManager.ExpClusters.insertElementAt(cC,0);
				
				
				ClusteringManager cManager = dialog.seurat.getClusteringManager();
				cManager.addRowsClustering(cR);
				cManager.addColumnsClustering(cC);
				
				dialog.seurat.repaint();
				
				

				KMeansView globalView = new KMeansView(dialog.seurat,
						"Clustering"+ dataManager.ClusteringNumber, cC,cR);
				
				
				dataManager.ClusteringNumber++;
				

				
				int count = 0;
				for (int i = 0; i < Gens.size(); i++) {
					count+=Gens.elementAt(i).size();
				}
				
				int aggr = 1;
				while (count*dialog.seurat.settings.PixelH/aggr > 700) aggr++;
				 	
				globalView.panel.Aggregation = aggr;
			
				globalView.setLocation(350, 0);
				globalView.panel.Model = dialog.seurat.settings.Model;
				count = 0;
				for (int i = 0; i < Exps.size(); i++) {
					count+=Exps.elementAt(i).size();
				}
				
				
 
				
				
				globalView.applyNewPixelSize(dialog.pixelW,dialog.pixelH);
	            if (aggregation > 0) globalView.panel.setAggregation(aggregation);

						//dialog.seurat.settings.PixelW,dialog.seurat.settings.PixelH);
                
				dialog.setVisible(false);
			}
		});
		
		
		tPanel.addTab("Partitioning", p);
		
		
		
		this.getContentPane().add(tPanel,BorderLayout.CENTER);
		

		

		this.setVisible(true);
	}
	
	
	
	
	
	
	
/*
	public ClusteringDialog(Seurat seurat,Vector Rows, Vector Columns, int pixelW, int pixelH, int aggr) {
		super("Clustering");
		
		System.out.println(">Clsutering Dialog  Rows: " + Rows.size() + " Columns: "+Columns.size());
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.setBounds(100, 270, 350, 190);
        this.pixelW = pixelW;
        this.pixelH = pixelH;
        this.aggregation = aggr;
        
			
		this.Rows = Rows;
		
		this.Columns = Columns;
		
		
		
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
					calculateClustersSpalten(methodColumns, DistanceColumns);
					
				
					nodeSpalten.name = " Clustering" + dataManager.ClusteringNumber + "(\""+methodColumns+","+DistanceColumns+"\")";
					nodeZeilen.name = " Clustering" + dataManager.ClusteringNumber + "(\""+methodRows+","+DistanceRows+"\")";
					nodeSpalten.isRows = false;
					nodeZeilen.isRows = true;
					
					ClusteringManager cManager = dialog.seurat.getClusteringManager();
					cManager.addRowsClustering(nodeZeilen);
					cManager.addColumnsClustering(nodeSpalten);
					
					
					GlobalView globalView = new GlobalView(dialog.seurat,
							"Clustering" + dataManager.ClusteringNumber, nodeSpalten,nodeZeilen);

					dataManager.ClusteringNumber++;
					
					globalView.applyNewPixelSize(dialog.pixelW,dialog.pixelH);
		            if (aggregation > 0) globalView.gPanel.setAggregation(aggregation);


					globalView.setLocation(350, 0);
	                
					dialog.setVisible(false);
					return;
				}
				
				
				
				
				
				
				
			 long time = System.currentTimeMillis();
				
				Vector<Vector<ISelectable>> Gens = new Vector();
				if (nR != 0) {
					Gens = calculateClustersZeilen(nR, methodRows, DistanceRows);
				}
				else {
					
					Gens.add(dialog.Rows);
				}
				
				
				Vector<Vector<ISelectable>> Exps = new Vector();
				if (nC != 0) {
					Exps = calculateClustersSpalten(nC, methodColumns, DistanceColumns);
				}
				else {
					
					Exps.add(dialog.Columns);
				}
				
				System.out.println((System.currentTimeMillis() - time)/1000);
				
					
				Vector<String> GeneNames = new Vector();
				for (int i = 0; i < Gens.size(); i++) {
					GeneNames.add(""+i);
				}
				
				
				
				Vector<String> ExpsNames = new Vector();
				for (int i = 0; i < Gens.size(); i++) {
					ExpsNames.add(""+i);
				}
				
				Clustering cR = new Clustering(" Clustering"+dataManager.ClusteringNumber+"(\""+methodRows + " , " + DistanceRows + " , "+nR + "\")", Gens,GeneNames, true);
				Clustering cC = new Clustering(" Clustering"+dataManager.ClusteringNumber+"(\""+methodColumns + " , " + DistanceColumns + " , "+nC+ "\")", Exps,ExpsNames, false);
								dialog.seurat.dataManager.GeneClusters.insertElementAt(cR,0);
				dialog.seurat.dataManager.ExpClusters.insertElementAt(cC,0);
				
				
				ClusteringManager cManager = dialog.seurat.getClusteringManager();
				cManager.addRowsClustering(cR);
				cManager.addColumnsClustering(cC);
				
				dialog.seurat.repaint();
				
				

				KMeansView globalView = new KMeansView(dialog.seurat,
						"Clustering"+ dataManager.ClusteringNumber, cC,cR);
				
				
				dataManager.ClusteringNumber++;
				

				
				int count = 0;
				for (int i = 0; i < Gens.size(); i++) {
					count+=Gens.elementAt(i).size();
				}
				
				int aggr = 1;
				while (count*dialog.seurat.settings.PixelH/aggr > 700) aggr++;
				 	
				globalView.panel.Aggregation = aggr;
			
				globalView.setLocation(350, 0);
				globalView.panel.Model = dialog.seurat.settings.Model;
				count = 0;
				for (int i = 0; i < Exps.size(); i++) {
					count+=Exps.elementAt(i).size();
				}
				
				
 
				
				
				globalView.applyNewPixelSize(dialog.pixelW,dialog.pixelH);
	            if (aggregation > 0) globalView.panel.setAggregation(aggregation);

						//dialog.seurat.settings.PixelW,dialog.seurat.settings.PixelH);
                
				dialog.setVisible(false);
			}
		});

		this.setVisible(true);
	}
	
	*/
	
	

	public Vector<Vector<ISelectable>> calculateClustersZeilen(int n, String method, String distance) {
		
		int[] geneClusters = new int[Rows.size()];
		int max = 1;
		
		int [] clusterArray = null;
		
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();
			
		
			
			
			
			//Vector<Variable> Experiments = dataManager.getExperiments();
			
			rConnection.voidEval("require(stats)");
			rConnection.voidEval("require(amap)");
			rConnection.assign("tempData", Columns
					.elementAt(0).getColumn(Rows));

			for (int i = 1; i < Columns.size(); i++) {
				rConnection.assign("x",
			Columns.elementAt(i).getColumn(Rows));
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
			
			
            clusterArray = new int [Rows.size()];
			
			
				for (int j = 0; j < Rows.size(); j++)
					clusterArray [j] = rConnection.eval(
							"c [[" + (j + 1) + "]]").asInteger();
				
				
			
				for (int j = 0; j < Rows.size(); j++)
				if (max < clusterArray [j]) max = clusterArray [j];
				

				rConnection.voidEval("rm(list=ls())");
				rConnection.voidEval("gc()"); 
	
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}

		
		
		Vector<Vector<ISelectable>> result = new Vector();
		
		for (int i = 0; i <= max; i++) {
			
			result.add(new Vector());
		}
		
		for (int i = 0; i < Rows.size(); i++) {
			result.elementAt(clusterArray [i]).add(Rows.elementAt(i));
		}
		
		
		result.remove(0);
		
		return result;
		

	}
	
	
	
	
	
	

	public Vector<Vector<ISelectable>> calculateClustersSpalten(int n, String method, String distance) {

	int max = 1;
		
		int [] clusterArray = null;
		int spaltenCount = Columns.size();

	

		boolean weiter = true;

		try {

			
			
			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());
			
			
			RConnection rConnection = dataManager.getRConnection();
		
			rConnection.assign("tempData", Rows.elementAt(0).getRow(Columns));

			for (int i = 1; i < Rows.size(); i++) {
				rConnection.assign("x", Rows.elementAt(i).getRow(Columns));
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
			
			
			clusterArray = new int [Columns.size()];
			
			
				for (int j = 0; j < Columns.size(); j++)
					clusterArray [j] = rConnection.eval(
							"c [[" + (j + 1) + "]]").asInteger();
				
				
			
				for (int j = 0; j < Columns.size(); j++)
				if (max < clusterArray [j]) max = clusterArray [j];
				
				
				
				
				Vector<Vector<ISelectable>> result = new Vector();
				
				for (int i = 0; i <= max; i++) {
					
					result.add(new Vector());
				}
				
				for (int i = 0; i < Columns.size(); i++) {
					result.elementAt(clusterArray [i]).add(Columns.elementAt(i));
				}
				
				
				result.remove(0);
				
				rConnection.voidEval("rm(list=ls())");
				rConnection.voidEval("gc()");
				
				return result;
				
				
				
				
			
			} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}
         return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	public void calculateClustersZeilen(String method, String distance) {
		int[][] clusterArray = new int[1][1];
		int[] geneClusters = new int[Rows.size()];
		double [] height = null;
		
		
		System.out.println(method + "  " + distance);
		
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();
			
			
	//		rConnection.voidEval("require(amap)");

			rConnection.voidEval("require(stats)");
			rConnection.voidEval("require(amap)");
			
			rConnection.assign("tempData", Columns.elementAt(0).getColumn(Rows));

			for (int i = 1; i < Columns.size(); i++) {
				rConnection.assign("x",
			(Columns.elementAt(i)).getColumn(Rows));
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

			clusterArray = new int[2][Rows.size()];

			

			
			rConnection.voidEval("hm<-h$merge");
			
			
			 int [] []clust = new int [Rows.size()-1][2];
             for (int i = 0; i < Rows.size()-1; i++) {
            	
            	// System.out.print(rConnection.eval("hm [" +  (i+1)+ ","+1+"]").asInteger()+"   ");
            	// System.out.println(rConnection.eval("hm [" +  (i+1)+ ","+2+"]").asInteger()+"   ");
            	 clust [i][0] = rConnection.eval("hm [" +  (i+1)+ ","+1+"]").asInteger();
            	 clust [i][1] = rConnection.eval("hm [" +  (i+1)+ ","+2+"]").asInteger();
            	 
            	// System.out.println(i);
            	 
             }
             
             
             
             Vector<ClusterNode> Nodes = new Vector();
             for (int i = 0; i < Rows.size(); i++) {
            	 Vector<ISelectable> cases = new Vector();
            	 cases.add(Rows.elementAt(i));
            	 ClusterNode nd = new ClusterNode(cases);
            	 nd.ClusterNumber = -(i+1);
            	 
            	 Nodes.add(nd);
             }
             
             
             
             for (int i = 0; i < Rows.size()-1; i++) {
            	int c1 = clust [i][0]; 
            	int c2 = clust [i][1];
                Nodes = union(Nodes,c1,c2,i+1, height [i]);	
         //   	System.out.println(i);
             }
             
             
             nodeZeilen = Nodes.firstElement();
             
             
             
			
			/*
			
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
			
			
			*/
			
			
             
             
         	rConnection.voidEval("rm(list=ls())");
			rConnection.voidEval("gc()");

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}
		
		
		

		
		System.out.println(nodeZeilen);
		nodeZeilen.calculateHeight(height [height.length-1]);
		System.out.println("Clustering Rows calculated...");

	}

	
	
	public Vector<ClusterNode> union(Vector<ClusterNode> Nodes, int c1, int c2, int newNumbr, double height) {
		Vector<ClusterNode> newNodes = new Vector();
		ClusterNode c1Node= null;
		ClusterNode c2Node = null;
		
		for (int i = 0;i < Nodes.size(); i++) {
			ClusterNode node = Nodes.elementAt(i);
			if (node.ClusterNumber != c1 && node.ClusterNumber != c2) {
				newNodes.add(node);
			}
			if (node.ClusterNumber == c1) {
				c1Node = node;
			}
			if (node.ClusterNumber == c2) {
				c2Node = node;
			}
			
			
			
		}
		
		
		if (c1Node == null || c2Node == null)  {
			System.out.println("Node not found   " + c1 + "   " + c2);
			if (c1Node == null) System.out.println("C1: " + c1);
			if (c2Node == null) System.out.println("C2: " + c2);
		}
		
		
		ClusterNode newNode = new ClusterNode(newNumbr,c1Node,c2Node);
		newNode.currentHeight = height;
		newNodes.add(newNode);
	
		
		
		return newNodes;
		
	}
	
	
	
	
	
	public void calculateClustersSpalten(String method, String distance) {

		int[][] clusterArray = new int[1][1];
		int spaltenCount = Columns.size();

		int[] geneClusters = new int[spaltenCount];
		double [] height = null;
		boolean weiter = true;
		

		try {

			
			RConnection rConnection = dataManager.getRConnection();
			
			rConnection.assign("tempData", Rows.elementAt(0).getRow(Columns));

			for (int i = 1; i < Rows.size(); i++) {
				rConnection.assign("x", Rows.elementAt(i).getRow(Columns));
				rConnection.voidEval("tempData <- cbind(tempData, x)");
			}

			//rConnection.voidEval("h <- hclust(dist(tempData, method = '"
				//+ distance + "', p = " + 2 + " ), method = '" + method
					//+ "', members=NULL)");
			
			rConnection.voidEval("h <- hcluster(tempData, method = '"
					+ distance + "', link = '" + method + "',diag=FALSE," +
					"upper=FALSE, doubleprecision = TRUE, nbproc=2, members=NULL)"); 
			
			  height = rConnection.eval("h$height").asDoubles();
			  
		 
			  
			  
			  rConnection.voidEval("hm<-h$merge");
				
				
				 int [] []clust = new int [Columns.size()-1][2];
	             for (int i = 0; i < Columns.size()-1; i++) {
	            	
	            //	 System.out.print(rConnection.eval("hm [" +  (i+1)+ ","+1+"]").asInteger()+"   ");
	            	// System.out.println(rConnection.eval("hm [" +  (i+1)+ ","+2+"]").asInteger()+"   ");
	            	 clust [i][0] = rConnection.eval("hm [" +  (i+1)+ ","+1+"]").asInteger();
	            	 clust [i][1] = rConnection.eval("hm [" +  (i+1)+ ","+2+"]").asInteger();
	            	 
	            //	 System.out.println(i);
	            	 
	             }
		
		
		 
        Vector<ClusterNode> Nodes = new Vector();
        for (int i = 0; i < Columns.size(); i++) {
       	 Vector<ISelectable> cases = new Vector();
       	 cases.add(Columns.elementAt(i));
       	 ClusterNode nd = new ClusterNode(cases);
       	 nd.ClusterNumber = -(i+1);
       	 Nodes.add(nd);
        }
        
        
        
        
        for (int i = 0; i < Columns.size()-1; i++) {
       	int c1 = clust [i][0]; 
       	int c2 = clust [i][1];
        Nodes = union(Nodes,c1,c2,i+1, height [i]);	
        }
        
        
        nodeSpalten = Nodes.firstElement();	
		nodeSpalten.calculateHeight(height [height.length-1]);
		
		
		rConnection.voidEval("rm(list=ls())");
		rConnection.voidEval("gc()");
		
		

	} catch (Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Calculation failed.");
	}
	
	
	
	
	
	
	}
	
	
	
	
	
	
	
/**
 * 
 * 
  
 
	
	

	public Vector<Vector<ISelectable>> calculateClustersZeilen(int n, String method, String distance) {
		
		int[] geneClusters = new int[Genes.size()];
		int max = 1;
		
		int [] clusterArray = null;
		
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();
			
		
			
			
			
			//Vector<Variable> Experiments = dataManager.getExperiments();
			
			rConnection.voidEval("require(stats)");
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
			
			
			
			rConnection.voidEval("require(stats)");
			rConnection.voidEval("require(amap)");
			
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

 
 
 * 
 * */
	
	
	
	
	
	
	
	

}
