package Data;

import java.awt.Color;
import java.util.Vector;

import GUI.IPlot;

public interface ISelectable {

	public void select(boolean weiter);
	
	public void unselect(boolean weiter);
	
	public boolean isSelected();
	
	
	
	public boolean isVariable();
	
	public boolean isGene();
	
	public boolean isClone();
	
	public boolean isCGHVariable();
	
	public int getID();
	
	public String getName();
	
	public double [] getColumn(Vector<ISelectable> cols);
	
	public double [] getRow(Vector<ISelectable> rows);
	
	public Vector<Color> getColors(); 
	
	public Vector<String> getColorNames(); 
	
	public Vector<IPlot> getBarchartToColors();
	
	
	public double getRealValue(int id);
	
	public String [] getStringData();
	
	
	public double [] getDoubleData();
	
	
	public double getValue(int id);
	
	public double getMax();
	
	
	public double getMin();
	
	public Vector<ISelectable> getVariables();
	
	/**Continuous == 1, Discrete == 2, List == 3*/
	public int getType();
	
	 
	
	
}
