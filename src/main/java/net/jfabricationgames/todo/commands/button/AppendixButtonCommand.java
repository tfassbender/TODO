package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoAppendixDialogController;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class AppendixButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public AppendixButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		TodoAppendixDialogController appendixController = controller.getAppendixDialogController();
		appendixController.updateTodo(true);
		appendixController.show();
		appendixController.requestFocus();
	}
}
