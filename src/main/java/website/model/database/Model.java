package website.model.database;

import java.util.Date;
import org.apache.tapestry5.ioc.services.PropertyAccess;



public class Model {
	

	private String key;
	
	private String name;
	
	private String description;
	
	private String photo;
	
	private String detail_0;
	
	private String detail_1;
	
	private String detail_2;

	private Category category;
	
	private String translation_en;
	
	private String translation_ru;
	

	public String getTranslation_en() {
		return translation_en;
	}

	public void setTranslation_en(String translation_en) {
		this.translation_en = translation_en;
	}
	
	private boolean published;
	
	private Date created;
	
	private Date modified;


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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getTranslation(String language, PropertyAccess ac) {	
		return (String) ac.get(this, "translation_"+language);
	}

	public void setTranslation(String language, String translation, PropertyAccess ac) {
		ac.set(this, "translation_"+language, translation);
	}

	public String getTranslation_ru() {
		return translation_ru;
	}

	public void setTranslation_ru(String translation_ru) {
		this.translation_ru = translation_ru;
	}

	public String getDetail_0() {
		return detail_0;
	}

	public void setDetail_0(String detail_0) {
		this.detail_0 = detail_0;
	}

	public String getDetail_1() {
		return detail_1;
	}

	public void setDetail_1(String detail_1) {
		this.detail_1 = detail_1;
	}

	public String getDetail_2() {
		return detail_2;
	}

	public void setDetail_2(String detail_2) {
		this.detail_2 = detail_2;
	}

}
