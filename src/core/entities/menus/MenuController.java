package core.entities.menus;

import java.util.List;

import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public abstract class MenuController<Manager extends MenuManager<?, ?>> {

	protected Manager manager1;
	protected Manager manager2;
	protected List<List<Field>> pages;
	protected List<String> fieldButtons;
	protected List<String> utilityButtons;
	protected int pageSize = 0;

	public synchronized void start() {
		manager1.createMenu();
		manager2.createMenu();
		onMenuCreation();
		
		boolean condition = true;

		while (condition) {
			try {
				wait();
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

	public List<Field> getPage(int pageIndex) {
		return pages.get(pageIndex);
	}

	public int getNumberOfPages() {
		if (pages == null) {
			return 0;
		}

		return pages.size();
	}

	protected void removeField(int index) {
		int pageIndex = index / pageSize;
		int fieldIndex = index % pageSize;
		List<Field> page = pages.get(pageIndex);

		page.remove(fieldIndex);

		if (page.size() == 0) {
			pages.remove(page);
		}
	}
	
	public void cancel(){
		manager1.cancel();
		manager2.cancel();
	}

	protected abstract void managerActionTaken(Manager manager);

	protected abstract boolean checkCondition();

	protected abstract void complete();

	protected abstract void generatePages();
	
	protected abstract void onMenuCreation();
}
