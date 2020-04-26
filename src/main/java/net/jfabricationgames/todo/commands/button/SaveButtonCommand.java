package net.jfabricationgames.todo.commands.button;

import java.util.Optional;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class SaveButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public SaveButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		Optional<TodoTabController> tabController = controller.getCurrentTabController();
		if (tabController.isPresent()) {
			tabController.get().save();
		}
	}
}
