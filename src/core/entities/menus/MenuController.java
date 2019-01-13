package core.entities.menus;

public abstract class MenuController<T extends EmbedMenu> {

	private T[] menus;
	private MenuOptions options;
	private boolean cancelled = false;

	public synchronized void start() {
		options = buildMenuOptions();
		
		for(T menu : menus) {
			menu.start();
		}
		
		boolean condition = true;

		while (condition) {
			try {
				wait();
				
				if(cancelled){
					return;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			condition = checkCondition();
		}

		complete();
	}
	
	public synchronized void cancel(){
		cancelled = true;
		
		for(T menu : menus) {
			menu.delete();
		}
		
		notifyAll();
	}
	
	public boolean isCancelled(){
		return cancelled;
	}

	protected abstract void menuActionTaken(IMenu sender);

	protected abstract boolean checkCondition();

	protected abstract void complete();

	protected abstract MenuOptions buildMenuOptions();
	
	public MenuOptions getMenuOptions() {
		return options;
	}
	
	public T[] getMenus() {
		return menus;
	}
	
	public T getMenu(int i) {
		return menus[i];
	}
	
	public void setMenus(T[] menus) {
		this.menus = menus;
	}
	
	public void updateMenus() {
		for(T menu : menus) {
			menu.update();
		}
	}
}
