package be.pyrrh4.customcommands;

import java.util.HashMap;

import org.bukkit.entity.Player;

import be.pyrrh4.core.PlayerPluginData;
import be.pyrrh4.core.util.Utils;
import be.pyrrh4.customcommands.command.CustomArgument;

public class PlayerData extends PlayerPluginData
{
	// ------------------------------------------------------------
	// Fields and getters
	// ------------------------------------------------------------

	private HashMap<String, Boolean> toggles = new HashMap<String, Boolean>();
	private transient HashMap<String, Boolean> persistence = new HashMap<String, Boolean>();

	public boolean getToggle(CustomArgument arg, Player player) {
		String argFullId = CustomCommands.instance().getFullId(arg);
		persistence.put(argFullId, arg.isTogglePersistent());
		if (!toggles.containsKey(argFullId)) {
			toggles.put(argFullId, false);
		}
		return toggles.get(argFullId);
	}

	public void toggle(CustomArgument arg, Player player)
	{
		String argFullId = CustomCommands.instance().getFullId(arg);
		boolean toggle = getToggle(arg, player);
		toggles.put(argFullId, !toggle);
		persistence.put(argFullId, arg.isTogglePersistent());
	}

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	private transient HashMap<String, Boolean> togglesCopy = null;

	@Override
	public void presave()
	{
		togglesCopy = Utils.asMapCopy(toggles);

		for (String argFullId : Utils.asList(toggles.keySet())) {
			if (persistence.containsKey(argFullId) && persistence.get(argFullId)) {}
			else toggles.remove(argFullId);
		}
	}

	@Override
	public void postsave()
	{
		for (String argFullId : togglesCopy.keySet()) {
			if (!toggles.containsKey(argFullId)) {
				toggles.put(argFullId, togglesCopy.get(argFullId));
			}
		}

		togglesCopy.clear();
		togglesCopy = null;
	}
}
