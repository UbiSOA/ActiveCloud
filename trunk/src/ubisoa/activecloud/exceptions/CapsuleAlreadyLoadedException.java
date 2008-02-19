package ubisoa.activecloud.exceptions;

public class CapsuleAlreadyLoadedException extends RuntimeException{
	private static final long serialVersionUID = 6724290185978906618L;
	
	public CapsuleAlreadyLoadedException(String message){
		super(message);
	}
}
