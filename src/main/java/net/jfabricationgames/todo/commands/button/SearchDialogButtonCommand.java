package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class SearchDialogButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public SearchDialogButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		DialogUtils.showInfoDialog("Search TODOs", "WIP", "");
	}
}
