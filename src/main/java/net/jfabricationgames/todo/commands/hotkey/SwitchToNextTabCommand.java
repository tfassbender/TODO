package net.jfabricationgames.todo.commands.hotkey;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class SwitchToNextTabCommand extends AbstractButtonCommand implements ButtonCommand {

	public SwitchToNextTabCommand(TodoFrameController controller) {
		super(controller);
	}

	@Override
	public void execute() {
		controller.setSelectedTab((controller.getSelectedTabIndex() + 1) % controller.getNumTabs());
	}
}
