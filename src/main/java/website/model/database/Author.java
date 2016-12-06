package website.model.database;

public enum Author {

	DI("Kadi Kiho"),
	SIL("Sille Seer"),
	TUU("Tuuli Kotka"),
	ANON("Autor puudub");

	Author(String author){
		this.name = author;
	}
	
	private final String name;
	
	public String getName() {
		return name;
	}
	

}
