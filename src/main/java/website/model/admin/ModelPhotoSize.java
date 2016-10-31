package website.model.admin;

public enum ModelPhotoSize {
	
	ORIGINAL(null,null),
	FULL_SCREEN(1024d,768d),
	PREVIEW(600d,500d),
	THUMBNAIL(200d,300d);
	
	private final Double maxWidthPx;
	private final Double maxHeightPx;
	
	private ModelPhotoSize(Double maxWidthPx, Double maxHeightPx) {
		this.maxWidthPx = maxWidthPx;
		this.maxHeightPx = maxHeightPx;
	}
	
	public Double getMaxWidthPx() {
		return maxWidthPx;
	}

	public Double getMaxHeightPx() {
		return maxHeightPx;
	}
}
