package net.jfabricationgames.todo.configuration.tab;

import java.util.Arrays;
import java.util.Collection;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;

import net.jfabricationgames.todo.configuration.highlighting.TodoHighlightingConfigurator;

/**
 * Count the number of important, questionable, done and open todos
 */
public class TodoTypeCounter {
	
	private TypeCount lastTypeCount = new TypeCount();
	
	private TodoTypePatternMatcher matcher;
	
	private TypeCountChangeListener onChange;
	
	public void onCountChange(CodeArea codeArea, TypeCountChangeListener onChange) {
		//create a new matcher
		matcher = new TodoTypePatternMatcher();
		//configure the matcher to listen to changes in the code area
		matcher.configure(codeArea);
		//remember the change listener
		this.onChange = onChange;
	}
	
	/**
	 * Called from the {@link TodoTypePatternMatcher} when a change in the {@link CodeArea} is noticed.
	 */
	private void countChanged() {
		if (!lastTypeCount.equals(matcher.getCount())) {
			lastTypeCount = matcher.getCount().clone();
			//notify the observer if there are changes in the code area
			onChange.receiveChange(lastTypeCount);
		}
	}
	
	//***********************************************************************************
	//*** classes
	//***********************************************************************************
	
	@FunctionalInterface
	public interface TypeCountChangeListener {
		
		public void receiveChange(TypeCount typeCount);
	}
	
	/**
	 * Simple counter for all highlighting types
	 */
	public class TypeCount {
		
		public int headlines;
		public int importants;
		public int questions;
		public int done;
		public int comments;
		public int results;
		public int striked;
		public int strikedComment;
		public int total;
		
		public TypeCount() {
			
		}
		
		public TypeCount(int headlines, int importants, int questions, int done, int comments, int results, int striked, int strikedComment,
				int total) {
			this.headlines = headlines;
			this.importants = importants;
			this.questions = questions;
			this.done = done;
			this.comments = comments;
			this.results = results;
			this.striked = striked;
			this.strikedComment = strikedComment;
			this.total = total;
		}
		
		public void reset() {
			headlines = 0;
			importants = 0;
			questions = 0;
			done = 0;
			comments = 0;
			results = 0;
			striked = 0;
			strikedComment = 0;
			total = 0;
		}
		
		public TypeCount clone() {
			return new TypeCount(headlines, importants, questions, done, comments, results, striked, strikedComment, total);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + comments;
			result = prime * result + done;
			result = prime * result + headlines;
			result = prime * result + importants;
			result = prime * result + questions;
			result = prime * result + results;
			result = prime * result + striked;
			result = prime * result + strikedComment;
			result = prime * result + total;
			return result;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof TypeCount) {
				return hashCode() == other.hashCode();
			}
			return super.equals(other);
		}
	}
	
	private class TodoTypePatternMatcher extends TodoHighlightingConfigurator {
		
		private TypeCount count = new TypeCount();
		
		@Override
		public void configure(CodeArea codeArea) {
			//compute the highlighting without setting it to the codeArea
			codeArea.textProperty().addListener((obs, oldText, newText) -> computeHighlighting(newText));
		}
		
		/**
		 * Count the matched groups
		 */
		@Override
		protected void groupMatched(String name) {
			if (name.equals(TodoHighlightingConfigurator.PatternMatchers.HEADLINE.getName())
					|| name.equals(TodoHighlightingConfigurator.PatternMatchers.HEADLINE_2.getName())
					|| name.equals(TodoHighlightingConfigurator.PatternMatchers.HEADLINE_3.getName())
					|| name.equals(TodoHighlightingConfigurator.PatternMatchers.HEADLINE_4.getName())) {
				count.headlines++;
			}
			else if (name.equals(TodoHighlightingConfigurator.PatternMatchers.IMPORTANT.getName())) {
				count.importants++;
			}
			else if (name.equals(TodoHighlightingConfigurator.PatternMatchers.QUESTIONABLE.getName())) {
				count.questions++;
			}
			else if (name.equals(TodoHighlightingConfigurator.PatternMatchers.DONE.getName())) {
				count.done++;
			}
			else if (name.equals(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getName())) {
				count.comments++;
			}
			else if (name.equals(TodoHighlightingConfigurator.PatternMatchers.RESULT.getName())) {
				count.results++;
			}
			else if (name.equals(TodoHighlightingConfigurator.PatternMatchers.STRIKED.getName())) {
				count.striked++;
			}
			else if (name.equals(TodoHighlightingConfigurator.PatternMatchers.STRIKED_COMMENT.getName())) {
				count.strikedComment++;
			}
		}
		
		@Override
		protected StyleSpans<Collection<String>> computeHighlighting(String text) {
			count.reset();
			count.total = (int) Arrays.stream(text.split("\n")).filter(line -> !line.trim().isEmpty()).count();
			StyleSpans<Collection<String>> computedHighlighting = super.computeHighlighting(text);
			//notify the TodoTypeCounter that the count has changed
			countChanged();
			return computedHighlighting;
		}
		
		public TypeCount getCount() {
			return count;
		}
	}
}
