package be.pyrrh4.customcommands.command.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.core.messenger.Replacer;
import be.pyrrh4.core.util.UBukkit;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionCommandsAs implements Action
{
	public ActionCommandsAs(final Player sender, final List<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				String target = CustomCommands.replaceString(data.get(0).replace(" ", ""), sender, args);

				// Target player

				if (target.equalsIgnoreCase("player"))
				{
					for (int i = 1; i < data.size(); i++) {
						Bukkit.dispatchCommand(sender, CustomCommands.replaceString(data.get(i), sender, args));
					}
				}

				// Target everyone

				else if (target.equalsIgnoreCase("everyone"))
				{
					for (Player pl : UBukkit.getOnlinePlayers())
					{
						for (int i = 1; i < data.size(); i++) {
							Bukkit.dispatchCommand(pl, CustomCommands.replaceString(data.get(i), sender, args));
						}
					}
				}

				// Target player in argument

				else
				{
					try
					{
						Player newTarget = Bukkit.getPlayer(target);

						for (int i = 1; i < data.size(); i++) {
							Bukkit.dispatchCommand(newTarget, CustomCommands.replaceString(data.get(i), sender, args));
						}
					}
					catch (Exception exception) {
						CustomCommands.i.config.getMessage("error_target").send(new Replacer("{player}", target), sender);
					}
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
