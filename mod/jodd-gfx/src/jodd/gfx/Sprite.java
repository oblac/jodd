// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

/**
 * Single sprite. Sprite is special since it can be accessed
 * as pixel buffer as well as through Graphics2D. This makes
 * sprite usage very efficient.
 *
 * All sprite routines are optimized.
 *
 * Note: all operations takes the following pattern: dest.operation(src);
 */
public class Sprite {

	// ---------------------------------------------------------------- color types

	/**
	 * Default color (buffer) type for buffer that uses 3 bytes (RBG) per pixel.
	 */
	public static final	int COLOR_RGB	= BufferedImage.TYPE_INT_RGB;
	/**
	 * Color (buffer) type with 4 bytes per pixel, RGB + Alpha channel.
	 */
	public static final int COLOR_ARGB	= BufferedImage.TYPE_INT_ARGB;

	// ---------------------------------------------------------------- public

	/**
	 * Sprite width.
	 */
	public int width = -1;
	/**
	 * Sprite height.
	 */
	public int height = -1;
	/**
	 * Sprite color (buffer) type.
	 */
	public int colorType = COLOR_RGB;
	/**
	 * Cached value of width * height.
	 */
	public int size;
	/**
	 * Sprite buffer (i.e. pixels).
	 */
	public int buf[];
	/**
	 * Sprite BufferedImage.
	 */
	public BufferedImage img;
	/**
	 * Sprite Graphics2D.
	 */
	public Graphics2D g2d;

	// ----------------------------------------------------------------	ctors
	
	protected int[] rows;					// cached row offsets

	/**
	 * Empty constructor.
	 */
	public Sprite() {
	}

	/**
	 * Creates new empty sprite, with default RGB color type.
	 *
	 * @param w      sprite width
	 * @param h      sprite height
	 */
	public Sprite(int w, int h) {
		this(w, h, COLOR_RGB);
	}

	/**
	 * Creates new empty sprite.
	 * 
	 * @param w		sprite width
	 * @param h		sprite height
	 * @param type	sprite color type
	 */
	public Sprite(int w, int h, int type) {
		width = w;
		height = h;
        colorType = type;
		create();
	}


	public Sprite(String imageFile) {
		this(imageFile, COLOR_RGB, 1);
	}

	/**
	 * Creates a new sprite by loading it from an image.
	 */
	public Sprite(String imageFile, int type) {
		this(imageFile, type, 1);
	}

	public Sprite(String imageFile, int type, int numberOfImages) {
		load(imageFile, type, numberOfImages);
	}


	// ----------------------------------------------------------------	load images

	private BufferedImage images[];
	private Graphics2D imagesGraphics[];
	private int[][] imagesPixels;

	/**
	 * Loads sprite content from single image.
	 */
	public void load(String imageFile, int type) {
		load(imageFile, type, 1);
	}

	/**
	 * Loads sprite content from image strip.
	 *
	 * @param imageFile			image filename
	 * @param type				type of image
	 * @param numberOfImages	number of images in the strip
	 */
	public void load(String imageFile, int type, int numberOfImages) {
		colorType = type;
		BufferedImage strip;
		try {
			strip = ImageIO.read(getClass().getResource(imageFile));
		} catch (IOException e) {
			return;
		}
		int stripWidth = strip.getWidth();
        width = stripWidth / numberOfImages;
		height = strip.getHeight();

		images = new BufferedImage[numberOfImages];
		imagesGraphics = new Graphics2D[numberOfImages];
		imagesPixels = new int[numberOfImages][];
		for (int i = 0; i < numberOfImages; i++) {
			images[i] = new BufferedImage(width, height, type);
			Graphics2D g = (Graphics2D) images[i].getGraphics();
			imagesGraphics[i] = g;
			imagesPixels[i] = ((DataBufferInt) images[i].getRaster().getDataBuffer()).getData();
			g.clipRect(0, 0, width, height);
			g.drawImage(strip, -i * width, 0, null);

		}
		init();
		select(0);
	}

	public int currentImage;
	/**
	 * Selects and draws loaded image to sprite buffer.
	 * @param i	index of loaded image, 0-based
	 */
	public void select(int i) {
		currentImage = i;
		img = images[currentImage];
		g2d = imagesGraphics[currentImage];
		buf = imagesPixels[currentImage];
	}

	public void selectNext() {
		currentImage++;
		if (currentImage >= images.length) {
			currentImage = 0;
		}
		select(currentImage);
	}

	// ----------------------------------------------------------------	create image

	private void init() {
		size = width * height;
		rows = new int[height];
		for (int i = 0; i < height; i++) {
			rows[i] = i * width;
		}
	}

	/**
	 * Creates a sprite.
	 */
	public void create() {
		init();
		img = new BufferedImage(width, height, colorType);
		//g2d = img.createGraphics();
		g2d = (Graphics2D) img.getGraphics();
		buf = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
	}

}

