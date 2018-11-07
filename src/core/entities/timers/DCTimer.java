package core.entities.timers;

import java.util.concurrent.TimeUnit;

import core.entities.Server;
import net.dv8tion.jda.core.entities.Member;

public class DCTimer extends Timer {

	private Server server;
	private Member member;
	private boolean cancelled = false;
	
	public DCTimer(Server server, Member member) {
		super(server.getSettingsManager().getDCTimeout(), TimeUnit.MINUTES);
		this.server = server;
		this.member = member;
	}

	@Override
	protected void cycleCompleted() {
		if(!cancelled){
			server.dcTimerEnd(member);
		}

		condition = false;
	}

	public synchronized void cancel(){
		cancelled = true;
		
		notifyAll();
	}
}
