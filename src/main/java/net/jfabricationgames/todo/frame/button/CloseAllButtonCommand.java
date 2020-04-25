package net.jfabricationgames.todo.frame.button;

import java.util.List;

import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class CloseAllButtonCommand extends AbstractCloseButtonCommand implements ButtonCommand {
	
	public CloseAllButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		List<TodoTabController> tabControllers = controller.getAllTabControllers();
		for (int i = 0; i < tabControllers.size(); i++) {
			TodoTabController tabController = tabControllers.get(i);
			closeIfUnchanged(tabController);
		}
	}
}
