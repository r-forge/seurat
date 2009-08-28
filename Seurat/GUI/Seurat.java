package GUI;

import Data.*;

import java.util.*;
import java.io.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;

import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.datatransfer.*;
import javax.imageio.*;

import Data.CGHVariable;
import Data.Chromosome;
import Data.DescriptionVariable;
import Data.Gene;
import Data.MyStringTokenizer;
import Data.Variable;

import java.util.zip.*;
import java.util.jar.*;

import RConnection.RConnectionManager;
import Settings.*;

import Data.*;

import java.util.*;
import java.io.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;

import javax.swing.table.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import javax.imageio.*;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import Data.Gene;
import Data.MyStringTokenizer;
import Data.Variable;

import java.util.zip.*;
import java.util.jar.*;

import RConnection.RConnectionManager;
import Settings.*;

public class Seurat extends JFrame implements ColorListener {

	JPanel MainPanel = new JPanel();

	JFrame WorksPanel = new JFrame();

	JMenuBar menuBar = new JMenuBar();

	JMenu fileMenu;

	JMenu optionsMenu;

	JMenu windowMenu;

	JMenu helpMenu;

	JMenu plotsMenu;

	Vector<JFrame> windows = new Vector();

	Seurat seurat = this;

	FileDialog fileDialog;

	CGHViewer cghViewer;

	VariablesTable variablesTable;

	ExperimentDescriptionFrame descriptionFrame;

	GeneDescriptionFrame geneFrame;

	PicCanvas logoIcon;

	PicCanvas contIcon, numIcon;

	ImageIcon contImageIcon, numImageIcon, geneImageIcon, cloneImageIcon,
			chrImageIcon, expImageIcon;

	public DataManager dataManager = new DataManager();

	Settings settings = new Settings();

	JScrollPane objectsPane;
	JScrollPane datasetsPane;
	JPanel middlePanel = new JPanel();

	// JTree objectsTree;
	// JTree datasetsTree;

	public JProgressBar progressBar = new JProgressBar();
	JLabel infoLabel;

	public JPanel infoPanel = new JPanel();

	DefaultMutableTreeNode topObj;
	DefaultMutableTreeNode topData;

	JTree tree;

	public final byte SYSTEM;
	public final byte WINDOWS = 0;
	public final byte MAC = 1;
	public final byte OTHERSYSTEM = 2;

	JMenuItem openCGHItem;
	JMenuItem openGeneExpressionItem;
	JMenuItem openDescriptionItem;
	JMenuItem openGeneAnnotationsItem;
	JMenuItem saveGeneExpressions;
	JMenuItem closeDataset;

	public Seurat() {
		super("Seurat");
		this.setBounds(0, 0, 330, 400);

		System.out.println((System.getProperties().getProperty("os.name")));

		if ((System.getProperties().getProperty("os.name"))
				.equals("Windows XP")
				|| (System.getProperties().getProperty("os.name"))
						.equals("Windows Vista"))
			this.SYSTEM = this.WINDOWS;
		else {
			if ((System.getProperties().getProperty("os.name"))
					.equals("Mac OS X"))
				this.SYSTEM = this.MAC;
			else
				this.SYSTEM = this.OTHERSYSTEM;
		}

		try {

			RConnectionManager rCon = new RConnectionManager();
			rCon.connectToR();

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Connection to R failed.");
		}

		loadMainWindow();

		/*
		 * 
		 * infoPanel.removeAll();
		 * infoLabel.setText("Loading Geneexpression dataset...");
		 * MainPanel.remove(infoPanel);
		 * infoPanel.add(progressBar,BorderLayout.CENTER);
		 * MainPanel.add(infoPanel,BorderLayout.SOUTH); seurat.setVisible(true);
		 * this.update(this.getGraphics());
		 * this.MainPanel.setBackground(Color.RED);
		 * JOptionPane.showMessageDialog(this, "Connection to R failed.");
		 * this.setSize(800,800); this.setVisible(true);
		 */

	}

