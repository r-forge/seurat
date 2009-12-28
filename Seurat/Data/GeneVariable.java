package Data;
import java.awt.Color;
import java.util.*;

import GUI.IPlot;



public class GeneVariable implements ISelectable{
	public String name;

	public int type; // Double,String

	 public boolean isDouble = true;

	public boolean isDiscrete = false;
	
	public boolean isList = false;
	

	public final double NA = Math.PI;

	public Vector<String> stringBuffer = new Vector();

	public static final int Double = 1, String = 2;

	public double[] doubleData;

	public String[] stringData;
	
	public Vector<AnnGene> AnnGenes;
	
	public int [] bufferCount; 
	
	public Vector<Vector<Integer>> geneMitListValue;
	
    public boolean isLink = false;
	
    public boolean isChromosome = false;
    

	public GeneVariable(String name, int type, Vector variables) {
		this.name = name;
		this.type = type;
		this.AnnGenes = variables;
	}

	
	public boolean testList() {
		if (name.contains("#")) {
				isList = true;
				return true;
			
		}
	    return false;
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
	
	
	
	
	public void sortBuffer() {
		
		if (isChromosome) {
			sortChromosomes();
			return;
		}

		Vector<String> newBuffer = new Vector();
		for (int i = 0; i < stringBuffer.size(); i++) {
			String s = stringBuffer.elementAt(i);
			int j = 0;
			while (j < newBuffer.size()
					&& compareLexico(s, newBuffer.elementAt(j))) {
				j++;
			}
			newBuffer.insertElementAt(s, j);

		}

		this.stringBuffer = newBuffer;
		
	}
	
	
	
	
	public void sortChromosomes() {

		Vector<String> newBuffer = new Vector();
		for (int i = 0; i < stringBuffer.size(); i++) {
			String s = stringBuffer.elementAt(i);
			int j = 0;
			while (j < newBuffer.size()
					&& compareCHRs(s, newBuffer.elementAt(j))) {
				j++;
			}
			newBuffer.insertElementAt(s, j);

		}
		
		
		this.stringBuffer = newBuffer;
		
		
		newBuffer = new Vector();
		for (int i = 0; i < stringBuffer.size(); i++) {
			String s = stringBuffer.elementAt(i);
			int j = 0;
			while (j < newBuffer.size()
					&& compareCHRs(s, newBuffer.elementAt(j))) {
				j++;
			}
			newBuffer.insertElementAt(s, j);

		}
		
		
		this.stringBuffer = newBuffer;
		
		for (int i = 0; i < stringBuffer.size(); i++) System.out.print(stringBuffer.elementAt(i) + " ");
			

		this.stringBuffer = newBuffer;
		
	}
	
	
	
	
	public boolean compareCHRs(String a, String b) {
		int i = 0;

		String tA = a.replace("\"","");
		String tB = b.replace("\"","");
		if (tA.equals("X") || tA.equals("x")) tA = "23";
		if (tB.equals("X") || tB.equals("x")) tB = "23";

		if (tA.equals("Y") || tA.equals("y")) tA = "24";
		if (tB.equals("Y") || tB.equals("y")) tB = "24";
		
		if (tA.equals("NA") || tB.equals("NA")) return true;
		
		
		int aa = Integer.parseInt(tA);
		int bb = Integer.parseInt(tB);
		
		if (aa < bb) return false;
		return true;
	}
	
	
	
	

	public boolean compareLexico(String a, String b) {
		int i = 0;

		String tA = a.replace("\"","");
		String tB = b.replace("\"","");
		if (tA.equals("X") || tA.equals("x")) tA = "23";
		if (tB.equals("X") || tB.equals("x")) tB = "23";

		if (tA.equals("Y") || tA.equals("y")) tA = "24";
		if (tB.equals("Y") || tB.equals("y")) tB = "24";
		
		if (tA.equals("NA") || tB.equals("NA")) return true;
		
		
		int aa = Integer.parseInt(tA);
		int bb = Integer.parseInt(tB);
		
		if (aa < bb) return false;
		return true;
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
		if (isList) return 3;
		if (isDouble) return 1;
		if (isDiscrete) return 2;
		
		
		return -1;
	}


	public double getValue(int id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public String [] getStringData() {
		// TODO Auto-generated method stub
		return stringData;
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


	public Vector<ISelectable> getVariables() {
		// TODO Auto-generated method stub
		
		Vector<ISelectable> v = new Vector();
		for (int i = 0; i < AnnGenes.size(); i++) {
			v.add(AnnGenes.elementAt(i));
		}
		return v;
	}


	public double[] getDoubleData() {
		// TODO Auto-generated method stub
		return doubleData;
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
