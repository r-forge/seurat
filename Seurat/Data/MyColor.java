package Data;

import java.awt.Color;

public class MyColor {
    
	public double x,y,z;
    
    public MyColor(double xx,double yy,double zz) {
    	this.x = xx;
    	this.y = yy;
    	this.z = zz;
    }
    
    public Color getRGBColor() {
    	
    	if (x >1 ) x = 1;
    	if (y > 1) y = 1;
    	if (z > 1) z = 1;
    	
    	if (x  < 0 ) x = 0;
    	if (y  < 0) y = 0;
    	if (z  < 0) z = 0;
    
    	return new Color((float)x,(float)y,(float)z);
    }
    
    
}
