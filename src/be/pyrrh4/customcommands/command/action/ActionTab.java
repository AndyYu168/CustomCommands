package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import be.pyrrh4.core.messenger.Messenger;
import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionTab implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionTab(Player sender, ArrayList<String> data, String[] args)
	{
		String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);
		String header = Utils.fillPAPI(sender, Utils.format(CustomCommands.instance().replaceString(data.get(1), sender, args))), footer = Utils.fillPAPI(sender, Utils.format(CustomCommands.instance().replaceString(data.get(2), sender, args)));

		// target player
		if (target.equalsIgnoreCase("player")) {
			Messenger.tab(sender, header.replace("$RECEIVER", sender.getName()), footer.replace("$RECEIVER", sender.getName()));
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				Messenger.tab(pl, header.replace("$RECEIVER", pl.getName()), footer.replace("$RECEIVER", pl.getName()));
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				Messenger.tab(newTarget, header.replace("$RECEIVER", newTarget.getName()), footer.replace("$RECEIVER", newTarget.getName()));
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
