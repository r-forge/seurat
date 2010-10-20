package Data;

import java.util.Vector;

public class Clustering {
	
	public Vector<Cluster> clusters;
	
	public String name;
	
	public boolean isRows;
	
	public ClusterNode node;
	
	public Clustering(String name, Vector<Vector<ISelectable>> clusters,Vector<String> Names, boolean rows) {
		this.name = name;
		
		this.clusters = new Vector();
		for (int i = 0; i < clusters.size(); i++) {
		    this.clusters.add(new Cluster(clusters.elementAt(i),Names.elementAt(i)));	
		}
		
		isRows = rows;
	}
	
	
	
	public Clustering(String name, boolean rows, Vector<Cluster> clusters,Vector<String> Names) {
		this.name = name;	
		this.clusters = clusters;
		for (int i = 0; i < clusters.size(); i++) {
			clusters.elementAt(i).name = Names.elementAt(i);
		}
		
		isRows = rows;
	}
	
	
	
	
	public Clustering(String name, boolean rows, Vector<Cluster> clusters) {
		this.name = name;	
		this.clusters = clusters;
		isRows = rows;
	}
	
	
	
	public Clustering(String name, ClusterNode node, boolean rows) {
		this.name = name;
		this.node = node;
		isRows = rows;
		clusters = new Vector();
		Vector<ClusterNode> nodes = node.getLeafList();
		for (int i = 0; i < nodes.size(); i++) {
			clusters.add(nodes.elementAt(i).cluster);
		}
		
		
	}
	
	
	
	
    /*returns a list of all possible permutations for the given clustering**/	
	public Vector<Permutation> getAllPermutations() {
		Vector per = new Vector();
	    // K-Means
		if (node != null) {
			Vector<ClusterNode> nodes = node.getParents();
			for (int i = 0; i < nodes.size(); i++) {
				per.add(new Permutation(i));
			}
		}	
		else {
			// HClustering 
			
			for (int i = 0; i < clusters.size()-1; i++ ) {
				for (int j = i+1; j < clusters.size(); j++ ) {
					per.add(new Permutation(i,j));
				}
			}
			
			
			for (int i = 1; i < clusters.size(); i++ ) {
				
					per.add(new Permutation(i,i));
				
			}
			
			
		}
		
		per.add(null);
		
		return per;
	}
	
	// makes given permutation
	public Clustering permute(Permutation per) {
	
		Clustering c = this.copy();
		if (per == null) return c; 
		if (per.j == -1) {
			c.node.getParents().elementAt(per.i).permute();		
			c.clusters = new Vector();
			Vector<ClusterNode> nodes = c.node.getLeafList();
			for (int i = 0; i < nodes.size(); i++) c.clusters.add(nodes.elementAt(i).cluster);
		}
		else {
			if (per.i != per.j){
			Cluster v = c.clusters.elementAt(per.i);
			c.clusters.setElementAt(c.clusters.elementAt(per.j),per.i);
			c.clusters.setElementAt(v,per.j);
			}
			else {
				for (int i = 0; i < per.i; i++) {
					c.clusters.add(clusters.elementAt(i));
				}
				for (int i = 0; i < per.i; i++) {
					c.clusters.remove(0);
				}
			}
		}
	
	
		return c;
		
	}
	
	
	
	
	
	
	public Clustering copy() {
		if (clusters != null) {
			Vector<Cluster> cs = new Vector();
			for (int i = 0; i < clusters.size(); i++) cs.add(clusters.elementAt(i));
			Clustering c = new Clustering(name, isRows, cs);
			if (node != null) c.node = node.copy();
			return c;
		} 
		else {
			return new Clustering(name, node.copy(), isRows);
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	

}
