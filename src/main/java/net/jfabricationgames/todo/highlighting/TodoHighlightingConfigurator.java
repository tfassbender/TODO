package net.jfabricationgames.todo.highlighting;

public class TodoHighlightingConfigurator extends HighlightingConfigurator {
	
	public enum PatternMatchers {
		
		HEADLINE("HEADLINE", "^# .*", "headline"), //
		HEADLINE_2("HEADLINE2", "^## .*", "headline2"), //
		HEADLINE_3("HEADLINE3", "^### .*", "headline3"), //
		HEADLINE_4("HEADLINE4", "^#### .*", "headline4"), //
		IMPORTANT("IMPORTANT", "^! .*", "important"), //
		COMMENT("COMMENT", "^\\/\\/ .*", "comment"), //
		DONE("DONE", "^\\/ .*", "done"), //
		QUESTIONABLE("QUESTIONABLE", "^\\? .*", "questionable"), //
		RESULT("RESULT", "^> .*", "result"), //
		SEARCHED("SEARCHED", "\\b(searched)\\b", "searched");
		
		private String name;
		private String regex;
		private String cssClass;
		
		private PatternMatchers(String name, String regex, String cssClass) {
			this.name = name;
			this.regex = regex;
			this.cssClass = cssClass;
		}
		
		public String getName() {
			return name;
		}
		
		public String getRegex() {
			return regex;
		}
		
		public String getCssClass() {
			return cssClass;
		}
	}
	
	public TodoHighlightingConfigurator() {
		for (PatternMatchers matcher : PatternMatchers.values()) {
			addHighlighting(matcher.getName(), matcher.getRegex(), matcher.getCssClass());
		}
	}
}
