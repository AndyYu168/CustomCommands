package be.pyrrh4.customcommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.core.Core;
import be.pyrrh4.core.PyrPlugin;
import be.pyrrh4.core.lib.command.CommandArgumentsPattern;
import be.pyrrh4.core.lib.command.CommandCallInfo;
import be.pyrrh4.core.lib.command.CommandHandler;
import be.pyrrh4.core.lib.command.CommandSubHandler;
import be.pyrrh4.core.lib.storage.ConfigFile;
import be.pyrrh4.core.util.UInventory;
import be.pyrrh4.core.util.ULocation;
import be.pyrrh4.core.util.UString;
import be.pyrrh4.customcommands.command.CustomCommand;
import be.pyrrh4.customcommands.command.action.ActionData;

public class CustomCommands extends PyrPlugin implements Listener
{
	public static CustomCommands i;
	private CommandHandler handler;
	public ConfigFile dataFile;
	private ArrayList<CustomCommand> commands = new ArrayList<CustomCommand>();

	public CustomCommands()
	{
		super(true, "config.yml", "msg", null, null, "https://www.spigotmc.org/resources/14363/", false);
	}

	@Override
	public void enable()
	{
		i = this;
		dataFile = getStorage().getConfig("storage.data");

		// Converting old data

		File oldFile = new File(getDataFolder().getParentFile() + File.separator + "CustomCommands", "database.yml");

		if (oldFile.exists() && !dataFile.getOrDefault("converted", false))
		{
			Bukkit.getLogger().info("[CustomCommands] Starting converting old data from /CustomCommands/database.yml to /pyrrh4_plugins/CustomCommands/storage.data ...");
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
							Bukkit.getLogger().warning("[CustomCommands] Could not load item '" + name + "' from the old database file.");
							continue;
						}

						dataFile.set("items." + name, UInventory.serializeItem(item));
						Bukkit.getLogger().info("[ScrollBoard] Successfully loaded item '" + name + "' from the old database file.");
					}
					catch (Exception exception) {
						Bukkit.getLogger().warning("[CustomCommands] Could not load item '" + name + "' from the old database file.");
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
							Bukkit.getLogger().warning("[CustomCommands] Could not load location '" + name + "' from the old database file.");
							continue;
						}

						dataFile.set("items." + name, ULocation.serializeLocation(loc));
						Bukkit.getLogger().info("[ScrollBoard] Successfully loaded location '" + name + "' from the old database file.");
					}
					catch (Exception exception) {
						Bukkit.getLogger().warning("[CustomCommands] Could not load location '" + name + "' from the old database file.");
					}
				}
			}

			dataFile.set("converted", true);
			Bukkit.getLogger().info("[CustomCommands] Successfully converted all items and locations from the old database file. " + items + " items" + (items > 1 ? "s" : "") + " and " + locations + " arena" + (locations > 1 ? "s" : "") + " were loaded.");
		}

		// Loading commands

		ConfigFile file = getStorage().getEmbeddedConfig("commands.yml");

		for (String key : file.getLast().getConfigurationSection("").getKeys(false))
		{
			try
			{
				List<String> aliases = file.getOrDefaultList(key + ".aliases");
				List<CommandArgumentsPattern> patterns = new ArrayList<CommandArgumentsPattern>();

				for (String pattern : file.getOrDefaultList(key + ".arguments-patterns")) {
					patterns.add(new CommandArgumentsPattern(pattern));
				}

				String usage = file.getOrDefault(key + ".usage", "/" + aliases.get(0));
				String permission = file.getOrDefault(key + ".permission", null);
				List<ActionData> actions = new ArrayList<ActionData>();

				for (String path : file.getLast().getConfigurationSection(key + ".actions").getKeys(false)) {
					actions.add(new ActionData(file, key + ".actions." + path));
				}

				commands.add(new CustomCommand(usage, permission, aliases, patterns, actions));
				Bukkit.getLogger().info("[CustomCommands] Successfully registered command '" + key + "'");
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
				Bukkit.getLogger().warning("[CustomCommands] Could not load command '" + key + "'.");
			}
		}

		// Events

		Bukkit.getPluginManager().registerEvents(this, this);

		// Plugin's commands

		handler = new CommandHandler("/ccmd", Core.getMessenger());
		getCommand("customcommands").setExecutor(this);

		// Command /ccmd saveitem [name]

		handler.addSubCommand(new CommandSubHandler(true, true, "ccmd.admin", new CommandArgumentsPattern("saveitem [string]"))
		{
			@Override
			public void execute(CommandCallInfo call)
			{
				Player player = call.getSenderAsPlayer();
				String name = call.getArgAsString(1);

				if (!UString.isAlphanumeric(name))
				{
					Core.getMessenger().error(player, "CustomCommands >>", "This name isn't alphanumeric !");
					return;
				}

				if (dataFile.update().contains("items." + name))
				{
					Core.getMessenger().error(player, "CustomCommands >>", "This name is already taken !");
					return;
				}

				ItemStack item = player.getItemInHand();

				if (item == null || item.getType().equals(Material.AIR))
				{
					Core.getMessenger().error(player, "CustomCommands >>", "This item is invalid !");
					return;
				}

				dataFile.set("items." + name, UInventory.serializeItem(item));
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

				if (!UString.isAlphanumeric(name))
				{
					Core.getMessenger().error(player, "CustomCommands >>", "This name isn't alphanumeric !");
					return;
				}

				if (dataFile.update().contains("locations." + name))
				{
					Core.getMessenger().error(player, "CustomCommands >>", "This name is already taken !");
					return;
				}

				dataFile.set("locations." + name, ULocation.serializeLocation(player.getLocation()));
				Core.getMessenger().normal(player, "CustomCommands >>", "This location has been saved with name '" + name + "' !");
			}
		});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 0)
		{
			// TODO : /help : /ccmd

			Core.getMessenger().listMessage(sender, "CustomCommands >>", "This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + ".");

			if (sender.hasPermission("pyr.core.admin")) {
				Core.getMessenger().listSubMessage(sender, "  >>", "§a/pyr rl CustomCommands §7: reload the plugin");
			}

			if (sender.hasPermission("ccmd.admin"))
			{
				Core.getMessenger().listSubMessage(sender, "  >>", "/ccmd saveitem [name] : save an item");
				Core.getMessenger().listSubMessage(sender, "  >>", "/ccmd saveloc [name] : save a location");
			}
		}
		else
		{
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

	public String replaceString(String string, Player sender, String[] args)
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
			string = string.replace("{arg:" + i + "}", args[i]);
		}

		string = string.replace("{player}", sender.getName()).replace("{args}", fullArgs);
		return string;
	}

	@Override
	public String getAdditionnalPasteContent()
	{
		return "\n" + "Custom commands (config) : " + getStorage().getEmbeddedConfig("commands.yml").getLast().getConfigurationSection("").getKeys(false).size() + "\n" + "Custom commands (registered) : " + commands.size();
	}
}
