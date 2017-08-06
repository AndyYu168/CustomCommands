package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import be.pyrrh4.core.Logger;
import be.pyrrh4.core.Logger.Level;
import be.pyrrh4.core.messenger.Messenger;
import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionTitle implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionTitle(Player sender, ArrayList<String> data, String[] args)
	{
		String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);
		String title = Utils.fillPAPI(sender, Utils.format(CustomCommands.instance().replaceString(data.get(1), sender, args))), subtitle = Utils.fillPAPI(sender, Utils.format(CustomCommands.instance().replaceString(data.get(2), sender, args)));

		int fadeIn, duration, fadeOut;

		try
		{
			fadeIn = Integer.parseInt(data.get(3));
			duration = Integer.parseInt(data.get(4));
			fadeOut = Integer.parseInt(data.get(5));
		}
		catch (Exception exception) {
			Logger.log(Level.WARNING, CustomCommands.instance(), "Could not find the fadeIn, duration or fadeOut.");
			return;
		}

		// target player
		if (target.equalsIgnoreCase("player")) {
			Messenger.title(sender, title.replace("$RECEIVER", sender.getName()), subtitle.replace("$RECEIVER", sender.getName()), fadeIn, duration, fadeOut);
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				Messenger.title(pl, title.replace("$RECEIVER", pl.getName()), subtitle.replace("$RECEIVER", pl.getName()), fadeIn, duration, fadeOut);
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				Messenger.title(newTarget, title.replace("$RECEIVER", newTarget.getName()), subtitle.replace("$RECEIVER", newTarget.getName()), fadeIn, duration, fadeOut);
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
