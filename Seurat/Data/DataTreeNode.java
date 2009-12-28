package Data;

import javax.swing.tree.DefaultMutableTreeNode;

public class DataTreeNode extends DefaultMutableTreeNode{

    public ISelectable object;
    
    
    
    /**TYPE = 4 for clustering*/
    public int TYPE;
    
    public String name;
    
    public Object cObject;
    
    public DataTreeNode(ISelectable object) {
    	super(object.getName());
    	this.object = object;
    	this.name = object.getName();
    	TYPE = object.getType();
    }
    
    
    public DataTreeNode(String name) {
    	super(name);
    	this.name = name;
    }
	
	
}
