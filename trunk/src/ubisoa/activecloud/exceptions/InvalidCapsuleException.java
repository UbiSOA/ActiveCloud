package ubisoa.activecloud.exceptions;

public class InvalidCapsuleException extends RuntimeException{

	private static final long serialVersionUID = 1705096474003641640L;
	
	public InvalidCapsuleException(String message){
		super(message);
	}
}
