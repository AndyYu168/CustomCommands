package be.pyrrh4.customcommands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import be.pyrrh4.core.Core;
import be.pyrrh4.core.Logger;
import be.pyrrh4.core.Logger.Level;
import be.pyrrh4.core.PyrPlugin;
import be.pyrrh4.core.User;
import be.pyrrh4.core.command.Argument;
import be.pyrrh4.core.command.CallInfo;
import be.pyrrh4.core.command.Command;
import be.pyrrh4.core.storage.PMLConfiguration;
import be.pyrrh4.core.util.Pair;
import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.command.CustomArgument;
import be.pyrrh4.customcommands.command.action.Action;
import be.pyrrh4.customcommands.command.action.Action.Type;
import be.pyrrh4.customcommands.command.action.ActionData;

public class CustomCommands extends PyrPlugin implements Listener
{
	// ------------------------------------------------------------
	// Instance and constructor
	// ------------------------------------------------------------

	private static CustomCommands instance;

	public CustomCommands() {
		instance = this;
	}

	public static CustomCommands instance() {
		return instance;
	}

	// ------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------

	private ArrayList<CustomArgument> commands = new ArrayList<CustomArgument>();
	private MainData mainData;

	public MainData getMainData() {
		return mainData;
	}

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	protected void init()
	{
		getSettings().autoUpdateUrl("https://www.spigotmc.org/resources/14363/");
		getSettings().localeDefault("customcommands_en_US.pyrml");
		getSettings().localeConfigName("locale");
	}

	@Override
	protected void initPluginData() {
		// Main data
		mainData = Utils.getPluginData(Core.getDataStorage().getFile("customcommands.data"), new MainData());
	}

	@Override
	protected void savePluginData() {
		mainData.save();
	}

	@Override
	public void initUserPluginData(User user) {
		// Player data
		if (!user.hasPluginData("customcommands")) {
			user.setPluginData("customcommands", new PlayerData());
		}
	}

	// ------------------------------------------------------------
	// Override : enable
	// ------------------------------------------------------------

	@Override
	protected void enable()
	{
		// load custom commands
		PMLConfiguration commands = Core.getRootStorage().getConfig(this, "customcommands_commands.pyrml", false);

		for (String path : commands.getKeysForSection("", false)) {
			try {
				loadChild(commands, path, null);
				Logger.log(Level.SUCCESS, this, "Successfully loaded custom command " + path);
			} catch (Exception exception) {
				exception.printStackTrace();
				Logger.log(Level.SEVERE, this, "Could not load custom command " + path);
			}
		}

		// events
		Bukkit.getPluginManager().registerEvents(this, this);

		// plugin commands
		Command command = new Command(this, Utils.asList("customcommands", "ccmd"), Utils.emptyList(), "customcommands", true, false, "customcommands.admin", "CustomCommands main command", Utils.emptyList());
		Argument argSaveItem = new CommandSaveItem(command, Utils.asList("saveitem", "setitem"), Utils.asList(Argument.STRING), true, false, "customcommands.admin", "save an item", Utils.asList("[item id]"));
		Argument argSaveLocation = new CommandSaveLocation(command, Utils.asList("savelocation", "saveloc", "setlocation", "setloc"), Utils.asList(Argument.STRING), true, false, "customcommands.admin", "save a location", Utils.asList("[location id]"));

		// set children
		command.addChild(argSaveItem);
		command.addChild(argSaveLocation);
	}

	// ------------------------------------------------------------
	// Override : disable
	// ------------------------------------------------------------

	@Override
	protected void disable() {}

	// ------------------------------------------------------------
	// Events
	// ------------------------------------------------------------

	@EventHandler
	public void event(PlayerCommandPreprocessEvent event)
	{
		Pair<String, String[]> separate = Utils.separateRoot(event.getMessage().substring(1), false);
		String root = separate.getA();
		String[] args = separate.getB();

		for (CustomArgument command : commands)
		{
			for (String alias : command.getAliases())
			{
				if (alias.equalsIgnoreCase(root))
				{
					event.setCancelled(true);
					command.call(event.getPlayer(), args, -1);

					if (command.getCallParamsError() != null) {
						command.getCallParamsError().showHelp(new CallInfo(event.getPlayer(), args));
						command.clearCallParamsError();
					}

					break;
				}
<<<<<<< HEAD
=======

				commands.add(new CustomCommand(usage, permission, aliases, patterns, actions));
				log(Level.INFO, "Successfully registered custom command '" + key + "'");
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
				log(Level.WARNING, "Could not load custom command '" + key + "'.");
<<<<<<< HEAD
>>>>>>> 9f485b6ff15b0c866706f84792d583c3b92a747d
=======
>>>>>>> 9f485b6ff15b0c866706f84792d583c3b92a747d
			}
		}
	}

<<<<<<< HEAD
	// ------------------------------------------------------------
	// Static methods
	// ------------------------------------------------------------

