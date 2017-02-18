package website.services;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Decorate;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.URLEncoder;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.apache.tapestry5.upload.modules.UploadModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.database.Category;
import website.model.database.Model;
import website.services.impl.FileManagerImpl;
import website.services.impl.ImageDispatcher;
import website.services.impl.ModelDaoImpl;
import website.services.impl.UrlEncoderUtf8;

@ImportModule(UploadModule.class)
public class WebsiteModule {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteModule.class);

	private static final String WEBSITE_HOME = "sepised";
	public static final  String websiteFolder = System.getProperty("user.home")+File.separator+WEBSITE_HOME;
	public static final  String dbHomeDir = websiteFolder +File.separator + "db";
	
	
	public static void contributeApplicationDefaults(MappedConfiguration<String,String> configuration){
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES,"et,en,ru");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE,"dfsdfadfdsasdvds");
		
	}
	
	 public static void bind(ServiceBinder binder){
		 binder.bind(FileManager.class,FileManagerImpl.class);
		 binder.bind(ModelDao.class,ModelDaoImpl.class);
	 }
	 
	 public static void  contributeDefaultDataTypeAnalyzer(MappedConfiguration<Class, String> c) {
		 c.add(Category.class, "category");
	 } 
	    
	  @Startup
	  public void onStartup(RegistryShutdownHub shutdown,ModelDao dao) throws IOException{
			
		  dao.loadDatabase();

		  shutdown.addRegistryShutdownListener(new Runnable(){

				@Override
				public void run() {
					
				}
				
			});
	  }
	 
	 
	  
	  public void contributeAssetDispatcher(MappedConfiguration<String,AssetRequestHandler> conf){
		  conf.addInstance(FileManager.catalog, ImageDispatcher.class);
	  }
	  
	  

      @Decorate(serviceInterface = ThreadLocale.class)     
      public ThreadLocale decorateThreadLocale(final ThreadLocale threadLocale,  final PersistentLocale persistentLocale) 
      { 
          return new ThreadLocale() 
          { 
              @Override 
              public void setLocale(Locale locale) 
              { 
                  threadLocale.setLocale(locale); 
              } 

              @Override 
              public Locale getLocale() 
              { 
                  if (!persistentLocale.isSet()) 
                  { 
                      setLocale(Locale.forLanguageTag("et")); 
                  } 
                  return threadLocale.getLocale(); 
              } 

          }; 
      } 

      
      
      
      @Contribute(ServiceOverride.class)
      public static void setupOverrides(MappedConfiguration<Class,Object> configuration){
        configuration.add(URLEncoder.class, new UrlEncoderUtf8());
      }
      
      public void contributeRequestHandler(
    	OrderedConfiguration<RequestFilter> configuration, 
    	final RequestGlobals requestGlobals){
          configuration.add("Utf8Filter",   new RequestFilter()
	        {
	            public boolean service(Request request, Response response, RequestHandler handler)
	                throws IOException
	            {
	                requestGlobals.getHTTPServletRequest().setCharacterEncoding("UTF-8");
	                requestGlobals.getHTTPServletResponse().setCharacterEncoding("UTF-8");
	                return handler.service(request, response);
	            }
	        }); 
      }
      
      
      
}
