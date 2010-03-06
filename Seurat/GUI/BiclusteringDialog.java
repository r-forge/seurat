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

import Data.Bicluster;
import Data.ClusterNode;
import Data.Clustering;
import Data.DataManager;
import Data.Gene;
import Data.ISelectable;
import Data.Variable;


import java.util.zip.*;
import java.util.jar.*;

import Data.*;


public class BiclusteringDialog extends JFrame{
	
	JTabbedPane tabLeiste = new JTabbedPane();
	
	Vector<ISelectable> Columns;
	
	Vector<ISelectable> Rows;
	
	Seurat seurat;

	BiclusteringDialog bicdialog = this;
	
	int pixelW = 1, pixelH = 1;
	
	DataManager dataManager;
	
	//ISA
	
	//default parameter
	
	//JTextField fieldCthres = new JTextField("1.8",4);
	//JTextField fieldRthres = new JTextField("1.8",4);
	//JTextField fieldminCISA = new JTextField("5",4);
	//JTextField fieldminRISA = new JTextField("5",4);
	JTextField fieldminCBimax = new JTextField("5",2);
	JTextField fieldminRBimax = new JTextField("5",2);
	JTextField fieldnumberBimax = new JTextField("10",2);
	JTextField quantileBimax = new JTextField("0.2",2);
	JTextField fieldRowrel = new JTextField("0.6",2);
	JTextField fieldColrel = new JTextField("0.6",2);
	JTextField fieldShuffle = new JTextField("3",2);
	String[] bimaxopt = {"up","down"};
	JComboBox boxBimaxopt = new JComboBox(bimaxopt);
	//JButton okBtnISA = new JButton("Ok");
		
	JButton okBtnBimax = new JButton("Ok");
	
	JButton okBtnPlaid = new JButton("Ok");
	
