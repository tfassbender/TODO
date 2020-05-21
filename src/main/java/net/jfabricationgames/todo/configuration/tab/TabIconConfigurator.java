package net.jfabricationgames.todo.configuration.tab;

import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import net.jfabricationgames.todo.configuration.TabConfigurator;
import net.jfabricationgames.todo.frame.TodoTabController;

public class TabIconConfigurator implements TabConfigurator {
	
	private TodoTabController tabController;
	
	private static final String ICON_IMPORTANT = "/net/jfabricationgames/todo/tab/icon_important_small.png";
	private static final String ICON_QUESTION = "/net/jfabricationgames/todo/tab/icon_question_small.png";
	private static final String ICON_TODO = "/net/jfabricationgames/todo/tab/icon_todo_small.png";
	private static final String ICON_DONE = "/net/jfabricationgames/todo/tab/icon_done_small.png";
	
	public TabIconConfigurator(TodoTabController tabController) {
		this.tabController = tabController;
	}
	
	@Override
	public void configure(Tab tab) {
		new TodoTypeCounter().onCountChange(tabController.getCodeArea(), this::chooseTabIcon);
		if (tab != null) {
			tab.setGraphic(new ImageView(ICON_TODO));
		}
	}
	
	private void chooseTabIcon(TodoTypeCounter.TypeCount typeCount) {
		if (typeCount.importants > 0) {
			tabController.getTab().setGraphic(new ImageView(ICON_IMPORTANT));
		}
		else if (typeCount.questions > 0) {
			tabController.getTab().setGraphic(new ImageView(ICON_QUESTION));
		}
		else {
			int open = typeCount.total - typeCount.headlines - typeCount.comments - typeCount.results - typeCount.done - typeCount.striked;
			if (open > 0) {
				tabController.getTab().setGraphic(new ImageView(ICON_TODO));
			}
			else {
				tabController.getTab().setGraphic(new ImageView(ICON_DONE));
			}
		}
	}
}
