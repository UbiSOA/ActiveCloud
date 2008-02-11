package ubisoa.activecloud.hal.capsuleloader;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

public class CapsuleLoaderWorker extends SwingWorker<List<Image>, String>{
	private static Logger log = Logger.getLogger(CapsuleLoaderWorker.class);
	private JPanel viewer;
	private JProgressBar progressBar;
	private String[] filenames;
	
	public CapsuleLoaderWorker(JPanel viewer, JProgressBar progressBar, 
			String... filenames){
		this.viewer = viewer;
		this.progressBar = progressBar;
		this.filenames = filenames;
	}
	
	//In the EDT
	@Override
	protected void done(){
		try{
			for(Image image : get()){
				viewer.add(new JLabel(new ImageIcon(image)));
				viewer.revalidate();
				progressBar.setValue(0);
			}
		} catch(Exception e) {
			log.error(e.getMessage());
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
	protected List<Image> doInBackground() throws Exception {
		List<Image> images = new ArrayList<Image>();
		progressBar.setMaximum(filenames.length);
		log.debug("Max Progress Value: "+filenames.length);
		progressBar.setValue(0);
		int n = 0;
		for(String filename : filenames){
			try{
				/*The given filenames are those of Capsules, we need to extract
				 * the image file and return it as a list. By convention the
				 * image file must be named icon.png*/
				JarFile jar = new JarFile(new File(filename));
				images.add(ImageIO.read(jar.getInputStream(new ZipEntry("icon.png"))));
				publish("Loaded " + filename);
				n++;
				log.debug("Progress Value: "+n);
				progressBar.setValue(n);
			} catch (IOException ioe) {
				log.error(ioe.getMessage());
			}
		}
		return images;
	}
}