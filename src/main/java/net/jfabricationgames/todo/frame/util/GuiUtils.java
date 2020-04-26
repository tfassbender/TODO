package net.jfabricationgames.todo.frame.util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class GuiUtils {
	
	/**
	 * Load an fxml pane into an anchor pane with a loader object (any object) for the relative path
	 * 
	 * @param fxmlFileName
	 *        The path and name of the fxml file.
	 * @param parent
	 *        The parent {@link AnchorPane} in which the loaded content is added.
	 * @param controller
	 *        The {@link Initializable} that controls the loaded content.
	 * @param cssFileName
	 *        The name of a css file to style the loaded component (can be null if no styling is used)
	 * @param loader
	 *        An {@link Object} for a class loader (usually just 'this' from the calling context)
	 */
	public static void insertPane(String fxmlFileName, AnchorPane parent, Initializable controller, String cssFileName, Object loader) {
		try {
			URL fxmlUrl = loader.getClass().getResource(fxmlFileName);
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			fxmlLoader.setController(controller);
			Parent pane = fxmlLoader.load();
			if (cssFileName != null) {
				pane.getStylesheets().add(loader.getClass().getResource(cssFileName).toExternalForm());
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
	
	public static TodoTabController loadTab(TodoFrameController frameController, Object loader) {
		TodoTabController tabController = new TodoTabController(frameController);
		Tab tab = new Tab();
		AnchorPane tabContent = new AnchorPane();
		tab.setContent(tabContent);
		tabController.setTab(tab);
		GuiUtils.insertPane(TodoTabController.TODO_TAB_FXML, tabContent, tabController, null, loader);
		
		return tabController;
	}
}
