package net.jfabricationgames.todo.commands;

/**
 * A Runnable for button functions.
 */
@FunctionalInterface
public interface ButtonCommand {
	
	/**
	 * Execute the command
	 */
	public void execute();
}
