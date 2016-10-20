package website.services;

import java.io.File;
import java.sql.SQLException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.ClientCommand;

public class WebsiteModule {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteModule.class);

	private static final String WEBSITE_HOME = "website";
	public static final String websiteFolder = System.getProperty("user.home")+File.separator+WEBSITE_HOME;
	
	
	
	public static void contributeApplicationDefaults(MappedConfiguration<String,String> configuration){
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES,"et,en");
	}
	 
	 
	  @Startup
	  public void onStartup(RegistryShutdownHub shutdown) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException{
		  
			String dbHomeDir = websiteFolder +File.separator + "h2";
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
	  

}
