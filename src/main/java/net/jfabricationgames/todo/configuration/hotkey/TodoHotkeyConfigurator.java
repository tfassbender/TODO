package net.jfabricationgames.todo.configuration.hotkey;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import net.jfabricationgames.todo.commands.button.CloseButtonCommand;
import net.jfabricationgames.todo.commands.button.NewButtonCommand;
import net.jfabricationgames.todo.commands.button.OpenButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveAllButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveButtonCommand;
import net.jfabricationgames.todo.commands.hotkey.DeleteLineCommand;
import net.jfabricationgames.todo.commands.hotkey.SwitchTabBeforeCommand;
import net.jfabricationgames.todo.commands.hotkey.SwitchTabNextCommand;
import net.jfabricationgames.todo.commands.hotkey.ToSearchbarCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;

public class TodoHotkeyConfigurator extends HotkeyConfigurator {
	
	public TodoHotkeyConfigurator(TodoFrameController controller) {
		addTodoHotkeys(controller);
	}
	
	private void addTodoHotkeys(TodoFrameController controller) {
		//control buttons
		addHotkey("SAVE", new HotkeyCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), new SaveButtonCommand(controller));
		addHotkey("SAVE_ALL", new HotkeyCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new SaveAllButtonCommand(controller));
		addHotkey("NEW", new HotkeyCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), new NewButtonCommand(controller));
		addHotkey("OPEN", new HotkeyCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), new OpenButtonCommand(controller));
		addHotkey("CLOSE_TAB", new HotkeyCombination(KeyCode.W, KeyCombination.CONTROL_DOWN), new CloseButtonCommand(controller));
		
		//editor controls
		addHotkey("DELETE_LINE", new HotkeyCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), new DeleteLineCommand(controller));
		addHotkey("SWITCH_TAB_NEXT", new HotkeyCombination(KeyCode.PAGE_DOWN, KeyCombination.CONTROL_DOWN), new SwitchTabNextCommand(controller));
		addHotkey("SWITCH_TAB_BEFORE", new HotkeyCombination(KeyCode.PAGE_UP, KeyCombination.CONTROL_DOWN), new SwitchTabBeforeCommand(controller));
		addHotkey("FIND", new HotkeyCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), new ToSearchbarCommand(controller));
	}
}
