package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXPanel;

public class DeliveryLinkPanel extends JXPanel{
	private LineSquarePanel squarePanel;
	private static Logger log = Logger.getLogger(DeliveryLinkPanel.class);
	
	public DeliveryLinkPanel(){
		super();
		setOpaque(false);
		add(buildReflectionPanel());
		
		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
				log.debug("DeliveryLinkPanel clicked");
			}

			public void mouseEntered(MouseEvent arg0) {
				fadeIn();
				log.debug("Mouse Entered DeliveryLinkPanel");
			}

			public void mouseExited(MouseEvent arg0) {
				fadeOut();
				log.debug("Mouse Exited DeliveryLinkPanel");
			}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseReleased(MouseEvent arg0) {}
		});
		
		//setBackgroundPainter(Painters.matteDark());
	}
	
	private JComponent buildReflectionPanel(){
		squarePanel = new LineSquarePanel(false, "Click to set observer");
		squarePanel.setPreferredSize(new Dimension(400,200));
		squarePanel.setAlpha(0.5f);
		
		return squarePanel;
	}
	
	@Override
	public float getAlpha(){
		return squarePanel.getAlpha();
	}
	
	@Override
	public void setAlpha(float alpha){
		squarePanel.setAlpha(alpha);
		squarePanel.repaint();
	}
	
	public void fadeIn(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Animator animator = PropertySetter.createAnimator(400,squarePanel,"alpha",1.0f);
				animator.setAcceleration(0.2f);
				animator.setDeceleration(0.3f);
				animator.addTarget(new PropertySetter(DeliveryLinkPanel.this,"alpha",1.0f));
				animator.start();
			}
		});
	}
	
	public void fadeOut(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Animator animator = PropertySetter.createAnimator(400, squarePanel, "alpha", 0.5f);
				animator.setAcceleration(0.2f);
				animator.setDeceleration(0.3f);
				animator.addTarget(new PropertySetter(DeliveryLinkPanel.this, "alpha", 0.5f));
				animator.start();
			}
		});
	}
	
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.add(new DeliveryLinkPanel());
				//f.setPreferredSize(new Dimension(400,300));
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
