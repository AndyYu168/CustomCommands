package be.pyrrh4.customcommands.command.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import be.pyrrh4.core.lib.messenger.Replacer;
import be.pyrrh4.core.lib.messenger.internal.InternalMessenger;
import be.pyrrh4.core.util.UBukkit;
import be.pyrrh4.core.util.UString;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionBar implements Action
{
	public ActionBar(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.i.replaceString(data.get(0).replace(" ", ""), sender, args);
		String bar = UString.format(CustomCommands.i.replaceString(data.get(1), sender, args));

		// Target player

		if (target.equalsIgnoreCase("player")) {
			InternalMessenger.sendActionBar(sender, null, bar.replace("{receiver}", sender.getName()));
		}

		// Target everyone

		else if (target.equalsIgnoreCase("everyone"))
		{
			for (Player pl : UBukkit.getOnlinePlayers()) {
				InternalMessenger.sendActionBar(pl, null, bar.replace("{receiver}", pl.getName()));
			}
		}

		// Target player in argument

		else
		{
			try
			{
				Player newTarget = Bukkit.getPlayer(target);
				InternalMessenger.sendActionBar(newTarget, null, bar.replace("{receiver}", newTarget.getName()));
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
