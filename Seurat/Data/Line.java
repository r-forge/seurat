package Data;

public class Line{
	
	public int x1,x2,y1,y2;
	
  public Line(int x1,int y1, int x2, int y2) {
	  this.x1 = x1;
	  this.y2 = y2;
	  this.x2 = x2;
	  this.y1 = y1;
  }	
  
  
  public int getDistanceTo(int x, int y) {
	 
	int a = 100000;
	
	
	if ((y -y1)*(y-y2)<=0 && (y1!=y2)) a = (x-x1) * (x-x1); 
	if ((x -x1)*(x-x2)<=0 && (x1!=x2)) a = (y-y1) * (y-y1); 
	
	  
	return  Math.min(a, Math.min(((x1-x)*(x1-x) + (y1-y)*(y1-y)) , ((x2-x)*(x2-x) + (y2-y)*(y2-y)) ));
  }
  
  
  
  
}