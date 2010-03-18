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
	
	public String name;
	
	public boolean isRows;
	
	public boolean firstOrder = true;
	
	public ClusterNode(Vector<ISelectable> cases) {
		this.Cases = cases;
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

	
	
	public Vector<ISelectable> getFirstOrder() {

		if (firstOrder) {
		
		
			Vector<ISelectable> order = getOrder();
			
			
		firstOrder = false;
		return order;
		}
		return Cases;
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

	

	public int getTiefe() {
		if (nodeL == null)
			return 1;
		else
			return Math.max(nodeL.getTiefe() + 1, nodeR.getTiefe() + 1);
	}
	
	
	
	
	
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
