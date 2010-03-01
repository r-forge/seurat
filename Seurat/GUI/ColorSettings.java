package GUI;
import Settings.*;
import Tools.Tools;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.*;

import Data.Variable;

public class ColorSettings extends JFrame{
	Seurat seurat;

	ColorSettings cSettings = this;

	JTextField aFieldPos = new JTextField("         ");

	JTextField bFieldPos = new JTextField("         ");

	JSlider aSliderPos = new JSlider();
	
	JSlider bSliderPos = new JSlider();
	
	
	JTextField aFieldNeg = new JTextField("         ");

	JTextField bFieldNeg = new JTextField("         ");

	JSlider aSliderNeg = new JSlider();
	
	JSlider bSliderNeg = new JSlider();

	JCheckBox box = new JCheckBox("  invert shading  ");
	
	
	JTextField pixelWField = new JTextField("   ");
	JTextField pixelHField = new JTextField("   ");
	
	FunctionPanel fPanel;

	Settings settings;
	
	
	public ColorSettings(Seurat seurat) {
		super("Color Settings");

		this.seurat = seurat;
		settings = seurat.settings;
		
		this.setBounds(0, 450, 530, 350);

		
		
		
		
		
		
		
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(1,2));
		
		
		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(gridPanel, BorderLayout.CENTER);

		
		JPanel panel = new JPanel();
		gridPanel.add(panel);
		
		
		
		aFieldNeg.setText("    " + settings.getANeg());
		aFieldNeg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				settings.setANeg(Double.parseDouble(aFieldNeg.getText().replaceAll(" ", "")));
				
		 		aSliderNeg.setValue((int)Math.round(aSliderNeg.getMaximum()*settings.getANeg()));
		
		
		fPanel.aNeg = cSettings.settings.getANeg();
		fPanel.calculateColorHist();
		cSettings.repaint();
		cSettings.seurat.repaintWindows();
			}
		});
		
		
		bFieldNeg.setText("    " + settings.getBNeg());
		
		bFieldNeg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				cSettings.settings.setBNeg(Double.parseDouble(bFieldNeg.getText().replaceAll(" ", "")));
				
		 		bSliderNeg.setValue((int)Math.round(bSliderNeg.getMaximum()*cSettings.settings.getBNeg()/4));
		
		
		fPanel.bNeg = cSettings.settings.getBNeg();
		fPanel.calculateColorHist();
		cSettings.repaint();
		cSettings.seurat.repaintWindows();
			}
		});
		
		
		
		
		
		panel.add(new JLabel(" a:  "));
		panel.add(aFieldNeg);
		panel.add(aSliderNeg);
		
		aSliderNeg.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				cSettings.settings.setANeg((double) aSliderNeg.getValue()  
						/ aSliderNeg.getMaximum());
				
				aFieldNeg.setText(cSettings.settings.aNeg + " ");
				fPanel.aNeg = cSettings.settings.aNeg;
				fPanel.calculateColorHist();
				
				cSettings.repaint();
				cSettings.seurat.repaintWindows();
				
			}
		});
		
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		panel.add(new JLabel(" b:  "));
		panel.add(bFieldNeg);
