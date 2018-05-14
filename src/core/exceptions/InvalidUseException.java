package core.exceptions;

@SuppressWarnings("serial")
public class InvalidUseException extends RuntimeException{
	
	public InvalidUseException(){
		super("Invalid use");
	}
	
	public InvalidUseException(String s){
		super(s);
	}
}
