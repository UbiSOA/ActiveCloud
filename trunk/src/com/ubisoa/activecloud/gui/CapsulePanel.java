package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.SplineInterpolator;
import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;
import org.jdesktop.swingx.JXPanel;


public class CapsulePanel extends JXPanel implements TransitionTarget{	
	private ArrayList<ImageLabel> images;
	private ArrayList<JXPanel> imagePanels;
	private int curImageStart, curImageEnd = 0;
	private static Logger log = Logger.getLogger(CapsulePanel.class);
	private static final float iconAlignmentX = 0.5f;
	private static final float iconAlignmentY = 0.5f;
	private int nextScreen = -1;
	private ScreenTransition st;
	private Animator anim;
	
	public CapsulePanel(){
		anim = new Animator(200);
		anim.setInterpolator(new SplineInterpolator(0.97f, 0.03f, 1.0f, 0.00f));
		images = new ArrayList<ImageLabel>();
		imagePanels = new ArrayList<JXPanel>();
		JXPanel currentPanel = new JXPanel();
		currentPanel.setLayout(new BoxLayout(currentPanel, BoxLayout.Y_AXIS));
		imagePanels.add(currentPanel);
		add(getLastPanel());
		st = new ScreenTransition(this,this,anim);
	}
	
	private JXPanel getLastPanel(){
		if(!imagePanels.isEmpty()){
			return imagePanels.get(imagePanels.size() - 1);
		}
		return imagePanels.get(0);
	}
	
	public void addImageLabel(ImageLabel imageLabel){
		imageLabel.setAlignmentX(CapsulePanel.iconAlignmentX);
		imageLabel.setAlignmentY(CapsulePanel.iconAlignmentY);
		//How much space do the images take
		int spaceTaken = 0;
		int currentPanelHeight = getHeight();
		for(int i = curImageStart; i < curImageEnd; i++){
			spaceTaken += images.get(i).getImage().getHeight();
		}
		log.debug("Current panel has "+(curImageEnd - curImageStart));
		log.debug("Space already taken "+ spaceTaken);
		log.debug("Space available in panel "+getHeight());
		log.debug("Height of new Image "+imageLabel.getImage().getHeight());
		log.debug("Total space taken "+(spaceTaken + imageLabel.getImage().getHeight()));
		//See if this image fits inside the current panel, or a new one is needed
		if(spaceTaken + imageLabel.getImage().getHeight() >= currentPanelHeight){
			//Current Panel is full, copy to secondaryPanel
			log.debug("Panel full, creating new one");
			curImageStart = curImageEnd = 0;
			images.clear();
			JXPanel newPanel = new JXPanel();
			newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
			curImageEnd++;
			imagePanels.add(newPanel);
			//Set the new panel as the viewport (animated)
			st.start();
			getLastPanel().add(imageLabel);
		}else{
			log.debug("Adding to current panel");
			curImageEnd++;
			//image fits well inside currentPanel
			getLastPanel().add(imageLabel);
			getLastPanel().revalidate();
		}
		images.add(imageLabel);
		log.debug("Images so far: "+images.size());
		log.debug("Panels so far "+imagePanels.size());
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame f = new JFrame();
				f.setLayout(new BorderLayout());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				final CapsulePanel cp = new CapsulePanel();
				f.add(cp, BorderLayout.CENTER);
				f.setPreferredSize(new Dimension(130,600));
				JButton b = new JButton("Add Image");
				b.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						try{
							ImageLabel im = new ImageLabel(ImageIO.read(new File("icon.png")));
							cp.addImageLabel(im);
						}catch(IOException ioe){
							log.error(ioe.getMessage());
						}
					}
				});
				f.add(b,BorderLayout.SOUTH);
				f.pack();
				f.setVisible(true);
			}
		});
	}

	@Override
	public void setupNextScreen() {
		removeAll();
		if(nextScreen == -1){
			add(getLastPanel());	
		}
	}
}
