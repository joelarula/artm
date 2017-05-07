package services;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.modules.TapestryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import website.model.admin.ModelPhotoSize;
import website.services.FileManager;
import website.services.WebsiteModule;

public class ImageManipulationTest extends TestCase{
	
	private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);
	
//	public void testLoadDatbase() throws SQLException, IOException{
//		logger.info("starting");
//		RegistryBuilder builder = new RegistryBuilder();		 
//		builder.add(WebsiteModule.class,TapestryModule.class);		 
//		Registry registry = builder.build();			
//		registry.performRegistryStartup();
//		
//		FileManager fm = registry.getService(FileManager.class);
//		
//		
//		fm.getPhoto("testThumb", ModelPhotoSize.THUMBNAIL);
//		fm.getPhoto("testThumb", ModelPhotoSize.THUMBNAIL2);
//		fm.getPhoto("testThumb", ModelPhotoSize.PREVIEW);
//		fm.getPhoto("testThumb", ModelPhotoSize.FULL_SCREEN);
//		
//	}

}
