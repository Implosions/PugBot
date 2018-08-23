package core.commands;

import java.util.Random;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdBully extends Command {

	private List<String> actionList = getActionList();
	private Random random = new Random();

	public CmdBully(Server server) {
		this.helpMsg = Constants.BULLY_HELP;
		this.description = Constants.BULLY_DESC;
		this.name = Constants.BULLY_NAME;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		Member m = server.getMember(args[0]);
		String action = actionList.get(random.nextInt(actionList.size()));

		response = Utils.createMessage(String.format(action, m.getUser().getId()));

		return response;
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
}
