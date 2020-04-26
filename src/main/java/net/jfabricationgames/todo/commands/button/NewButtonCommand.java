package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class NewButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public NewButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		insertNewTab(null, "New TODO", "");
	}
}
