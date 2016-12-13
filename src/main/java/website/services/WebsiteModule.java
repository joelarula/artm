package website.services;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.apache.tapestry5.upload.modules.UploadModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import website.model.admin.ClientCommand;
import website.model.database.Author;
import website.model.database.Model;
import website.services.impl.FileManagerImpl;
import website.services.impl.ImageDispatcher;
import website.services.impl.ModelDaoImpl;

@ImportModule(UploadModule.class)
public class WebsiteModule {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteModule.class);

	private static final String WEBSITE_HOME = "sepised";
	public static final  String websiteFolder = System.getProperty("user.home")+File.separator+WEBSITE_HOME;
	public static final  String dbHomeDir = websiteFolder +File.separator + "db";
	
	
	public static void contributeApplicationDefaults(MappedConfiguration<String,String> configuration){
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES,"et,en");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE,"dfsdfadfdsasdvds");
		
	}
	
	 public static void bind(ServiceBinder binder){
		 binder.bind(FileManager.class,FileManagerImpl.class);
		 binder.bind(ModelDao.class,ModelDaoImpl.class);
	 }
	 
	  @Startup
	  public void onStartup(RegistryShutdownHub shutdown,ModelDao dao) throws IOException{
			
		//  dao.loadDatabase();
		 // for(Model m : dao.getAllModels()){
		//	  m.setPublished(true);
		//	  dao.saveModel(m);
		//  }
		  
		  shutdown.addRegistryShutdownListener(new Runnable(){

				@Override
				public void run() {
					
				}
				
			});
	  }
	 
	 
	  
	  public void contributeAssetDispatcher(MappedConfiguration<String,AssetRequestHandler> conf){
		  conf.addInstance(FileManager.catalog, ImageDispatcher.class);
	  }

}
