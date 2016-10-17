package website.model.admin;

public enum ClientCommand {
	
	HOME("index","home","home"),
	PAINTINGS("index","paintings","paintings"),
	SHOP("index","shop","shop"),
	CONTACT("index","contact","contact");
	
	public String getPage() {
		return page;
	}
	public String getContext() {
		return context;
	}
	public String getLabel() {
		return label;
	}
	
	ClientCommand(String page, String context,String label){
		this.page = page;
		this.context = context;
		this.label = label;
	}
	
	private final String page;
	private final String context;
	private final String label;
}
