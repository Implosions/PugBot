package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdSubCaptain extends Command{

	public CmdSubCaptain(){
		this.name = Constants.SUBCAPTAIN_NAME;
		this.description = Constants.SUBCAPTAIN_DESC;
		this.helpMsg = Constants.SUBCAPTAIN_HELP;
	}
	
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		if (args.length == 1) {
			Member target = server.getMember(args[0]);

			if (target != null) {
				server.getQueueManager().subCaptain(member.getUser(), target.getUser());
			} else {
				throw new DoesNotExistException(args[0]);
			}
		} else {
			throw new BadArgumentsException();
		}
		this.response = Utils.createMessage(String.format("`%s is now a captain`", member.getEffectiveName()));
		System.out.println(success());
	}
}