panel.add(bSliderNeg);
		
		bSliderNeg.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (fPanel != null) { 
					
				cSettings.settings.bNeg = (double) 4 * bSliderNeg.getValue()  
						/ bSliderNeg.getMaximum();
				
				bFieldNeg.setText(cSettings.settings.bNeg + " ");
				fPanel.bNeg = cSettings.settings.bNeg;
				
				fPanel.calculateColorHist();
				cSettings.repaint();
				cSettings.seurat.repaintWindows();
				repaint();
				}
			}
		});
		
		bSliderNeg.setValue((int)Math.round((cSettings.settings.bNeg*bSliderNeg.getMaximum()/4)));
		
		
		
		
		
		
		
		
		
		
		
		
		
		panel = new JPanel();
		gridPanel.add(panel);
		
		
		
		aFieldPos.setText("    " + settings.aPos);
		aFieldPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				cSettings.settings.aPos = Double.parseDouble(aFieldPos.getText().replaceAll(" ", ""));
				
		 		aSliderPos.setValue((int)Math.round(aSliderPos.getMaximum()*cSettings.settings.aPos));
		
		
		fPanel.aPos = cSettings.settings.aPos;
		fPanel.calculateColorHist();
		cSettings.repaint();
		cSettings.seurat.repaintWindows();
			}
		});
		
		
		bFieldPos.setText("    " + settings.bPos);
		
		bFieldPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				cSettings.settings.bPos = Double.parseDouble(bFieldPos.getText().replaceAll(" ", ""));
				
		 		bSliderPos.setValue((int)Math.round(bSliderPos.getMaximum()*cSettings.settings.bPos/4));
		
		
		fPanel.bPos = cSettings.settings.bPos;
		fPanel.calculateColorHist();
		cSettings.repaint();
		cSettings.seurat.repaintWindows();
			}
		});
		
		
		
		
		
		panel.add(new JLabel(" a:  "));
		panel.add(aFieldPos);
		panel.add(aSliderPos);
		
		aSliderPos.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				cSettings.settings.aPos = (double) aSliderPos.getValue()  
						/ aSliderPos.getMaximum();
				
				aFieldPos.setText(cSettings.settings.aPos + " ");
				fPanel.aPos = cSettings.settings.aPos;
				fPanel.calculateColorHist();
				
				cSettings.repaint();
				cSettings.seurat.repaintWindows();
				
			}
		});
		
	
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		panel.add(new JLabel(" b:  "));
		panel.add(bFieldPos);
panel.add(bSliderPos);
		
		bSliderPos.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				if (fPanel != null) { 
				cSettings.settings.bPos = (double) 4 * bSliderPos.getValue()  
						/ bSliderPos.getMaximum();
				
				bFieldPos.setText(cSettings.settings.bPos + " ");
				fPanel.bPos = cSettings.settings.bPos;
				
				fPanel.calculateColorHist();
				cSettings.repaint();
				cSettings.seurat.repaintWindows();
				repaint();
				}
			}
		});
		
		bSliderPos.setValue((int)Math.round((cSettings.settings.bPos*bSliderPos.getMaximum()/4)));
		
		
		
		
		
		
		
		
int dimX = 150, dimY = 20;
		
		aSliderPos.setPreferredSize(new Dimension(dimX,dimY));
		bSliderPos.setPreferredSize(new Dimension(dimX,dimY));
		aSliderNeg.setPreferredSize(new Dimension(dimX,dimY));
		bSliderNeg.setPreferredSize(new Dimension(dimX,dimY));
		
		
		
		
		
		
		
		
		
		JPanel panel2 = new JPanel();
		panel2.setPreferredSize(new Dimension(200,220));
		panel2.setBorder(BorderFactory.createEtchedBorder());
		panel2.setLayout(new BorderLayout());
		fPanel = new FunctionPanel(seurat,settings.aPos, settings.bPos,settings.aNeg, settings.bNeg);
		panel2.add(fPanel,BorderLayout.CENTER);
		this.getContentPane().add(panel2, BorderLayout.NORTH);
		

		JButton applyBtn = new JButton("Apply");
		applyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cSettings.settings.aPos = Double.parseDouble(aFieldPos.getText());
				cSettings.settings.bPos = Double.parseDouble(bFieldPos.getText());
				cSettings.settings.aNeg = Double.parseDouble(aFieldNeg.getText());
				cSettings.settings.bNeg = Double.parseDouble(bFieldNeg.getText());
			
				cSettings.seurat.repaintWindows();
				repaint();
			}
		});
		
		JPanel invertColorPanel = new JPanel();
		invertColorPanel.setLayout(new BorderLayout());
		this.getContentPane().add(invertColorPanel,BorderLayout.SOUTH);
		box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (box.isSelected()) {
					cSettings.seurat.setModel(2);

				} else {
					cSettings.seurat.setModel(1);

				}
				
				repaint();
			}
		});
		invertColorPanel.add(box);
		
		
		if (settings.Model == 2) box.setSelected(true);
		else box.setSelected(false);
			
		
		
		/*
		
		JPanel pixelSizePanel = new JPanel();
		pixelSizePanel.setBorder(BorderFactory.createEtchedBorder());
		
		pixelWField.setText("   " + settings.PixelW);
		JButton btn = new JButton("Change");
		
		pixelSizePanel.add(new JLabel("Pixel Width: "));
		pixelSizePanel.add(pixelWField);
		pixelSizePanel.add(btn);
		
		
		
		
		pixelHField.setText("   " + settings.PixelH);
		btn = new JButton("Change");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int PixelH = Integer.parseInt(pixelHField.getText().replaceAll(" ", ""));
				int PixelW = Integer.parseInt(pixelWField.getText().replaceAll(" ", ""));
				cSettings.seurat.applyNewPixelSize(PixelW,PixelH);
				repaint();
			}
			
		});
		
		pixelSizePanel.add(new JLabel("Pixel Height: "));
		pixelSizePanel.add(pixelHField);
		pixelSizePanel.add(btn);
		
		
		
		invertColorPanel.add(pixelSizePanel,BorderLayout.SOUTH);
		
		*/
		
		
		
		
		
		
		this.setVisible(true);
		
		
		
		
		
		
		
		

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}





