package Data;

import java.util.Vector;

public class Bicluster {
	
	public Vector<ISelectable> rows;
	public Vector<ISelectable> columns;
	public String name;

	public Bicluster(String name, Vector<ISelectable> rows, Vector<ISelectable> colums) {
		this.name = name;
		this.rows =rows;
	    this.columns = colums;
	   	}
}
