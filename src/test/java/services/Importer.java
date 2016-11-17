package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	
	public void testImportOld() throws SQLException{
		logger.info("starting");
		RegistryBuilder builder = new RegistryBuilder();		 
		builder.add(WebsiteModule.class,TapestryModule.class,HibernateCoreModule.class);		 
		Registry registry = builder.build();	
		
		registry.performRegistryStartup();
		
		String nodeCount = getNodesCount();
		processNodesandCategories();
		logger.info("nodes {}",nodeCount);
		logger.info("done");
	}


	private void processNodesandCategories() throws SQLException {
		Connection connection = getConnection();
		
	}


	private Connection getConnection() throws  SQLException {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return  DriverManager.getConnection("jdbc:mariadb://localhost:3306/atmoments", "root", "");  
		
	}


	private String getNodesCount() {
		
	        try {
				Connection connection = this.getConnection(); 				
				PreparedStatement st = connection.prepareStatement("select count(*) from node");
				ResultSet res = st.executeQuery();
				if(res.next()){
		           return res.getString(1);
		        }
				
	        
			} catch (SQLException e) {
				e.printStackTrace();
			}  
	      
	        return "failed";
	}
	
}
