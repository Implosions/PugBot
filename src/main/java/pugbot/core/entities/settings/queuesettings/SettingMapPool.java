package pugbot.core.entities.settings.queuesettings;

import java.util.Arrays;
import java.util.List;

import pugbot.core.Database;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class SettingMapPool extends QueueSetting<List<String>> {
	
	public SettingMapPool(long serverId, long queueId, List<String> value) {
		super(serverId, queueId, value);
	}

	@Override
	public String getName() {
		return "MapPool";
	}

	@Override
	public String getDescription() {
		return "Sets the map pool for this queue\n"
				+ "Add <map name>\n"
				+ "Delete <map name>";
	}

	@Override
	public String getValueString() {
		if(getValue().isEmpty()) {
			return "N/A";
		}
		
		return String.join(", ", getValue());
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
		String mapName = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
		
		switch(cmd){
			case "add": add(mapName); break;
			case "delete": delete(mapName); break;
			default: throw new BadArgumentsException("Missing **add** or **delete** operator");
		}
	}
	
	@Override
	public void save() {
	}
	
	private void add(String map) {
		if(getValue().contains(map)) {
			throw new InvalidUseException("Map is already added");
		}
		
		if(map.length() > 20) {
			throw new InvalidUseException("Map name must be less than 20 characters");
		}
		
		getValue().add(map);
		Database.addQueueMap(getServerId(), getQueueId(), map);
	}
	
	private void delete(String map) {
		getValue().remove(map);
		Database.deleteQueueMap(getServerId(), getQueueId(), map);
	}

}
