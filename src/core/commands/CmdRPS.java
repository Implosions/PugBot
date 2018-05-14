package core.commands;

import core.Constants;
import core.entities.Server;
import core.entities.Timer;
import core.entities.menus.RPSMenu;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Trigger;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRPS extends Command{
	
	public CmdRPS(){
		this.name = Constants.RPS_NAME;
		this.description = Constants.RPS_DESC;
		this.helpMsg = Constants.RPS_HELP;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if (args.length == 1) {
			Member m = server.getMember(args[0]);
			if (m != null && !m.getUser().isBot()) {
				Trigger t = () -> System.out.println("RPS completed");
				RPSMenu rps = new RPSMenu(member.getUser(), m.getUser(), t);
				t = () -> {
					if (!rps.finished()) {
						rps.complete();
					}
				};
				new Timer(180, t).start();
			} else {
				throw new InvalidUseException("User does not exist");
			}

		} else {
			throw new BadArgumentsException();
		}
		this.response = Utils.createMessage("`Challenge sent`");
		System.out.println(success());
		
		return response;
	}
}
