package GUI;

import java.util.*;
import java.io.*;

import javax.swing.*;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Data.CGHVariable;
import Data.Chromosome;
import Data.Clone;
import Data.Gene;
import Data.GeneVariable;
import Data.MyStringTokenizer;

import java.util.zip.*;
import java.util.jar.*;

public class SNPLoader {
	Seurat seurat;

	public Vector<CGHVariable> snpVariables;
    Vector<Clone> SNPs;
	
	
	FileDialog fileDialog;
	

	double NA = 6.02E23;

	public SNPLoader(Seurat amlTool, FileDialog fileDialog,JProgressBar progressBar) {
		
		System.out.println("CGH Data Viewer");
		this.seurat = amlTool;
		
		this.NA = amlTool.dataManager.NA;
		openSNPFile(fileDialog,progressBar);

		
		JMenuItem openItem = new JMenuItem("Heatmap SNP Data");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Vector vars = new Vector();
				for (int i = 0; i < snpVariables.size(); i++) {
					if (snpVariables.elementAt(i).name.contains("States")) {
						vars.add(snpVariables.elementAt(i));
					}
				}
				
				
				GlobalView v = new GlobalView(seurat,"Heatmap CGH Data", vars,
						SNPs);
				v.applyNewPixelSize(seurat.settings.PixelW,seurat.settings.PixelH);
			
			}
		});
		seurat.plotsMenu.insert(openItem,1);
		
		
		
		
		openItem = new JMenuItem("Chromosome Map SNP");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Vector vars = new Vector();
				for (int i = 0; i < snpVariables.size(); i++) {
					if (snpVariables.elementAt(i).name.contains("States")) {
						vars.add(snpVariables.elementAt(i));
					}
				}
				
				ChromosomeView v = new ChromosomeView(seurat, "Chromosome Map", seurat.dataManager.Chromosomes,vars);
				v.updateSelection();
				//new ChromosomeView(seurat,"Chromosome Viewer", seurat.dataManager.Chromosomes,cghVariables);
			
			}
		});
		seurat.plotsMenu.insert(openItem,2);
		
		

		

	}

	
	
	
	
	
	public void openSNPFile(FileDialog fileDialog,JProgressBar progressBar) {

		try {

			BufferedReader bfr = new BufferedReader(new FileReader(fileDialog
					.getDirectory()
					+ "/" + fileDialog.getFile()));

			
			seurat.dataManager.CGH_SNPName = fileDialog.getFile();
			this.snpVariables = new Vector();

			String line = bfr.readLine();

			MyStringTokenizer stk = new MyStringTokenizer(line);
			int col = 0;
			while (stk.hasMoreTokens()) {
				snpVariables.add(new CGHVariable(stk
						.nextToken(), col,CGHVariable.Double,null,seurat));
				col++;
			}

			int length = 0;

			while ((line = bfr.readLine()) != null) {
				length++;

			}

			bfr = new BufferedReader(new FileReader(fileDialog.getDirectory()
					+ "/" + fileDialog.getFile()));

			line = bfr.readLine();

			for (int i = 0; i < snpVariables.size(); i++) {
				CGHVariable var = snpVariables.elementAt(i);
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
							(snpVariables
									.elementAt(i)).stringBuffer, token))
						(snpVariables
								.elementAt(i)).stringBuffer.add(token);

					if ((snpVariables
							.elementAt(i)).isDouble) {
						try {
							(snpVariables
									.elementAt(i)).stringData[len] = token;
							
						
							
							(snpVariables
									.elementAt(i)).doubleData[len] = new Double(
									token).doubleValue();
							

						} catch (Exception e) {
							if (token.equals("NA") || token.equals("\"NA\"")) {
								(snpVariables
										.elementAt(i)).doubleData[len] = NA;
							} else {
								(snpVariables
										.elementAt(i)).stringData[len] = token;
								(snpVariables
										.elementAt(i)).isDouble = false;

							}
						}
					} else {
						(snpVariables
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

			for (int j = 0; j < this.snpVariables.size(); j++) {
				if (this.snpVariables.elementAt(j).stringBuffer.size() < 10
						|| !this.snpVariables.elementAt(j).isDouble) {
					this.snpVariables.elementAt(j).isDiscrete = true;

				}
			//	if (j == this.cghVariables.size()-1) {
				//	this.cghVariables.elementAt(j).isList = true;
					//this.cghVariables.elementAt(j).calculateList();
		
//				}
			
				
				this.snpVariables.elementAt(j).calculateMean();
			}
			
			seurat.dataManager.cghVariables = snpVariables;
			
			
			connectData();
			
			
			seurat.openSNPItem.setEnabled(false);
			seurat.openCGHItem.setEnabled(false);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(seurat,
				    "Wrong file format.",
				    "Load Error",
				    JOptionPane.ERROR_MESSAGE);
			
			System.out.println("Wrong file format  " + e);
		}

		
	}
	
	
	
	
	
	
	
	
	public void connectData() {
		
		/**Erstellung von Cromosomes*/
		CGHVariable varChr = null;
		for (int i = 0; i < snpVariables.size(); i++) {
			if (snpVariables.elementAt(i).name.contains(seurat.dataManager.ChromosomeNumber)) {
				varChr = snpVariables.elementAt(i);
				break;
			}
		}
		
		if (varChr != null) varChr.isChromosome = true;
		else JOptionPane.showMessageDialog(seurat,
			    "Variable "+ seurat.dataManager.ChromosomeNumber+ " not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		
		for (int i = 0; i < seurat.dataManager.Experiments.size(); i++) {
			Data.Variable var = seurat.dataManager.Experiments.elementAt(i);
			for (int j = 0; j < snpVariables.size(); j++) {
				CGHVariable snpVar = snpVariables.elementAt(j);

				if (snpVar.name.indexOf(var.name.replace("\"",""))!=-1) {
					snpVar.vars.add(var);
					var.cghVars.add(snpVar);
			//		System.out.println("Connected:   "+cghVar.name);
				}
			}
			
		}
		
		
		
		CGHVariable varPos = null;
		for (int i = 0; i < snpVariables.size(); i++) {
			if (snpVariables.elementAt(i).name.contains(seurat.dataManager.NucleotidePosition)) varPos = snpVariables.elementAt(i);
		}
		
		if (varPos == null) JOptionPane.showMessageDialog(seurat,
			    "Variable "+ seurat.dataManager.NucleotidePosition +" not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		CGHVariable varChrCen = null;
		for (int i = 0; i < snpVariables.size(); i++) {
			if (snpVariables.elementAt(i).name.contains(seurat.dataManager.ChrCen)) varChrCen = snpVariables.elementAt(i);
		}
		
		if (varChrCen == null) JOptionPane.showMessageDialog(seurat,
			    "Variable " +seurat.dataManager.ChrCen+" not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		
		
		
		CGHVariable varCytoBand = null;
		for (int i = 0; i < snpVariables.size(); i++) {
			if (snpVariables.elementAt(i).name.contains(seurat.dataManager.CytoBand)) varCytoBand = snpVariables.elementAt(i);
		}
		
		if (varCytoBand == null) JOptionPane.showMessageDialog(seurat,
			    "Variable "+ seurat.dataManager.CytoBand +" not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		
		
		
		
		
		
		
		if (seurat.dataManager.Chromosomes == null) {
		   seurat.dataManager.Chromosomes = new Vector();
		   varChr.stringBuffer = CGHVariable.sortChromosomes(varChr.stringBuffer);
		   for (int i = 0; i < varChr.stringBuffer.size(); i++) {
			   String s = varChr.stringBuffer.elementAt(i);
			   
			   if (s.equals("24")) s = "Y";
			   if (s.equals("23")) s = "X";
				
			   seurat.dataManager.Chromosomes.add(new Chromosome(seurat,s));
		   }
		}
		
		
		/**Erstellung des Clones*/
		CGHVariable cloneIDvar = snpVariables.elementAt(1);
		
		this.SNPs = new Vector();
		for (int i = 0; i < cloneIDvar.stringData.length; i++) {
			Clone clone = new Clone(seurat,cloneIDvar.stringData [i],i);
			SNPs.add(clone);
		    /*Zuweisung des Clones zu der Choromosome**/	
			
			for (int ii = 0; ii < seurat.dataManager.Chromosomes.size(); ii++) {
				if (varChr.stringData [i].equals(seurat.dataManager.Chromosomes.elementAt(ii).name)) {
						seurat.dataManager.Chromosomes.elementAt(ii).Clones.add(clone);
						clone.chromosome = seurat.dataManager.Chromosomes.elementAt(ii);
				}
			}
			
		}
		
		
		///**Verbindung Gene zu den Klonen**///
		
		
		
		GeneVariable chromosomeVar = null;
		for (int i = 0; i < seurat.dataManager.geneVariables.size(); i++) {
			if (seurat.dataManager.geneVariables.elementAt(i).name.contains(seurat.dataManager.ChromosomeNumber)) {
				chromosomeVar = seurat.dataManager.geneVariables.elementAt(i);
			}
		}
		
		
		if (chromosomeVar == null) {
			JOptionPane.showMessageDialog(seurat,
				    "Variable "+ seurat.dataManager.ChromosomeNumber +" in Genannotations not found.",
				    "Connection Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		
		GeneVariable Start = null;
		for (int i = 0; i < seurat.dataManager.geneVariables.size(); i++) {
			if (seurat.dataManager.geneVariables.elementAt(i).name.contains(seurat.dataManager.TranscriptStart)) {
				Start = seurat.dataManager.geneVariables.elementAt(i);
			}
		}
		
		if (Start == null) {
			JOptionPane.showMessageDialog(seurat,
				    "Variable "+ seurat.dataManager.TranscriptStart +" in Genannotations not found.",
				    "Connection Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		GeneVariable End = null;
		for (int i = 0; i < seurat.dataManager.geneVariables.size(); i++) {
			if (seurat.dataManager.geneVariables.elementAt(i).name.contains(seurat.dataManager.TranscriptEnd)) {
				End = seurat.dataManager.geneVariables.elementAt(i);
			}
		}
		
		
		if (End == null) {
			JOptionPane.showMessageDialog(seurat,
				    "Variable "+ seurat.dataManager.TranscriptEnd +" in Genannotations not found.",
				    "Connection Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		
		

		
		
			    
			for (int i = 0; i < seurat.dataManager.Genes.size(); i++ ) {
				
				Gene gene = seurat.dataManager.Genes.elementAt(i);
				double start = gene.nucleotideStart;
				double end = gene.nucleotideEnd;
				
				
				if (gene.chrName != null) {
			
					
					Chromosome chromosome = seurat.dataManager.getChromosome(gene.chrName);
					if (chromosome != null) {
					
				
				for (int k = 0; k < chromosome.Clones.size(); k++) {
							Clone clone = chromosome.Clones.elementAt(k);
						//	clone.chromosome = chromosome;
							double pos = varPos.getValue(clone.ID);
							
							double cen = 2*varChrCen.getValue(clone.ID);
							
							if (chromosome.length == -1) {
								
								chromosome.length = 2*clone.getValue(seurat.dataManager.ChrCen, snpVariables);
							    System.out.println("CHROMOSOME LENGTH: "+chromosome.length);
							
							}
								
							clone.chrStart = pos;
							clone.chrEnd = pos;
							clone.chromosome.chrCen = cen;
							clone.NucleoPosition = Math.round(pos);
							
							
							//System.out.println(start + " " + end + " " + " " + pos + " " + gene.getName());
							
							
							if (pos <=end && pos>=start) {
								
							//	System.out.println(start + " " + end + " " + " " + pos + " " + gene.getName());
								
								clone.Genes.add(gene);
								clone.AnnGenes.add(gene.annGene);
								gene.CLONES.add(clone);
								gene.annGene.CLONES.add(clone);
							}
						}
						
					}
					
				   }
			}		
				
			
			
			
		
		
		
		
		
		for (int i = 0; i < SNPs.size(); i++) {
			Clone clone = SNPs.elementAt(i);
			clone.CytoBand = varCytoBand.stringData [clone.ID];
			clone.chromosome.Center = varChrCen.doubleData [clone.ID];
		}
		
		
		for (int i = 0; i < seurat.dataManager.Chromosomes.size(); i++) {
			seurat.dataManager.Chromosomes.elementAt(i).calculateCytoBand();
		}
		
		
		
		seurat.dataManager.CLONES = SNPs;
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

	public boolean doesntContain(Vector<String> buffer, String word) {
		for (int i = 0; i < buffer.size(); i++) {
			if (buffer.elementAt(i).equals(word))
				return false;
		}
		return true;
	}
	

	

}