package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXPanel;

import com.ubisoa.activecloud.events.FileSystemEvent;
import com.ubisoa.activecloud.events.FileSystemEventListener;
import com.ubisoa.activecloud.services.FileSystemService;


public class CapsuleLoaderTest extends JFrame implements FileSystemEventListener{
	private static final long serialVersionUID = 5452960029326248074L;
	
	private JSplitPane splitPane;
	private JXPanel hcIconViewer;
	private JXPanel ncIconViewer;
	private BottomTabbedPanel configUI;
	private JScrollPane hcIconScroll;
	private JScrollPane ncIconScroll;
	
	private static final Logger log = Logger.getLogger(CapsuleLoaderTest.class);
	
	private static final Dimension hcIconScrollDimension = new Dimension(146,600);
	private static final Dimension ncIconScrollDimension = new Dimension(146,600);
	private static final Dimension mainWindowDimension = new Dimension(800,600);
	
	public CapsuleLoaderTest(){
		initComponents();
	}
	
	private void initComponents(){
		FileSystemService.get().addFileSystemEventListener(this);
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(CapsuleLoaderTest.mainWindowDimension);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		//Create hardwarecapsule viewer
		hcIconViewer = new JXPanel();
		hcIconViewer.setBackgroundPainter(Painters.checkerboard());
		hcIconViewer.setLayout(new BoxLayout(hcIconViewer, BoxLayout.Y_AXIS));
		hcIconScroll = new JScrollPane(hcIconViewer);
		hcIconScroll.setPreferredSize(CapsuleLoaderTest.hcIconScrollDimension);
		hcIconScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		hcIconScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Create notificationcapsule viewer
		ncIconViewer = new JXPanel();
		ncIconViewer.setBackgroundPainter(Painters.checkerboard());
		ncIconViewer.setLayout(new BoxLayout(ncIconViewer, BoxLayout.Y_AXIS));
		ncIconScroll = new JScrollPane(ncIconViewer);
		ncIconScroll.setPreferredSize(CapsuleLoaderTest.ncIconScrollDimension);
		ncIconScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ncIconScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		configUI = new BottomTabbedPanel();
		
		//this.add(splitPane, BorderLayout.CENTER);
		this.add(hcIconScroll, BorderLayout.WEST);
		this.add(ncIconScroll, BorderLayout.EAST);
		this.add(configUI, BorderLayout.CENTER);
		this.setTitle("ActiveCloud");
		this.pack();

		startMonitoring();
	}

	public synchronized void fileSystemEventOcurred(FileSystemEvent ce) {
		log.debug("FileSystemEvent received");
		new CapsuleLoaderWorker(hcIconViewer, ncIconViewer, configUI, 
				ce.getAddedJars()).execute();
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
			JOptionPane.showMessageDialog(this, "Please input a directory path", 
					"Error", JOptionPane.ERROR_MESSAGE);
			log.error("Given path is not directory");
		}
	}
	
	public static void main(String args[]){
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new CapsuleLoaderTest().setVisible(true);
			}
		});
	}

}