package com.ubisoa.activecloud.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.GaussianBlurFilter;

import com.ubisoa.activecloud.gui.dnd.FileDrop;
import com.ubisoa.activecloud.services.FileSystemService;

/**This is the GUI to install a new Capsule. It displays an informative legend and
 * on mouseover it expands into a drag and drop target. The user can drop a capsule
 * inside the target and it gets installed automatically*/
public class CapsuleInstallerPanel extends JXPanel{
	private LineSquarePanel squarePanel;
	private BufferedImage blurBuffer;
	private BufferedImage backBuffer;
	private float alpha = 0.0f;
	private static Logger log = Logger.getLogger(CapsuleInstallerPanel.class);
	
	CapsuleInstallerPanel(LineSquarePanel squarePanel){
		setLayout(new BorderLayout());
		
		this.squarePanel = squarePanel;
		this.squarePanel.setAlpha(0.0f);
		add(squarePanel, BorderLayout.CENTER);
		
		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
				fadeOut();
			}

			public void mouseEntered(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseReleased(MouseEvent arg0) {}
			
		});
		
		new FileDrop(System.out, squarePanel, new FileDrop.Listener(){   
			public void filesDropped( java.io.File[] files ){   
				// handle file drop
				for(File f : files){
					if(FileSystemService.isCapsule(f)){
						log.debug(f + " seems to be a capsule, installing");
						FileSystemService.installCapsule(f);
					}
				}
			}   // end filesDropped
		}); // end FileDrop.Listener
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
	
	@Override
	public float getAlpha(){
		return alpha;
	}
	
	@Override
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
	
	public void fadeOut(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Animator animator = PropertySetter.createAnimator(400, squarePanel, "alpha", 0.0f);
				/*What to do when animation ends*/
				animator.addTarget(new TimingTarget(){
					public void begin(){}
					public void repeat() {}
					public void timingEvent(float arg0) {}
					
					public void end() {
						setVisible(false);
					}
				});
				
				animator.setAcceleration(0.2f);
				animator.setDeceleration(0.3f);
				animator.addTarget(new PropertySetter(CapsuleInstallerPanel.this, "alpha", 0.0f));
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
				
				
				LineSquarePanel box = new LineSquarePanel(false);
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
