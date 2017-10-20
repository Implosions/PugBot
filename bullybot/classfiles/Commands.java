package bullybot.classfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bullybot.commands.*;

public class Commands {
	private static HashMap<String, Command> cmdList;
	private static ArrayList<String> adminCmds;
	private static ArrayList<String> cmds;

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
		
		populateLists();
	}

	public static boolean validateCommand(String cmd) {
		return cmdList.containsKey(cmd);
	}

	public static Command getCommandObj(String cmd) {
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
	
	public static ArrayList<String> getAdminCmds(){
		return adminCmds;
	}
	
	public static ArrayList<String> getCmds(){
		return cmds;
	}
}
