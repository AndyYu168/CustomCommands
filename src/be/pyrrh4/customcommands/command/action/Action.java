package be.pyrrh4.customcommands.command.action;

public interface Action
{
	public enum Type
	{
		MESSAGE;
	}

	public boolean isOver();
}
