package ubisoa.activecloud.hal.capsuleloader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CapsuleLoaderTest extends JFrame{
	private JButton load;
	private JTextField path;
	private JSplitPane splitPane;
	private JPanel buttonPanel;
	private JPanel imageViewer;
	private JTabbedPane configTabs;
	private JScrollPane imageScroll;
	private JProgressBar progressBar;
	
	public CapsuleLoaderTest(){
		super();
		initComponents();
	}
	
	private void initComponents(){
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(800,600));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		load = new JButton("Load capsule");
		path = new JTextField();
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		path.setPreferredSize(new Dimension(300,21));
		buttonPanel.add(load);
		buttonPanel.add(path);
		buttonPanel.add(progressBar);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		imageViewer = new JPanel();
		imageViewer.setLayout(new BoxLayout(imageViewer, BoxLayout.Y_AXIS));
		imageScroll = new JScrollPane(imageViewer);
		imageScroll.setPreferredSize(new Dimension(146,600));
		imageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		imageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		configTabs = new JTabbedPane();
		splitPane.add(imageScroll);
		splitPane.add(configTabs);
		
		this.add(splitPane, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setTitle("CapsuleLoaderTest");
		this.pack();
		
		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				new CapsuleLoaderWorker(imageViewer, progressBar, path.getText()).execute();
			}
		});
	}
	
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new CapsuleLoaderTest().setVisible(true);
			}
		});
	}

}