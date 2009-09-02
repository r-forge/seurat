package GUI;
import java.util.*;
import java.io.*;

import javax.swing.*;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.awt.*;

import Data.Clustering;
import Data.DescriptionVariable;
import Data.ISelectable;
import Data.MyStringTokenizer;

import java.util.zip.*;
import java.util.jar.*;

public class ExperimentDescriptionFrame extends JFrame {
	Seurat seurat;

	Vector<DescriptionVariable> descriptionVariables;

	FileDialog fileDialog;

	PicCanvas contIcon, numIcon;

	DescriptionVariableTable descriptionVariableTable;

	final double NA = Math.PI;

	public ExperimentDescriptionFrame(Seurat amlTool, FileDialog fileDialog,JProgressBar progressBar) {
		super("Experiment Descriptor");
		this.seurat = amlTool;
		this.loadLogos();

		openDescription(fileDialog, progressBar);

		JPanel MainPanel = new JPanel();

		Object[][] data = new Object[this.descriptionVariables.size()][2];
		for (int i = 0; i < this.descriptionVariables.size(); i++) {
			if (this.descriptionVariables.elementAt(i).isDiscrete)
				data[i][0] = numIcon;
			else
				data[i][0] = contIcon;
			data[i][1] = descriptionVariables.elementAt(i).name;
		}
		String[] names = new String[] { "", "Variables" };
		descriptionVariableTable = new DescriptionVariableTable(amlTool, this,
				data, names);
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(new JScrollPane(descriptionVariableTable),
				BorderLayout.CENTER);

		this.getContentPane().add(MainPanel, BorderLayout.CENTER);
		this.setBounds(0, 450, 200, 300);
		//this.setVisible(true);
		
		
		seurat.dataManager.descriptionVariables = descriptionVariables;

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

	public void openDescription(FileDialog fileDialog,JProgressBar progressBar) {

		try {

			BufferedReader bfr = new BufferedReader(new FileReader(fileDialog
					.getDirectory()
					+ "/" + fileDialog.getFile()));

			this.descriptionVariables = new Vector();

			String line = bfr.readLine();

			StringTokenizer stk = new StringTokenizer(line, "	");
			int col = 0;
			while (stk.hasMoreTokens()) {
				descriptionVariables.add(new DescriptionVariable(stk
						.nextToken(), DescriptionVariable.Double,seurat.dataManager.Experiments));
				col++;
			}

			int length = 0;

			while ((line = bfr.readLine()) != null) {
				length++;

			}

			bfr = new BufferedReader(new FileReader(fileDialog.getDirectory()
					+ "/" + fileDialog.getFile()));

			line = bfr.readLine();

			for (int i = 0; i < descriptionVariables.size(); i++) {
				DescriptionVariable var = descriptionVariables.elementAt(i);
				var.doubleData = new double[length];
				var.stringData = new String[length];

			}

			int len = 0;
			while ((line = bfr.readLine()) != null) {

				
				if (len%10 == 0) {progressBar.setValue((100*len/length));
				progressBar.repaint();
				seurat.update(seurat.getGraphics());
				}
				
				
				
				MyStringTokenizer myStk = new MyStringTokenizer(line);
				String token;

				for (int i = 0; i < col; i++) {
					token = myStk.tokens.elementAt(i);

					if (doesntContain(
							(descriptionVariables
									.elementAt(i)).stringBuffer, token))
						(descriptionVariables
								.elementAt(i)).stringBuffer.add(token);

					if ((descriptionVariables
							.elementAt(i)).isDouble) {
						try {
							(descriptionVariables
									.elementAt(i)).stringData[len] = token;
							(descriptionVariables
									.elementAt(i)).doubleData[len] = new Double(
									token).doubleValue();

						} catch (Exception e) {
							if (token.equals("NA")) {
								(descriptionVariables
										.elementAt(i)).doubleData[len] = NA;
							} else {
								(descriptionVariables
										.elementAt(i)).stringData[len] = token;
								(descriptionVariables
										.elementAt(i)).isDouble = false;

							}
						}
					} else {
						(descriptionVariables
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

			for (int j = 0; j < this.descriptionVariables.size(); j++) {
				if (this.descriptionVariables.elementAt(j).stringBuffer.size() < 10
						|| !this.descriptionVariables.elementAt(j).isDouble) {
					this.descriptionVariables.elementAt(j).isDiscrete = true;
                    
					
					
					
					/** Add Clusters to ConfufionsMatrix*/
					Vector<Vector<ISelectable>> Clusters = new Vector();
					for (int i = 0; i < this.descriptionVariables.elementAt(j).stringBuffer.size(); i++) {
						Clusters.add(new Vector());
					}
					
					for (int i = 0; i < seurat.dataManager.Experiments.size(); i++) {
						ISelectable exp = seurat.dataManager.Experiments.elementAt(i);
					  
					    
					    for (int ii = 0; ii < this.descriptionVariables.elementAt(j).stringBuffer.size(); ii++) {
							if (this.descriptionVariables.elementAt(j).stringBuffer.elementAt(ii).equals(this.descriptionVariables.elementAt(j).stringData [i])) {
								Clusters.elementAt(ii).add(exp);
							}
						}
					    
					
					}
					
					
					seurat.dataManager.ExpClusters.add(new Clustering(this.descriptionVariables.elementAt(j).getName(),Clusters,this.descriptionVariables.elementAt(j).stringBuffer));
					
					
					
				}

			}
			
			
			seurat.openDescriptionItem.setEnabled(false);
			
			

		
			
			
		} catch (IOException e) {
			System.out.println("Wrong file format  " + e);
		}

	}
	
	
	
	
	
	
	
	

	public boolean doesntContain(Vector<String> buffer, String word) {
		for (int i = 0; i < buffer.size(); i++) {
			if (buffer.elementAt(i).equals(word))
				return false;
		}
		return true;
	}

}
