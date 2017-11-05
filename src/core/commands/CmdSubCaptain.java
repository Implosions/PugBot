package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class CmdSubCaptain extends Command{

	public CmdSubCaptain(){
		this.name = Constants.SUBCAPTAIN_NAME;
		this.description = Constants.SUBCAPTAIN_DESC;
		this.helpMsg = Constants.SUBCAPTAIN_HELP;
	}
	
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		try{
			if(args.length == 1){
				User target = null;
				for (Member m : server.getGuild().getMembers()) {
					if (m.getEffectiveName().equalsIgnoreCase(args[0])) {
						target = m.getUser();
						break;
					}
				}
				if(target != null){
					server.getQueueManager().subCaptain(member.getUser(), target);
				}else{
					throw new DoesNotExistException(args[0]);
				}
			}else{
				throw new BadArgumentsException();
			}
			this.response = Utils.createMessage(String.format("`%s is now a captain`", member.getEffectiveName()));
		}catch(BadArgumentsException | InvalidUseException | DoesNotExistException ex){
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
