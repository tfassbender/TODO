package net.jfabricationgames.todo.configuration.highlighting;

public class TodoHighlightingConfigurator extends HighlightingConfigurator {
	
	public enum PatternMatchers {
		
		HEADLINE("HEADLINE", "^# .*", "headline", false), //
		HEADLINE_2("HEADLINE2", "^## .*", "headline2", false), //
		HEADLINE_3("HEADLINE3", "^### .*", "headline3", false), //
		HEADLINE_4("HEADLINE4", "^#### .*", "headline4", false), //
		IMPORTANT("IMPORTANT", "^( |\\t)*! .*", "important", false), //
		COMMENT("COMMENT", "^( |\\t)*\\/\\/ .*", "comment", false), //
		DONE("DONE", "^( |\\t)*\\/ .*", "done", false), //
		QUESTIONABLE("QUESTIONABLE", "^( |\\t)*\\? .*", "questionable", false), //
		RESULT("RESULT", "^( |\\t)*> .*", "result", false), //
		STRIKED("STRIKED", "^( |\\t)*\\/- .*", "striked", false), //
		STRIKED_COMMENT("STRIKEDCOMMENT", "^( |\\t)*\\/\\/- .*", "striked-comment", false), INLINE_BOLD("INLINEBOLD", "\\*\\*[^\\n\\*\\*]+\\*\\*",
				"inline-bold", true), INLINE_STRIKED("INLINESTRIKED", "--[^\\n\\-\\-]+--", "inline-striked", true);
		
		private final String name;
		private final String regex;
		private final String cssClass;
		private final boolean inline;
		
		private PatternMatchers(String name, String regex, String cssClass, boolean inline) {
			this.name = name;
			this.regex = regex;
			this.cssClass = cssClass;
			this.inline = inline;
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
		
		public boolean isInline() {
			return inline;
		}
	}
	
	public TodoHighlightingConfigurator() {
		for (PatternMatchers matcher : PatternMatchers.values()) {
			if (!matcher.isInline()) {
				addHighlighting(matcher.getName(), matcher.getRegex(), matcher.getCssClass());
			}
			else {
				addInlineHighlighting(matcher.getName(), matcher.getRegex(), matcher.getCssClass());
			}
		}
	}
}
