package net.jfabricationgames.todo.configuration.hotkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.commands.hotkey.EmptyCommand;
import net.jfabricationgames.todo.configuration.CodeAreaConfiguator;

public class HotkeyConfigurator implements CodeAreaConfiguator {
	
	/** maps the name of the hotkey command to the keys that have to be pressed as a {@link HotkeyCombination} */
	protected Map<String, HotkeyCombination> hotkeyCombinations = new HashMap<String, HotkeyCombination>();
	/** maps the name of the hotkey to the {@link ButtonCommand} that is executed */
	protected Map<String, ButtonCommand> commands = new HashMap<String, ButtonCommand>();
	/** a list of hotkey combinations that are removed from the code area by setting them to empty commands */
	private List<HotkeyCombination> combinationsToRemove = new ArrayList<HotkeyCombination>();
	
	@Override
	public void configure(CodeArea codeArea) {
		addHotkeys(codeArea);
		removeHotkeys(codeArea);
	}
	
	/**
	 * Add a hotkey to the configurator that will be added to the code area when the <code>configure(CodeArea)</code> method is called.
	 * 
	 * @param name
	 *        A unique name for the hotkey
	 * @param hotkeyCombination
	 *        The key combination that calls the hotkey command
	 * @param command
	 *        The command that is to be executed when the hotkey is pressed as a {@link ButtonCommand}
	 */
	public void addHotkey(String name, HotkeyCombination hotkeyCombination, ButtonCommand command) {
		this.hotkeyCombinations.put(name, hotkeyCombination);
		this.commands.put(name, command);
	}
	
	/**
	 * Remove a key combination from the code area. The hotkey combination is removed by overriding it to a new, empty command. The hotkey is removed
	 * when the <code>configure(CodeArea)</code> method is called.
	 * 
	 * @param hotkeyCombination
	 *        The key combination that is to be removed.
	 */
	public void removeHotkey(HotkeyCombination hotkeyCombination) {
		combinationsToRemove.add(hotkeyCombination);
	}
	
	/**
	 * Add all hotkeys to the code area
	 */
	private void addHotkeys(CodeArea codeArea) {
		for (String hotkeyName : hotkeyCombinations.keySet()) {
			HotkeyCombination keyCombination = hotkeyCombinations.get(hotkeyName);
			ButtonCommand command = commands.get(hotkeyName);
			
			addHotkey(codeArea, keyCombination, command);
		}
	}
	
	/**
	 * Remove hotkeys that are listed as "to be removed" by overriding them with empty commands
	 */
	private void removeHotkeys(CodeArea codeArea) {
		for (HotkeyCombination combination : combinationsToRemove) {
			
			addHotkey(codeArea, combination, new EmptyCommand());
		}
	}
	
	private void addHotkey(CodeArea codeArea, HotkeyCombination keyCombination, ButtonCommand command) {
		Nodes.addInputMap(codeArea, // 
				InputMap.consume(EventPattern.keyPressed(keyCombination.getKeyCode(), keyCombination.getModifiers()), // the keys that have to be pressed
						e -> command.execute())); // the command that is executed
	}
}
