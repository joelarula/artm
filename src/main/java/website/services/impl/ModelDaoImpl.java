package website.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.Language;
import website.model.database.Model;
import website.services.FileManager;
import website.services.ModelDao;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ModelDaoImpl implements ModelDao{

	private Map<String,Model> models = new HashMap<String,Model>();
	
	private Set<String> categories_et = new HashSet<String>();
	
	private Set<String> categories_en = new HashSet<String>();
	
	private Set<String> categories_ru = new HashSet<String>();
	
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
	public List<Model> search(String searchName, String searchCategory, Boolean unPublished) {
		
		Stream<Model>  s = new ArrayList<Model>(models.values()).stream();
		if(searchCategory != null){
			s = s.filter(m -> m.getCategory().getName().equals(searchCategory));
		}if(unPublished != null){
			s = s.filter(m-> !m.isPublished());
		}if(searchName != null){
			s = s.filter(m -> m.getName().toLowerCase().contains(searchName.toLowerCase()));
		}
		
		return s.collect(Collectors.toList());
	}

	@Override
	public void loadDatabase(){
		logger.info("loading database");
		long start = System.currentTimeMillis();
		this.models = this.fileManager.loadModels();
		logger.info("loading database {} models elapsed {}",this.models.size(),System.currentTimeMillis()-start);
		loadCategories();
	}

	private void loadCategories() {
		for(Model m: this.getAllModels()){
			
			if(m.getCategory() != null && (m.getCategory().getName() != null && !m.getCategory().getName().isEmpty())){
				this.categories_et.add(m.getCategory().getName());
			}
			
			if(m.getCategory() != null && (m.getCategory().getName_en()!= null && !m.getCategory().getName_en().isEmpty())){
				this.categories_en.add(m.getCategory().getName_en());
			}
			
			if(m.getCategory() != null && (m.getCategory().getName_ru() != null && !m.getCategory().getName_ru().isEmpty())){
				this.categories_ru.add(m.getCategory().getName_ru());
			}
		}
		
	}

	@Override
	public Collection<Model> getAllModels() {
		return this.models.values();
	}

	@Override
	public List<String> getCategories(Language l) {
		switch(l){
			case ET: return new ArrayList<String>(this.categories_et);
			case EN: return new ArrayList<String>(this.categories_en);
			case RU: return new ArrayList<String>(this.categories_ru);
		}
		return null;
	}

}
