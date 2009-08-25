package Data;
import java.util.*;

public class ClusterNode {

	public ClusterNode nodeR;

	public ClusterNode nodeL;

	//public Vector<Integer> cases;

	public int ClusterNumber = 1;


	public double currentHeight;
	
	public Vector<ISelectable> Cases;
	
	public CoordinateNode cNode;
	
	
	public ClusterNode(Vector<ISelectable> cases) {
		this.Cases = cases;
	}


	
	/*
	public void calculateWeights() {

		if (nodeR == null && nodeR == null) {
		} else {
			Weight = 0;
			nodeR.calculateWeights();
			Weight += nodeR.Weight * nodeR.ClusterNumber;

			nodeL.calculateWeights();
			Weight += nodeL.Weight * nodeL.ClusterNumber;

			ClusterNumber = nodeR.ClusterNumber + nodeL.ClusterNumber;
			Weight /= ClusterNumber;
			// System.out.println(Weight + " "+ ClusterNumber);

		}

   }
	
	*/
	
	
	
	public void calculateHeight(double height) {
		currentHeight/=height;
		if (nodeR!= null) nodeR.calculateHeight(height);
		if (nodeL!= null) nodeL.calculateHeight(height);
		
	}

	/*

	public void sort() {
		if (nodeR == null && nodeR == null) {
		} else {
			double wR = nodeR.Weight;
			double wL = nodeL.Weight;
			System.out.println(wR + "  " + wL);
			if (wR < wL) {

				ClusterNode temp = nodeR;
				System.out.println("UMTAUSgrCHEN");
				nodeR = nodeL;
				nodeL = temp;

			}

			wR = nodeR.Weight;
			wL = nodeL.Weight;
			System.out.println(wR + "  " + wL);
			System.out.println();

			nodeR.sort();
			nodeL.sort();

		}

	}*/
	
	
/*
	public void printWeights(String s) {
		if (nodeR == null && nodeR == null) {
		} else {

			System.out.println(nodeR.Weight + "  XXX   " + nodeL.Weight);

			nodeR.printWeights("  " + s);
			nodeL.printWeights("  " + s);

		}

	}*/

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
	
	/*

	public void output(String s) {
		System.out.println();
		for (int i = 0; i < cases.size(); i++) {
			System.out.print(s + cases.elementAt(i) + " ");
		}
		System.out.println();
		System.out.print(s + "Left: ");
		if (this.nodeL != null)
			nodeL.output(s + "  ");
		else
			System.out.println(s + "null");
		System.out.print(s + "Right: ");
		if (this.nodeR != null)
			nodeR.output(s + "  ");
		else
			System.out.println(s + "null");
	}
	
	
	*/

	public Vector<ISelectable> getOrder() {

		if (nodeR == null && nodeL == null) {
			Vector<ISelectable> order = new Vector();
			
			
			order.add(Cases.firstElement());
			return order;
		}

		Vector orderR = nodeR.getOrder();
		Vector orderL = nodeL.getOrder();

		for (int i = 0; i < orderR.size(); i++) {
			orderL.add(orderR.elementAt(i));
		}

		return orderL;
	}

	
	public ClusterNode(int ClusterNumber, ISelectable item) {
		this.ClusterNumber = ClusterNumber;
		Cases = new Vector();
		Cases.add(item);
	}

	public ClusterNode(int ClusterNumber, ClusterNode nodeR, ClusterNode nodeL) {
		this.ClusterNumber = ClusterNumber;
		this.nodeR = nodeR;
		this.nodeL = nodeL;
		this.loadCases();
	}

	public void loadCases() {
		Cases = new Vector();
		for (int i = 0; i < nodeR.Cases.size(); i++) {
			Cases.add(nodeR.Cases.elementAt(i));
		}
	
		for (int i = 0; i < nodeL.Cases.size(); i++) {
			Cases.add(nodeL.Cases.elementAt(i));
		}
		
	}

	
	/*
	public ClusterNode getClusterNode(int i) {
		if (nodeL == null) {
			for (int j = 0; j < Cases.size(); j++) {
				if (Cases.elementAt(j) == i)
					return this;

			}
			return null;
		}
		ClusterNode node = this.nodeL.getClusterNode(i);
		if (node == null)
			return nodeR.getClusterNode(i);
		return node;
	}*/

	public int getTiefe() {
		if (nodeL == null)
			return 1;
		else
			return Math.max(nodeL.getTiefe() + 1, nodeR.getTiefe() + 1);
	}
	
	/*

	public boolean isSelectedV() {
		for (int i = 0; i < cases.size(); i++) {
			int id = cases.elementAt(i);
			ISelectable var = Experiments.elementAt(id);
		//	for (int k = 0; k < var.isSelected.length; k++) {
			//	if (var.isSelected[k])
				//	return true;
			//}
			if (var.isSelected()) return true;
		}
		return false;
	}

	public boolean isSelectedG() {
		for (int i = 0; i < cases.size(); i++) {
			int row = cases.elementAt(i);
			

		//	System.out.println(row + "  "+Genes.elementAt(row));
			if (Genes.elementAt(row).isSelected()) return true;
		//	for (int k = 0; k < dataManager.getExperiments().size(); k++) {
			//	if (dataManager.getExperiments().elementAt(k).isSelected[row])
				//	return true;
			//}
		}
		return false;
	}
	*/
	
	
	
	public boolean isSelected() {
		for (int i = 0; i < Cases.size(); i++) {
			
			if (Cases.elementAt(i).isSelected()) return true;
		}
		return false;
	}
	
	
	
	public void selectNode() {
		
		
		
		
		for (int i = 0; i < this.Cases.size(); i++) {
			Cases.elementAt(i).select(true);
			
		}
			
		
	}
	

}
