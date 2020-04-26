package net.jfabricationgames.todo.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
		this.controller.addTab(tabController);
		
		return tabController;
	}
}
