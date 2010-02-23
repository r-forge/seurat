package Data;

import javax.swing.tree.DefaultMutableTreeNode;

public class DataTreeNode extends DefaultMutableTreeNode{

    public ISelectable object;
    
    
    /**TYPE = 1 for Histogram*/
    /**TYPE = 2 for Barchart*/
    /**TYPE = 3 for Listenplot*/
    /**TYPE = 4 for clustering*/
    public int TYPE;
    
    public String name;
    
    public Object cObject;
    
    
    public boolean datasetVar = false;
    
    
    public DataTreeNode(ISelectable object) {
    	super(object.getName());
    	this.object = object;
    	this.name = object.getName();
    	TYPE = object.getType();
    }
    
    
    public DataTreeNode(ISelectable object, boolean datasetVar) {
    	super(object.getName());
    	this.object = object;
    	this.name = object.getName();
    	TYPE = object.getType();
    	this.datasetVar = datasetVar;
    }
    
    
    
    
    public DataTreeNode(String name) {
    	super(name);
    	this.name = name;
    }
	
	
}
