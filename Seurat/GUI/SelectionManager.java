package GUI;

import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import Data.Biclustering;
import Data.Clone;
import Data.Clustering;
import Data.DataTreeNode;
import Data.Gene;
import Data.Variable;

public class SelectionManager {
	
	
    Seurat seurat;
	
	JTree tree;
	
	DefaultMutableTreeNode root;
	DataTreeNode Genes = new DataTreeNode("Genes");
	DataTreeNode Samples = new DataTreeNode("Samples");
	DataTreeNode Clones= new DataTreeNode("Clones");
	
	
	public SelectionManager(Seurat seurat, JTree tree ,DefaultMutableTreeNode root ) {
	       // super("ClusteringBrowser");
			this.seurat = seurat;
			this.root = root;
	        this.tree = tree;
		//	this.getContentPane().setLayout(new BorderLayout());
			
	        
	        root.add(Samples);
	        root.add(Genes);
			root.add(Clones);
	}		
	
	
	public void addObjects() {
		

		int l = Genes.getChildCount();
		
        for (int i = 0; i < l; i++) {
        	DefaultMutableTreeNode c = Genes.getLastLeaf(); 
        	((DefaultTreeModel)tree.getModel()).removeNodeFromParent(c);	           
       }
	   
      l = Clones.getChildCount();
		
        for (int i = 0; i < l; i++) {
        	DefaultMutableTreeNode c = Clones.getLastLeaf(); 
        	((DefaultTreeModel)tree.getModel()).removeNodeFromParent(c);	           
       }
        
        
      l = Samples.getChildCount();
		
        for (int i = 0; i < l; i++) {
        	DefaultMutableTreeNode c = Samples.getLastLeaf(); 
        	((DefaultTreeModel)tree.getModel()).removeNodeFromParent(c);	           
       }
		
		
		
		
		for (int i = 0; i < seurat.dataManager.Genes.size(); i++) {
			Gene g = seurat.dataManager.Genes.elementAt(i);
			if (g.isSelected()) addGene(g);
		}
		
		
		for (int i = 0; i < seurat.dataManager.Experiments.size(); i++) {
			Variable g = seurat.dataManager.Experiments.elementAt(i);
			if (g.isSelected()) addSample(g);
		}
		
		
		if (seurat.dataManager.CLONES!=null) {
			for (int i = 0; i < seurat.dataManager.CLONES.size(); i++) {
				Clone g = seurat.dataManager.CLONES.elementAt(i);
				if (g.isSelected()) addClone(g);
			}
		}
		
	
	}
	
	public void addGene(Gene g) {
		
		DataTreeNode node = new DataTreeNode(g);
		node.cObject = g;
		
		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(
				node, Genes, Genes.getChildCount());
		node.setParent(Genes);

		tree.scrollPathToVisible(new TreePath(Genes.getPath()));
	}
	
    public void addClone(Clone c) {
		
		DataTreeNode node = new DataTreeNode(c);
		node.cObject = c;
		
		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(
				node, Clones, Clones.getChildCount());

		tree.scrollPathToVisible(new TreePath(Clones.getPath()));
	}
    
    
  public void addSample(Variable c) {
		
		DataTreeNode node = new DataTreeNode(c);
		node.cObject = c;
		
		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(
				node, Samples, Samples.getChildCount());

		tree.scrollPathToVisible(new TreePath(Samples.getPath()));
	}
	
	
	

	
	

}
