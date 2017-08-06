package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionMessage implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionMessage(Player sender, ArrayList<String> data, String[] args)
	{
		String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);
		ArrayList<String> message = new ArrayList<String>();

		for (int i = 1; i < data.size(); i++)
		{
			String str = Utils.format(CustomCommands.instance().replaceString(data.get(i), sender, args));
			message.add(Utils.format(Utils.fillPAPI(sender, str)));
		}

		// target player
		if (target.equalsIgnoreCase("player")) {
			for (String str : message) {
				sender.sendMessage(str.replace("$RECEIVER", sender.getName()));
			}
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				for (String str : message) {
					pl.sendMessage(str.replace("$RECEIVER", pl.getName()));
				}
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				for (String str : message) {
					newTarget.sendMessage(str.replace("$RECEIVER", newTarget.getName()));
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				CustomCommands.instance().getLocale().getMessage("error_target").send(sender, "$PLAYER", target);
			}
		}
	}

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return true;
	}
}
