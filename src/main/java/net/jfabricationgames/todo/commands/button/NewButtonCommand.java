package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
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
		TodoTabController tabController = GuiUtils.loadTab(controller, controller);
		tabController.getTab().setText("New TODO");
		tabController.assumeTextSaved();
		this.controller.addTab(tabController);
	}
}
