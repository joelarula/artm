package website.services;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.Block;

import website.model.admin.Language;
import website.model.database.Model;

public interface ModelDao {

	public  Model saveModel(Model model) throws IOException;

	public List<Model> search(String searchName, String searchCategory, Boolean searchUnPublished);

	public Model get(String modelKey);

	public void loadDatabase();

	public Collection<Model> getAllModels();
	
	public List<String> getCategories(Language l);

	public Model getCategoryLead(String category, Language l);

	public List<Model> getAllForCategory(String category, Language language);

	public Model getById(String string, Language language);
	
}
