package core.entities.menus;

public abstract class MenuController<Manager extends MenuManager<?, ?>> {

	protected Manager manager1;
	protected Manager manager2;
	protected MenuOptions options;
	private boolean cancelled = false;

	public synchronized void start() {
		buildMenuOptions();
		manager1.createMenu();
		manager2.createMenu();
		onMenuCreation();
		
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

	public Manager getOpponent(MenuManager<?, ?> manager) {
		return (manager == manager1) ? manager2 : manager1;
	}

	public MenuOptions getOptions() {
		return options;
	}
	
	public synchronized void cancel(){
		cancelled = true;
		
		manager1.cancel();
		manager2.cancel();
		notifyAll();
	}
	
	public boolean isCancelled(){
		return cancelled;
	}

	protected abstract void managerActionTaken(Manager manager);

	protected abstract boolean checkCondition();

	protected abstract void complete();

	protected abstract void buildMenuOptions();
	
	protected abstract void onMenuCreation();
}
