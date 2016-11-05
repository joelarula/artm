package website.services;

import java.io.File;
import java.sql.SQLException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateConfigurer;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.ClientCommand;
import website.model.database.Author;
import website.services.impl.FileManagerImpl;
import website.services.impl.ImageDispatcher;

public class WebsiteModule {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteModule.class);

	private static final String WEBSITE_HOME = "website";
	public static final  String websiteFolder = System.getProperty("user.home")+File.separator+WEBSITE_HOME;
	public static final  String dbHomeDir = websiteFolder +File.separator + "h2";
	
	
	public static void contributeApplicationDefaults(MappedConfiguration<String,String> configuration){
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES,"et,en");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE,"f4jf4jf34fjx435f");
		
	}
	
	 public static void bind(ServiceBinder binder){
		 binder.bind(FileManager.class,FileManagerImpl.class);
	 }
	 
	  @Startup
	  public void onStartup(RegistryShutdownHub shutdown,Server server, Session session,HibernateSessionManager manager) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException{
			
		  server.start();
		  
		  this.createAuthors(session,manager);
		  
		  shutdown.addRegistryShutdownListener(new Runnable(){

				@Override
				public void run() {
					server.stop();	
				}
				
			});
	  }
	 
	 
	  
	  private void createAuthors(Session session, HibernateSessionManager manager) {
		Author kadi = new Author();
		kadi.setKey("DI");
		kadi.setName("Kadi Kiho");
		session.saveOrUpdate(kadi);
		
		Author sille = new Author();	
		sille.setKey("SIL");
		sille.setName("Sille Seer");		
		session.saveOrUpdate(sille);
		manager.commit();
	}

	@Startup
	public Server buildServer(RegistryShutdownHub shutdown) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException{
		  	
			final Server server = Server.createTcpServer("-baseDir",new File(dbHomeDir).getAbsolutePath()).start();
			logger.debug("dbhome", dbHomeDir);
			logger.debug("port {}", server.getPort());
			logger.debug("url {}", server.getURL());
			return server;
	  }
	  
	  

	  
	  public static void contributeHibernateEntityPackageManager(Configuration<String> conf){
		  conf.add("website.model.database");
	  }
	  
	  public static void contributeHibernateSessionSource(OrderedConfiguration<HibernateConfigurer> config)
	  {
	    config.add("artbase", new HibernateConfigurer(){

			@Override
			public void configure(org.hibernate.cfg.Configuration conf) {
				
				conf.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
				conf.setProperty("hibernate.dialect","org.hibernate.dialect.H2Dialect");
				conf.setProperty("hibernate.connection.url","jdbc:h2:" + dbHomeDir + "/artmoments");
				conf.setProperty("hibernate.connection.username","artmoments");
				conf.setProperty("hibernate.connection.password","");
			}
	    	
	    });
	  }
	  
	  public void contributeAssetDispatcher(MappedConfiguration<String,AssetRequestHandler> conf){
		  conf.addInstance(FileManager.catalog, ImageDispatcher.class);
	  }

}
