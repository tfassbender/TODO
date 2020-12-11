package net.jfabricationgames.todo.commands.hotkey;

import java.util.Optional;

import org.fxmisc.richtext.CodeArea;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class MoveLinesCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public enum Direction {
		UP, DOWN;
	}
	
	private Direction moveDirection;
	
	public MoveLinesCommand(TodoFrameController controller, Direction moveDirection) {
		super(controller);
		this.moveDirection = moveDirection;
	}
	
	@Override
	public void execute() {
		Optional<TodoTabController> tabController = controller.getCurrentTabController();
		if (tabController.isPresent()) {
			CodeArea codeArea = tabController.get().getCodeArea();
			int[] selectedLineTextIndices = getSelectedLineTextIndices(codeArea);
			
			String movedText = codeArea.getText().substring(selectedLineTextIndices[0], selectedLineTextIndices[1]);
			codeArea.replaceText(selectedLineTextIndices[0], selectedLineTextIndices[1], "");
			
			int insertPosition;
			if (moveDirection == Direction.UP) {
				insertPosition = getStartOfPreviousLine(codeArea, selectedLineTextIndices[0]);
			}
			else {
				insertPosition = getStartOfNextLine(codeArea, selectedLineTextIndices[0]);
			}
			
			codeArea.insertText(insertPosition, movedText);
			
			int selectedTextLength = selectedLineTextIndices[1] - selectedLineTextIndices[0];
			codeArea.selectRange(insertPosition, insertPosition + selectedTextLength - 1);
		}
	}
	
	private int getStartOfPreviousLine(CodeArea codeArea, int startOfSelectedLine) {
		String textBeforeSelectedLine = codeArea.getText().substring(0, startOfSelectedLine);
		return Math.max(0, textBeforeSelectedLine.lastIndexOf('\n', textBeforeSelectedLine.length() - 2) + 1);
	}
	
	private int getStartOfNextLine(CodeArea codeArea, int startOfSelectedLine) {
		String textAfterSelectedLine = codeArea.getText().substring(startOfSelectedLine);
		return startOfSelectedLine + Math.max(0, textAfterSelectedLine.indexOf('\n') + 1);
	}
}
