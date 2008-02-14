package ubisoa.activecloud.hal.capsules;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdom.Element;


public class DummyCapsule implements ICapsule{
    private org.apache.log4j.Logger log = Logger
    .getLogger(DummyCapsule.class);
    
    public JPanel getConfigUI(){
    	JPanel panel = new JPanel();
    	panel.add(new JLabel("Config UI"));
    	return panel;
    }
    
	public void init(Element e) throws Exception {
		log.info("init called");
	}

	public void send(byte[] data) throws Exception {
		log.info("send called");
	}

	public void start() throws Exception {
		log.info("start called");
	}

	public void stop() throws Exception {
		log.info("stop called");
	}

	public void subscribe(Object o) throws Exception {
		log.info("subscribe called");
	}

}
