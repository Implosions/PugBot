package core.commands;

import core.Constants;
import core.entities.Server;
import core.entities.Timer;
import core.entities.menus.RPSMenu;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Trigger;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class CmdRPS extends Command{
	
	public CmdRPS(){
		this.name = Constants.RPS_NAME;
		this.description = Constants.RPS_DESC;
		this.helpMsg = Constants.RPS_HELP;
		this.pugCommand = false;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		try{
			if(args.length == 1){
				User u = null;
				for (Member m : member.getGuild().getMembers()) {
					if (m.getEffectiveName().equalsIgnoreCase(args[0])) {
						u = m.getUser();
						break;
					}
				}
				if(u != null && !u.isBot()){
					Trigger t = () -> System.out.println("RPS completed");
					RPSMenu rps = new RPSMenu(member.getUser(), u, t);
					t = () -> {if(!rps.finished()){rps.complete();}};
					new Timer(180, t).start();
				}else{
					throw new DoesNotExistException("User: " + args[0]);
				}
				
			}else{
				throw new BadArgumentsException();
			}
			this.response = Utils.createMessage("`Challenge sent`");
			System.out.println(success());
		}catch(BadArgumentsException | DoesNotExistException ex){
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
