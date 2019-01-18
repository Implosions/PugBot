package pugbot.core.entities.settings;

public abstract class Setting<T> implements ISetting {
	
	private T value;
	private long serverId;
	
	public Setting(long serverId, T value){
		this.value = value;
		this.serverId = serverId;
	}
	
	public T getValue(){
		return value;
	}
	
	protected void setValue(T newValue){
		value = newValue;
	}
	
	public long getServerId() {
		return serverId;
	}
}
