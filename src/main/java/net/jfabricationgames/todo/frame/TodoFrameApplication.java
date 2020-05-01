package net.jfabricationgames.todo.frame;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TodoFrameApplication extends Application {

	public static final String APPLCIATION_NAME = "TODO";
	
	private TodoFrameController controller;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		controller = new TodoFrameController();
		try {
			URL fxmlUrl = getClass().getResource("/net/jfabricationgames/todo/frame/TodoFrame.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			fxmlLoader.setController(controller);
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 1000, 600);
			String stylesheet = getClass().getResource("/net/jfabricationgames/todo/css/highlighting.css").toExternalForm();
			scene.getStylesheets().add(stylesheet);
			primaryStage.setTitle(APPLCIATION_NAME);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("/net/jfabricationgames/todo/frame/icon_todo.png"));
			primaryStage.show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
