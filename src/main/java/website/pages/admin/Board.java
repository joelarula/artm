package website.pages.admin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.ColumnSort;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.GridSortModel;
import org.apache.tapestry5.internal.grid.CollectionGridDataSource;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.AdminCommand;
import website.model.admin.Details;
import website.model.admin.Language;
import website.model.admin.ModelPhotoSize;
import website.model.admin.SearchCommand;
import website.model.database.Category;
import website.model.database.Model;
import website.services.FileManager;
import website.services.ModelDao;
import website.services.WebsiteModule;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
	

	@Component(parameters = {"enctype='application/x-www-form-urlencoded; charset=UTF-8'", "accept-charset='utf-8'"})
	private Form modelsForm;
	
	
	@Property
	private  Model model;
	
	@Property
	private  Model previousModel;

	@Property
	private  Model nextModel;

	@Component
	private Grid searchGrid;
	
	@Property
	private UploadedFile original;
	
	@Property
	private  String searchName;
	
	@Property
	private  String searchCategory;
	
	@Property
	private  Boolean searchUnPublished;
	
	@Inject
	private Messages messages;
	
	@Inject
	private AlertManager alerts;
	
	@Inject
	private Request request;
	
	@Inject
	private FileManager filemanager;
	
	@Inject 
	private LinkSource linkSource;
	
	@Inject 
	private BeanModelSource beanModelSource;
	
	@Inject
	private PropertyAccess ac;
	
	@Inject
	private ModelDao dao;
	
	@Property
	private Details editDetail;
	
	@Property
	private int index;
	
	@Inject
	private AjaxResponseRenderer ajax;

	
	public Object onActivate(EventContext context) throws JsonParseException, JsonMappingException, IOException{
		
	
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
				this.model = (Model) dao.get(modelKey);
				if(model == null && request.getMethod().toUpperCase().equals("POST")){
					this.model =  getNewModel();
				}else if(model == null){
					return this.linkSource.createPageRenderLink("admin/board", true, new Object[]{"model"});
				}
			}else if(this.command.equals(AdminCommand.MODEL)){
				this.model =  getNewModel();
			}
		}
		
		return true;
	}
	 
	private void collectSearchParameters(EventContext context) {
		Map<String,String> params = this.getSearchParams(context); 
		
		for(Entry<String,String> e  : params.entrySet()){ 
			try{
				SearchCommand c = SearchCommand.valueOf(e.getKey().toUpperCase());
				switch(c){
				case CATEGORY : 
					this.searchCategory = URLDecoder.decode(e.getValue(),"UTF-8");
					break;				
				case NAME : 
					this.searchName =  URLDecoder.decode(e.getValue(),"UTF-8");
					break;	
				case UNPUBLISHED : 
					this.searchUnPublished = true;
					break;	
				
				}
			}catch(Exception ex){
				logger.debug("unknown  parameter {}",e.getKey());
			}

		}
	}

	private Map<String, String> getSearchParams(EventContext context) {
		Map<String,String> params= new HashMap<String,String>();
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
		logger.info("searching {}",params.toString());
		return params;
	}

	private Model getNewModel() {
		Model model = new Model();		
		model.setCategory(new Category());
		model.setPublished(true);	
		return model;
	}

	public String[] onPassivate() throws UnsupportedEncodingException { 
		
		if(this.command != null && this.command.equals(AdminCommand.MODELS)){
			List<String> list = this.getSearchContext();
			return list.toArray(new String[list.size()]);
		}else if(this.command != null && this.command.equals(AdminCommand.MODEL) ){
			if(this.model.getKey() != null){
				return new String[]{this.command.name(),model.getKey()};
			}
			return new String[]{this.command.name().toLowerCase()};
		}else if(this.command != null){
			return new String[]{this.command.name().toLowerCase()};
		}
		return new String[]{};
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
	public Link   onSubmitFromSeachForm() throws UnsupportedEncodingException{
		List<String> ctx = this.getSearchContext();
		final Link l =  linkSource.createPageRenderLink("admin/board", true, ctx.toArray());
		return l;
	}
	
	
	private List<String> getSearchContext() throws UnsupportedEncodingException {
		
		List<String> ctx = new ArrayList<String>();
		
		ctx.add(AdminCommand.MODELS.name().toLowerCase());
		if(this.searchCategory != null){
			ctx.add(SearchCommand.CATEGORY.name().toLowerCase());
			ctx.add(URLEncoder.encode(this.searchCategory,"UTF-8"));
		}
		
		if(this.searchName != null){
			ctx.add(SearchCommand.NAME.name().toLowerCase());
			ctx.add(URLEncoder.encode(this.searchName,"UTF-8"));
		}
		if(this.searchUnPublished != null && this.searchUnPublished){
			ctx.add(SearchCommand.UNPUBLISHED.name().toLowerCase());
			ctx.add("true");
		}
		
		return ctx;
	}

	@OnEvent(value=EventConstants.VALIDATE,component="modelForm")
	public void  onValidateFromModelForm(){
		if(this.original != null ){
			logger.info("uploaded photo {}",original.getFileName());
			if(!this.filemanager.supportsPhotoExtension(original.getFileName())){
				logger.info("{} unsupported file type {}",original.getFileName());
				this.modelForm.recordError(messages.format("unsupported file type",original.getFileName()));
			}
		}
	}

	@OnEvent(value=EventConstants.SUCCESS,component="modelForm")
	public Link  onSuccessFromModelForm() throws IOException{
		
		if(this.model.getKey() == null || this.model.getKey().isEmpty()){
			model.setKey(this.generateModelKey());
			model.setCreated(new Date());
		}
		model.setModified(new Date());
		
		if(this.original != null){
					
			logger.info("uploaded photo {}",original.getFileName());
			if(this.filemanager.supportsPhotoExtension(original.getFileName())){
				try {
					this.filemanager.savePhoto(model.getKey(),ModelPhotoSize.ORIGINAL,original.getStream(),original.getFileName());
					String path = filemanager.getPhoto(model.getKey(),ModelPhotoSize.ORIGINAL).getAbsolutePath();
					logger.info("{} original photo saved in {}",model.getKey(),path);
					
					String prefix = WebsiteModule.websiteFolder 
							+ File.separator + FileManager.catalog 
							+ File.separator + ModelPhotoSize.ORIGINAL.name().toLowerCase()
							+ File.separator ;
					
					String photo =path.substring(prefix.length());
					this.model.setPhoto(photo);
					
				} catch (IOException e) {
					logger.error("{} saving failed {}",original.getFileName(),e.getMessage());
				}

			}

		}
		
		for(Details d : this.getEditDetails()){
			if(this.details.get(d) != null){
				UploadedFile f = this.details.get(d); 
				logger.info("uploaded detail {}",f.getFileName());
				if(this.filemanager.supportsPhotoExtension(f.getFileName())){
					try {
						this.filemanager.savePhoto(model.getKey()+":"+d.name(),ModelPhotoSize.ORIGINAL,f.getStream(),f.getFileName());
						String path = filemanager.getPhoto(model.getKey()+":"+d.name(),ModelPhotoSize.ORIGINAL).getAbsolutePath();
						logger.info("{} original photo saved in {}",model.getKey()+":"+d.name(),path);
						
						String prefix = WebsiteModule.websiteFolder 
								+ File.separator + FileManager.catalog 
								+ File.separator + ModelPhotoSize.ORIGINAL.name().toLowerCase()
								+ File.separator ;
						
						String dname =path.substring(prefix.length());
						if(d.equals(Details.DETAIL0)){
							this.model.setDetail_0(dname);
						}else if(d.equals(Details.DETAIL1)){
							this.model.setDetail_1(dname);
						}else if(d.equals(Details.DETAIL2)){
							this.model.setDetail_2(dname);
						}
						
						
					} catch (IOException e) {
						logger.error("{} saving failed {}",original.getFileName(),e.getMessage());
					}

				}

			}
		}

		this.dao.saveModel(model);	
		
		alerts.success(messages.format("modelSavedSucessfully",model.getName()));
		return linkSource.createPageRenderLink("admin/board", true, new Object[]{"model",model.getKey()});
	}
	
	private String generateModelKey() {
		return UUID.randomUUID().toString();
	}

	
	
	public String getModelLabel(){

		return model.getName() != null ? model.getName() : "";
	
	}
	
	private CollectionGridDataSource modelSource;

	public GridDataSource getModels(){
		if(this.modelSource == null){
			this.modelSource = new CollectionGridDataSource(this.getSearchCriterion());
		}
		this.searchGrid.getSortModel().updateSort("position");
		ColumnSort m = this.searchGrid.getSortModel().getColumnSort("position");
		if(m.DESCENDING.equals(m)){
			this.searchGrid.getSortModel().updateSort("position");
		}
		
		return this.modelSource;
	}
	
	private List<Model> getSearchCriterion() {
		return dao.search(this.searchName,this.searchCategory, this.searchUnPublished);

	}

	public String[] getSwitchUpCtx(){
		return new String[]{this.model.getKey(),this.getPreviousModelKey()};
	}
	
	public String getPreviousModelKey() {
		Model m = null;
		if(index-1 >=0){
			m = (Model) this.modelSource.getRowValue(index-1);
		}	
		return m != null ? m.getKey() : null;
	}

	public String[] getSwitchDownCtx(){
		return new String[]{this.getNextModelKey(),this.model.getKey()};
	}
	
	
	public String getNextModelKey() {
		Model m = null;
		
		if(index+1 < this.getModels().getAvailableRows()){
			m = (Model) this.modelSource.getRowValue(index+1);	
		}
		
		return m != null ? m.getKey() : null;
	}
	
	@Component
	private Zone gridZone;

	@OnEvent("switch")
	public void onSwitch(String lowerKey, String upperKey) throws IOException{
		Model l = this.dao.get(lowerKey);
		int lp = l.getPosition();
		Model u = this.dao.get(upperKey);
		int up = u.getPosition();
		if(lp > up){
			
			l.setPosition(up);
			u.setPosition(lp);
			
		}
		if(lp == up){
			l.setPosition(up);
			u.setPosition(lp+1);
		}
		
		dao.saveModel(l);
		dao.saveModel(u);
		
		this.ajax.addRender("gridZone", gridZone.getBody());
	}
	
	private BeanModel<Model> gridModel;
	
	public BeanModel<Model> getGridModel(){
		if(this.gridModel == null){
			this.gridModel = beanModelSource.createDisplayModel(Model.class, messages);
			this.gridModel.getById("name").label(messages.get("name"));
			this.gridModel.getById("category").label(messages.get("category"));
			this.gridModel.getById("published").label(messages.get("published"));
			this.gridModel.getById("photo").label(messages.get("photo"));
			this.gridModel.exclude("key","created","modified","description","translation_en","translation_ru","detail_0","detail_1","detail_2");
			this.gridModel.reorder("name","category","published","photo");
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
	
	public String getDetailPreviewPath(){
		return this.filemanager.getPath(model.getKey()+":"+editDetail.name(), ModelPhotoSize.PREVIEW);
	}
	
	public String getDetailThumbnailPath(){
		return this.filemanager.getPath(model.getKey()+":"+editDetail.name(), ModelPhotoSize.THUMBNAIL);
	}
	
	public String getIconPath(){
		return this.filemanager.getPath(model.getKey(), ModelPhotoSize.ICON);
	}
	
	public List<Language> getEditLocales(){
		return Arrays.asList(Language.EN,Language.RU);
	}
	
	public List<Details> getEditDetails(){
		return Arrays.asList(Details.DETAIL0,Details.DETAIL1,Details.DETAIL2);
	}
	
	public String getDetailLabel(){
		return messages.get(editDetail.name()+".detailEditLabel");
	}

	public UploadedFile getDetailFile(){
		return null;
	}
	
	private Map<Details,UploadedFile> details = new  HashMap<Details,UploadedFile>();
	
	public void setDetailFile(UploadedFile f){
		details.put(editDetail, f);
	}

	
	public boolean getDetailExists(){
		if(editDetail.equals(Details.DETAIL0)){
			return this.model.getDetail_0() != null;
		}else if(editDetail.equals(Details.DETAIL1)){
			return this.model.getDetail_1() != null;
		}else if(editDetail.equals(Details.DETAIL2)){
			return this.model.getDetail_2() != null;
		}
		return false;
	}
	
	public List<Integer> getPositions(){
		return Arrays.asList(5,4,3,2,1);
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
	
	public String getCategoryTranslationLabel(){
		return messages.get(editLocale.getLocale().getLanguage()+".categoryEditLabel");
	}
	
	@OnEvent(value=EventConstants.PROVIDE_COMPLETIONS,component="searchCategory")
	public String[] getCategoryModel(String search){
		List<String> results = this.dao.getCategories(Language.ET).stream()
			.filter(c -> c.toLowerCase().contains(search.toLowerCase()))
			.collect(Collectors.toList());
		
		String[] res = new String[results.size()];
		res = results.toArray(res);
		return res;
	}
	
	public String getCategoryTranslation() {
		switch (editLocale){
			case ET: return model.getCategory().getName();
			case EN: return model.getCategory().getName_en();
			case RU: return model.getCategory().getName_ru();
		}
		return null;
	}
	
	public void setCategoryTranslation(String translation) {
		logger.info("set category translation {} {}",editLocale.getLocale().getLanguage(),translation);
		switch (editLocale){
			case ET: this.model.getCategory().setName(translation);break;
			case EN: this.model.getCategory().setName_en(translation);break;
			case RU: this.model.getCategory().setName_ru(translation);break;
		}
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
				
				if( clientValue.equalsIgnoreCase("ru")){
					return Language.RU;
				}
				
				return null;
			}
			
		};
		
	}
	
	
	public ValueEncoder<Details> getDetailEncoder(){
		return new ValueEncoder<Details>(){

			@Override
			public String toClient(Details value) {
				return value.name().toLowerCase();
			}

			@Override
			public Details toValue(String clientValue) {
				
				if( clientValue.equalsIgnoreCase(Details.DETAIL0.name())){
					return Details.DETAIL0;
				}
				
				if( clientValue.equalsIgnoreCase(Details.DETAIL1.name())){
					return Details.DETAIL1;
				}
				
				if( clientValue.equalsIgnoreCase(Details.DETAIL2.name())){
					return Details.DETAIL2;
				}
				
				return null;
			}
			
		};
		
	}
}
