package net.jfabricationgames.todo.configuration;

import javafx.scene.control.Tab;

public interface TabConfigurator {
	
	/**
	 * Configure the {@link Tab}
	 * 
	 * @param tab
	 *        The {@link Tab} that is configured.
	 */
	public void configure(Tab tab);
}
