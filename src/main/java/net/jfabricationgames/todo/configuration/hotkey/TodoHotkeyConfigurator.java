package net.jfabricationgames.todo.configuration.hotkey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import net.jfabricationgames.todo.commands.button.CloseButtonCommand;
import net.jfabricationgames.todo.commands.button.NewButtonCommand;
import net.jfabricationgames.todo.commands.button.OpenButtonCommand;
import net.jfabricationgames.todo.commands.button.ReloadFileButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveAllButtonCommand;
import net.jfabricationgames.todo.commands.button.SaveButtonCommand;
import net.jfabricationgames.todo.commands.hotkey.DeleteLineCommand;
import net.jfabricationgames.todo.commands.hotkey.EditorHighlightingCommand;
import net.jfabricationgames.todo.commands.hotkey.MoveTabCommand;
import net.jfabricationgames.todo.commands.hotkey.SwitchTabBeforeCommand;
import net.jfabricationgames.todo.commands.hotkey.SwitchTabNextCommand;
import net.jfabricationgames.todo.commands.hotkey.ToSearchbarCommand;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.TodoFrameController.Direction;

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
		addHotkey("RELOAD", new HotkeyCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), new ReloadFileButtonCommand(controller));
		addHotkey("CLOSE_TAB", new HotkeyCombination(KeyCode.W, KeyCombination.CONTROL_DOWN), new CloseButtonCommand(controller));
		
		//editor controls
		addHotkey("DELETE_LINE", new HotkeyCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), new DeleteLineCommand(controller));
		addHotkey("SWITCH_TAB_NEXT", new HotkeyCombination(KeyCode.PAGE_DOWN, KeyCombination.CONTROL_DOWN), new SwitchTabNextCommand(controller));
		addHotkey("SWITCH_TAB_BEFORE", new HotkeyCombination(KeyCode.PAGE_UP, KeyCombination.CONTROL_DOWN), new SwitchTabBeforeCommand(controller));
		addHotkey("MOVE_TAB_RIGHT", new HotkeyCombination(KeyCode.PAGE_DOWN, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new MoveTabCommand(controller, Direction.RIGHT));
		addHotkey("MOVE_TAB_LEFT", new HotkeyCombination(KeyCode.PAGE_UP, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new MoveTabCommand(controller, Direction.LEFT));
		addHotkey("FIND", new HotkeyCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), new ToSearchbarCommand(controller));
		
		//editor highlighting controls
		addHotkey("MARK_ROWS_DONE", new HotkeyCombination(KeyCode.D, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.DONE));
		addHotkey("MARK_ROWS_COMMENT", new HotkeyCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.COMMENT));
		addHotkey("MARK_ROWS_IMPORTANT", new HotkeyCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.IMPORTANT));
		addHotkey("MARK_ROWS_QUESTION", new HotkeyCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.QUESTION));
		addHotkey("MARK_ROWS_HEADLINE_1", new HotkeyCombination(KeyCode.NUMPAD1, KeyCombination.CONTROL_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.HEADLINE_1));
		addHotkey("MARK_ROWS_HEADLINE_2", new HotkeyCombination(KeyCode.NUMPAD2, KeyCombination.CONTROL_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.HEADLINE_2));
		addHotkey("MARK_ROWS_HEADLINE_3", new HotkeyCombination(KeyCode.NUMPAD3, KeyCombination.CONTROL_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.HEADLINE_3));
		addHotkey("MARK_ROWS_HEADLINE_4", new HotkeyCombination(KeyCode.NUMPAD4, KeyCombination.CONTROL_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.HEADLINE_4));
		addHotkey("MARK_ROWS_STRIKED", new HotkeyCombination(KeyCode.X, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.STRIKED));
		addHotkey("MARK_ROWS_STRIKED_COMMENT", new HotkeyCombination(KeyCode.V, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
				new EditorHighlightingCommand(controller, EditorHighlightingCommand.HighlightingType.STRIKED_COMMENT));
	}
	
	public String getDescriptionText() {
		final List<String> lines = new ArrayList<String>();
		final String format = "%s -> %s";
		
		for (Entry<String, HotkeyCombination> hotkey : hotkeyCombinations.entrySet()) {
			String name = hotkey.getKey();
			HotkeyCombination keyCombination = hotkey.getValue();
			String keyCombinationString = "";
			for (Modifier modifier : keyCombination.getModifiers()) {
				keyCombinationString += modifier.getKey().getName() + " + ";
			}
			keyCombinationString += keyCombination.getKeyCode().getName();
			lines.add(String.format(format, name, keyCombinationString));
		}
		Collections.sort(lines);
		
		return lines.stream().collect(Collectors.joining("\n"));
	}
}
