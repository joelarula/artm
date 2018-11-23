package website.pages;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.ClientCommand;
import website.model.admin.Language;
import website.model.admin.ModelPhotoSize;
import website.model.database.Model;
import website.services.FileManager;
import website.services.ModelDao;

@Import(
	library={"lightbox-master/dist/ekko-lightbox.js"},
	stylesheet={"lightbox-master/dist/ekko-lightbox.css"},
	module={"index"})
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
	private Request request;
	
	@Inject
	private ModelDao modelSource;
	
	@Inject
	private FileManager filemanager;
	
	@Inject 
	private LinkSource linkSource;
	
	@Environmental
	private JavaScriptSupport jsSupport;
	
	
	Object onActivate(EventContext context){
		
		if(context.getCount() == 1 && context.get(String.class, 0).equals("admin")){
			return this.linkSource.createPageRenderLink("admin/board", false, new Object[]{});
		}
		
		Locale locale = this.persistentLocale.get() != null ? this.persistentLocale.get() : Language.ET.getLocale();
		
		this.command = ClientCommand.HOME;
		if(context.getCount() > 0){
			this.command = ClientCommand.findCandidate(context.get(String.class, 0),locale);
		}		
		if(context.getCount() ==2){
			if(this.command.equals(ClientCommand.MODELS)){
				this.category = context.get(String.class, 1);
			}else if(this.command.equals(ClientCommand.MODEL)){
				this.model = this.modelSource.getById(context.get(String.class, 1),Language.get(this.persistentLocale.get()));
			}
		}

		
		persistentLocale.set(locale);
		
		return null;
	}
	 
	public String[] onPassivate() { 
		//return this.command.getContext(this.persistentLocale.get()).getRoute(); 
		switch (command){
			case HOME : return new String[]{ClientCommand.HOME.getContext(this.persistentLocale.get()).getRoute()};
			case MODELS: return new String[]{ClientCommand.MODELS.getContext(this.persistentLocale.get()).getRoute()};
			case MODEL :{ 
				if(this.model != null){
					return new String[]{
						ClientCommand.MODEL.getContext(this.persistentLocale.get()).getRoute(),
						this.model.getKey()};					
				}else{
					return new String[]{ClientCommand.HOME.getContext(this.persistentLocale.get()).getRoute()};	
				}
			}
	
		}
		return new String[]{ClientCommand.HOME.getContext(this.persistentLocale.get()).getRoute()};	
	}
	
	
	public Block getActiveBlock(){
		switch (command){
			case HOME : return this.home;
			case MODELS : return this.modelsBlock;
			case MODEL :  return this.modelBlock;
		};
		
		return this.home;
	}

	public ClientCommand getCommand() {
		return this.command;
	}
	


	@Property
	private String category;
	
	@Property
	private String menuCategory;
	
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
	
	public String getModelPreviewPath(){
		return this.filemanager.getPath(this.model.getKey(), ModelPhotoSize.PREVIEW);
	}
	
	public String getCategoryCmd(){
		return linkSource.createPageRenderLink(ClientCommand.MODELS.getPage(),false, new Object[]{
			ClientCommand.MODELS.getContext(this.persistentLocale.get()).getRoute(),	
			this.category	
		}).toURI();
	}
	
	public String getCategoryMenuCmd(){
		return linkSource.createPageRenderLink(ClientCommand.MODELS.getPage(),false, new Object[]{
			ClientCommand.MODELS.getContext(this.persistentLocale.get()).getRoute(),	
			this.menuCategory	
		}).toURI();
	}
	
	public String getModelCmd(){
		String name = null;
		switch(Language.get(this.persistentLocale.get())){
			case ET: name = this.model.getName(); break;
			case EN: name = this.model.getTranslation_en(); break;
			case RU: name = this.model.getTranslation_ru(); break;
		}
		
		Link l =  linkSource.createPageRenderLink(ClientCommand.MODEL.getPage(),true, new Object[]{
			ClientCommand.MODEL.getContext(this.persistentLocale.get()).getRoute(),	
			name	
		});
		
		l.addParameterValue("i", this.index);	
		l.addParameter("c", this.category);
		return l.toURI();
	}
	
	public String getModelName(){
		if(this.model != null){
			switch(Language.get(this.persistentLocale.get())){
			case ET: return this.model.getName();
			case EN: return this.model.getTranslation_en();
			case RU: return this.model.getTranslation_ru();
		}
		}

		return "";
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
	
	public String getActiveMenuClass(){
		return this.menuCategory.equals(this.category) ? "active" : "passive";
	}
	
	@Property
	private String detail;
	
	public List<String> getDetails(){
		
		return Arrays.asList(
			this.model.getDetail_0(),
			this.model.getDetail_1(),
			this.model.getDetail_2()).stream().filter( m -> m != null)
			.collect(Collectors.toList());
	}
	
	public String getDetailPreviewPath(){
		return this.filemanager.getPath(detail.substring(0, detail.length()-4), ModelPhotoSize.ICON);
	}
	
	public String getLocale(){
		return this.persistentLocale.get().getLanguage();
	}
	
	
	public String getPreviousModelKey() {
		Model m = null;
		this.category = request.getParameter("c");
		String i = request.getParameter("i");
		if(i != null) {
			Integer ix = Integer.valueOf(request.getParameter("i"));
			if(ix != null  && ix-1 >=0){
				m = (Model) this.getCategoryModels().get(ix-1);
			}	
		}

		return m != null ? m.getKey() : null;
	}

	public String getNextModelKey() {
		Model m = null;
		this.category = request.getParameter("c");
		String i = request.getParameter("i");
		if(i != null) {
			Integer ix = Integer.valueOf(request.getParameter("i"));
			if(ix != null  && ix+1 < this.getCategoryModels().size() && ix+1 >= 0){
				m = (Model) this.getCategoryModels().get(ix+1);
			}
		}

		
		return m != null ? m.getKey() : null;
	}
	
	public String getPrevCmd(){
		
		Model m = null;
		this.category = request.getParameter("c");
		Integer ix = Integer.valueOf(request.getParameter("i"));
		if(ix != null  && ix-1 >= 0){
			m = (Model) this.getCategoryModels().get(ix-1);
			Link l =  linkSource.createPageRenderLink(ClientCommand.MODEL.getPage(),true, new Object[]{
				ClientCommand.MODEL.getContext(this.persistentLocale.get()).getRoute(),	
				m.getKey()	
			});
				
			l.addParameterValue("i", ix-1);	
			l.addParameter("c", this.category);
			return l.toURI();
			
		}
		
		return "#";
	}
	
	public String getNextCmd(){
		
		Model m = null;
		this.category = request.getParameter("c");
		Integer ix = Integer.valueOf(request.getParameter("i"));
		if(ix != null  && ix+1 < this.getCategoryModels().size()){
			m = (Model) this.getCategoryModels().get(ix+1);
			Link l =  linkSource.createPageRenderLink(ClientCommand.MODEL.getPage(),true, new Object[]{
				ClientCommand.MODEL.getContext(this.persistentLocale.get()).getRoute(),	
				m.getKey()	
			});
				
			l.addParameterValue("i", ix+1);	
			l.addParameter("c", this.category);
			return l.toURI();
		}
				
		return "#";
	}
	
	public String getModelOriginalCmd(){
		return "/assets/"+this.filemanager.getPath(this.model.getKey(), ModelPhotoSize.ORIGINAL);
	}
	
	public String getDetailOriginalCmd(){
		return "/assets/"+this.filemanager.getPath(detail.substring(0, detail.length()-4), ModelPhotoSize.ORIGINAL);
	}
}
