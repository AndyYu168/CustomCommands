package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import be.pyrrh4.core.messenger.Messenger;
import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionActionbar implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionActionbar(Player sender, ArrayList<String> data, String[] args)
	{
		String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);
		String bar = Utils.format(CustomCommands.instance().replaceString(data.get(1), sender, args));
		bar = Utils.fillPAPI(sender, bar);

		// target player
		if (target.equalsIgnoreCase("player")) {
			Messenger.actionBar(sender, bar.replace("$RECEIVER", sender.getName()));
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				Messenger.actionBar(sender, bar.replace("$RECEIVER", pl.getName()));
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				Messenger.actionBar(sender, bar.replace("$RECEIVER", newTarget.getName()));
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
