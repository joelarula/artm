package website.model.admin;

import java.util.Arrays;
import java.util.Locale;

public enum Language {
	
	ET(new Locale("et")),
	EN(new Locale("en")),
	RU(new Locale("ru"));
	
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

	public static Language get(Locale locale) {
		for(Language l : Language.values()){
			if(l.locale.equals(locale)){
				return l;
			}
		}
		return null;
	}
}
