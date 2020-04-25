package net.jfabricationgames.todo.frame.button;

import net.jfabricationgames.todo.frame.TodoFrameController;

public abstract class AbstractButtonCommand implements ButtonCommand {
	
	protected TodoFrameController controller;
	
	public AbstractButtonCommand(TodoFrameController controller) {
		this.controller = controller;
	}
}
