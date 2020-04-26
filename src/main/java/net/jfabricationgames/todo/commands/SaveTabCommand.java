package net.jfabricationgames.todo.commands;

import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class SaveTabCommand extends AbstractButtonCommand implements ButtonCommand {
	
	private TodoTabController tabController;
	
	public SaveTabCommand(TodoFrameController controller, TodoTabController tabController) {
		super(controller);
		this.tabController = tabController;
	}
	
	@Override
	public void execute() {
		tabController.save();
	}
}
