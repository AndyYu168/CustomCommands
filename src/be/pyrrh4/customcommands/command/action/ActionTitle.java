package be.pyrrh4.customcommands.command.action;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import be.pyrrh4.core.messenger.Messenger;
import be.pyrrh4.core.messenger.Replacer;
import be.pyrrh4.core.util.UBukkit;
import be.pyrrh4.core.util.UString;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionTitle implements Action
{
	public ActionTitle(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.replaceString(data.get(0).replace(" ", ""), sender, args);
		String title = UString.format(CustomCommands.replaceString(data.get(1), sender, args));
		String subtitle = UString.format(CustomCommands.replaceString(data.get(2), sender, args));
		int fadeIn, duration, fadeOut;

		try
		{
			fadeIn = Integer.parseInt(data.get(3));
			duration = Integer.parseInt(data.get(4));
			fadeOut = Integer.parseInt(data.get(5));
		}
		catch (Exception exception) {
			CustomCommands.i.log(Level.WARNING, "Could not find the fadeIn, duration or fadeOut.");
			return;
		}

		// Target player

		if (target.equalsIgnoreCase("player")) {
			Messenger.sendTitle(sender, null, title.replace("{receiver}", sender.getName()), subtitle.replace("{receiver}", sender.getName()), fadeIn, duration, fadeOut);
		}

		// Target everyone

		else if (target.equalsIgnoreCase("everyone"))
		{
			for (Player pl : UBukkit.getOnlinePlayers()) {
				Messenger.sendTitle(pl, null, title.replace("{receiver}", pl.getName()), subtitle.replace("{receiver}", pl.getName()), fadeIn, duration, fadeOut);
			}
		}

		// Target player in argument

		else
		{
			try
			{
				Player newTarget = Bukkit.getPlayer(target);
				Messenger.sendTitle(newTarget, null, title.replace("{receiver}", newTarget.getName()), subtitle.replace("{receiver}", newTarget.getName()), fadeIn, duration, fadeOut);
			}
			catch (Exception exception) {
				CustomCommands.i.config.getMessage("error_target").send(new Replacer("{player}", target), sender);
			}
		}
	}

	@Override
	public boolean isOver()
	{
		return true;
	}
}
