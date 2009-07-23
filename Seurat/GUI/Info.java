package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import Data.Variable;

public class Info extends JFrame{

Seurat seurat;	


JMenuItem item = new JMenuItem("Info: ");
	
Info info = this;

	public Info(Seurat seurat, String name) {
		super(name);
        item.setName("Info: " + name);
		
		
		this.seurat = seurat;
		this.getContentPane().setLayout(new BorderLayout());
		


		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				info.setVisible(true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				info.seurat.windowMenu.remove(item);
			}
		});

		
		this.setBounds(330,0,330,500);
		this.setVisible(true);
		
	}
}
