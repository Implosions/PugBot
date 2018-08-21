package core.commands;

import java.util.Arrays;

import core.Constants;
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
		this.name = Constants.QUEUESETTINGS_NAME;
		this.helpMsg = Constants.QUEUESETTINGS_HELP;
		this.description = Constants.QUEUESETTINGS_DESC;
		this.adminRequired = true;
		this.server = server;
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
			response = Utils.createMessage(title, settingsManager.toString(), true);
		}else if(args.length == 2){
			ISetting setting = settingsManager.getSetting(args[1]);
			
			response = Utils.createMessage(title, 
					String.format("%s: %s%n%s", setting.getName(), setting.getValueString(), setting.getDescription()), true);
			
		}else if(args.length > 2){
			String[] settingArgs = Arrays.copyOfRange(args, 2, args.length);
			String settingName = args[1];
			
			settingsManager.setSetting(args[1], settingArgs);
			
			response = Utils.createMessage(title, settingName + " updated", true);
		}
		
		return response;
	}
}
