package net.jfabricationgames.todo.frame.button;

import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class SettingsButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public SettingsButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		//TODO implement real settings dialog
		DialogUtils.showInfoDialog("TODO Settings", "WIP", "");
	}
}
