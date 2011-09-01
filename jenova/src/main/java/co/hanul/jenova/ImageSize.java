package co.hanul.jenova;

import org.springframework.roo.addon.tostring.RooToString;

@RooToString
public class ImageSize {

	private int width;
	private int height;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
