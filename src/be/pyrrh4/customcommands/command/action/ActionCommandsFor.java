package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionCommandsFor implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionCommandsFor(final Player sender, final ArrayList<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// execute commands
				for (int i = 0; i < data.size(); i++) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.format(Utils.fillPAPI(sender, CustomCommands.instance().replaceString(data.get(i), sender, args))));
				}
			}
		}.runTask(CustomCommands.instance());
	}

	// ------------------------------------------------------------
	// Override : is over
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return true;
	}
}
