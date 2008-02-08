package ubisoa.activecloud.hal.filesystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class FileSystemTest extends JFrame{
	private static final long serialVersionUID = 8996088674680594677L;
	public static Logger log = Logger.getLogger(FileSystemTest.class);
	private JScrollPane scrollPane;
	private JPanel buttonPanel;
	private JTextArea textArea;
	private FileSystemWatcher fsw;
	private JButton start;
	private JButton stop;
	
	public FileSystemTest(){
		super();
		initComponents();
	}
	
	private void initComponents(){
		try{
			fsw = new FileSystemWatcher("/Users/cesarolea");
		} catch (Exception e) {
			log.error(e.getMessage());
			fsw.stopWatching();
		}
		
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
		
		fsw.addCapsuleEventListener(new CapsuleEventListener(){
			public void CapsuleEventOcurred(CapsuleEvent evt){
				fswCapsuleEventOcurred(evt);
			}
		});
	}
	
	private void startButtonActionPerformed(ActionEvent evt){
		if(fsw != null)
			fsw.startWatching(1000);
		else
			log.error("FileSystemWatcher is null");
	}
	
	private void stopButtonActionPerformed(ActionEvent evt){
		if(fsw != null)
			fsw.stopWatching();
		else
			log.error("FileSystemWatcher is null");
	}
	
	private void fswCapsuleEventOcurred(CapsuleEvent evt){
		if(!evt.getAddedJars().isEmpty()){
			final Vector<String> jarsAdded = evt.getAddedJars();
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
		
		if(!evt.getDeletedJars().isEmpty()){
			final Vector<String> jarsDeleted = evt.getDeletedJars();
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
