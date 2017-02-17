package website.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
	
	private List<String> categories_et = new ArrayList<String>();
	
	private List<String> categories_en = new ArrayList<String>();
	
	private List<String> categories_ru = new ArrayList<String>();
		
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
		for(Language l :Language.values()){
			switch(l){
			case ET: this.categories_et.add(model.getCategory().getName());
			case EN: this.categories_en.add(model.getCategory().getName_en());
			case RU: this.categories_ru.add(model.getCategory().getName_ru());
			}
		}
		
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

	@Override
	public Model getCategoryLead(String category,Language l) {
		Optional<Model> model =  this.models.values().stream()
			.filter(m -> m.getCategoryTranslation(l) != null)
			.filter(m -> m.getCategoryTranslation(l).equals(category))
			.sorted((m1,m2)-> (m1.getPosition().compareTo(m2.getPosition())))
			.findFirst();
		
		if(model.isPresent()){
			return model.get();
		}else{
			return null;
		}
	}

	@Override
	public List<Model> getAllForCategory(String category, Language l) {
		return this.models.values().stream()
		.filter(m -> m.getCategoryTranslation(l) != null)
		.filter(m -> m.getCategoryTranslation(l).equals(category))
		.sorted((m1,m2)-> (m1.getSize().getProportion().compareTo(m2.getSize().getProportion())))
		.collect(Collectors.toList());
	}

	@Override
	public Model getById(String name, Language language) {
		Optional<Model> model = this.models.values().stream().filter(m-> m.getKey().equals(name)).findFirst();
		
		return  model.isPresent() ? model.get() :  this.models.values().stream().filter(m-> {
			
			switch(language){
				case ET: if(m.getName()!= null && m.getName().equals(name))return true;
				case EN: if(m.getTranslation_en() != null && m.getTranslation_en().equals(name))return true;
				case RU: if(m.getTranslation_ru() != null && m.getTranslation_ru().equals(name))return true;
					
			}
			return false;
		})
		.findFirst().get();	
	}

}
