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
		cmdList.put("createqueue", new CmdCreateQueue());
		cmdList.put("status", new CmdStatus());
		cmdList.put("add", new CmdAdd());
		cmdList.put("finish", new CmdFinish());
		cmdList.put("del", new CmdDel());
		cmdList.put("removequeue", new CmdRemoveQueue());
		cmdList.put("editqueue", new CmdEditQueue());
		cmdList.put("sub", new CmdSub());
		cmdList.put("help", new CmdHelp());
		cmdList.put("remove", new CmdRemove());
		cmdList.put("bully", new CmdBully());
		cmdList.put("notify", new CmdNotify());
		cmdList.put("deletenotification", new CmdDeleteNotification());
		cmdList.put("terminate", new CmdTerminate());
		cmdList.put("mumble", new CmdMumble());
		cmdList.put("loadsettings", new CmdLoadSettings());
		cmdList.put(Constants.RPS_NAME, new CmdRPS());
		
		populateLists();
	}

	public boolean validateCommand(String cmd) {
		return cmdList.containsKey(cmd);
	}

	public Command getCommandObj(String cmd) {
		return cmdList.get(cmd);
	}
	
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
	
	public ArrayList<String> getAdminCmds(){
		return adminCmds;
	}
	
	public  ArrayList<String> getCmds(){
		return cmds;
	}
}
