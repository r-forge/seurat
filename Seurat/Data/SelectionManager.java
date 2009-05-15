package Data;

import java.util.*;

public class SelectionManager {
	Vector<Variable> Experiments;
	
	Vector<Gene> Genes;
	
	
	DataManager dataManager;
	
	
	public SelectionManager(DataManager dataManager) {
		this.dataManager = dataManager;
		Experiments = dataManager.Experiments;
	}
/*
	
	public void addSelectionToDataMatrix() {
			
			for (int i = 0; i < variables.size(); i++) {
				boolean isSelected = variables.elementAt(i).isSelected();
			    Variable var = (Variable) variables.elementAt(i);
			    for (int j = 0; j < var.isSelected.length; j++) {		
			    	if (isSelected) var.isSelected [j] = true; 
			    	else var.isSelected [j] = false;
			    }
				
			}
			
		
		
		
 
			
			for (int i = 0; i < dataManager.Experiments.size(); i++) {
				Variable var = dataManager.Experiments.elementAt(i);
				
			    for (int j = 0; j < var.isSelected.length; j++) {	
			    	boolean isSelected = variables.elementAt(j).isSelected();
					   
			    	if (isSelected) var.isSelected [j] = true; 
			    	else var.isSelected [j] = false;
			    }
			}
			
		
		*/
		
		
		
	
	
	
	
	public void applySelectionFromDataMatrix() {
		
	}
	
	
	
	
	
	
	
	
	
	public void select() {}
	
	

}