class FunctionPanel extends JPanel
implements MouseListener, MouseMotionListener{
	double aPos,bPos, aNeg,bNeg;
	int abstandLinks = 50;
	int abstandUnten = 50;
	
	int WIDTH; 
	
	Settings settings;
	
	int segments = 32;
	Seurat seurat;
	
	
	int [] posBins;
	int posMax;
	
	int [] negBins;
	int negMax;
	
	
	int [] posBinsColor;
	int posMaxColor;
	
	JColorChooser chooser;
	
	int [] negBinsColor;
	int negMaxColor;
	boolean dragPosMin=false,dragPosMax=false,dragNegMin= false,dragNegMax= false;
	
	public FunctionPanel(Seurat seurat, double aPos, double bPos, double aNeg, double bNeg) {
		this.aPos = aPos;
		this.bPos = bPos;
		this.aNeg = aNeg;
		this.bNeg = bNeg;
		
		this.seurat = seurat;
		this.settings = seurat.settings;
		this.calculateHist();
		this.calculateColorHist();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	
	@Override
	public void paint (Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		WIDTH = this.getWidth()/2;
		
		g.setColor(Color.black);
		
		
		for (int i = 0; i < WIDTH; i++) {
			double x = (double)i / WIDTH;
			g.fillRect((int)Math.round(WIDTH +abstandLinks + x * (WIDTH - abstandLinks- abstandLinks/4)),
					this.getHeight() - abstandUnten -(int)Math.round(fPos(x) * (this.getHeight()-abstandUnten- abstandUnten/4)), 
					   1, 
					   1);
		}
		
		for (int i = 0; i < WIDTH; i++) {
			double x = (double)i / WIDTH;
			double xx = (double)(i+1) / WIDTH;
			g.drawLine((int)Math.round(WIDTH +abstandLinks + x * (WIDTH - abstandLinks- abstandLinks/4)),
					this.getHeight() - abstandUnten -(int)Math.round(fPos(x) * (this.getHeight()-abstandUnten- abstandUnten/4)), 
					(int)Math.round(WIDTH +abstandLinks + xx * (WIDTH - abstandLinks- abstandLinks/4)),
					this.getHeight() - abstandUnten -(int)Math.round(fPos(xx) * (this.getHeight()-abstandUnten- abstandUnten/4))
					  );
		}
		
		
		g.drawLine(WIDTH +abstandLinks, this.getHeight()-abstandUnten, WIDTH +WIDTH - abstandLinks/4, this.getHeight()-abstandUnten);
		g.drawLine(WIDTH +abstandLinks, this.getHeight()-abstandUnten, WIDTH +abstandLinks,abstandUnten/8);
		
		
		for (int i = 0; i < segments; i++) {
			g.drawLine(WIDTH +abstandLinks+ (WIDTH - abstandLinks-abstandLinks/4)*i/segments, 
					this.getHeight()-abstandUnten-2,
					WIDTH +abstandLinks + (WIDTH  - abstandLinks -abstandLinks/4)*i/segments, 
					this.getHeight()-abstandUnten+2
					);
			
			
		}
		
		
		
		for (int i = 0; i < segments+1; i++) {
			
			g.drawRect(WIDTH +abstandLinks + (WIDTH - abstandLinks-abstandLinks/4)*i/segments, 
					this.getHeight()- 1- (abstandUnten- abstandUnten/4) * posBins [i]/posMax,
					(WIDTH  - abstandLinks -abstandLinks/4)/segments, 

					(abstandUnten - abstandUnten/4) * posBins [i]/posMax
					);
			
		g.drawRect(WIDTH  - abstandLinks/7 + abstandLinks - (abstandLinks - abstandLinks/4) * posBinsColor [segments-i]/posMaxColor, 
				   abstandUnten/4 + (this.getHeight() - abstandUnten- abstandUnten/4) *(i-1) / segments,
					

					(abstandLinks - abstandLinks/4) * posBinsColor [segments-i]/posMaxColor,
					
					(this.getHeight() - abstandUnten- abstandUnten/4)/ segments
		);
			
			
			
		}
		
		
		for (int i = 0; i < segments; i++) {
			
			float h = Color.RGBtoHSB(seurat.PosColor.getRed(), seurat.PosColor.getGreen(), seurat.PosColor.getBlue(), null) [0];
			float s = Color.RGBtoHSB(seurat.PosColor.getRed(), seurat.PosColor.getGreen(), seurat.PosColor.getBlue(), null) [1];
			float v = Color.RGBtoHSB(seurat.PosColor.getRed(), seurat.PosColor.getGreen(), seurat.PosColor.getBlue(), null) [2];

			float koeff = 1-(float)i/segments;
				
			Color c = (Color.getHSBColor(h,
					koeff*s, v));
            if (settings.Model == 2) c = Color.getHSBColor(h,
					s,(float) koeff*v);
            
            g.setColor(c);
        		
			
			g.fillRect(WIDTH+abstandLinks - abstandLinks/8, 
					   abstandUnten/8  +  (this.getHeight()-abstandUnten -abstandUnten/8)*i/segments , 
					    abstandLinks/10,
					    (this.getHeight()- abstandUnten)/segments +1 );
			
			
		}
		
		
		
		/**************************Neg******************************/
		
		
	WIDTH = this.getWidth()/2;
		
		g.setColor(Color.black);
		
		
		for (int i = 0; i < WIDTH; i++) {
			double x = (double)(i) / WIDTH;
			g.fillRect((int)Math.round(abstandLinks + (1-x) * (WIDTH - abstandLinks- abstandLinks/4)),
					this.getHeight() - abstandUnten -(int)Math.round(((fNeg(x))) * (this.getHeight()-abstandUnten- abstandUnten/4)), 
					   1, 
					   1);
		}
		
		
		for (int i = 0; i < WIDTH; i++) {
			double x = (double)(i) / WIDTH;
			double xx = (double)(i+1) / WIDTH;
			g.drawLine((int)Math.round(abstandLinks + (1-x) * (WIDTH - abstandLinks- abstandLinks/4)),
					this.getHeight() - abstandUnten -(int)Math.round(((fNeg(x))) * (this.getHeight()-abstandUnten- abstandUnten/4)), 
					(int)Math.round(abstandLinks + (1-xx) * (WIDTH - abstandLinks- abstandLinks/4)),
					this.getHeight() - abstandUnten -(int)Math.round(((fNeg(xx))) * (this.getHeight()-abstandUnten- abstandUnten/4)) 
					  );
		}
		
		g.drawLine(abstandLinks, this.getHeight()-abstandUnten, WIDTH - abstandLinks/4, this.getHeight()-abstandUnten);
		g.drawLine(abstandLinks, this.getHeight()-abstandUnten, abstandLinks,abstandUnten/8);
		
		
		for (int i = 0; i < segments; i++) {
			g.drawLine(abstandLinks+ (WIDTH - abstandLinks-abstandLinks/4)*i/segments, 
					this.getHeight()-abstandUnten-2,
					abstandLinks + (WIDTH  - abstandLinks -abstandLinks/4)*i/segments, 
					this.getHeight()-abstandUnten+2
					);
			
			
		}
		
		
		if (negMax != 0) {
		for (int i = 0; i < segments+1; i++) {
			
			g.drawRect(abstandLinks + (WIDTH - abstandLinks-abstandLinks/4)*i/segments, 
					this.getHeight()- 1- (abstandUnten- abstandUnten/4) * negBins [segments-i]/negMax,
					(WIDTH  - abstandLinks -abstandLinks/4)/segments, 

					(abstandUnten - abstandUnten/4) * negBins [segments-i]/negMax
					);
			
			g.drawRect(abstandLinks - abstandLinks /7 - (abstandLinks - abstandLinks/4) * negBinsColor [segments-i]/negMaxColor, 
					   abstandUnten/4 + (this.getHeight() - abstandUnten- abstandUnten/4) *(i-1) / segments,
						

						(abstandLinks - abstandLinks/4) * negBinsColor [segments-i]/negMaxColor,
						
						(this.getHeight() - abstandUnten- abstandUnten/4)/ segments);
		}
			
			
		}
		
		
		
		
		
		
		
		
		
		for (int i = 0; i < segments; i++) {
				
			
			float h = Color.RGBtoHSB(seurat.NegColor.getRed(), seurat.NegColor.getGreen(), seurat.NegColor.getBlue(), null) [0];
			float s = Color.RGBtoHSB(seurat.NegColor.getRed(), seurat.NegColor.getGreen(), seurat.NegColor.getBlue(), null) [1];
			float v = Color.RGBtoHSB(seurat.NegColor.getRed(), seurat.NegColor.getGreen(), seurat.NegColor.getBlue(), null) [2];

			float koeff = (float)i/segments;
				
			Color c = (Color.getHSBColor(h,
					(float) koeff*s, v));
            if (settings.Model == 2) c = Color.getHSBColor(h,
					s,(float) koeff*v);
            
            g.setColor(c);
        		
			
			g.fillRect(abstandLinks - abstandLinks/8, 
					   abstandUnten/8  +  (this.getHeight()-abstandUnten -abstandUnten/8)*i/segments , 
					    abstandLinks/10,
					    (this.getHeight()- abstandUnten)/segments +1 );
			}
			
		
			
			
		
		
		paintZeiger(g);
		
		
	}
	
	
	
	public void paintZeiger(Graphics g) {
		
		int WIDTH = this.getWidth()/2;
		
		g.setColor(Color.black);
		
		
		
		
		
		
		
		int x1 = abstandLinks + (int) Math.round((1-settings.negMin) * (WIDTH - abstandLinks - abstandLinks/4));
		int y1= this.getHeight()-abstandUnten;
		
		g.drawLine(x1, y1, x1, y1+8);
		g.drawLine(x1, y1+8, x1-8, y1+8);
		g.drawLine(x1-8, y1+8, x1, y1);
		
		
		
		x1 = abstandLinks + (int) Math.round((1-settings.negMax) * (WIDTH - abstandLinks - abstandLinks/4));
		y1= this.getHeight()-abstandUnten;
		
		g.drawLine(x1, y1, x1, y1+8);
		g.drawLine(x1, y1+8, x1+8, y1+8);
		g.drawLine(x1+8, y1+8, x1, y1);
		
		
		x1 = WIDTH+abstandLinks + (int) Math.round(settings.posMin * (WIDTH - abstandLinks - abstandLinks/4));
		y1= this.getHeight()-abstandUnten;
		
		g.drawLine(x1, y1, x1, y1+8);
		g.drawLine(x1, y1+8, x1+8, y1+8);
		g.drawLine(x1+8, y1+8, x1, y1);
		
		
		
		x1 = WIDTH+abstandLinks + (int) Math.round(settings.posMax * (WIDTH - abstandLinks - abstandLinks/4));
		y1= this.getHeight()-abstandUnten;
		
		g.drawLine(x1, y1, x1, y1+8);
		g.drawLine(x1, y1+8, x1-8, y1+8);
		g.drawLine(x1-8, y1+8, x1, y1);
		
		
		
	}
	
	
	public void calculateHist() {
		posBins = new int [segments+1];
		negBins = new int [segments+1];
		
		/*
		double max = 0;
		double min = 0;
		
		for (int i = 0; i < settings.Experiments.size(); i++) {
			for (int j = 0; j < settings.RowCount; j++) {
				Variable var = settings.Experiments.elementAt(i);
				if (var.getValue(j)>max) max = var.getValue(j);
				if (var.getValue(j)<min) min = var.getValue(j);
			}
			
		}
		*/
		
		
		double value;
		Variable var;
		for (int i = 0; i < seurat.dataManager.getExperiments().size(); i++) {
			for (int j = 0; j < seurat.dataManager.getRowCount(); j++) {
				var = seurat.dataManager.getExperiments().elementAt(i);
				value = var.getValue(j);
				if (value/var.max >= 0) posBins [(int)Math.round((value/var.max*segments))]++;
				else negBins [(int)Math.round((value/var.min*segments))]++;
				
			}
			
		}
		
		
		for (int i = 0; i < posBins.length; i++) {
			if (posBins [i]>posMax) posMax = posBins [i];
			if (negBins [i]>negMax) negMax = negBins [i];
		}
		
	}
	
	
	
	public double fPos(double xx) {
		
		if (xx < settings.posMin) return 0;
		if (xx > settings.posMax) return 1;
		
		
		double x = (xx - settings.posMin)/(settings.posMax- settings.posMin);
		if (x <= aPos) {
			return aPos * Math.pow(x/aPos, bPos);
		}
		else {
	        return 1 - (1-aPos)*Math.pow((1-x)/(1-aPos),bPos );	     	
		}
		
	
	}
	
	
	
     public double fNeg(double xx) {
		
    	 if (xx < settings.negMin) return 0;
 		if (xx > settings.negMax) return 1;
 		
 		
 		double x = (xx - settings.negMin)/(settings.negMax- settings.negMin);
    	
		if (x <= aNeg) {
			return aNeg * Math.pow(x/aNeg, bNeg);
		}
		else {
	        return 1 - (1-aNeg)*Math.pow((1-x)/(1-aNeg),bNeg );	     	
		}
		
	
	}
	
	
	public void calculateColorHist() {
		posBinsColor = new int [segments+1];
		negBinsColor = new int [segments+1];
		
		/*
		double max = 0;
		double min = 0;
		
		for (int i = 0; i < settings.Experiments.size(); i++) {
			for (int j = 0; j < settings.RowCount; j++) {
				Variable var = settings.Experiments.elementAt(i);
				if ((var.getValue(j))>max) max = (var.getValue(j));
				if (var.getValue(j)<min) min = var.getValue(j);
			}
			
		}
		*/
		
		for (int i = 0; i < seurat.dataManager.getExperiments().size(); i++) {
			for (int j = 0; j < seurat.dataManager.getRowCount(); j++) {
				Variable var = seurat.dataManager.getExperiments().elementAt(i);
				double value = var.getValue(j);
				if (value/var.max >= 0) posBinsColor [(int)Math.round((fPos(value/var.max)*segments))]++;
				else negBinsColor [(int)Math.round(fNeg(value/var.min)*segments)]++;
				
			}
			
		}
		posMaxColor = 0;
		negMaxColor = 0;
		
		for (int i = 0; i < posBinsColor.length; i++) {
			if (posBinsColor [i]>posMaxColor) posMaxColor = posBinsColor [i];
			if (negBinsColor [i]>negMaxColor) negMaxColor = negBinsColor [i];
		}
		
		
		
	}


	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		
		
		
		
		//*Click on negative scale/
		if (e.getX() >=abstandLinks - abstandLinks/8 &&
			e.getX() <=	abstandLinks - abstandLinks/8 +   abstandLinks/10  &&
			e.getY() >=  abstandUnten/8  &&
			e.getY() <=   abstandUnten/8  +  (this.getHeight()-abstandUnten -abstandUnten/8)
		) {
			
			
		
			
			
			seurat.NegColor = JColorChooser.showDialog(
                    this,
                    "Choose color for negative values",
                     seurat.NegColor);
			
			seurat.repaintWindows();
			repaint();
		}
		
		
		
		
		
		if (e.getX() >=WIDTH + abstandLinks - abstandLinks/8 &&
				e.getX() <=	WIDTH+ abstandLinks - abstandLinks/8 +   abstandLinks/10  &&
				e.getY() >=  abstandUnten/8  &&
				e.getY() <=   abstandUnten/8  +  (this.getHeight()-abstandUnten -abstandUnten/8)
			) {
			
				seurat.PosColor = JColorChooser.showDialog(
	                     this,
	                     "Choose color for positive values",
	                      seurat.PosColor);
;
				seurat.repaintWindows();
				repaint();
			}
			
		
	
		
	}


	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		int x1 = abstandLinks + (int) Math.round((1-settings.negMax) * (WIDTH - abstandLinks - abstandLinks/4));
		int y1= this.getHeight()-abstandUnten;
		
		if (Math.abs(e.getX() - x1)<10 && Math.abs(e.getY() - y1)<10) {
			dragNegMax = true;
		}
		
		x1 = abstandLinks + (int) Math.round((1-settings.negMin) * (WIDTH - abstandLinks - abstandLinks/4));
		
		if (Math.abs(e.getX() - x1)<10 && Math.abs(e.getY() - y1)<10) {
			dragNegMin = true;
		}
		
        x1 = abstandLinks +WIDTH+ (int) Math.round(settings.posMin* (WIDTH - abstandLinks - abstandLinks/4));
		
		if (Math.abs(e.getX() - x1)<10 && Math.abs(e.getY() - y1)<10) {
			dragPosMin = true;
		}
		
		 x1 = abstandLinks +WIDTH+ (int) Math.round(settings.posMax* (WIDTH - abstandLinks - abstandLinks/4));
			
			if (Math.abs(e.getX() - x1)<10 && Math.abs(e.getY() - y1)<10) {
				dragPosMax = true;
			}
		
	}


	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		 dragPosMin=false;
		 dragPosMax=false;
		 dragNegMin= false;
		 dragNegMax= false;
			
	
	}


	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		if (dragPosMin) dragPosMinZeiger(arg0);
		if (dragPosMax) dragPosMaxZeiger(arg0);
		if (dragNegMin) dragNegMinZeiger(arg0);
		if (dragNegMax) dragNegMaxZeiger(arg0);
		
		this.calculateColorHist();
		this.repaint();
		seurat.repaintWindows();
		
	}

	public void dragPosMinZeiger(MouseEvent e) {
			double posMin = (double)(e.getX() - abstandLinks - WIDTH)/(WIDTH - abstandLinks - abstandLinks/4);
		if (posMin >=0 && posMin<=1 && settings.posMax > posMin) settings.posMin = posMin; 
		seurat.repaintWindows();
		repaint();
		
	}
	
	
	public void dragPosMaxZeiger(MouseEvent e) {
		double posMax = (double)(e.getX() - abstandLinks - WIDTH)/(WIDTH - abstandLinks - abstandLinks/4);
	if (posMax >=0 && posMax<=1 && posMax > settings.posMin) settings.posMax = posMax; 
	seurat.repaintWindows();
	repaint();
	
}
	
	public void dragNegMinZeiger(MouseEvent e) {
		double negMin = 1-(double)(e.getX() - abstandLinks)/(WIDTH - abstandLinks - abstandLinks/4);
	if (negMin >=0 && negMin<=1&& settings.negMax > negMin) settings.negMin = negMin; 
	seurat.repaintWindows();
	repaint();
	
}
	
	public void dragNegMaxZeiger(MouseEvent e) {
		double negMax = 1-(double)(e.getX() - abstandLinks)/(WIDTH - abstandLinks - abstandLinks/4);
	if (negMax >=0 && negMax<=1&& negMax > settings.negMin) settings.negMax = negMax; 
	seurat.repaintWindows();
	repaint();
	
}
	
	

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}


