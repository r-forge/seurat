package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Data.MyColor;
import Settings.Settings;
import Tools.Tools;

public class MyColorChooser extends JDialog{
	
	int color;
	
	JSlider slider = new JSlider(JSlider.VERTICAL,
            0, 100, 50);
	MyCPanel panel;
	
	MyColorChooser z = this;
	ColorSettings frame;
	boolean pos;
	
	
	public MyColorChooser(ColorSettings frame, int c, String s, boolean pos) {
		super(frame,s,true);
	this.frame = frame;
	this.pos = pos;	
	this.color = c;
		this.getContentPane().setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1,1));
		JPanel pp = new JPanel();
		pp.setLayout(new BorderLayout());
		
		pp.add(slider,BorderLayout.WEST);
		
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setValue(100*c/360);

		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() 
		{
		public void windowClosing(WindowEvent we)
		{
			addWindowListener(new WindowAdapter() 
			{
			public void windowClosing(WindowEvent we)
			{
				
				if (z.pos) z.frame.seurat.PosColor = z.color;
				else z.frame.seurat.NegColor = z.color;
				z.frame.seurat.repaintWindows();
				z.frame.repaint();
				
				setVisible(false);
			}
			});
		}
		});
		
		pp.add(new ColorPanel(), BorderLayout.CENTER);
		p.add(pp);
		pp.setBorder(BorderFactory.createEtchedBorder());
		panel = new MyCPanel(c,this);
	    pp = new JPanel();
	    pp.setLayout(new BorderLayout());
		//pp.add(panel,BorderLayout.CENTER);
	//	pp.setBorder(BorderFactory.createEtchedBorder());
	    
	//	p.add(pp);
	    slider.addChangeListener(panel);
		
		
		this.getContentPane().add(p,BorderLayout.CENTER);
		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton ok = new JButton("ok");
		ok.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (z.pos) z.frame.seurat.PosColor = z.panel.c;
				else z.frame.seurat.NegColor = z.panel.c;
				z.frame.seurat.repaintWindows();
				z.frame.repaint();
				
				setVisible(false);
			}
		});
		p.add(ok);
		
		
		ok = new JButton("cancel");
		ok.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (z.pos) z.frame.seurat.PosColor = z.color;
				else z.frame.seurat.NegColor = z.color;
				z.frame.seurat.repaintWindows();
				z.frame.repaint();
				
				setVisible(false);
			}
		});
		p.add(ok);
		
		
		
		
		
		this.getContentPane().add(p,BorderLayout.SOUTH);
		this.setBounds(350,0,200,300);
		this.setVisible(true);
	}

}



class ColorPanel extends JPanel {
	public ColorPanel() {
		
	}
	
    public void paint(Graphics g) {
    	for (int i = 0; i < 100; i++) {
    		g.setColor(Tools.convertHLCtoRGB(new MyColor(60,100*(99-i)/100, 360*(99-i)/100 )).getRGBColor());
    		g.fillRect(0,10 + i*(this.getHeight()-20)/100,this.getWidth(), (this.getHeight()-20)/100+1);
    	}
    } 	
	
}





class MyCPanel extends JPanel implements ChangeListener {

	int c;
	
	MyColorChooser mc;
	
	public MyCPanel(int c, MyColorChooser mc) {
		this.c = c;
		this.mc = mc;
	}  
	
    public void paint(Graphics g) {
    	/*
    		g.setColor(Tools.convertHLCtoRGB(new MyColor(60,100* c/360, c )).getRGBColor());
    		g.fillRect(15,15,this.getWidth()-30, this.getHeight()-30);
    	    g.setColor(Color.BLACK);
    	    g.drawRect(14,14,this.getWidth()-30+1, this.getHeight()-30+1);
    	    */
    	
    	int max = 100;
    	for (int i = 1; i < max; i++) {
    		MyColor color = Tools.convertHLCtoRGB(new MyColor(i,i, c ));
    		if (color.x < 0) System.out.println(" i: " + i + " c: " + c +  "  " + color.x + " " + color.y + " "+ color.z);
    		g.setColor(color.getRGBColor());
    		
    		g.fillRect(5,5+i*(this.getHeight()-10)/max,this.getWidth()-10, (this.getHeight()-10)/max+1);
    	}
     	   
    }

	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JSlider s = (JSlider)e.getSource();
		c =  360*s.getValue()/100;
		repaint();
		
		
		if (mc.pos) mc.frame.seurat.PosColor = mc.panel.c;
		else mc.frame.seurat.NegColor = mc.panel.c;
		mc.frame.seurat.repaintWindows();
		mc.frame.repaint();
		
		
    	
	} 	
	
}






