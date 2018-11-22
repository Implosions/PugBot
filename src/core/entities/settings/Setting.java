package core.entities.settings;

public abstract class Setting<T> implements ISetting {
	
	protected SettingManager manager;
	private T value;
	
	public Setting(T value){
		this.value = value;
	}
	
	public T getValue(){
		return value;
	}
	
	protected void setValue(T newValue){
		value = newValue;
	}
	
	public void setManager(SettingManager manager){
		this.manager = manager;
	}
	
	public long getServerId() {
		return manager.getServer().getId();
	}
}
