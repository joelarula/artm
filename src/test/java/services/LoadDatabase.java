package services;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.modules.TapestryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.Size;
import website.model.database.Model;
import website.services.FileManager;
import website.services.ModelDao;
import website.services.WebsiteModule;
import junit.framework.TestCase;

public class LoadDatabase extends TestCase{
	
	private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

	
	public void testLoadDatbase() throws SQLException, IOException{
		logger.info("starting");
		RegistryBuilder builder = new RegistryBuilder();		 
		builder.add(WebsiteModule.class,TapestryModule.class);		 
		Registry registry = builder.build();			
		registry.performRegistryStartup();
		
		ModelDao dao = registry.getService(ModelDao.class);
		FileManager fm = registry.getService(FileManager.class);
		dao.loadDatabase();
		logger.info("calculating");
		for(Model m : dao.getAllModels()){
			
			Size size = fm.getGraphics(m.getKey());
			Integer h = size.getHeight();
			Integer w = size.getWidth();
			Double proportion = h.doubleValue() / w.doubleValue();
			logger.info("{} {} "+"w="+w+" h="+h,m.getName(),proportion);
			m.setSize(size);
			//fm.saveModel(m);
			
			
		}
		
	}

}
