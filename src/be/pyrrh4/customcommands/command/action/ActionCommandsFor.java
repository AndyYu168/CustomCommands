package be.pyrrh4.customcommands.command.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CustomCommands;

public class ActionCommandsFor implements Action
{
	public ActionCommandsFor(Player sender, List<String> data, String[] args)
	{
		for (int i = 0; i < data.size(); i++) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), CustomCommands.i.replaceString(data.get(i), sender, args));
		}
	}

	@Override
	public boolean isOver()
	{
		return true;
	}
}
