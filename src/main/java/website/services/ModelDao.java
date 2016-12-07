package website.services;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;

public interface ModelDao {

	public  Model saveModel(Model model) throws IOException;

	public List<Model> search(String searchName, Category searchCategory, Stock searchStock);

	public Model get(String modelKey);
	
}
