package net.jfabricationgames.todo.configuration;

import org.fxmisc.richtext.CodeArea;

public interface CodeAreaConfiguator {
	
	/**
	 * Configure the {@link CodeArea}.
	 * 
	 * @param codeArea The {@link CodeArea} that is configured.
	 */
	public void configure(CodeArea codeArea);
}
