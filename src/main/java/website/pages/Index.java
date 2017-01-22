package website.pages;

import java.util.Locale;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;

import website.model.admin.ClientCommand;
import website.model.admin.Language;

public class Index {
	
	private ClientCommand command;
	
	@Inject
	private Block models;
	
	@Inject
	private Block home;
	
	
	@Inject
	private PersistentLocale persistentLocale;
	
	
	void onActivate(EventContext context){
		
		Locale locale = this.persistentLocale.get() != null ? this.persistentLocale.get() : Language.ET.getLocale();
		
		this.command = ClientCommand.HOME;
		if(context.getCount() > 0){
			this.command = ClientCommand.findCandidate(context.get(String.class, 0),locale);
		}		
		persistentLocale.set(locale);
	}
	 
	public String onPassivate() { return this.command.getContext(this.persistentLocale.get()).getRoute(); }
	
	public Block getActiveBlock(){
		switch (command){
			case HOME : return this.home;
			case MODELS : return this.models;
		}
		return this.models;
	}

	public ClientCommand getCommand() {
		return this.command;
	}
	
}
