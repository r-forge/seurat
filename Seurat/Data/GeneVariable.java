package Data;
import java.util.*;

public class GeneVariable {
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
	
	public Vector<ISelectable> variables;
	
	public int [] bufferCount; 
	
	public Vector<Vector<Integer>> geneMitListValue;
	

	public GeneVariable(String name, int type, Vector variables) {
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
	
	
	

}
