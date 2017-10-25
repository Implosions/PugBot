package core.exceptions;

public class InvalidUseException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidUseException(){
		super("Invalid use");
	}
	
	public InvalidUseException(String s){
		super(s);
	}
}