	public void loadMainWindow() {
		fileMenu = new JMenu("File");
		optionsMenu = new JMenu("Options");
		helpMenu = new JMenu("Help");

		// this.setBounds(20,20,150,450);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(MainPanel, BorderLayout.CENTER);

		this.loadLogos();

		MainPanel.setLayout(new BorderLayout());
		// MainPanel.add(logoIcon, BorderLayout.CENTER);

		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Data");

		topObj = new DefaultMutableTreeNode("Objects");
		treeNode.add(topObj);
		topData = new DefaultMutableTreeNode("Datasets");
		treeNode.add(topData);

		// objectsPane.setBorder(BorderFactory.createEtchedBorder());
		// objectsPane.setBackground(Color.WHITE);
		// objectsPane.setPreferredSize(new Dimension(230,200));
		// datasetsPane.setBackground(Color.WHITE);
		// datasetsPane.setPreferredSize(new Dimension(230,200));
		middlePanel.setBackground(Color.WHITE);

		// datasetsPane.setBorder(BorderFactory.createEtchedBorder());
		middlePanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel west = new JPanel();
		west.setBorder(BorderFactory.createEtchedBorder());
		west.setBackground(Color.WHITE);
		// west.setPreferredSize(new Dimension(240,200));

		// west.setLayout(new GridLayout(2,1));
		// west.add(objectsPane);
		// west.add(datasetsPane);

		tree = new JTree(treeNode);
		DataCellRenderer renderer =

		new DataCellRenderer(numImageIcon, contImageIcon, geneImageIcon,
				chrImageIcon, cloneImageIcon, expImageIcon);

		tree.setCellRenderer(renderer);

		west.setLayout(new BorderLayout());
		west.add(new JScrollPane(tree), BorderLayout.CENTER);

		MainPanel.add(west, BorderLayout.CENTER);
		// MainPanel.add(datasetsPane, BorderLayout.EAST);
		// MainPanel.add(middlePanel, BorderLayout.CENTER);

		// infoPanel.setBackground(Color.WHITE);
		infoPanel.setPreferredSize(new Dimension(25, 25));

		infoPanel.setBorder(BorderFactory.createEtchedBorder());
		MainPanel.add(infoPanel, BorderLayout.SOUTH);

		infoLabel = new JLabel("");
		// infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(infoLabel, BorderLayout.WEST);

		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1 && e.getClickCount() == 2) {

					Object obj = tree.getLastSelectedPathComponent();

					if (obj instanceof DataTreeNode) {

						ISelectable object = ((DataTreeNode) obj).object;

						if (object.isVariable()) {

							if (((Variable) object).isExperiment)
								createExperimentInfo((Variable) object);
							return;
						}

						if (object.isGene()) {
							// System.out.println(object.getName() + "  "
							// +object.getType());

							createGeneInfo((Gene) object);
							return;
						}

					}

					// myDoubleClick(selRow, selPath);
					// obj = tree.getLastSelectedPathComponent();

					if (obj instanceof DataTreeNode) {
						ISelectable object = ((DataTreeNode) obj).object;

						if ((!object.isVariable() && !object.isGene())
								&& object.getType() == 1) {
							new Histogram(seurat, object);
						}

						if ((!object.isVariable() && !object.isGene())
								&& object.getType() == 2) {
							new Barchart(seurat, object);
						}
						// Liste
						if (object.getType() == 3) {

						}

						if (object instanceof Chromosome) {
							// System.out.println(object.getName() + "  "
							// +object.getType());

							// System.out.println("Chromosome");
							Vector Cases = seurat.dataManager.getStates();
							Vector<Chromosome> v = new Vector();
							v.add((Chromosome) object);

							//new ChrView(seurat, "Cariogramm", v, Cases);
							
							new ChrView(seurat, "Chromosome " + v.elementAt(0).name, v,
					    			Cases);
							return;
						}

						cleanMiddlePanel();

					}
				}
			}
		};

		tree.addMouseListener(ml);

		this.setJMenuBar(menuBar);

		/**
		 * ************************************************File
		 * Menu*************
		 * *****************************************************
		 */

		menuBar.add(fileMenu);

		openGeneExpressionItem = new JMenuItem("Open Gene Expression File");
		openGeneExpressionItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (dataManager.Experiments != null && shouldBeClosed()) {
				closeDataSet();
				
				
				seurat.openFile(null);
				// seurat.initVariablesTable();

				repaintWindows();
				return;
				}
				
				
				if (dataManager.Experiments == null) {
					
					
					
					seurat.openFile(null);
					// seurat.initVariablesTable();

					repaintWindows();
					}
				
				
				
			}
		});
		fileMenu.add(openGeneExpressionItem);

		openDescriptionItem = new JMenuItem("Open Clinical Data");
		openDescriptionItem.setEnabled(false);

		// openDescription.setEnabled(false);

		openDescriptionItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileDialog = new FileDialog(seurat, "Open Clinical Data", 0);
				fileDialog.setVisible(true);

				infoLabel.setText("Loading Clinical Data...");
				infoPanel.removeAll();
				infoPanel.add(progressBar, BorderLayout.CENTER);
				MainPanel.remove(infoPanel);
				MainPanel.add(infoPanel, BorderLayout.SOUTH);
				seurat.setVisible(true);
				seurat.update(seurat.getGraphics());

				if (fileDialog.getFile() != null) {
					descriptionFrame = new ExperimentDescriptionFrame(seurat,
							fileDialog, progressBar);

					DataTreeNode ClinicalData = new DataTreeNode(
							"Clinical Data");
					for (int i = 0; i < dataManager.descriptionVariables.size(); i++) {
						DescriptionVariable var = dataManager.descriptionVariables
								.elementAt(i);
						DataTreeNode Experiment = new DataTreeNode(var);
						ClinicalData.add(Experiment);

						infoLabel.setText("");
						infoPanel.remove(progressBar);
						MainPanel.remove(infoPanel);
						MainPanel.add(infoPanel, BorderLayout.SOUTH);
						seurat.update(seurat.getGraphics());

					}

					((DefaultTreeModel) tree.getModel()).insertNodeInto(
							ClinicalData, topData, topData.getChildCount());

					tree.scrollPathToVisible(new TreePath(topData.getPath()));
					

					repaintWindows();

					// topData.add(ClinicalData);

				}

				// new DescriptionFrame(amlTool);
			}
		});
		fileMenu.add(openDescriptionItem);

		openGeneAnnotationsItem = new JMenuItem("Open Gene Annotations");

		openGeneAnnotationsItem.setEnabled(false);

		// geneDescription.setEnabled(false);

		openGeneAnnotationsItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileDialog = new FileDialog(seurat, "Open Gene Annotations", 0);
				fileDialog.setVisible(true);

				if (fileDialog.getFile() != null) {

					infoLabel.setText("Loading Gene Annotations...");
					infoPanel.removeAll();
					MainPanel.remove(infoPanel);
					infoPanel.add(progressBar, BorderLayout.CENTER);
					MainPanel.add(infoPanel, BorderLayout.SOUTH);
					seurat.setVisible(true);
					seurat.update(seurat.getGraphics());

					geneFrame = new GeneDescriptionFrame(seurat, fileDialog,
							progressBar);

					DataTreeNode GeneAnnotations = new DataTreeNode(
							"GeneAnnotations");
					for (int i = 0; i < dataManager.geneVariables.size(); i++) {
						GeneVariable var = dataManager.geneVariables
								.elementAt(i);
						DataTreeNode Experiment = new DataTreeNode(var);
						GeneAnnotations.add(Experiment);

					}

					((DefaultTreeModel) tree.getModel()).insertNodeInto(
							GeneAnnotations, topData, topData.getChildCount());

					tree.scrollPathToVisible(new TreePath(topData.getPath()));

					/*
					 * ObjectTreeNode Chromosomes = new
					 * ObjectTreeNode("Chromosomes"); for (int i = 0; i <
					 * dataManager.Chromosomes.size(); i++) { Chromosome var =
					 * dataManager.Chromosomes.elementAt(i); ObjectTreeNode
					 * Experiment = new ObjectTreeNode(var);
					 * Chromosomes.add(Experiment); }
					 * 
					 * ((DefaultTreeModel)tree.getModel()).insertNodeInto(
					 * Chromosomes, topObj, topObj.getChildCount());
					 */

					

					tree.scrollPathToVisible(new TreePath(topObj.getPath()));

					infoLabel.setText("");
					infoPanel.remove(progressBar);
					MainPanel.remove(infoPanel);
					MainPanel.add(infoPanel, BorderLayout.SOUTH);
					seurat.update(seurat.getGraphics());

				}
				repaintWindows();

				// new DescriptionFrame(amlTool);
			}
		});
		fileMenu.add(openGeneAnnotationsItem);

		openCGHItem = new JMenuItem("Open CGH File");
		openCGHItem.setEnabled(false);

		openCGHItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileDialog = new FileDialog(seurat, "Open CGH File", 0);
				fileDialog.setVisible(true);

				if (fileDialog.getFile() != null) {

					infoLabel.setText("Loading CGH Data...");
					MainPanel.remove(infoPanel);
					infoPanel.removeAll();
					infoPanel.add(progressBar, BorderLayout.CENTER);
					MainPanel.add(infoPanel, BorderLayout.SOUTH);
					seurat.setVisible(true);
					seurat.update(seurat.getGraphics());

					cghViewer = new CGHViewer(seurat, fileDialog, progressBar);

					DataTreeNode CGHData = new DataTreeNode("CGHData");
					for (int i = 0; i < dataManager.cghVariables.size(); i++) {
						CGHVariable var = dataManager.cghVariables.elementAt(i);
						DataTreeNode Experiment = new DataTreeNode(var);
						CGHData.add(Experiment);

					}

					((DefaultTreeModel) tree.getModel()).insertNodeInto(
							CGHData, topData, topData.getChildCount());

					tree.scrollPathToVisible(new TreePath(topData.getPath()));

					DataTreeNode Clones = new DataTreeNode("Clones");
					for (int i = 0; i < dataManager.CLONES.size(); i++) {
						Clone var = dataManager.CLONES.elementAt(i);
						DataTreeNode Experiment = new DataTreeNode(var);
						Clones.add(Experiment);
					}

					((DefaultTreeModel) tree.getModel()).insertNodeInto(Clones,
							topObj, topObj.getChildCount());

					DataTreeNode Chromosomes = new DataTreeNode("Chromosomes");
					for (int i = 0; i < dataManager.Chromosomes.size(); i++) {
						Chromosome var = dataManager.Chromosomes.elementAt(i);
						DataTreeNode Experiment = new DataTreeNode(var);
						Chromosomes.add(Experiment);
					}

					((DefaultTreeModel) tree.getModel()).insertNodeInto(
							Chromosomes, topObj, topObj.getChildCount());

					tree.scrollPathToVisible(new TreePath(topObj.getPath()));

					

					infoLabel.setText("");
					infoPanel.remove(progressBar);
					MainPanel.remove(infoPanel);
					MainPanel.add(infoPanel, BorderLayout.SOUTH);
					seurat.update(seurat.getGraphics());

				}
				repaintWindows();
				// new DescriptionFrame(amlTool);
			}
		});
		fileMenu.add(openCGHItem);

		fileMenu.addSeparator();
		
		saveGeneExpressions = new JMenuItem("Save File");
		saveGeneExpressions.setEnabled(false);
		
		saveGeneExpressions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new SaveSelectionFrame(seurat);

			}
		});
		fileMenu.add(saveGeneExpressions);
		
		
		


		closeDataset = new JMenuItem("Close");
		closeDataset.setEnabled(false);
		closeDataset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				if (dataManager.Experiments != null && shouldBeClosed()) {
				closeDataSet();
				}
			}
		});
		fileMenu.add(closeDataset);

		
		fileMenu.addSeparator();
		
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (shouldExit()) System.exit(0);
			}
		});
		fileMenu.add(exitItem);

		JMenuItem item = new JMenuItem("Pixel Settings");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ColorDialog(seurat,seurat,settings.PixelW,settings.PixelH);
			}
		});
		optionsMenu.add(item);
		
		
		
		
		item = new JMenuItem("Color Settings");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ColorSettings(seurat);
			}
		});
		optionsMenu.add(item);
		
		
		optionsMenu.addSeparator();
		

		item = new JMenuItem("Clear all Colors");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Variable var = seurat.dataManager.Experiments.elementAt(0);
				while (var.getBarchartToColors().size() != 0) {
					var.getBarchartToColors().elementAt(0).removeColoring();
				}
				seurat.repaintWindows();

			}
		});
		optionsMenu.add(item);

		plotsMenu = new JMenu("Plots");
		menuBar.add(plotsMenu);

		item = new JMenuItem("Heatmap Genexpressions");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// {"ARSA","BBURCG","BBWRCG","TSP","Chen","MDS","HC","GW","OLO"};

				int[] orderSpalten = new int[dataManager.getExperiments()
						.size()];
				for (int i = 0; i < orderSpalten.length; i++) {
					orderSpalten[i] = i;
				}

				int[] orderZeilen = new int[dataManager.getRowCount()];
				for (int i = 0; i < orderZeilen.length; i++) {
					orderZeilen[i] = i;
				}

				Vector<Gene> Genes = new Vector();
				int[] order = new int[orderZeilen.length];
				for (int i = 0; i < orderZeilen.length; i++) {
					order[orderZeilen[i]] = i;
				}

				for (int i = 0; i < order.length; i++) {
					Genes.add(dataManager.Genes.elementAt(order[i]));
				}

				Vector<Variable> Experiments = new Vector();
				order = new int[orderSpalten.length];
				for (int i = 0; i < orderSpalten.length; i++) {
					order[orderSpalten[i]] = i;
				}

				for (int i = 0; i < order.length; i++) {
					Experiments
							.add(dataManager.Experiments.elementAt(order[i]));
				}

				GlobalView frame = new GlobalView(seurat, "GlobalView",
						Experiments, Genes, false);
				frame.applyNewPixelSize(seurat.settings.PixelW,seurat.settings.PixelH);
				// GlobalView frame = new GlobalView(seurat, "GlobalView",
				// Experiments,Genes,false);

			}
		});
		plotsMenu.add(item);
		
		
		plotsMenu.addSeparator();

		item = new JMenuItem("Seriation");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SeriationDialog(seurat, dataManager.Genes,
						dataManager.Experiments);

			}
		});
		plotsMenu.add(item);

		/*
		 * item = new JMenuItem("Clustering"); item.addActionListener(new
		 * ActionListener() { public void actionPerformed(ActionEvent e) { new
		 * ClusteringDialog(seurat); } }); plotsMenu.add(item);
		 */

		item = new JMenuItem("Clustering");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// new ClusteringDialog(seurat);
				new ClusteringDialog(seurat, seurat.dataManager.Genes,
						seurat.dataManager.Experiments);
			}
		});
		plotsMenu.add(item);

		/*
		 * item = new JMenuItem("Compare Seriation Methods");
		 * item.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { new CompareDialog(seurat); } });
		 * plotsMenu.add(item);
		 */

		
		plotsMenu.addSeparator();
		
		item = new JMenuItem("Confusions Matrix");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ConfusionsDialog(seurat);
			}
		});
		plotsMenu.add(item);

		menuBar.add(optionsMenu);

		windowMenu = new JMenu("Window");
		item = new JMenuItem("Close all");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < seurat.windows.size(); i++) {
					seurat.windows.elementAt(i).setVisible(false);
					seurat.windowMenu.remove(1);
				}
				seurat.windows = new Vector();
			}
		});
		windowMenu.add(item);
		windowMenu.addSeparator();

		menuBar.add(windowMenu);

		menuBar.add(helpMenu);
		
		item = new JMenuItem("Online Help");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BrowserLauncher launcher = new BrowserLauncher();
					launcher.openURLinBrowser("http://seurat.r-forge.r-project.org/index.html");

				} catch (BrowserLaunchingInitializingException ee) {
					// TODO Auto-generated catch block
					ee.printStackTrace();
				} catch (UnsupportedOperatingSystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		helpMenu.add(item);
		
		

		this.setVisible(true);

		this.loadLogos();

	}

	public void loadLogos() {

		numImageIcon = new ImageIcon(this.readGif("num.gif"));
		contImageIcon = new ImageIcon(this.readGif("alpha.gif"));

		geneImageIcon = new ImageIcon(this.readGif("Gene.gif"));
		System.out.println("Gene Logo loaded...");
		expImageIcon = new ImageIcon(this.readGif("Exp.gif"));
		System.out.println("Exp Logo loaded...");
		cloneImageIcon = new ImageIcon(this.readGif("Clone.gif"));
		System.out.println("Clone Logo loaded...");
		chrImageIcon = new ImageIcon(this.readGif("Chromosom.gif"));
		System.out.println("Chromosome Logo loaded...");

		logoIcon = new PicCanvas(new ImageIcon(this.readGif("logo.gif"))
				.getImage(), this);

		contIcon = new PicCanvas(new ImageIcon(this.readGif("num.gif"))
				.getImage(), this);
		numIcon = new PicCanvas(new ImageIcon(this.readGif("alpha.gif"))
				.getImage(), this);

	}

	byte[] readGif(String name) {

		byte[] arrayLogo;
		try {
			JarFile MJF;
			try {
				MJF = new JarFile("Seurat.jar");
			} catch (Exception e) {
				MJF = new JarFile(System.getProperty("java.class.path"));
			}

			ZipEntry LE = MJF.getEntry(name);
			InputStream inputLogo = MJF.getInputStream(LE);
			arrayLogo = new byte[(int) LE.getSize()];
			for (int i = 0; i < arrayLogo.length; i++) {
				arrayLogo[i] = (byte) inputLogo.read();
			}
		} catch (Exception e) {
			System.out.println("Logo Exception: " + e);
			arrayLogo = new byte[1];
		}
		return arrayLogo;
	}

	public void repaintWindows() {
		// dataManager.selectVariables();

		for (int i = 0; i < this.windows.size(); i++) {
			((IPlot) windows.elementAt(i)).updateSelection();
		}

		String s = "";

		int selectedGenes = 0;
		int anzahlGenes = 0;
		if (dataManager.Genes != null) {
			for (int i = 0; i < dataManager.Genes.size(); i++) {
				if (dataManager.Genes.elementAt(i) != null
						&& dataManager.Genes.elementAt(i).isSelected())
					selectedGenes++;
				if (dataManager.Genes.elementAt(i) != null)
					anzahlGenes++;
			}

			s += "Genes: " + selectedGenes + "/" + anzahlGenes + " ("
					+ (double) (selectedGenes * 10000 / anzahlGenes) / 100
					+ "%)";

		}

		int selectedExps = 0;
		if (dataManager.Experiments != null) {
			int anzahlExps = dataManager.Experiments.size();
			for (int i = 0; i < dataManager.Experiments.size(); i++) {
				if (dataManager.Experiments.elementAt(i).isSelected())
					selectedExps++;

			}

			s += "   Samples: " + selectedExps + "/" + anzahlExps + " ("
					+ (double) (selectedExps * 10000 / anzahlExps) / 100 + "%)";

		}

		int selectedCGHs = 0;
		String clones = null;

		if (dataManager.CLONES != null) {
			int anzahlCGHs = dataManager.CLONES.size();
			for (int i = 0; i < dataManager.CLONES.size(); i++) {
				if (dataManager.CLONES.elementAt(i).isSelected())
					selectedCGHs++;

			}

			clones = "Clones: " + selectedCGHs + "/" + anzahlCGHs + " ("
					+ (double) (selectedCGHs * 10000 / anzahlCGHs) / 100 + "%)";

		}

		MainPanel.remove(infoPanel);
		MainPanel.add(infoPanel, BorderLayout.SOUTH);
		infoPanel.removeAll();
		JLabel infoLabel = new JLabel(s);
		// infoLabel.setFont(new Font("Calibri",Font.PLAIN,12));

		if (clones != null) {

			infoPanel.setPreferredSize(new Dimension(this.getWidth(), 50));
			infoPanel.setLayout(new GridLayout(2, 1));
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.LEFT));
			p1.add(infoLabel);
			infoPanel.add(p1);

			infoLabel = new JLabel(clones);
			// infoLabel.setFont(new Font("Calibri",Font.PLAIN,12));

			p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.LEFT));
			p1.add(infoLabel);
			infoPanel.add(p1);

		}

		else
			infoPanel.add(infoLabel);

		seurat.update(seurat.getGraphics());
		infoPanel.repaint();
		infoPanel.paint(infoPanel.getGraphics());
		infoPanel.updateUI();

	}

	public void updateWithoutConfusionsPlot(Object o1) {
		dataManager.selectVariables();
		for (int i = 0; i < this.windows.size(); i++) {
			try {
				Object o = ((ComparePlot) windows.elementAt(i));
				// if (o != o1) ((ComparePlot)o).updateSelection();
				break;
			} catch (Exception e) {
			}
			((IPlot) windows.elementAt(i)).updateSelection();
		}

	}

	public void initVariablesTable() {
		Object[][] data = new Object[dataManager.getVariables().size()][2];
		for (int i = 0; i < dataManager.getVariables().size(); i++) {

			if (!dataManager.getVariables().elementAt(i).isExperiment)
				data[i][0] = numIcon;
			else
				data[i][0] = contIcon;

			data[i][1] = dataManager.getVariables().elementAt(i).name;
		}
		String[] names = new String[] { "", "Variables" };
		variablesTable = new VariablesTable(this, data, names);
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(new JScrollPane(variablesTable), BorderLayout.CENTER);
		this.setVisible(true);

	}

	public void openFile(String fileName) {
		if (fileName == null) {
			fileDialog = new FileDialog(this, "Open Gene Expression File", 0);
			fileDialog.setVisible(true);
		}

		if (fileDialog.getFile() != null) {

			// MainPanel.removeAll();
			try {

				// infoPanel.removeAll();
				infoPanel.removeAll();
				infoLabel.setText("Loading Geneexpression dataset...");
				MainPanel.remove(infoPanel);
				infoPanel.add(progressBar, BorderLayout.CENTER);
				MainPanel.add(infoPanel, BorderLayout.SOUTH);
				seurat.setVisible(true);
				this.update(this.getGraphics());

				BufferedReader bfr = null;
				if (fileName == null)
					bfr = new BufferedReader(new FileReader(fileDialog
							.getDirectory()
							+ "/" + fileDialog.getFile()));
				else
					bfr = new BufferedReader(new FileReader(new File(fileName)));

				settings = new Settings();
				DataLoader dataLoader = new DataLoader(this);

				dataLoader.loadGeneExpressions(dataManager, bfr, fileName,
						fileDialog, progressBar);

				DataTreeNode Experiments = new DataTreeNode("Samples");
				for (int i = 0; i < dataManager.Experiments.size(); i++) {
					Variable var = dataManager.Experiments.elementAt(i);
					DataTreeNode Experiment = new DataTreeNode(var);
					Experiments.add(Experiment);
				}

				topObj.add(Experiments);

				DataTreeNode Genes = new DataTreeNode("Genes");
				for (int i = 0; i < dataManager.Genes.size(); i++) {
					Gene var = dataManager.Genes.elementAt(i);
					DataTreeNode Gene = new DataTreeNode(var);
					Genes.add(Gene);
				}

				topObj.add(Genes);

				DataTreeNode GeneExpressions = new DataTreeNode(
						"Genexpression data");
				for (int i = 0; i < dataManager.variables.size(); i++) {
					Variable var = dataManager.variables.elementAt(i);
					DataTreeNode Experiment = new DataTreeNode(var);
					GeneExpressions.add(Experiment);

				}

				DataCellRenderer renderer =

				new DataCellRenderer(numImageIcon, contImageIcon,
						geneImageIcon, chrImageIcon, cloneImageIcon,
						expImageIcon);

				tree.setCellRenderer(renderer);

				topData.add(GeneExpressions);

				infoLabel.setText("");
				infoPanel.remove(progressBar);
				MainPanel.remove(infoPanel);
				MainPanel.add(infoPanel, BorderLayout.SOUTH);
				seurat.update(seurat.getGraphics());

				

				openDescriptionItem.setEnabled(true);
				openGeneAnnotationsItem.setEnabled(true);
				saveGeneExpressions.setEnabled(true);
				closeDataset.setEnabled(true);
				

			} catch (Exception e) {
				System.out.println("Load Error ");
				e.printStackTrace();
			}

		}

	}

	public void applyNewPixelSize(int pixelW,int pixelH) {
		settings.setPixelSize(pixelW,pixelH);
		for (int i = 0; i < this.windows.size(); i++) {
			try {
				((MatrixWindow) windows.elementAt(i)).applyNewPixelSize(pixelW,pixelH);
			} catch (Exception e) {
			}
		}
		this.repaintWindows();
	}
	
	
	
	
	
	
	public void applyNewPixelSize() {
	
		for (int i = 0; i < this.windows.size(); i++) {
			try {
				((MatrixWindow) windows.elementAt(i)).applyNewPixelSize();
			} catch (Exception e) {
			}
		}
		this.repaintWindows();
	}
	
	
	
	
	
	
	

	public void closeDataSet() {

		
		
		
		
		for (int i = 0; i < this.windows.size(); i++) {

			(windows.elementAt(i)).setVisible(false);

		}
		if (this.descriptionFrame != null)
			this.descriptionFrame.setVisible(false);
		this.descriptionFrame = null;
		this.dataManager = new DataManager();
		MainPanel.removeAll();
		this.getContentPane().removeAll();
		menuBar.removeAll();
		infoPanel.removeAll();
		middlePanel.removeAll();

		loadMainWindow();
		this.repaint();
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Seurat s = new Seurat();

	}

	public void createExperimentInfo(Variable exp) {
		/*
		 * JPanel middlePanel = new JPanel();
		 * 
		 * 
		 * if (dataManager.descriptionVariables != null) {
		 * 
		 * 
		 * 
		 * 
		 * 
		 * String text = "";
		 * 
		 * 
		 * int m = 0;
		 * 
		 * for (int i = 0 ; i < dataManager.descriptionVariables .size(); i++) {
		 * 
		 * DescriptionVariable var =
		 * dataManager.descriptionVariables.elementAt(i);
		 * 
		 * 
		 * 
		 * if (m < var.getName().length()) m = var.getName().length();
		 * 
		 * }
		 * 
		 * for (int i = 0 ; i < dataManager.descriptionVariables .size(); i++) {
		 * 
		 * DescriptionVariable var =
		 * dataManager.descriptionVariables.elementAt(i);
		 * 
		 * 
		 * 
		 * 
		 * text += var.getName() + ":"; int k = m - var.getName().length(); for
		 * (int j = 0; j < k; j++) text+=" ";
		 * 
		 * text+="\t"+var.stringData [exp.getID()]+ "\n";
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * JTextArea pp = new JTextArea(text); pp.setBackground(Color.WHITE); //
		 * pp.setLayout(new
		 * GridLayout(dataManager.descriptionVariables.size(),1));
		 * middlePanel.setLayout(new BorderLayout()); middlePanel.add(new
		 * JScrollPane(pp));
		 * 
		 * 
		 * 
		 * //infoLabel.setText("");
		 * 
		 * 
		 * 
		 * 
		 * Info info = new Info(seurat,exp.getName());
		 * info.getContentPane().add(middlePanel,BorderLayout.CENTER);
		 * info.setVisible(true); }
		 */

		JEditorPane editorPane = new JEditorPane("text/html", "");
		StyleSheet css = ((HTMLEditorKit) editorPane.getEditorKit())
				.getStyleSheet();
		Style style = css.getStyle("body");
		JTextField tempField = new JTextField();
		editorPane.setBorder(tempField.getBorder());
		StyleConstants.setRightIndent(style, (float) (2.0));
		StyleConstants.setLeftIndent(style, (float) (2.0));
		StyleConstants.setSpaceBelow(style, (float) (-2.0));
		StyleConstants.setSpaceAbove(style, (float) (-2.0));
		StyleConstants.setFontFamily(style, tempField.getFont().getFamily());
		StyleConstants.setFontSize(style, tempField.getFont().getSize());

		if (dataManager.descriptionVariables != null) {

			String text = "<html><body><font face='Arial'><font color='#20000'><table border='0' ALIGN=LEFT>";

			int m = 0;

			for (int i = 0; i < dataManager.descriptionVariables.size(); i++) {

				DescriptionVariable var = dataManager.descriptionVariables
						.elementAt(i);
				if (m < var.getName().length())
					m = var.getName().length();

			}

			for (int i = 0; i < dataManager.descriptionVariables.size(); i++) {

				DescriptionVariable var = dataManager.descriptionVariables
						.elementAt(i);

				text += "<tr ALIGN=LEFT><th ALIGN=LEFT><FONT FACE = 'Courier New'><h4>"
						+ var.getName() + ":</th>";
				// int k = m - var.getName().length();
				// for (int j = 0; j < k; j++)
				// text+=" ";

				text += "<th ALIGN=LEFT><FONT FACE = 'Courier New'><h4>"
						+ var.stringData[exp.getID()] + "</th></tr>";

			}

			text += "</body></html>";

			editorPane.setEditable(false);
			editorPane.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						System.out.println("Open browser: " + e.getURL());
						try {
							BrowserLauncher launcher = new BrowserLauncher();
							launcher.openURLinBrowser("" + e.getURL());

						} catch (BrowserLaunchingInitializingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (UnsupportedOperatingSystemException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});

			editorPane.setText(text);

			/*
			 * JTextArea pp = new JTextArea(text);
			 * pp.setBackground(Color.WHITE); // pp.setLayout(new
			 * GridLayout(dataManager.descriptionVariables.size(),1));
			 * middlePanel.setLayout(new BorderLayout()); middlePanel.add(new
			 * JScrollPane(pp));
			 */

			Info info = new Info(seurat, exp.getName());
			info.getContentPane().add(new JScrollPane(editorPane),
					BorderLayout.CENTER);
			info.setSize(new Dimension(400, 600));
			info.setVisible(true);

		}

	}

	public void createGeneInfo(Gene gene) {
		// this.middlePanel.removeAll();

		JEditorPane editorPane = new JEditorPane("text/html", "");
		StyleSheet css = ((HTMLEditorKit) editorPane.getEditorKit())
				.getStyleSheet();
		Style style = css.getStyle("body");
		JTextField tempField = new JTextField();
		editorPane.setBorder(tempField.getBorder());
		StyleConstants.setRightIndent(style, (float) (2.0));
		StyleConstants.setLeftIndent(style, (float) (2.0));
		StyleConstants.setSpaceBelow(style, (float) (-2.0));
		StyleConstants.setSpaceAbove(style, (float) (-2.0));
		StyleConstants.setFontFamily(style, tempField.getFont().getFamily());
		StyleConstants.setFontSize(style, tempField.getFont().getSize());

		if (dataManager.geneVariables != null) {

			String text = "<html><body><font face='Arial'><font color='#20000'><table border='0' ALIGN=LEFT>";

			int m = 0;

			for (int i = 0; i < dataManager.geneVariables.size(); i++) {

				GeneVariable var = dataManager.geneVariables.elementAt(i);
				if (m < var.getName().length())
					m = var.getName().length();

			}

			for (int i = 0; i < dataManager.geneVariables.size(); i++) {

				GeneVariable var = dataManager.geneVariables.elementAt(i);

				text += "<tr ALIGN=LEFT><th ALIGN=LEFT><h4>" + var.getName()
						+ ":</th>";
				// int k = m - var.getName().length();
				// for (int j = 0; j < k; j++)
				// text+=" ";

				if (!var.isLink)
					text += "<th ALIGN=LEFT><h4>"
							+ var.stringData[gene.getID()] + "</th></tr>";
				else {
					text += "<th ALIGN=LEFT><h4><a href='"
							+ var.stringData[gene.getID()].replace("\"", "")
							+ "'> "
							+ var.stringData[gene.getID()].replace("\"", "")
							+ "</a></th></tr>";
				}
			}

			text += "</body></html>";

			editorPane.setEditable(false);
			editorPane.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						System.out.println("Open browser: " + e.getURL());
						try {
							BrowserLauncher launcher = new BrowserLauncher();
							launcher.openURLinBrowser("" + e.getURL());

						} catch (BrowserLaunchingInitializingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (UnsupportedOperatingSystemException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});

			editorPane.setText(text);

			/*
			 * JTextArea pp = new JTextArea(text);
			 * pp.setBackground(Color.WHITE); // pp.setLayout(new
			 * GridLayout(dataManager.descriptionVariables.size(),1));
			 * middlePanel.setLayout(new BorderLayout()); middlePanel.add(new
			 * JScrollPane(pp));
			 */

			Info info = new Info(seurat, gene.getName());
			info.getContentPane().add(new JScrollPane(editorPane),
					BorderLayout.CENTER);
			info.setSize(new Dimension(500, 700));
			info.setVisible(true);

		}
	}
	
	
	
	
	public boolean shouldBeClosed()
	{    
		if (JOptionPane.showConfirmDialog(this,"Close all datasets?","Choose one of the following options",0) == 1)
		return false;
		else return true;
	}
	
	public boolean shouldExit()
	{    
		if (JOptionPane.showConfirmDialog(this,"Close all datasets and quit Seurat?","Choose one of the following options",0) == 1)
		return false;
		else return true;
	}
	
	
	
	

	public void cleanMiddlePanel() {
		this.middlePanel.removeAll();

		infoLabel.setText("");

		MainPanel.remove(infoPanel);
		MainPanel.add(infoPanel, BorderLayout.SOUTH);
		seurat.setVisible(true);
		seurat.update(seurat.getGraphics());

	}

	public void setModel(int model) {
		// TODO Auto-generated method stub
		settings.setModel(model);
		for (int i = 0; i < this.windows.size(); i++) {
			try {
				((MatrixWindow) windows.elementAt(i)).setModel(model);
			} catch (Exception e) {
			}
		}
		this.repaintWindows();
	}

	public void setAggregation(int aggr) {
		// TODO Auto-generated method stub
		
	}

}

