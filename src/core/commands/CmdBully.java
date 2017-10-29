package core.commands;

import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

// TODO: Load actionList from file

public class CmdBully extends Command {

	private List<String> actionList = getActionList();
	
	private Random random = new Random();
	private HashMap<User, Long> cooldownCollection = new HashMap<User, Long>();

	public CmdBully() {
		this.helpMsg = Constants.BULLY_HELP;
		this.description = Constants.BULLY_DESC;
		this.name = Constants.BULLY_NAME;
		this.pugCommand = false;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		try {
			if (args.length == 1) {
				// Match user to given name
				User u = null;
				for (Member m : member.getGuild().getMembers()) {
					if (m.getUser().getName().equalsIgnoreCase(args[0]) ||  m.getEffectiveName().equalsIgnoreCase(args[0])) {
						u = m.getUser();
						break;
					}
				}
				if (u != null) {
					// Check invoker is not on cooldown
					if (!cooldownCollection.containsKey(member.getUser()) || System.currentTimeMillis() - (60000 * 30) >= cooldownCollection.get(member.getUser())) {
						if (u.getId().equals(Constants.OWNER_ID)) {
							u = member.getUser();
						}
						this.response = Utils.createMessage(String.format(actionList.get(random.nextInt(actionList.size())), u.getId()));
						// Put user in cooldownCollection
						if(!member.getUser().getId().equals(Constants.OWNER_ID)){
							cooldownCollection.put(member.getUser(), System.currentTimeMillis());
						}
					} else {
						this.response = Utils.createMessage("Your bullying is on cooldown", String.format("Time remaining: %d Minutes",
								30 - ((System.currentTimeMillis() - cooldownCollection.get(member.getUser())) / 60000)), false);
					}
				} else {
					throw new DoesNotExistException("User");
				}
			} else {
				throw new BadArgumentsException();
			}
			System.out.println(successMsg);
		} catch (BadArgumentsException | DoesNotExistException ex) {
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
	}
	
	private List<String> getActionList(){
		List<String> bl = new ArrayList<String>();
		if (new File("app_data/bullylist.txt").exists()) {
			try {
				Scanner reader = new Scanner(new FileInputStream("app_data/bullylist.txt"));

				while (reader.hasNextLine()) {
					bl.add(reader.nextLine());
				}
				
				reader.close();
				return bl;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

}
