package website.model.admin;

import java.util.Locale;

public class Route {
	
	@Override
	public String toString() {
		return "Route [locale=" + locale + ", route=" + route + "]";
	}

	private final Locale locale;
	
	private final  String route;
	
	public Route(Locale locale, String route) {
		this.locale = locale;
		this.route = route;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getRoute() {
		return route;
	}

	
}
