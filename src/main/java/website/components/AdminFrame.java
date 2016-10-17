package website.components;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import website.model.admin.AdminCommand;
import website.pages.admin.Board;

@Import(stylesheet="base/css/AdminFrame.css")
public class AdminFrame {

	@InjectPage
	private Board board;
	
	@Property
	private AdminCommand command;
	
	@Inject
	private Messages messages;
	
	public List<AdminCommand> getMenu() {
		return Arrays.asList(AdminCommand.PAINTINGS,AdminCommand.SETTINGS);
	}
	
	public String getActiveMenuItem() { 
		return this.board.getCommand().equals(command) ? "selected":"idle";
	}
	
	public String getLabel(){
		return messages.get(this.command.getLabel());
	}
}
