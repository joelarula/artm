package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tapestry5.hibernate.modules.HibernateCoreModule;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.modules.TapestryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;










import website.model.admin.Util;
import website.model.database.Author;
import website.model.database.Category;
import website.model.database.Model;
import website.services.WebsiteModule;


public class Importer extends TestCase{
	
	private static final Logger logger = LoggerFactory.getLogger(Importer.class);
	
	private final String paintings = 
			"select n.nid, n.type, n.title, n.created, cfp.field_painting_fid, f.filename from node n "+
			" inner join content_field_painting cfp on n.nid = cfp.nid "+ 
			" inner join files f on f.fid = cfp.field_painting_fid "+
			" where n.type in('painting')";
	
	private final String drawings  = 
			" select n.nid, n.type, n.title, n.created, cfp.field_photo_fid, f.filename, f.filepath from   node n " +
			" inner join content_field_photo cfp on n.nid = cfp.nid " +
			" inner join files f on f.fid = cfp.field_photo_fid " +
			" where n.type in('joonistus')";

	private final String terms = "select td.name from term_node tn inner join term_data td on tn.tid = td.tid where tn.nid = ?";
	
	
	public void testImportOld() throws SQLException{
		logger.info("starting");
		RegistryBuilder builder = new RegistryBuilder();		 
		builder.add(WebsiteModule.class,TapestryModule.class,HibernateCoreModule.class);		 
		Registry registry = builder.build();	
		
		registry.performRegistryStartup();
		
		String nodeCount = getNodesCount();
		logger.info("nodes {}",nodeCount);
		
		processNodesandCategories();
		
		logger.info("done");
	}


	synchronized private void processNodesandCategories() throws SQLException {

		logger.info("running {}",paintings);
		Connection connection = getConnection();
		PreparedStatement st = connection.prepareStatement(paintings);
		ResultSet res = st.executeQuery();
		
		logger.info("running {}",drawings);
		Connection connection2 = getConnection();
		PreparedStatement st2 = connection2.prepareStatement(drawings);
		ResultSet res2 = st2.executeQuery();
		
		
		while(res.next()){		
			processResult(res,"P");  
        }
		
		while(res2.next()){		
			processResult(res2,"D");  
        }
		
		res.close();
		st.close();
		res2.close();
		st2.close();
		connection2.close();
		connection.close();
	}


	private void processResult(ResultSet res, String topic) throws SQLException {
        Model m = new Model();  
        m.setName(res.getString("title").trim());
        m.setPhoto(res.getString("filename"));      
        Integer code = res.getInt("nid");
        List<String> t = this.getTerms(code); 
       
         Author a = this.getAuthor(m,t);
         List<Category> c = this.getCategories(t);
         String key = a.getKey()+"-"+ String.valueOf(code);
        
        String photo = res.getString("filename");      
        logger.info("{} {}",topic +" "+key+" "+ a.getName()+" "+ m.getName(),c.toString()+" "+ photo);
		
	}


	private List<Category> getCategories(List<String> t) {
		List<Category> cat = new ArrayList<Category>();
		boolean added = false;
		for(String tt : t){
			for(Category c : Category.values()){
				if(tt.equals(c.getName())){
					cat.add(c);
					added = true;
				}
			}
			if(!added){
				cat.add(Category.UNCATEGORIZED);
				logger.info("NOT ADDED {}",t);
			}
		}
		
		return cat;
	}


	private Author getAuthor(Model m, List<String> t) {
        Author a = null;
		if(t.contains(Util.getAuthors().get("DI").getName())){
        	a = Util.getAuthors().get("DI");
        	t.remove(Util.getAuthors().get("DI").getName());
        }else if(t.contains(Util.getAuthors().get("SIL").getName())){
        	a = Util.getAuthors().get("SIL");
        	t.remove(Util.getAuthors().get("SIL").getName());
        }else if(t.contains(Util.getAuthors().get("TUU").getName())){
        	a = Util.getAuthors().get("TUU");
        	t.remove(Util.getAuthors().get("TUU").getName());
        }else{
        	 if(m.getName().startsWith("di")){
        		 a = Util.getAuthors().get("DI");
        	 }else if(m.getName().startsWith("Sil")){
        		 a = Util.getAuthors().get("SIL");
        	 }else{
            	 a = Util.getAuthors().get("ANON");
        	 }	

        }
		return a;
	}


	private List<String> getTerms(int nid) throws SQLException {
		List<String> t = new ArrayList<String>();
		Connection connection = getConnection();
		PreparedStatement st = connection.prepareStatement(terms);
		st.setInt(1, nid);
		ResultSet res = st.executeQuery();
		while(res.next()){
          t.add(res.getString("name"));    
        }
		res.close();
		st.close();
		connection.close();
		return t;
	}


	synchronized private Connection getConnection() throws  SQLException {
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
				res.close();
				st.close();
	        
			} catch (SQLException e) {
				e.printStackTrace();
			}  
	      
	        return "failed";
	}
	
}
