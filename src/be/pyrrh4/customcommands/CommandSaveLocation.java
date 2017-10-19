package be.pyrrh4.customcommands;

import org.bukkit.entity.Player;

import be.pyrrh4.core.command.Arguments.Performer;
import be.pyrrh4.core.command.CallInfo;
import be.pyrrh4.core.util.Requires;

public class CommandSaveLocation implements Performer
{
	@Override
	public void perform(CallInfo call)
	{
		Player player = call.getSenderAsPlayer();
		String name = call.getArgAsString(1);

		if (!Requires.isAlphanumeric(name, player, CustomCommands.instance().getLocale().getMessage("error_alphanumeric"))) return;
		if (!Requires.isValidCondition(!CustomCommands.instance().getMainData().getItems().containsKey(name), player, CustomCommands.instance().getLocale().getMessage("error_name_taken"))) return;

		CustomCommands.instance().getMainData().getLocations().put(name, player.getLocation());
		CustomCommands.instance().getLocale().getMessage("location_saved").send(player, "$NAME", name);
	}
}
