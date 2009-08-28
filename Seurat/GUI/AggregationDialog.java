package GUI;
import Settings.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

import javax.swing.event.*;
import Data.ISelectable;
import Data.Variable;

public class AggregationDialog extends JFrame{
	Seurat seurat;
	
	public JTextField field = new JTextField("   ");
	

	Settings settings;
	
	ColorListener listener;

	AggregationDialog dialog = this;
	
	
	
	
	public AggregationDialog(Seurat seurat, ColorListener listener, int aggr) {
		super("Aggregation");

		this.listener = listener;
		this.seurat = seurat;
		settings = seurat.settings;
		this.listener = listener;
		
		
		this.setBounds(360, 0, 170, 100);

		
		
		
		
		
		
		
	
		
		this.getContentPane().setLayout(new BorderLayout());

			
        JPanel invertColorPanel = new JPanel();
		invertColorPanel.setLayout(new BorderLayout());
	
				
		
		JPanel pixelSizePanel = new JPanel();
		pixelSizePanel.setBorder(BorderFactory.createEtchedBorder());
		pixelSizePanel.setLayout(new FlowLayout(FlowLayout.LEFT
				));
		
		
		
	    JButton btn = new JButton("Change");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int aggr = Integer.parseInt(field.getText().replaceAll(" ", ""));
				dialog.listener.setAggregation(aggr);		
			    dialog.setVisible(false);	
			}
			
		});
		
		pixelSizePanel.add(new JLabel("Aggregation: "));
		
		field = new JTextField("   " + aggr);
		
		pixelSizePanel.add(field);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(btn);
		
		this.getContentPane().add(panel,BorderLayout.SOUTH);
		
		
		
		this.getContentPane().add(pixelSizePanel,BorderLayout.CENTER);
		
		
		
		
		
		
		
		
		this.setVisible(true);
		
		
		
		
		
		
		
		

	}
	
	
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	


	