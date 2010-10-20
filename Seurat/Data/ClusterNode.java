package Data;
import java.util.*;

import Tools.Tools;

public class ClusterNode {

	public ClusterNode nodeR;

	public ClusterNode nodeL;

	//public Vector<Integer> cases;

	public int ClusterNumber = 1;


	public double currentHeight;
	
	public Cluster cluster;
	
	public CoordinateNode cNode;
	
	public String name;
	
	public boolean isRows;
	
	public boolean firstOrder = true;
	
	
	
	public ClusterNode(Vector<ISelectable> cases) {
		this.cluster = new Cluster(cases,null);
	}


	
	
	
	public void calculateHeight(double height) {
		currentHeight/=height;
		if (nodeR!= null) nodeR.calculateHeight(height);
		if (nodeL!= null) nodeL.calculateHeight(height);
		
	}



	public Vector<ClusterNode> getLeafList() {

		if (nodeR == null && nodeR == null) {
			Vector List = new Vector();
			List.add(this);
			return List;
		} else {
			Vector List = nodeL.getLeafList();
			Vector List2 = nodeR.getLeafList();

			for (int i = 0; i < List2.size(); i++) {
				List.add(List2.elementAt(i));
			}

			return List;
		}

	}
	

	public Vector<ISelectable> getOrder() {

		if (nodeR == null && nodeL == null) {
			Vector<ISelectable> order = new Vector();
			
			
			order.add(cluster.items.firstElement());
			return order;
		}

		Vector orderR = nodeR.getOrder();
		Vector orderL = nodeL.getOrder();

		for (int i = 0; i < orderR.size(); i++) {
			orderL.add(orderR.elementAt(i));
		}

		return orderL;
	}

	
	
	public ClusterNode cut(double H) {
		if (currentHeight >= H) {
		   ClusterNode node = copy();
		   node.nodeR = node.nodeR.cut(H);
		   node.nodeL = node.nodeL.cut(H);
		   return node;
		}
		else {
			return null;
		}
		
		
		
		
	}
	
	
	public Vector<ISelectable> getFirstOrder() {

		if (firstOrder) {
		
		
			Vector<ISelectable> order = getOrder();
			
			
		firstOrder = false;
		return order;
		}
		return cluster.items;
	}
	
	
	
	
	
	
	
	
	public ClusterNode(int ClusterNumber, ISelectable item) {
		this.ClusterNumber = ClusterNumber;
		cluster = new Cluster(new Vector(),null);
		cluster.items.add(item);
	}

	public ClusterNode(int ClusterNumber, ClusterNode nodeR, ClusterNode nodeL) {
		this.ClusterNumber = ClusterNumber;
		this.nodeR = nodeR;
		this.nodeL = nodeL;
		this.loadCases();
	}

	public void loadCases() {
		cluster = new Cluster(new Vector(),null);
		for (int i = 0; i < nodeR.cluster.items.size(); i++) {
			cluster.items.add(nodeR.cluster.items.elementAt(i));
		}
	
		for (int i = 0; i < nodeL.cluster.items.size(); i++) {
			cluster.items.add(nodeL.cluster.items.elementAt(i));
		}
		
	}

	
	public void permute() {
		ClusterNode c = nodeR;
		nodeR = nodeL;
		nodeL = c;
	}
	
	
	public ClusterNode copy() {
		ClusterNode node = new ClusterNode(cluster.items);
		
		if (nodeR != null) node.nodeR = nodeR.copy(); 
		if (nodeL != null) node.nodeL = nodeL.copy(); 
		
		
		
		node.ClusterNumber = ClusterNumber;


		node.currentHeight = currentHeight;
		
		node.cluster = cluster;
		
		node.cNode = cNode;
		
		node.name = name;
		
		node.isRows = isRows;
		
		node.firstOrder = firstOrder;
		
		return node;
		
	}
	
	

	public int getTiefe() {
		if (nodeL == null)
			return 1;
		else
			return Math.max(nodeL.getTiefe() + 1, nodeR.getTiefe() + 1);
	}
	
	
	// returns all nodes in the tree not children
	public Vector<ClusterNode> getParents() {
		if (nodeL != null && nodeR != null) {
			Vector v = Tools.mergeVectors(nodeL.getParents(),nodeR.getParents());
			v.add(this);
			return v;
		}	
		
		return new Vector();
	}
	
	
	
	
	
	
	
	public boolean isSelected() {
		for (int i = 0; i < cluster.items.size(); i++) {
			
			if (cluster.items.elementAt(i).isSelected()) return true;
		}
		return false;
	}
	
	
	
	public void selectNode() {
		
		
		
		
		for (int i = 0; i < this.cluster.items.size(); i++) {
			cluster.items.elementAt(i).select(true);
			
		}
			
		
	}
	

}
