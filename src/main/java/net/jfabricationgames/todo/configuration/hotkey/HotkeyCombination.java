package net.jfabricationgames.todo.configuration.hotkey;

import java.util.Arrays;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;

/**
 * A combination of keys for a hotkey-command
 */
public class HotkeyCombination {
	
	private final KeyCode keyCode;
	private final KeyCombination.Modifier[] modifiers;
	
	public HotkeyCombination(KeyCode keyCode, Modifier... modifiers) {
		this.keyCode = keyCode;
		this.modifiers = modifiers;
	}
	
	public KeyCode getKeyCode() {
		return keyCode;
	}
	
	public KeyCombination.Modifier[] getModifiers() {
		return modifiers;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyCode == null) ? 0 : keyCode.hashCode());
		result = prime * result + Arrays.hashCode(modifiers);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HotkeyCombination other = (HotkeyCombination) obj;
		if (keyCode != other.keyCode)
			return false;
		if (!Arrays.equals(modifiers, other.modifiers))
			return false;
		return true;
	}
}
