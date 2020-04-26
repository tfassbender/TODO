package net.jfabricationgames.todo.commands.hotkey;

import java.util.Optional;

import org.fxmisc.richtext.CodeArea;

import javafx.scene.control.IndexRange;
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
			String text = codeArea.getText();
			IndexRange selection = codeArea.getSelection();
			
			int beginningOfLine = text.lastIndexOf('\n', selection.getStart());
			if (beginningOfLine == selection.getStart()) {
				//cursor was placed at the end of line, where it directly finds the \n char
				beginningOfLine = text.lastIndexOf('\n', selection.getStart() - 1);
			}
			
			//remove start of line till next line
			beginningOfLine += 1;
			
			if (beginningOfLine < 0) {
				beginningOfLine = 0;
			}
			
			int endOfLine = text.indexOf('\n', selection.getEnd());
			if (endOfLine == -1) {
				//last line -> select till end
				endOfLine = text.length() - 1;
			}

			//remove start of line till next line
			endOfLine += 1;
			
			if (endOfLine < 0) {
				endOfLine = 0;
			}
			
			if (beginningOfLine == endOfLine) {
				//empty line -> remove it too
				endOfLine += 1;
			}
			
			if (endOfLine > text.length()) {
				endOfLine = text.length();
			}
			
			codeArea.replace(beginningOfLine, endOfLine, "", "");
		}
	}
}
