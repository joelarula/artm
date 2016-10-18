package website.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;

public class WebsiteModule {

	
	 public static void contributeApplicationDefaults(MappedConfiguration<String,String> configuration){
		 configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		 configuration.add(SymbolConstants.SUPPORTED_LOCALES,"et,en");
	 }
}
