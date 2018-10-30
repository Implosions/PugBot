package core.commands;

import java.util.Arrays;

import core.entities.Queue;
import core.entities.Server;
import core.entities.settings.ISetting;
import core.entities.settings.QueueSettingsManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdQueueSettings extends Command {
	
	public CmdQueueSettings(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length < 1) {
			throw new BadArgumentsException();
		}
		
		String queueVal = args[0];
		Queue queue = server.getQueueManager().getQueue(queueVal);
		
		if(queue == null){
			new InvalidUseException("Queue does not exist");
		}
		
		QueueSettingsManager settingsManager = queue.getSettingsManager();
		String title = queue.getName() + " settings";
		
		if(args.length == 1){
			return Utils.createMessage(title, settingsManager.toString(), true);
		}
		else if(args.length == 2){
			ISetting setting = settingsManager.getSetting(args[1]);
			
			return Utils.createMessage(title, 
					String.format("%s: %s%n%s", setting.getName(), setting.getValueString(), setting.getDescription()), true);
			
		}
		else{
			String[] settingArgs = Arrays.copyOfRange(args, 2, args.length);
			String settingName = args[1];
			
			settingsManager.setSetting(args[1], settingArgs);
			
			return Utils.createMessage(title, settingName + " updated", true);
		}
	}

	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "QueueSettings";
	}

	@Override
	public String getDescription() {
		return "Sets or gets a game queue's settings";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " <queue name|queue index> - Lists this queue's settings\n" +
				getBaseCommand() + " <queue name|queue index> <setting> - Lists a setting and its description\n" +
				getBaseCommand() + " <queue name|queue index> <setting> <args> - Updates a setting";
	}
}
