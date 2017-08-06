package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionChangeGamemode implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionChangeGamemode(final Player sender, final ArrayList<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				String target = CustomCommands.instance().replaceString(data.get(0).replace(" ", ""), sender, args);
				GameMode gamemode = null;

				try {
					int id = Integer.parseInt(data.get(1));
					if (id == 0) gamemode = GameMode.SURVIVAL;
					else if (id == 1) gamemode = GameMode.CREATIVE;
					else if (id == 2) gamemode = GameMode.SURVIVAL;
					else if (id == 3) gamemode = GameMode.valueOf("SPECTATOR");
				} catch (Exception ignored) {}

				// target player
				if (target.equalsIgnoreCase("player")) {
					sender.setGameMode(gamemode);
				}
				// target everyone
				else if (target.equalsIgnoreCase("everyone")) {
					for (Player pl : Utils.getOnlinePlayers()) {
						pl.setGameMode(gamemode);
					}
				}
				// target player in argument
				else {
					try {
						Player newTarget = Utils.getPlayer(target);
						newTarget.setGameMode(gamemode);
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
