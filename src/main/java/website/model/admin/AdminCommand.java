package website.model.admin;

public enum AdminCommand {

	MODEL("admin/board","model","model"),
	MODELS("admin/board","models","models"),
	SETTINGS("admin/board","settings","settings"), 
	LOGOUT("admin/logout",null,"logout");
	
	public String getPage() {
		return page;
	}
	public String getContext() {
		return context;
	}
	public String getLabel() {
		return label;
	}
	AdminCommand(String page, String context,String label){
		this.page = page;
		this.context = context;
		this.label = label;
	}
	
	private final String page;
	private final String context;
	private final String label;
}
