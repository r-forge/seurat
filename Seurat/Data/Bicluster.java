package Data;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;


public class Bicluster {
	
	public Vector<ISelectable> rows;
	public Vector<ISelectable> columns;
	public String name;

	public Bicluster(String name, Vector<ISelectable> rows, Vector<ISelectable> colums) {
		this.name = name;
		this.rows =rows;
	    this.columns = colums;
	   	}
	
	
	public void seriate(DataManager dataManager) {
		
	Vector<ISelectable> Experiments = new Vector();
	Vector<ISelectable> Genes = new Vector();
	for (int i = 0; i < columns.size(); i++) {
		Experiments.add(columns.elementAt(i));
	}
	for (int i = 0; i < rows.size(); i++) {
		Genes.add(rows.elementAt(i));
	}
	

	String method = "MDS";
	String Distance = "euclidean";

	try {
	try {

		if (dataManager.getRConnection() == null)
			dataManager.setRConnection(new RConnection());

		RConnection rConnection = dataManager.getRConnection();	
		
		
		dataManager.rConnection.voidEval("require(stats)");
		
		
		
	
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
		if (!method.equals("BEA") && !method.equals("PCA")) dataManager.rConnection.voidEval("order<-seriate(d,method=\"" + method  + "\")");
		else dataManager.rConnection.voidEval("order<-seriate(tempData,method=\"" + method + "\")");

		int[] orderZeilen = dataManager.rConnection.eval("get_order(order,1)")
				.asIntegers();
			
					
		
		dataManager.rConnection.assign("tempData", Genes.elementAt(0).getRow(Experiments));

		for (int i = 1; i < Genes.size(); i++) {
			dataManager.rConnection.assign("x", Genes.elementAt(i).getRow(Experiments));
			dataManager.rConnection.voidEval("tempData <- cbind(tempData, x)");

		}

		dataManager.rConnection.voidEval("d<-dist(tempData,method=\""
				+ Distance + "\")");
		
		if (!method.equals("BEA") && !method.equals("PCA")) dataManager.rConnection.voidEval("order<-seriate(d,method=\"" + method+ "\")");
		else dataManager.rConnection.voidEval("order<-seriate(tempData,method=\"" + method+ "\")");

		int[] orderSpalten = dataManager.rConnection.eval("get_order(order,1)")
				.asIntegers();
			
		
		Vector<ISelectable> G = new Vector();
		
		
		System.out.println("RowIDs");
		for (int i = 0; i < Genes.size(); i++) {
			G.add(Genes.elementAt(orderZeilen[i]-1));
	//		System.out.print(((ISelectable)G.lastElement()).getID() + ",");
		}
		//System.out.println();
		
		
		
		
		Vector<ISelectable> E = new Vector();
		

		for (int i = 0; i < Experiments.size(); i++) {
			E.add(Experiments.elementAt(orderSpalten[i]-1));
		}
		
		

	    columns = E;
	    rows = G;

		
		rConnection.voidEval("rm(list=ls())");
		rConnection.voidEval("gc()");
		
	} catch (RserveException e) {
		e.printStackTrace();
		
	}
} catch (Exception e) {
	e.printStackTrace();
}
}
	
	
}
