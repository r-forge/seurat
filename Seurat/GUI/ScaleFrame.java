package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ScaleFrame extends JFrame{
            
public Seurat seurat;	
ButtonGroup gr = new ButtonGroup();
ScaleFrame frame = this;
JRadioButton btn1 = new JRadioButton("Global"), btn2 = new JRadioButton("Columns");

	     public ScaleFrame(Seurat seurat) {
	    	 super("Scaling Dialog");
	    	 this.seurat = seurat;
             this.getContentPane().setLayout(new BorderLayout());
             JPanel p = new JPanel();
             p.setLayout(new BorderLayout());
             p.add(btn1,BorderLayout.WEST);
             p.add(btn2,BorderLayout.EAST);
             gr.add(btn1);
             
             gr.add(btn2);
             this.getContentPane().add(p,BorderLayout.CENTER);
             if (seurat.globalScaling) btn1.setSelected(true);
             else btn2.setSelected(true);
             p.setBorder(BorderFactory.createEtchedBorder());
             
             p = new JPanel();
             p.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    	 JButton ok = new JButton("ok");
	    	 ok.addActionListener(new ActionListener() {
	    		 
	    		 public void actionPerformed(ActionEvent e) {
	    			 if (btn1.isSelected()) frame.seurat.globalScaling= true;
	    			 else frame.seurat.globalScaling = false;
	    			 
	    			 frame.setVisible(false);
	    		 }
	    	 });
	    	 p.add(ok);
             this.getContentPane().add(p,BorderLayout.SOUTH);
	    	 
             
	    	 this.setBounds(350,0,200,100);
	    	 this.setVisible(true);
	     }
	  
}
