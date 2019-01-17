package pugbot.core.entities.timers;

import java.util.concurrent.TimeUnit;

import pugbot.core.entities.ServerManager;

public class AFKTimer extends Timer {

	private ServerManager manager;
	
	public AFKTimer(ServerManager manager) {
		super(1, TimeUnit.MINUTES);
		this.manager = manager;
	}

	@Override
	protected void cycleCompleted() {
		manager.checkServerActivityLists();
	}
}
