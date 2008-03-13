package ubisoa.activecloud.events;

import java.util.EventObject;
import java.util.jar.JarFile;

import org.jdom.Element;

import ubisoa.activecloud.capsules.IHardwareCapsule;

public class HardwareCapsuleInitEvent extends EventObject {
	private static final long serialVersionUID = -428099616975439785L;
	private int id;
	private Element config;
	private IHardwareCapsule hSource;
	private JarFile jarFile;
	
	public HardwareCapsuleInitEvent(IHardwareCapsule source, int id, Element config,
			JarFile jarFile){
		super(source);
		this.hSource = source;
		this.id = id;
		this.config = config;
		this.jarFile = jarFile;
	}
	
	@Override
	public IHardwareCapsule getSource(){
		return hSource;
	}
	
	public int getId(){
		return id;
	}
	
	public Element getConfig(){
		return config;
	}
	
	public String getClassName(){
		return source.getClass().getCanonicalName();
	}
	
	public JarFile getJarFile(){
		return jarFile;
	}
}
