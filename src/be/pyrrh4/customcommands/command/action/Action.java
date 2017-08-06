package be.pyrrh4.customcommands.command.action;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public interface Action
{
	// ------------------------------------------------------------
	// Type
	// ------------------------------------------------------------

	public static enum Type
	{
		SEND_MESSAGE("send message", ActionMessage.class),
		WAIT_TICKS("wait ticks", ActionWait.class),
		EXECUTE_COMMANDS_FOR("execute commands for", ActionCommandsFor.class),
		EXECUTE_COMMANDS_AS("execute commands as", ActionCommandsAs.class),
		SEND_TITLE("send title", ActionTitle.class),
		SEND_ACTIONBAR("send actionbar", ActionActionbar.class),
		GIVE_ITEM("give item", ActionItem.class),
		TELEPORT("teleport", ActionTeleport.class),
		CHANGE_GAMEMODE("change gamemode", ActionChangeGamemode.class),
		CHANGE_TAB("change tab", ActionTab.class);

		private String type;
		private Class<? extends Action> clazz;

		private Type(String type, Class<? extends Action> clazz) {
			this.type = type;
			this.clazz = clazz;
		}

		public Action create(Player sender, ArrayList<String> data, String[] args)
		{
			try {
				Action action = clazz.getConstructor(Player.class, new ArrayList<String>().getClass(), String[].class).newInstance(sender, data, args);
				return action;
			} catch (Exception exception) {
				exception.printStackTrace();
				return null;
			}
		}

		public static Type fromType(String type)
		{
			for (Type value : values()) {
				if (value.type.equalsIgnoreCase(type)) {
					return value;
				}
			}

			return null;
		}
	}

	// ------------------------------------------------------------
	// Abstract methods
	// ------------------------------------------------------------

	public boolean isOver();
}

