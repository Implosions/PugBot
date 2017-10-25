package core.exceptions;

public class DuplicateEntryException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateEntryException(){
		super("Entry must be unique");
	}
	
	public DuplicateEntryException(String var){
		super(var);
	}
}
