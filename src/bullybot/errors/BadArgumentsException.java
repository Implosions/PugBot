package bullybot.errors;

public class BadArgumentsException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadArgumentsException(){
		super("Error! Invalid arguments input.");
	}
	
	public BadArgumentsException(String s){
		super(s);
	}
}
