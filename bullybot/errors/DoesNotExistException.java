package bullybot.errors;

public class DoesNotExistException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public DoesNotExistException(){
		super("Does not exist");
	}
	
	public DoesNotExistException(String var){
		super(var + " does not exist");
	}
}
