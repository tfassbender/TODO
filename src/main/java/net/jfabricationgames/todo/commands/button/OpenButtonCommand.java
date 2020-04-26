package net.jfabricationgames.todo.commands.button;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javafx.stage.FileChooser;
import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;
import net.jfabricationgames.todo.frame.util.DialogUtils;
import net.jfabricationgames.todo.frame.util.GuiUtils;

public class OpenButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	private File lastOpened;
	
	public OpenButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		FileChooser fileChooser = new FileChooser();
		
		//configure the file chooser
		fileChooser.setTitle("Open TODOs");
		if (lastOpened != null) {
			fileChooser.setInitialDirectory(lastOpened.getParentFile());
		}
		else {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		}
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TODO files (*.todo)", "*.todo"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*"));
		
		//choose the files
		List<File> filesToOpen = fileChooser.showOpenMultipleDialog(controller.getStage());
		if (filesToOpen != null) {
			for (File file : filesToOpen) {
				openFileAsTodo(file);
			}
		}
	}
	
	private void openFileAsTodo(File file) {
		lastOpened = file;
		String content;
		try {
			content = new String(Files.readAllBytes(file.toPath()));
		}
		catch (IOException e) {
			DialogUtils.showErrorDialog("Couldn't open file", "The file couldn't be opened:\n" + file.getAbsolutePath(), e.getMessage(), true);
			return;
		}
		insertNewTab(file, file.getName(), content);
	}
	
	private void insertNewTab(File file, String name, String content) {
		TodoTabController tabController = GuiUtils.loadTab(controller, controller);
		tabController.setFile(file);
		tabController.getTab().setText(name);
		tabController.setText(content);
		this.controller.addTab(tabController);
	}
}
