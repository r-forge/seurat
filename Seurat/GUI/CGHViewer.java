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

public class CGHViewer {
	Seurat seurat;

	public Vector<CGHVariable> cghVariables;
    Vector<Clone> CLONES;
	
	
	FileDialog fileDialog;


	double NA = 6.02E23;

	public CGHViewer(Seurat amlTool, FileDialog fileDialog,JProgressBar progressBar) {
		System.out.println("CGH Data Viewer");
		this.seurat = amlTool;
		this.NA = amlTool.dataManager.NA;
		
		
		openCGHFile(fileDialog,progressBar);

	

		

		JMenuItem openItem = new JMenuItem("Heatmap Genomic Variations");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Vector vars = new Vector();
				for (int i = 0; i < cghVariables.size(); i++) {
					if (cghVariables.elementAt(i).name.contains("States")) {
						vars.add(cghVariables.elementAt(i));
					}
				}
				
				
				GlobalView v = new GlobalView(seurat,"Heatmap Genomic Variations", vars,
						CLONES);
				v.applyNewPixelSize(seurat.settings.PixelW,seurat.settings.PixelH);
			
			}
		});
		seurat.plotsMenu.insert(openItem,1);
		
		

		
		
		
		
		
		
		
		
		
		
		openItem = new JMenuItem("Chromosome Map");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Vector vars = new Vector();
				for (int i = 0; i < cghVariables.size(); i++) {
					if (cghVariables.elementAt(i).name.contains("States")) {
						vars.add(cghVariables.elementAt(i));
					}
				}
				
				ChromosomeView v = new ChromosomeView(seurat, "Chromosome Map", seurat.dataManager.Chromosomes,vars);
				v.updateSelection();
				//new ChromosomeView(seurat,"Chromosome Viewer", seurat.dataManager.Chromosomes,cghVariables);
			
			}
		});
		seurat.plotsMenu.insert(openItem,2);
		
		

		
		
		
		
		
		
		
		

	}

	
	
	
	public void openCGHFile(FileDialog fileDialog,JProgressBar progressBar) {

		try {

			BufferedReader bfr = new BufferedReader(new FileReader(fileDialog
					.getDirectory()
					+ "/" + fileDialog.getFile()));

			seurat.dataManager.CGH_SNPName = fileDialog.getFile();

			
			this.cghVariables = new Vector();

			String line = bfr.readLine();

			MyStringTokenizer stk = new MyStringTokenizer(line);
			int col = 0;
			while (stk.hasMoreTokens()) {
				cghVariables.add(new CGHVariable(stk
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

			for (int i = 0; i < cghVariables.size(); i++) {
				CGHVariable var = cghVariables.elementAt(i);
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
							(cghVariables
									.elementAt(i)).stringBuffer, token))
						(cghVariables
								.elementAt(i)).stringBuffer.add(token);

					if ((cghVariables
							.elementAt(i)).isDouble) {
						try {
							(cghVariables
									.elementAt(i)).stringData[len] = token;
							
						
							
							(cghVariables
									.elementAt(i)).doubleData[len] = new Double(
									token).doubleValue();
							

						} catch (Exception e) {
							if (token.equals("NA") || token.equals("\"NA\"")) {
								(cghVariables
										.elementAt(i)).doubleData[len] = NA;
							} else {
								(cghVariables
										.elementAt(i)).stringData[len] = token;
								(cghVariables
										.elementAt(i)).isDouble = false;

							}
						}
					} else {
						(cghVariables
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

			for (int j = 0; j < this.cghVariables.size(); j++) {
				if (this.cghVariables.elementAt(j).stringBuffer.size() < 10
						|| !this.cghVariables.elementAt(j).isDouble) {
					this.cghVariables.elementAt(j).isDiscrete = true;

				}
			//	if (j == this.cghVariables.size()-1) {
				//	this.cghVariables.elementAt(j).isList = true;
					//this.cghVariables.elementAt(j).calculateList();
		
//				}
			
				
				this.cghVariables.elementAt(j).calculateMean();
			}
			
			seurat.dataManager.cghVariables = cghVariables;
			
			
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
		for (int i = 0; i < cghVariables.size(); i++) {
			if (cghVariables.elementAt(i).name.contains(seurat.dataManager.ChromosomeNumber)) {
				varChr = cghVariables.elementAt(i);
				break;
			}
		}
		
		if (varChr != null) varChr.isChromosome = true;
		else JOptionPane.showMessageDialog(seurat,
			    "Variable ChromosomeNumber not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		
		for (int i = 0; i < seurat.dataManager.Experiments.size(); i++) {
			Data.Variable var = seurat.dataManager.Experiments.elementAt(i);
			for (int j = 0; j < cghVariables.size(); j++) {
				CGHVariable cghVar = cghVariables.elementAt(j);

				if (cghVar.name.indexOf(var.name.replace("\"",""))!=-1) {
					cghVar.vars.add(var);
					var.cghVars.add(cghVar);
			//		System.out.println("Connected:   "+cghVar.name);
				}
			}
			
		}
		
		
		
		CGHVariable varChrStart = null;
		for (int i = 0; i < cghVariables.size(); i++) {
			if (cghVariables.elementAt(i).name.contains(seurat.dataManager.CloneStart)) varChrStart = cghVariables.elementAt(i);
		}
		
		if (varChrStart == null) JOptionPane.showMessageDialog(seurat,
			    "Variable ChrStart not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		CGHVariable varChrEnd = null;
		for (int i = 0; i < cghVariables.size(); i++) {
			if (cghVariables.elementAt(i).name.contains(seurat.dataManager.CloneEnd)) varChrEnd = cghVariables.elementAt(i);
		}
		
		
		if (varChrEnd == null) JOptionPane.showMessageDialog(seurat,
			    "Variable ChrEnd not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		CGHVariable varChrCen = null;
		for (int i = 0; i < cghVariables.size(); i++) {
			if (cghVariables.elementAt(i).name.contains(seurat.dataManager.ChrCen)) varChrCen = cghVariables.elementAt(i);
		}
		
		if (varChrCen == null) JOptionPane.showMessageDialog(seurat,
			    "Variable ChromosomeCen not found.",
			    "Connection Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		/*
		CGHVariable varCloneMidpoint = null;
		for (int i = 0; i < cghVariables.size(); i++) {
			if (cghVariables.elementAt(i).name.contains(seurat.dataManager.CloneMidPoint)) varCloneMidpoint = cghVariables.elementAt(i);
		}
		*/
		
		
		CGHVariable varCloneCytoBand = null;
		for (int i = 0; i < cghVariables.size(); i++) {
			if (cghVariables.elementAt(i).name.contains(seurat.dataManager.CytoBand)) varCloneCytoBand = cghVariables.elementAt(i);
		}
		
		if (varCloneCytoBand == null) JOptionPane.showMessageDialog(seurat,
			    "Variable Mapping not found.",
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
		CGHVariable cloneIDvar = cghVariables.elementAt(1);
		
		this.CLONES = new Vector();
		for (int i = 0; i < cloneIDvar.stringData.length; i++) {
			Clone clone = new Clone(seurat,cloneIDvar.stringData [i],i);
			CLONES.add(clone);
		    /*Zuweisung des Clones zu der Choromosome**/	
			
			for (int ii = 0; ii < seurat.dataManager.Chromosomes.size(); ii++) {
				if (varChr.stringData [i].equals(seurat.dataManager.Chromosomes.elementAt(ii).name)) {
						seurat.dataManager.Chromosomes.elementAt(ii).Clones.add(clone);
						clone.chromosome = seurat.dataManager.Chromosomes.elementAt(ii);
				}
			}
			
		}
		
		
		///**Verbindung Gene zu den Klonen**///
		
		/*
		
		Data.Variable chromosomeVar = null;
		for (int i = 0; i < seurat.dataManager.ExperimentDescr.size(); i++) {
			if (seurat.dataManager.ExperimentDescr.elementAt(i).name.contains(seurat.dataManager.ChromosomeNumber)) {
				chromosomeVar = seurat.dataManager.ExperimentDescr.elementAt(i);
			}
		}
		
		
		Data.Variable nucleoVar = null;
		for (int i = 0; i < seurat.dataManager.ExperimentDescr.size(); i++) {
			if (seurat.dataManager.ExperimentDescr.elementAt(i).name.contains(seurat.dataManager.TranscriptStart)) {
				nucleoVar = seurat.dataManager.ExperimentDescr.elementAt(i);
			}
		}
		*/
		
		
		
		
		/*
		
		if (chromosomeVar != null && nucleoVar != null) { 
			
			/**Verbindung Ÿber die Expressionen
		
		for (int i = 0; i < seurat.dataManager.Genes.size(); i++ ) {
		
			Gene gene = seurat.dataManager.Genes.elementAt(i);
			double pos = nucleoVar.getValue(i);

			
			for (int j = 0; j < seurat.dataManager.Chromosomes.size();j++) {
				
				
				//System.out.println(seurat.dataManager.Chromosomes.elementAt(j).name +"   "+chromosomeVar.stringData [i]); 
				
				Chromosome chromosome = seurat.dataManager.Chromosomes.elementAt(j);
				
				
				String s = chromosomeVar.stringData [i];
				 
				   if (s.equals("24")) s = "Y";
				   if (s.equals("23")) s = "X";
					
				
				
				if (chromosome.name.equals(s)) {
					for (int k = 0; k < chromosome.Clones.size(); k++) {
						Clone clone = chromosome.Clones.elementAt(k);
						clone.chromosome = chromosome;
						double start = varChrStart.getValue(clone.ID);
						double end = varChrEnd.getValue(clone.ID);
						double cen = 2*varChrCen.getValue(clone.ID);
						
						if (chromosome.length == -1) chromosome.length = 2*clone.getValue("ChromosomeCen");
							
						clone.chromosome.chrStart = start;
						clone.chromosome.chrEnd = end;
						clone.chromosome.chrCen = cen;
						
						System.out.println(start + " " + end + " " + " " + pos + " " +seurat.dataManager.Chromosomes.elementAt(j).name);
						
						if (pos <=end && pos>=start) {
							clone.Genes.add(gene);
							gene.CLONES.add(clone);
						}
					}
					
				}
				
			
				System.out.println(i);
				
			}
		}
		}
		else {/**Connection via Genannotations*/
		
		
		
		
		
		
		
		
		
		

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
		
		
		
		
		
		
		
		
			
	    
			for (int i = 0; i < seurat.dataManager.Genes.size(); i++ ) {
				
				Gene gene = seurat.dataManager.Genes.elementAt(i);
				double pos = gene.nucleotideStart;
				//System.out.println(gene.getName()+ "   " + pos);
				
				
				if (gene.chrName != null) {
					//System.out.println("-->"+gene.chrName);
					
					Chromosome chromosome = seurat.dataManager.getChromosome(gene.chrName);
					if (chromosome != null) {
					
				
				for (int k = 0; k < chromosome.Clones.size(); k++) {
							Clone clone = chromosome.Clones.elementAt(k);
						//	clone.chromosome = chromosome;
							double start = varChrStart.getValue(clone.ID);
							double end = varChrEnd.getValue(clone.ID);
							double cen = 2*varChrCen.getValue(clone.ID);
							
							if (chromosome.length == -1) chromosome.length = 2*clone.getValue(seurat.dataManager.ChrCen, cghVariables);
								
							clone.chrStart = start;
							clone.chrEnd = end;
							clone.chromosome.chrCen = cen;
							clone.NucleoPosition = Math.round((end + start)/2);
							
						//	System.out.println(start + " " + end + " " + " " + pos + " " + gene.getName());
							
							if (pos <=end && pos>=start) {
								clone.Genes.add(gene);
								clone.AnnGenes.add(gene.annGene);
								gene.CLONES.add(clone);
								gene.annGene.CLONES.add(clone);
							}
						}
						
					}
					
				   }
			}		
				
			
			
			
		
		
		
		
		
		for (int i = 0; i < CLONES.size(); i++) {
			Clone clone = CLONES.elementAt(i);
			//System.out.println(varCloneMidpoint.doubleData);
		//	clone.NucleoPosition = varCloneMidpoint.doubleData [clone.ID];
			clone.CytoBand = varCloneCytoBand.stringData [clone.ID];
			clone.chromosome.Center = varChrCen.doubleData [clone.ID];
		}
		
		
		for (int i = 0; i < seurat.dataManager.Chromosomes.size(); i++) {
			seurat.dataManager.Chromosomes.elementAt(i).calculateCytoBand();
		}
		
		
		
		seurat.dataManager.CLONES = CLONES;
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

	public boolean doesntContain(Vector<String> buffer, String word) {
		for (int i = 0; i < buffer.size(); i++) {
			if (buffer.elementAt(i).equals(word))
				return false;
		}
		return true;
	}
	

	

}