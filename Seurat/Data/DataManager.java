package Data;


import java.io.BufferedWriter;
import java.util.Vector;

import org.rosuda.REngine.Rserve.RConnection;

import GUI.ExperimentDescriptionFrame;



public class DataManager {
	public Vector<Variable> variables;

	//public boolean[] selectedVariables;

	//public boolean[] selectedRows;
	
	public Vector<Gene> Genes;
	
	public RConnection rConnection;

	public static double NA = 6.02E23;

	public double minValue, maxValue;
	
	
	
	public Vector<Variable> Experiments;

	public Vector<Variable> ExperimentDescr;
	
    public Vector<GeneVariable> geneVariables;
    
    
    public Vector<CGHVariable> cghVariables;
    
    public Vector<Clone> CLONES;
    
    public Vector<AnnGene> AnnGenes;
	
	
	public int RowCount;
	
	
	public Vector<DescriptionVariable> descriptionVariables;

//	public Object rConnection;
	
	
	public Vector<Vector<ISelectable>> seriationsGenes = new Vector();
	
	public Vector<Vector<ISelectable>> seriationsExperiments =new Vector();
	
	public Vector<String> seriationGeneNames = new Vector();
	
	public Vector<String> seriationExperimentNames = new Vector();
	
	
	
	public Vector<Clustering> GeneClusters = new Vector();
	public Vector<Clustering> ExpClusters = new Vector();
	
	public Vector<Chromosome> Chromosomes;
	
	
	

	/**Konstanten */
	
	public String ChromosomeNumber = "ChromosomeNumber";
	public String NucleotidePosition = "NucleotidePosition";
	public String ChrStart = "ChrStart";
	public String ChrEnd = "ChrEnd";
	public String ChrCen = "ChromosomeCen";
	//public String CloneMidPoint = "CloneMidpoint";
	public String CloneCytoBand = "Mapping";
	public String States = ".States";
	
	
	public DataManager()
	{
		
		
		
	}	
	
	
	
