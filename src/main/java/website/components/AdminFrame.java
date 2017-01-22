package website.components;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;

import website.model.admin.AdminCommand;
import website.model.admin.Language;
import website.pages.admin.Board;

@Import(stylesheet="base/css/AdminFrame.css")
public class AdminFrame {

	
	@InjectPage
	private Board board;
	
	@Property
	private AdminCommand command;
	
	@Property
	private AdminCommand logoutCommand = AdminCommand.LOGOUT;
	
	
	@Inject
	private Messages messages;
	
	public List<AdminCommand> getMenu() {
		return Arrays.asList(AdminCommand.MODELS);
	}
	
	public String getActiveMenuItem() { 
		return this.board.getCommand().equals(command) ? "selected":"idle";
	}
	
	public String getLabel(){
		return messages.get(this.command.getLabel());
	}
	

}
