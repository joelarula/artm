package website.services;

import java.io.File;
import java.sql.SQLException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateConfigurer;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.ClientCommand;
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
	  public void onStartup(RegistryShutdownHub shutdown) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException{
		  
			
			final Server server = Server.createTcpServer("-baseDir",new File(dbHomeDir).getAbsolutePath()).start();

			logger.debug("dbhome", dbHomeDir);
			logger.debug("port {}", server.getPort());
			logger.debug("url {}", server.getURL());
		
			//Class.forName("org.h2.Driver");
			//Connection con = DriverManager.getConnection("jdbc:h2:~/xxx", "", "" );
			
			shutdown.addRegistryShutdownListener(new Runnable(){

				@Override
				public void run() {
					server.stop();	
				}
				
			});

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
//		          <property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
//		          <property name="javax.persistence.jdbc.url"    value="jdbc:h2:mem:test" />
//		          <property name="javax.persistence.jdbc.user" value="sa" />
//		          <property name="javax.persistence.jdbc.password" value="" />
//		            <property name="hibernate.connection.driver_class">org.h2.Driver</property>
//		            <property name="hibernate.connection.url">jdbc:hsqldb:./target/work/t5_tutorial1;shutdown=true</property>
//		            <property name="hibernate.dialect">org.hibernate.dialect.HSQLDialect</property>
//		            <property name="hibernate.connection.username">sa</property>
//		            <property name="hibernate.connection.password"></property>
			}
	    	
	    });
	  }
	  
	  public void contributeAssetDispatcher(MappedConfiguration<String,AssetRequestHandler> conf){
		  conf.addInstance(FileManager.catalog, ImageDispatcher.class);
	  }

}
