package website.services;

import java.util.List;

import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;

public interface ModelDao {

	public  Model saveModel(Model model);

	public List<Model> search(String searchName, Category searchCategory, Stock searchStock);

	public Model get(String modelKey);
	
}
