package net.jfabricationgames.todo.configuration.highlighting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import net.jfabricationgames.todo.configuration.CodeAreaConfiguator;

public class HighlightingConfigurator implements CodeAreaConfiguator {
	
	/** matches the name of the highlighting component to it's regular expression */
	private Map<String, String> regex = new HashMap<String, String>();
	/** matches the name of the highlighting to the css class (provided in src/main/resources/.../css/highlighting.css) */
	private Map<String, String> cssClasses = new HashMap<String, String>();
	
	/** the pattern that is used to match the highlighting */
	private Pattern pattern;
	
	public HighlightingConfigurator() {
		compilePattern();
	}
	
	/**
	 * Configure the {@link CodeArea} to enable custom highlighting.
	 * 
	 * @param codeArea
	 *        The {@link CodeArea} that is configured.
	 */
	@Override
	public void configure(CodeArea codeArea) {
		codeArea.textProperty().addListener((obs, oldText, newText) -> codeArea.setStyleSpans(0, computeHighlighting(newText)));
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	/**
	 * Add a new highlighting to the configurator by providing a name, a regex and the css class
	 * 
	 * @param name
	 *        A name for the highlighting that has to be unique
	 * @param regex
	 *        The regular expression to match the highlighting
	 * @param cssClass
	 *        The css class for the highlighting (provided in src/main/resources/.../css/highlighting.css)
	 */
	public void addHighlighting(String name, String regex, String cssClass) {
		this.regex.put(name, regex);
		this.cssClasses.put(name, cssClass);
		compilePattern();
	}
	
	/**
	 * Remove a highlighting from the configurator.
	 * 
	 * @param name
	 *        The name of the highlighting that is to be removed.
	 */
	public void removeHighliting(String name) {
		this.regex.remove(name);
		this.cssClasses.remove(name);
		compilePattern();
	}
	
	/**
	 * Build and compile the pattern from the current maps.
	 */
	private void compilePattern() {
		StringBuilder patternBuilder = new StringBuilder();
		for (Entry<String, String> highlighting : regex.entrySet()) {
			patternBuilder.append("(?<").append(highlighting.getKey()).append('>').append(highlighting.getValue()).append(")|");
		}
		//remove last '|' char
		if (patternBuilder.length() > 0) {
			patternBuilder.setLength(patternBuilder.length() - 1);
		}
		
		//compile the pattern
		pattern = Pattern.compile(patternBuilder.toString(), Pattern.MULTILINE);
	}
	
	/**
	 * Compute the customized highlighting.
	 * 
	 * @param text
	 *        The new text of the {@link CodeArea}.
	 * 		
	 * @return The {@link StyleSpans} for styling the {@link CodeArea}'s text
	 */
	protected StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = pattern.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = null;
			
			//search for the group that is matched
			for (Entry<String, String> highlighting : cssClasses.entrySet()) {
				String name = highlighting.getKey();
				String cssClass = highlighting.getValue();
				
				//if a group is matched -> set the style class
				if (matcher.group(name) != null) {
					groupMatched(name);
					styleClass = cssClass;
				}
			}
			
			//should always be found, but ...
			if (styleClass != null) {
				spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
				spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
				lastKwEnd = matcher.end();
			}
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
	
	protected void groupMatched(String name) {
		//can be used in subclasses
	}
}
