package website.model.admin;

public enum FileSize {
	
	ORIGINAL(null,null),
	PREVIEW(600,500),
	THUMBNAIL(200,300);
	
	private final Integer maxWidthPx;
	private final Integer maxHeightPx;
	
	private FileSize(Integer maxWidthPx, Integer maxHeightPx) {
		this.maxWidthPx = maxWidthPx;
		this.maxHeightPx = maxHeightPx;
	}
	
	public Integer getMaxWidthPx() {
		return maxWidthPx;
	}

	public Integer getMaxHeightPx() {
		return maxHeightPx;
	}
}
