package net.jfabricationgames.todo.commands;

import net.jfabricationgames.todo.frame.TodoFrameController;

public abstract class AbstractButtonCommand implements ButtonCommand {
	
	protected TodoFrameController controller;
	
	public AbstractButtonCommand(TodoFrameController controller) {
		this.controller = controller;
	}
}
