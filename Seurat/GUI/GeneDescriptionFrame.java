package GUI;
import java.util.*;
import java.io.*;

import javax.swing.*;

import java.awt.*;

import Data.AnnGene;
import Data.CGHVariable;
import Data.Chromosome;
import Data.Gene;
import Data.GeneVariable;
import Data.ISelectable;
import Data.MyStringTokenizer;

import java.util.zip.*;
import java.util.jar.*;

public class GeneDescriptionFrame extends JFrame {
	Seurat seurat;

	Vector<GeneVariable> geneVariables;

	FileDialog fileDialog;

	PicCanvas contIcon, numIcon;
	GeneVariableTable geneVariableTable;


	final double NA = 6.02E23;
	
	
	
	
	

	public GeneDescriptionFrame(Seurat amlTool, FileDialog fileDialog, JProgressBar progressBar) {
		super("Gene Descriptor");
		this.seurat = amlTool;
		this.loadLogos();

		openDescription(fileDialog,progressBar);

		JPanel MainPanel = new JPanel();

		Object[][] data = new Object[this.geneVariables.size()][2];
		for (int i = 0; i < this.geneVariables.size(); i++) {
			if (this.geneVariables.elementAt(i).isDiscrete)
				data[i][0] = numIcon;
			else
				data[i][0] = contIcon;
			data[i][1] = geneVariables.elementAt(i).name;
		}
		String[] names = new String[] { "", "Variables" };
		geneVariableTable = new GeneVariableTable(amlTool, this,
				data, names);
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(new JScrollPane(geneVariableTable),
				BorderLayout.CENTER);

		this.getContentPane().add(MainPanel, BorderLayout.CENTER);
		this.setBounds(0, 450, 200, 300);
		//this.setVisible(true);

	}

