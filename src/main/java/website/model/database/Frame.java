package website.model.database;

public enum Frame {

	FRAME20x20(50,50),
	FRAME50x50(50,50),
	FRAME100x100(100,100);
	


	private final int widthCm;
	private final int heightCm;
	
	private Frame(int widthCm, int heightCm) {
		this.widthCm = widthCm;
		this.heightCm = heightCm;
	}

	public int getWidthCm() {
		return widthCm;
	}

	public int getHeightCm() {
		return heightCm;
	}
}
