package net.jfabricationgames.todo.configuration.tab;

import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import net.jfabricationgames.todo.configuration.TabConfigurator;
import net.jfabricationgames.todo.frame.TodoTabController;

public class TabIconConfigurator implements TabConfigurator {
	
	private TodoTabController tabController;
	
	private static final ImageView ICON_IMPORTANT = new ImageView("/net/jfabricationgames/todo/tab/icon_important_small.png");
	private static final ImageView ICON_QUESTION = new ImageView("/net/jfabricationgames/todo/tab/icon_question_small.png");
	private static final ImageView ICON_TODO = new ImageView("/net/jfabricationgames/todo/tab/icon_todo_small.png");
	private static final ImageView ICON_DONE = new ImageView("/net/jfabricationgames/todo/tab/icon_done_small.png");
	
	public TabIconConfigurator(TodoTabController tabController) {
		this.tabController = tabController;
	}
	
	@Override
	public void configure(Tab tab) {
		new TodoTypeCounter().onCountChange(tabController.getCodeArea(), this::chooseTabIcon);
		if (tab != null) {
			tab.setGraphic(ICON_TODO);
		}
	}
	
	private void chooseTabIcon(TodoTypeCounter.TypeCount typeCount) {
		if (typeCount.importants > 0) {
			tabController.getTab().setGraphic(ICON_IMPORTANT);
		}
		else if (typeCount.questions > 0) {
			tabController.getTab().setGraphic(ICON_QUESTION);
		}
		else {
			int open = typeCount.total - typeCount.headlines - typeCount.comments - typeCount.results - typeCount.done;
			if (open > 0) {
				tabController.getTab().setGraphic(ICON_TODO);
			}
			else {
				tabController.getTab().setGraphic(ICON_DONE);
			}
		}
	}
}
