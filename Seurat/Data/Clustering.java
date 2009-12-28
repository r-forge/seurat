package Data;

import java.util.Vector;

public class Clustering {
	
	public Vector<Vector<ISelectable>> clusters;
	
	public Vector<String> Names;
	
	public String name;
	
	public boolean isRows; 
	
	public Clustering(String name, Vector<Vector<ISelectable>> clusters,Vector<String> Names, boolean rows) {
		this.name = name;
		this.clusters = clusters;
		this.Names = Names;
		isRows = rows;
	}
	

}
