package be.pyrrh4.customcommands.command.action;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.core.messenger.Replacer;
import be.pyrrh4.core.util.UBukkit;
import be.pyrrh4.core.util.UInventory;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionItem implements Action
{
	public ActionItem(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.i.replaceString(data.get(0).replace(" ", ""), sender, args);
		String itemName = CustomCommands.i.replaceString(data.get(1).replace(" ", ""), sender, args);
		ItemStack item;

		try
		{
			item = UInventory.unserializeItem(CustomCommands.i.dataFile.getOrDefault("items." + itemName, (String) null));
		}
		catch (Exception exception)
		{
			CustomCommands.i.log(Level.WARNING, "Could not find the item.");
			return;
		}

		if (item == null)
		{
			CustomCommands.i.log(Level.WARNING, "Could not find the item.");
			return;
		}

		// Target player

		if (target.equalsIgnoreCase("player"))
		{
			sender.getInventory().addItem(item);
			sender.updateInventory();
		}

		// Target everyone

		else if (target.equalsIgnoreCase("everyone"))
		{
			for (Player pl : UBukkit.getOnlinePlayers())
			{
				pl.getInventory().addItem(item);
				pl.updateInventory();
			}
		}

		// Target player in argument

		else
		{
			try
			{
				Player newTarget = Bukkit.getPlayer(target);
				newTarget.getInventory().addItem(item);
				newTarget.updateInventory();
			}
			catch (Exception exception) {
				CustomCommands.i.getMessage("error-target").send(new Replacer("{player}", target), sender);
			}
		}
	}

	@Override
	public boolean isOver()
	{
		return true;
	}
}
