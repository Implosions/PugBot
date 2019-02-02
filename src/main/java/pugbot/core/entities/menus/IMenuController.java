package pugbot.core.entities.menus;

public interface IMenuController {
	
	public void start();
	
	public void cancel();
	
	public boolean isCancelled();
}
