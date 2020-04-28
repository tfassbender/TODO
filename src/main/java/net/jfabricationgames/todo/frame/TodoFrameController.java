package net.jfabricationgames.todo.frame;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.commands.SaveTabCommand;
import net.jfabricationgames.todo.commands.button.CloseAllButtonCommand;
import net.jfabricationgames.todo.commands.button.CloseButtonCommand;
import net.jfabricationgames.todo.commands.button.NewButtonCommand;
import net.jfabricationgames.todo.commands.button.OpenButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveAllButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveButtonCommand;
import net.jfabricationgames.todo.commands.button.SettingsButtonCommand;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class TodoFrameController implements Initializable {
	
	@FXML
	private Button buttonNew;
	@FXML
	private Button buttonOpen;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonSaveAll;
	@FXML
	private Button buttonClose;
	@FXML
	private Button buttonCloseAll;
	@FXML
	private Button buttonSettings;
	@FXML
	private TextField textAreaSearch;
	@FXML
	private Button buttonSearch;
	@FXML
	private TabPane tabView;
	
	private List<TodoTabController> todoTabControllers = new ArrayList<TodoTabController>();
	
	private TodoFramePropertiesStore properties = new TodoFramePropertiesStore();
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadTabs();
		insertInitialTab();
		addButtonCommands();
		addButtonTooltips();
		addWindowClosingListeners();
		adjustWindowPosition();
		chooseInitialSelectedTab();
	}
	
	/**
	 * Add a new tab that is included in a tabController
	 */
	public void addTab(TodoTabController tabController) {
		todoTabControllers.add(tabController);
		tabView.getTabs().add(tabController.getTab());
		tabView.getSelectionModel().select(tabController.getTab());
		tabController.requestFocusOnCodeArea();
	}
	
	/**
	 * Remove a tab that is identified by it's tab controller
	 */
	public void removeTab(TodoTabController tabController) {
		todoTabControllers.remove(tabController);
		tabView.getTabs().remove(tabController.getTab());
		if (todoTabControllers.isEmpty()) {
			//always keep one tab open (by adding a new one if all are closed)
			new NewButtonCommand(this).execute();
		}
	}
	
	/**
	 * Get the current window for showing dialogs
	 */
	public Window getWindow() {
		return tabView.getScene().getWindow();
	}
	
	/**
	 * Get the controller of the currently selected tab
	 */
	public Optional<TodoTabController> getCurrentTabController() {
		int selectedTab = tabView.getSelectionModel().getSelectedIndex();
		if (selectedTab < 0 || todoTabControllers.size() <= selectedTab) {
			return Optional.empty();
		}
		return Optional.of(todoTabControllers.get(selectedTab));
	}
	
	/**
	 * Get the controllers of all tabs that are opened as a list
	 */
	public List<TodoTabController> getAllTabControllers() {
		return new ArrayList<TodoTabController>(todoTabControllers);
	}
	
	public int getSelectedTabIndex() {
		return tabView.getSelectionModel().getSelectedIndex();
	}
	
	public int getNumTabs() {
		return todoTabControllers.size();
	}
	
	public void setSelectedTab(int tab) {
		tabView.getSelectionModel().select(tab);
		requestFocusOnCurrentCodeArea();
	}
	
	public void requestFocusOnCurrentCodeArea() {
		getCurrentTabController().ifPresent(controller -> controller.requestFocusOnCodeArea());
	}
	
	public void requestFocusOnSearchBar() {
		textAreaSearch.requestFocus();
	}
	
	//***********************************************************************************
	//*** private
	//***********************************************************************************
	
	private void loadTabs() {
		List<File> rememberedTabs = properties.getTodoFiles();
		for (File file : rememberedTabs) {
			new OpenFileCommand(this, file).execute();
		}
	}
	
	private void insertInitialTab() {
		if (getNumTabs() == 0) {
			new NewButtonCommand(this).execute();
			requestFocusOnCurrentCodeArea();
		}
	}
	
	private void addButtonCommands() {
		NewButtonCommand newButtonCommand = new NewButtonCommand(this);
		buttonNew.setOnAction(e -> newButtonCommand.execute());
		OpenButtonCommand openButtonCommand = new OpenButtonCommand(this);
		buttonOpen.setOnAction(e -> openButtonCommand.execute());
		SaveButtonCommand saveButtonCommand = new SaveButtonCommand(this);
		buttonSave.setOnAction(e -> saveButtonCommand.execute());
		SaveAllButtonCommand saveAllButtonCommand = new SaveAllButtonCommand(this);
		buttonSaveAll.setOnAction(e -> saveAllButtonCommand.execute());
		CloseButtonCommand closeButtonCommand = new CloseButtonCommand(this);
		buttonClose.setOnAction(e -> closeButtonCommand.execute());
		CloseAllButtonCommand closeAllButtonCommand = new CloseAllButtonCommand(this);
		buttonCloseAll.setOnAction(e -> closeAllButtonCommand.execute());
		SettingsButtonCommand settingsButtonCommand = new SettingsButtonCommand(this);
		buttonSettings.setOnAction(e -> settingsButtonCommand.execute());
	}
	
	private void addButtonTooltips() {
		Tooltip newButtonTooltip = new Tooltip("New TODO (Ctrl + N)");
		buttonNew.setTooltip(newButtonTooltip);
		Tooltip openButtonTooltip = new Tooltip("Open TODO (Ctrl + O)");
		buttonOpen.setTooltip(openButtonTooltip);
		Tooltip saveButtonTooltip = new Tooltip("Save (Ctrl + S)");
		buttonSave.setTooltip(saveButtonTooltip);
		Tooltip saveAllButtonTooltip = new Tooltip("Save All (Ctrl + Shift + S)");
		buttonSaveAll.setTooltip(saveAllButtonTooltip);
		Tooltip closeButtonTooltip = new Tooltip("Close (Ctrl + W)");
		buttonClose.setTooltip(closeButtonTooltip);
		Tooltip closeAllTooltip = new Tooltip("Close All");
		buttonCloseAll.setTooltip(closeAllTooltip);
		Tooltip settingsTooltip = new Tooltip("Open Settings Dialog");
		buttonSettings.setTooltip(settingsTooltip);
	}
	
	private void addWindowClosingListeners() {
		Platform.runLater(() -> {
			getWindow().setOnCloseRequest(e -> {
				new SaveBeforeClosingCommand(e).execute();
				properties.setFiles(
						getAllTabControllers().stream().map(TodoTabController::getFile).filter(file -> file != null).collect(Collectors.toList()));
				properties.setWindowPosition(getWindow());
				properties.setSelectedTab(getSelectedTabIndex());
				properties.store();
			});
		});
	}
	
	private void adjustWindowPosition() {
		Platform.runLater(() -> {
			properties.adjustWindowPosition(getWindow());
		});
	}
	
	private void chooseInitialSelectedTab() {
		setSelectedTab(properties.getSelectedTab());
	}
	
	//***********************************************************************************
	//*** classes
	//***********************************************************************************
	
	/**
	 * Opens a file as ToDo without a {@link FileChooser} dialog.
	 */
	public static class OpenFileCommand extends AbstractButtonCommand implements ButtonCommand {
		
		private File file;
		
		public OpenFileCommand(TodoFrameController controller, File file) {
			super(controller);
			this.file = file;
		}
		
		@Override
		public void execute() {
			openFileAsTodo(file);
		}
	}
	
	public class SaveBeforeClosingCommand extends AbstractButtonCommand implements ButtonCommand {
		
		private WindowEvent windowClosingEvent;
		
		private boolean abortClose = false;
		
		public SaveBeforeClosingCommand(WindowEvent windowClosingEvent) {
			super(null);
			this.windowClosingEvent = windowClosingEvent;
		}
		
		@Override
		public void execute() {
			for (TodoTabController controller : getAllTabControllers()) {
				if (!abortClose) {
					if (controller.isTextChanged()) {
						DialogUtils.showConfirmationDialog_YesNoCancel("Save before closing?",
								"The TODO has changed:\n" + controller.getTab().getText(), "Do you want to save before closing?", //
								() -> new SaveTabCommand(TodoFrameController.this, controller).execute(), // yes -> save the tab
								null, // no -> don't do anything
								() -> {// cancel -> consume the window closing event and abort the dialogs
									windowClosingEvent.consume();
									abortClose = true;
								});
					}
				}
			}
		}
	}
}