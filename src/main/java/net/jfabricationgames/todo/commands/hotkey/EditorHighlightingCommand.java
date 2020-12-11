package net.jfabricationgames.todo.commands.hotkey;

import java.util.Arrays;
import java.util.Optional;

import org.fxmisc.richtext.CodeArea;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

/**
 * Add or remove a highlighting type from the beginning of all selected lines in the editor
 */
public class EditorHighlightingCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public enum HighlightingType {
		
		DONE("/ "), // 
		COMMENT("// "), //
		IMPORTANT("! "), //
		QUESTION("? "), //
		HEADLINE_1("# "), //
		HEADLINE_2("## "), //
		HEADLINE_3("### "), //
		HEADLINE_4("#### "), //
		STRIKED("/- "), //
		STRIKED_COMMENT("//- ");
		
		private final String lineStart;
		
		private HighlightingType(String lineStart) {
			this.lineStart = lineStart;
		}
		
		public String getLineStart() {
			return lineStart;
		}
	}
	
	private HighlightingType type;
	
	public EditorHighlightingCommand(TodoFrameController controller, HighlightingType type) {
		super(controller);
		this.type = type;
	}
	
	@Override
	public void execute() {
		Optional<TodoTabController> tabController = controller.getCurrentTabController();
		if (tabController.isPresent()) {
			CodeArea codeArea = tabController.get().getCodeArea();
			int[] selectedLineTextIndices = getSelectedLineTextIndices(codeArea);
			
			String selectedText = codeArea.getText().substring(selectedLineTextIndices[0], selectedLineTextIndices[1]);
			//add spaces to not ignore blank lines
			selectedText = selectedText.replace("\n", " \n");
			String[] lines = selectedText.split("\n");
			//remove the spaces again to prevent index errors
			for (int i = 0; i < lines.length - 1; i++) {
				if (lines[i].length() > 0) {
					lines[i] = lines[i].substring(0, lines[i].length() - 1);
				}
			}
			//handle the last line separately cause it might not end with a line-break
			if (selectedText.endsWith("\n")) {
				lines[lines.length - 1] = lines[lines.length - 1].substring(0, lines[lines.length - 1].length() - 1);
			}
			
			boolean allLinesStartWithHighlighting = Arrays.stream(lines).filter(line -> !line.startsWith(type.getLineStart())).count() == 0;
			boolean markChangedLines = true;
			int caretPosition = codeArea.getCaretPosition();
			
			//if only an empty line is selected, add the highlighting without marking it
			if (lines.length <= 1) {
				markChangedLines = false;
				
				if (lines.length == 1 && lines[0].equals("")) {
					allLinesStartWithHighlighting = false;
					lines = new String[] {""};
				}
			}
			
			int insertIndex = selectedLineTextIndices[0];
			for (String line : lines) {
				if (allLinesStartWithHighlighting) {
					//all lines start with the highlighting -> remove the highlighting
					codeArea.replace(insertIndex, insertIndex + type.getLineStart().length(), "", "");
					insertIndex += line.length() + 1 - type.getLineStart().length();
				}
				else {
					//not all lines start with the highlighting -> add it to those which do not start with a highlighting
					if (!line.startsWith(type.getLineStart())) {
						codeArea.insert(insertIndex, type.getLineStart(), "");
						insertIndex += type.getLineStart().length();
					}
					insertIndex += line.length() + 1;
				}
			}
			
			if (markChangedLines) {
				//re-select the text that was edited
				codeArea.selectRange(selectedLineTextIndices[0], insertIndex - 1);
			}
			else {
				int newCaretPosition = caretPosition;
				if (allLinesStartWithHighlighting) {
					newCaretPosition -= type.getLineStart().length();
				}
				else {
					newCaretPosition += type.getLineStart().length();
				}
				codeArea.moveTo(newCaretPosition);
			}
		}
	}
}
