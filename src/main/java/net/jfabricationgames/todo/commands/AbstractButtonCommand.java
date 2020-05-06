package net.jfabricationgames.todo.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.fxmisc.richtext.CodeArea;

import javafx.scene.control.IndexRange;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;
import net.jfabricationgames.todo.frame.util.DialogUtils;
import net.jfabricationgames.todo.frame.util.GuiUtils;

public abstract class AbstractButtonCommand implements ButtonCommand {
	
	protected TodoFrameController controller;
	
	public AbstractButtonCommand(TodoFrameController controller) {
		this.controller = controller;
	}
	
	protected void openFileAsTodo(File file) {
		//if the file is already opened, show the tab in which it is opened instead of opening it again in a new tab
		Optional<TodoTabController> openedTabController = controller.getTabControllerForFile(file);
		if (openedTabController.isPresent()) {
			controller.setSelectedTab(openedTabController.get());
			return;
		}
		
		String content;
		try {
			content = new String(Files.readAllBytes(file.toPath()));
		}
		catch (IOException e) {
			DialogUtils.showErrorDialog("Couldn't open file", "The file couldn't be opened:\n" + file.getAbsolutePath(), e.getMessage(), true);
			return;
		}
		TodoTabController newTabController = insertNewTab(file, file.getName().substring(0, file.getName().lastIndexOf('.')), content);
		newTabController.updateTabName();
		newTabController.assumeTextSaved();
	}
	
	protected TodoTabController insertNewTab(File file, String name, String content) {
		TodoTabController tabController = GuiUtils.loadTab(controller, controller);
		tabController.setFile(file);
		tabController.getTab().setText(name);
		tabController.setText(content);
		tabController.assumeTextSaved();
		this.controller.addTab(tabController);
		
		return tabController;
	}
	
	/**
	 * Find the start and end indices of the code areas text from the start of the first selected line till the end of the last selected line
	 * 
	 * @param codeArea
	 *        The code area in which the selected lines are searched
	 * 		
	 * @return The start and end index of the selected lines as an int-array: int[2]
	 */
	protected int[] getSelectedLineTextIndices(CodeArea codeArea) {
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
		
		return new int[] {beginningOfLine, endOfLine};
	}
}
