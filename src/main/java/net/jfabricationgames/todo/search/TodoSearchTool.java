package net.jfabricationgames.todo.search;

import java.util.Optional;

import org.fxmisc.richtext.CodeArea;

import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.commands.button.SearchNextButtonCommand;
import net.jfabricationgames.todo.commands.button.SearchPreviousButtonCommand;
import net.jfabricationgames.todo.configuration.highlighting.TodoSearchHighlightingConfigurator;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoTabController;

public class TodoSearchTool {
	
	private String searchedPhrase;
	
	private final ButtonCommand nextOccurenceButtonCommand;
	private final ButtonCommand previousOccurenceButtonCommand;
	
	private TodoFrameController controller;
	
	private TodoSearchHighlightingConfigurator highlightingConfigurator;
	
	public TodoSearchTool(TodoFrameController controller) {
		this.controller = controller;
		nextOccurenceButtonCommand = new SearchNextButtonCommand(controller, this);
		previousOccurenceButtonCommand = new SearchPreviousButtonCommand(controller, this);
		highlightingConfigurator = new TodoSearchHighlightingConfigurator(controller);
	}
	
	//***********************************************************************************
	//*** properties
	//***********************************************************************************
	
	public String getSearchedPhrase() {
		return searchedPhrase;
	}
	
	public void setSearchedPhrase(String searchedPhrase) {
		this.searchedPhrase = searchedPhrase;
		highlightingConfigurator.setSearchedText(searchedPhrase);
	}
	
	public ButtonCommand getNextOccurenceButtonCommand() {
		return nextOccurenceButtonCommand;
	}
	
	public ButtonCommand getPreviousOccurenceButtonCommand() {
		return previousOccurenceButtonCommand;
	}
	
	public boolean isUseRegexSearch() {
		return highlightingConfigurator.isUseRegexSearch();
	}
	public void setUseRegexSearch(boolean useRegexSearch) {
		highlightingConfigurator.setUseRegexSearch(useRegexSearch);
	}
	
	//***********************************************************************************
	//*** public
	//***********************************************************************************
	
	public void highlightSearchedPhrase() {
		highlightingConfigurator.highlightSearchedPhrase();
	}
	
	/**
	 * Set the cursor position to the next occurrence of the current searched string
	 */
	public void toNextOccurrence() {
		int nextOccurence = findOccurrence(true);
		highlightingConfigurator.highlightSearchedPhrase();
		setSelectedRange(nextOccurence, searchedPhrase.length());
	}
	
	/**
	 * Set the cursor position to the previous occurrence of the current searched string
	 */
	public void toPreviousOccurrence() {
		int nextOccurence = findOccurrence(false);
		highlightingConfigurator.highlightSearchedPhrase();
		setSelectedRange(nextOccurence, searchedPhrase.length());
	}
	
	//***********************************************************************************
	//*** private
	//***********************************************************************************
	
	private void setSelectedRange(int pos, int length) {
		if (pos != -1) {
			Optional<TodoTabController> tabControllerOptional = controller.getCurrentTabController();
			tabControllerOptional.ifPresent(tabController -> {
				tabController.getCodeArea().selectRange(pos, pos + length);
				tabController.requestFocusOnCodeArea();
				tabController.getCodeArea().requestFollowCaret();
			});
		}
	}
	
	private int findOccurrence(boolean searchForward) {
		if (searchedPhrase != null && !searchedPhrase.isEmpty()) {
			Optional<TodoTabController> tabControllerOptional = controller.getCurrentTabController();
			if (tabControllerOptional.isPresent()) {
				TodoTabController tabController = tabControllerOptional.get();
				CodeArea codeArea = tabController.getCodeArea();
				
				String text = codeArea.getText();
				int cursorPosition = codeArea.getCaretPosition();
				if (searchForward) {
					cursorPosition++;//increase the cursor position to not find the same phrase again				
				}
				else {
					cursorPosition -= searchedPhrase.length() + 1;//decrease the cursor position to not find the same phrase again
				}
				
				if (cursorPosition > text.length()) {
					cursorPosition = 0;
				}
				if (cursorPosition <= 0) {
					cursorPosition = text.length();
				}
				
				int nextOccurrence;
				if (searchForward) {
					nextOccurrence = text.indexOf(searchedPhrase, cursorPosition);
					if (nextOccurrence == -1) {
						nextOccurrence = text.indexOf(searchedPhrase);
					}
				}
				else {
					nextOccurrence = text.lastIndexOf(searchedPhrase, cursorPosition);
					if (nextOccurrence == -1) {
						nextOccurrence = text.lastIndexOf(searchedPhrase);
					}
				}
				
				return nextOccurrence;
			}
		}
		return -1;
	}
}
