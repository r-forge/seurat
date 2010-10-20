package Data;

import java.util.Vector;

public class Cluster {

	public Vector<ISelectable> items;
	
	public int ID;
	
	public int tempID;
	
	public String name;
	
	public Cluster(Vector<ISelectable> items, String name) {
		this.items = items;
		this.name = name;
	}
	
	public void select() {
		for (int i = 0; i < items.size(); i++) {
			items.elementAt(i).select(true);
		} 
	}
	
}
