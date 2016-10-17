package website.model.admin;

public enum AdminCommand {

	PAINTING("admin/board","painting","paintings"),
	PAINTINGS("admin/board","paintings","paintings"),
	SETTINGS("admin/board","settings","settings");
	
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
