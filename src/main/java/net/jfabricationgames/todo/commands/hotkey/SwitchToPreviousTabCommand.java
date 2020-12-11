package net.jfabricationgames.todo.commands.hotkey;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class SwitchToPreviousTabCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public SwitchToPreviousTabCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		controller.setSelectedTab((controller.getSelectedTabIndex() - 1 + controller.getNumTabs()) % controller.getNumTabs());
	}
}
