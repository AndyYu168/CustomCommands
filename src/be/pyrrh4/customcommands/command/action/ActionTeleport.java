package be.pyrrh4.customcommands.command.action;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.pyrrh4.core.messenger.Replacer;
import be.pyrrh4.core.util.UBukkit;
import be.pyrrh4.core.util.ULocation;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionTeleport implements Action
{
	public ActionTeleport(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.i.replaceString(data.get(0).replace(" ", ""), sender, args);
		String locName = CustomCommands.i.replaceString(data.get(1).replace(" ", ""), sender, args);
		Location loc;

		try
		{
			loc = ULocation.unserializeLocation(CustomCommands.i.dataFile.getOrDefault("locations." + locName, (String) null));
		}
		catch (Exception exception)
		{
			CustomCommands.i.log(Level.WARNING, "Could not find the location.");
			return;
		}

		if (loc == null)
		{
			CustomCommands.i.log(Level.WARNING, "Could not find the location.");
			return;
		}

		// Target player

		if (target.equalsIgnoreCase("player")) {
			sender.teleport(loc);
		}

		// Target everyone

		else if (target.equalsIgnoreCase("everyone"))
		{
			for (Player pl : UBukkit.getOnlinePlayers()) {
				pl.teleport(loc);
			}
		}

		// Target player in argument

		else
		{
			try
			{
				Player newTarget = Bukkit.getPlayer(target);
				newTarget.teleport(loc);
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
