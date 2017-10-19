package be.pyrrh4.customcommands.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.pyrrh4.core.Core;
import be.pyrrh4.core.command.CallInfo;
import be.pyrrh4.core.util.Handler;
import be.pyrrh4.core.util.Utils;

public class Argument
{
	// ------------------------------------------------------------
	// Type
	// ------------------------------------------------------------

	public static final String STRING = "[string]";
	public static final String STRING_SIZED_START = "[string:";
	public static final String STRING_SIZED_END = "]";
	public static final String INTEGER = "[integer]";
	public static final String DOUBLE = "[double]";
	public static final String OFFLINE_PLAYER = "[player]";
	public static final String ONLINE_PLAYER = "[player-online]";

	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private Argument parent;
	private ArrayList<String> aliases, params, paramsDescription;
	private boolean playerOnly, async;
	private String permission, description;
	private ArrayList<Argument> children = new ArrayList<Argument>();
	protected Argument callParamsError = null;

	public Argument(Argument parent, ArrayList<String> aliases, ArrayList<String> params, boolean playerOnly, boolean async, String permission, String description, ArrayList<String> paramsDescription) {
		this.parent = parent;
		this.aliases = aliases;
		this.params = params;
		this.playerOnly = playerOnly;
		this.async = async;
		this.permission = permission;
		this.description = description;
		this.paramsDescription = paramsDescription;

		if (parent != null) {
			parent.addChild(this);
		}
	}

	public Argument getParent() {
		return parent;
	}

	public ArrayList<String> getAliases() {
		return aliases;
	}

	public ArrayList<String> getParams() {
		return params;
	}

	public boolean isPlayerOnly() {
		return playerOnly;
	}

	public boolean isAsync() {
		return async;
	}

