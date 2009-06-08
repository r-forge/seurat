package Data;
import java.util.*;


public class CoordinateNode {
      public ClusterNode node;
      public Vector<Line> Lines = new Vector();
      
      public boolean isSelected = false;
      
      public CoordinateNode(ClusterNode node, int x1, int y1, int x2, int y2) {
    	  this.node = node;
    	  Lines.add(new Line(x1,y1,x2,y2));
      }
      
      
      public int getDistance(int x, int y) {
    	  int distance = 10000;
    	  for (int i = 0; i < Lines.size(); i++) {
    		  Line line = Lines.elementAt(i);
    		  distance = Math.min(distance, line.getDistanceTo(x,y));
    	  }
    	  
    	  
    	  return distance;
      }
}



