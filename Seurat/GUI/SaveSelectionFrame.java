package GUI;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.*;

import Data.GeneVariable;

public class SaveSelectionFrame extends JFrame {

	JCheckBox expressionBox = new JCheckBox("  save gene expression dataset  ",
			true);
	JCheckBox experimentsBox = new JCheckBox(
			"  save experiments descriptions  ");
	JCheckBox genesBox = new JCheckBox("  save gene annotations  ");
	Seurat seurat;
	SaveSelectionFrame zeiger = this;

	public SaveSelectionFrame(Seurat seurat) {
		super("Save Selection");
        this.seurat = seurat;
		
		
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		panel.add(expressionBox);
		panel.add(experimentsBox);
		panel.setBorder(BorderFactory.createEtchedBorder());
		if (seurat.descriptionFrame == null)
			experimentsBox.setEnabled(false);
		else
			experimentsBox.setSelected(true);

		if (seurat.geneFrame == null)
			genesBox.setEnabled(false);
		else
			genesBox.setSelected(true);

		panel.add(genesBox);
		this.getContentPane().add(panel, BorderLayout.CENTER);

		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3));
		panel.add(new JLabel("          "));

		JButton btn = new JButton("ok");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				try {
				
				
					
				if (expressionBox.isSelected()) {
					 FileDialog fileDialog = new FileDialog(zeiger.seurat, "Save Geneexpression Selection", 1);
						fileDialog.setVisible(true);
				
						
						BufferedWriter bfr = null;
						bfr = new BufferedWriter(new FileWriter(fileDialog
									.getDirectory()
									+ "/" + fileDialog.getFile()));
						
						Vector Experiments = new Vector();
						
						
						for (int i = 0; i < zeiger.seurat.dataManager.ExperimentDescr.size(); i++) {
							Experiments.add(zeiger.seurat.dataManager.ExperimentDescr.elementAt(i));
						}
						
						
						for (int i = 0; i < zeiger.seurat.dataManager.Experiments.size(); i++) {
							if (zeiger.seurat.dataManager.Experiments.elementAt(i).isSelected()) Experiments.add(zeiger.seurat.dataManager.Experiments.elementAt(i));
						}
						
						zeiger.seurat.dataManager.saveGeneExpressionData(Experiments, bfr);
				
					
				}
				
				
				
				
				if (experimentsBox.isSelected()) {
					
					
					FileDialog fileDialog = new FileDialog(zeiger.seurat, "Save Experiment Descriptions", 1);
					fileDialog.setVisible(true);
			
					
					BufferedWriter bfr = null;
					bfr = new BufferedWriter(new FileWriter(fileDialog
								.getDirectory()
								+ "/" + fileDialog.getFile()));
					
					Vector ExperimentDescriptions = new Vector();
					
					
					for (int i = 0; i < zeiger.seurat.descriptionFrame.descriptionVariables.size(); i++) {
						 ExperimentDescriptions.add(zeiger.seurat.descriptionFrame.descriptionVariables.elementAt(i));
					}
					
					zeiger.seurat.dataManager.saveExperimentsDescriptions(ExperimentDescriptions, bfr);
					
					
					
					
					
					

				}
				if (genesBox.isSelected()) {
					FileDialog fileDialog = new FileDialog(zeiger.seurat, "Save Gene Annotations", 1);
					fileDialog.setVisible(true);
			
					
					BufferedWriter bfr = null;
					bfr = new BufferedWriter(new FileWriter(fileDialog
								.getDirectory()
								+ "/" + fileDialog.getFile()));
					
					Vector<GeneVariable> GeneAnnotations = new Vector();
					
					for (int i = 0; i < zeiger.seurat.geneFrame.geneVariables.size(); i++) {
							GeneAnnotations.add(zeiger.seurat.geneFrame.geneVariables.elementAt(i));
						    
							
					}
					
				
					zeiger.seurat.dataManager.saveGeneAnnotations(GeneAnnotations, bfr);
					
					
				}
				
				
				
				
				
				
			    }
				catch (Exception ee) {
				     ee.printStackTrace();
				}	
				
				zeiger.setVisible(false);
				
				
			}
		});

		panel.add(btn);
		btn = new JButton("cancel");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zeiger.setVisible(false);
			}
		});
		panel.add(btn);

		this.getContentPane().add(panel, BorderLayout.SOUTH);

		this.setBounds(200, 200, 266, 150);
		this.setVisible(true);

	}

}
