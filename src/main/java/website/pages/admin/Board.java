package website.pages.admin;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.AdminCommand;
import website.model.admin.Language;
import website.model.admin.ModelPhotoSize;
import website.model.admin.SearchCommand;
import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;
import website.services.FileManager;
import website.services.impl.ModelGridDataSource;

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
	private UploadedFile original;
	
	@Property
	private  String searchName;
	
	@Property
	private  Category searchCategory;
	
	@Property
	private  Stock searchStock;
	
	@Property
	private  Boolean searchPublished;
	
	@Inject
	private Messages messages;
	
	@Inject
	private AlertManager alerts;
	
	@Inject
	private Session session;
	
	@Inject
	private FileManager filemanager;
	
	@Inject 
	private LinkSource linkSource;
	
	@Inject 
	private BeanModelSource beanModelSource;
	
	@Inject
	private PropertyAccess ac;
	
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
				case PUBLISHED : 
					this.searchPublished = true;
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

	public String[] onPassivate() { 
		
		if(this.command.equals(AdminCommand.MODELS)){
			return new String[]{this.command.name()};
		}else if(this.command.equals(AdminCommand.MODEL) ){
			if(this.model.getKey() != null){
				return new String[]{this.command.name(),model.getKey()};
			}
			return new String[]{this.command.name()};
		}else{
			return new String[]{this.command.name()};
		}
	}
	
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
	public Link  onSubmitFromSeachForm(){
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
		if(this.searchPublished != null && this.searchPublished){
			ctx.add(SearchCommand.PUBLISHED.name().toLowerCase());
			ctx.add("true");
		}
		return linkSource.createPageRenderLink("admin/board", true, ctx.toArray());
	}
	
	@CommitAfter
	@OnEvent(value=EventConstants.VALIDATE,component="modelForm")
	public void  onValidateFromModelForm(){
		if(this.original != null ){
			logger.info("uploaded photo {}",original.getFileName());
			if(!this.filemanager.supports(original.getFileName())){
				logger.info("{} unsupported file type {}",original.getFileName());
				this.modelForm.recordError(messages.format("unsupported file type",original.getFileName()));
			}
		}
	}

	@CommitAfter
	@OnEvent(value=EventConstants.SUCCESS,component="modelForm")
	public Link  onSuccessFromModelForm(){
		
		if(this.model.getKey() == null){
			this.model.setKey(generateModelKey());
		}
		
		
		if(this.original != null){
			logger.info("uploaded photo {}",original.getFileName());
			if(this.filemanager.supports(original.getFileName())){
				try {
					this.filemanager.saveFile(model.getKey(),ModelPhotoSize.ORIGINAL,original);
					String path = filemanager.getFile(model.getKey(),ModelPhotoSize.ORIGINAL).getAbsolutePath();
					//path = path.substring(FileManager.)
					logger.info("{} original photo saved in {}",model.getKey(),path);
					this.model.setPhoto(model.getKey());
				} catch (IOException e) {
					logger.error("{} saving failed {}",original.getFileName(),e.getMessage());
				}

			}

		}

		this.session.saveOrUpdate(this.model);
		logger.info("model {} saved",model.getKey());	
		alerts.success(messages.format("modelSavedSucessfully",model.getName()));
		return linkSource.createPageRenderLink("admin/board", true, new Object[]{"model",model.getKey()});
	}
	
	private String generateModelKey() {
		this.session.createSQLQuery("CREATE SEQUENCE IF NOT EXISTS models START WITH 1 INCREMENT BY 1").executeUpdate();
		BigInteger nextval = (BigInteger) this.session.createSQLQuery("select NEXTVAL('models')").uniqueResult();
		String key = "DI";
		key= key + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2);
		key=key+nextval.toString();
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
	
	private ModelGridDataSource modelSource;

	public GridDataSource getModels(){
		if(this.modelSource == null){
			this.modelSource = new ModelGridDataSource(session, Model.class,this.getSearchCriterion());
		}
		return this.modelSource;
	}
	
	private Criterion getSearchCriterion() {
		List<Criterion> conditions = new ArrayList<Criterion>();
		if(this.searchCategory != null){
			conditions.add(Restrictions.eq("category", this.searchCategory));
		}
		if(this.searchStock != null){
			conditions.add(Restrictions.eq("stock", this.searchStock));
		}
		if(this.searchName != null){
			conditions.add(Restrictions.ilike("name", this.searchName));
		}
		Criterion c = Restrictions.conjunction(conditions.toArray(new Criterion[]{}));
		return c;
	}

	private BeanModel<Model> gridModel;
	
	public BeanModel<Model> getGridModel(){
		if(this.gridModel == null){
			this.gridModel = beanModelSource.createDisplayModel(Model.class, messages);
			this.gridModel.getById("key").label(messages.get("key"));
			this.gridModel.getById("name").label(messages.get("name"));
			this.gridModel.getById("category").label(messages.get("category"));
			this.gridModel.getById("stock").label(messages.get("stock"));
			this.gridModel.getById("published").label(messages.get("published"));
			//this.gridModel.getById("photo").label(messages.get("photo"));
			this.gridModel.exclude("created","modified","author","description");
			this.gridModel.reorder("key","name","category","stock","published","photo");
		}
		return gridModel;
	}
	
	public String[] getModelContext(){
		return new String[]{AdminCommand.MODEL.name().toLowerCase(),model.getKey()};
	}
	
	public String getPhotoLabel(){
		if(this.model.getPhoto() != null){
			return this.model.getPhoto();
		}else{
			return messages.get("addPhoto");
		}
	}
	
	public String getFullscreenPath(){
		return this.filemanager.getPath(model.getKey(), ModelPhotoSize.FULL_SCREEN);
	}
	
	public String getPreviewPath(){
		return this.filemanager.getPath(model.getKey(), ModelPhotoSize.PREVIEW);
	}
	
	public String getThumbnailPath(){
		return this.filemanager.getPath(model.getKey(), ModelPhotoSize.THUMBNAIL);
	}
	
	public List<Language> getEditLocales(){
		return Arrays.asList(Language.EN);
	}
	
	@Property
	private Language editLocale;
	
	public String getNameTranslationLabel(){
		return messages.get(editLocale.getLocale().getLanguage()+".nameEditLabel");
	}

	public String getTranslation() {
		return this.model.getTranslation(editLocale.getLocale().getLanguage(),ac);
	}
	
	public void setTranslation(String translation) {
		logger.info("set name translation {} {}",editLocale.getLocale().getLanguage(),translation);
		this.model.setTranslation(editLocale.getLocale().getLanguage(), translation, ac);
	}
	

	public ValueEncoder<Language> getLocaleEncoder(){
		return new ValueEncoder<Language>(){

			@Override
			public String toClient(Language value) {
				return value.getLocale().getLanguage();
			}

			@Override
			public Language toValue(String clientValue) {
				
				if( clientValue.equalsIgnoreCase("en")){
					return Language.EN;
				}
				
				return null;
			}
			
		};
		
	}
}
