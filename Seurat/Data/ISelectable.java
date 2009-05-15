package Data;

import java.util.Vector;

public interface ISelectable {

	public void select(boolean weiter);
	
	public void unselect(boolean weiter);
	
	public boolean isSelected();
	
	public boolean isVariable();
	
	public int getID();
	
	public String getName();
	
	public double [] getColumn(Vector<ISelectable> cols);
	
	public double [] getRow(Vector<ISelectable> rows);
	
	
	public double getRealValue(int id);
	
	public double getValue(int id);
	
	public double getMax();
	
	
	public double getMin();
	
	
}
