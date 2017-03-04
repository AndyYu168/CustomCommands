package be.pyrrh4.customcommands.command.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.customcommands.CustomCommands;

public class ActionCommandsFor implements Action
{
	public ActionCommandsFor(final Player sender, final List<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for (int i = 0; i < data.size(); i++) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), CustomCommands.replaceString(data.get(i), sender, args));
				}
			}
		}.runTask(CustomCommands.i);
	}

	@Override
	public boolean isOver()
	{
		return true;
	}
}
