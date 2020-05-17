package net.jfabricationgames.todo.commands.button.appendix;

import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoAppendixDialogController;

public class RemoveAppendixCommand extends AbstractAppendixButtonCommand implements ButtonCommand {
	
	public RemoveAppendixCommand(TodoAppendixDialogController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		controller.deleteSelectedAppendix();
	}
}
