package Data;

import java.awt.Color;
import java.util.Vector;
import java.util.Iterator;

import GUI.IPlot;
import GUI.Seurat;

public class Chromosome implements ISelectable{
	
	public Vector<Clone> Clones = new Vector();
	
	public Vector<Gene> Genes = new Vector();
	
	
	public String name;
	
	public double length = -1;
	
	public Vector<CytoBand> CytoBands;
	
	
	public double chrStart;
	public double chrEnd;
	public double chrCen;
	public double Center;
	
	DataManager dataManager;
	
	Seurat seurat;
	
	public Chromosome(Seurat seurat,String name) {
		this.name = name;
		this.seurat = seurat;
		this.dataManager = seurat.dataManager;
	}
	
	
	public void calculateCytoBand() {
		
		
		CytoBands = new Vector();
		for (int i = 0; i < Clones.size(); i++) {
			//System.out.println("Calculate CytoBand");

			
			Clone clone = Clones.elementAt(i);
	        String CytoBand = clone.CytoBand;
	         if (contains(CytoBand)!=null) {
	        	 CytoBand cyto = contains(CytoBand);
	        	 cyto.Clones.add(clone);
	        	 if (clone.NucleoPosition != dataManager.NA && cyto.Start > clone.NucleoPosition) cyto.Start = clone.NucleoPosition;
	        	 if (clone.NucleoPosition != dataManager.NA && cyto.End < clone.NucleoPosition) cyto.End = clone.NucleoPosition;
	   	      
	         
	         }        
	         else {
	        	 CytoBand cyto = new CytoBand(seurat,CytoBand,new Vector(), clone.chromosome);
	        	 cyto.Clones.add(clone);
	        	 cyto.Start = clone.NucleoPosition;
	        	 cyto.End = clone.NucleoPosition;
	        	 CytoBands.add(cyto);
	         }
		}
	}
	
	
	public CytoBand contains(String s) {
		for (int i = 0; i  < CytoBands.size(); i++) {
			CytoBand cyto = CytoBands.elementAt(i);
			if (cyto.name.equals(s)) return cyto;
		}
		return null;
	}
	
	
	
	
	
	
	public void calculateLength() {
		Iterator iter = Clones.iterator();
		while(iter.hasNext()) {
		Clone clone = (Clone)iter.next();
		if (length < clone.NucleoPosition) length = clone.NucleoPosition;
		}
	    
		
	}
	
	public int [] calculatePosHist(int len, int width, Vector<CGHVariable> cghVars) {
		double [] hist = new double [len];
	    java.util.Iterator<Clone> iter = Clones.iterator();
		while (iter.hasNext()) {
			Clone clone = iter.next();
		    int start = (int)Math.round(chrStart*len/length);
		    int end = (int)Math.round(chrEnd*len/length);
		    
		//    System.out.println(name + "   "+ clone.chrCen + "  "+ clone.chrStart + "  " + clone.chrEnd);
		    
		    for (int i = start; i <= end; i++) {
		    	if (i < hist.length) {
		    	for (int j = 0; j < cghVars.size(); j++) {
		    		CGHVariable cghVar = cghVars.elementAt(j);
		    		if (cghVar.getValue(clone.getID())>0)  hist [i] = hist [i] + cghVar.getValue(clone.getID());
		        }
		    	}
		    }
		    
		}
		
		
		double max = getMax(hist);
		 
		
		int [] intHist = new int [len];
		
		for (int i = 0; i < hist.length; i++) {
			intHist [i] = (int) Math.round(hist [i] * width / max);
		}
		 
		
		return intHist;
		
	}
	
	
	public double getMax(double [] hist) {
		double max = -100;
		for (int i = 0; i < hist.length; i++) {
		      if (hist [i]>max) max = hist [i];	
		}
		return max;
		
	}
	
	
	
	
	public int [] calculateNegHist(int len, int width, Vector<CGHVariable> cghVars) {
		double [] hist = new double [len];
	    java.util.Iterator<Clone> iter = Clones.iterator();
		while (iter.hasNext()) {
			Clone clone = iter.next();
		    int start = (int)Math.round(chrStart*len/length);
		    int end = (int)Math.round(chrEnd*len/length);
		    
		    for (int i = start; i <= end; i++) {
		    	if (i < hist.length) {
		    	for (int j = 0; j < cghVars.size(); j++) {
		    		CGHVariable cghVar = cghVars.elementAt(j);
		    		
		    //		System.out.println("+++++++++++++"+cghVar.getValue(clone.getID()));
		    		
		    		if (cghVar.getValue(clone.getID())<0)  hist [i] = hist [i] - cghVar.getValue(clone.getID());
		        }
		    	}
		    }
		    
		}
		
		
		double min = getMax(hist);
		 
		
		int [] intHist = new int [len];
		
		for (int i = 0; i < hist.length; i++) {
			intHist [i] = (int) Math.round(hist [i] * width / (min));
		}
		 
		
		return intHist;
		
	}
	
	
	public double getMin(double [] hist) {
		double min = 0;
		for (int i = 0; i < hist.length; i++) {
		      if (hist [i]>min) min = hist [i];	
		}
		return min;
		
	}


	public double[] getColumn(Vector<ISelectable> cols) {
		// TODO Auto-generated method stub
		return null;
	}


	public double[] getDoubleData() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getMax() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getMin() {
		// TODO Auto-generated method stub
		return 0;
	}


	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}


	public double getRealValue(int id) {
		// TODO Auto-generated method stub
		return 0;
	}


	public double[] getRow(Vector<ISelectable> rows) {
		// TODO Auto-generated method stub
		return null;
	}


	public String[] getStringData() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getValue(int id) {
		// TODO Auto-generated method stub
		return 0;
	}


	public Vector<ISelectable> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isVariable() {
		// TODO Auto-generated method stub
		return false;
	}


	public void select(boolean weiter) {
		// TODO Auto-generated method stub
		
	}


	public void unselect(boolean weiter) {
		// TODO Auto-generated method stub
		
	}


	public boolean isGene() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isClone() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isCGHVariable() {
		// TODO Auto-generated method stub
		return false;
	}


	public Vector<Color> getColors() {
		// TODO Auto-generated method stub
		return null;
	}


	public Vector<String> getColorNames() {
		// TODO Auto-generated method stub
		return null;
	}


	public Vector<IPlot> getBarchartToColors() {
		// TODO Auto-generated method stub
		return null;
	}

}
