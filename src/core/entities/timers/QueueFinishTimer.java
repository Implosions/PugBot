package core.entities.timers;

import java.util.concurrent.TimeUnit;

import core.entities.Game;
import core.entities.QueueManager;

public class QueueFinishTimer extends Timer {

	private QueueManager manager;
	private Game game;
	private int timerDuration;
	private int timeElapsed = 0;
	
	public QueueFinishTimer(QueueManager manager, Game game) {
		super(1, TimeUnit.SECONDS);
		this.manager = manager;
		this.game = game;
		timerDuration = manager.getServer().getSettingsManager().getQueueFinishTimer();
	}

	@Override
	protected void cycleCompleted() {
		timeElapsed++;
		
		if(timeElapsed >= timerDuration){
			condition = false;
			
			manager.queueFinishTimerEnd(game);
		}
	}
	
	public int getTimeRemaining(){
		return timerDuration - timeElapsed;
	}

}
