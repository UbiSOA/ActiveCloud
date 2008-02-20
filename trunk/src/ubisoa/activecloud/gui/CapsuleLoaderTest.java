package ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ubisoa.activecloud.hal.filesystem.CapsuleEvent;
import ubisoa.activecloud.hal.filesystem.CapsuleEventListener;
import ubisoa.activecloud.services.FileSystemService;

public class CapsuleLoaderTest extends JFrame implements CapsuleEventListener{
	private static final long serialVersionUID = 5452960029326248074L;
	
	private JButton load;
	private JButton stop;
	private JTextField path;
	private JSplitPane splitPane;
	private JPanel buttonPanel;
	private JPanel imageViewer;
	private JPanel configUI;
	private JScrollPane imageScroll;
	private JProgressBar progressBar;
	
	private static final Logger log = Logger.getLogger(CapsuleLoaderTest.class);;
	
	public CapsuleLoaderTest(){
		super();
		initComponents();
	}
	
	private void initComponents(){
		FileSystemService.get().addCapsuleEventListener(this);
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(800,600));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		load = new JButton("Start monitoring");
		stop = new JButton("Stop monitoring");
		path = new JTextField();
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		path.setPreferredSize(new Dimension(300,21));
		buttonPanel.add(load);
		buttonPanel.add(stop);
		buttonPanel.add(path);
		buttonPanel.add(progressBar);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		imageViewer = new JPanel();
		imageViewer.setLayout(new BoxLayout(imageViewer, BoxLayout.Y_AXIS));
		imageScroll = new JScrollPane(imageViewer);
		imageScroll.setPreferredSize(new Dimension(146,600));
		imageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		imageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		configUI = new JPanel();
		configUI.setLayout(new BorderLayout());
		configUI.add(new JLabel("Hello from CapsuleLoaderTest"), BorderLayout.CENTER);
		splitPane.add(imageScroll);
		splitPane.add(configUI);
		
		this.add(splitPane, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setTitle("CapsuleLoaderTest");
		this.pack();
		
//		load.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent evt){
//				loadActionPerformed(evt);
//			}
//		});
		
//		stop.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent evt){
//				load.setText("Start monitoring");
//				if(fsw != null)
//					fsw.stopWatching();
//			}
//		});
		startMonitoring();
	}

	public void CapsuleEventOcurred(CapsuleEvent ce) {
		new CapsuleLoaderWorker(imageViewer, progressBar, configUI, ce.getAddedJars())
			.execute();
	}
	
	private void startMonitoring(){
		File f = new File("capsules"+File.separator+"hal");
		log.info("Monitoring: "+f.getAbsolutePath());
		if(f.isDirectory()){
			try{
				FileSystemService.get().start(1000, f.getAbsolutePath());
				load.setEnabled(false);
				load.setText("Monitoring...");
			} catch (Exception e) {
				log.error(e.getMessage());
				JOptionPane.showMessageDialog(this, e.getMessage(), 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please input a directory path", 
					"Error", JOptionPane.ERROR_MESSAGE);
			log.error("Given path is not directory: "+path.getText());
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