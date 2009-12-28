package GUI;

import java.util.Vector;

import Data.Chromosome;
import Data.Clone;
import Data.ClusterNode;
import Data.Clustering;
import Data.DataTreeNode;
import Data.Gene;
import Data.GeneVariable;
import Data.ISelectable;
import Data.Variable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;



public class ClusteringManager{

	Seurat seurat;
	
	JTree tree;
	
	DefaultMutableTreeNode root;
	DataTreeNode Rows = new DataTreeNode("Rows");
	DataTreeNode Columns= new DataTreeNode("Columns");

	
	Vector <Clustering> Clusterings = new Vector();
/*
	JPanel panel = new JPanel();
	JPanel p = new JPanel();
	
	JMenuBar menuBar = new JMenuBar();
	*/
	public ClusteringManager(Seurat seurat, JTree tree ,DefaultMutableTreeNode root ) {
       // super("ClusteringBrowser");
		this.seurat = seurat;
		this.root = root;
        this.tree = tree;
	//	this.getContentPane().setLayout(new BorderLayout());
		
		this.root = root;
		root.add(Rows);
		root.add(Columns);
		
		
		//tree = new JTree(root);
	/*
		for (int i = 0; i < seurat.dataManager.ExpClusters.size(); i++) {
			addClustering(seurat.dataManager.ExpClusters.elementAt(i),Samples);
		}
		
		
		for (int i = 0; i < seurat.dataManager.GeneClusters.size(); i++) {
			addClustering(seurat.dataManager.GeneClusters.elementAt(i),Genes);
		}
		*/
		
	/*	
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1 && e.getClickCount() == 2) {

					Object obj = tree.getLastSelectedPathComponent();

					if (obj instanceof DataTreeNode) {

						Clustering c = ((DataTreeNode)obj).cObject;
						
					//	createInfo(c);
					

					}
				}
			}
		};

		tree.addMouseListener(ml);
		
		
		
		
		
		
		
		
		
		
		
		this.setJMenuBar(menuBar);
		JMenu plots = new JMenu("Plots");
        menuBar.add(plots);
        
		JMenuItem item = new JMenuItem("Heatmap");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		plots.add(item);
		
		item = new JMenuItem("Confusion Matrix");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		plots.add(item);
		
		
		
		
		
		
		
		
		
		//panel.setLayout(new BorderLayout());
		//panel.setBorder(BorderFactory.createEtchedBorder());
		//this.getContentPane().add(panel,BorderLayout.EAST);
		
		
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(p,BorderLayout.CENTER);
		
		p.add(tree,BorderLayout.CENTER);
		setBounds(0,400,400,500);
		setVisible(true);
		
		*/
	}
	
	public void addRowsClustering(Clustering c) {
		Clusterings.add(c);
		DataTreeNode node = new DataTreeNode(c.name);
		node.cObject = c;
		
		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(
				node, Rows, Rows.getChildCount());

		tree.scrollPathToVisible(new TreePath(Rows.getPath()));
	}
	
	
	
	public void addRowsClustering(ClusterNode c) {
		DataTreeNode node = new DataTreeNode(c.name);
		node.cObject = c;
		
		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(
				node, Rows, Rows.getChildCount());

		tree.scrollPathToVisible(new TreePath(Rows.getPath()));
	}
	
	
	
	
	public void addColumnsClustering(Clustering c) {
		Clusterings.add(c);
		DataTreeNode node = new DataTreeNode(c.name);
		node.cObject = c;
		
		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(
				node, Columns, Columns.getChildCount());

		tree.scrollPathToVisible(new TreePath(Columns.getPath()));
		
	}
	
	
	
	public void addColumnsClustering(ClusterNode c) {
		
		DataTreeNode node = new DataTreeNode(c.name);
		node.cObject = c;
		
		
		((DefaultTreeModel) tree.getModel()).insertNodeInto(
				node, Columns, Columns.getChildCount());

		tree.scrollPathToVisible(new TreePath(Columns.getPath()));
		
	}
	
	
	
	
	
	
	
}


