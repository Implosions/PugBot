package core.entities.menus;

public interface IMenu {
	public long getId();

	public void buttonClick(String reactionId);

	public void delete();

	public void complete();
}
