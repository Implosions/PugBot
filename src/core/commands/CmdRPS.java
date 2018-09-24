package core.commands;

import core.entities.Server;
import core.entities.menus.RPSMenuController;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRPS extends Command {

	public CmdRPS(Server server) {
		super(server);
	}

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
		

		this.response = Utils.createMessage("`Challenge sent`");

		return response;
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
