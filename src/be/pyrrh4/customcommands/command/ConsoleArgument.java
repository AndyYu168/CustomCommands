package be.pyrrh4.customcommands.command;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import be.pyrrh4.core.Core;
import be.pyrrh4.core.command.CallInfo;

public class ConsoleArgument extends Argument
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ConsoleArgument(Argument parent, ArrayList<String> aliases, ArrayList<String> params, boolean async) {
		super(parent, aliases, params, false, async, null, null, null);
	}

	// ------------------------------------------------------------
	// Override : help
	// ------------------------------------------------------------

	@Override
	public void showHelp(CallInfo call) {}

	// ------------------------------------------------------------
	// Override : performInner
	// ------------------------------------------------------------

	@Override
	protected void performInner(CallInfo call) {
		if (!(call.getSender() instanceof Player)) {
			Core.instance().getLocale().getMessage("error_no_permission").send(call.getSender());
		} else {
			super.performInner(call);
		}
	}
}
