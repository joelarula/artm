package website.model.admin;

public enum ModelPhotoSize {
	
	ORIGINAL(null,null,FileFormat.PNG),
	FULL_SCREEN(1024d,768d,FileFormat.JPEG),
	PREVIEW(620d,500d,FileFormat.JPEG),
	THUMBNAIL(250d,300d,FileFormat.JPEG),
	THUMBNAIL2(200d,250d,FileFormat.JPEG),
	ICON(125d,150d,FileFormat.JPEG);
	
	
	public final Double maxWidthPx;
	public final Double maxHeightPx;
	public final FileFormat format;
	
	private ModelPhotoSize(Double maxWidthPx, Double maxHeightPx,FileFormat format) {
		this.maxWidthPx = maxWidthPx;
		this.maxHeightPx = maxHeightPx;
		this.format = format;
	}
	
}
