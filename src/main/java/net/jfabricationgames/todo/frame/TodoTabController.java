package net.jfabricationgames.todo.frame;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.fxmisc.richtext.CodeArea;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import net.jfabricationgames.todo.highlighting.CodeAreaConfiguator;
import net.jfabricationgames.todo.highlighting.ParagraphConfigurator;
import net.jfabricationgames.todo.highlighting.TodoHighlightingConfigurator;

public class TodoTabController implements Initializable {
	
	@FXML
	private CodeArea codeArea;
	
	private static final List<CodeAreaConfiguator> configurators = Arrays.asList(new TodoHighlightingConfigurator(), new ParagraphConfigurator());
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configureCodeArea();
	}
	
	private void configureCodeArea() {
		for (CodeAreaConfiguator configurator : configurators) {
			configurator.configure(codeArea);
		}
	}
}
