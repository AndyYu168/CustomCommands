package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.core.Logger;
import be.pyrrh4.core.Logger.Level;
import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.customcommands.MainData;

public class ActionItem implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionItem(Player sender, ArrayList<String> data, String[] args)
	{
		String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);
		String itemName = CustomCommands.instance().replaceString(data.get(1).replace(" ", ""), sender, args);
		MainData mainData = CustomCommands.instance().getMainData();
		ItemStack item = mainData.getItems().get(itemName);

		if (item == null) {
			Logger.log(Level.WARNING, CustomCommands.instance(), "Could not find the item.");
			return;
		}

		// rarget player
		if (target.equalsIgnoreCase("player")) {
			sender.getInventory().addItem(item);
			sender.updateInventory();
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				pl.getInventory().addItem(item);
				pl.updateInventory();
			}
		}
		// Target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				newTarget.getInventory().addItem(item);
				newTarget.updateInventory();
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
