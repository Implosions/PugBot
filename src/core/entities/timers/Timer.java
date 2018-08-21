package core.entities.timers;

import java.util.concurrent.TimeUnit;

public abstract class Timer extends Thread {
	
	private int timeVal;
	private TimeUnit unit;
	protected boolean condition = true;
	
	public Timer(int timeVal, TimeUnit unit){
		this.timeVal = timeVal;
		this.unit = unit;
	}
	
	public void run(){
		long ms = TimeUnit.MILLISECONDS.convert(timeVal, unit);

		while(condition){
			try{
				Thread.sleep(ms);
			}catch(InterruptedException ex){
				return;
			}
			
			cycleCompleted();
		}
	}
	
	protected abstract void cycleCompleted();
}
