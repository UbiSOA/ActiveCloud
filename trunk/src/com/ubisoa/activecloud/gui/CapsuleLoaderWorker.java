package com.ubisoa.activecloud.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.swingworker.SwingWorker;

import com.ubisoa.activecloud.capsules.HardwareCapsule;
import com.ubisoa.activecloud.capsules.ICapsule;
import com.ubisoa.activecloud.capsules.NotificationCapsule;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.InvalidCapsuleException;
import com.ubisoa.activecloud.services.NodeAccessService;


public class CapsuleLoaderWorker extends SwingWorker<List<ICapsule>, String>{
	private static Logger log = Logger.getLogger(CapsuleLoaderWorker.class);

	private String[] filenames;
	private CapsuleLoaderTest mainGUI;
	
	private static final float iconAlignmentX = 0.5f;
	private static final float iconAlignmentY = 0.5f;
	
	public CapsuleLoaderWorker(CapsuleLoaderTest mainGUI, String... filenames){

		this.mainGUI = mainGUI;
		this.filenames = filenames;
	}
	
	//In the EDT
	@Override
	protected void done(){
		try{
			for(final ICapsule capsule : get()){
				if(!(capsule == null)){
					final ImageLabel capsuleLabel = new ImageLabel(capsule.getIcon());
					capsuleLabel.setAlignmentX(CapsuleLoaderWorker.iconAlignmentX);
					capsuleLabel.setAlignmentY(CapsuleLoaderWorker.iconAlignmentY);
					capsuleLabel.addMouseListener(new MouseListener(){
						
						@Override
						public void mouseClicked(MouseEvent arg0) {
							SwingUtilities.invokeLater(new Runnable(){
								public void run(){
									new CapsuleConfigPanel(capsule.getConfigUI()).setVisible(true);
								}
							});
							
							log.debug("loaded configUI");
						}

						@Override
						public void mouseEntered(MouseEvent arg0) {

						}

						@Override
						public void mouseExited(MouseEvent arg0) {

						}

						@Override
						public void mousePressed(MouseEvent arg0) {}

						@Override
						public void mouseReleased(MouseEvent arg0) {}
						
					});
					
					if(capsule instanceof HardwareCapsule){
						mainGUI.getHcIconViewer().add(capsuleLabel);
						mainGUI.getHcIconViewer().revalidate();
					}else if(capsule instanceof NotificationCapsule){
						mainGUI.getNcIconViewer().add(capsuleLabel);
						mainGUI.getNcIconViewer().revalidate();
					}else{
						log.debug("Instance of what?");
					}
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
		log.debug("Max Progress Value: "+filenames.length);
		ArrayList<ICapsule> capsules = new ArrayList<ICapsule>();
		for(String filename : filenames){
			try{
				ICapsule cap = 
					NodeAccessService.get().loadCapsule(new JarFile(new File(filename)));
				if(cap != null){
					/*Publish the temporary results*/
					publish("Loaded " + filename);
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