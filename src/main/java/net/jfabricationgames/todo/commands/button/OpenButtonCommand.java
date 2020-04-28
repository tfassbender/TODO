package net.jfabricationgames.todo.commands.button;

import java.io.File;
import java.util.List;

import javafx.stage.FileChooser;
import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;

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
		List<File> filesToOpen = fileChooser.showOpenMultipleDialog(controller.getWindow());
		if (filesToOpen != null) {
			for (File file : filesToOpen) {
				openFileAsTodo(file);
				lastOpened = file;
			}
		}
	}
}
