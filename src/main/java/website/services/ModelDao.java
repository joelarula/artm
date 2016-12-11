package website.services;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import website.model.database.Author;
import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;

public interface ModelDao {

	public  Model saveModel(Model model) throws IOException;

	public List<Model> search(String searchName, Category searchCategory, Stock searchStock, Boolean searchUnPublished, Author author);

	public Model get(String modelKey);

	public void loadDatabase();

	public Collection<Model> getAllModels();
	
}
