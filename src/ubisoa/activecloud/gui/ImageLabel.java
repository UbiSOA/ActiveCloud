package ubisoa.activecloud.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GlowFilter;

public class ImageLabel extends JLabel implements MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8345530376048921661L;
	private BufferedImage original;
	private BufferedImage filterOver = null;
	private BufferedImage filterClick = null;
	
	public ImageLabel(BufferedImage image){
		super(new ImageIcon(image));
		this.original = image;
		GaussianFilter filter = new GaussianFilter(5);
		filterOver = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		filter.filter(image, filterOver);
		
		GlowFilter glow = new GlowFilter();
		glow.setAmount(.2f);
		filterClick = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		glow.filter(original, filterClick);
		addMouseListener(this);
	}
	
	public BufferedImage getImage(){
		return original;
	}

	public void mouseClicked(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {
		setIcon(new ImageIcon(filterOver));
	}

	public void mouseExited(MouseEvent arg0) {
		setIcon(new ImageIcon(original));
	}

	public void mousePressed(MouseEvent arg0) {
		setIcon(new ImageIcon(filterClick));
	}

	public void mouseReleased(MouseEvent arg0) {
		Point location = getLocationOnScreen();
		
		int clickX = arg0.getXOnScreen();
		int clickY = arg0.getYOnScreen();
		
		//If the click is inside the image
		if((clickX > location.x && clickX < (location.x + original.getWidth())) &&
				(clickY > location.y && clickY < (location.y + original.getHeight()))){
			setIcon(new ImageIcon(filterOver));
		}
	}
}
