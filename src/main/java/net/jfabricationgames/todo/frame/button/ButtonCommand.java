package net.jfabricationgames.todo.frame.button;

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
