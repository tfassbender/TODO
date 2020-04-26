package net.jfabricationgames.todo.configuration.highlighting;

import java.util.function.IntFunction;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import net.jfabricationgames.todo.configuration.CodeAreaConfiguator;

public class ParagraphConfigurator implements CodeAreaConfiguator {
	
	private static IntFunction<String> format = (digits -> " %" + digits + "d ");

	/**
	 * Configure the {@link CodeArea} to add line numbers.
	 * 
	 * @param codeArea The {@link CodeArea} that is configured.
	 */
	@Override
	public void configure(CodeArea codeArea) {
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea, format));
	}
}
