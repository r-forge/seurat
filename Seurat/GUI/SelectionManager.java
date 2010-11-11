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
import Data.DescriptionVariable;
import Data.Gene;
import Data.GeneVariable;
import Data.Variable;

public class SelectionManager {
	
	
    Seurat seurat;
	
	JTree tree;
	
	DefaultMutableTreeNode root;
	DataTreeNode Genes;
	DataTreeNode Samples;
	DataTreeNode Clones;
	
	
	public SelectionManager(Seurat seurat, JTree tree ,DefaultMutableTreeNode root ) {
	     
			this.seurat = seurat;
			this.root = root;
	        this.tree = tree;
	
	}		
	
	
	public void loadGenes(GeneVariable var) {
		System.out.println("LoadGenes");
	}
    public void loadClones(GeneVariable var) {
    	System.out.println("LoadClones");
	}
    public void loadSamples(GeneVariable var) {
    	System.out.println("LoadSamples");
    } 
	
	
	
	public void addObjects(GeneVariable var) {
		
        if (Genes == null) {
        	Genes = new DataTreeNode("Genes");
	        root.add(Genes);
        }
        if (Samples == null) {
        	Samples = new DataTreeNode("Samples");
        	root.add(Samples);
        }
        if (Clones == null) {
        	  Clones= new DataTreeNode("Clones");
        	  if (seurat.snpLoaded) Clones = new DataTreeNode("SNPs");
        	  root.add(Clones);
        }
		
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
			if (g.isSelected()) addGene(g,var);
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
	
	public void addGene(Gene g, GeneVariable var) {
		
		String name = g.getName();
		if (var != null && g.annGene != null) {
			
			name = var.getStringData(g.annGene.ID);
		}
		
		DataTreeNode node = new DataTreeNode(g,name);
		
		node.cObject = g;		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(node, Genes, Genes.getChildCount());
		node.setParent(Genes);
		tree.scrollPathToVisible(new TreePath(Genes.getPath()));
		
	}
	
    public void addClone(Clone c) {
		
		DataTreeNode node = new DataTreeNode(c);
		node.cObject = c;		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(node, Clones, Clones.getChildCount());
		tree.scrollPathToVisible(new TreePath(Clones.getPath()));
		
	}
    
    
  public void addSample(Variable c) {
		
		DataTreeNode node = new DataTreeNode(c);
		node.cObject = c;	
		((DefaultTreeModel) tree.getModel()).insertNodeInto(node, Samples, Samples.getChildCount());
		tree.scrollPathToVisible(new TreePath(Samples.getPath()));
	}
	
	
	

	
	

}
