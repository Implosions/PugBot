package core.entities;

public class Setting {
	
	private String name;
	private Object value;
	private String descriptor;
	private String description;
	
	public Setting(String name, String value, String descriptor, String description){
		this.name = name;
		this.value = value;
		this.descriptor = descriptor;
		this.description = description;
	}
	
	public Setting(String name, Boolean value, String descriptor, String description){
		this.name = name;
		this.value = value;
		this.descriptor = descriptor;
		this.description = description;
	}
	
	public Setting(String name, Integer value, String descriptor, String description){
		this.name = name;
		this.value = value;
		this.descriptor = descriptor;
		this.description = description;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getValue(){
		return value;
	}
	
	public String getDescriptor(){
		return descriptor;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setValue(Object newValue){
		value = newValue;
	}
}
