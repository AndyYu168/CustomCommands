package be.pyrrh4.customcommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.core.AbstractPlugin;
import be.pyrrh4.core.Core;
import be.pyrrh4.core.Requires;
import be.pyrrh4.core.Setting;
import be.pyrrh4.core.command.CommandArgumentsPattern;
import be.pyrrh4.core.command.CommandCallInfo;
import be.pyrrh4.core.command.CommandHandler;
import be.pyrrh4.core.command.CommandSubHandler;
import be.pyrrh4.core.storage.PMLConvertor;
import be.pyrrh4.core.storage.PMLReader;
import be.pyrrh4.core.storage.PMLWriter;
import be.pyrrh4.core.util.UInventory;
import be.pyrrh4.core.util.ULocation;
import be.pyrrh4.customcommands.command.CustomCommand;
import be.pyrrh4.customcommands.command.action.ActionData;

public class CustomCommands extends AbstractPlugin implements Listener
{
	public static CustomCommands i;
	private CommandHandler handler;
	public PMLWriter dataFile;
	private ArrayList<CustomCommand> commands = new ArrayList<CustomCommand>();

	@Override
	public void initialize()
	{
		setSetting(Setting.AUTO_UPDATE_URL, "https://www.spigotmc.org/resources/14363/");
		setSetting(Setting.HAS_STORAGE, true);
		setSetting(Setting.CONFIG_FILE_NAME, "config.pyrml");
	}

