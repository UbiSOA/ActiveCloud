package ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.jdesktop.swingworker.SwingWorker;

import ubisoa.activecloud.capsules.IHardwareCapsule;
import ubisoa.activecloud.services.NodeAccessService;

public class CapsuleLoaderWorker extends SwingWorker<List<IHardwareCapsule>, String>{
	private static Logger log = Logger.getLogger(CapsuleLoaderWorker.class);
	private JPanel viewer;
	private JPanel configUI;
	private JProgressBar progressBar;
	private String[] filenames;
	
	public CapsuleLoaderWorker(JPanel viewer, JProgressBar progressBar, 
			JPanel configUI, String... filenames){
		this.viewer = viewer;
		this.configUI = configUI;
		this.progressBar = progressBar;
		this.filenames = filenames;
	}
	
	//In the EDT
	@Override
	protected void done(){
		try{
			for(final IHardwareCapsule capsule : get()){
				JLabel capsuleLabel = new JLabel(new ImageIcon(capsule.getIcon()));
				capsuleLabel.addMouseListener(new MouseListener(){
					@Override
					public void mouseClicked(MouseEvent arg0) {
						configUI.add(capsule.getConfigUI(), BorderLayout.CENTER);
						configUI.revalidate();
						log.debug("loaded configUI");
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {}

					@Override
					public void mouseExited(MouseEvent arg0) {}

					@Override
					public void mousePressed(MouseEvent arg0) {}

					@Override
					public void mouseReleased(MouseEvent arg0) {}
					
				});
				viewer.add(capsuleLabel);
				viewer.revalidate();
				progressBar.setValue(0);
			}
		} catch(Exception e) {
			log.error(e.getMessage());
			JOptionPane.showMessageDialog(null, "A capsule was detected but not loaded.\n" +
					"Possible reasons include incomplete or malformed config.xml.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// In the EDT
	@Override
	protected void process(List<String> messages) {
		for (String message : messages) {
			log.info(message+"\n");
		}
	}

	//This runs in a background thread
	@Override
	protected List<IHardwareCapsule> doInBackground() throws Exception {
		List<IHardwareCapsule> capsules = new ArrayList<IHardwareCapsule>();
		progressBar.setMaximum(filenames.length);
		log.debug("Max Progress Value: "+filenames.length);
		progressBar.setValue(0);
		int n = 0;
		for(String filename : filenames){
			try{
				capsules.add(NodeAccessService.get().loadCapsule(
						new JarFile(new File(filename))));
				
				/*Publish the temporary results*/
				publish("Loaded " + filename);
				n++;
				log.debug("Progress Value: "+n);
				progressBar.setValue(n);
			} catch (Exception ioe) {
				log.error(ioe.getMessage());
				JOptionPane.showMessageDialog(null, "You can only load a capsule once.\n" +
						"Please try with another one.", 
						"Capsule already there...", JOptionPane.ERROR_MESSAGE);
			}
		}
		return capsules;
	}
}