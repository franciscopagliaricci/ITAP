package utils;

import java.awt.Color;
import java.awt.Point;

import model.ColorImage;
import model.Image;

public class BinaryImageManager {

	public static Image createSquare(int height, int width) {
		Image binaryImage = new ColorImage(height, width,
				Image.ImageFormat.BMP, Image.ImageType.RGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = (y > height / 4 && y < height * 3 / 4
						&& x > width / 4 && x < width * 3 / 4) ? Color.WHITE
						: Color.BLACK;
				binaryImage.setPixel(x, y, color);
			}
		}
		return binaryImage;
	}

	public static Image createCircle(int height, int width) {
		Image binaryImage = new ColorImage(height, width,
				Image.ImageFormat.BMP, Image.ImageType.RGB);

		int radius = Math.min(height, width) / 4;
		Point center = new Point(width / 2, height / 2);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				Color color = (Math.pow(x - center.x, 2)
						+ Math.pow(y - center.y, 2) < Math.pow(radius, 2)) ? Color.WHITE
						: Color.BLACK;
				binaryImage.setPixel(x, y, color);
			}
		}
		return binaryImage;
	}
}
