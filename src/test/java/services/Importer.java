package services;

import junit.framework.TestCase;

import org.apache.tapestry5.hibernate.modules.HibernateCoreModule;
import org.apache.tapestry5.hibernate.modules.HibernateModule;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.modules.TapestryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.services.WebsiteModule;


public class Importer extends TestCase{
	
	private static final Logger logger = LoggerFactory.getLogger(Importer.class);

	
	public void testImportOld(){
		logger.info("starting");
		RegistryBuilder builder = new RegistryBuilder();		 
		builder.add(WebsiteModule.class,TapestryModule.class,HibernateCoreModule.class);		 
		Registry registry = builder.build();	 
		registry.performRegistryStartup();
		logger.info("done");
	}
	
}
