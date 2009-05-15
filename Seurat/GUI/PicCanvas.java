package GUI;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.*;

import javax.swing.JComponent;

public class PicCanvas extends JComponent {
	Image pic;

	JFrame descriptionFrame;

	public PicCanvas(Image pic, JFrame descriptionFrame) {
		this.pic = pic;
		this.descriptionFrame = descriptionFrame;
	}

	@Override
	public void paint(Graphics g) {

		g.drawImage(pic, 0, 0, this.getWidth(), this.getHeight(),
				descriptionFrame);

	}
}