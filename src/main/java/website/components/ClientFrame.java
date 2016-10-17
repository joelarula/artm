package website.components;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import website.model.admin.AdminCommand;
import website.model.admin.ClientCommand;
import website.pages.Index;
import website.pages.admin.Board;

public class ClientFrame {

	@InjectPage
	private Index index;
	
	@Property
	private ClientCommand command;
	
	@Property
	private ClientCommand homeCommand= ClientCommand.HOME;
	
	@Inject
	private Messages messages;
	
	public List<ClientCommand> getMenu() {
		return Arrays.asList(ClientCommand.PAINTINGS,ClientCommand.SHOP,ClientCommand.CONTACT);
	}
	
	public String getActiveMenuItem() { 
		return this.index.getCommand().equals(command) ? "active":"idle";
	}
	
	public String getLabel(){
		return messages.get(this.command.getLabel());
	}
	
}
