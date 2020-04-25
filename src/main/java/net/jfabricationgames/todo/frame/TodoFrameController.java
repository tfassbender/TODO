package net.jfabricationgames.todo.frame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class TodoFrameController implements Initializable {

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
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		insertTestTab();
	}

	private void insertTestTab() {
		TodoTabController controller = new TodoTabController();
		Tab tab = new Tab();
		AnchorPane tabContent = new AnchorPane();
		tab.setContent(tabContent);
		insertPane("/net/jfabricationgames/todo/frame/TodoTab.fxml", tabContent, controller, null, this);
		tabView.getTabs().add(tab);
		tab.setText("Tab Title");
	}
	
	/**
	 * Load an fxml pane into an anchor pane with a loader object (any object) for the relative path
	 */
	public void insertPane(String fxmlFileName, AnchorPane parent, Initializable controller, String cssFileName, Object loader) {
		try {
			URL fxmlUrl = loader.getClass().getResource(fxmlFileName);
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			fxmlLoader.setController(controller);
			Parent pane = fxmlLoader.load();
			if (cssFileName != null) {
				pane.getStylesheets().add(getClass().getResource(cssFileName).toExternalForm());
			}
			parent.getChildren().add(pane);
			AnchorPane.setBottomAnchor(pane, 0d);
			AnchorPane.setTopAnchor(pane, 0d);
			AnchorPane.setLeftAnchor(pane, 0d);
			AnchorPane.setRightAnchor(pane, 0d);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}