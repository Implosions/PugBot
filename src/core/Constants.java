package core;

public class Constants {
	
	public final static String OWNER_ID = "236345439706808321";
	
	public final static String CREATEQUEUE_NAME = "createqueue";
	public final static String CREATEQUEUE_HELP = "!createqueue <name> <maxplayers>";
	public final static String CREATEQUEUE_DESC = "Creates a new queue";
	
	public final static String STATUS_NAME = "status";
	public final static String STATUS_HELP = "!status | !status <index|name>...";
	public final static String STATUS_DESC = "Returns detailed queue information";
	
	public final static String ADD_NAME = "add";
	public final static String ADD_HELP = "!add | !add <index|name>...";
	public final static String ADD_DESC = "Adds player to the specified queue";
	
	public final static String FINISH_NAME = "finish";
	public final static String FINISH_HELP = "!finish";
	public final static String FINISH_DESC = "Completes a game so players can re-add";
	
	public final static String DEL_NAME = "del";
	public final static String DEL_HELP = "!del | !del <index|name>...";
	public final static String DEL_DESC = "Deletes player from the specified queue";
	
	public final static String REMOVEQUEUE_NAME = "removequeue";
	public final static String REMOVEQUEUE_HELP = "!removequeue <index|name>...";
	public final static String REMOVEQUEUE_DESC = "Deletes a game queue";
	
	public final static String EDITQUEUE_NAME = "editqueue";
	public final static String EDITQUEUE_HELP = "!editqueue <index|name> <new name> <new max players>";
	public final static String EDITQUEUE_DESC = "Edits the specified queue";
	
	public final static String REMOVE_NAME = "remove";
	public final static String REMOVE_HELP = "!remove <player> | !remove <player> <index|name>...";
	public final static String REMOVE_DESC = "Removes the specified player from the queue";
	
	public final static String SUB_NAME = "sub";
	public final static String SUB_HELP = "!sub <target> <substitute>";
	public final static String SUB_DESC = "Substitutes an ingame player with another player";
	
	public final static String HELP_NAME = "help";
	public final static String HELP_HELP = "!help <command>";
	public final static String HELP_DESC = "Returns help information";
	
	public final static String BULLY_NAME = "bully";
	public final static String BULLY_HELP = "!bully <player>";
	public final static String BULLY_DESC = "Bullies the specified player";
	
	public final static String NOTIFY_NAME = "notify";
	public final static String NOTIFY_HELP = "!notify <queue> <playercount>";
	public final static String NOTIFY_DESC = "Notifies you when the specified queue hits the desired player count";
	
	public final static String DELETENOTIFICATION_NAME = "deletenotification";
	public final static String DELETENOTIFICATION_HELP = "!deletenotification | !deletenotification <queue>";
	public final static String DELETENOTIFICATION_DESC = "Removes your notification(s)";
	
	public final static String TERMINATE_NAME = "terminate";
	public final static String TERMINATE_HELP = "!terminate";
	public final static String TERMINATE_DESC = "Kills the bot";
	
	public final static String MUMBLE_NAME = "mumble";
	public final static String MUMBLE_HELP = "!mumble";
	public final static String MUMBLE_DESC = "Returns the PUG mumble address";
	
	public final static String LOADSETTINGS_NAME = "loadsettings";
	public final static String LOADSETTINGS_HELP = "!loadsettings";
	public final static String LOADSETTINGS_DESC = "Loads settings from file";
	
	public final static String RPS_NAME = "rps";
	public final static String RPS_HELP = "!rps <opponent>";
	public final static String RPS_DESC = "Challenges a player to a rock paper scissors duel";
	
	public final static String GITHUB_NAME = "github";
	public final static String GITHUB_HELP = "!github";
	public final static String GITHUB_DESC = "Returns the github repo location";
	
	public final static String SUBCAPTAIN_NAME = "subcaptain";
	public final static String SUBCAPTAIN_HELP = "!subcaptain <captain>";
	public final static String SUBCAPTAIN_DESC = "Replaces a captain with yourself";
	
	public final static String RESTART_NAME = "restart";
	public final static String RESTART_HELP = "!restart";
	public final static String RESTART_DESC = "restarts server instance";
	
	public final static String PUGSERVERS_NAME = "pugservers";
	public final static String PUGSERVERS_HELP = "!pugservers | !pugservers <region>";
	public final static String PUGSERVERS_DESC = "creates a list of pug servers";
	
	public final static String BAN_NAME = "ban";
	public final static String BAN_HELP = "!ban <name>";
	public final static String BAN_DESC = "bans a user from interacting with the bot";
	
	public final static String UNBAN_NAME = "unban";
	public final static String UNBAN_HELP = "!unban <name>";
	public final static String UNBAN_DESC = "unbans a user";
	
	public final static String ADDADMIN_NAME = "addadmin";
	public final static String ADDADMIN_HELP = "!addadmin <name>";
	public final static String ADDADMIN_DESC = "gives a user bot admin privileges";
	
	public final static String REMOVEADMIN_NAME = "removeadmin";
	public final static String REMOVEADMIN_HELP = "!removeadmin <name>";
	public final static String REMOVEADMIN_DESC = "removes a user's admin privileges";
	
	public final static String SETTINGS_NAME = "settings";
	public final static String SETTINGS_HELP = "!settings | !settings <setting> | !settings <setting> <value>";
	public final static String SETTINGS_DESC = "gets or sets a setting's value";
	
	public final static String CREATECOMMAND_NAME = "createcommand";
	public final static String CREATECOMMAND_HELP = "!createcommand <name> <message>";
	public final static String CREATECOMMAND_DESC = "Creates a new command that responds with a set message";
	
	public final static String DELETECOMMAND_NAME = "deletecommand";
	public final static String DELETECOMMAND_HELP = "!deletecommand <name>";
	public final static String DELETECOMMAND_DESC = "Deletes a custom command";
}
