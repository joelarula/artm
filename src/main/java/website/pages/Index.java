package website.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.ioc.annotations.Inject;

import website.model.admin.ClientCommand;

public class Index {
	
	private ClientCommand command;
	
	@Inject
	private Block paintings;
	
	@Inject
	private Block home;
	
	@Inject
	private Block shop;
	
	@Inject
	private Block contact;
	
	
	void onActivate(EventContext context){
		this.command = ClientCommand.HOME;
		if(context.getCount() > 0){
			String cmd = context.get(String.class, 0).toUpperCase();
			this.command = ClientCommand.valueOf(cmd);
		}
	}
	 
	public String onPassivate() { return this.command.name().toLowerCase(); }
	
	public Block getActiveBlock(){
		switch (command){
			case HOME : return this.home;
			case PAINTINGS : return this.paintings;
			case SHOP : return this.shop;
			case CONTACT : return this.contact;
		}
		return this.paintings;
	}

	public ClientCommand getCommand() {
		return this.command;
	}
	
}
