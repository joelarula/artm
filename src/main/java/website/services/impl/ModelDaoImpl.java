package website.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import website.model.database.Category;
import website.model.database.Model;
import website.model.database.Stock;
import website.services.ModelDao;

public class ModelDaoImpl implements ModelDao{

	private Map<String,Model> models = new HashMap<String,Model>();
	
	private static final Logger logger = LoggerFactory.getLogger(ModelDao.class);

	private final ObjectMapper mapper = new ObjectMapper();
	
	public ModelDaoImpl(){}
	
	@Override
	public Model saveModel(Model model) {
		logger.info("model {} saved",model.getKey());
		return model;
	}


	@Override
	public Model get(String modelKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Model> search(String searchName, Category searchCategory,Stock searchStock) {
		if(searchCategory != null){
			
		}
		if(searchStock != null){
			
		}
		if(searchName != null){
			
		}
		
		return this.models;
	}

}
