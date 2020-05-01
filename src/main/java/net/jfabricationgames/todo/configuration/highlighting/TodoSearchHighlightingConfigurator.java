package net.jfabricationgames.todo.configuration.highlighting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.fxmisc.richtext.CodeArea;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class TodoSearchHighlightingConfigurator {
	
	private static final String CSS_CLASS_SEARCHED_TEXT = "searched";
	private static final String CSS_CLASS_NON_EXISTING = "nonExistingCssClass";
	private static final String CSS_CLASS_REGEX_ERROR = "regexError";
	
	//private static final String SEARCHED_TEXT_SURROUNDED_BY_BLANK_REGEX_TEMPLATE = "\\b(_)\\b";//replace _ with searched text
	private static final String NON_EXISTING_TEXT = "___some_random_text_that_probably_does_not_exist_in_the_code_area___";
	
	private Tooltip defaultTooltip;
	private Tooltip regexErrorTooltip;
	
	private String searchedText;
	
	private boolean useRegexSearch;
	
	private TodoFrameController controller;
	
	public TodoSearchHighlightingConfigurator(TodoFrameController controller) {
		this.controller = controller;
		
		defaultTooltip = new Tooltip("Search the current tab");
		defaultTooltip.setFont(new Font(12));
		regexErrorTooltip = new Tooltip("Syntax error in regular expression");
		regexErrorTooltip.setFont(new Font(12));
		
		controller.getTextAreaSearch().setTooltip(defaultTooltip);
	}
	
	//***********************************************************************************
	//*** properties
	//***********************************************************************************
	
	public String getSearchedText() {
		return searchedText;
	}
	public void setSearchedText(String searchedText) {
		this.searchedText = searchedText;
		highlightSearchedPhrase();
	}
	
	public boolean isUseRegexSearch() {
		return useRegexSearch;
	}
	public void setUseRegexSearch(boolean useRegexSearch) {
		this.useRegexSearch = useRegexSearch;
	}
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	public void highlightSearchedPhrase() {
		if (searchedText == null || searchedText.isEmpty()) {
			removeSearchedText();
		}
		else {
			highlightSearchedPhrase(searchedText, CSS_CLASS_SEARCHED_TEXT);
		}
	}
	
	public void removeSearchedText() {
		highlightSearchedPhrase(NON_EXISTING_TEXT, CSS_CLASS_NON_EXISTING);
	}
	
	//***********************************************************************************
	//*** private
	//***********************************************************************************
	
	private void highlightSearchedPhrase(String searchedTextRegex, String cssClass) {
		//remove the old highlighting
		removeSearchHighlighting();
		
		//disable regex in the searched text
		if (!useRegexSearch) {
			searchedTextRegex = Pattern.quote(searchedTextRegex);
		}
		
		//add the highlighting
		addHighlighting(searchedTextRegex, cssClass);
	}
	
	private void removeSearchHighlighting() {
		//change the text of the code area to recalculate the highlighting
		controller.getCurrentTabController().ifPresent(tabController -> {
			tabController.getCodeArea().insert(0, " ", "");
			tabController.getCodeArea().replace(0, 1, "", "");
		});
	}
	
	private void addHighlighting(String searchedTextRegex, String cssClass) {
		try {
			Pattern pattern = Pattern.compile(searchedTextRegex);
			controller.getTextAreaSearch().getStyleClass().remove(CSS_CLASS_REGEX_ERROR);//reset to default style
			controller.getTextAreaSearch().setTooltip(defaultTooltip);
			controller.getCurrentTabController().ifPresent(tabController -> {
				CodeArea codeArea = tabController.getCodeArea();
				String text = codeArea.getText();
				Matcher matcher = pattern.matcher(text);
				
				while (matcher.find()) {
					List<String> currentStyle = new ArrayList<>(codeArea.getStyleAtPosition(matcher.start()));
					currentStyle.add(cssClass);
					codeArea.setStyle(matcher.start(), matcher.end(), currentStyle);
				}
			});
		}
		catch (PatternSyntaxException pse) {
			String syntaxErrorHint = pse.getMessage();
			regexErrorTooltip.setText("Syntax error in regular expression:\n" + syntaxErrorHint);
			//set the search bar style to indicate the false regex syntax
			TextField searchBar = controller.getTextAreaSearch();
			if (!searchBar.getStyleClass().contains(CSS_CLASS_REGEX_ERROR)) {
				searchBar.getStyleClass().add(CSS_CLASS_REGEX_ERROR);
			}
			searchBar.setTooltip(regexErrorTooltip);
		}
	}
}
