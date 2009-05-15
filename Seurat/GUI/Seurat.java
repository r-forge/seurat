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
import Data.Gene;
import Data.MyStringTokenizer;
import Data.Variable;

import java.util.zip.*;
import java.util.jar.*;

import RConnection.RConnectionManager;
import Settings.*;

public class Seurat extends JFrame {

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

    public DataManager dataManager = new DataManager();	
	
	Settings settings = new Settings();
	
	
	
	
	public final byte SYSTEM; 
	public final byte WINDOWS = 0;
	public final byte MAC = 1;
	public final byte OTHERSYSTEM = 2;
	

	public Seurat() {
		super("Seurat");
		this.setBounds(0, 0, 250, 300);
		
		System.out.println((System.getProperties().getProperty("os.name")));
		
		if ((System.getProperties().getProperty("os.name")).equals("Windows XP")
				|| (System.getProperties().getProperty("os.name")).equals("Windows Vista"))
     	    this.SYSTEM = this.WINDOWS;
     	else {
     		    if ((System.getProperties().getProperty("os.name")).equals("Mac OS X"))
                   this.SYSTEM = this.MAC;
     		    else this.SYSTEM = this.OTHERSYSTEM;
     		 }

		fileMenu = new JMenu("File");
		optionsMenu = new JMenu("Options");
		helpMenu = new JMenu("Help");

		// this.setBounds(20,20,150,450);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(MainPanel, BorderLayout.CENTER);

		this.loadLogos();
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(logoIcon, BorderLayout.CENTER);
		this.setJMenuBar(menuBar);

		/**
		 * ************************************************File
		 * Menu******************************************************************
		 */

		menuBar.add(fileMenu);

		JMenuItem openItem = new JMenuItem("Open File");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				seurat.openFile(null);
				seurat.initVariablesTable();
				// System.out.println(amlTool.variables.size()-2);

				// amlTool.sortManager = new SortManager(amlTool);
				// genesFrame = new GenesFrameClustering(amlTool);
				// GenesFrameNormal genesNFrame = new GenesFrameNormal(amlTool);

				// amlTool.seriation();
				// new GenesFrameNormal(amlTool);
			}
		});
		fileMenu.add(openItem);
		
		
		
		
		
		

		JMenuItem openDescription = new JMenuItem("Open Experiment Descriptor File");
		openDescription.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileDialog = new FileDialog(seurat, "Open data", 0);
				fileDialog.setVisible(true);

				if (fileDialog.getFile() != null) {
					descriptionFrame = new ExperimentDescriptionFrame(seurat, fileDialog);
				}
				// new DescriptionFrame(amlTool);
			}
		});
		fileMenu.add(openDescription);
		
		
		
		JMenuItem geneDescription = new JMenuItem("Open Gene Descriptor File");
		geneDescription.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileDialog = new FileDialog(seurat, "Open data", 0);
				fileDialog.setVisible(true);

				if (fileDialog.getFile() != null) {
					geneFrame = new GeneDescriptionFrame(seurat, fileDialog);
				}
				// new DescriptionFrame(amlTool);
			}
		});
		fileMenu.add(geneDescription);
		
		
		
		

		JMenuItem cgh = new JMenuItem("Open CGH File");
		cgh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileDialog = new FileDialog(seurat, "Open data", 0);
				fileDialog.setVisible(true);

				if (fileDialog.getFile() != null) {
					cghViewer = new CGHViewer(seurat, fileDialog);
				}
				// new DescriptionFrame(amlTool);
			}
		});
		fileMenu.add(cgh);
		
		
		
		
		
		
		
		openItem = new JMenuItem("Save File");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				new SaveSelectionFrame(seurat);    
				
				
				
			}
		});
		fileMenu.add(openItem);
		
		
		

		JMenuItem closeDataset = new JMenuItem("Close");
		closeDataset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				closeDataSet();
			}
		});
		fileMenu.add(closeDataset);

		fileMenu.addSeparator();

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);

		JMenuItem item = new JMenuItem("Pixel Settings");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ColorSettings(seurat);
			}
		});
		optionsMenu.add(item);
		
		
		item = new JMenuItem("Clear all Colors");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					Variable var = seurat.dataManager.Experiments.elementAt(0);
					while (var.barchartsToColors.size() != 0) {
						var.barchartsToColors.elementAt(0).removeColoring();
					}
					seurat.repaintWindows();
			    
			}
		});
		optionsMenu.add(item);
		
		
		

		plotsMenu = new JMenu("Plots");
		menuBar.add(plotsMenu);

		item = new JMenuItem("GlobalView");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// {"ARSA","BBURCG","BBWRCG","TSP","Chen","MDS","HC","GW","OLO"};
				
				int[] orderSpalten = new int[dataManager.getExperiments().size()];
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
					Experiments.add(dataManager.Experiments.elementAt(order[i]));
				}
				
				

				GlobalView frame = new GlobalView(seurat, "GlobalView",
						Experiments,Genes,false);
			

			}
		});
		plotsMenu.add(item);

		

		item = new JMenuItem("Seriation");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SeriationDialog(seurat);

			}
		});
		plotsMenu.add(item);

		item = new JMenuItem("Clustering");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ClusteringDialog(seurat);
			}
		});
		plotsMenu.add(item);
		
		
		
		
		item = new JMenuItem("Kmeans");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new KMeansDialog(seurat,seurat.dataManager.Genes, seurat.dataManager.Experiments);
			}
		});
		plotsMenu.add(item);
		
		
		
		
		item = new JMenuItem("Compare Seriation Methods");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CompareDialog(seurat);
			}
		});
		plotsMenu.add(item);
		
		
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

		this.setVisible(true);
		
		this.loadLogos();


		try {
			
			
		RConnectionManager rCon = new RConnectionManager();
		rCon.connectToR();
		
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Connection to R failed.");
		}

	}

	public void loadLogos() {
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
		dataManager.selectVariables();
		for (int i = 0; i < this.windows.size(); i++) {
			((IPlot)windows.elementAt(i)).updateSelection();
		}
	}
	
	
	public void updateWithoutConfusionsPlot(Object o1) {
		dataManager.selectVariables();
		for (int i = 0; i < this.windows.size(); i++) {
			try {
				Object o = ((ComparePlot)windows.elementAt(i));
			//	if (o != o1) ((ComparePlot)o).updateSelection();
				break;
			}
			catch (Exception e) {}
			((IPlot)windows.elementAt(i)).updateSelection();
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
		String[] names = new String[] { "","Variables" };
		variablesTable = new VariablesTable(this, data, names);
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(new JScrollPane(variablesTable), BorderLayout.CENTER);
		this.setVisible(true);

	}

	



	public void openFile(String fileName) {
		if (fileName == null) {
			fileDialog = new FileDialog(this, "Open data", 0);
			fileDialog.setVisible(true);
		}

		if (fileDialog.getFile() != null) {

			MainPanel.removeAll();
			try {
				
			
			BufferedReader bfr = null;
			if (fileName == null)
				bfr = new BufferedReader(new FileReader(fileDialog
						.getDirectory()
						+ "/" + fileDialog.getFile()));
			else
				bfr = new BufferedReader(new FileReader(new File(fileName)));

			settings = new Settings();
			DataLoader dataLoader = new DataLoader();
			
			
			dataLoader.loadGeneExpressions(dataManager,bfr,fileName,fileDialog);
		
		
	    }
		catch (Exception e) {
		    System.out.println("Load Error ");
		     e.printStackTrace();
		}	
		
		}	
		
	}



	public void applyNewPixelSize(int size) {
		settings.setPixelSize(size);
		for (int i = 0; i < this.windows.size(); i++) {
			try {
				((MatrixWindow) windows.elementAt(i)).applyNewPixelSize(size);
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
		this.MainPanel.removeAll();
		this.repaint();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Seurat s = new Seurat();
		
	}

}
