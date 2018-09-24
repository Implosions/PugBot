package core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.Database;
import core.commands.*;

public class CommandManager {
	
	private HashMap<String, ICommand> commandMap = new HashMap<>();
	
	public CommandManager(Server server){
		Database.loadCustomCommands(this, server);
		
		addCommand(new CmdCreateQueue(server));
		addCommand(new CmdStatus(server));
		addCommand(new CmdAdd(server));
		addCommand(new CmdDel(server));
		addCommand(new CmdRemoveQueue(server));
		addCommand(new CmdEditQueue(server));
		addCommand(new CmdSub(server));
		addCommand(new CmdHelp(server));
		addCommand(new CmdRemove(server));
		addCommand(new CmdBully(server));
		addCommand(new CmdNotify(server));
		addCommand(new CmdDeleteNotification(server));
		addCommand(new CmdTerminate(server));
		addCommand(new CmdRPS(server));
		addCommand(new CmdGithub(server));
		addCommand(new CmdSubCaptain(server));
		addCommand(new CmdRestart(server));
		addCommand(new CmdBan(server));
		addCommand(new CmdUnban(server));
		addCommand(new CmdAddAdmin(server));
		addCommand(new CmdRemoveAdmin(server));
		addCommand(new CmdCreateCommand(server));
		addCommand(new CmdDeleteCommand(server));
		addCommand(new CmdServerSettings(server));
		addCommand(new CmdQueueSettings(server));
		addCommand(new CmdCreateGroup(server));
		addCommand(new CmdDeleteGroup(server));
		addCommand(new CmdGroups(server));
		addCommand(new CmdJoinGroup(server));
		addCommand(new CmdLeaveGroup(server));
		addCommand(new CmdFinishGame(server));
		addCommand(new CmdStats(server));
	}
		
	public boolean doesCommandExist(String cmdName){
		return commandMap.containsKey(cmdName);
	}
	
	public ICommand getCommand(String cmdName){
		return commandMap.get(cmdName);
	}
	
	public List<ICommand> getCommandList(){
		List<ICommand> cmdList = new ArrayList<>();
		
		cmdList.addAll(commandMap.values());
		
		return cmdList;
	}
	
	public void addCommand(ICommand cmd){
		commandMap.put(cmd.getName().toLowerCase(), cmd);
	}
	
	public void removeCommand(String cmdName){
		commandMap.remove(cmdName);
	}
}
