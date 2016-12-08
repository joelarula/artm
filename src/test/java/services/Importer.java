package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.modules.TapestryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.ModelPhotoSize;
import website.model.database.Author;
import website.model.database.Category;
import website.model.database.Model;
import website.services.FileManager;
import website.services.ModelDao;
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
	
	
	public void testImportOld() throws SQLException, IOException{
		logger.info("starting");
		RegistryBuilder builder = new RegistryBuilder();		 
		builder.add(WebsiteModule.class,TapestryModule.class);		 
		Registry registry = builder.build();	
		
		registry.performRegistryStartup();
		
		String nodeCount = getNodesCount();
		logger.info("nodes {}",nodeCount);
		
		processNodesandCategories(registry);
		
		logger.info("done");
	}


	synchronized private void processNodesandCategories(Registry registry) throws SQLException, IOException {

		logger.info("running {}",paintings);
		Connection connection = getConnection();
		PreparedStatement st = connection.prepareStatement(paintings);
		ResultSet res = st.executeQuery();
		
		logger.info("running {}",drawings);
		Connection connection2 = getConnection();
		PreparedStatement st2 = connection2.prepareStatement(drawings);
		ResultSet res2 = st2.executeQuery();
		
		ModelDao dao = registry.getService(ModelDao.class);
		FileManager fm = registry.getService(FileManager.class);
		int index = 1;
		while(res.next()){		
			processResult(res,"P",dao,fm,index);  
			index++;
        }
		
		while(res2.next()){		
			processResult(res2,"D",dao,fm,index);
			index++;
        }
		
		res.close();
		st.close();
		res2.close();
		st2.close();
		connection2.close();
		connection.close();
	}


	private void processResult(ResultSet res, String topic, ModelDao dao, FileManager fm, int index) throws SQLException {
        Model m = new Model();  
        m.setName(res.getString("title").trim());
        m.setPhoto(res.getString("filename"));      
        Integer code = res.getInt("nid");
        List<String> t = this.getTerms(code);  
        Author a = this.getAuthor(m,t);
        List<Category> c = this.getCategories(t,topic);
        if(c.isEmpty()){     	
        	m.setCategory(Category.UNCATEGORIZED);
        }else{
        	 m.setCategory(c.get(0));   
        }
       
        m.setAuthor(a);
        m.setKey(UUID.randomUUID().toString());
        
        String mPath = null;
        if(topic.equals("P")){
        	mPath="C:\\dev\\artmoments\\pildid\\files\\maalid\\"+m.getPhoto();
        }else{
        	mPath="C:\\dev\\artmoments\\pildid\\files\\"+m.getPhoto();
        }
        
        try {
			dao.saveModel(m);
		} catch (IOException e) {
			logger.error("failed MODEL {}",m.getName());
		}
        File f = new File(mPath);
        if(f.exists()){
        	  try {
				fm.savePhoto(m.getKey(),ModelPhotoSize.ORIGINAL,new FileInputStream(f),mPath);
			} catch (IOException e) {
				logger.error("failed PHOTO {}",m.getName());
			} catch (Exception e) {
				logger.error("failed PHOTO {}",m.getName());
			}
        }else{
        	logger.info("NO FILE");
        }
        
        logger.info("{} {}",index +"  "+ a.getName()+" "+ m.getName(),m.getCategory().toString()+" "+ m.getPhoto());
		
	}


	private List<Category> getCategories(List<String> t, String topic) {
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
			}
		}
		
		if(topic.equals("D")){
			cat.add(Category.DRAWINGS);
		}
		
		return cat;
	}


	private Author getAuthor(Model m, List<String> t) {
        Author a = null;
		if(t.contains(Author.DI.getName())){
        	a = Author.DI;
        	t.remove(Author.DI.getName());
        }else if(t.contains(Author.SIL.getName())){
        	a = Author.SIL;
        	t.remove(Author.SIL.getName());
        }else if(t.contains(Author.TUU.getName())){
        	a = Author.TUU;
        	t.remove(Author.TUU.getName());
        }else{
        	 if(m.getName().startsWith("di")){
        		 a = Author.DI;
        	 }else if(m.getName().startsWith("Sil")){
        		 a = Author.SIL;
        	 }else{
            	 a = Author.ANON;
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
