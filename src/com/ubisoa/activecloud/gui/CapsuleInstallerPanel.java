package com.ubisoa.activecloud.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.GaussianBlurFilter;

/**This is the GUI to install a new Capsule. It displays an informative legend and
 * on mouseover it expands into a drag and drop target. The user can drop a capsule
 * inside the target and it gets installed automatically*/
public class CapsuleInstallerPanel extends JXPanel{
	private LineSquarePanel squarePanel;
	private BufferedImage blurBuffer;
	private BufferedImage backBuffer;
	private float alpha = 0.0f;
	
	CapsuleInstallerPanel(LineSquarePanel squarePanel){
		setLayout(new BorderLayout());
		
		this.squarePanel = squarePanel;
		this.squarePanel.setAlpha(0.0f);
		add(squarePanel, BorderLayout.CENTER);
		
		addMouseListener(new MouseAdapter(){});
	}
	
	private void createBlur(){
		JRootPane root = SwingUtilities.getRootPane(this);
		blurBuffer = GraphicsUtilities.createCompatibleImage(getWidth(),getHeight());
		Graphics2D g2d = blurBuffer.createGraphics();
		root.paint(g2d);
		g2d.dispose();
		
		backBuffer = blurBuffer;
		
		blurBuffer = GraphicsUtilities.createThumbnailFast(blurBuffer, getWidth()/2);
		blurBuffer = new GaussianBlurFilter(5).filter(blurBuffer, null);
	}
	
	@Override
	protected void paintComponent(Graphics g){
		if(isVisible() && blurBuffer != null){
			Graphics2D g2d = (Graphics2D)g.create();
			
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.drawImage(backBuffer, 0, 0, null);
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(blurBuffer, 0, 0, getWidth(), getHeight(), null);
			g2d.dispose();
		}
	}
	
	public float getAlpha(){
		return alpha;
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
		repaint();
	}
	
	public void fadeIn(){
		createBlur();
		
		setVisible(true);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Animator animator = PropertySetter.createAnimator(400,squarePanel,"alpha",1.0f);
				animator.setAcceleration(0.2f);
				animator.setDeceleration(0.3f);
				animator.addTarget(new PropertySetter(CapsuleInstallerPanel.this,"alpha",1.0f));
				animator.start();
			}
		});
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setPreferredSize(new Dimension(800,600));
				f.setLayout(new BorderLayout());
				//Image
				ImageLabel capsuleLabel = null;
				try{
					
					capsuleLabel = new ImageLabel(ImageIO.read(new File("logo.jpg")));
					f.add(capsuleLabel,BorderLayout.CENTER);
				}catch(IOException ioe){
					System.out.println("Cant load image");
				}
				
				JButton b = new JButton("Fade");
				f.add(b,BorderLayout.SOUTH);
				
				
				LineSquarePanel box = new LineSquarePanel(true);
				final CapsuleInstallerPanel cip = new CapsuleInstallerPanel(box);
				f.setGlassPane(cip);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				b.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						cip.fadeIn();
					}
				});
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
