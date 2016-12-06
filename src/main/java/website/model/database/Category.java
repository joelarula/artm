package website.model.database;

public enum Category {

	ABSTRACT("Abstraktsed maalid"),
	FLOWERS("Lillemaalid"),
	NATURE("Loodusmaalid"),
	FIGURATIVE("Figuratiivesed maalid"),
	CHILDRENS("Lastetoa maalid"),
	STILL_LIFE("Vaikelu maalid"),
	VARIA("Varia maalid"),
	SWEDISH_MODELS("Swedish models"),
	DRAWINGS("Drawings"),
	UNCATEGORIZED("Liigitamata");
	
	private final String name;
	
	Category(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
