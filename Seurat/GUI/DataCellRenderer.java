package GUI;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.sun.org.apache.xpath.internal.operations.Variable;

import Data.Chromosome;
import Data.Clone;
import Data.ClusterNode;
import Data.Clustering;
import Data.DataTreeNode;
import Data.Gene;


public class DataCellRenderer extends DefaultTreeCellRenderer{
	 Icon numIcon,discreteIcon,geneIcon,geneIconS,chrIcon,cloneIcon,cloneIconS,expIcon, chrIconS,hclustIcon;



	    public DataCellRenderer(Icon numIcon,Icon discreteIcon,Icon geneIcon,Icon geneIconS,Icon chrIcon,Icon chrIconS,Icon cloneIcon,Icon cloneIconS,Icon expIcon,Icon hclustIcon) {

	        this.numIcon = numIcon;
	        this.discreteIcon = discreteIcon;
            this.geneIcon = geneIcon;
            this.geneIconS = geneIconS;
            this.chrIcon = chrIcon;
            this.chrIconS = chrIconS;
            this.cloneIcon = cloneIcon;
            this.cloneIconS = cloneIconS;
            this.expIcon = expIcon;
            this.hclustIcon = hclustIcon;
	        
	    }



	    public Component getTreeCellRendererComponent(

	                        JTree tree,

	                        Object value,

	                        boolean sel,

	                        boolean expanded,

	                        boolean leaf,

	                        int row,

	                        boolean hasFocus) {



	        super.getTreeCellRendererComponent(

	                        tree, value, sel,

	                        expanded, leaf, row,

	                        hasFocus);

	        if (leaf) {
	        	
	        	if (getType(value) == 1) {
	        		setIcon(numIcon);
	        		return this;
	        	}
	        	if (getType(value) == 2) {
	        		setIcon(discreteIcon);
	        		return this;
	        	}
	        	
	        	if (value instanceof DataTreeNode && ((DataTreeNode)value).object instanceof Clone) {
	        		setIcon(cloneIconS);
	        	    return this;
	        	}
	        	
	        	
	        	if (value instanceof DataTreeNode && (((DataTreeNode)value).cObject!=null)&&((DataTreeNode)value).cObject instanceof Clustering) {
	        		setIcon(discreteIcon);
	        	    return this;
	        	}
	        	
	        	
	        	if (value instanceof DataTreeNode && (((DataTreeNode)value).cObject!=null)&& ((DataTreeNode)value).cObject instanceof ClusterNode) {
	        		setIcon(hclustIcon);
	        	    return this;
	        	}
	        	
	        	if (value instanceof DataTreeNode && ((DataTreeNode)value).object instanceof Gene) {
	        		setIcon(geneIconS);
	        	    return this;
	        	}
	        	
	        	
	        	if (value instanceof DataTreeNode && ((DataTreeNode)value).object instanceof Chromosome) {
	        		setIcon(chrIconS);
	        	    return this;
	        	}
	        	
	        	
	        	
	        setIcon(null);

	      //      setToolTipText("This book is in the Tutorial series.");

	        } else {
	        	
	        	if (value instanceof DataTreeNode) {
	        		
	        	//	System.out.println("Object  "+((DataTreeNode)value).name);
	        		
	        		Object o = ((DataTreeNode)value).object;
	        		
	        		
	        		
	        		if (((DataTreeNode)value).name.equals("Genes")) {
	        			setIcon(geneIcon);
	        			return this;
	        		}
	        		if (((DataTreeNode)value).name.equals("Clones")) {
	        			setIcon(cloneIcon);
	        	        return this;
	        		}
	        		if (((DataTreeNode)value).name.equals("Samples")) {
	        			setIcon(expIcon);
	        			return this;
	        		}
	        		if (((DataTreeNode)value).name.equals("Chromosomes")) {
	        			setIcon(chrIcon);
	        			return this;
	        		}
	        		setIcon(null);
	        	}

	        	else setIcon(null);
	            setToolTipText(null); //no tool tip

	        }

	        return this;

	    }



	    protected int getType(Object value) {

	    	if (value instanceof DataTreeNode) {
    	        DataTreeNode node = (DataTreeNode)value;

     	       return node.TYPE;
	    	}
	    	return -1;
     	       
	    }

}
