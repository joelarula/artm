package website.model.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ClientCommand {
	
	HOME("index",Arrays.asList(new Route(Language.ET.getLocale(),"artmoments"),new Route(Language.EN.getLocale(),"artmoments")),"home"),
	PAINTINGS("index",Arrays.asList(new Route(Language.ET.getLocale(),"maalid"),new Route(Language.EN.getLocale(),"paintings")),"paintings"),
	SHOP("index",Arrays.asList(new Route(Language.ET.getLocale(),"pood"),new Route(Language.EN.getLocale(),"shop")),"shop"),
	CONTACT("index",Arrays.asList(new Route(Language.ET.getLocale(),"kontakt"),new Route(Language.EN.getLocale(),"contact")),"contact");

	private static final Logger logger = LoggerFactory.getLogger(ClientCommand.class);
	
	private final String page;
	private final List<Route> routes;
	private final String label;
	
	private Route command;

	
	ClientCommand(String page,List<Route> routes,String label){
		this.page = page;
		this.routes = routes;
		this.label = label;
	}
	
	public static ClientCommand findCandidate(String cmd, Locale locale) {
		//logger.trace("command {}, locale {}",cmd,locale.getLanguage());
		return Arrays.asList(ClientCommand.values()).stream().filter(cc -> cc.getContext(locale).getRoute().equals(cmd)).findFirst().get();
	}
	
	public String getPage() {
		return page;
	}
	
	public Route getContext(Locale locale) {
		//logger.trace("contextlang {}",locale.getLanguage());
		return this.routes.stream().filter(l -> locale.getLanguage().equals(l.getLocale().getLanguage())).findFirst().get();
	}
	
	public String getLabel() {
		return label;
	}

	public Route getCommand() {
		return command;
	}

	public void setCommand(Route command) {
		this.command = command;
	}
	
}
