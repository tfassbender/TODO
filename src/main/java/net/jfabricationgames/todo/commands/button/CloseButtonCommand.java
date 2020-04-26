package net.jfabricationgames.todo.commands.button;

import java.util.Optional;

import net.jfabricationgames.todo.commands.AbstractCloseButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class CloseButtonCommand extends AbstractCloseButtonCommand implements ButtonCommand {
	
	public CloseButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		Optional<TodoTabController> tabControllerOptional = controller.getCurrentTabController();
		if (tabControllerOptional.isPresent()) {
			TodoTabController tabController = tabControllerOptional.get();
			closeIfUnchanged(tabController);
		}
	}
}
