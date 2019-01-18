package pugbot.core.entities.settings.queuesettings;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.core.entities.Role;
import pugbot.core.Database;
import pugbot.core.entities.ServerManager;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class SettingRoleRestrictions extends QueueSetting<List<Role>>{

	public SettingRoleRestrictions(long serverId, long queueId, List<Role> value) {
		super(serverId, queueId, value);
	}

	@Override
	public String getName() {
		return "RoleRestrictions";
	}

	@Override
	public String getDescription() {
		return "Restricts this queue to the listed roles\n" +
				"add <role> - Adds a role to the list\n" +
				"delete <role> - Deletes a role from the list";
	}

	@Override
	public String getValueString() {	
		StringBuilder sb = new StringBuilder();
		
		for(Role role : getValue()){
			sb.append(role.getName() + ", "); 
		}
		
		if(sb.length() == 0){
			return "N/A";
		}
		
		sb.delete(sb.length() - 2, sb.length());
		
		return sb.toString();
	}

	@Override
	public String getSaveString() {
		return null;
	}

	@Override
	public void set(String[] args) {
		if(args.length < 2){
			throw new BadArgumentsException();
		}
		
		String cmd = args[0].toLowerCase();
		String roleName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		
		switch(cmd){
		case "add": add(roleName); break;
		case "delete": delete(roleName); break;
		default: throw new BadArgumentsException("Missing **add** or **delete** operator");
		}
	}

	private void add(String roleName){
		Role role = findRole(roleName);
		
		if(!getValue().contains(role)){
			getValue().add(role);
			Database.addQueueRole(getServerId(), getQueueId(), role.getIdLong());
		}
	}
	
	private void delete(String roleName){
		Role role = findRole(roleName);
		
		getValue().remove(role);
		Database.deleteQueueRole(getServerId(), getQueueId(), role.getIdLong());
	}
	
	private Role findRole(String roleName){
		List<Role> roles = ServerManager.getGuild(getServerId()).getRolesByName(roleName, true);
		
		if(roles.size() == 0){
			throw new InvalidUseException(String.format("role '%s' does not exist", roleName));
		}
		
		return roles.get(0);
	}
	
	@Override
	public void save() {
	}
}
