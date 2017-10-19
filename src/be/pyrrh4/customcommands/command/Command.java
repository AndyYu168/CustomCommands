package be.pyrrh4.customcommands.command;

import java.util.ArrayList;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import be.pyrrh4.core.Core;
import be.pyrrh4.core.PyrPlugin;
import be.pyrrh4.core.command.CallInfo;
import be.pyrrh4.core.messenger.Messenger;

public class Command extends Argument implements CommandExecutor
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private PyrPlugin plugin;
	private String commandName;

	public Command(PyrPlugin plugin, ArrayList<String> aliases, ArrayList<String> params, String commandName, boolean playerOnly, boolean async, String permission, String description, ArrayList<String> paramsUsage) {
		super(null, aliases, params, playerOnly, async, permission, description, paramsUsage);
		this.plugin = plugin;
		this.commandName = commandName;
		plugin.getCommand(commandName).setExecutor(this);
	}

	public PyrPlugin getPlugin() {
		return plugin;
	}

	public String getCommandName() {
		return commandName;
	}

	// ------------------------------------------------------------
	// Override : on command
	// ------------------------------------------------------------

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		if (!Core.instance().usersLoaded()) {
			Messenger.send(sender, Messenger.Level.SEVERE_INFO, plugin.getName(), "Please wait while the plugin is initializing.");
			return true;
		}

		call(sender, args, -1);

		if (getCallParamsError() != null) {
			getCallParamsError().showHelp(new CallInfo(sender, args));
			clearCallParamsError();
		}

		return true;
	}
}
