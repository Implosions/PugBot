package core.entities;

import core.util.Trigger;

public class Timer extends Thread{
	
	private Integer time;
	private Trigger m;
	
	public Timer(Integer time, Trigger m){
		this.time = time * 1000;
		this.m = m;
	}
	
	public void run(){
		try{
			Thread.sleep(time);
			m.activate();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}
}
