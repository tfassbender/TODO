package net.jfabricationgames.todo.commands.button.appendix;

import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoAppendixDialogController;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class NewAppendixCommand extends AbstractAppendixButtonCommand implements ButtonCommand {
	
	public NewAppendixCommand(TodoAppendixDialogController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		// TODO
		DialogUtils.showInfoDialog("New Appendix", "WIP", "");
	}
}
