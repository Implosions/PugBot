package core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import core.Constants;
import core.Database;
import core.commands.*;

public class Commands {
	private HashMap<String, Command> cmdList;
	private List<String> adminCmds;
	private List<String> cmds;
	private List<String> customCmds;

	public Commands(Server server) {
		cmdList = new HashMap<String, Command>();
		adminCmds = new ArrayList<String>();
		cmds = new ArrayList<String>();
		customCmds = new ArrayList<String>();
		
		// Commands
		cmdList.put(Constants.CREATEQUEUE_NAME, new CmdCreateQueue(server));
		cmdList.put(Constants.STATUS_NAME, new CmdStatus(server));
		cmdList.put(Constants.ADD_NAME, new CmdAdd(server));
		cmdList.put(Constants.FINISH_NAME, new CmdFinish(server));
		cmdList.put(Constants.DEL_NAME, new CmdDel(server));
		cmdList.put(Constants.REMOVEQUEUE_NAME, new CmdRemoveQueue(server));
		cmdList.put(Constants.EDITQUEUE_NAME, new CmdEditQueue(server));
		cmdList.put(Constants.SUB_NAME, new CmdSub(server));
		cmdList.put(Constants.HELP_NAME, new CmdHelp(server));
		cmdList.put(Constants.REMOVE_NAME, new CmdRemove(server));
		cmdList.put(Constants.BULLY_NAME, new CmdBully(server));
		cmdList.put(Constants.NOTIFY_NAME, new CmdNotify(server));
		cmdList.put(Constants.DELETENOTIFICATION_NAME, new CmdDeleteNotification(server));
		cmdList.put(Constants.TERMINATE_NAME, new CmdTerminate(server));
		cmdList.put(Constants.RPS_NAME, new CmdRPS(server));
		cmdList.put(Constants.GITHUB_NAME, new CmdGithub(server));
		cmdList.put(Constants.SUBCAPTAIN_NAME, new CmdSubCaptain(server));
		cmdList.put(Constants.RESTART_NAME, new CmdRestart(server));
		cmdList.put(Constants.BAN_NAME, new CmdBan(server));
		cmdList.put(Constants.UNBAN_NAME, new CmdUnban(server));
		cmdList.put(Constants.ADDADMIN_NAME, new CmdAddAdmin(server));
		cmdList.put(Constants.REMOVEADMIN_NAME, new CmdRemoveAdmin(server));
		cmdList.put(Constants.CREATECOMMAND_NAME, new CmdCreateCommand(server));
		cmdList.put(Constants.DELETECOMMAND_NAME, new CmdDeleteCommand(server));
		cmdList.put(Constants.SERVERSETTINGS_NAME, new CmdServerSettings(server));
		cmdList.put(Constants.QUEUESETTINGS_NAME, new CmdQueueSettings(server));
		cmdList.put(Constants.ADDGROUP_NAME, new CmdCreateGroup(server));
		cmdList.put(Constants.DELETEGROUP_NAME, new CmdDeleteGroup(server));
		cmdList.put(Constants.GROUPS_NAME, new CmdGroups(server));
		cmdList.put(Constants.JOINGROUP_NAME, new CmdJoinGroup(server));
		cmdList.put(Constants.LEAVEGROUP_NAME, new CmdLeaveGroup(server));
		
		populateLists();	
		loadCustomCommands(server.getId());
	}

	/**
	 * Checks if a command is valid
	 * 
	 * @param cmd the name of the command to check
	 * @return returns true if valid
	 */
	public boolean validateCommand(String cmd) {
		return cmdList.containsKey(cmd);
	}

	/**
	 * Returns the command object of a command
	 * 
	 * @param cmd the name of the command
	 * @return the command object
	 */
	public Command getCommandObj(String cmd) {
		return cmdList.get(cmd);
	}
	
	/**
	 * Populates and sorts the cmds and adminCmds lists
	 */
	private void populateLists(){
		for(Command cmd : cmdList.values()){
			if(cmd.getAdminRequired()){
				adminCmds.add(cmd.getName());
			}else{
				cmds.add(cmd.getName());
			}
		}
		Collections.sort(adminCmds, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(cmds, String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * @return the list of admin command names
	 */
	public List<String> getAdminCmds(){
		return adminCmds;
	}
	
	/**
	 * @return the list of regular command names
	 */
	public List<String> getCmds(){
		return cmds;
	}
	
	/**
	 * @return the list of custom command names
	 */
	public List<String> getCustomCmds(){
		return customCmds;
	}
	
	/**
	 * Adds a custom command to the cmdList
	 * 
	 * @param cmd The command to add
	 */
	public void addCommand(Command cmd){
		cmdList.put(cmd.getName(), cmd);
		customCmds.add(cmd.getName());
	}
	
	/**
	 * Removes a custom command from the cmdList
	 * 
	 * @param name The name of the command to remove
	 */
	public void removeCommand(String name){
		cmdList.remove(name);
		customCmds.remove(name);
	}
	
	/**
	 * Loads all of the custom commands from the database
	 * 
	 * @param serverId The id of the server to load custom commands for
	 */
	private void loadCustomCommands(long serverId){
		for(CustomCommand cmd : Database.getCustomCommands(serverId)){
			addCommand(cmd);
		}
	}
}
