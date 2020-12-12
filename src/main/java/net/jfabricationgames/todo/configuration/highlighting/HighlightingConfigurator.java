package net.jfabricationgames.todo.configuration.highlighting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
	
	/** matches the name of the highlighting component to it's regular expression (only inline styles) */
	private Map<String, String> inlineRegex = new HashMap<String, String>();
	/** matches the name of the highlighting to the css class (provided in src/main/resources/.../css/highlighting.css) (only inline styles) */
	private Map<String, String> inlineCssClasses = new HashMap<String, String>();
	
	/** the pattern that is used to match the highlighting */
	private Pattern pattern;
	/** the pattern that is used to match the inline highlighting */
	private Pattern inlinePattern;
	
	public HighlightingConfigurator() {
		compilePatterns();
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
		compilePatterns();
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
		compilePatterns();
	}
	
	/**
	 * Add a new inline highlighting to the configurator by providing a name, a regex and the css class
	 * 
	 * @param name
	 *        A name for the highlighting that has to be unique
	 * @param regex
	 *        The regular expression to match the highlighting
	 * @param cssClass
	 *        The css class for the highlighting (provided in src/main/resources/.../css/highlighting.css)
	 */
	public void addInlineHighlighting(String name, String regex, String cssClass) {
		this.inlineRegex.put(name, regex);
		this.inlineCssClasses.put(name, cssClass);
		compilePatterns();
	}
	
	/**
	 * Remove an inline highlighting from the configurator.
	 * 
	 * @param name
	 *        The name of the highlighting that is to be removed.
	 */
	public void removeInlineHighliting(String name) {
		this.inlineRegex.remove(name);
		this.inlineCssClasses.remove(name);
		compilePatterns();
	}
	
	/**
	 * Build and compile the pattern from the current maps.
	 */
	private void compilePatterns() {
		pattern = compilePattern(regex);
		inlinePattern = compilePattern(inlineRegex);
	}
	
	private Pattern compilePattern(Map<String, String> regex) {
		StringBuilder patternBuilder = new StringBuilder();
		for (Entry<String, String> highlighting : regex.entrySet()) {
			patternBuilder.append("(?<").append(highlighting.getKey()).append('>').append(highlighting.getValue()).append(")|");
		}
		//remove last '|' char
		if (patternBuilder.length() > 0) {
			patternBuilder.setLength(patternBuilder.length() - 1);
		}
		
		//compile the pattern
		return Pattern.compile(patternBuilder.toString(), Pattern.MULTILINE);
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
		List<PatternMatch> fullLineMatches = computeHighlighting(text, pattern, cssClasses);
		List<PatternMatch> inLineMatches = computeHighlighting(text, inlinePattern, inlineCssClasses);
		
		try {
			return mergeToStyleSpans(fullLineMatches, inLineMatches, text.length());
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return new StyleSpansBuilder<Collection<String>>().add(Collections.emptyList(), text.length()).create();
		}
	}
	
	private List<PatternMatch> computeHighlighting(String text, Pattern pattern, Map<String, String> cssClasses) {
		Matcher matcher = pattern.matcher(text);
		List<PatternMatch> matches = new ArrayList<>();
		
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
			
			matches.add(new PatternMatch(matcher.start(), matcher.end(), styleClass));
		}
		
		return matches;
	}
	
	/**
	 * ATTENTION: this merge will cause problems if more than two patterns overlap. The implementation is not needed yet because inline highlightings
	 * always end at the end of a line.
	 */
	private StyleSpans<Collection<String>> mergeToStyleSpans(List<PatternMatch> fullLineMatches, List<PatternMatch> inLineMatches, int textLength) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		
		int fullLineMatchesIndex = 0;
		int inLineMatchesIndex = 0;
		int lastMatchEnd = 0;
		boolean multipleInLineMatchesInFullLineMatch = false;
		
		while (fullLineMatches.size() > fullLineMatchesIndex || inLineMatches.size() > inLineMatchesIndex) {
			PatternMatch nextFullLineMatch = fullLineMatches.size() > fullLineMatchesIndex ? fullLineMatches.get(fullLineMatchesIndex) : null;
			PatternMatch nextInLineMatch = inLineMatches.size() > inLineMatchesIndex ? inLineMatches.get(inLineMatchesIndex) : null;
			PatternMatch nextMatch = null;
			
			boolean matchesIntersect = true;
			
			if (nextFullLineMatch != null && nextInLineMatch != null) {
				int Sf = nextFullLineMatch.startIndex;
				int Ef = nextFullLineMatch.endIndex;
				int Si = nextInLineMatch.startIndex;
				int Ei = nextInLineMatch.endIndex;
				String CssF = nextFullLineMatch.cssStyleClass;
				String CssI = nextInLineMatch.cssStyleClass;
				
				if (Si > Ef) {
					matchesIntersect = false;
					nextMatch = nextFullLineMatch;
					fullLineMatchesIndex++;
				}
				else if (Sf > Ei) {
					matchesIntersect = false;
					nextMatch = nextInLineMatch;
					inLineMatchesIndex++;
				}
				else {
					// the matches intersect
					// ...<Sf>...<Si>... (other cases can't happen, because Sf is always the start of the line and Si can't be on multiple lines)
					
					if (Sf - lastMatchEnd > 0) {
						spansBuilder.add(Collections.emptyList(), Sf - lastMatchEnd);
					}
					
					if (Ef == Ei) {
						// ...<Sf>...<Si>...<Ef, Ei>...
						if (multipleInLineMatchesInFullLineMatch) {
							spansBuilder.add(Collections.singleton(CssF), Si - lastMatchEnd);
						}
						else {
							spansBuilder.add(Collections.singleton(CssF), Si - Sf);
						}
						spansBuilder.add(Arrays.asList(CssF, CssI), Ei - Si);
						lastMatchEnd = Ei;
					}
					else {
						// ...<Sf>...<Si>...<Ei>...<Ef>...
						if (multipleInLineMatchesInFullLineMatch) {
							spansBuilder.add(Collections.singleton(CssF), Si - lastMatchEnd);
						}
						else {
							spansBuilder.add(Collections.singleton(CssF), Si - Sf);
						}
						spansBuilder.add(Arrays.asList(CssF, CssI), Ei - Si);
						lastMatchEnd = Ei;
					}
					// ...<Sf>...<Si>...<Ef>...<Ei>... can't happen because Ef will always be the end of the line and Ei can't be more than that
					
					inLineMatchesIndex++;
					if (inLineMatches.size() <= inLineMatchesIndex || inLineMatches.get(inLineMatchesIndex).startIndex > Ef) {
						//the next inline match is after this full line match or there is no next inline match
						spansBuilder.add(Collections.singleton(CssF), Ef - Ei);
						lastMatchEnd = Ef;
						fullLineMatchesIndex++;
						multipleInLineMatchesInFullLineMatch = false;
					}
					else {
						multipleInLineMatchesInFullLineMatch = true;
					}
				}
			}
			else {
				// one PatternMatch is null
				matchesIntersect = false;
				
				if (nextFullLineMatch != null) {
					nextMatch = nextFullLineMatch;
					
					fullLineMatchesIndex++;
				}
				else {
					nextMatch = nextInLineMatch;
					
					inLineMatchesIndex++;
				}
			}
			
			if (!matchesIntersect) {
				spansBuilder.add(Collections.emptyList(), nextMatch.startIndex - lastMatchEnd);
				spansBuilder.add(Collections.singleton(nextMatch.cssStyleClass), nextMatch.endIndex - nextMatch.startIndex);
				lastMatchEnd = nextMatch.endIndex;
			}
		}
		
		spansBuilder.add(Collections.emptyList(), textLength - lastMatchEnd);
		
		return spansBuilder.create();
	}
	
	protected void groupMatched(String name) {
		//can be used in subclasses
	}
	
	private class PatternMatch {
		
		public int startIndex;
		public int endIndex;
		public String cssStyleClass;
		
		public PatternMatch(int startIndex, int endIndex, String cssStyleClass) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.cssStyleClass = cssStyleClass;
		}
	}
}
