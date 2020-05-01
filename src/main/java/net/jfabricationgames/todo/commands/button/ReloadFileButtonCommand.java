package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class ReloadFileButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public ReloadFileButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		controller.getCurrentTabController().ifPresent(tabController -> tabController.reloadFileContent());
	}
}