/*
 * public class Seurat extends JFrame {
 * 
 * JPanel MainPanel = new JPanel();
 * 
 * JFrame WorksPanel = new JFrame();
 * 
 * JMenuBar menuBar = new JMenuBar();
 * 
 * JMenu fileMenu;
 * 
 * JMenu optionsMenu;
 * 
 * JMenu windowMenu;
 * 
 * JMenu helpMenu;
 * 
 * JMenu plotsMenu;
 * 
 * Vector<JFrame> windows = new Vector();
 * 
 * Seurat seurat = this;
 * 
 * FileDialog fileDialog;
 * 
 * CGHViewer cghViewer;
 * 
 * 
 * VariablesTable variablesTable;
 * 
 * ExperimentDescriptionFrame descriptionFrame;
 * 
 * GeneDescriptionFrame geneFrame;
 * 
 * PicCanvas logoIcon;
 * 
 * PicCanvas contIcon, numIcon;
 * 
 * public DataManager dataManager = new DataManager();
 * 
 * Settings settings = new Settings();
 * 
 * 
 * 
 * 
 * public final byte SYSTEM; public final byte WINDOWS = 0; public final byte
 * MAC = 1; public final byte OTHERSYSTEM = 2;
 * 
 * 
 * public Seurat() { super("Seurat"); this.setBounds(0, 0, 250, 300);
 * 
 * System.out.println((System.getProperties().getProperty("os.name")));
 * 
 * if ((System.getProperties().getProperty("os.name")).equals("Windows XP") ||
 * (System.getProperties().getProperty("os.name")).equals("Windows Vista"))
 * this.SYSTEM = this.WINDOWS; else { if
 * ((System.getProperties().getProperty("os.name")).equals("Mac OS X"))
 * this.SYSTEM = this.MAC; else this.SYSTEM = this.OTHERSYSTEM; }
 * 
 * fileMenu = new JMenu("File"); optionsMenu = new JMenu("Options"); helpMenu =
 * new JMenu("Help");
 * 
 * // this.setBounds(20,20,150,450); this.getContentPane().setLayout(new
 * BorderLayout()); this.getContentPane().add(MainPanel, BorderLayout.CENTER);
 * 
 * this.loadLogos(); MainPanel.setLayout(new BorderLayout());
 * MainPanel.add(logoIcon, BorderLayout.CENTER); this.setJMenuBar(menuBar);
 * 
 * 
 * 
 * menuBar.add(fileMenu);
 * 
 * JMenuItem openItem = new JMenuItem("Open File");
 * openItem.addActionListener(new ActionListener() { public void
 * actionPerformed(ActionEvent e) {
 * 
 * 
 * seurat.openFile(null); seurat.initVariablesTable(); //
 * System.out.println(amlTool.variables.size()-2);
 * 
 * // amlTool.sortManager = new SortManager(amlTool); // genesFrame = new
 * GenesFrameClustering(amlTool); // GenesFrameNormal genesNFrame = new
 * GenesFrameNormal(amlTool);
 * 
 * // amlTool.seriation(); // new GenesFrameNormal(amlTool); } });
 * fileMenu.add(openItem);
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * JMenuItem openDescription = new JMenuItem("Open Experiment Descriptor File");
 * openDescription.addActionListener(new ActionListener() {
 * 
 * public void actionPerformed(ActionEvent e) { fileDialog = new
 * FileDialog(seurat, "Open data", 0); fileDialog.setVisible(true);
 * 
 * if (fileDialog.getFile() != null) { descriptionFrame = new
 * ExperimentDescriptionFrame(seurat, fileDialog); } // new
 * DescriptionFrame(amlTool); } }); fileMenu.add(openDescription);
 * 
 * 
 * 
 * JMenuItem geneDescription = new JMenuItem("Open Gene Descriptor File");
 * geneDescription.addActionListener(new ActionListener() {
 * 
 * public void actionPerformed(ActionEvent e) { fileDialog = new
 * FileDialog(seurat, "Open data", 0); fileDialog.setVisible(true);
 * 
 * if (fileDialog.getFile() != null) { geneFrame = new
 * GeneDescriptionFrame(seurat, fileDialog); } // new DescriptionFrame(amlTool);
 * } }); fileMenu.add(geneDescription);
 * 
 * 
 * 
 * 
 * 
 * JMenuItem cgh = new JMenuItem("Open CGH File"); cgh.addActionListener(new
 * ActionListener() {
 * 
 * public void actionPerformed(ActionEvent e) { fileDialog = new
 * FileDialog(seurat, "Open data", 0); fileDialog.setVisible(true);
 * 
 * if (fileDialog.getFile() != null) { cghViewer = new CGHViewer(seurat,
 * fileDialog); } // new DescriptionFrame(amlTool); } }); fileMenu.add(cgh);
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * openItem = new JMenuItem("Save File"); openItem.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) {
 * 
 * 
 * new SaveSelectionFrame(seurat);
 * 
 * 
 * 
 * } }); fileMenu.add(openItem);
 * 
 * 
 * 
 * 
 * JMenuItem closeDataset = new JMenuItem("Close");
 * closeDataset.addActionListener(new ActionListener() {
 * 
 * public void actionPerformed(ActionEvent e) { closeDataSet(); } });
 * fileMenu.add(closeDataset);
 * 
 * fileMenu.addSeparator();
 * 
 * JMenuItem exitItem = new JMenuItem("Exit"); exitItem.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) {
 * System.exit(0); } }); fileMenu.add(exitItem);
 * 
 * JMenuItem item = new JMenuItem("Pixel Settings"); item.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) { new
 * ColorSettings(seurat); } }); optionsMenu.add(item);
 * 
 * 
 * item = new JMenuItem("Clear all Colors"); item.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) { Variable var
 * = seurat.dataManager.Experiments.elementAt(0); while
 * (var.barchartsToColors.size() != 0) {
 * var.barchartsToColors.elementAt(0).removeColoring(); }
 * seurat.repaintWindows();
 * 
 * } }); optionsMenu.add(item);
 * 
 * 
 * 
 * 
 * plotsMenu = new JMenu("Plots"); menuBar.add(plotsMenu);
 * 
 * item = new JMenuItem("GlobalView"); item.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) { //
 * {"ARSA","BBURCG","BBWRCG","TSP","Chen","MDS","HC","GW","OLO"};
 * 
 * int[] orderSpalten = new int[dataManager.getExperiments().size()]; for (int i
 * = 0; i < orderSpalten.length; i++) { orderSpalten[i] = i; }
 * 
 * int[] orderZeilen = new int[dataManager.getRowCount()]; for (int i = 0; i <
 * orderZeilen.length; i++) { orderZeilen[i] = i; }
 * 
 * 
 * 
 * 
 * Vector<Gene> Genes = new Vector(); int[] order = new int[orderZeilen.length];
 * for (int i = 0; i < orderZeilen.length; i++) { order[orderZeilen[i]] = i; }
 * 
 * for (int i = 0; i < order.length; i++) {
 * Genes.add(dataManager.Genes.elementAt(order[i])); }
 * 
 * 
 * 
 * Vector<Variable> Experiments = new Vector(); order = new
 * int[orderSpalten.length]; for (int i = 0; i < orderSpalten.length; i++) {
 * order[orderSpalten[i]] = i; }
 * 
 * for (int i = 0; i < order.length; i++) {
 * Experiments.add(dataManager.Experiments.elementAt(order[i])); }
 * 
 * 
 * 
 * GlobalView frame = new GlobalView(seurat, "GlobalView",
 * Experiments,Genes,false);
 * 
 * 
 * } }); plotsMenu.add(item);
 * 
 * 
 * 
 * item = new JMenuItem("Seriation"); item.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) { new
 * SeriationDialog(seurat);
 * 
 * } }); plotsMenu.add(item);
 * 
 * item = new JMenuItem("Clustering"); item.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) { new
 * ClusteringDialog(seurat); } }); plotsMenu.add(item);
 * 
 * 
 * 
 * 
 * item = new JMenuItem("Kmeans"); item.addActionListener(new ActionListener() {
 * public void actionPerformed(ActionEvent e) { new
 * KMeansDialog(seurat,seurat.dataManager.Genes,
 * seurat.dataManager.Experiments); } }); plotsMenu.add(item);
 * 
 * 
 * 
 * 
 * item = new JMenuItem("Compare Seriation Methods"); item.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) { new
 * CompareDialog(seurat); } }); plotsMenu.add(item);
 * 
 * 
 * item = new JMenuItem("Confusions Matrix"); item.addActionListener(new
 * ActionListener() { public void actionPerformed(ActionEvent e) { new
 * ConfusionsDialog(seurat); } }); plotsMenu.add(item);
 * 
 * 
 * 
 * 
 * 
 * 
 * menuBar.add(optionsMenu);
 * 
 * windowMenu = new JMenu("Window"); item = new JMenuItem("Close all");
 * item.addActionListener(new ActionListener() { public void
 * actionPerformed(ActionEvent e) { for (int i = 0; i < seurat.windows.size();
 * i++) { seurat.windows.elementAt(i).setVisible(false);
 * seurat.windowMenu.remove(1); } seurat.windows = new Vector(); } });
 * windowMenu.add(item); windowMenu.addSeparator();
 * 
 * menuBar.add(windowMenu);
 * 
 * menuBar.add(helpMenu);
 * 
 * this.setVisible(true);
 * 
 * this.loadLogos();
 * 
 * 
 * try {
 * 
 * 
 * RConnectionManager rCon = new RConnectionManager(); rCon.connectToR();
 * 
 * } catch (Exception e) { e.printStackTrace();
 * JOptionPane.showMessageDialog(this, "Connection to R failed."); }
 * 
 * }
 * 
 * public void loadLogos() { logoIcon = new PicCanvas(new
 * ImageIcon(this.readGif("logo.gif")) .getImage(), this);
 * 
 * contIcon = new PicCanvas(new ImageIcon(this.readGif("num.gif")) .getImage(),
 * this); numIcon = new PicCanvas(new ImageIcon(this.readGif("alpha.gif"))
 * .getImage(), this);
 * 
 * }
 * 
 * byte[] readGif(String name) {
 * 
 * byte[] arrayLogo; try { JarFile MJF; try { MJF = new JarFile("Seurat.jar"); }
 * catch (Exception e) { MJF = new
 * JarFile(System.getProperty("java.class.path")); }
 * 
 * ZipEntry LE = MJF.getEntry(name); InputStream inputLogo =
 * MJF.getInputStream(LE); arrayLogo = new byte[(int) LE.getSize()]; for (int i
 * = 0; i < arrayLogo.length; i++) { arrayLogo[i] = (byte) inputLogo.read(); } }
 * catch (Exception e) { System.out.println("Logo Exception: " + e); arrayLogo =
 * new byte[1]; } return arrayLogo; }
 * 
 * public void repaintWindows() { dataManager.selectVariables(); for (int i = 0;
 * i < this.windows.size(); i++) {
 * ((IPlot)windows.elementAt(i)).updateSelection(); } }
 * 
 * 
 * public void updateWithoutConfusionsPlot(Object o1) {
 * dataManager.selectVariables(); for (int i = 0; i < this.windows.size(); i++)
 * { try { Object o = ((ComparePlot)windows.elementAt(i)); // if (o != o1)
 * ((ComparePlot)o).updateSelection(); break; } catch (Exception e) {}
 * ((IPlot)windows.elementAt(i)).updateSelection(); }
 * 
 * }
 * 
 * 
 * 
 * public void initVariablesTable() { Object[][] data = new
 * Object[dataManager.getVariables().size()][2]; for (int i = 0; i <
 * dataManager.getVariables().size(); i++) {
 * 
 * if (!dataManager.getVariables().elementAt(i).isExperiment) data[i][0] =
 * numIcon; else data[i][0] = contIcon;
 * 
 * 
 * data[i][1] = dataManager.getVariables().elementAt(i).name; } String[] names =
 * new String[] { "","Variables" }; variablesTable = new VariablesTable(this,
 * data, names); MainPanel.setLayout(new BorderLayout()); MainPanel.add(new
 * JScrollPane(variablesTable), BorderLayout.CENTER); this.setVisible(true);
 * 
 * }
 * 
 * 
 * 
 * 
 * 
 * public void openFile(String fileName) { if (fileName == null) { fileDialog =
 * new FileDialog(this, "Open data", 0); fileDialog.setVisible(true); }
 * 
 * if (fileDialog.getFile() != null) {
 * 
 * MainPanel.removeAll(); try {
 * 
 * 
 * BufferedReader bfr = null; if (fileName == null) bfr = new BufferedReader(new
 * FileReader(fileDialog .getDirectory() + "/" + fileDialog.getFile())); else
 * bfr = new BufferedReader(new FileReader(new File(fileName)));
 * 
 * settings = new Settings(); DataLoader dataLoader = new DataLoader();
 * 
 * 
 * dataLoader.loadGeneExpressions(dataManager,bfr,fileName,fileDialog);
 * 
 * 
 * } catch (Exception e) { System.out.println("Load Error ");
 * e.printStackTrace(); }
 * 
 * }
 * 
 * }
 * 
 * 
 * 
 * public void applyNewPixelSize(int size) { settings.setPixelSize(size); for
 * (int i = 0; i < this.windows.size(); i++) { try { ((MatrixWindow)
 * windows.elementAt(i)).applyNewPixelSize(size); } catch (Exception e) { } }
 * this.repaintWindows(); }
 * 
 * public void closeDataSet() {
 * 
 * for (int i = 0; i < this.windows.size(); i++) {
 * 
 * (windows.elementAt(i)).setVisible(false);
 * 
 * } if (this.descriptionFrame != null) this.descriptionFrame.setVisible(false);
 * this.descriptionFrame = null; this.dataManager = new DataManager();
 * this.MainPanel.removeAll(); this.repaint(); }
 * 
 * 
 * public static void main(String[] args) { // TODO Auto-generated method stub
 * 
 * Seurat s = new Seurat();
 * 
 * }
 * 
 * }
 */