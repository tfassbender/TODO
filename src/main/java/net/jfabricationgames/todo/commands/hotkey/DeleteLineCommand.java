package net.jfabricationgames.todo.commands.hotkey;

import java.util.Optional;

import org.fxmisc.richtext.CodeArea;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class DeleteLineCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public DeleteLineCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		deleteCurrentLine();
	}
	
	private void deleteCurrentLine() {
		Optional<TodoTabController> tabController = controller.getCurrentTabController();
		if (tabController.isPresent()) {
			CodeArea codeArea = tabController.get().getCodeArea();
			int[] selectedLineTextIndices = getSelectedLineTextIndices(codeArea);
			
			codeArea.replace(selectedLineTextIndices[0], selectedLineTextIndices[1], "", "");
		}
	}
}
