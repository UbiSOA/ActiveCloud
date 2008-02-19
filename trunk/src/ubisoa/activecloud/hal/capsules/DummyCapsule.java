package ubisoa.activecloud.hal.capsules;

import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdom.Element;


public class DummyCapsule implements ICapsule{
    private org.apache.log4j.Logger log = Logger
    .getLogger(DummyCapsule.class);
    
    @Override
    public JPanel getConfigUI(){
    	JPanel panel = new JPanel();
    	panel.add(new JLabel("Config UI"));
    	return panel;
    }
    
    @Override
	public void init(Element e) throws Exception {
		log.info("init called");
	}

    @Override
	public void send(byte[] data) throws Exception {
		log.info("send called");
	}

    @Override
	public void start() throws Exception {
		log.info("start called");
	}

    @Override
	public void stop() throws Exception {
		log.info("stop called");
	}

    @Override
	public void subscribe(Object o) throws Exception {
		log.info("subscribe called");
	}

	@Override
	public Element getConfigElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfigElement(Element configElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfigUI(JPanel configUI) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIcon(Image icon) {
		// TODO Auto-generated method stub
		
	}

}