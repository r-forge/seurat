package Data;

import java.util.Vector;

import GUI.Seurat;

public class CytoBand {
	

	
	public String name;
	
	public Vector<Clone> Clones;
	
	public double Start;
	public double End;
	
	Chromosome chr;
	
	Seurat seurat;
	
	public CytoBand(Seurat seurat,String name,Vector Clones, Chromosome chr) {
	
		this.seurat = seurat;
		this.name = name;
		this.Clones = Clones;
		this.chr = chr;
	
	}
	
	
	public void select(boolean weiter, Vector<CGHVariable> Cases) {
		for (int ii = 0; ii < Clones.size(); ii++) {
			Clones.elementAt(ii).select(weiter);
		
		} 
	}
	
	public void selectCGHs(Clone clone, Vector<CGHVariable> Cases) {
		for (int i = 0; i < Cases.size(); i++) {
			CGHVariable var = Cases.elementAt(i);
			if (var.getValue(clone.getID()) !=seurat.dataManager.NA &&  var.getValue(clone.getID())!=0) {
				var.select(true);
			}
		}
	}
	
	
	

	
	
	

}
