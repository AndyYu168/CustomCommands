package be.pyrrh4.customcommands.command.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import be.pyrrh4.core.lib.messenger.Replacer;
import be.pyrrh4.core.util.UBukkit;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionCommandsAs implements Action
{
	public ActionCommandsAs(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.i.replaceString(data.get(0).replace(" ", ""), sender, args);

		// Target player

		if (target.equalsIgnoreCase("player"))
		{
			for (int i = 1; i < data.size(); i++) {
				Bukkit.dispatchCommand(sender, CustomCommands.i.replaceString(data.get(i), sender, args));
			}
		}

		// Target everyone

		else if (target.equalsIgnoreCase("everyone"))
		{
			for (Player pl : UBukkit.getOnlinePlayers())
			{
				for (int i = 1; i < data.size(); i++) {
					Bukkit.dispatchCommand(pl, CustomCommands.i.replaceString(data.get(i), sender, args));
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
					Bukkit.dispatchCommand(newTarget, CustomCommands.i.replaceString(data.get(i), sender, args));
				}
			}
			catch (Exception exception) {
				CustomCommands.i.getMessage("error-target").send(new Replacer("{player}", target), sender);
			}
		}
	}

	@Override
	public boolean isOver()
	{
		return true;
	}
}
