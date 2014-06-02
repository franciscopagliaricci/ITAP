package model;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import utils.Mask;

public class Channel implements Cloneable {

	static final int MIN_CHANNEL_COLOR = 0;
	static final int MAX_CHANNEL_COLOR = 255;

	private int width;
	private int height;

	// The matrix is represented by an array, and to get a pixel(x,y) make y *
	// this.getWidth() + x
	private double[] channel;

	public Channel(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(
					"Images must have at least 1x1 pixel size");
		}

		this.width = width;
		this.height = height;
		this.channel = new double[width * height];
	}

	/**
	 * Indicates whether a coordinate is valid for a pixel
	 * 
	 * @param x
	 * @param y
	 * @return True if the pixel is valid
	 */
	public boolean validPixel(int x, int y) {
		boolean validX = x >= 0 && x < this.getWidth();
		boolean validY = y >= 0 && y < this.getHeight();
		return validX && validY;
	}

	/**
	 * Returns the Channel height
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the Channel width
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets a pixel(x,y) in the channel
	 * 
	 * @param x
	 * @param y
	 * @param color
	 */
	public void setPixel(int x, int y, double color) {
		if (!validPixel(x, y)) {
			throw new IndexOutOfBoundsException();
		}

		channel[y * this.getWidth() + x] = color;
	}

	/**
	 * Returns a pixel in the position x,y
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double getPixel(int x, int y) {
		if (!validPixel(x, y)) {
			throw new IndexOutOfBoundsException();
		}

		return channel[y * this.getWidth() + x];
	}

	int truncatePixel(double originalValue) {
		if (originalValue > Channel.MAX_CHANNEL_COLOR) {
			return Channel.MAX_CHANNEL_COLOR;
		} else if (originalValue < Channel.MIN_CHANNEL_COLOR) {
			return Channel.MIN_CHANNEL_COLOR;
		}
		return (int) originalValue;
	}

	public void add(Channel otherChannel) {
		for (int x = 0; x < width && x < otherChannel.width; x++) {
			for (int y = 0; y < height && y < otherChannel.height; y++) {
				double color = this.getPixel(x, y)
						+ otherChannel.getPixel(x, y);
				this.setPixel(x, y, color);
			}
		}
	}

	public void substract(Channel otherChannel) {
		for (int x = 0; x < width && x < otherChannel.width; x++) {
			for (int y = 0; y < height && y < otherChannel.height; y++) {
				double color = this.getPixel(x, y)
						- otherChannel.getPixel(x, y);
				color = Math.abs(color);
				this.setPixel(x, y, color);
			}
		}
	}

	public void multiply(Channel otherChannel) {
		for (int x = 0; x < width && x < otherChannel.width; x++) {
			for (int y = 0; y < height && y < otherChannel.height; y++) {
				double color = this.getPixel(x, y)
						* otherChannel.getPixel(x, y);
				this.setPixel(x, y, color);
			}
		}
	}

	public void multiply(double scalar) {
		for (int i = 0; i < this.channel.length; i++) {
			this.channel[i] *= scalar;
		}
	}

	public void negative() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double color = this.getPixel(x, y);
				// T(r) = -r + L - 1 -- (L - 1 is represented by
				// MAX_CHANNEL_COLOR)
				this.setPixel(x, y, -color + MAX_CHANNEL_COLOR);
			}
		}
	}

	@Override
	public Channel clone() {
		Channel newChannel = new Channel(width, height);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newChannel.setPixel(i, j, this.getPixel(i, j));
			}
		}
		return newChannel;
	}

	public void dynamicRangeCompression(double R) {
		double L = MAX_CHANNEL_COLOR;
		double c = (L - 1) / Math.log(1 + R);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double r = this.getPixel(x, y);
				double color = (double) (c * Math.log(1 + r));
				this.setPixel(x, y, color);
			}
		}
	}

	/**
	 * Applies one iteration of anisotropic diffusion.
	 * 
	 * @param bd
	 * @return
	 */
	public Channel applyAnisotropicDiffusion(BorderDetector bd) {
		Channel newChannel = new Channel(width, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double oldValueIJ = getPixel(x, y);

				double DnIij = x > 0 ? getPixel(x - 1, y) - oldValueIJ : 0;
				double DsIij = x < width - 1 ? getPixel(x + 1, y) - oldValueIJ
						: 0;
				double DeIij = y < height - 1 ? getPixel(x, y + 1) - oldValueIJ
						: 0;
				double DoIij = y > 0 ? getPixel(x, y - 1) - oldValueIJ : 0;

				double Cnij = bd.g(DnIij);
				double Csij = bd.g(DsIij);
				double Ceij = bd.g(DeIij);
				double Coij = bd.g(DoIij);

				double DnIijCnij = DnIij * Cnij;
				double DsIijCsij = DsIij * Csij;
				double DeIijCeij = DeIij * Ceij;
				double DoIijCoij = DoIij * Coij;

				double lambda = 0.25;
				double newValueIJ = oldValueIJ + lambda
						* (DnIijCnij + DsIijCsij + DeIijCeij + DoIijCoij);
				newChannel.setPixel(x, y, newValueIJ);
			}
		}
		return newChannel;
	}

	public void applyMask(Mask mask) {
		Channel newChannel = new Channel(this.width, this.height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double newPixel = applyMask(x, y, mask);
				newChannel.setPixel(x, y, newPixel);
			}
		}
		this.channel = newChannel.channel;
	}

	private double applyMask(int pointX, int pointY, Mask mask) {
		double newColor = 0;
		for (int x = -mask.getWidth() / 2; x <= mask.getWidth() / 2; x++) {
			for (int y = -mask.getHeight() / 2; y <= mask.getHeight() / 2; y++) {
				if (this.validPixel(pointX + x, pointY + y)) {
					double oldColor = 0;
					try {
						oldColor = this.getPixel(pointX + x, pointY + y);
						newColor += oldColor * mask.getValue(x, y);
					} catch (IndexOutOfBoundsException e) {
						// newColor += oldColor * mask.getValue(x, y);
					}
				}
			}
		}
		return newColor;
	}

	public void applySusan(Mask mask, double threshold, double common,
			double side, double corner) {
		Channel newChannel = new Channel(this.width, this.height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int count = applySusan(x, y, mask, threshold);
				double sr0 = 1 - (((double) count) / 37);
				double color;
				if (sr0 > 0.625)
					color = corner;
				else if (sr0 > 0.25)
					color = side;
				else
					color = common;
				newChannel.setPixel(x, y, color);
			}
		}
		this.channel = newChannel.channel;
	}

	private int applySusan(int pointX, int pointY, Mask mask, double threshold) {
		int count = 0;
		for (int x = -mask.getWidth() / 2; x <= mask.getWidth() / 2; x++) {
			for (int y = -mask.getHeight() / 2; y <= mask.getHeight() / 2; y++) {
				if (this.validPixel(pointX + x, pointY + y)) {
					try {
						double difference = (this.getPixel(pointX + x, pointY
								+ y) * mask.getValue(x, y))
								- this.getPixel(pointX, pointY);
						if (Math.abs(difference) < threshold)
							count++;
					} catch (IndexOutOfBoundsException e) {
					}
				}
			}
		}
		return count;
	}

	public void synthesize(SynthesisFunction func, Channel... chnls) {
		double[] result = new double[width * height];

		for (int x = 0; x < channel.length; x++) {
			double[] colors = new double[chnls.length + 1];
			colors[0] = this.channel[x];
			for (int y = 1; y <= chnls.length; y++) {
				colors[y] = chnls[y - 1].channel[x];
			}
			result[x] = func.apply(colors);
		}
		this.channel = result;
	}

	/**
	 * @param r1
	 *            - The lower boundary
	 * @param r2
	 *            - The higher boundary
	 * @param y1
	 *            - The new value for the lower boundary
	 * @param y2
	 *            - The new value for the higher boundary
	 */
	public void contrast(double r1, double r2, double y1, double y2) {
		// Primera recta - antes de r1
		double m1 = y1 / r1;
		double b1 = 0;

		// Recta del medio - entre r1 y r2
		double m = (y2 - y1) / (r2 - r1);
		// when r = r1, y = y1; y1 = m*r1 + b
		double b = y1 - m * r1;

		// Ultima recta - de r2 hacia arriba
		double m2 = (MAX_CHANNEL_COLOR - y2) / (MAX_CHANNEL_COLOR - r2);
		// when r = r2, y = y2 ; y2 = m2 * r2 + b
		double b2 = y2 - m2 * r2;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double r = this.getPixel(x, y);
				double f = 0;
				if (r < r1) {
					f = m1 * r + b1;
				} else if (r > r2) {
					f = m2 * r + b2;
				} else {
					f = m * r + b;
					f = (f > MAX_CHANNEL_COLOR) ? MAX_CHANNEL_COLOR : f;
				}
				this.setPixel(x, y, f);
			}
		}
	}

	public void deleteNotMaximums(int[][] directions) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				double pixel = this.getPixel(x, y);
				double left = 0, right = 0;
				int direction = directions[x][y];
				try {
					if (direction == 0)
						left = this.getPixel(x - 1, y);
					else if (direction == 1)
						left = this.getPixel(x - 1, y - 1);
					else if (direction == 2)
						left = this.getPixel(x + 1, y);
					else if (direction == 3)
						left = this.getPixel(x - 1, y + 1);
				} catch (Exception e) {
				}
				try {
					if (direction == 0)
						right = this.getPixel(x + 1, y);
					else if (direction == 1)
						right = this.getPixel(x + 1, y + 1);
					else if (direction == 2)
						left = this.getPixel(x - 1, y);
					else if (direction == 3)
						left = this.getPixel(x + 1, y - 1);
				} catch (Exception e) {
				}
				if (left > pixel || right > pixel)
					this.setPixel(x, y, 0);
			}
		return;
	}

	public void histeresisThreshold(double t1, double t2) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				double pixel = this.getPixel(x, y);
				if (pixel < t1)
					this.setPixel(x, y, 0);
				if (pixel > t1 && pixel < t2) {
					double left = 0, right = 0, up = 0, down = 0;
					try {
						left = this.getPixel(x - 1, y);
					} catch (Exception e) {
					}
					try {
						right = this.getPixel(x + 1, y);
					} catch (Exception e) {
					}
					try {
						up = this.getPixel(x, y + 1);
					} catch (Exception e) {
					}
					try {
						down = this.getPixel(x, y - 1);
					} catch (Exception e) {
					}
					if (left < t2 && right < t2 && up < t2 && down < t2) {
						this.setPixel(x, y, 0);
					}
				}
			}
		return;
	}

	public void markZeroCrossers() {
		double previous = 0;
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				double pixel = this.getPixel(x, y);
				if (previous < 0 && pixel > 0)
					this.setPixel(x, y, MAX_CHANNEL_COLOR);
				else
					this.setPixel(x, y, 0);
				previous = pixel;
			}
		return;
	}

	public void markCrossersWithThreshold(int threshold) {
		double previous = 0;
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				double pixel = this.getPixel(x, y);
				double difference = pixel - previous;
				if (difference > threshold)
					this.setPixel(x, y, MAX_CHANNEL_COLOR);
				else
					this.setPixel(x, y, 0);
				previous = pixel;
			}
		return;
	}

	public HashMap<Double, Double> applyHough(int granularityTita, int granularityRo,
			double threshold, int totalLines) {
		int[][] bucket = new int[granularityTita][granularityRo];
		double sqrt2D = Math.sqrt(2) * Math.max(width, height);
		for (int i = 0; i < granularityTita; i++)
			for (int j = 0; j < granularityRo; j++) {
				double tita = -Math.PI / 2 + (Math.PI * i) / granularityTita;
				double ro = -sqrt2D + (sqrt2D * 2 * j) / granularityRo;
				bucket[i][j] = countWhites(tita, ro, threshold);
			}

		Set<Point> winners = new HashSet<Point>();
		for (int w = 0; w < totalLines; w++) {
			int maxValue = 0;
			Point max = new Point(0, 0);
			for (int i = 0; i < granularityTita; i++)
				for (int j = 0; j < granularityRo; j++) {
					if (bucket[i][j] > maxValue
							&& !winners.contains(new Point(i, j))) {
						maxValue = bucket[i][j];
						max = new Point(i, j);
					}
				}
			winners.add(max);
		}

		HashMap<Double, Double> roTita = new HashMap<Double, Double>();
		for (Point p : winners)
			roTita.put(-sqrt2D + (sqrt2D * 2 * p.y) / granularityRo, -Math.PI
					/ 2 + (Math.PI * p.x) / granularityTita);
		return roTita;
	}

	public void drawLines(HashMap<Double, Double> roTitas, double threshold) {
		for (Double ro : roTitas.keySet())
			drawLine(roTitas.get(ro), ro, threshold);
		return;
	}

	public void drawLine(double tita, double ro, double threshold) {
		// System.out.println("ro:" + ro + " tita:" + tita);
		for (int x = 0; x < width; x++) {
			int y = (int) ((ro - x * Math.cos(tita)) / Math.sin(tita));
			// System.out.println(y);
			if (y > 0 && y < height)
				setPixel(x, y, MAX_CHANNEL_COLOR);
		}
		// for (int x = 0; x < width; x++)
		// for (int y = 0; y < height; y++) {
		// if(getPixel(x, y) == MAX_CHANNEL_COLOR){
		// double straightLineError = ro - x*Math.cos(tita) - y*Math.sin(tita);
		// if(Math.abs(straightLineError) < threshold)
		// setPixel(x, y, MAX_CHANNEL_COLOR);
		// }
		// }
		// return;
	}

	private int countWhites(double tita, double ro, double threshold) {
		int count = 0;
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				// System.out.println(getPixel(x, y));
				if (truncatePixel(getPixel(x, y)) == MAX_CHANNEL_COLOR) {
					double straightLineError = ro - x * Math.cos(tita) - y
							* Math.sin(tita);
					// System.out.println(straightLineError);
					if (Math.abs(straightLineError) < threshold)
						count++;
				}
			}
		return count;
	}
	
	public Set<Point3D> applyCircleHough(int granularityA, int granularityB, int granularityR,
			double threshold, int totalLines) {
		int[][][] bucket = new int[granularityA][granularityB][granularityR];
		for (int i = 0; i < granularityA; i++)
			for (int j = 0; j < granularityB; j++) 
				for (int k = 0; k < granularityR; k++) {
					double a = width/granularityA * i;
					double b = width/granularityB * j;
					double r = 30 + 10/granularityR * k;
					bucket[i][j][k] = countCircleWhites(a, b, r, threshold);
				}
			

		Set<Point3D> winners = new HashSet<Point3D>();
		for (int w = 0; w < totalLines; w++) {
			int maxValue = 0;
			Point3D max = new Point3D(0, 0, 0);
			for (int i = 0; i < granularityA; i++)
				for (int j = 0; j < granularityB; j++)
					for (int k = 0; k < granularityR; k++) {
						if (bucket[i][j][k] > maxValue
								&& !winners.contains(new Point3D(i, j, k))) {
							maxValue = bucket[i][j][k];
							max = new Point3D(i, j, k);
						}
					}
			winners.add(max);
		}

		Set<Point3D> abr = new HashSet<Point3D>();
		for (Point3D p : winners)
			abr.add(new Point3D(width/granularityA * p.x, width/granularityB * p.y, 30 + 10/p.z));
		return abr;
	}

	private int countCircleWhites(double a, double b, double r, double threshold) {
		int count = 0;
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				// System.out.println(getPixel(x, y));
				if (truncatePixel(getPixel(x, y)) == MAX_CHANNEL_COLOR) {
					double circleError = Math.pow((x - a), 2) + Math.pow((y - b), 2) - Math.pow(r, 2); 
					// System.out.println(straightLineError);
					if (Math.abs(circleError) < threshold)
						count++;
				}
			}
		return count;
	}
	
	public void drawCircles(Set<Point3D> abr, double threshold) {
		for (Point3D p : abr)
			drawCircle(p.x, p.y, p.z, threshold);
		return;
	}

	private void drawCircle(double a, double b, double r, double threshold) {
		for(int x = (int)(a - r); x < a + r; x++)
			for(int y = (int)(b - r); y < b + r; y++){
				if(Math.abs(Math.pow((x-a), 2) + Math.pow((y-b), 2) - Math.pow(r, 2)) < threshold)
					if(validPixel(x, y))
						setPixel(x, y, MAX_CHANNEL_COLOR);
			}
	}
	
	public class Point3D {
		public final double x;
		public final double y;
		public final double z;
		
		public Point3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
}
