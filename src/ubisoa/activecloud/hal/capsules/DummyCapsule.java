package ubisoa.activecloud.hal.capsules;

import org.jdom.Element;
import org.apache.log4j.Logger;


public class DummyCapsule implements ICapsule{
    private org.apache.log4j.Logger log = Logger
    .getLogger(DummyCapsule.class);
    
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
