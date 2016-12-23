package be.pyrrh4.customcommands.command.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
			Bukkit.getLogger().warning("[CustomCommands] Could not find the delay.");
		}
	}

	@Override
	public boolean isOver()
	{
		return ++current >= max;
	}
}
