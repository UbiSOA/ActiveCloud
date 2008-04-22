package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXPanel;

import bsh.util.JConsole;

import com.ubisoa.activecloud.events.FileSystemEvent;
import com.ubisoa.activecloud.events.FileSystemEventListener;
import com.ubisoa.activecloud.services.FileSystemService;
import com.ubisoa.activecloud.services.QueryProcessorService;

public class MainGUI extends JFrame implements FileSystemEventListener{
	//Capsule Panels
	private JScrollPane hcIconScroll;
	private JScrollPane ncIconScroll;
	private JXPanel hcIconViewer;
	private JXPanel ncIconViewer;
	private CapsuleInstallerPanel cip;
	private JConsole console;
	private NavigationHeader nav;
	private JXPanel mainPanel;
	
	private static final Dimension hcIconScrollDimension = new Dimension(146,430);
	private static final Dimension ncIconScrollDimension = new Dimension(146,430);
	
	private static final Logger log = Logger.getLogger(MainGUI.class);
	
	public MainGUI(){
		initComponents();
		QueryProcessorService.get().startInterpreter(console);
		QueryProcessorService.get().bootstrapInterpreter();
		startMonitoring();
	}
	
	private void initComponents(){
		FileSystemService.get().addFileSystemEventListener(this);
		mainPanel = new JXPanel();
		mainPanel.setLayout(new BorderLayout());
		add(mainPanel);
		
		//Create hardwarecapsule viewer
		hcIconViewer = new JXPanel();
		hcIconViewer.setBackgroundPainter(Painters.matteDark());
		hcIconViewer.setLayout(new BoxLayout(hcIconViewer, BoxLayout.Y_AXIS));
		hcIconViewer.add(Box.createVerticalStrut(10));
		hcIconScroll = new JScrollPane(hcIconViewer);
		hcIconScroll.setPreferredSize(MainGUI.hcIconScrollDimension);
		hcIconScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		hcIconScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		//Create notificationcapsule viewer
		ncIconViewer = new JXPanel();
		ncIconViewer.setBackgroundPainter(Painters.matteDark());
		ncIconViewer.setLayout(new BoxLayout(ncIconViewer, BoxLayout.Y_AXIS));
		ncIconViewer.add(Box.createVerticalStrut(10));
		ncIconScroll = new JScrollPane(ncIconViewer);
		ncIconScroll.setPreferredSize(MainGUI.ncIconScrollDimension);
		ncIconScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ncIconScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		//Create the console editor
		console = new JConsole();
		console.setPreferredSize(new Dimension(650,100));
		
		//Create the navigation bar
		nav = new NavigationHeader();
		nav.addLink("Install");
		nav.addLink("Observers");
		nav.addLink("Console");
		
		mainPanel.add(hcIconScroll, BorderLayout.WEST);
		mainPanel.add(ncIconScroll, BorderLayout.EAST);
		mainPanel.add(console, BorderLayout.CENTER);
		mainPanel.add(nav, BorderLayout.NORTH);
		
		cip = new CapsuleInstallerPanel(new LineSquarePanel(false, "Drop Capsule Here"));
		setGlassPane(cip);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ActiveCloud");
		new TransitionManager(this);
	}
	
	public void setCapsuleInstallPaneVisible(){
		cip.fadeIn();
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				MainGUI gui = new MainGUI();
				gui.setResizable(false);
				gui.setVisible(true);
			}
		});
	}

	@Override
	public void fileSystemEventOcurred(FileSystemEvent ce) {
		log.debug("FileSystemEvent received");
		new CapsuleWorker(this, ce.getAddedJars()).execute();
	}
	
	private void startMonitoring(){
		File f = new File("capsules"+File.separator+"hal");
		File f2 = new File("capsules"+File.separator+"ns");
		log.info("Monitoring: "+f.getAbsolutePath());
		log.info("Monitoring: "+f2.getAbsolutePath());
		if(f.isDirectory()){
			try{
				
				FileSystemService.get().start(new String[]{f.getAbsolutePath(), 
						f2.getAbsolutePath()});
			} catch (Exception e) {
				log.error(e.getMessage());
				JOptionPane.showMessageDialog(this, e.getMessage(), 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			log.error("Given path is not directory");
		}
	}

	public JXPanel getHcIconViewer() {
		return hcIconViewer;
	}

	public JXPanel getNcIconViewer() {
		return ncIconViewer;
	}

	public JXPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JXPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public NavigationHeader getNav() {
		return nav;
	}

	public JScrollPane getHcIconScroll() {
		return hcIconScroll;
	}

	public JScrollPane getNcIconScroll() {
		return ncIconScroll;
	}

	public JConsole getConsole() {
		return console;
	}
}