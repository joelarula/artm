package website.pages.admin;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.AdminCommand;
import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;
import website.services.WebsiteModule;

public class Board {

	private static final Logger logger = LoggerFactory.getLogger(Board.class);
	
	private AdminCommand command;
		
	@Inject
	private Block modelsBlock;
	
	@Inject
	private Block modelBlock;
	
	@Inject
	private Block settings;
	
	@Component
	private Form modelForm;
	
	@Property
	private  Model model;
	
	@Inject
	private Messages messages;
	
	@Inject
	private AlertManager alerts;
	
	void onActivate(EventContext context){
		this.command = AdminCommand.MODELS;
		if(context.getCount() > 0){
			String cmd = context.get(String.class, 0).toUpperCase();
			this.command = AdminCommand.valueOf(cmd);
			if(this.command.equals(AdminCommand.MODEL) && context.getCount() > 1){
				Long modelId = context.get(Long.class, 1);
			}else if(this.command.equals(AdminCommand.MODEL)){
				this.model =  getNewModel();
			}
		}
	}
	 
	private Model getNewModel() {
		Model model = new Model();
		model.setStock(Stock.ON_DEMAND);
		model.setCategory(Category.VARIA);
		model.setPublished(true);
		return model;
	}

	public String onPassivate() { return this.command.name().toLowerCase(); }
	
	public Block getActiveBlock(){
		switch (command){
			case MODEL : return this.modelBlock;
			case MODELS : return this.modelsBlock;
			case SETTINGS : return this.settings;
		}
		return this.modelsBlock;
	}

	public AdminCommand getCommand() {
		return this.command;
	}
	

	
	@OnEvent(EventConstants.SUCCESS)
	public void  onSuccessFromModelForm(){
		logger.info("model saved");	
		alerts.success(messages.get("modelSavedSucessfully"));
	}
	
	public List<Category> getCatgories(){
		return Arrays.asList(
			Category.ABSTRACT,Category.FLOWERS,Category.NATURE,
			Category.FIGURATIVE,Category.STILL_LIFE,
			Category.CHILDRENS,Category.VARIA);
	}
	
	public List<Stock> getStocks(){
		return Arrays.asList(Stock.ON_DEMAND,Stock.IN_STOCK,Stock.E_KAUBAMAJA);
	}
	
}
