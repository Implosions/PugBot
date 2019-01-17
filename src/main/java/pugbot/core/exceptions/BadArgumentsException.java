package pugbot.core.exceptions;

@SuppressWarnings("serial")
public class BadArgumentsException extends RuntimeException{

	public BadArgumentsException(){
		super("Error! Invalid arguments input.");
	}
	
	public BadArgumentsException(String s){
		super(s);
	}
}
