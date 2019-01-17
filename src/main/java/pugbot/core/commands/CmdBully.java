package pugbot.core.commands;

import java.util.Random;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.exceptions.BadArgumentsException;

public class CmdBully extends Command {

	private List<String> actionList = getActionList();
	private Random random = new Random();

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}
		
		Member m = server.getMember(args[0]);
		String action = actionList.get(random.nextInt(actionList.size()));

		return Utils.createMessage(String.format(action, m.getUser().getId()));
	}

	private List<String> getActionList() {
		List<String> actionList = new ArrayList<String>();
		String listFileName = "app_data/action_list.txt";
		
		try {
			Scanner reader = new Scanner(new FileInputStream(listFileName));

			while (reader.hasNextLine()) {
				actionList.add(reader.nextLine());
			}

			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return actionList;
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
		return "Bully";
	}

	@Override
	public String getDescription() {
		return "Banter another user";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <user>";
	}
}
