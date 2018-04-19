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

	public Commands(long serverId) {
		cmdList = new HashMap<String, Command>();
		adminCmds = new ArrayList<String>();
		cmds = new ArrayList<String>();
		customCmds = new ArrayList<String>();
		
		// Commands
		cmdList.put(Constants.CREATEQUEUE_NAME, new CmdCreateQueue());
		cmdList.put(Constants.STATUS_NAME, new CmdStatus());
		cmdList.put(Constants.ADD_NAME, new CmdAdd());
		cmdList.put(Constants.FINISH_NAME, new CmdFinish());
		cmdList.put(Constants.DEL_NAME, new CmdDel());
		cmdList.put(Constants.REMOVEQUEUE_NAME, new CmdRemoveQueue());
		cmdList.put(Constants.EDITQUEUE_NAME, new CmdEditQueue());
		cmdList.put(Constants.SUB_NAME, new CmdSub());
		cmdList.put(Constants.HELP_NAME, new CmdHelp());
		cmdList.put(Constants.REMOVE_NAME, new CmdRemove());
		cmdList.put(Constants.BULLY_NAME, new CmdBully());
		cmdList.put(Constants.NOTIFY_NAME, new CmdNotify());
		cmdList.put(Constants.DELETENOTIFICATION_NAME, new CmdDeleteNotification());
		cmdList.put(Constants.TERMINATE_NAME, new CmdTerminate());
		cmdList.put(Constants.MUMBLE_NAME, new CmdMumble());
		cmdList.put(Constants.LOADSETTINGS_NAME, new CmdLoadSettings());
		cmdList.put(Constants.RPS_NAME, new CmdRPS());
		cmdList.put(Constants.GITHUB_NAME, new CmdGithub());
		cmdList.put(Constants.SUBCAPTAIN_NAME, new CmdSubCaptain());
		cmdList.put(Constants.RESTART_NAME, new CmdRestart());
		//cmdList.put(Constants.PUGSERVERS_NAME, new CmdPugServers());
		cmdList.put(Constants.BAN_NAME, new CmdBan());
		cmdList.put(Constants.UNBAN_NAME, new CmdUnban());
		cmdList.put(Constants.ADDADMIN_NAME, new CmdAddAdmin());
		cmdList.put(Constants.REMOVEADMIN_NAME, new CmdRemoveAdmin());
		cmdList.put(Constants.SETTINGS_NAME, new CmdSettings());
		cmdList.put(Constants.CREATECOMMAND_NAME, new CmdCreateCommand());
		cmdList.put(Constants.DELETECOMMAND_NAME, new CmdDeleteCommand());
		
		loadCustomCommands(serverId);
		
		populateLists();
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
