package ubisoa.activecloud.hal.filesystem;

import java.util.EventObject;
import java.util.Vector;

public class CapsuleEvent extends EventObject{
	private static final long serialVersionUID = -2258402404358872576L;
	
	private Vector<String> addedJars;
	private Vector<String> deletedJars;
	
	public CapsuleEvent(Object source, Vector<String> addedJars, Vector<String> deletedJars){
		super(source);
		this.addedJars = addedJars;
		this.deletedJars = deletedJars;
	}

	public Vector<String> getAddedJars() {
		return addedJars;
	}

	public void setAddedJars(Vector<String> addedJars) {
		this.addedJars = addedJars;
	}

	public Vector<String> getDeletedJars() {
		return deletedJars;
	}

	public void setDeletedJars(Vector<String> deletedJars) {
		this.deletedJars = deletedJars;
	}
}
