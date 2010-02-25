package GUI;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;


public class LoadingDialog extends JFrame{

	Seurat seurat;
	LoadingDialog dialog = this;
	
	
	
	
	
	
	JTextField fieldChromosomeNumber;
	JTextField fieldTranscriptStart;
	JTextField fieldTranscriptEnd;
	JTextField fieldNucleotidePosition;
	JTextField fieldCloneStart;
	JTextField fieldCloneEnd;
	JTextField fieldChrCen;
	JTextField fieldCytoBand;
	JTextField fieldStates;
	
	
	
	
	
	
	public LoadingDialog(Seurat seurat) {
		super("Loading Settings");
		this.seurat = seurat;
		
		create();
		
		this.setBounds(350,0,670,400);
		this.setVisible(true);
	}
	
	
	
	
	
	public void create() {
		
		this.getContentPane().removeAll();
		
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEtchedBorder());
		p.setLayout(new GridLayout(9,2));
		
		JLabel l = new JLabel("  Chromosome Number(Genannotations, CGH, SNP):");
		fieldChromosomeNumber = new JTextField(seurat.dataManager.ChromosomeNumber);
		p.add(l);
		p.add(fieldChromosomeNumber);
		
		l = new JLabel("  Transcription Start(Genannotations):");
		fieldTranscriptStart = new JTextField(seurat.dataManager.TranscriptStart);
		p.add(l);
		p.add(fieldTranscriptStart);
		
		l = new JLabel("  Transcription End(Genannotations):");
		fieldTranscriptEnd = new JTextField(seurat.dataManager.TranscriptEnd);
		p.add(l);
		p.add(fieldTranscriptEnd);
		
		
		l = new JLabel("  Clone Start(CGH):");
		fieldCloneStart = new JTextField(seurat.dataManager.CloneStart);
		p.add(l);
		p.add(fieldCloneStart);
		
		
		l = new JLabel("  Clone End(CGH):");
		fieldCloneEnd = new JTextField(seurat.dataManager.CloneEnd);
		p.add(l);
		p.add(fieldCloneEnd);
	
		l = new JLabel("  Chromosome center(CGH,SNP):");
		fieldChrCen = new JTextField(seurat.dataManager.ChrCen);
		p.add(l);
		p.add(fieldChrCen);
		
		l = new JLabel("  CytoBand(CGH,SNP):");
		fieldCytoBand = new JTextField(seurat.dataManager.CytoBand);
		p.add(l);
		p.add(fieldCytoBand);
		
		
		
		
		l = new JLabel("  States(CGH, SNP):");
		fieldStates = new JTextField(seurat.dataManager.States);
		p.add(l);
		p.add(fieldStates);
		
		l = new JLabel("  Nucleotide Position(SNP):");
		fieldNucleotidePosition = new JTextField(seurat.dataManager.NucleotidePosition);
		p.add(l);
		p.add(fieldNucleotidePosition);
		

		
		this.getContentPane().setLayout(new BorderLayout());
		

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton b = new JButton("Save");
		b.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					save();
					setVisible(false);
				}
			}
		);
		
		panel.add(b);
		
		b = new JButton("Save file");
		b.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveFile();
					setVisible(false);
				}
			}
		);
		
		this.getContentPane().add(p,BorderLayout.CENTER);
		
		
		
		
		
		panel.add(b);
		
		b = new JButton("Load file");
		panel.add(b);
		
		b.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						
					try {
						
						
						FileDialog fileDialog = new FileDialog(dialog, "Open Loading Settings", 0);
						fileDialog.setVisible(true);
						
						if (fileDialog.getFile() == null) return;
						
						BufferedReader bfr = new BufferedReader(new FileReader(fileDialog
									.getDirectory()
									+ "/" + fileDialog.getFile()));
						
						loadFile(seurat,bfr);
						
						create();
						
						
					}
					catch (Exception ee) {
						
				           ee.printStackTrace();	
				           
				           JOptionPane.showMessageDialog(dialog,
								    "Wrong file format.",
								    "Load Error",
								    JOptionPane.ERROR_MESSAGE);
					}	
					
					setVisible(false);
						
						
					}
				}
			);
		
		this.getContentPane().add(panel,BorderLayout.SOUTH);
		this.repaint();
		this.setVisible(true);
	}
	
	
	
	public void saveFile() {
		
		try {
			
		
		FileDialog fileDialog = new FileDialog(this, "Save Loading Settings", 1);
		fileDialog.setVisible(true);

		if (fileDialog.getFile() == null) return;
		
		BufferedWriter bfr = null;
		bfr = new BufferedWriter(new FileWriter(fileDialog
					.getDirectory()
					+ "/" + fileDialog.getFile()));
		
		
		
		
		//  Geneannotations
		
		bfr.write("Chromosome number="+fieldChromosomeNumber.getText()+"\n");
		bfr.write("Nucleotide position="+fieldNucleotidePosition.getText()+"\n");
		bfr.write("Transcription start="+fieldTranscriptStart.getText()+"\n");
		bfr.write("Transcription end="+fieldTranscriptEnd.getText()+"\n");
		bfr.write("Clone start="+fieldCloneStart.getText()+"\n");
		bfr.write("Clone end="+fieldCloneEnd.getText()+"\n");
		bfr.write("Chromosome center="+fieldChrCen.getText()+"\n");
		bfr.write("CytoBand="+fieldCytoBand.getText()+"\n");
		bfr.write("States="+fieldStates.getText()+"\n");
			
		
		bfr.close();
			
		
		}
		catch (Exception e) {
	           e.printStackTrace();		
		}
		
	}
	
	
	
	
	

	public void save() {
		
		
		seurat.dataManager.ChromosomeNumber = fieldChromosomeNumber.getText();
        seurat.dataManager.NucleotidePosition = fieldNucleotidePosition.getText();
        seurat.dataManager.TranscriptStart = fieldTranscriptStart.getText();
        seurat.dataManager.TranscriptEnd = fieldTranscriptEnd.getText();
        seurat.dataManager.CloneStart = fieldCloneStart.getText();
        seurat.dataManager.CloneEnd = fieldCloneEnd.getText();
        seurat.dataManager.ChrCen = fieldChrCen.getText();
        seurat.dataManager.CytoBand = fieldCytoBand.getText();
        seurat.dataManager.States = fieldStates.getText();
		
		
		
	}
	
	
	
	
	
	public static void loadFile(Seurat seurat, BufferedReader bfr) throws IOException {
		
			
			String line = null;
			
			while ((line = bfr.readLine()) != null) {
				StringTokenizer str = new StringTokenizer(line, "=");
                String name = str.nextToken();
                String value = str.nextToken();
                
                if (name.contains("Chromosome number")) seurat.dataManager.ChromosomeNumber = value;
                if (name.contains("Nucleotide position")) seurat.dataManager.NucleotidePosition = value;
                if (name.contains("Transcription start")) seurat.dataManager.TranscriptStart = value;
                if (name.contains("Transcription end")) seurat.dataManager.TranscriptEnd = value;
                if (name.contains("Clone start")) seurat.dataManager.CloneStart = value;
                if (name.contains("Clone end"))  seurat.dataManager.CloneEnd = value;
                if (name.contains("Chromosome center")) seurat.dataManager.ChrCen = value;
                if (name.contains("CytoBand"))  seurat.dataManager.CytoBand = value;
                if (name.contains("States")) seurat.dataManager.States = value;
                
        		
			}
			
			
			
			
	}
	
}
