package be.pyrrh4.customcommands;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.core.PluginData;

public class MainData extends PluginData
{
	// ------------------------------------------------------------
	// Fields and getters
	// ------------------------------------------------------------

	private HashMap<String, ItemStack> items = new HashMap<String, ItemStack>();
	private HashMap<String, Location> locations = new HashMap<String, Location>();

	public HashMap<String, ItemStack> getItems() {
		return items;
	}

	public HashMap<String, Location> getLocations() {
		return locations;
	}
}
