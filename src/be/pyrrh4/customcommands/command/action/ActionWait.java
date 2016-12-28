package be.pyrrh4.customcommands.command.action;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CustomCommands;

public class ActionWait implements Action
{
	private int current = 0, max = 0;

	public ActionWait(Player sender, List<String> data, String[] args)
	{
		try
		{
			max = Integer.parseInt(data.get(0));
		}
		catch (Exception exception) {
			CustomCommands.i.log(Level.WARNING, "Could not find the delay.");
		}
	}

	@Override
	public boolean isOver()
	{
		return ++current >= max;
	}
}
