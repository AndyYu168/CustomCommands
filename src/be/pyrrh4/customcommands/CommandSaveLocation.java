package be.pyrrh4.customcommands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import be.pyrrh4.core.command.Argument;
import be.pyrrh4.core.command.CallInfo;
import be.pyrrh4.core.util.Requires;

public class CommandSaveLocation extends Argument
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public CommandSaveLocation(Argument parent, ArrayList<String> aliases, ArrayList<String> params, boolean playerOnly, boolean async, String permission, String description, ArrayList<String> paramsDescription) {
		super(parent, aliases, params, playerOnly, async, permission, description, paramsDescription);
	}

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

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