	public Vector<CGHVariable> getStates() {
		Vector v = new Vector();
		
		for (int i= 0; i < cghVariables.size(); i++) {
			CGHVariable var = cghVariables.elementAt(i);
			if (var.name.contains(States)) v.add(var);
			
		}
		return v;
	}
	
	
	/**
	 * Speichert die Selektion aus Genexpressiondaten
	 * */
	public void saveGeneExpressionData(Vector<Variable> Experiments, BufferedWriter bfr) {
		// Speichern Variablennamen
		try {
			
			
			
			
		for (int i = 0; i < Experiments.size(); i++) {
			bfr.write(Experiments.elementAt(i).name + "	");
		}
		bfr.write('\n');
		
		
		int a = 0;
		for (int j = 0; j< Genes.size(); j++) {
		if (Genes.elementAt(j).isSelected()) {
			
			a++;
			for (int i = 0; i < Experiments.size(); i++) {
				if (Experiments.elementAt(i).containsGeneProfiles()) bfr.write(Experiments.elementAt(i).getColumn() [j]+"	");
				else bfr.write(Experiments.elementAt(i).stringData [j]+"	");
			}
			
			
			
			
			
			
			
		bfr.write('\n');
		}
		
		}
		
		System.out.println(a);
		
		bfr.close();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public Chromosome getChromosome(String name) {
		for (int i = 0; i < Chromosomes.size() ; i++) {
			//System.out.println("* "+Chromosomes.elementAt(i).name);
			if (Chromosomes.elementAt(i).name.replace("\"","").equals(name.replace("\"",""))) return Chromosomes.elementAt(i);
		}
		return null;
	}
	
	
	
	
	/**
	 * Speichert die Selektion aus Experiment Descriptions
	 * */
	public void saveExperimentsDescriptions(Vector<DescriptionVariable> DescrVars, BufferedWriter bfr) {
		// Speichern Variablennamen
		try {
			
			for (int i = 0; i < DescrVars.size(); i++) {
				bfr.write(DescrVars.elementAt(i).name+"	");
			}
			
			
		
			bfr.write('\n');
		
		
		for (int j = 0; j< DescrVars.elementAt(0).stringData.length; j++) {
			if (Experiments.elementAt(j).isSelected()) {
				
			for (int i = 0; i< DescrVars.size(); i++) {
				bfr.write(DescrVars.elementAt(i).stringData [j]+"	");
			}
			 //   System.out.print(b) 
			
		bfr.write('\n');
			}
		
		}
		
		bfr.close();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	/**
	 * Saves selected Gene Annotations
	 * */
	public void saveGeneAnnotations(Vector<GeneVariable> GeneVars, BufferedWriter bfr) {
		// Speichern Variablennamen
		try {
			
			for (int i = 0; i < GeneVars.size(); i++) {
				bfr.write(GeneVars.elementAt(i).name+"	");
			//	 System.out.print(Genes.elementAt(i).name+"	"); 
			}
			//System.out.println();
			
		
			bfr.write('\n');
		
			for (int i = 0; i< Genes.size(); i++) {
				if (Genes.elementAt(i).isSelected()) {
					
		
		           for (int j = 0; j< GeneVars.size(); j++) {
		
				bfr.write(GeneVars.elementAt(j).stringData [i]+"	");
				  //System.out.print(Genes.elementAt(i).stringData [j]+"	"); 
			}
			
		bfr.write('\n');
		
			}     
		
		}
		
		bfr.close();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Saves selected CGH Data
	 * */
	public void saveCGH(BufferedWriter bfr) {
		// Speichern Variablennamen
		try {
			
			for (int i = 0; i < cghVariables.size(); i++) {
				CGHVariable var= cghVariables.elementAt(i);
				
				if (var.isSelected || var.vars == null) bfr.write(var.name+"	");
			//	 System.out.print(Genes.elementAt(i).name+"	"); 
			}
			//System.out.println();
			
		
			bfr.write('\n');
		
			for (int i = 0; i< CLONES.size(); i++) {
				if (CLONES.elementAt(i).isSelected()) {
					
		
		           for (int j = 0; j< cghVariables.size(); j++) {
		        	   CGHVariable var= cghVariables.elementAt(j);
						
		        	   if (var.isSelected || var.vars == null) bfr.write(var.stringData [i]+"	");
				  //System.out.print(Genes.elementAt(i).stringData [j]+"	"); 
			}
			
		bfr.write('\n');
		
			}     
		
		}
		
		bfr.close();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	/*
	
	public void clearSelection() {
		
		for (int i = 0; i < Experiments.size(); i++) {
			//selectedVariables[i] = false;
			Experiments.elementAt(i).selected = false;
			
		}

		for (int i = 0; i < Genes.size(); i++) {
			Genes.elementAt(i).selected = false;

		}
		
	//	for (int i = 0; i < this.Experiments.size(); i++) {
		//	for (int j = 0; j < Experiments.elementAt(i).isSelected.length; j++) {
			//	Experiments.elementAt(i).isSelected[j] = false;
			//}
		//}
		
	}
*/
	public void selectVariables() {
		if (Experiments != null) {
		for (int i = 0; i < this.Experiments.size(); i++) {
			Variable var = Experiments.elementAt(i);
	//		var.selected = false;
	//		for (int j = 0; j < var.isSelected.length; j++) {
		//		if (var.isSelected[j])
			//		var.selected = true;
			//}
		}
		}
	}
	
	
	
	
	public boolean isSomethingSelected() {
		
		if (Experiments != null) {
			for (int i = 0; i < this.Experiments.size(); i++) {
				Variable var = Experiments.elementAt(i);
				if (var.isSelected()) return true;
		
			}
			}
		
		
		if (Genes != null) {
			for (int i = 0; i < this.Genes.size(); i++) {
				Gene var = Genes.elementAt(i);
				if (var.isSelected()) return true;
		
			}
			}
		
		
	//	for (int i = 0; i < Experiments.size(); i++) {
	//		for (int j = 0; j < Experiments.elementAt(i).isSelected.length; j++) {
	//			if (Experiments.elementAt(i).isSelected [j]) return true;
	//		}
	//	}
		return false;
		
	}
	
	
	
	
	
	
	public double[] getRowData(int row) {
		double[] rowData = new double[Experiments.size()];
		for (int i = 0; i < rowData.length; i++) {
			rowData[i] = Experiments.elementAt(i).getValue(row);
		}
		return rowData;
	}

	public boolean isRowSelected(int row) {
		return (Genes.elementAt(row).isSelected());
		//for (int i = 0; i < this.Experiments.size(); i++) {
		//	Variable var = Experiments.elementAt(i);
		//	if (var.isSelected[row])
		//		return true;
		//}
		//return false;
	}

	public int getIndexInArray(int index, int[] order) {
		for (int i = 0; i < order.length; i++) {
			if (order[i] == index)
				return i;
		}
		return -1;
	}

	public void selectRow(int row) {
		
		Genes.elementAt(row).select(true);
		/*
		for (int i = 0; i < this.Experiments.size(); i++) {
			Variable var = Experiments.elementAt(i);
			var.selected = true;
			var.isSelected[row] = true;

		}
	*/	
	}
	
	
	
	
	public void deleteSelection() {
		if (Experiments != null) {
		for (int i = 0; i < this.Experiments.size(); i++) {
			Experiments.elementAt(i).unselect(false);
		}
		
		
		for (int i = 0; i < this.Genes.size(); i++) {
			Genes.elementAt(i).unselect(false);
		}
		
		}
		
		if (AnnGenes != null) {
			for (int i =0; i < AnnGenes.size(); i++) {
				AnnGenes.elementAt(i).unselect(false);
			}
		}
		
		
		
		if (cghVariables != null) {
			for (int i = 0; i < this.cghVariables.size(); i++) {
				cghVariables.elementAt(i).unselect(false);
			}
			
			
			for (int i = 0; i < this.CLONES.size(); i++) {
				CLONES.elementAt(i).unselect(false);
			}
			
			}
			
		
		
	}
	
	

	public void selectGenesClones() {
		if (Experiments != null) {
		
		
		
		for (int i = 0; i < this.Genes.size(); i++) {
			Genes.elementAt(i).select(false);
		}
		
		}
		
		
		
		if (cghVariables != null) {
			
			
			
			for (int i = 0; i < this.CLONES.size(); i++) {
				CLONES.elementAt(i).select(false);
			}
			
			}
			
		
		
	}
	
	

	public void selectExperiments() {
		if (Experiments != null) {
		for (int i = 0; i < this.Experiments.size(); i++) {
			Experiments.elementAt(i).select(false);
		}
		
		
	
		}
		
		
		
		if (cghVariables != null) {
			for (int i = 0; i < this.cghVariables.size(); i++) {
				cghVariables.elementAt(i).select(false);
			}
			
		
			}
			
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void selectAll() {
		if (Experiments != null) {
		for (int i = 0; i < this.Experiments.size(); i++) {
			Experiments.elementAt(i).select(false);
		}
		
		
		for (int i = 0; i < this.Genes.size(); i++) {
			Genes.elementAt(i).select(false);
		}
		
		}
		
		
		
		if (cghVariables != null) {
			for (int i = 0; i < this.cghVariables.size(); i++) {
				cghVariables.elementAt(i).select(false);
			}
			
			
			for (int i = 0; i < this.CLONES.size(); i++) {
				CLONES.elementAt(i).select(false);
			}
			
			}
			
		
		
	}
	
	
	
	
	
	
	
	
	
	

	public Vector<Variable> getVariables() {
		return variables;
	}

	public void setVariables(Vector<Variable> variables) {
		this.variables = variables;
	}

	

	
	public RConnection getRConnection() {
		return rConnection;
	}

	public void setRConnection(RConnection connection) {
		rConnection = connection;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public Vector<Variable> getExperiments() {
		return Experiments;
	}

	public void setExperiments(Vector<Variable> experiments) {
		Experiments = experiments;
	}

	public Vector<Variable> getDescriptions() {
		return ExperimentDescr;
	}

	public void setDescriptions(Vector<Variable> descriptions) {
		ExperimentDescr = descriptions;
	}

	public int getRowCount() {
		return RowCount;
	}

	public void setRowCount(int rowCount) {
		RowCount = rowCount;
	}
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
