package website.pages;

import java.util.HashMap;
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
	private Block models;
	
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
		persistentLocale.set(locale);
	}
	 
	public String onPassivate() { return this.command.getContext(this.persistentLocale.get()).getRoute(); }
	
	public Block getActiveBlock(){
		switch (command){
			case HOME : return this.home;
			case MODELS : return this.models;
		}
		return this.models;
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
	
	public List<String> getCategories(){
		return this.modelSource.getCategories(Language.get(this.persistentLocale.get()));
	}
	
	public String getCatalogCellClass(){
		int c = getCategoriesSize() - this.index; 
		if(c == 3 || c==2){
			return "col-md-6";
		}else if (c==1){
			return "col-md-12 ";
		}
		return "col-md-4";
	}
	
	public String getCategoryPath(){
		return this.filemanager.getPath(this.getCurrentCategory().getKey(), ModelPhotoSize.THUMBNAIL2);
	}
	
	public String getCategoryCmd(){
		return linkSource.createPageRenderLink(ClientCommand.MODELS.getPage(),false, new Object[]{
			ClientCommand.MODELS.getContext(this.persistentLocale.get()).getRoute(),	
			this.category	
		}).toURI();
	}
	
	public boolean getCategoryPreviewExists(){
		return this.getCurrentCategory() != null;
	}

	private Map<String,Model> currentCategory = new HashMap<String,Model>();
	
	private Model getCurrentCategory() {
		if(this.currentCategory.get(category) == null){
			currentCategory.put(category,this.modelSource.getCategoryLead(this.category,Language.get(this.persistentLocale.get())));
		}
		return this.currentCategory.get(category);
	}
	
}
