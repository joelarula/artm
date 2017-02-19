package website.components;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;

import website.model.admin.AdminCommand;
import website.model.admin.ClientCommand;
import website.model.admin.Language;
import website.pages.Index;
import website.pages.admin.Board;

@Import(module = {"bootstrap/dropdown","screen"},stylesheet="base/css/ClientFrame.css")
public class ClientFrame {

	
	@Inject
	private PersistentLocale persistentLocale;
	
	@InjectPage
	private Index index;
	
	@Property
	private Locale locale;
	
	@Property
	private ClientCommand command;
	
	@Property
	private ClientCommand homeCommand = ClientCommand.HOME;
	
	@Inject
	private Messages messages;
	
	
	public String getCurrentLocaleLabel(){
		return this.messages.get(this.persistentLocale.get().getLanguage());
	}
	
	public String getCurrentLocale(){
		return this.locale.getLanguage();
	}
	
	public String getSetLocale(){
		return this.persistentLocale.get().getLanguage();
	}
	
	public String getLocaleLabel(){
		return messages.get(this.locale.getLanguage());
	}
	
	public List<Locale> getLocales(){
		return Arrays.asList(Language.values()).stream().map(l -> l.getLocale())
		.filter(l-> !l.equals(persistentLocale.get()))
		.collect(Collectors.toList());
	}
	
	@OnEvent("toggleLanguage")
	public void onToggleLanguage(String locale){
		Locale l = new Locale(locale);
		if(Language.matchesAny(l)){
			this.persistentLocale.set(l);
		}
		
	}
	
	public String getActiveMenuItem() { 
		return this.index.getCommand().equals(command) ? "active":"idle";
	}
	
	public String getLabel(){
		return messages.get(this.command.getLabel());
	}
	
	public String getHomeCommandContext(){
		return this.homeCommand.getContext(this.persistentLocale.get()).getRoute();
	}
	
	public String getCommandContext(){
		return this.command.getContext(this.persistentLocale.get()).getRoute();
	}
	
}
