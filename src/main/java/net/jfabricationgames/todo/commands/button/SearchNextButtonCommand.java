package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.search.TodoSearchTool;

public class SearchNextButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	private TodoSearchTool searchTool;
	
	public SearchNextButtonCommand(TodoFrameController controller, TodoSearchTool searchTool) {
		super(controller);
		this.searchTool = searchTool;
	}
	
	@Override
	public void execute() {
		searchTool.toNextOccurrence();
	}
}
