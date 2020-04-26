package net.jfabricationgames.todo.commands.hotkey;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class ToSearchbarCommand extends AbstractButtonCommand implements ButtonCommand {

	public ToSearchbarCommand(TodoFrameController controller) {
		super(controller);
	}

	@Override
	public void execute() {
		controller.requestFocusOnSearchBar();
	}
}
