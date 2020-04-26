package net.jfabricationgames.todo.commands;

import net.jfabricationgames.todo.commands.button.SaveButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public abstract class AbstractCloseButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public AbstractCloseButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	protected void closeIfUnchanged(TodoTabController tabController) {
		if (tabController.isTextChanged()) {
			DialogUtils.showConfirmationDialog_YesNoCancel("Save before closing?", "The TODO has changed", "Do you want to save before closing?", //
					() -> saveAndClose(tabController), // yes
					() -> close(tabController), // no
					null);// cancel
		}
		else {
			controller.removeTab(tabController);
		}
	}
	
	protected void saveAndClose(TodoTabController tabController) {
		new SaveButtonCommand(controller).execute();
		close(tabController);
	}
	
	protected void close(TodoTabController tabController) {
		controller.removeTab(tabController);
	}
}
