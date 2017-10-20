package bullybot.commands;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.functions.Stuff;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

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
	
	private Random random;
	private HashMap<User, Long> cooldownCollection;
	private String vip = "236345439706808321";

	public CmdBully() {
		this.helpMsg = Info.BULLY_HELP;
		this.description = Info.BULLY_DESC;
		this.name = "bully";
		this.pugCommand = false;
		random = new Random();
		cooldownCollection = new HashMap<User, Long>();
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try {
			if (args.size() == 1) {
				User u = null;
				for (Member m : member.getGuild().getMembers()) {
					if (m.getUser().getName().equalsIgnoreCase(args.get(0)) || (m.getNickname() != null && m.getNickname().equalsIgnoreCase(args.get(0)))) {
						u = m.getUser();
						break;
					}
				}
				if (u != null) {
					if (!cooldownCollection.containsKey(member.getUser()) || System.currentTimeMillis() - (60000 * 30) >= cooldownCollection.get(member.getUser())) {
						if (u.getId().equals(vip)) {
							u = member.getUser();
						}
						this.response = Stuff.createMessage(String.format(actionList[random.nextInt(actionList.length)], u.getId()));
						if(!member.getUser().getId().equals(vip)){
							cooldownCollection.put(member.getUser(), System.currentTimeMillis());
						}
					} else {
						this.response = Stuff.createMessage("Your bullying is on cooldown", String.format("Time remaining: %d Minutes",
								30 - ((System.currentTimeMillis() - cooldownCollection.get(member.getUser())) / 60000)), false);
					}
				} else {
					throw new DoesNotExistException("User");
				}
			} else {
				throw new BadArgumentsException();
			}
			System.out.println("Completed bully request");
		} catch (BadArgumentsException | DoesNotExistException ex) {
			this.response = Stuff.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
