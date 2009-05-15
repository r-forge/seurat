package Data;

import java.util.Vector;

public class Chromosome {
	
	public Vector<Clone> Clones = new Vector();
	public String name;
	
	public Chromosome(String name) {
		this.name = name;
	}

}
