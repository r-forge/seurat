package GUI;
import java.util.*;
import java.io.*;

import javax.swing.*;

import java.awt.*;

import Data.GeneVariable;
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

	public GeneDescriptionFrame(Seurat amlTool, FileDialog fileDialog) {
		super("Gene Descriptor");
		this.seurat = amlTool;
		this.loadLogos();

		openDescription(fileDialog);

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
		this.setVisible(true);

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

	public void openDescription(FileDialog fileDialog) {

		try {

			BufferedReader bfr = new BufferedReader(new FileReader(fileDialog
					.getDirectory()
					+ "/" + fileDialog.getFile()));

			this.geneVariables = new Vector();

			String line = bfr.readLine();

			MyStringTokenizer stk = new MyStringTokenizer(line);
			int col = 0;
			while (stk.hasMoreTokens()) {
				geneVariables.add(new GeneVariable(stk
						.nextToken(), GeneVariable.Double,seurat.dataManager.Genes));
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
				if (j == this.geneVariables.size()-1) {
					this.geneVariables.elementAt(j).isList = true;
					this.geneVariables.elementAt(j).calculateList();
		
				}
			
				

			}
			
			seurat.dataManager.geneVariables = geneVariables;

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