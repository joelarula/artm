package website.pages.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.AdminCommand;
import website.model.admin.SearchCommand;
import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;
import website.services.WebsiteModule;

public class Board {

	private static final Logger logger = LoggerFactory.getLogger(Board.class);
	
	private AdminCommand command;
	
	@Property
	private AdminCommand addModelCommand = AdminCommand.MODEL;
		
	@Inject
	private Block modelsBlock;
	
	@Inject
	private Block modelBlock;
	
	@Inject
	private Block settings;
	
	@Component
	private Form modelForm;
	
	@Component
	private Form modelsForm;
	
	
	@Property
	private  Model model;
	
	@Property
	private  String searchName;
	
	@Property
	private  Category searchCategory;
	
	@Property
	private  Stock searchStock;
	
	@Property
	private  Boolean searchPublisehd;
	
	@Inject
	private Messages messages;
	
	@Inject
	private AlertManager alerts;
	
	@Inject
	private Session session;
	
	@Inject 
	private LinkSource linkSource;
	
	@Inject 
	private BeanModelSource modelSource;
	
	public Object onActivate(EventContext context){
		this.command = AdminCommand.MODELS;
		if(context.getCount() > 0){
			String cmd = context.get(String.class, 0).toUpperCase();
			try{
				this.command = AdminCommand.valueOf(cmd);
			}catch(Exception ex){
				logger.warn("unknown command {}",cmd);
			}
		
			if(this.command.equals(AdminCommand.MODELS) && context.getCount() > 1){
				collectSearchParameters(context);
			}else if(this.command.equals(AdminCommand.MODEL) && context.getCount() > 1){
				String modelKey = context.get(String.class, 1);
				this.model = (Model) session.get(Model.class, modelKey);
				if(model == null){
					return this.linkSource.createPageRenderLink("admin/board", true, new Object[]{"model"});
				}
			}else if(this.command.equals(AdminCommand.MODEL)){
				this.model =  getNewModel();
			}
		}
		
		return true;
	}
	 
	private void collectSearchParameters(EventContext context) {
		Map<String,String> params = new HashMap<String,String>();
		String topic = null;
		for( int i = 1;i <= context.getCount()-1;i++ ){	
				
			int m = (i+1) % 2;
			String token = context.get(String.class, i);
			if(m == 0){
				topic = token;
			}else{
				params.put(topic, token);
			}
				 
		}
		logger.info(params.toString());
		
		for(Entry<String,String> e  : params.entrySet()){ 
			try{
				SearchCommand c = SearchCommand.valueOf(e.getKey().toUpperCase());
				switch(c){
				case CATEGORY : 
					this.searchCategory = Category.valueOf(e.getValue().toUpperCase());
					break;
				case STOCK : 
					this.searchStock = Stock.valueOf(e.getValue().toUpperCase());
					break;
				case NAME : 
					this.searchName = e.getValue();
					break;	
				
				}
			}catch(Exception ex){
				logger.debug("unknown  parameter {}",e.getKey());
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
	
	
	@OnEvent(value=EventConstants.SUCCESS,component="modelsForm")
	public Link  onSubmitFromModelsForm(){
		List<String> ctx = new ArrayList<String>();
		ctx.add(AdminCommand.MODELS.name().toLowerCase());
		if(this.searchCategory != null){
			ctx.add(SearchCommand.CATEGORY.name().toLowerCase());
			ctx.add(this.searchCategory.name().toLowerCase());
		}
		
		if(this.searchStock != null){
			ctx.add(SearchCommand.STOCK.name().toLowerCase());
			ctx.add(this.searchStock.name().toLowerCase());
		}
		
		if(this.searchName != null){
			ctx.add(SearchCommand.NAME.name().toLowerCase());
			ctx.add(this.searchName);
		}
		return linkSource.createPageRenderLink("admin/board", true, ctx.toArray());
	}

	@CommitAfter
	@OnEvent(value=EventConstants.SUCCESS,component="modelForm")
	public Link  onSuccessFromModelForm(){
		if(this.model.getKey() == null){
			this.model.setKey(generateModelKey());
		}
		this.session.saveOrUpdate(this.model);
		logger.info("model {} saved",model.getKey());	
		alerts.success(messages.format("modelSavedSucessfully",model.getName()));
		return linkSource.createPageRenderLink("admin/board", true, new Object[]{"model",model.getKey()});
	}
	
	private String generateModelKey() {
		String key = "DI_";
		key= key + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2);
		return key;
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
	
	public String getModelLabel(){
		if(this.model.getKey() == null){
			return messages.get("addModel");
		}else{
			return model.getKey();
		}
	}
	

	public List<Model> getModels(){
		return session.createCriteria(Model.class).list();
	}
	
	private BeanModel gridModel;
	
	public BeanModel getGridModel(){
		if(this.gridModel == null){
			this.gridModel = modelSource.createDisplayModel(Model.class, messages);
			this.gridModel.getById("key").label(messages.get("key"));
			this.gridModel.getById("name").label(messages.get("name"));
			this.gridModel.getById("category").label(messages.get("category"));
			this.gridModel.getById("stock").label(messages.get("stock"));
			this.gridModel.getById("published").label(messages.get("published"));
			this.gridModel.getById("photo").label(messages.get("photo"));
			this.gridModel.exclude("created","modified","author","description");
			this.gridModel.reorder("key","name","category","stock","published","photo");
		}
		return gridModel;
	}
}