	@Override
	public void enable()
	{
		i = this;
		config.loadTextPaths(this, "msg", null, null);

		// Converting data

		File f = new File(getStorage().getParentDirectory(), "config.yml");

		if (f.exists())
		{
			PMLConvertor convertor = new PMLConvertor(this, f);
			convertor.addPath("msg.error-permission");
			convertor.addPath("msg.error-number");
			convertor.addPath("msg.error-length");
			convertor.addPath("msg.error-target");
			convertor.addPath("msg.error-usage");
			convertor.convert();
		}

		f = new File(getStorage().getParentDirectory(), "commands.yml");

		if (f.exists())
		{
			PMLConvertor convertor = new PMLConvertor(this, f);
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
			ConfigurationSection sec = cfg.getConfigurationSection("");

			if (sec != null)
			{
				for (String key : sec.getKeys(false))
				{
					convertor.addPath(key + ".usage");
					convertor.addPath(key + ".aliases");
					convertor.addPath(key + ".arguments-patterns");
					convertor.addPath(key + ".permission");

					ConfigurationSection sec2 = cfg.getConfigurationSection(key + ".actions");

					if (sec2 != null)
					{
						for (String key2 : sec2.getKeys(false))
						{
							convertor.addPath(key + ".actions." + key2 + ".type");
							convertor.addPath(key + ".actions." + key2 + ".data");
						}
					}
				}
			}

			convertor.convert();
		}

		// Storage files

		dataFile = getStorage().getPMLWriter("storage.data");

		// Converting old data

		File oldFile = new File(getDataFolder().getParentFile() + File.separator + "CustomCommands", "database.yml");

		if (oldFile.exists() && !dataFile.reader().getOrDefault("converted", false))
		{
			log(Level.INFO, "Starting converting old data from /CustomCommands/database.yml to /pyrrh4_plugins/CustomCommands/storage.data ...");
			YamlConfiguration old = YamlConfiguration.loadConfiguration(oldFile);
			int items = 0;
			int locations = 0;

			if (old.contains("items"))
			{
				for (String name : old.getConfigurationSection("items").getKeys(false))
				{
					try
					{
						ItemStack item = (ItemStack) old.get("items." + name);

						if (item == null)
						{
							log(Level.WARNING, "Could not load item '" + name + "' from the old database file.");
							continue;
						}

						dataFile.set("items." + name, UInventory.serializeItem(item));
						log(Level.INFO, "Successfully loaded item '" + name + "' from the old database file.");
					}
					catch (Exception exception) {
						log(Level.WARNING, "Could not load item '" + name + "' from the old database file.");
					}
				}
			}

			if (old.contains("locations"))
			{
				for (String name : old.getConfigurationSection("locations").getKeys(false))
				{
					try
					{
						Location loc = (Location) old.get("locations." + name);

						if (loc == null)
						{
							log(Level.WARNING, "Could not load location '" + name + "' from the old database file.");
							continue;
						}

						dataFile.set("items." + name, ULocation.serializeLocation(loc));
						log(Level.INFO, "Successfully loaded location '" + name + "' from the old database file.");
					}
					catch (Exception exception) {
						log(Level.WARNING, "Could not load location '" + name + "' from the old database file.");
					}
				}
			}

			dataFile.set("converted", true).save();
			log(Level.INFO, "Successfully converted all items and locations from the old database file. " + items + " items" + (items > 1 ? "s" : "") + " and " + locations + " arena" + (locations > 1 ? "s" : "") + " were loaded.");
		}

		// Loading commands

		PMLReader file = getStorage().getPMLReader("commands.pyrml");

		for (String key : file.getKeysForSection("", false))
		{
			try
			{
				List<String> aliases = file.getOrDefaultList(key + ".aliases");
				List<CommandArgumentsPattern> patterns = new ArrayList<CommandArgumentsPattern>();

				for (String pattern : file.getOrDefaultList(key + ".arguments_patterns")) {
					patterns.add(new CommandArgumentsPattern(pattern));
				}

				String usage = file.getOrDefault(key + ".usage", "/" + aliases.get(0));
				String permission = file.getOrDefault(key + ".permission", null);
				List<ActionData> actions = new ArrayList<ActionData>();

				for (String path : file.getKeysForSection(key + ".actions", false)) {
					actions.add(new ActionData(file, key + ".actions." + path));
				}

				commands.add(new CustomCommand(usage, permission, aliases, patterns, actions));
				log(Level.INFO, "Successfully registered command '" + key + "'");
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
				log(Level.WARNING, "Could not load command '" + key + "'.");
			}
		}

		// Events

		Bukkit.getPluginManager().registerEvents(this, this);

		// Plugin's commands

		getCommand("customcommands").setExecutor(this);
		handler = new CommandHandler(this, "/ccmd", Core.getMessenger());

		handler.addHelp("/pyr reload CustomCommands", "reload the plugin", "pyr.core.admin");
		handler.addHelp("/ccmd saveitem [name]", "save an item", "ccmd.admin");
		handler.addHelp("/ccmd saveloc [name]", "save a location", "ccmd.admin");

		// Command /ccmd saveitem [name]

		handler.addSubCommand(new CommandSubHandler(true, true, "ccmd.admin", new CommandArgumentsPattern("saveitem [string]"))
		{
			@Override
			public void execute(CommandCallInfo call)
			{
				Player player = call.getSenderAsPlayer();
				String name = call.getArgAsString(1);
				ItemStack item = player.getItemInHand();

				if (!Requires.stringAlphanumeric(name, player, "CustomCommands >>", "This name isn't alphanumeric !")) return;
				if (!Requires.fileNotContains(dataFile, "items." + name, player, "CustomCommands >>", "This name is already taken !")) return;
				if (!Requires.itemValid(item, player, "CustomCommands >>", "This item is invalid !")) return;

				dataFile.set("items." + name, UInventory.serializeItem(item)).save();
				Core.getMessenger().normal(player, "CustomCommands >>", "This item has been saved with name '" + name + "' !");
			}
		});

		// Command /ccmd saveloc [name]

		handler.addSubCommand(new CommandSubHandler(true, true, "ccmd.admin", new CommandArgumentsPattern("saveloc [string]"))
		{
			@Override
			public void execute(CommandCallInfo call)
			{
				Player player = call.getSenderAsPlayer();
				String name = call.getArgAsString(1);

				if (!Requires.stringAlphanumeric(name, player, "CustomCommands >>", "This name isn't alphanumeric !")) return;
				if (!Requires.fileNotContains(dataFile, "locations." + name, player, "CustomCommands >>", "This name is already taken !")) return;

				dataFile.set("locations." + name, ULocation.serializeLocation(player.getLocation())).save();
				Core.getMessenger().normal(player, "CustomCommands >>", "This location has been saved with name '" + name + "' !");
			}
		});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 0) {
			handler.showHelp(sender);
		}
		else {
			handler.execute(sender, args);
		}

		return true;
	}

	@EventHandler
	public void event(PlayerCommandPreprocessEvent event)
	{
		for (CustomCommand ccmd : commands)
		{
			if (ccmd.execute(event.getPlayer(), event.getMessage().substring(1)))
			{
				event.setCancelled(true);
				return;
			}
		}
	}

	@Override
	public void disable() {}

	public static String replaceString(String string, Player sender, String[] args)
	{
		String fullArgs = "";

		for (int i = 0; i < args.length; i++)
		{
			fullArgs += args[i];

			if (i < args.length) {
				fullArgs += " ";
			}
		}

		for (int i = 0; i < args.length; i++) {
			string = string.replace("{arg:" + (i + 1) + "}", args[i]);
		}

		string = string.replace("{player}", sender.getName()).replace("{args}", fullArgs);
		return string;
	}

	@Override
	public String getAdditionalPasteContent()
	{
		return "\n" + "Custom commands (config) : " + getStorage().getPMLReader("commands.pyrml").getKeysForSection("", false).size() + "\n" + "Custom commands (registered) : " + commands.size();
	}
}
