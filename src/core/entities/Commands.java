package core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import core.Constants;
import core.commands.*;

public class Commands {
	private HashMap<String, Command> cmdList;
	private ArrayList<String> adminCmds;
	private ArrayList<String> cmds;

	public Commands() {
		cmdList = new HashMap<String, Command>();
		adminCmds = new ArrayList<String>();
		cmds = new ArrayList<String>();

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
		cmdList.put(Constants.PUGSERVERS_NAME, new CmdPugServers());
		cmdList.put(Constants.BAN_NAME, new CmdBan());
		cmdList.put(Constants.UNBAN_NAME, new CmdUnban());
		cmdList.put(Constants.ADDADMIN_NAME, new CmdAddAdmin());
		cmdList.put(Constants.REMOVEADMIN_NAME, new CmdRemoveAdmin());
		
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
	 * @return the list of admin commands
	 */
	public ArrayList<String> getAdminCmds(){
		return adminCmds;
	}
	/**
	 * @return the list of regular commands
	 */
	public  ArrayList<String> getCmds(){
		return cmds;
	}
}
