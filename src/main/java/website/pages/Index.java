package website.pages;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.ClientCommand;
import website.model.admin.Language;
import website.model.admin.ModelPhotoSize;
import website.model.database.Model;
import website.services.FileManager;
import website.services.ModelDao;
import website.services.WebsiteModule;

public class Index {
	
	private static final Logger logger = LoggerFactory.getLogger(Index.class);

	
	private ClientCommand command;
	
	@Inject
	private Block modelsBlock;
	
	@Inject
	private Block modelBlock;
	
	@Inject
	private Block home;
	
	
	@Inject
	private PersistentLocale persistentLocale;
	
	
	@Inject
	private ModelDao modelSource;
	
	@Inject
	private FileManager filemanager;
	
	@Inject 
	private LinkSource linkSource;
	
	
	void onActivate(EventContext context){
		
		Locale locale = this.persistentLocale.get() != null ? this.persistentLocale.get() : Language.ET.getLocale();
		
		this.command = ClientCommand.HOME;
		if(context.getCount() > 0){
			this.command = ClientCommand.findCandidate(context.get(String.class, 0),locale);
		}		
		if(context.getCount() ==2){
			if(this.command.equals(ClientCommand.MODELS)){
				this.category = context.get(String.class, 1);
			}
		}

		
		persistentLocale.set(locale);
	}
	 
	public String onPassivate() { return this.command.getContext(this.persistentLocale.get()).getRoute(); }
	
	public Block getActiveBlock(){
		switch (command){
			case HOME : return this.home;
			case MODELS : return this.modelsBlock;
			case MODEL : return this.modelBlock;
		}
		return this.home;
	}

	public ClientCommand getCommand() {
		return this.command;
	}

	@Property
	private String category;
	
	@Property
	private Integer index;


	public int getCategoriesSize(){
		return this.modelSource.getCategories(Language.get(this.persistentLocale.get())).size();
	}
	
	private  Map<String,Model> categories;
	
	public Collection<String> getCategoriesValues(){
		return this.getCategories().keySet();
	}
	
	public Map<String,Model> getCategories(){
		if(this.categories == null){
			this.categories = new LinkedHashMap<String,Model>();
			for(String c : this.modelSource.getCategories(Language.get(this.persistentLocale.get()))){
				Model m = this.modelSource.getCategoryLead(c,Language.get(this.persistentLocale.get()));
				if(m != null){
					this.categories.put(c, m);
				}
			}			
		}
		return this.categories;
	}
	
	public String getCategoryCellClass(){
		return "col-md-4";
	}
	
	public String getCatalogCellClass(){
		int r = this.getCategoriesSize() % 3;
		int c = getCategoriesSize() - this.index; 
		if(r == 0){
			if(c == 3 || c==2){
				return "col-md-6";
			}else if (c==1){
				return "col-md-12 ";
			}
		}else if(c==2){
			if(c == 2 || c==2){
				return "col-md-6";
			}
		}
		else if(r==1){
			if (c==1){
				return "col-md-12 ";
			}
		}
		

		return "col-md-4";
	}
	
	public String getCategoryPath(){
		return this.filemanager.getPath(this.getCurrentCategory().getKey(), ModelPhotoSize.THUMBNAIL2);
	}
	
	public String getModelPath(){
		return this.filemanager.getPath(this.model.getKey(), ModelPhotoSize.THUMBNAIL2);
	}
	
	public String getCategoryCmd(){
		return linkSource.createPageRenderLink(ClientCommand.MODELS.getPage(),false, new Object[]{
			ClientCommand.MODELS.getContext(this.persistentLocale.get()).getRoute(),	
			this.category	
		}).toURI();
	}
	
	public String getModelCmd(){
		String name = null;
		switch(Language.get(this.persistentLocale.get())){
			case ET: return name = this.model.getName();
			case EN: return name = this.model.getTranslation_en();
			case RU: return name = this.model.getTranslation_ru();
		}
		
		return linkSource.createPageRenderLink(ClientCommand.MODEL.getPage(),false, new Object[]{
			ClientCommand.MODEL.getContext(this.persistentLocale.get()).getRoute(),	
			name	
		}).toURI();
	}
	
	public String getModelName(){
		switch(Language.get(this.persistentLocale.get())){
			case ET: return this.model.getName();
			case EN: return this.model.getTranslation_en();
			case RU: return this.model.getTranslation_ru();
		}
		return null;
	}
	
	private Model getCurrentCategory() {
		return this.categories.get(category);
	}
	
	@Property
	private Model model;
	
	private List<Model> models;
	
	public List<Model> getCategoryModels(){
		if(this.models == null){
			this.models = this.modelSource.getAllForCategory(category,Language.get(this.persistentLocale.get()));
		}
		return models;
	}
	
}
