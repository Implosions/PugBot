package bullybot.classfiles;

import bullybot.classfiles.functions.TimerTrigger;

public class Timer extends Thread{
	
	private Integer time;
	private TimerTrigger m;
	
	public Timer(Integer time, TimerTrigger m){
		this.time = time * 1000;
		this.m = m;
	}
	
	public void run(){
		try{
			Thread.sleep(time);
			m.trigger();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}
}
