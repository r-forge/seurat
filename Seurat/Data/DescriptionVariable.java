package Data;
import java.util.*;

public class DescriptionVariable {
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

}
