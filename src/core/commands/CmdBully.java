package core.commands;

import java.util.Random;
import java.util.HashMap;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

// TODO: Load actionList from file

public class CmdBully extends Command {

	private String[] actionList = {"They used to call them jumpolines til <@%s>'s momma bounced on one back in '63",
			"<@%s> so poor the ducks throw bread at him", "<@%s>'s momma so fat she on both sides of the family",
			"Ay <@%s> yo momma so fat, I took a picture of her last Christmas and it's still printin'",
			"Ay <@%s> yo momma so fat, she try to enter a ugly contest and the judges be like \"uh sry no professionals\"",
			"Ay <@%s> yo momma so dumb she climbed over a glass wall to see what was on the other side",
			"Ay <@%s> yo momma stink so bad she try to take a bath and the water jumped out",
			"<@%s> so ugly when his mom drop him off for school she got a fine for littering",
			"<@%s> breath smell so bad his dentist will only treat him over da phone",
			"<@%s> so ugly, when I took him to the zoo, the security guard thank me for bringin him back",
			"<@%s> face look like somethin i drew with my left hand",
			"<@%s> is about as useful as a screen door on a submarine",
			"<@%s> head so fat he ran a race and came in first AND last",
			"when i look at <@%s> i understand why some animals eat their young",
			"<@%s> so ugly his imaginary friend used to play with other kids",
			"<@%s>: \"Siri, why am I single?\" *Siri activates front camera*",
			"<@%s> so ugly, his portraits hang themselves", "<@%s> so dumb it take him 2 hours to watch 60 minutes",
			"<@%s> so fat, I swerved to miss him and my car ran outta gas" };
	
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
						this.response = Utils.createMessage(String.format(actionList[random.nextInt(actionList.length)], u.getId()));
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

}
