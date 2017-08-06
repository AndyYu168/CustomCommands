package be.pyrrh4.customcommands.command.action;

import org.bukkit.entity.Player;

import be.pyrrh4.core.Logger;
import be.pyrrh4.core.Logger.Level;
import java.util.ArrayList;
import be.pyrrh4.customcommands.CustomCommands;

public class ActionWait implements Action
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private int current = 0, max = 0;

	public ActionWait(Player sender, ArrayList<String> data, String[] args)
	{
		try {
			max = Integer.parseInt(data.get(0));
		} catch (Exception exception) {
			Logger.log(Level.WARNING, CustomCommands.instance(), "Could not find the delay.");
		}
	}

	// ------------------------------------------------------------
	// Override : is over
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return ++current >= max;
	}
}
