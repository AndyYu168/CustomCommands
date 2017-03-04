package be.pyrrh4.customcommands.command.action;

import java.util.List;

import be.pyrrh4.core.storage.PMLReader;

public class ActionData
{
	private final String type;
	private final List<String> data;

	public ActionData(PMLReader file, String fullPath)
	{
		this.type = file.getOrDefault(fullPath + ".type", null);
		this.data = file.getOrDefaultList(fullPath + ".data");
	}

	public String getType()
	{
		return type;
	}

	public List<String> getData()
	{
		return data;
	}
}
