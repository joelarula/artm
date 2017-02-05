package website.model.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ClientCommand {
	
	HOME("index",
		Arrays.asList(
			new Route(Language.ET.getLocale(),"kodu"),
			new Route(Language.EN.getLocale(),"home"),
			new Route(Language.RU.getLocale(),"home")),
		"home"),
	MODELS("index",
		Arrays.asList(
			new Route(Language.ET.getLocale(),"sepised"),
			new Route(Language.EN.getLocale(),"forgings"),
			new Route(Language.RU.getLocale(),"forgings")),
		"sepised");

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
		ClientCommand ccmd;
		try{
			ccmd = Arrays.asList(ClientCommand.values()).stream().filter(cc -> cc.getContext(locale).getRoute().equals(cmd)).findFirst().get();
		}catch(NoSuchElementException ex){
			logger.error(ex.getMessage());
			ccmd = ClientCommand.HOME;
		}
		return ccmd;
	}
	
	public String getPage() {
		return page;
	}
	
	public Route getContext(Locale locale) {
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
