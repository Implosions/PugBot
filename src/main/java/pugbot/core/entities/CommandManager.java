package pugbot.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pugbot.core.Database;
import pugbot.core.commands.*;

public class CommandManager {
	
	private HashMap<String, ICommand> commandMap = new HashMap<>();
	private List<String> disabledCommands;
	private Server server;
	
	public CommandManager(Server server){
		this.server = server;
		Database.loadCustomCommands(this, server);
		disabledCommands = Database.getDisabledCommands(server.getId());
		
		addCommand(new CmdCreateQueue());
		addCommand(new CmdStatus());
		addCommand(new CmdAdd());
		addCommand(new CmdDel());
		addCommand(new CmdRemoveQueue());
		addCommand(new CmdEditQueue());
		addCommand(new CmdSub());
		addCommand(new CmdHelp());
		addCommand(new CmdRemove());
		addCommand(new CmdBully());
		addCommand(new CmdNotify());
		addCommand(new CmdDeleteNotification());
		addCommand(new CmdTerminate());
		addCommand(new CmdRPS());
		addCommand(new CmdGithub());
		addCommand(new CmdSubCaptain());
		addCommand(new CmdRestart());
		addCommand(new CmdBan());
		addCommand(new CmdUnban());
		addCommand(new CmdAddAdmin());
		addCommand(new CmdRemoveAdmin());
		addCommand(new CmdCreateCommand());
		addCommand(new CmdDeleteCommand());
		addCommand(new CmdServerSettings());
		addCommand(new CmdQueueSettings());
		addCommand(new CmdCreateGroup());
		addCommand(new CmdDeleteGroup());
		addCommand(new CmdGroups());
		addCommand(new CmdJoinGroup());
		addCommand(new CmdLeaveGroup());
		addCommand(new CmdFinishGame());
		addCommand(new CmdStats());
		addCommand(new CmdSwapPlayers());
		addCommand(new CmdRepick());
		addCommand(new CmdDisableCommand());
		addCommand(new CmdEnableCommand());
		addCommand(new CmdMatchHistory());
		addCommand(new CmdSetTeams());
	}
		
	public boolean doesCommandExist(String cmdName){
		return commandMap.containsKey(cmdName.toLowerCase());
	}
	
	public ICommand getCommand(String cmdName){
		return commandMap.get(cmdName.toLowerCase());
	}
	
	public List<ICommand> getCommandList(){
		List<ICommand> cmdList = new ArrayList<>();
		
		cmdList.addAll(commandMap.values());
		
		return cmdList;
	}
	
	public void addCommand(Command cmd){
		cmd.setServer(server);
		commandMap.put(cmd.getName().toLowerCase(), cmd);
	}
	
	public void removeCommand(String cmdName){
		commandMap.remove(cmdName.toLowerCase());
	}
	
	public void disableCommand(String cmdName) {
		cmdName = cmdName.toLowerCase();
		
		if(!disabledCommands.contains(cmdName)) {
			disabledCommands.add(cmdName);
			Database.insertDisabledCommand(server.getId(), cmdName);
		}
	}
	
	public void enableCommand(String cmdName) {
		cmdName = cmdName.toLowerCase();
		
		if(disabledCommands.contains(cmdName)) {
			disabledCommands.remove(cmdName);
			Database.deleteDisabledCommand(server.getId(), cmdName);
		}
	}
	
	public boolean isCommandEnabled(String cmdName) {
		return !disabledCommands.contains(cmdName.toLowerCase());
	}
}
