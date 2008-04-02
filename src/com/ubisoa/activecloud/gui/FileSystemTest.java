package com.ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.ubisoa.activecloud.events.FileSystemEvent;
import com.ubisoa.activecloud.events.FileSystemEventListener;
import com.ubisoa.activecloud.services.FileSystemService;


public class FileSystemTest extends JFrame{
	private static final long serialVersionUID = 8996088674680594677L;
	public static Logger log = Logger.getLogger(FileSystemTest.class);
	private JScrollPane scrollPane;
	private JPanel buttonPanel;
	private JTextArea textArea;
	private JButton start;
	private JButton stop;
	
	public FileSystemTest(){
		super();
		initComponents();
	}
	
	private void initComponents(){
		/*Prepare the button panel*/
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		start = new JButton("Start");
		stop = new JButton("Stop");
		buttonPanel.add(start);
		buttonPanel.add(stop);
		
		scrollPane = new JScrollPane();
		textArea = new JTextArea();
		
		textArea.setPreferredSize(new Dimension(800,600));
		textArea.setAutoscrolls(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(textArea);
		
		this.setLayout(new BorderLayout());
		this.add(scrollPane,BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		
		/*Actions*/
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				startButtonActionPerformed(evt);
			}
		});
		
		stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				stopButtonActionPerformed(evt);
			}
		});
		
		FileSystemService.get().addFileSystemEventListener(new FileSystemEventListener(){
			public void fileSystemEventOcurred(FileSystemEvent evt){
				fswCapsuleEventOcurred(evt);
			}
		});
	}
	
	private void startButtonActionPerformed(ActionEvent evt){
		if(!FileSystemService.get().isRunning())
			FileSystemService.get().start(1000, "/Users/cesarolea");
		else
			log.error("FileSystemService is null");
	}
	
	private void stopButtonActionPerformed(ActionEvent evt){
		if(FileSystemService.get().isRunning())
			FileSystemService.get().stop();
		else
			log.error("FileSystemService is null");
	}
	
	private void fswCapsuleEventOcurred(FileSystemEvent evt){
		if(evt.getAddedJars().length != 0){
			final String[] jarsAdded = evt.getAddedJars();
			log.info("New JAR added");
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					for(final String path : jarsAdded){
						textArea.append(">> ");
						textArea.append(path+"\n\r");
					}
				}
			});
		} else {
			log.info("No new JARs added");
		}
		
		if(evt.getDeletedJars().length != 0){
			final String[] jarsDeleted = evt.getDeletedJars();
			log.info("JAR deleted");
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					for(final String path : jarsDeleted){
						textArea.append("<< ");
						textArea.append(path+"\n\r");
					}
				}
			});
		} else {
			log.info("No JARs deleted");
		}
	}
	
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				FileSystemTest test = new FileSystemTest();
				test.setVisible(true);
			}
		});
	}
}
