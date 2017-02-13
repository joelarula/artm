package website.model.admin;

public enum ModelPhotoSize {
	
	ORIGINAL(null,null),
	FULL_SCREEN(1024d,768d),
	PREVIEW(620d,500d),
	THUMBNAIL(250d,300d),
	THUMBNAIL2(200d,250d),
	ICON(125d,150d);
	
	
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
