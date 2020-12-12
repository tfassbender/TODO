package net.jfabricationgames.todo.configuration.highlighting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.fxmisc.richtext.model.StyleSpans;
import org.junit.jupiter.api.Test;

class HighlightingConfiguratorTest {
	
	//use a TodoHighlightingConfigurator for the tests because it sets the highlight configurations that are used for testing
	private HighlightingConfigurator configurator = new TodoHighlightingConfigurator();
	
	@Test
	public void testComputeHighlights_fullLineOnly() {
		String text = "\n\n# headline\n" // 2 \n + 10 characters + \n
				+ "/ done\n" // 6 characters + \n
				+ "// comment\n" // 10 characters + \n
				+ "? question\n" // 10 characters + \n
				+ "! important\n" // 11 characters + \n
				+ "no styling"; // 10 characters
		
		StyleSpans<Collection<String>> highlightings = configurator.computeHighlighting(text);
		
		int[] expectedLengths = new int[] { //
				2, 10, 1, //
				6, 1, //
				10, 1, //
				10, 1, //
				11, 11};
		
		List<List<String>> expectedCssConfigs = new ArrayList<>();
		expectedCssConfigs.addAll(Arrays.asList(//
				Collections.emptyList(), Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.HEADLINE.getCssClass()), Collections.emptyList(), //
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.DONE.getCssClass()), Collections.emptyList(), //
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()), Collections.emptyList(), //
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.QUESTIONABLE.getCssClass()), Collections.emptyList(), //
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.IMPORTANT.getCssClass()), Collections.emptyList() //
		));
		
		assertHighlightings(highlightings, expectedLengths, expectedCssConfigs);
	}
	
	@Test
	public void testComputeHighlights_inlineOnly() {
		String text = "\n\nno styling\n" // 2 \n + 10 characters + \n
				+ "**bold_full_line**\n" // 18 characters + \n
				+ "no styling ** bold ** no styling\n" // 11 characters + 10 characters + 11 characters + \n
				+ "no styling -- striked -- no styling\n" // 11 characters + 13 characters + 11 characters + \n
				+ "no styling\n\n\n"; // 10 characters + 3 \n 
		
		StyleSpans<Collection<String>> highlightings = configurator.computeHighlighting(text);
		
		int[] expectedLengths = new int[] { //
				13, //
				18, //
				12, 10, 11 + 1 + 11, //
				13, //
				11 + 1 + 10 + 3};
		
		List<List<String>> expectedCssConfigs = new ArrayList<>();
		expectedCssConfigs.addAll(Arrays.asList(//
				Collections.emptyList(), //
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.INLINE_BOLD.getCssClass()), //
				Collections.emptyList(), Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.INLINE_BOLD.getCssClass()),
				Collections.emptyList(), //
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.INLINE_STRIKED.getCssClass()), //
				Collections.emptyList() //
		));
		
		assertHighlightings(highlightings, expectedLengths, expectedCssConfigs);
	}
	
	@Test
	public void testComputeHighlights_fullLineAndInLine() {
		String text = "\n\n" // 2 \n
				+ "# headline\n" // 10 characters + \n
				+ "/ done with **bold inline**\n" // 12 characters + 15 characters + \n
				+ "// --striked within-- comment\n" // 3 characters + 18 characters + 8 characters + \n
				+ "no styling"; // 10 characters
		
		StyleSpans<Collection<String>> highlightings = configurator.computeHighlighting(text);
		
		int[] expectedLengths = new int[] { //
				2, //
				10, 1, //
				12, 15, 1, //
				3, 18, 8, // 
				11}; //
		List<List<String>> expectedCssConfigs = new ArrayList<>();
		
		expectedCssConfigs.addAll(Arrays.asList(//
				Collections.emptyList(), Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.HEADLINE.getCssClass()), Collections.emptyList(), //
				
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.DONE.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.DONE.getCssClass(),
						TodoHighlightingConfigurator.PatternMatchers.INLINE_BOLD.getCssClass()),
				Collections.emptyList(), //
				
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass(),
						TodoHighlightingConfigurator.PatternMatchers.INLINE_STRIKED.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()), //
				
				Collections.emptyList() //
		));
		
		assertHighlightings(highlightings, expectedLengths, expectedCssConfigs);
	}
	
	@Test
	public void testComputeHighlights_multipleInLineInFullLine() {
		String text = "\n\n" // 2 \n
				+ "# headline\n" // 10 characters + \n
				+ "// **bold within** comment and --striked within-- comment\n" // 3 characters + 15 characters + 13 characters + 18 characters + 8 characters + \n
				+ "no styling"; // 10 characters
		
		StyleSpans<Collection<String>> highlightings = configurator.computeHighlighting(text);
		
		int[] expectedLengths = new int[] { //
				2, //
				10, 1, //
				3, 15, 13, 18, 8, // 
				11}; //
		List<List<String>> expectedCssConfigs = new ArrayList<>();
		
		expectedCssConfigs.addAll(Arrays.asList(//
				Collections.emptyList(), Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.HEADLINE.getCssClass()), Collections.emptyList(), //
				
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass(),
						TodoHighlightingConfigurator.PatternMatchers.INLINE_BOLD.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass(),
						TodoHighlightingConfigurator.PatternMatchers.INLINE_STRIKED.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()), //
				
				Collections.emptyList() //
		));
		
		assertHighlightings(highlightings, expectedLengths, expectedCssConfigs);
	}
	
	@Test
	public void testComputeHighlights_multipleInLineInFullLine_endsWithInLine() {
		String text = "\n\n" // 2 \n
				+ "# headline\n" // 10 characters + \n
				+ "// **bold within** comment and --striked within--\n" // 3 characters + 15 characters + 13 characters + 18 characters + \n
				+ "no styling"; // 10 characters
		
		StyleSpans<Collection<String>> highlightings = configurator.computeHighlighting(text);
		
		int[] expectedLengths = new int[] { //
				2, //
				10, 1, //
				3, 15, 13, 18, // 
				11}; //
		List<List<String>> expectedCssConfigs = new ArrayList<>();
		
		expectedCssConfigs.addAll(Arrays.asList(//
				Collections.emptyList(), Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.HEADLINE.getCssClass()), Collections.emptyList(), //
				
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass(),
						TodoHighlightingConfigurator.PatternMatchers.INLINE_BOLD.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.COMMENT.getCssClass(),
						TodoHighlightingConfigurator.PatternMatchers.INLINE_STRIKED.getCssClass()), //
				
				Collections.emptyList() //
		));
		
		assertHighlightings(highlightings, expectedLengths, expectedCssConfigs);
	}
	
	@Test
	public void testComputeHighlights_inlineBetweenFullLineAndBoth() {
		String text = "# headline\n" // 10 characters + \n
				+ "**bold**\n" // 8 characters + \n
				+ "## --striked--"; // 3 characters + 11 characters
		
		StyleSpans<Collection<String>> highlightings = configurator.computeHighlighting(text);
		
		int[] expectedLengths = new int[] { //
				10, 1, //
				8, 1, //
				3, 11}; //
		List<List<String>> expectedCssConfigs = new ArrayList<>();
		
		expectedCssConfigs.addAll(Arrays.asList(//
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.HEADLINE.getCssClass()), Collections.emptyList(), //
				
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.INLINE_BOLD.getCssClass()), Collections.emptyList(), //
				
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.HEADLINE_2.getCssClass()),
				Arrays.asList(TodoHighlightingConfigurator.PatternMatchers.HEADLINE_2.getCssClass(),
						TodoHighlightingConfigurator.PatternMatchers.INLINE_STRIKED.getCssClass()) //
		));
		
		assertHighlightings(highlightings, expectedLengths, expectedCssConfigs);
	}
	
	private void assertHighlightings(StyleSpans<Collection<String>> highlightings, int[] expectedLengths, List<List<String>> expectedCssConfigs) {
		System.out.println("Highlightings: " + highlightings);
		assertEquals(expectedLengths.length, highlightings.getSpanCount(), "Number of highlightings differs");
		
		for (int i = 0; i < expectedLengths.length; i++) {
			assertEquals(expectedLengths[i], highlightings.getStyleSpan(i).getLength(), "Length differs at i=" + i);
			assertEquals(new HashSet<>(expectedCssConfigs.get(i)), new HashSet<>(highlightings.getStyleSpan(i).getStyle()),
					"Classes differ at i=" + i);
		}
	}
}
