package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class WordWrapToggleButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public WordWrapToggleButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		for (TodoTabController tabController : controller.getAllTabControllers()) {
			tabController.getCodeArea().setWrapText(controller.isWordWrapEnabled());
			tabController.refreshCodeArea();
		}
	}
}
