package ubisoa.activecloud.hal.capsules;

import ubisoa.activecloud.services.NodeAccessService;

/**
 * This class is for presistence purposes only
 * */
public class Capsule {
	private String className;
	
	public Capsule(String className){
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public static void main(String args[]){
		Capsule c = new Capsule("Foo");
		NodeAccessService.get().saveCapsule(c);
	}
}
