package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.pyrrh4.core.Logger;
import be.pyrrh4.core.Logger.Level;
import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.customcommands.MainData;

public class ActionTeleport implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionTeleport(Player sender, ArrayList<String> data, String[] args)
	{
		String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);
		String locName = CustomCommands.instance().replaceString(data.get(1).replace(" ", ""), sender, args);
		MainData mainData = CustomCommands.instance().getMainData();
		Location loc = mainData.getLocations().get(locName);

		if (loc == null)
		{
			Logger.log(Level.WARNING, CustomCommands.instance(), "Could not find the location.");
			return;
		}

		// target player
		if (target.equalsIgnoreCase("player")) {
			sender.teleport(loc);
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				pl.teleport(loc);
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				newTarget.teleport(loc);
			} catch (Exception exception) {
				CustomCommands.instance().getLocale().getMessage("error_target").send(sender, "$PLAYER", target);
			}
		}
	}

	// ------------------------------------------------------------
	// Override : is over
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return true;
	}
}
