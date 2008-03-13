package ubisoa.activecloud.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.jdesktop.swingworker.SwingWorker;

import ubisoa.activecloud.capsules.ICapsule;
import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.InvalidCapsuleException;
import ubisoa.activecloud.services.NodeAccessService;

public class CapsuleLoaderWorker extends SwingWorker<List<ICapsule>, String>{
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
			for(final ICapsule capsule : get()){
				if(!(capsule == null)){
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
				}else{
					log.debug("Capsule not initialized");
				}
			} 
		}catch (InterruptedException ie){
			log.error(ie.getMessage());
		}catch (ExecutionException ex){
			log.error(ex.getMessage());
		}
		/*
		catch(Exception e) {
			log.error(e.getMessage());
			JOptionPane.showMessageDialog(null, "A capsule was detected but not loaded.\n" +
					"Possible reasons include incomplete or malformed config.xml.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		*/
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
	protected List<ICapsule> doInBackground() throws Exception {
		progressBar.setMaximum(filenames.length);
		log.debug("Max Progress Value: "+filenames.length);
		progressBar.setValue(0);
		ArrayList<ICapsule> capsules = new ArrayList<ICapsule>();
		int n = 0;
		for(String filename : filenames){
			try{
				ICapsule cap = 
					NodeAccessService.get().loadCapsule(new JarFile(new File(filename)));
				if(cap != null){
					/*Publish the temporary results*/
					publish("Loaded " + filename);
					n++;
					log.debug("Progress Value: "+n);
					progressBar.setValue(n);
					capsules.add(cap);					
				}
			} catch (CapsuleInitException cie){
				log.error(cie.getMessage());
				JOptionPane.showMessageDialog(null, "There was an error initializing "+cie.getFailedCapsuleName()+
						"\nThe reported error was: '"+cie.getMessage()+"'");
			} catch (InvalidCapsuleException cnfe){
				log.error(cnfe.getMessage());
				JOptionPane.showMessageDialog(null, "The class " + cnfe.getMessage() + " can't be located.\n" +
						"Please verify that the name is correct and try again.");
			} catch (Exception e){
				log.error(e.getMessage());
			}
		}
		return capsules;
	}
}