package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionCommandsAs implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionCommandsAs(final Player sender, final ArrayList<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);

				for (int i = 0; i < data.size(); i++) {
					data.set(i, Utils.format(Utils.fillPAPI(sender, data.get(i))));
				}

				// target player
				if (target.equalsIgnoreCase("player")) {
					for (int i = 1; i < data.size(); i++) {
						Bukkit.dispatchCommand(sender, CustomCommands.instance().replaceString(data.get(i), sender, args));
					}
				}
				// target everyone
				else if (target.equalsIgnoreCase("everyone")) {
					for (Player pl : Utils.getOnlinePlayers()) {
						for (int i = 1; i < data.size(); i++) {
							Bukkit.dispatchCommand(pl, CustomCommands.instance().replaceString(data.get(i), sender, args));
						}
					}
				}
				// target player in argument
				else {
					try {
						Player newTarget = Utils.getPlayer(target);
						for (int i = 1; i < data.size(); i++) {
							Bukkit.dispatchCommand(newTarget, CustomCommands.instance().replaceString(data.get(i), sender, args));
						}
					} catch (Exception exception) {
						CustomCommands.instance().getLocale().getMessage("error_target").send(sender, "$PLAYER", target);
					}
				}
			}
		}.runTask(CustomCommands.instance());
	}

	// ------------------------------------------------------------
	// Override : is over
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return true;
	}
}
