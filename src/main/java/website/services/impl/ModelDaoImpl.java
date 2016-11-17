package website.services.impl;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.database.Model;
import website.pages.admin.Board;
import website.services.ModelDao;

public class ModelDaoImpl implements ModelDao{

	private static final Logger logger = LoggerFactory.getLogger(ModelDao.class);

	
	private Session session;
	
	public ModelDaoImpl(Session session){
		this.session = session;
	}
	
	@Override
	public Model saveModel(Model model) {
		this.session.saveOrUpdate(model);
		logger.info("model {} saved",model.getKey());
		return model;
	}

}
