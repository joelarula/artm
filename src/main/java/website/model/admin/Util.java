package website.model.admin;

import java.util.HashMap;
import java.util.Map;

import website.model.database.Author;

public class Util {
	
	public static final  Map<String,Author> getAuthors() {
		
		Map<String,Author> a = new HashMap<String,Author>();
		Author kadi = new Author();
		kadi.setKey("DI");
		kadi.setName("Kadi Kiho");
		a.put(kadi.getKey(), kadi);
		
		Author sille = new Author();	
		sille.setKey("SIL");
		sille.setName("Sille Seer");	
		a.put(sille.getKey(), sille);
		
		Author tuuli = new Author();	
		tuuli.setKey("TUU");
		tuuli.setName("Tuuli Kotka");	
		a.put(tuuli.getKey(), tuuli);
		
		Author unknown = new Author();	
		unknown.setKey("ANON");
		unknown.setName("Autor puudub");	
		a.put(unknown.getKey(), unknown);
		
		return a;
	
	}

}
