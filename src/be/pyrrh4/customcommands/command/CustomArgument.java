package be.pyrrh4.customcommands.command;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.core.User;
import be.pyrrh4.core.command.Argument;
import be.pyrrh4.core.command.CallInfo;
import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.customcommands.PlayerData;
import be.pyrrh4.customcommands.command.action.Action;
import be.pyrrh4.customcommands.command.action.ActionData;

public class CustomArgument extends Argument
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private String id;
	private boolean toggle, togglePersistent;
	private ArrayList<ActionData> actionsToggleFalse, actionsToggleTrue;

	public CustomArgument(String id, boolean toggle, boolean togglePersistent, ArrayList<ActionData> actionsToggleFalse, ArrayList<ActionData> actionsToggleTrue,
			Argument parent, ArrayList<String> aliases, ArrayList<String> params, boolean playerOnly, boolean async, String permission, String description, ArrayList<String> paramsDescription)
	{
		super(parent, aliases, params, playerOnly, async, permission, description, paramsDescription);
		this.id = id;
		this.toggle = toggle;
		this.togglePersistent = togglePersistent;
		this.actionsToggleFalse = actionsToggleFalse;
		this.actionsToggleTrue = actionsToggleTrue;
	}

	// ------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------

	public String getId() {
		return id;
	}

	public boolean isToggle() {
		return toggle;
	}

	public boolean isTogglePersistent() {
		return togglePersistent;
	}

	public ArrayList<ActionData> getActionsToggleFalse() {
		return actionsToggleFalse;
	}

	public ArrayList<ActionData> getActionsToggleTrue() {
		return actionsToggleTrue;
	}

	// ------------------------------------------------------------
	// Override : perform
	// ------------------------------------------------------------

	@Override
	public void perform(CallInfo call)
	{
		// show help
		if (actionsToggleFalse.size() == 0 && actionsToggleTrue.size() == 0) {
			showHelp(call);
		}
		// execute
		else
		{
			// get actions
			final Player sender = call.getSenderAsPlayer();
			final PlayerData playerData = User.from(sender).getPluginData("customcommands");
			final ArrayList<ActionData> actions = isToggle() && playerData.getToggle(this, sender) ? actionsToggleTrue : actionsToggleFalse;
			final String[] args = call.getArgs();

			if (isToggle()) {
				playerData.toggle(this, sender);
			}

			// execute actions
			new BukkitRunnable()
			{
				private int index = -1;
				private Action current = null;

				@Override
				public void run()
				{
					if (current == null)
					{
						if (++index < actions.size())
						{
							ActionData data = actions.get(index);
							current = CustomCommands.instance().createAction(CustomArgument.this, data.getType(), sender, data.getData(), args);
						}
						else
						{
							cancel();
							return;
						}
					}

					if (current != null && current.isOver()) {
						current = null;
					}
				}
			}.runTaskTimerAsynchronously(CustomCommands.instance(), 0L, 1L);
		}
	}
}
