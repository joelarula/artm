package website.model.admin;

import java.util.Arrays;
import java.util.Locale;

public enum Language {
	
	ET(new Locale("et")),
	EN(new Locale("en"));
	
	private final Locale locale;
	
	private Language(Locale locale){
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	public static boolean matchesAny(Locale locale) {
		return Arrays.asList(Language.values()).stream().filter(l-> l.getLocale().getLanguage().equals(locale.getLanguage())).count() == 1;
	}
}
