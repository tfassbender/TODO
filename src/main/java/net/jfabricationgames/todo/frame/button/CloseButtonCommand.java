package net.jfabricationgames.todo.frame.button;

import java.util.Optional;

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