	public void loadLogos() {
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

	public void openDescription(FileDialog fileDialog, JProgressBar progressBar) {

		try {

			BufferedReader bfr = new BufferedReader(new FileReader(fileDialog
					.getDirectory()
					+ "/" + fileDialog.getFile()));

			this.geneVariables = new Vector();

			String line = bfr.readLine();

			MyStringTokenizer stk = new MyStringTokenizer(line);
			int col = 0;
			while (stk.hasMoreTokens()) {
				GeneVariable geneVar = new GeneVariable(stk
						.nextToken(), GeneVariable.Double,null);
				if (geneVar.getName().contains("@")) geneVar.isLink = true;
				geneVariables.add(geneVar);
				col++;
			}

			int length = 0;

			while ((line = bfr.readLine()) != null) {
				length++;

			}

			bfr = new BufferedReader(new FileReader(fileDialog.getDirectory()
					+ "/" + fileDialog.getFile()));

			line = bfr.readLine();

			for (int i = 0; i < geneVariables.size(); i++) {
				GeneVariable var = geneVariables.elementAt(i);
				var.doubleData = new double[length];
				var.stringData = new String[length];

			}

			int len = 0;
			while ((line = bfr.readLine()) != null) {

				MyStringTokenizer myStk = new MyStringTokenizer(line,col);
				String token;
				
				
				if (len%10 == 0) {progressBar.setValue((100*len/length));
				progressBar.repaint();
				seurat.update(seurat.getGraphics());
				}
				

				for (int i = 0; i < col; i++) {
					
					token = myStk.tokens.elementAt(i);
					if (token.equals("")) token = "NA";
					
						
					if (doesntContain(
							(geneVariables
									.elementAt(i)).stringBuffer, token))
						(geneVariables
								.elementAt(i)).stringBuffer.add(token);

					if ((geneVariables
							.elementAt(i)).isDouble) {
						try {
							(geneVariables
									.elementAt(i)).stringData[len] = token;
							
						
							try {
							(geneVariables
									.elementAt(i)).doubleData[len] = Integer.parseInt(token);
							}
							catch (Exception e) {
							
							(geneVariables
									.elementAt(i)).doubleData[len] = new Double(
									token).doubleValue();
							}

						} catch (Exception e) {
							if (token.equals("NA")) {
								(geneVariables
										.elementAt(i)).doubleData[len] = NA;
							} else {
								(geneVariables
										.elementAt(i)).stringData[len] = token;
								(geneVariables
										.elementAt(i)).isDouble = false;

							}
						}
					} else {
						(geneVariables
								.elementAt(i)).stringData[len] = token;
					}
				}

				len++;
			}

			
			progressBar.setValue(0);
			
			
			/**
			 * *************************************File
			 * Output*****************************************************************
			 */
			/*
			 * for (int j = 0; j < this.variables.size(); j++) {
			 * System.out.print(this.variables.elementAt(j).name + " "); }
			 * 
			 * System.out.println();
			 * 
			 * for (int i = 0; i < length; i++) { for (int j = 0; j <
			 * this.variables.size(); j++) { if (variables.elementAt(j).type ==
			 * Variable.String)
			 * System.out.print(this.variables.elementAt(j).stringData [i] + "
			 * "); else { if (variables.elementAt(j).isNotNA [i])
			 * System.out.print(this.variables.elementAt(j).doubleData [i] + "
			 * "); else System.out.print("NA "); } } System.out.println(""); }
			 */

			for (int j = 0; j < this.geneVariables.size(); j++) {
				if (this.geneVariables.elementAt(j).stringBuffer.size() < 10
						|| !this.geneVariables.elementAt(j).isDouble) {
					this.geneVariables.elementAt(j).isDiscrete = true;

				}
				if (this.geneVariables.elementAt(j).isDiscrete && this.geneVariables.elementAt(j).testList()) {
					this.geneVariables.elementAt(j).isList = true;
					this.geneVariables.elementAt(j).calculateList();
		
				}
			
				

			}
			
			seurat.dataManager.geneVariables = geneVariables;
			connectData();
			

			seurat.openGeneAnnotationsItem.setEnabled(false);
			
			
		} catch (IOException e) {
			System.out.println("Wrong file format  " + e);
		}

	}

	
	public void connectData() {
		//Connect Genes to Geneexpression
		GeneVariable indexVar = geneVariables.elementAt(0);
		
		Vector<AnnGene> AnnGenes = new Vector();
		for (int i = 0; i < indexVar.stringData.length; i++) {
			AnnGene gene = new AnnGene(indexVar.stringData [i],i,seurat.dataManager);
			AnnGenes.add(gene);
		}
		
		seurat.dataManager.AnnGenes = AnnGenes;
		
		
		for (int i = 0; i < indexVar.stringData.length; i++)
		{
			boolean found = false;
			for (int j = i; j < seurat.dataManager.Genes.size(); j++) {
				if (indexVar.stringData [i].equals(seurat.dataManager.Genes.elementAt(j).getName())) {
					AnnGenes.elementAt(i).gene = (seurat.dataManager.Genes.elementAt(j));
					seurat.dataManager.Genes.elementAt(j).annGene = AnnGenes.elementAt(i);
					found = true;
					break;
				} 
			}
			if (!found) {
			for (int j = 0; j < Math.min(i,seurat.dataManager.Genes.size()); j++) {
				if (indexVar.stringData [i].equals(seurat.dataManager.Genes.elementAt(j).getName())) {
					AnnGenes.elementAt(i).gene = (seurat.dataManager.Genes.elementAt(j));
					seurat.dataManager.Genes.elementAt(j).annGene = AnnGenes.elementAt(i);
					found = true;
					break;
				} 
			}
			}
			
			//if (!found) Genes.add(null);
			
			
		}
		
		
		
		GeneVariable chromosomeVar = null;
		for (int i = 0; i < geneVariables.size(); i++) {
			if (geneVariables.elementAt(i).name.contains(seurat.dataManager.ChromosomeNumber)) chromosomeVar = geneVariables.elementAt(i);
		}
		
		chromosomeVar.sortBuffer();
		
		
		GeneVariable nucleoVar = null;
		for (int i = 0; i < geneVariables.size(); i++) {
			if (geneVariables.elementAt(i).name.contains(seurat.dataManager.NucleotidePosition)) nucleoVar = geneVariables.elementAt(i);
		}
		
		
		
		
		for (int i = 0; i < AnnGenes.size(); i++) {
			
			    AnnGene gene = AnnGenes.elementAt(i);
			    gene.chrName = chromosomeVar.stringData [i];
			    
			    gene.nucleotidePosition = nucleoVar.doubleData [i];
			    
			    if (gene.gene != null) {
			    	 gene.gene.chrName = chromosomeVar.stringData [i];
					    
					    gene.gene.nucleotidePosition = nucleoVar.doubleData [i];
			    } 
			    
			 //   System.out.println(gene.chrName + "   " + gene.nucleotidePosition);
		
		}
		
		
		
		for (int i = 0; i < geneVariables.size(); i++) {
			geneVariables.elementAt(i).AnnGenes = AnnGenes;
		}
		
		
		
		/**  Create Chromosomes, Clones, etc**/
		/*
		GeneVariable chromosomeVar = null;
		for (int i = 0; i < geneVariables.size(); i++) {
			if (geneVariables.elementAt(i).name.contains(seurat.dataManager.ChromosomeNumber)) chromosomeVar = geneVariables.elementAt(i);
		}
		
		chromosomeVar.sortBuffer();
		
		
		GeneVariable nucleoVar = null;
		for (int i = 0; i < geneVariables.size(); i++) {
			if (geneVariables.elementAt(i).name.contains(seurat.dataManager.NucleotidePosition)) nucleoVar = geneVariables.elementAt(i);
		}
		
		
		

		if (seurat.dataManager.Chromosomes == null) {
		   seurat.dataManager.Chromosomes = new Vector();
		   for (int i = 0; i < chromosomeVar.stringBuffer.size(); i++) {
			   String chr = chromosomeVar.stringBuffer.elementAt(i);
			   chr = chr.replace("\"","");
			   if (!chromosomeVar.stringBuffer.elementAt(i).contains("NA")) seurat.dataManager.Chromosomes.add(new Chromosome(chromosomeVar.stringBuffer.elementAt(i),seurat.dataManager));
		   }
		}
		
	
		
		for (int i = 0; i < chromosomeVar.Genes.size(); i++) {
			Chromosome chr = seurat.dataManager.getChromosome(chromosomeVar.stringData [i]);
		
			chromosomeVar.Genes.elementAt(i).chromosome = chr;
			if (chr != null) {
				chr.Genes.add(chromosomeVar.Genes.elementAt(i));
			}
			
		}
		
		
		for (int i = 0; i < nucleoVar.Genes.size(); i++) {
			double pos = nucleoVar.doubleData [i];
			chromosomeVar.Genes.elementAt(i).nucleotidePosition = pos;
			
			
		}
		*/
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean doesntContain(Vector<String> buffer, String word) {
		for (int i = 0; i < buffer.size(); i++) {
			if (buffer.elementAt(i).equals(word))
				return false;
		}
		return true;
	}
	

	

}