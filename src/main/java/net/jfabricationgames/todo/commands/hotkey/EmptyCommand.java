package net.jfabricationgames.todo.commands.hotkey;

import net.jfabricationgames.todo.commands.ButtonCommand;

/**
 * An empty command that does nothing (used to remove hotkeys).
 */
public class EmptyCommand implements ButtonCommand {
	
	@Override
	public void execute() {
		//do nothing here because this command is empty
	}
}
