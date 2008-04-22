package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;
import org.jdesktop.swingx.JXPanel;

public class TransitionManager{
	private static MainGUI gui;
	
	public TransitionManager(MainGUI gui){
		TransitionManager.gui = gui;
	}
	
	public static void setCapsuleInstallPaneVisible(){
		gui.setCapsuleInstallPaneVisible();
	}
	
	public static void setInitialView(){
		Animator animator = new Animator(1500);
		animator.setAcceleration(.3f);
		animator.setDeceleration(.2f);
		new ScreenTransition(gui.getMainPanel(), new TransitionTarget(){
			public void setupNextScreen(){
				JPanel mp = gui.getMainPanel();
				mp.removeAll();
				NavigationHeader nh = gui.getNav();
				nh.removeLastLink();
				mp.add(nh,BorderLayout.NORTH);
				mp.add(gui.getNcIconScroll(), BorderLayout.EAST);
				mp.add(gui.getHcIconScroll(), BorderLayout.WEST);
				mp.add(gui.getConsole(), BorderLayout.CENTER);
			}
		},animator).start();
	}
	
	public static void transitionConfigureCapsule(final JPanel configUI){
		Animator animator = new Animator(1500);
		animator.setAcceleration(.3f);
		animator.setDeceleration(.2f);
		new ScreenTransition(gui.getMainPanel(),new TransitionTarget(){
			public void setupNextScreen(){
				JPanel mp = gui.getMainPanel();
				mp.removeAll();
				NavigationHeader nh = gui.getNav();
				nh.addLink("Configuring capsule...");
				mp.add(nh,BorderLayout.NORTH);
				mp.add(gui.getNcIconScroll(), BorderLayout.EAST);
				mp.add(gui.getHcIconScroll(), BorderLayout.WEST);
				mp.add(configUI,BorderLayout.CENTER);
			}
		},animator).start();
	}
	
	public static void transitionDeliveryLink(final JXPanel dlg){
		Animator animator = new Animator(1500);
		animator.setAcceleration(.3f);
		animator.setDeceleration(.2f);
		final JScrollPane scroll = new JScrollPane(dlg);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		new ScreenTransition(gui.getMainPanel(),new TransitionTarget(){
			public void setupNextScreen(){
				JPanel mp = gui.getMainPanel();
				mp.removeAll();
				NavigationHeader nh = gui.getNav();
				nh.addLink("Adding Observers");
				mp.add(nh,BorderLayout.NORTH);
				mp.add(gui.getNcIconScroll(), BorderLayout.EAST);
				mp.add(gui.getHcIconScroll(), BorderLayout.WEST);
				mp.add(scroll,BorderLayout.CENTER);
			}
		},animator).start();
	}
}
