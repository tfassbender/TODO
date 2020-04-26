package net.jfabricationgames.todo.frame;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import net.jfabricationgames.todo.commands.button.CloseAllButtonCommand;
import net.jfabricationgames.todo.commands.button.CloseButtonCommand;
import net.jfabricationgames.todo.commands.button.NewButtonCommand;
import net.jfabricationgames.todo.commands.button.OpenButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveAllButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveButtonCommand;
import net.jfabricationgames.todo.commands.button.SettingsButtonCommand;

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
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		insertInitialTab();
		addButtonCommands();
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
	public Window getStage() {
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
	
	private void insertInitialTab() {
		new NewButtonCommand(this).execute();
		requestFocusOnCurrentCodeArea();
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
}