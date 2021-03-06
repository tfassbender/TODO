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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.commands.SaveTabCommand;
import net.jfabricationgames.todo.commands.button.AppendixButtonCommand;
import net.jfabricationgames.todo.commands.button.CloseAllButtonCommand;
import net.jfabricationgames.todo.commands.button.CloseButtonCommand;
import net.jfabricationgames.todo.commands.button.NewButtonCommand;
import net.jfabricationgames.todo.commands.button.OpenButtonCommand;
import net.jfabricationgames.todo.commands.button.ReloadFileButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveAllButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveButtonCommand;
import net.jfabricationgames.todo.commands.button.SearchDialogButtonCommand;
import net.jfabricationgames.todo.commands.button.SettingsButtonCommand;
import net.jfabricationgames.todo.commands.button.WordWrapToggleButtonCommand;
import net.jfabricationgames.todo.frame.util.DialogUtils;
import net.jfabricationgames.todo.frame.util.GuiUtils;
import net.jfabricationgames.todo.search.TodoSearchTool;

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
	private Button buttonReload;
	@FXML
	private Button buttonClose;
	@FXML
	private Button buttonCloseAll;
	@FXML
	private Button buttonAppendix;
	@FXML
	private Button buttonSettings;
	@FXML
	private ToggleButton toggleButtonWordWrap;
	@FXML
	private TextField textAreaSearch;
	@FXML
	private Button buttonSearchDialog;
	@FXML
	private Button buttonSearchNext;
	@FXML
	private Button buttonSearchPrevious;
	@FXML
	private CheckBox checkBoxRegex;
	@FXML
	private TabPane tabView;
	
	private ImageView appendixButtonImage = new ImageView(GuiUtils.loadImage("net/jfabricationgames/todo/buttons/icon_appendix_small.png"));
	private ImageView appendixButtonImagePresent = new ImageView(
			GuiUtils.loadImage("net/jfabricationgames/todo/buttons/icon_appendix_present_small.png"));
	
	private List<TodoTabController> todoTabControllers = new ArrayList<TodoTabController>();
	
	private TodoFramePropertiesStore properties = new TodoFramePropertiesStore();
	
	private TodoSearchTool searchTool;
	
	private TodoAppendixDialogController appendixController;
	
	public enum Direction {
		RIGHT, //
		LEFT; //
	}
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchTool = new TodoSearchTool(this);
		
		loadTabs();
		insertInitialTab();
		createAppendixDialog();
		addButtonCommands();
		addButtonTooltips();
		addSearchCommands();
		addWindowClosingListeners();
		adjustWindowPosition();
		chooseInitialSelectedTab();
		setInitialToggleButtonStates();
		createTabChangeListener();
		
		Platform.runLater(() -> appendixController.updateTodo());
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
	 * Move the currently selected tab one step to the left or right
	 * 
	 * @param direction
	 *        The direction to which the tab shall be moved
	 */
	public void moveCurrentTab(Direction direction) throws IllegalStateException {
		int tabIndex = tabView.getSelectionModel().getSelectedIndex();
		int tabCount = getNumTabs();
		
		if (tabIndex == -1) {
			throw new IllegalStateException("No active tab");
		}
		
		int nextTabIndex = tabIndex;
		switch (direction) {
			case LEFT:
				nextTabIndex--;
				break;
			case RIGHT:
				nextTabIndex++;
				break;
			default:
				throw new IllegalStateException("Unknown direction: " + direction);
		}
		
		//cyclic border condition
		nextTabIndex = (nextTabIndex + tabCount) % tabCount;
		
		//remove tab and insert at new position
		Tab tab = tabView.getTabs().get(tabIndex);
		TodoTabController controller = todoTabControllers.get(tabIndex);
		
		tabView.getTabs().remove(tabIndex);
		tabView.getTabs().add(nextTabIndex, tab);
		
		todoTabControllers.remove(tabIndex);
		todoTabControllers.add(nextTabIndex, controller);
		
		//select the moved tab
		setSelectedTab(nextTabIndex);
	}
	
	public TextField getTextAreaSearch() {
		return textAreaSearch;
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
	
	public void setSelectedTab(TodoTabController tabController) {
		tabView.getSelectionModel().select(tabController.getTab());
		requestFocusOnCurrentCodeArea();
	}
	
	public void requestFocusOnCurrentCodeArea() {
		getCurrentTabController().ifPresent(controller -> controller.requestFocusOnCodeArea());
	}
	
	public void requestFocusOnSearchBar() {
		textAreaSearch.requestFocus();
	}
	
	public void scrollToTop() {
		getCurrentTabController().ifPresent(controller -> controller.scrollToTop());
	}
	
	/**
	 * Get the TodoTabController that manages the given file (or Optional.empty() if the file is not opened in any tab)
	 * 
	 * @param file
	 *        The searched file
	 * @return The TodoTabController (as Optional) or an empty Optional
	 */
	public Optional<TodoTabController> getTabControllerForFile(File file) {
		return todoTabControllers.stream().filter(controller -> controller.getFile() != null).
				filter(controller -> controller.getFile().equals(file)).findAny();
	}
	
	public boolean isWordWrapEnabled() {
		return toggleButtonWordWrap.isSelected();
	}
	
	public TodoAppendixDialogController getAppendixDialogController() {
		return appendixController;
	}
	
	public void setAppendixButtonImage(boolean hasAppendix) {
		if (hasAppendix) {
			buttonAppendix.setGraphic(appendixButtonImagePresent);
		}
		else {
			buttonAppendix.setGraphic(appendixButtonImage);
		}
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
		ReloadFileButtonCommand reloadFileButtonCommand = new ReloadFileButtonCommand(this);
		buttonReload.setOnAction(e -> reloadFileButtonCommand.execute());
		CloseButtonCommand closeButtonCommand = new CloseButtonCommand(this);
		buttonClose.setOnAction(e -> closeButtonCommand.execute());
		CloseAllButtonCommand closeAllButtonCommand = new CloseAllButtonCommand(this);
		buttonCloseAll.setOnAction(e -> closeAllButtonCommand.execute());
		AppendixButtonCommand appendixButtonCommand = new AppendixButtonCommand(this);
		buttonAppendix.setOnAction(e -> appendixButtonCommand.execute());
		SettingsButtonCommand settingsButtonCommand = new SettingsButtonCommand(this);
		buttonSettings.setOnAction(e -> settingsButtonCommand.execute());
		WordWrapToggleButtonCommand wordWrapToggleButtonCommand = new WordWrapToggleButtonCommand(this);
		toggleButtonWordWrap.setOnAction(e -> wordWrapToggleButtonCommand.execute());
	}
	
	private void addButtonTooltips() {
		Font tooltipFont = new Font(12);
		Tooltip newButtonTooltip = new Tooltip("New TODO (Ctrl + N)");
		newButtonTooltip.setFont(tooltipFont);
		buttonNew.setTooltip(newButtonTooltip);
		Tooltip openButtonTooltip = new Tooltip("Open TODO (Ctrl + O)");
		openButtonTooltip.setFont(tooltipFont);
		buttonOpen.setTooltip(openButtonTooltip);
		Tooltip saveButtonTooltip = new Tooltip("Save (Ctrl + S)");
		saveButtonTooltip.setFont(tooltipFont);
		buttonSave.setTooltip(saveButtonTooltip);
		Tooltip saveAllButtonTooltip = new Tooltip("Save All (Ctrl + Shift + S)");
		saveAllButtonTooltip.setFont(tooltipFont);
		buttonSaveAll.setTooltip(saveAllButtonTooltip);
		Tooltip reloadFileButtonTooltip = new Tooltip("Reload from Disk (Ctrl + R)");
		reloadFileButtonTooltip.setFont(tooltipFont);
		buttonReload.setTooltip(reloadFileButtonTooltip);
		Tooltip closeButtonTooltip = new Tooltip("Close (Ctrl + W)");
		closeButtonTooltip.setFont(tooltipFont);
		buttonClose.setTooltip(closeButtonTooltip);
		Tooltip closeAllTooltip = new Tooltip("Close All");
		closeAllTooltip.setFont(tooltipFont);
		buttonCloseAll.setTooltip(closeAllTooltip);
		Tooltip settingsTooltip = new Tooltip("Open Settings Dialog");
		settingsTooltip.setFont(tooltipFont);
		buttonSettings.setTooltip(settingsTooltip);
		Tooltip wordWrapTooltip = new Tooltip("Enable / Disable Word Wrap");
		wordWrapTooltip.setFont(tooltipFont);
		toggleButtonWordWrap.setTooltip(wordWrapTooltip);
		Tooltip searchTooltip = new Tooltip("Open Search Dialog (Ctrl + Shift + F)");
		searchTooltip.setFont(tooltipFont);
		buttonSearchDialog.setTooltip(searchTooltip);
	}
	
	private void addSearchCommands() {
		SearchDialogButtonCommand searchDialogButtonCommand = new SearchDialogButtonCommand(this);
		buttonSearchDialog.setOnAction(e -> searchDialogButtonCommand.execute());
		
		buttonSearchNext.setOnAction(e -> searchTool.getNextOccurenceButtonCommand().execute());
		buttonSearchPrevious.setOnAction(e -> searchTool.getPreviousOccurenceButtonCommand().execute());
		
		textAreaSearch.textProperty().addListener((observer, oldText, newText) -> searchTool.setSearchedPhrase(newText));
		textAreaSearch.setOnAction(e -> searchTool.toNextOccurrence());
		
		checkBoxRegex.setOnAction(e -> {
			searchTool.setUseRegexSearch(checkBoxRegex.isSelected());
			searchTool.highlightSearchedPhrase();
		});
	}
	
	private void addWindowClosingListeners() {
		Platform.runLater(() -> {
			getWindow().setOnCloseRequest(e -> {
				new SaveBeforeClosingCommand(e).execute();
				properties.setFiles(
						getAllTabControllers().stream().map(TodoTabController::getFile).filter(file -> file != null).collect(Collectors.toList()));
				properties.setWindowPosition(getWindow());
				properties.setAppendixWindowPosition(appendixController.getWindow());
				properties.setSelectedTab(getSelectedTabIndex());
				properties.setWordWrap(toggleButtonWordWrap.isSelected());
				properties.setAppendixFitImageSize(appendixController.isFitImageSize());
				
				properties.store();
				
				appendixController.hide();
			});
		});
	}
	
	private void adjustWindowPosition() {
		Platform.runLater(() -> {
			properties.adjustWindowPosition(getWindow());
			properties.adjustAppendixWindowPosition(appendixController.getWindow());
		});
	}
	
	private void chooseInitialSelectedTab() {
		setSelectedTab(properties.getSelectedTab());
	}
	
	private void setInitialToggleButtonStates() {
		if (properties.getWordWrap()) {
			toggleButtonWordWrap.setSelected(true);
			new WordWrapToggleButtonCommand(this).execute();
			
			appendixController.setFitImageSize(properties.getAppendixFitImageSize());
		}
	}
	
	private void createAppendixDialog() {
		appendixController = new TodoAppendixDialogController(this);
		try {
			URL fxmlUrl = getClass().getResource("/net/jfabricationgames/todo/frame/TodoAppendixDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			fxmlLoader.setController(appendixController);
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 1000, 600);
			String stylesheet = getClass().getResource("/net/jfabricationgames/todo/css/highlighting.css").toExternalForm();
			scene.getStylesheets().add(stylesheet);
			Stage stage = new Stage();
			stage.setTitle("TODO Appendix");
			stage.setScene(scene);
			stage.getIcons().add(new Image("/net/jfabricationgames/todo/frame/icon_todo.png"));
			
			appendixController.setStage(stage);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createTabChangeListener() {
		//update the appendix dialog when the selected tab changes
		tabView.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> appendixController.updateTodo(false));
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