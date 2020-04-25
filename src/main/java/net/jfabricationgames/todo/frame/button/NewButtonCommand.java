package net.jfabricationgames.todo.frame.button;

import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;
import net.jfabricationgames.todo.frame.util.GuiUtils;

public class NewButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public NewButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		insertNewTab();
	}
	
	private void insertNewTab() {
		TodoTabController tabController = GuiUtils.loadTab(controller);
		tabController.getTab().setText("New TODO");
		tabController.assumeTextSaved();
		this.controller.addTab(tabController);
	}
}
