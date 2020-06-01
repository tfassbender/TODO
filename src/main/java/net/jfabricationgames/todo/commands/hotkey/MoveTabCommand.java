package net.jfabricationgames.todo.commands.hotkey;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoFrameController.Direction;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class MoveTabCommand extends AbstractButtonCommand implements ButtonCommand {
	
	private Direction direction;
	
	public MoveTabCommand(TodoFrameController controller, Direction direction) {
		super(controller);
		this.direction = direction;
	}
	
	@Override
	public void execute() {
		try {
			controller.moveCurrentTab(direction);
		}
		catch (IllegalStateException ise) {
			DialogUtils.showErrorDialog("Can't move Tab", "The tab can't be moved:", ise.getMessage());
		}
	}
}
