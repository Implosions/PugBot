package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.menus.RPSMenuController;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class CmdRPS extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		Member m = server.getMember(args[0]);

		if (m.getUser().isBot()) {
			throw new InvalidUseException("Cannot RPS a bot");
		}

		new Thread(new Runnable(){
			public void run(){
				RPSMenuController controller = new RPSMenuController(caller, m);
				controller.start();
			}
		}).start();
		
		return Utils.createMessage("`Challenge sent`");
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}

	@Override
	public boolean isGlobalCommand() {
		return true;
	}

	@Override
	public String getName() {
		return "RPS";
	}

	@Override
	public String getDescription() {
		return "Starts a Rock-Paper-Scissors game with another user";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <username>";
	}
}
