package Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import java.awt.*;


public class DataLoader {
	
	
	

    
	
	public void loadGeneExpressions(DataManager dataManager,BufferedReader bfr, String fileName,FileDialog fileDialog) {
		



		try {

	

		

			dataManager.variables = new Vector();

			String line = bfr.readLine();

			MyStringTokenizer stk = new MyStringTokenizer(line);
			int col = 0;
			while (stk.hasMoreTokens()) {
				dataManager.variables.add(new Variable(dataManager,stk.nextToken(),
						Variable.Double, col));
				col++;
			}

			
			
			
			int length = 0;
			boolean format = false;

			while ((line = bfr.readLine()) != null) {

				if (line.equals("")) {
					format = true;
					line = bfr.readLine();
				}
				length++;
			}
			if (format)
				length--;

			if (fileName == null)
				bfr = new BufferedReader(new FileReader(fileDialog
						.getDirectory()
						+ "/" + fileDialog.getFile()));
			else
				bfr = new BufferedReader(new FileReader(new File(fileName)));

			line = bfr.readLine();

			for (int i = 0; i < dataManager.variables.size(); i++) {
				Variable var = dataManager.variables.elementAt(i);
				var.setColumn(new double[length]);
				var.isNotNA = new boolean[length];
			//	var.isSelected = new boolean[length];
				var.stringData = new String[length];

			}
			
			dataManager.RowCount = length;

		//	System.out.println(length);
			
			int len = 0;
			while ((line = bfr.readLine()) != null) {
				if (line.equals("")) {

					line = bfr.readLine();
					if (line == null)
						break;
				}

				MyStringTokenizer st = new MyStringTokenizer(line,col);
				String token;
			//	System.out.println(line);

				for (int i = 0; i < col; i++) {
					token = st.nextToken();

					if ((dataManager.variables.elementAt(i)).type == Variable.Double) {
						try {
							(dataManager.variables.elementAt(i)).setValue(len,new Double(
									token).doubleValue());

							(dataManager.variables.elementAt(i)).isNotNA[len] = true;
							if ((dataManager.variables.elementAt(i)).getValue(len) > (dataManager.variables
									.elementAt(i)).max) {
								(dataManager.variables.elementAt(i)).max = (dataManager.variables
										.elementAt(i)).getValue(len);
							}
							if ((dataManager.variables.elementAt(i)).getValue(len) < (dataManager.variables
									.elementAt(i)).min) {
								(dataManager.variables.elementAt(i)).min = (dataManager.variables
										.elementAt(i)).getValue(len);
							}

							if ((dataManager.variables.elementAt(i)).getValue(len) > dataManager.maxValue) {
								dataManager.maxValue = (dataManager.variables
										.elementAt(i)).getValue(len);
							}
							if ((dataManager.variables.elementAt(i)).getValue(len) < dataManager.minValue) {
								dataManager.minValue = (dataManager.variables
										.elementAt(i)).getValue(len);
							}

						} catch (Exception e) {
							if (token.equals("NA") || token.equals("")) {
								(dataManager.variables.elementAt(i)).isNotNA[len] = false;
								(dataManager.variables.elementAt(i)).setValue(len,dataManager.NA);
							} else {
								(dataManager. variables.elementAt(i)).stringData[len] = token;
								(dataManager.variables.elementAt(i)).type = Variable.String;
								(dataManager.variables.elementAt(i)).isExperiment = false;
							}
						}
					} else {
						(dataManager.variables.elementAt(i)).stringData[len] = token;
					}
				}

				len++;
			}

		

			//dataManager.selectedRows = new boolean[dataManager.RowCount];
			//dataManager.selectedVariables = new boolean[dataManager.variables.size() - 2];

			 dataManager.Genes = new Vector();
			 for (int i = 0; i < dataManager.RowCount; i++) {
				 dataManager.Genes.add(new Gene(dataManager.variables.elementAt(0).stringData [i],i,dataManager));
				 
			 }
			
			
			
			dataManager.Experiments = new Vector();
			dataManager.ExperimentDescr = new Vector();

			for (int i = 0; i < dataManager.variables.size(); i++) {
				System.out.println(dataManager.variables.elementAt(i).name);
				if (dataManager.variables.elementAt(i).isExperiment && !dataManager.variables.elementAt(i).name.equals(dataManager.ChromosomeNumber) && !dataManager.variables.elementAt(i).name.equals(dataManager.NucleotidePosition) )
					dataManager.Experiments.add(dataManager.variables.elementAt(i));
				else {
					dataManager.ExperimentDescr.add(dataManager.variables.elementAt(i));
				}
			}
			
			
			
			for (int i = 0; i < dataManager.Experiments.size(); i++) {
				dataManager.Experiments.elementAt(i).calculateMean();
				dataManager.Experiments.elementAt(i).ID = i;
				
				
			}
			
			
			/*
			for (int j = 0; j < dataManager.getRowCount(); j++) { 
			for (int i = 0; i < dataManager.Experiments.size(); i++) {
				Variable var = dataManager.Experiments.elementAt(i);
			    System.out.print(var.getRealValue(j) + "	");	
			}
			    	System.out.println();
			
			}
			
			*/

			// System.out.println(" Maxvalue: " + this.maxValue);

		} catch (IOException e) {
			System.out.println("Wrong file format  " + e);
		}
		
	}
	
	
}
