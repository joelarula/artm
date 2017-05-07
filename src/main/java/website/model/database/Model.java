package website.model.database;

import java.util.Date;
import org.apache.tapestry5.ioc.services.PropertyAccess;

import website.model.admin.Size;



public class Model {
	
	@Override
	public String toString() {
		return "Model [key=" + key + ", name=" + name + ", photo=" + photo
				+ ", author=" + author + ", alias=" +
				 ", translation_en=" + translation_en + ", category="
				+ category + ", stock=" + stock + ", published=" + published
				+ ", created=" + created + ", modified=" + modified + "]";
	}

	private String key;
	
	private String name;
	
	private String description;
	
	private String photo;
	
	private Author author;
	
	private String translation_en;
	
	private Size size;
	
	private Integer position = 5;
	

	public String getTranslation_en() {
		return translation_en;
	}

	public void setTranslation_en(String translation_en) {
		this.translation_en = translation_en;
	}
	
	private Category category;
	
	private Stock stock;
	
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

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
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
	
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}
	
	
}
