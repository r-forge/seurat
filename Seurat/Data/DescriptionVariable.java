package Data;
import java.awt.Color;
import java.util.*;

import GUI.IPlot;

public class DescriptionVariable implements ISelectable{
	public String name;

	public int type; // Double,String

	 public boolean isDouble = true;

	public boolean isDiscrete = false;

	public final double NA = Math.PI;

	public Vector<String> stringBuffer = new Vector();

	public static final int Double = 1, String = 2;

	public double[] doubleData;

	public String[] stringData;
	
	public Vector<ISelectable> variables;
	
	public DescriptionVariable indexVar;

	public DescriptionVariable(String name, int type,Vector variables) {
		this.name = name;
		this.type = type;
		this.variables = variables;
	
	}

	public boolean containsNa() {
		for (int i = 0; i < stringBuffer.size(); i++) {
			if (stringBuffer.elementAt(i).equals("" + NA))
				return true;
		}
		return false;
	}

	public double[] getColumn(Vector<ISelectable> cols) {
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

	public java.lang.String getName() {
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

	public int getType() {
		// TODO Auto-generated method stub
		if (isDiscrete) return 2;
		if (isDouble) return 1;
		
		
		
		return -1;
	}

	public double getValue(int id) {
		// TODO Auto-generated method stub
		return 0;
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
	
	
	public String [] getStringData() {
		// TODO Auto-generated method stub
		 String [] data = new String [variables.size()];
		   for (int i = 0; i < variables.size(); i++) {
			   ISelectable var = variables.elementAt(i);
			   if (findInBuffer(variables.elementAt(i).getName(),indexVar.stringBuffer)!=-1) {data [i] = stringData [findInBuffer(variables.elementAt(i).getName(),indexVar.stringBuffer)];}
			   else data [i] = "NA";
		   }
			
			return data;
			
			
	}

	public Vector<ISelectable> getVariables() {
		// TODO Auto-generated method stub
		return variables;
	}

	public double[] getDoubleData() {
		// TODO Auto-generated method stub
	   double [] data = new double [variables.size()];
	   for (int i = 0; i < variables.size(); i++) {
		   ISelectable var = variables.elementAt(i);
		   if (findInBuffer(variables.elementAt(i).getName(),indexVar.stringBuffer)!=-1) {data [i] = doubleData [findInBuffer(variables.elementAt(i).getName(),indexVar.stringBuffer)];}
		   else data [i] = DataManager.NA;
	   }
		
		return data;
	}
	
	public int findInBuffer(String s, Vector<String> Buffer) {
		for (int i = 0; i < Buffer.size(); i++) {
			String ss = s.replaceAll("\"","");
			if (Buffer.elementAt(i).contains(ss)) return i;
		}
		return -1;
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

	public Vector<java.lang.String> getColorNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<IPlot> getBarchartToColors() {
		// TODO Auto-generated method stub
		return null;
	}

}
