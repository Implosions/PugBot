package bullybot.classfiles;

public class Info {
	//BullyBot
	//public final static String TOKEN = "MzU5OTI1NTczMTM0MzE5NjI4.DKOGYw.dtcqDt34KpEfA3DSuippvW8g2EY";
	//TestBot
	public final static String TOKEN = "MzYyNDAyNzEyODE1NTM0MDgw.DKyKBw.v34M34tX2S_y9BnFZNWsFBilcOU";
	public final static String CLIENT_ID = "359925573134319628";
	public final static String PUG_CHANNEL = "pugs";
	
	public final static String CREATEQUEUE_SUCCESS = "Queue created";
	public final static String CREATEQUEUE_HELP = "!createqueue <name> <maxplayers>";
	public final static String CREATEQUEUE_DESC = "Creates a new queue";
	
	public final static String STATUS_HELP = "!status | !status <index|name>...";
	public final static String STATUS_DESC = "Returns detailed queue information";
	
	public final static String ADD_HELP = "!add | !add <index|name>...";
	public final static String ADD_SUCCESS = "Added to queue";
	public final static String ADD_DESC = "Adds player to the specified queue";
	
	public final static String FINISH_HELP = "!finish";
	public final static String FINISH_SUCCESS = "Finished game";
	public final static String FINISH_DESC = "Completes a game so players can re-add";
	
	public final static String DEL_HELP = "!del | !del <index|name>...";
	public final static String DEL_SUCCESS = "Removed from queue";
	public final static String DEL_DESC = "Deletes player from the specified queue";
	
	public final static String REMOVEQUEUE_HELP = "!removequeue <index|name>...";
	public final static String REMOVEQUEUE_SUCCESS = "Queue removed";
	public final static String REMOVEQUEUE_DESC = "Deletes a game queue";
	
	public final static String EDITQUEUE_HELP = "!editqueue <index|name> <new name> <new max players>";
	public final static String EDITQUEUE_SUCCESS = "Queue edited";
	public final static String EDITQUEUE_DESC = "Edits the specified queue";
	
	public final static String REMOVE_HELP = "!remove <player> | !remove <player> <index|name>...";
	public final static String REMOVE_SUCCESS = "Player removed";
	public final static String REMOVE_DESC = "Removes the specified player from the queue";
	
	public final static String SUB_HELP = "!sub <target> <substitute>";
	public final static String SUB_SUCCESS = "Sub completed";
	public final static String SUB_DESC = "Substitutes an ingame player with another player";
	
	public final static String HELP_HELP = "!help <command>";
	public final static String HELP_DESC = "Returns help information";
	
	public final static String BULLY_HELP = "!bully <player>";
	public final static String BULLY_DESC = "Bullies the specified player";
	
	public final static String NOTIFY_HELP = "!notify <queue> <playercount>";
	public final static String NOTIFY_DESC = "Notifies you when the specified queue hits the desired player count";
	public final static String NOTIFY_SUCCESS = "Notification added";
	
	public final static String DELETENOTIFICATION_HELP = "!deletenotification | !deletenotification <queue>";
	public final static String DELETENOTIFICATION_DESC = "Removes your notification(s)";
	public final static String DELETENOTIFICATION_SUCCESS = "Notification(s) removed";
	
	public final static String TERMINATE_HELP = "!terminate";
	public final static String TERMINATE_DESC = "Kills the bot";
	
	public final static String MUMBLE_HELP = "!mumble";
	public final static String MUMBLE_DESC = "Returns the PUG mumble address";
	public final static String MUMBLE_SUCCESS = "[xzanth.com:64738](mumble://xzanth.com:64738)";
	
}
