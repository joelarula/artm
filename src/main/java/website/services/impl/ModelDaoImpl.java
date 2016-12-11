package website.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import website.model.database.Author;
import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;
import website.services.FileManager;
import website.services.ModelDao;

public class ModelDaoImpl implements ModelDao{

	private Map<String,Model> models = new HashMap<String,Model>();
	
	private static final Logger logger = LoggerFactory.getLogger(ModelDao.class);
	
	private final FileManager fileManager;
	
	public ModelDaoImpl(FileManager fileManager){	
		this.fileManager = fileManager;
	}
	
	@Override
	public Model saveModel(Model model) throws IOException {	
		this.fileManager.saveModel(model);
		logger.info("model {} saved",model.getKey());
		this.models.put(model.getKey(), model);
		return model;
	}


	@Override
	public Model get(String modelKey)  {
		
		Model model = this.models.get(modelKey);
		if(model != null){
			return model;
		}
		logger.warn("model {}  not found in memory {}",modelKey);
		try {
			return this.fileManager.getModel(modelKey);
		} catch (JsonParseException e) {
			logger.error(e.toString());
		} catch (JsonMappingException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		logger.warn("model {}  not found in disk {}",modelKey);
		return null;
	}

	@Override
	public List<Model> search(String searchName, Category searchCategory,Stock searchStock, Boolean unPublished,Author author) {
		
		Stream<Model>  s = new ArrayList<Model>(models.values()).stream();
		if(searchCategory != null){
			s = s.filter(m -> m.getCategory().equals(searchCategory));
		}if(searchStock != null){
			s = s.filter(m -> m.getStock().equals(searchStock));
		}if(author != null){
			s = s.filter(m -> m.getAuthor().equals(author));
		}if(unPublished != null){
			s = s.filter(m-> !m.isPublished());
		}if(searchName != null){
			
		}
		
		return s.collect(Collectors.toList());
	}

	@Override
	public void loadDatabase(){
		logger.info("loading database");
		long start = System.currentTimeMillis();
		this.models = this.fileManager.loadModels();
		logger.info("loading database {} models elapsed {}",this.models.size(),System.currentTimeMillis()-start);
		
	}

}
