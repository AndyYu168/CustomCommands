package be.pyrrh4.customcommands.command;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.core.lib.command.CommandArgumentsPattern;
import be.pyrrh4.core.lib.command.result.PatternResult;
import be.pyrrh4.core.lib.command.result.ResultIncorrectNumber;
import be.pyrrh4.core.lib.command.result.ResultIncorrectOther;
import be.pyrrh4.core.lib.command.result.ResultSuccess;
import be.pyrrh4.core.lib.messenger.Replacer;
import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.customcommands.command.action.Action;
import be.pyrrh4.customcommands.command.action.ActionBar;
import be.pyrrh4.customcommands.command.action.ActionCommandsAs;
import be.pyrrh4.customcommands.command.action.ActionCommandsFor;
import be.pyrrh4.customcommands.command.action.ActionData;
import be.pyrrh4.customcommands.command.action.ActionItem;
import be.pyrrh4.customcommands.command.action.ActionMessage;
import be.pyrrh4.customcommands.command.action.ActionTeleport;
import be.pyrrh4.customcommands.command.action.ActionTitle;
import be.pyrrh4.customcommands.command.action.ActionWait;

public class CustomCommand
{
	private final String permission;
	private final String usage;
	private final List<String> aliases;
	private final List<CommandArgumentsPattern> patterns;
	private final List<ActionData> actions;

	public CustomCommand(String usage, String permission, List<String> aliases, List<CommandArgumentsPattern> patterns, List<ActionData> actions)
	{
		this.usage = usage;
		this.permission = permission;
		this.aliases = aliases;
		this.patterns = patterns;
		this.actions = actions;
	}

	public boolean execute(final Player sender, String fullCommand)
	{
		// Command match

		String[] allArgs = fullCommand.split(" ");

		if (allArgs.length == 0) {
			return false;
		}

		String alias = allArgs[0];
		boolean aliasMatch = false;

		for (String al : aliases)
		{
			if (alias.equalsIgnoreCase(al))
			{
				aliasMatch = true;
				break;
			}
		}

		if (!aliasMatch) {
			return false;
		}

		// Getting the final args

		final String[] args = new String[allArgs.length - 1];

		for (int i = 0; i < args.length; i++) {
			args[i] = allArgs[i + 1];
		}

		// Arguments match

		PatternResult result = argumentsMatch(args);

		if (result instanceof ResultSuccess)
		{
			if (!hasPermission(sender))
			{
				CustomCommands.i.getMessage("error-permission").send(null, sender);
				return false;
			}

			// Execute actions

			new BukkitRunnable()
			{
				private int index = -1;
				private Action current = null;

				@Override
				public void run()
				{
					if (current == null)
					{
						if (++index < actions.size())
						{
							ActionData data = actions.get(index);

							if (data.getType().equalsIgnoreCase("send message")) {
								current = new ActionMessage(sender, data.getData(), args);
							}
							else if (data.getType().equalsIgnoreCase("wait x ticks")) {
								current = new ActionWait(sender, data.getData(), args);
							}
							else if (data.getType().equalsIgnoreCase("execute commands for")) {
								current = new ActionCommandsFor(sender, data.getData(), args);
							}
							else if (data.getType().equalsIgnoreCase("execute commands as")) {
								current = new ActionCommandsAs(sender, data.getData(), args);
							}
							else if (data.getType().equalsIgnoreCase("send title")) {
								current = new ActionTitle(sender, data.getData(), args);
							}
							else if (data.getType().equalsIgnoreCase("send actionbar")) {
								current = new ActionBar(sender, data.getData(), args);
							}
							else if (data.getType().equalsIgnoreCase("give item")) {
								current = new ActionItem(sender, data.getData(), args);
							}
							else if (data.getType().equalsIgnoreCase("teleport")) {
								current = new ActionTeleport(sender, data.getData(), args);
							}

							if (current == null) {
								throw new NullPointerException("Could not find action '" + data.getType() + "'");
							}

							// TODO : action change tab + other actions
						}
						else
						{
							cancel();
							return;
						}
					}

					if (current.isOver()) {
						current = null;
					}
				}
			}.runTaskTimerAsynchronously(CustomCommands.i, 0L, 1L);

			return true;
		}
		else if (result instanceof ResultIncorrectNumber)
		{
			CustomCommands.i.getMessage("error-number").send(null, sender);
			return true;
		}
		else if (result instanceof ResultIncorrectOther)
		{
			CustomCommands.i.getMessage("error-usage").send(new Replacer("{usage}", usage), sender);
			return true;
		}

		return false;
	}

	public boolean hasPermission(Player player)
	{
		if (permission == null) {
			return true;
		}

		return player.isOp() || player.hasPermission(permission);
	}

	public PatternResult argumentsMatch(String[] args)
	{
		if (patterns.size() == 0 && args.length == 0)
			return new ResultSuccess();

		for (CommandArgumentsPattern pattern : patterns)
		{
			PatternResult result = pattern.match(args);

			if (result instanceof ResultSuccess) return result;
			if (result instanceof ResultIncorrectOther) continue;

			return result;
		}

		return new ResultIncorrectOther(-1);
	}
}