	public String getPermission() {
		return permission;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<String> getParamsDescription() {
		return paramsDescription;
	}

	public ArrayList<Argument> getDirectChildren() {
		return children;
	}

	public void addChild(Argument arg) {
		children.add(arg);
	}

	public Argument getCallParamsError() {
		return callParamsError;
	}

	public void clearCallParamsError() {
		this.callParamsError = null;
	}

	// ------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------

	public boolean isRoot() {
		return getParent() == null;
	}

	public ArrayList<Argument> getParents()
	{
		ArrayList<Argument> parents = new ArrayList<Argument>();
		Argument current = parent;

		while (current != null) {
			parents.add(current);
			current = current.getParent();
		}

		return Utils.asReverseList(parents);
	}

	public Argument getRoot()
	{
		ArrayList<Argument> parents = getParents();
		if (parents.size() == 0) return this;
		return parents.get(0);
	}

	private MatchResult getMatchParams(String[] arguments, int index)
	{
		MatchResult match = params.size() == 0 ? MatchResult.SUCCESS : MatchResult.PARAM_ERROR_MATCH;

		for (int paramIndex = 0; paramIndex < params.size(); paramIndex++)
		{
			int argIndex = index + 1 + paramIndex;

			if (argIndex >= arguments.length) {
				match = MatchResult.PARAM_ERROR_MATCH;
				break;
			}

			String arg = arguments[argIndex], param = params.get(paramIndex);

			if (param.equals(INTEGER))
			{
				if (Utils.isInteger(arg)) {
					match = MatchResult.SUCCESS;
				} else {
					match = MatchResult.PARAM_ERROR_NUMBER;
				}
			}
			else if (param.equals(DOUBLE))
			{
				if (Utils.isDouble(arg)) {
					match = MatchResult.SUCCESS;
				} else {
					match = MatchResult.PARAM_ERROR_NUMBER;
					break;
				}
			}
			else if (param.equalsIgnoreCase(OFFLINE_PLAYER))
			{
				if (Utils.getOfflinePlayer(arg) != null) {
					match = MatchResult.SUCCESS;
				} else {
					match = MatchResult.PARAM_ERROR_PLAYER;
					break;
				}
			}
			else if (param.equalsIgnoreCase(ONLINE_PLAYER))
			{
				if (Utils.getPlayer(arg) != null) {
					match = MatchResult.SUCCESS;
				} else {
					match = MatchResult.PARAM_ERROR_PLAYER_OFFLINE;
					break;
				}
			}
			else if (param.toLowerCase().startsWith(STRING_SIZED_START))
			{
				try {
					ArrayList<String> raw = Utils.split(":", param.toLowerCase().replace(STRING_SIZED_START, "").replace(STRING_SIZED_END, ""), false);
					int min = Integer.parseInt(raw.get(0)), max = Integer.parseInt(raw.get(1)), length = arg.length();

					if (length >= min || length <= max) {
						match = MatchResult.SUCCESS;
					} else {
						match = MatchResult.PARAM_ERROR_STRING_SIZED;
						break;
					}
				} catch (Exception ignored) {
					match = MatchResult.PARAM_ERROR_STRING_SIZED;
					break;
				}
			}
			else if (param.equalsIgnoreCase(STRING))
			{
				match = MatchResult.SUCCESS;
			}
			else
			{
				if (param.equalsIgnoreCase(arg)) {
					match = MatchResult.SUCCESS;
				} else {
					match = MatchResult.PARAM_ERROR_MATCH;
					break;
				}
			}
		}

		return match;
	}

	private MatchResult getMatchChildrens(CallInfo call, int index)
	{
		// check childrens
		for (Argument child : children)
		{
			MatchResult result = child.call(call, index);

			if (!result.equals(MatchResult.NO_MATCH)) {
				return result;
			}
		}

		// no match
		return MatchResult.NO_MATCH;
	}

	public MatchResult call(CommandSender sender, String[] arguments, int index) {
		return call(new CallInfo(sender, arguments), index);
	}

	public MatchResult call(CallInfo call, int index)
	{
		// check aliases if not root
		if (!isRoot())
		{
			boolean aliasMatch = false;

			for (String alias : aliases) {
				if (index < call.getArgs().length && call.getArgs()[index].equalsIgnoreCase(alias)) {
					aliasMatch = true;
					break;
				} else {
				}
			}

			// no matching alias
			if (!aliasMatch) {
				return MatchResult.NO_MATCH;
			}
		}

		// check params
		MatchResult matchParams = getMatchParams(call.getArgs(), index);

		// no matching params
		if (matchParams.equals(MatchResult.PARAM_ERROR_MATCH))
		{
			getRoot().callParamsError = this;
			return MatchResult.NO_MATCH;
		}
		else if (matchParams.equals(MatchResult.PARAM_ERROR_NUMBER)) {
			Core.instance().getLocale().getMessage("error_invalid_number").send(call.getSender());
			return MatchResult.SUCCESS;
		} else if (matchParams.equals(MatchResult.PARAM_ERROR_PLAYER)) {
			Core.instance().getLocale().getMessage("error_invalid_player").send(call.getSender());
			return MatchResult.SUCCESS;
		} else if (matchParams.equals(MatchResult.PARAM_ERROR_PLAYER_OFFLINE)) {
			Core.instance().getLocale().getMessage("error_offline_player").send(call.getSender());
			return MatchResult.SUCCESS;
		} else if (matchParams.equals(MatchResult.PARAM_ERROR_STRING_SIZED)) {
			Core.instance().getLocale().getMessage("error_arg_size").send(call.getSender());
			return MatchResult.SUCCESS;
		}
		// perform because max index reached
		else if (index + params.size() >= call.getArgs().length - 1)
		{
			performInner(call);
			return MatchResult.SUCCESS;
		}
		// check childrens
		else
		{
			MatchResult matchChildrens = getMatchChildrens(call, index + 1 + params.size());

			if (matchChildrens.equals(MatchResult.NO_MATCH))
			{
				getRoot().clearCallParamsError();
				Core.instance().getLocale().getMessage("error_command_unknown").send(call.getSender());
				showHelp(call);
				return MatchResult.SUCCESS;
			}
			else if (matchChildrens.equals(MatchResult.PARAM_ERROR_NUMBER)) {
				Core.instance().getLocale().getMessage("error_invalid_number").send(call.getSender());
			} else if (matchChildrens.equals(MatchResult.PARAM_ERROR_PLAYER)) {
				Core.instance().getLocale().getMessage("error_invalid_player").send(call.getSender());
			} else if (matchChildrens.equals(MatchResult.SUCCESS)) {
				return MatchResult.SUCCESS;
			}

			return MatchResult.SUCCESS;
		}
	}

	protected void performInner(final CallInfo call)
	{
		getRoot().clearCallParamsError();

		if (playerOnly && !(call.getSender() instanceof Player)) {
			Core.instance().getLocale().getMessage("error_in_game").send(call.getSender());
			return;
		}

		if (permission != null && !call.getSender().hasPermission(permission)) {
			Core.instance().getLocale().getMessage("error_no_permission").send(call.getSender());
			return;
		}

		if (async) {
			new Handler() {
				@Override
				public void execute() {
					perform(call);
				}
			}.runAsync();
		} else {
			perform(call);
		}
	}

	public void perform(CallInfo call)
	{
		// Show help by default
		getRoot().clearCallParamsError();
		showHelp(call);
	}

	// TODO : show other params help as well (for ex. /quests start [param] : doesn't show admin command)
	public void showHelp(CallInfo call)
	{
		Argument root = getRoot();
		Core.instance().getLocale().getMessage("command_help_header").send(call.getSender(), "$PLUGIN", root instanceof Command ? ((Command) root).getPlugin().getName() : Utils.capitalizeFirstLetter(root.aliases.get(0)), "$DESCRIPTION", Utils.capitalizeFirstLetter(description));

		if (children.size() == 0) {
			call.getSender().sendMessage(getCommandString(call) + " §f: " + getDescription());
		} else {
			for (Argument child : children) {
				if (!(child instanceof ConsoleArgument)) {
					call.getSender().sendMessage(child.getCommandString(call) + " §f: " + child.getDescription());
				}
			}
		}
	}

	// TODO : for 1.8 and above, use SpecialMessage, to hover the aliases or the parameters types
	public String getCommandString(CallInfo call)
	{
		String command = "§a/";
		ArrayList<Argument> list = Utils.asListMultiple(getParents(), this);

		for (Argument arg : list) {
			if (arg.getPermission() == null || (call.getSender().isOp() || call.getSender().hasPermission(arg.getPermission()))) {
				command += arg.getCommandStringPart(call) + " ";
			}
		}

		if (command.endsWith(" ")) {
			command = command.substring(0, command.length() - 1);
		}

		return command;
	}

	private String getCommandStringPart(CallInfo call)
	{
		String part = aliases.get(0);

		if (params.size() > 0)
		{
			ArrayList<Argument> list = Utils.asListMultiple(getParents(), this);
			int allArgsSize = 0;

			for (Argument parent : list) {
				allArgsSize += 1 + parent.getParams().size();
			}

			int argIndexWhereParamsShouldStart = allArgsSize - params.size() - 1;
			ArrayList<String> args = Utils.asList(call.getArgs());

			for (int i = 0; i < params.size(); i++)
			{
				int argIndex = argIndexWhereParamsShouldStart + i;

				if (argIndex >= args.size()) {
					/*if (i >= paramsDescription.size()) {
						part += " " + params.get(i);
					} else {*/
						part += " " + paramsDescription.get(i);
					//}
				} else {
					part += " " + args.get(argIndex);
				}
			}
		}

		return part;
	}
}
