package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.PainterGlasspane;

import bsh.util.JConsole;

import com.ubisoa.activecloud.services.QueryProcessorService;

public class MainGUI extends JFrame{
	//Capsule Panels
	private JScrollPane hcIconScroll;
	private JScrollPane ncIconScroll;
	private JXPanel hcIconViewer;
	private JXPanel ncIconViewer;
	private JToolBar toolBar;
	private CapsuleInstallerPanel cip;
	private JConsole console;
	
	private static final Dimension hcIconScrollDimension = new Dimension(146,600);
	private static final Dimension ncIconScrollDimension = new Dimension(146,600);
	private static final Dimension mainWindowDimension = new Dimension(800,600);
	
	private static final Logger log = Logger.getLogger(MainGUI.class);
	
	public MainGUI(){
		initComponents();
		QueryProcessorService.get().startInterpreter(console);
		QueryProcessorService.get().bootstrapInterpreter();
	}
	
	private void initComponents(){
		setLayout(new BorderLayout());
		
		//Create hardwarecapsule viewer
		hcIconViewer = new JXPanel();
		hcIconViewer.setBackgroundPainter(Painters.matteDark());
		hcIconViewer.setLayout(new BoxLayout(hcIconViewer, BoxLayout.Y_AXIS));
		hcIconScroll = new JScrollPane(hcIconViewer);
		hcIconScroll.setPreferredSize(MainGUI.hcIconScrollDimension);
		hcIconScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		hcIconScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		//Create notificationcapsule viewer
		ncIconViewer = new JXPanel();
		ncIconViewer.setBackgroundPainter(Painters.matteDark());
		ncIconViewer.setLayout(new BoxLayout(ncIconViewer, BoxLayout.Y_AXIS));
		ncIconScroll = new JScrollPane(ncIconViewer);
		ncIconScroll.setPreferredSize(MainGUI.ncIconScrollDimension);
		ncIconScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ncIconScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		//Create the console editor
		console = new JConsole();
		console.setPreferredSize(new Dimension(800,600));
		PainterGlasspane pg = new PainterGlasspane();
		pg.setPainter(Painters.matteDark());
		pg.addTarget(console);
		
		add(hcIconScroll, BorderLayout.EAST);
		add(ncIconScroll, BorderLayout.WEST);
		add(console, BorderLayout.CENTER);
		
		cip = new CapsuleInstallerPanel(new LineSquarePanel(false));
		setGlassPane(cip);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void addToolBarButtons(){
		JButton installCapsule = new JButton("i");
		installCapsule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				installCapsuleButtonClicked(evt);
			}
		});
		toolBar.add(installCapsule);
	}
	
	private void installCapsuleButtonClicked(ActionEvent evt){
		cip.fadeIn();
	}
	
	public static void main(String args[]){
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				MainGUI gui = new MainGUI();
				gui.setVisible(true);
			}
		});
	}
}
