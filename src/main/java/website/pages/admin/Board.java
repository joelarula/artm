package website.pages.admin;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import website.model.admin.AdminCommand;

public class Board {

	private AdminCommand command;
		
	@Inject
	private Block paintings;
	
	@Inject
	private Block painting;
	
	@Inject
	private Block settings;
	
	void onActivate(EventContext context){
		this.command = AdminCommand.PAINTING;
		if(context.getCount() > 0){
			String cmd = context.get(String.class, 0).toUpperCase();
			this.command = AdminCommand.valueOf(cmd);
		}
	}
	 
	public String onPassivate() { return this.command.name().toLowerCase(); }
	
	public Block getActiveBlock(){
		switch (command){
			case PAINTING : return this.painting;
			case PAINTINGS : return this.paintings;
			case SETTINGS : return this.settings;
		}
		return this.paintings;
	}

	public AdminCommand getCommand() {
		return this.command;
	}
	

}
