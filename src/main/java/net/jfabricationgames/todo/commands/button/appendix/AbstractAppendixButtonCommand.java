package net.jfabricationgames.todo.commands.button.appendix;

import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoAppendixDialogController;

public abstract class AbstractAppendixButtonCommand implements ButtonCommand {
	
	protected TodoAppendixDialogController controller;

	public AbstractAppendixButtonCommand(TodoAppendixDialogController controller) {
		this.controller = controller;
	}
}
