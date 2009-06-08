package Data;

import java.awt.Color;
import java.util.Vector;

import GUI.CGHViewer;
import GUI.IPlot;

public class Clone implements ISelectable{
	
	public String NAME;
	
	public Vector<Gene> Genes = new Vector();
	
	public int ID;
	
	boolean isSelected = false;
	
	CGHViewer viewer;
	
//	public double chrStart;
	//public double chrEnd;
	//public double chrCen;

	public double NucleoPosition;
	
	public String CytoBand;
	
    public Chromosome chromosome;
	
	public Clone(String NAME, int ID,CGHViewer viewer) {
		this.ID = ID;
		this.NAME = NAME;
		this.viewer = viewer;
	}
	
	public void select(boolean weiter) {
		isSelected = true;
		
		if (weiter) 
		for (int i = 0; i < Genes.size(); i++) {
		
		    Genes.elementAt(i).select(false);	
		}
	}
	

	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}

	public boolean isSelected() {
		// TODO Auto-generated method stub
		return isSelected;
	}

	public boolean isVariable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void unselect(boolean weiter) {
		// TODO Auto-generated method stub
		isSelected = false;
		
		if (weiter) 
		{
		for (int i = 0; i < Genes.size(); i++) {
		    Genes.elementAt(i).unselect(false);	
		}
		}
		
	}

	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	public double getRealValue(int varID) {
		// TODO Auto-generated method stub
		return viewer.cghVariables.elementAt(varID).doubleData [ID];
	}

	public double getValue(int varID) {
		// TODO Auto-generated method stub
		return viewer.cghVariables.elementAt(varID).doubleData [ID];
	}
	
	public double getValue(String varName) {
		for (int i = 0; i < viewer.cghVariables.size(); i++) {
			CGHVariable var = viewer.cghVariables.elementAt(i);
			if (var.name.contains(varName)) return var.doubleData [ID];
		}
		return 0/0;
		
	}
	
	

	public double[] getColumn(Vector<ISelectable> cols) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getRow(Vector<ISelectable> rows) {
		// TODO Auto-generated method stub
		double[] rowData = new double[rows.size()];
		for (int i = 0; i < rowData.length; i++) {
			rowData[i] = rows.elementAt(i).getValue(ID);
		}
		return rowData;
	}

	public double getMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return -1;
	}

	public String[] getStringData() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<ISelectable> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getDoubleData() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isGene() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isClone() {
		// TODO Auto-generated method stub
		return true;
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
