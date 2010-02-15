package GUI;

import java.awt.BorderLayout;
import java.awt.CheckboxGroup;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import Data.DescriptionVariable;

public class EventDialog extends JFrame{
	
	Vector<DescriptionVariable> TimeVars = new Vector();
	
	Vector<DescriptionVariable> StatusVars = new Vector(); 
	
Vector<String> TimeVarsS = new Vector();
	
	Vector<String> StatusVarsS = new Vector(); 
	
	
	JComboBox time;

	JComboBox status;

    Seurat seurat; 
    
    EventDialog dialog = this;
    
    JRadioButton btn1,btn2;
    CheckboxGroup gr;
	
	public EventDialog(Seurat seurat) {
		
		this.seurat = seurat;
		
		for (int i = 0 ; i < seurat.dataManager.descriptionVariables.size(); i++) {
			DescriptionVariable var = seurat.dataManager.descriptionVariables.elementAt(i);
			if (var.isDouble) {
				TimeVars.add(var);
				TimeVarsS.add(var.name);
			}
			else if (var.stringBuffer.size() == 2) {
				StatusVars.add(var);
				StatusVarsS.add(var.name);
			}
		}
		
		if (StatusVarsS.size() == 0 || TimeVarsS.size() == 0) JOptionPane.showMessageDialog(seurat,
			    "Varaibles not fount",
			    "Error",
			    JOptionPane.ERROR_MESSAGE);
		
		
		time = new JComboBox(TimeVarsS);
		
		status = new JComboBox(StatusVarsS);
		
		
		JButton ok = new JButton("ok");
		ok.addActionListener(
		       new ActionListener(){
		    	   public void actionPerformed(ActionEvent e) {
		    		   int i = time.getSelectedIndex();
		    		   int j = status.getSelectedIndex();
		    		   new EventChart(dialog.seurat, TimeVars.elementAt(i), StatusVars.elementAt(j));
		    	   }
		       }		
		);
		
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2,2));
		p.add(new JLabel(" Time:  "));
		p.add(time);
		p.add(new JLabel(" Status:  "));
		p.add(status);
		
		
		
		
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(p,BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		
		
		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p.add(ok);
		panel.add(p,BorderLayout.SOUTH);
		getContentPane().add(panel,BorderLayout.SOUTH);
		
		setBounds(400,0,300,130);
		setVisible(true);
		
	}
	
}
