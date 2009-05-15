package Data;

import java.util.Vector;

public class Gene implements ISelectable,NodeCase{
	public String Name;
	
	Vector<Clone> clones;
	
	public int ID;
	
	DataManager dataManager;
	
	public boolean selected = false;
	
	public Vector<Clone> CLONES = new Vector();
	
	
	
	public Gene(String ID,int Index,DataManager dataManager) {
		this.Name = ID;
		this.ID = Index;
		this.dataManager = dataManager;
		
	}
	
	public void select(boolean weiter) {
		selected = true;
	//	for (int i = 0; i < dataManager.Experiments.size(); i++) {
		//	dataManager.Experiments.elementAt(i).isSelected [ID] = true;
		//}
		
		
		if ( weiter) {
		for (int i = 0; i < CLONES.size(); i++) {
			CLONES.elementAt(i).select(false);
		}
		
		}
	}
	
	
	public void unselect(boolean weiter) {
		selected = false;
	//	for (int i = 0; i < dataManager.Experiments.size(); i++) {
		//	dataManager.Experiments.elementAt(i).isSelected [ID] = false;
		//}
		
		if (weiter) {
		
		for (int i = 0; i < CLONES.size(); i++) {
			CLONES.elementAt(i).unselect(false);
		}
		}
	}
	
	
	public boolean isSelected() {
		
		/*
		for (int i = 0; i < dataManager.Experiments.size(); i++) {
			if (dataManager.Experiments.elementAt(i).isSelected [ID]) return true;
		}
		
		return false;
		*/
		return selected;
		
	}
	
	
	
	
	public double[] getRow(Vector<ISelectable> Experiments) {
		double[] rowData = new double[Experiments.size()];
		for (int i = 0; i < rowData.length; i++) {
			rowData[i] = Experiments.elementAt(i).getValue(ID);
		}
		return rowData;
	}
	
	
	
	
	

	public boolean isVariable() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return Name;
		//return dataManager.variables.elementAt(0).elementAt(Integer.parseString(ID));
	}

	public double getRealValue(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getValue(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double[] getColumn(Vector<ISelectable> cols) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMin() {
		// TODO Auto-generated method stub
		return 0;
	}

}