	public BiclusteringDialog(Seurat seurat,Vector Rows, Vector Columns, int pixelW, int pixelH){
		super("Biclustering");
		
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
		this.setBounds(100, 320, 450, 245);
        this.pixelW = pixelW;
        this.pixelH = pixelH;
        this.Rows = Rows;
		this.Columns = Columns;
        
        
        this.getContentPane().setLayout(new BorderLayout());
     /*   JPanel panelallISA = new JPanel();
		JPanel panelgridISA = new JPanel();
		JPanel panelbuttonISA = new JPanel(); 
			
		panelgridISA.setLayout(new GridLayout(5,2));
		
		panelgridISA.add(new JLabel("column threshold:"));

		panelgridISA.add(fieldCthres);
		
		//panelgridISA.add(new JLabel("[1.2,...,2]"));
		
		panelgridISA.add(new JLabel("min number of columns:"));
		
		panelgridISA.add(fieldminCISA);
		
		//panelgridISA.add(new JLabel("5"));
		
		panelgridISA.add(new JLabel("row threshold:"));
		
		panelgridISA.add(fieldRthres);
		
		//panelgridISA.add(new JLabel("[1.2,...,2]"));
		
		panelgridISA.add(new JLabel("min number of rows:"));

		panelgridISA.add(fieldminRISA);
		
		//panelgridISA.add(new JLabel("5"));
		
		panelgridISA.add(new JLabel("number of seeds:"));
		
		panelgridISA.add(fieldSeeds);
		
		//panelgridISA.add(new JLabel("1000"));
		
		panelgridISA.setBorder(BorderFactory.createEtchedBorder());
			
		panelbuttonISA.setLayout(new BorderLayout());
	    panelbuttonISA.add(okBtnISA, BorderLayout.CENTER);
		
	    okBtnISA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				    ISA();		
							
					}
					
			});
	    
	    panelallISA.add(panelgridISA,BorderLayout.CENTER);
	    panelallISA.add(panelbuttonISA,BorderLayout.SOUTH);
	    
		tabLeiste.addTab("ISA", panelallISA);
		*/
		//Bimax
		
		JPanel panelallBimax = new JPanel();
		JPanel panelgridBimax = new JPanel();
		JPanel panelbuttonBimax = new JPanel(); 
		
	    panelgridBimax.setLayout(new GridLayout(5,3));
		
		panelgridBimax.add(new JLabel("minimum number of columns:"));
		
		panelgridBimax.add(fieldminCBimax);
		
		//panelgridBimax.add(new JLabel("5"));
		
		panelgridBimax.add(new JLabel("minimum number of rows:"));
		
		panelgridBimax.add(fieldminRBimax);
		
		panelgridBimax.add(new JLabel("number of biclusters:"));
		
		panelgridBimax.add(fieldnumberBimax);
		
		panelgridBimax.add(new JLabel("proportion of ones:"));
		
		panelgridBimax.add(quantileBimax );
		
		panelgridBimax.add(new JLabel("direction:"));
		
		panelgridBimax.add(boxBimaxopt);
		//panelgridBimax.add(new JLabel("5"));
		
		//panelgridBimax.setBorder(BorderFactory.createEtchedBorder());
			
		panelbuttonBimax.setLayout(new BorderLayout());
	    panelbuttonBimax.add(okBtnBimax, BorderLayout.CENTER);
		
	    okBtnBimax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int minrow  =Integer.parseInt(fieldminRBimax.getText());
			    int mincol  =Integer.parseInt(fieldminCBimax.getText());
			    int number  =Integer.parseInt(fieldnumberBimax.getText());
			    double quantile =Double.parseDouble(quantileBimax.getText());
			    String direction = boxBimaxopt.getSelectedItem().toString();
			    Biclustering biclustering;
			    biclustering = Bimax(minrow,mincol,number,quantile,direction);		
			    bicdialog.seurat.dataManager.Biclusterings.insertElementAt(biclustering,0);
			    ClusteringManager cManager = bicdialog.seurat.getClusteringManager();
				cManager.addBiclustering(biclustering);
			    bicdialog.seurat.repaint();
			    dataManager.BiclusteringNumber++;
			    bicdialog.setVisible(false);
				}
					
			});
	    
	    panelallBimax.add(panelgridBimax,BorderLayout.CENTER);
	    panelallBimax.add(panelbuttonBimax,BorderLayout.SOUTH);
	    
		tabLeiste.addTab("Bimax", panelallBimax);
		//Plaid
		
		JPanel panelallPlaid = new JPanel();
		JPanel panelgridPlaid = new JPanel();
		JPanel panelbuttonPlaid = new JPanel(); 

		panelgridPlaid.setLayout(new GridLayout(3,2));
		
		panelgridPlaid.add(new JLabel("row release:"));
		
		panelgridPlaid.add(fieldRowrel);
		
		//panelgridPlaid.add(new JLabel("0.7"));
		
		panelgridPlaid.add(new JLabel("column release:"));

		panelgridPlaid.add(fieldColrel);
		
		//panelgridPlaid.add(new JLabel("0.7"));
		
		panelgridPlaid.add(new JLabel("shuffle iterations:"));
		
		panelgridPlaid.add(fieldShuffle);
		
		//panelgridPlaid.add(new JLabel("3"));
		
		//panelgridPlaid.setBorder(BorderFactory.createEtchedBorder());
			
		panelbuttonPlaid.setLayout(new BorderLayout());
	    panelbuttonPlaid.add(okBtnPlaid, BorderLayout.CENTER);
		
	    okBtnPlaid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				    double rowrel  =Double.parseDouble(fieldRowrel.getText());
				    double colrel  =Double.parseDouble(fieldColrel.getText());
				    double shuffle =Double.parseDouble(fieldShuffle.getText());
			     	Biclustering biclustering;
				    biclustering = PlaidModel(rowrel,colrel,shuffle);		
				    bicdialog.seurat.dataManager.Biclusterings.insertElementAt(biclustering,0);
				    ClusteringManager cManager = bicdialog.seurat.getClusteringManager();
					cManager.addBiclustering(biclustering);
				    bicdialog.seurat.repaint();
				    dataManager.BiclusteringNumber++;
				    bicdialog.setVisible(false);
				    }
					
			});
	    
	    panelallPlaid.add(panelgridPlaid,BorderLayout.CENTER);
	    panelallPlaid.add(panelbuttonPlaid,BorderLayout.SOUTH);
	    
		tabLeiste.addTab("PlaidModel", panelallPlaid);
		
		this.setResizable(false);
		
		this.getContentPane().add(tabLeiste,BorderLayout.CENTER);
		
			
		this.setVisible(true);
	}
	
	//public void ISA(){}
	
	public void Bimax(){
	
	
}

	
	public Biclustering PlaidModel(double rowrel,double colrel, double shuffle){
	int nbiclusters = 0;
	Vector<Vector<ISelectable>> rowresult = new Vector();
    Vector<Vector<ISelectable>> colresult = new Vector();
    Vector<Bicluster> bicresult = new Vector();
	try {

		if (dataManager.getRConnection() == null)
			dataManager.setRConnection(new RConnection());

		RConnection rConnection = dataManager.getRConnection();
			
		rConnection.voidEval("require(biclust)");
		rConnection.assign("tempData", Columns.elementAt(0).getColumn(Rows));

		for (int i = 1; i < Columns.size(); i++) {
			rConnection.assign("x",
		(Columns.elementAt(i)).getColumn(Rows));
			rConnection.voidEval("tempData <- cbind(tempData, x)");
		}
		
		rConnection.voidEval("res <- biclust(tempData,BCPlaid(),row.release="+ rowrel+ ",col.release=" + colrel + ",shuffle=" + shuffle+ ")"); 
		
	    nbiclusters = rConnection.eval("res@Number").asInteger();      
	    
	   //Biclustering bicresult = new Biclustering();
	    if(nbiclusters==0) JOptionPane.showMessageDialog(this, "No biclusters found");
	    
	    
	   	for (int i = 0; i < nbiclusters; i++) {
	   		colresult.add(new Vector());
			rowresult.add(new Vector());
		}
	    //System.out.println("Colresultsize"+colresult.size()+"gleich"+nbiclusters);
	    
	   	
	   	
	   	
	   	
	    for (int i = 1 ; i < (nbiclusters+1); i++){
	    	//System.out.println("next bicluster"+i);
	    	int[] rows = rConnection.eval("which(res@RowxNumber[," + i + "])").asIntegers();
	    	for (int j= 0; j < rows.length; j++){
	    		//System.out.print("Gene:");
	    		//System.out.println(rows[j]);
	    		//System.out.println(">i:"+i+ "j:" +j);
	    		//System.out.println(i-1);
	    		rowresult.elementAt(i-1).add(Rows.elementAt(rows[j]-1));
	    		//System.out.println(rowresult.elementAt(i-1).toString());
	    	} 
	    	 	
	    	int[] cols = rConnection.eval("which(res@NumberxCol[" + i + ",])").asIntegers();
	    	
	    	
	    	
	    	for (int j = 0; j < cols.length; j++){
	    		//System.out.print("Sample:");
	    		//System.out.println(cols[j]);
	    		colresult.elementAt(i-1).add(Columns.elementAt(cols[j]-1));
	    	}
	    	
	    	bicresult.add(new Bicluster("Bicluster"+i+"(PlaidModel,Rows:"+rowresult.elementAt(i-1).size()+",Columns:"+colresult.elementAt(i-1).size()+")",
	    			rowresult.elementAt(i-1),colresult.elementAt(i-1)));
	    	GlobalView globalView = new GlobalView(seurat,bicresult.elementAt(i-1).name,bicresult.elementAt(i-1).colums,bicresult.elementAt(i-1).rows);
	        
	        
	        int aggr = 1;
			while (rowresult.elementAt(i-1).size()*5/aggr > 700) aggr++;
            globalView.gPanel.setAggregation(aggr);
            globalView.applyNewPixelSize(15, 5);
	        globalView.setLocation(320 + (1000 * (i-1))/nbiclusters,0);	
	    
	    }    
	    rConnection.voidEval("rm(list=ls())");
 	    rConnection.voidEval("gc()");
	    
	} catch (Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Calculation failed.");
	}
	
	//System.out.println("PlaidModel calculated...");
	Biclustering biclustering = new Biclustering("Biclustering"+dataManager.BiclusteringNumber+"(PlaidModel)",bicresult);
	//bicresult.remove(0);
	return biclustering;
}
	
	public Biclustering Bimax(int minr,int minc,int number, double quantile, String direction){
		int nbiclusters = 0;
		Vector<Vector<ISelectable>> rowresult = new Vector();
	    Vector<Vector<ISelectable>> colresult = new Vector();
	    Vector<Bicluster> bicresult = new Vector();
		try {

			if (dataManager.getRConnection() == null)
				dataManager.setRConnection(new RConnection());

			RConnection rConnection = dataManager.getRConnection();
				
			rConnection.voidEval("require(biclust)");
			rConnection.assign("tempData", Columns.elementAt(0).getColumn(Rows));

			for (int i = 1; i < Columns.size(); i++) {
				rConnection.assign("x",
			(Columns.elementAt(i)).getColumn(Rows));
				rConnection.voidEval("tempData <- cbind(tempData, x)");
			}
			if(direction=="up"){ 
				rConnection.voidEval("thres <- quantile(tempData,probs="+ (1- quantile) +",na.rm=T)");
				rConnection.voidEval("tempData <- ifelse(tempData > thres,1,0)");
				rConnection.voidEval("tempData[which(is.na(tempData),arr.ind=T)] <- 0"); 
			}
			if(direction=="down"){
				rConnection.voidEval("thres <- quantile(tempData,probs="+ quantile +",na.rm=T)");
				rConnection.voidEval("tempData <-ifelese(tempData < thres ,1,0)");
				rConnection.voidEval("tempData[which(is.na(tempData),arr.ind=T)] <- 0");
			}
			    rConnection.voidEval("res <- biclust(tempData,method=BCBimax(),minr="+ minr+ ",minc=" + minc + ",number=" + number+ ")"); 
			
		    nbiclusters = rConnection.eval("res@Number").asInteger();      
		   
	//Biclustering bicresult = new Biclustering();
		    if(nbiclusters==0) JOptionPane.showMessageDialog(this, "No biclusters found");
		    
		    
		   	for (int i = 0; i < nbiclusters; i++) {
		   		colresult.add(new Vector());
				rowresult.add(new Vector());
			}
		   // System.out.println("Colresultsize"+colresult.size()+"gleich"+nbiclusters);
		    
		    for (int i = 1 ; i < (nbiclusters+1); i++){
		    	//System.out.println("next bicluster"+i);
		    	int[] rows = rConnection.eval("which(res@RowxNumber[," + i + "])").asIntegers();
		    	for (int j= 0; j < rows.length; j++){
		    		//System.out.print("Gene:");
		    		//System.out.println(rows[j]);
		    		//System.out.println(">i:"+i+ "j:" +j);
		    		//System.out.println(i-1);
		    		rowresult.elementAt(i-1).add(Rows.elementAt(rows[j]-1));
		    		//System.out.println(rowresult.elementAt(i-1).toString());
		    	} 
		    	 	
		    	int[] cols = rConnection.eval("which(res@NumberxCol[" + i + ",])").asIntegers();
		    	   	 			    		    	
		    	for (int j = 0; j < cols.length; j++){
		    		//System.out.print("Sample:");
		    		//System.out.println(cols[j]);
		    		colresult.elementAt(i-1).add(Columns.elementAt(cols[j]-1));
		    	}
		    	
		    	bicresult.add(new Bicluster("Bicluster"+i+"(Bimax,Rows:"+rowresult.elementAt(i-1).size()+",Columns:"+colresult.elementAt(i-1).size()+")",
		    			rowresult.elementAt(i-1),colresult.elementAt(i-1)));
		    	GlobalView globalView = new GlobalView(seurat,bicresult.elementAt(i-1).name,bicresult.elementAt(i-1).colums,bicresult.elementAt(i-1).rows);
		    	
		    	
		    	int aggr = 1;
				while (rowresult.elementAt(i-1).size()*5/aggr > 700) aggr++;
	            globalView.gPanel.setAggregation(aggr);
	            globalView.applyNewPixelSize(15, 5);
			    globalView.setLocation(320 + (1000 * (i-1))/nbiclusters,0);		
		    
		    
		    }    

		    rConnection.voidEval("rm(list=ls())");
		    rConnection.voidEval("gc()");    
		    
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Calculation failed.");
		}
		
		Biclustering biclustering = new Biclustering("Biclustering"+dataManager.BiclusteringNumber+"(Bimax)",bicresult);
		//bicresult.remove(0);
		return biclustering;
	}	
	
}