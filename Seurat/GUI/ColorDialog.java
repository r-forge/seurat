package GUI;
import Settings.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

import javax.swing.event.*;
import Data.ISelectable;
import Data.Variable;

public class ColorDialog extends JFrame{
	Seurat seurat;

	JCheckBox box = new JCheckBox("  invert shading  ");
	
	
	public JTextField pixelWField = new JTextField("   ");
	public JTextField pixelHField = new JTextField("   ");
	
	

	Settings settings;
	
	ColorListener listener;

	ColorDialog dialog = this;
	
	int PixelW; 
	int PixelH;
	int Model;
	
	int Pixel;
	
	
	public ColorDialog(Seurat seurat, ColorListener listener, int PixelW, int PixelH) {
		super("Color Settings");

		this.listener = listener;
		this.seurat = seurat;
		settings = seurat.settings;
		this.listener = listener;
		
		
		this.setBounds(360, 0, 170, 130);

		
		
		
		
		
		
		
	
		
		this.getContentPane().setLayout(new BorderLayout());

			
        JPanel invertColorPanel = new JPanel();
		invertColorPanel.setLayout(new BorderLayout());
	
				
		
		JPanel pixelSizePanel = new JPanel();
		pixelSizePanel.setBorder(BorderFactory.createEtchedBorder());
		pixelSizePanel.setLayout(new FlowLayout(FlowLayout.LEFT
				));
		
		pixelWField.setText("   " + PixelW);
		//JButton btn = new JButton("Change");
		
		pixelSizePanel.add(new JLabel("Pixel Width:   "));
		pixelSizePanel.add(pixelWField);
		
		
		
		
		pixelHField.setText("   " + PixelH);
		JButton btn = new JButton("Change");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int PixelH = Integer.parseInt(pixelHField.getText().replaceAll(" ", ""));
				int PixelW = Integer.parseInt(pixelWField.getText().replaceAll(" ", ""));
				dialog.listener.applyNewPixelSize(PixelW,PixelH);
				dialog.listener.applyNewPixelSize();			
			    dialog.setVisible(false);	
			}
			
		});
		
		pixelSizePanel.add(new JLabel("Pixel Height: "));
		pixelSizePanel.add(pixelHField);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(btn);
		
		this.getContentPane().add(panel,BorderLayout.SOUTH);
		
		
		
		this.getContentPane().add(pixelSizePanel,BorderLayout.CENTER);
		
		
		
		
		
		
		
		
		this.setVisible(true);
		
		
		
		
		
		
		
		

	}
	
	
	
	
	
	
	
	
	
	
	
	public ColorDialog(Seurat seurat, ColorListener listener, int Pixel) {
		super("Color Settings");

		this.listener = listener;
		this.seurat = seurat;
		settings = seurat.settings;
		this.listener = listener;
		
		
		this.setBounds(360, 0, 170, 130);

		
		this.getContentPane().setLayout(new BorderLayout());

			
        //JPanel invertColorPanel = new JPanel();
		//invertColorPanel.setLayout(new BorderLayout());
	
				
		
		JPanel pixelSizePanel = new JPanel();
		pixelSizePanel.setBorder(BorderFactory.createEtchedBorder());
		pixelSizePanel.setLayout(new FlowLayout(FlowLayout.LEFT
				));
		
		pixelWField.setText("   " + Pixel);
		//JButton btn = new JButton("Change");
		
		pixelSizePanel.add(new JLabel("Pixel Size:   "));
		pixelSizePanel.add(pixelWField);
		
	
		JButton btn = new JButton("Change");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int PixelW = Integer.parseInt(pixelWField.getText().replaceAll(" ", ""));
				dialog.listener.applyNewPixelSize(PixelW,PixelW);
				dialog.listener.applyNewPixelSize();			
			    dialog.setVisible(false);	
			}
			
		});
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(btn);
		
		this.getContentPane().add(panel,BorderLayout.SOUTH);
		
		
		
		this.getContentPane().add(pixelSizePanel,BorderLayout.CENTER);
		
			
		
		this.setVisible(true);
		

	}
	
	
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	


	

