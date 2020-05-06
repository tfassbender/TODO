package net.jfabricationgames.todo.frame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.fxmisc.richtext.CodeArea;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import net.jfabricationgames.todo.commands.button.CloseButtonCommand;
import net.jfabricationgames.todo.configuration.CodeAreaConfiguator;
import net.jfabricationgames.todo.configuration.TabConfigurator;
import net.jfabricationgames.todo.configuration.highlighting.ParagraphConfigurator;
import net.jfabricationgames.todo.configuration.highlighting.TodoHighlightingConfigurator;
import net.jfabricationgames.todo.configuration.hotkey.TodoHotkeyConfigurator;
import net.jfabricationgames.todo.configuration.tab.TabIconConfigurator;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class TodoTabController implements Initializable {
	
	public static final String TODO_TAB_FXML = "/net/jfabricationgames/todo/frame/TodoTab.fxml";
	public static final String DEFAULT_FILE_DIR = "TODOs/";
	public static final int MAX_TITLE_LENGTH = 20;
	
	private final TodoHighlightingConfigurator highlightingConfigurator;
	private final List<CodeAreaConfiguator> configurators;
	private final List<TabConfigurator> tabConfigurators;
	
	@FXML
	private CodeArea codeArea;
	
	private TodoFrameController frameController;
	
	private Tab tab;
	private File file;
	private String lastSavedText;
	private String ignoredFileChanges;
	private boolean displayingReloadDialog;
	
	public TodoTabController(TodoFrameController frameController) {
		this.frameController = frameController;
		highlightingConfigurator = new TodoHighlightingConfigurator();
		configurators = Arrays.asList(highlightingConfigurator, new ParagraphConfigurator(), new TodoHotkeyConfigurator(frameController));
		tabConfigurators = Arrays.asList(new TabIconConfigurator(this));
	}
	
	//***********************************************************************************
	//*** properties
	//***********************************************************************************
	
	public void requestFocusOnCodeArea() {
		codeArea.requestFocus();
	}
	
	public void setText(String content) {
		codeArea.replaceText(content);
	}
	
	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public CodeArea getCodeArea() {
		return codeArea;
	}
	
	public TodoHighlightingConfigurator getHighlightingConfigurator() {
		return highlightingConfigurator;
	}
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configureCodeArea();
		configureTab();
		addFileConsistencyListener();
	}
	
	/**
	 * Save this ToDo to a file
	 */
	public void save() {
		try {
			String text = codeArea.getText();
			if (file == null) {
				createFile();
			}
			
			//create the file if it doesn't exist
			file.getParentFile().mkdirs();
			
			//store the text to the file
			try (FileWriter fw = new FileWriter(file);//
					BufferedWriter bw = new BufferedWriter(fw)) {
				bw.write(text);
				bw.flush();
				bw.close();
			}
			lastSavedText = text;
			
			//set the tab title to the files title
			updateTabName();
		}
		catch (IOException ioe) {
			DialogUtils.showErrorDialog("File couldn't be saved", "The file couldn't be saved:\n" + file.getAbsolutePath(), ioe.getMessage(), true);
		}
	}
	
	/**
	 * Update the name of the tab that is hold by this controller to the new ToDo name. This is usually the first headline in the text or the file
	 * name.
	 */
	public void updateTabName() {
		if (codeArea.getText().startsWith("# ")) {
			//if the first line is a headline, use the headline as name of the tab
			int firstLineEndIndex = codeArea.getText().indexOf("\n");
			if (firstLineEndIndex == -1) {
				firstLineEndIndex = codeArea.getText().length();
			}
			String name = codeArea.getText().substring(2, firstLineEndIndex);
			tab.setText(getTabName(name));
		}
		else if (file != null) {
			tab.setText(getTabName(file.getName().substring(0, file.getName().lastIndexOf('.'))));
		}
	}
	
	/**
	 * Check whether the text of the code area has changed since the last save action
	 * 
	 * @return Returns true if the text has changed.
	 */
	public boolean isTextChanged() {
		return !codeArea.getText().equals(lastSavedText);
	}
	
	/**
	 * Assume that the current text is the same as in the file (used after loading from file)
	 */
	public void assumeTextSaved() {
		lastSavedText = codeArea.getText();
	}
	
	/**
	 * Reload the content from the .todo file into the editor
	 */
	public void reloadFileContent() {
		String content;
		try {
			content = new String(Files.readAllBytes(file.toPath()));
			int caretPosition = codeArea.getCaretPosition();
			codeArea.replaceText(content);
			codeArea.moveTo(Math.min(caretPosition, content.length()));
			
			//trigger highlighting
			codeArea.insert(0, " ", "");
			codeArea.replace(0, 1, "", "");
			
			assumeTextSaved();
			ignoredFileChanges = null;
		}
		catch (IOException e) {
			DialogUtils.showErrorDialog("Couldn't open file", "The file couldn't be opened:\n" + file.getAbsolutePath(), e.getMessage(), true);
			return;
		}
	}
	
	//***********************************************************************************
	//*** private
	//***********************************************************************************
	
	private void configureCodeArea() {
		for (CodeAreaConfiguator configurator : configurators) {
			configurator.configure(codeArea);
		}
	}
	
	private void configureTab() {
		for (TabConfigurator configurator : tabConfigurators) {
			configurator.configure(getTab());
		}
	}
	
	private void addFileConsistencyListener() {
		codeArea.focusedProperty().addListener((observable, oldState, newState) -> {
			if (newState) {
				checkFileConsistency();
			}
		});
	}
	
	/**
	 * Check whether the file has been modified by another program
	 */
	private void checkFileConsistency() {
		if (file != null) {
			String content;
			try {
				content = new String(Files.readAllBytes(file.toPath()));
			}
			catch (IOException e) {
				//ignore the exception and assume the file as unchanged
				return;
			}
			
			if (!displayingReloadDialog && !content.equals(lastSavedText) && !content.equals(ignoredFileChanges)) {
				//the file has been changed -> let the user choose whether it shall be reloaded
				displayingReloadDialog = true;
				DialogUtils.showConfirmationDialog_ThreeOptions("TODO file has changed", "The file has been changed by another application",
						"Do you want to load the new content to the editor?", // dialog texts
						"Yes", "No", "Close TODO",// button texts
						() -> { // on yes
							reloadFileContent();
							displayingReloadDialog = false;
						}, //
						() -> { // on no
							setIgnoredFileChanges(content);
							displayingReloadDialog = false;
						}, //
						() -> { //on close
							setIgnoredFileChanges(content);
							new CloseButtonCommand(frameController).execute();
							displayingReloadDialog = false;
						});
			}
		}
	}
	
	private void setIgnoredFileChanges(String ignoredFileChanges) {
		this.ignoredFileChanges = ignoredFileChanges;
	}
	
	/**
	 * Create a file for this ToDo where it is saved in the default location where the name is the first content line.
	 */
	private void createFile() {
		String filename = null;
		
		//the filename is the first text line of the todo
		String text = codeArea.getText();
		List<String> lines = Arrays.asList(text.split("\n"));
		
		for (int i = 0; i < lines.size(); i++) {
			if (filename == null) {
				String line = lines.get(i).trim();
				if (!line.isEmpty()) {
					if (line.startsWith("# ")) {
						line = line.substring(2);
					}
					filename = line;
				}
			}
		}
		
		if (filename == null) {
			//no content -> use default name
			filename = "TODO";
		}
		
		//remove spaces and other special chars
		filename = filename.replaceAll("[^a-zA-Z0-9\\.\\-\\_]", "_") + ".todo";
		
		file = new File(DEFAULT_FILE_DIR + filename);
		
		//choose a file that doesn't exist yet
		int postfix = 2;
		while (file.exists()) {
			String fileName = file.getAbsolutePath();
			int substringEndIndex = fileName.lastIndexOf(".");
			if (postfix > 2) {
				substringEndIndex -= 2;
			}
			String nextFileName = fileName.substring(0, substringEndIndex) + "_" + postfix + ".todo";
			postfix++;
			file = new File(nextFileName);
		}
	}
	
	private String getTabName(String longName) {
		if (longName.length() > MAX_TITLE_LENGTH) {
			return longName.substring(0, MAX_TITLE_LENGTH - 3) + "...";
		}
		return longName;
	}
}
