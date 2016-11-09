package website.model.database;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Author {

	@Id
	private String key;
	
	private String name;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
