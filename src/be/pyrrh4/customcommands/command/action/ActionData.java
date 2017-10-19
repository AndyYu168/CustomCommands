package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import be.pyrrh4.core.storage.YMLConfiguration;

public class ActionData
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private String path, type;
	private ArrayList<String> data;

	public ActionData(YMLConfiguration file, String path)
	{
		this.path = path;
		this.type = file.getString(path + ".type");
		this.data = file.getList(path + ".data");
	}

	// ------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------

	public String getPath() {
		return path;
	}

	public String getType() {
		return type;
	}

	public ArrayList<String> getData() {
		return data;
	}
}
