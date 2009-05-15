package Data;

import java.util.StringTokenizer;
import java.util.Vector;

import GUI.Seurat;

public class CGHVariable implements ISelectable{
	public String name;

	public int type; // Double,String

	 public boolean isDouble = true;

	public boolean isDiscrete = false;
	
	public boolean isList = false;
	
	double max,min;

	public double NA = 6.02E23;

	public Vector<String> stringBuffer = new Vector();

	public static final int Double = 1, String = 2;

	public double[] doubleData;

	public String[] stringData;
	
	public Vector<ISelectable> variables;
	
	public int [] bufferCount; 
	
	public Vector<Vector<Integer>> geneMitListValue;
	
	boolean isSelected = false;
	
	public double mean;
	
	public Vector<Variable> vars = new Vector();
	
	
	Seurat seurat;

	public CGHVariable(String name, int type, Vector variables, Seurat seurat) {
		this.name = name;
		this.type = type;
		this.variables = variables;
		this.seurat = seurat;
	}

	public boolean containsNa() {
		for (int i = 0; i < stringBuffer.size(); i++) {
			if (stringBuffer.elementAt(i).equals("" + NA))
				return true;
		}
		return false;
	}
	
	
	public void calculateList() {
		stringBuffer = new Vector();
		
		for (int i = 0; i < stringData.length; i++) {
	       StringTokenizer tokenizer = new StringTokenizer(stringData [i],",");
	       while (tokenizer.hasMoreTokens()) {
	           String token = tokenizer.nextToken(); 
	    	   if (doesntContain(stringBuffer,token)) stringBuffer.add(token);
	    	   
	       }
		}
		
		bufferCount = new int [stringBuffer.size()];
		geneMitListValue = new Vector();
        for (int i = 0; i < stringBuffer.size();i++){
        	geneMitListValue.add(new Vector<Integer>());
        }  
		
        
		for (int i = 0; i < stringData.length; i++) {
		       StringTokenizer tokenizer = new StringTokenizer(stringData [i],",");
		       while (tokenizer.hasMoreTokens()) {
		           String token = tokenizer.nextToken(); 
		           int index = indexOf(stringBuffer,token);
		           bufferCount [index] ++;
		    	   Vector<Integer> List = geneMitListValue.elementAt(index);
		    	   List.add(i);
		       }
			}
		
		
	}
	
	
	
	public boolean doesntContain(Vector<String> buffer, String word) {
		for (int i = 0; i < buffer.size(); i++) {
			if (buffer.elementAt(i).equals(word))
				return false;
		}
		return true;
	}
	
	public int indexOf(Vector<String> buffer, String word) {
		for (int i = 0; i < buffer.size(); i++) {
			if (buffer.elementAt(i).equals(word))
				return i;
		}
		return -1;
	}

	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public java.lang.String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getRealValue(int id) {
		// TODO Auto-generated method stub
		return doubleData [id];
	}

	public double getValue(int id) {
		// TODO Auto-generated method stub
		if (doubleData [id] != NA) return doubleData [id];
		return mean;
	}

	public boolean isSelected() {
		// TODO Auto-generated method stub
		return isSelected;
	}

	public boolean isVariable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void select(boolean weiter) {
		// TODO Auto-generated method stub
		isSelected = true;
		
		
		if (weiter)
	
		{	
		String var = new StringTokenizer(name,".").nextToken();
	
		for (int i =0; i < seurat.dataManager.Experiments.size(); i++) {
			if (seurat.dataManager.Experiments.elementAt(i).name.equals(var)) seurat.dataManager.Experiments.elementAt(i).select(false);
		}
		}
		
		
		
if (weiter) {
		
			
			for (int i =0; i < vars.size(); i++) {
				vars.elementAt(i).select(false);
			}
			
			}



		
	}

	public void unselect(boolean weiter) {
		// TODO Auto-generated method stub
		isSelected = false;
		

		if (weiter) {
		String var = new StringTokenizer(name,".").nextToken();
	
		for (int i =0; i < seurat.dataManager.Experiments.size(); i++) {
			if (seurat.dataManager.Experiments.elementAt(i).name.equals(var)) seurat.dataManager.Experiments.elementAt(i).unselect(false);
		}
		
		}
		
		
		
		if (weiter) {
		
			
			for (int i =0; i < vars.size(); i++) {
				vars.elementAt(i).unselect(false);
			}
			
			}
			
		
		
		
		
		
	}
	
	
	
	public void calculateMean() {
		mean = 0;
		int len = 0;
		max = 0;
		min = 0;
		
		for (int i = 0; i < this.doubleData.length; i++) {
			if (doubleData[i] != NA) {
				mean += doubleData[i];
			    len ++;
			    if (max < doubleData [i]) max = doubleData [i];
			    if (min > doubleData [i]) min = doubleData [i];
			}
		}
		mean /= len;

	}
	
	
	
	

	public double[] getColumn(Vector<ISelectable> cols) {
		// TODO Auto-generated method stub
		double [] column = new double [cols.size()];
		for (int i = 0; i < column.length;i++) {
			if (doubleData [cols.elementAt(i).getID()] != seurat.dataManager.NA) column [i] = doubleData [cols.elementAt(i).getID()];
			
		}
		return column;
	}

	public double[] getRow(Vector<ISelectable> rows) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getMax() {
		// TODO Auto-generated method stub
		return max;
	}
	
	public double getMin() {
		// TODO Auto-generated method stub
		return min;
	}
	
	
	

}