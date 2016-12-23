package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import be.pyrrh4.core.lib.messenger.Replacer;
import be.pyrrh4.core.util.UBukkit;
import be.pyrrh4.core.util.UString;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionMessage implements Action
{
	public ActionMessage(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.i.replaceString(data.get(0).replace(" ", ""), sender, args);
		ArrayList<String> message = new ArrayList<String>();

		for (int i = 1; i < data.size(); i++) {
			message.add(UString.format(CustomCommands.i.replaceString(data.get(i), sender, args)));
		}

		// Target player

		if (target.equalsIgnoreCase("player"))
		{
			for (String str : message) {
				sender.sendMessage(str.replace("{receiver}", sender.getName()));
			}
		}

		// Target everyone

		else if (target.equalsIgnoreCase("everyone"))
		{
			for (Player pl : UBukkit.getOnlinePlayers())
			{
				for (String str : message) {
					pl.sendMessage(str.replace("{receiver}", pl.getName()));
				}
			}
		}

		// Target player in argument

		else
		{
			try
			{
				Player newTarget = Bukkit.getPlayer(target);

				for (String str : message) {
					newTarget.sendMessage(str.replace("{receiver}", newTarget.getName()));
				}
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