	public String replaceString(String string, Player sender, String[] args)
	{
		String fullArgs = "";
=======
		// Events

		Bukkit.getPluginManager().registerEvents(this, this);

		// Plugin's commands

		getCommand("customcommands").setExecutor(this);
		handler = new CommandHandler(this, "/ccmd", Core.getMessenger());

		handler.addHelp("/pyr reload CustomCommands", "reload the plugin", "pyr.core.admin");
		handler.addHelp("/customcommands saveitem [name]", "save an item", "customcommands.admin");
		handler.addHelp("/customcommands saveloc [name]", "save a location", "customcommands.admin");

		// Command /ccmd saveitem [name]

		handler.addSubCommand(new CommandSubHandler(true, true, "customcommands.admin", new CommandArgumentsPattern("saveitem [string]"))
		{
			@Override
			public void execute(CommandCallInfo call)
			{
				Player player = call.getSenderAsPlayer();
				String name = call.getArgAsString(1);
				ItemStack item = player.getItemInHand();

				if (!Requires.stringAlphanumeric(name, player, "CustomCommands >>", "This name isn't alphanumeric!")) return;
				if (!Requires.fileNotContains(dataFile, "items." + name, player, "CustomCommands >>", "This name is already taken!")) return;
				if (!Requires.itemValid(item, player, "CustomCommands >>", "This item is invalid!")) return;
<<<<<<< HEAD
>>>>>>> 9f485b6ff15b0c866706f84792d583c3b92a747d
=======
>>>>>>> 9f485b6ff15b0c866706f84792d583c3b92a747d

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				fullArgs += args[i] += " ";
			}
			fullArgs = fullArgs.substring(0, fullArgs.length() - 1);
		}

		for (int i = 0; i < args.length; i++) {
			string = string.replace("$ARG:" + (i + 1), args[i].replace(" ", ""));
		}

<<<<<<< HEAD
<<<<<<< HEAD
		string = string.replace("$PLAYER", sender.getName()).replace("$ARGS", fullArgs);
		return string;
	}
=======
=======
>>>>>>> 9f485b6ff15b0c866706f84792d583c3b92a747d
		handler.addSubCommand(new CommandSubHandler(true, true, "customcommands.admin", new CommandArgumentsPattern("saveloc [string]"))
		{
			@Override
			public void execute(CommandCallInfo call)
			{
				Player player = call.getSenderAsPlayer();
				String name = call.getArgAsString(1);
>>>>>>> 9f485b6ff15b0c866706f84792d583c3b92a747d

	public String getFullId(CustomArgument argument)
	{
		String id = "";
		ArrayList<CustomArgument> args = Utils.asListMultiple(argument.getParents(), argument);

		for (CustomArgument arg : args) {
			id += arg.getId() + ".";
		}

		id = id.substring(0, id.length() - 1);
		return id;
	}

	public Action createAction(CustomArgument arg, String type, Player sender, ArrayList<String> data, String[] args)
	{
		Action action = null;
		Type actionType = Type.fromType(type);

		if (actionType == null) {
			Logger.log(Logger.Level.WARNING, instance, "Unknown action type " + type + " in argument " + getFullId(arg) + ", skipping");
		} else {
			action = actionType.create(sender, data, args);
		}

		if (action == null) {
			Logger.log(Logger.Level.WARNING, instance, "Could not create action of type " + actionType + " in argument " + getFullId(arg) + ", skipping");
		}

		return action;
	}

	private void loadChild(PMLConfiguration config, String path, CustomArgument parent)
	{
		// settings
		ArrayList<String> aliases = config.getList(path + ".aliases");
		String description = config.getString(path + ".description");
		String permission = config.contains(path + ".permission") ? config.getString(path + ".permission") : null;
		ArrayList<String> parameters = config.contains(path + ".parameters") ? config.getList(path + ".parameters") : Utils.emptyList();
		ArrayList<String> parametersDescription = config.contains(path + ".parameters_description") ? config.getList(path + ".parameters_description") : Utils.emptyList();
		boolean toggle = config.contains(path + ".toggle") ? config.getBoolean(path + ".toggle") : false;
		boolean togglePersistent = config.contains(path + ".toggle_persistent") ? config.getBoolean(path + ".toggle_persistent") : false;

		// perform
		ArrayList<ActionData> performToggleFalse = new ArrayList<ActionData>(), performToggleTrue = new ArrayList<ActionData>();

		if (config.contains(path + ".perform_toggle_false")) {
			for (String actionPath : config.getKeysForSection(path + ".perform_toggle_false", false)) {
				performToggleFalse.add(new ActionData(config, path + ".perform_toggle_false." + actionPath));
			}
		} else if (config.contains(path + ".perform")) {
			for (String actionPath : config.getKeysForSection(path + ".perform", false)) {
				performToggleFalse.add(new ActionData(config, path + ".perform." + actionPath));
			}
		}

		if (config.contains(path + ".perform_toggle_true")) {
			for (String actionPath : config.getKeysForSection(path + ".perform_toggle_true", false)) {
				performToggleTrue.add(new ActionData(config, path + ".perform_toggle_true." + actionPath));
			}
		}

		// build and register current argument
		CustomArgument current = new CustomArgument(path, toggle, togglePersistent, performToggleFalse, performToggleTrue, parent, aliases, parameters, true, false, permission, description, parametersDescription);

		if (parent == null) {
			commands.add(current);
		}

		// children
		if (config.contains(path + ".children")) {
			for (String childPath : config.getKeysForSection(path + ".children", false)) {
				loadChild(config, path + ".children." + childPath, current);
			}
		}
	}
}
