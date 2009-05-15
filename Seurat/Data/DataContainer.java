package Data;
import java.util.*;


public class DataContainer {
	Vector<Vector> data;
     public DataContainer(int col, int rows) {
    	 data = new Vector<Vector>();
    	 for (int i =0; i < col; i++) {
    		 Vector<Float> v = new Vector<Float>();
    		 for (int j = 0; j < rows; j++) {
    			 v.add(new Float(0));
    		 }
    		 data.add(v);
    	 }
     }
     
     public float get(int col,int row) {
    	 return (Float)data.elementAt(col).elementAt(row);
     }
     
     public void set(int col,int row, float value) {
    	 data.elementAt(col).set(row,value);
     }
     
     
}
