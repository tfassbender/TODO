package net.jfabricationgames.todo.configuration.highlighting;

public class TodoSearchHighlightingConfigurator extends HighlightingConfigurator {
	
	private static final String SEARCHED_TEXT_REGEX_TEMPLATE = "\\b(_)\\b";//replace _ with searched text
	private static final String SEARCHED_TEXT_CSS_CLASS = "searched";
	private static final String NON_EXISTING_CSS_CLASS = "nonExistingCssClass";
	private static final String SEARCHED_TEXT_GROUP_NAME = "SEARCHED";
	
	private String searchedText;
	
	public String getSearchedText() {
		return searchedText;
	}
	public void setSearchedText(String searchedText) {
		this.searchedText = searchedText;
		String searchedTextRegex = SEARCHED_TEXT_REGEX_TEMPLATE.replace("_", searchedText);
		addHighlighting(SEARCHED_TEXT_GROUP_NAME, searchedTextRegex, SEARCHED_TEXT_CSS_CLASS);
	}
	public void removeSearchedText() {
		addHighlighting(SEARCHED_TEXT_GROUP_NAME, "", NON_EXISTING_CSS_CLASS);
	}
}
