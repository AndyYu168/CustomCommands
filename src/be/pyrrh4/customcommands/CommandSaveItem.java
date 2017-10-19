package be.pyrrh4.customcommands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.core.command.Arguments.Performer;
import be.pyrrh4.core.command.CallInfo;
import be.pyrrh4.core.util.Requires;

public class CommandSaveItem implements Performer
{
	@Override
	public void perform(CallInfo call)
	{
		Player player = call.getSenderAsPlayer();
		String name = call.getArgAsString(1);
		ItemStack item = player.getItemInHand();

		if (!Requires.isAlphanumeric(name, player, CustomCommands.instance().getLocale().getMessage("error_alphanumeric"))) return;
		if (!Requires.isValidItem(item, player, CustomCommands.instance().getLocale().getMessage("error_item_invalid"))) return;
		if (!Requires.isValidCondition(!CustomCommands.instance().getMainData().getItems().containsKey(name), player, CustomCommands.instance().getLocale().getMessage("error_name_taken"))) return;

		CustomCommands.instance().getMainData().getItems().put(name, item);
		CustomCommands.instance().getLocale().getMessage("item_saved").send(player, "$NAME", name);
	}
}
