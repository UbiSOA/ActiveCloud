package ubisoa.activecloud.exceptions;

public class CapsuleInitException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1970631634369710255L;
	
	String theClass;

	public CapsuleInitException(String message, String className){
		super(message);
		theClass = className;
	}
	
	public String getFailedCapsuleName(){
		return theClass;
	}
}
