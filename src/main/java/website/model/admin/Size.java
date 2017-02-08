package website.model.admin;

import java.io.Serializable;

public class Size implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private int width;
	
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	private int height;
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public Double getProportion() {
		return Integer.valueOf(height).doubleValue() / Integer.valueOf(width).doubleValue();
	}
	
	public Size() {}
	
	public Size(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}
}
