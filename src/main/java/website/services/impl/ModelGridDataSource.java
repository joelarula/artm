package website.services.impl;

import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

public class ModelGridDataSource extends HibernateGridDataSource{

	private final Criterion criterion;
	
	public ModelGridDataSource(Session session, Class entityType,Criterion criterion) {
		super(session, entityType);
		this.criterion = criterion;
	
	}
	
    protected void applyAdditionalConstraints(Criteria crit) {
    	crit.add(criterion);
    }
    

}
