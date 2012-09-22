package jodd.prjtools.gfxgen;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Various image utilities for helping web designers.
 */
public class ImageUtil {

	// ---------------------------------------------------------------- core

	/**
	 * Creates new image.
	 */
	public static BufferedImage createImage(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Creates alpha color.
	 */
	public static Color alphacolor(long rgba) {
		return new Color((int) rgba, true);
	}
	public static Color alphacolor(int r, int g, int b, int a) {
		return new Color(r, g, b, a);
	}

	/**
	 * Creates solid color.
	 */
	public static Color color(long rgba) {
		return new Color((int) rgba, false);
	}

	/**
	 * Creates solid color.
	 */
	public static Color color(int r, int g, int b) {
		return new Color(r, g, b);
	}


	// ---------------------------------------------------------------- transformations

	/**
	 * Flips image horizontal.
	 */
	public static BufferedImage flipHorizontal(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage flipImage = new BufferedImage(width, height, image.getType());
		Graphics2D g = flipImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
		g.dispose();
		return flipImage;
	}

	/**
	 * Flips image vertical.
	 */
	public static BufferedImage flipVertical(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage flipImage = new BufferedImage(width, height, image.getType());
		Graphics2D g = flipImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
		g.dispose();
		return flipImage;
	}

	/**
	 * Rotates image for given image.
	 */
	public static BufferedImage rotate(BufferedImage image, int angle) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
		Graphics2D g = rotatedImage.createGraphics();
		g.rotate(Math.toRadians(angle), ((double) width) / 2, ((double) height) / 2);
		g.drawImage(image, null, 0, 0);
		g.dispose();
		return rotatedImage;
	}

	/**
	 * Translates image.
	 */
	public static BufferedImage translate(BufferedImage image, int x, int y) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
		Graphics2D g = rotatedImage.createGraphics();
		g.translate(x, y);
		g.drawImage(image, null, 0, 0);
		g.dispose();
		return rotatedImage;
	}

	// ---------------------------------------------------------------- css sprite

	/**
	 * Merges images for CSS sprites, one below other.
	 */
	public static BufferedImage joinVertical(Image... images) {
		return joinVertical(0, images);
	}

	/**
	 * Merges images for CSS sprites, one below other, with top offset.
	 */
	public static BufferedImage joinVertical(int top, Image... images) {
		int width = 0;
		int height = 0;

		// find max width and total height
		for (Image image : images) {
			height += image.getHeight(null) + 2 * top;
			if (image.getWidth(null) > width) {
				width = image.getWidth(null);
			}
		}

		BufferedImage bufferedImage = createImage(width, height);
		Graphics2D g2d = bufferedImage.createGraphics();
		// join
		int drawHeight = 0;
		for (Image image : images) {
			drawHeight += top;
			g2d.drawImage(image, 0, drawHeight, null);
			drawHeight += image.getHeight(null) + top;
		}
		g2d.dispose();
		return bufferedImage;
	}


	/**
	 * Merges images for CSS sprites, one below other.
	 */
	public static BufferedImage joinHorizontal(Image... images) {
		return joinHorizontal(0, images);
	}

	public static BufferedImage joinHorizontal(int left, Image... images) {
		int width = 0;
		int height = 0;

		// find max width and total height
		for (Image image : images) {
			width += image.getWidth(null) + 2 * left;
			if (image.getHeight(null) > height) {
				height = image.getHeight(null);
			}
		}

		BufferedImage bufferedImage = createImage(width, height);
		Graphics2D g2d = bufferedImage.createGraphics();
		// join
		int drawWidth = 0;
		for (Image image : images) {
			drawWidth += left;
			g2d.drawImage(image, drawWidth, 0, null);
			drawWidth += image.getWidth(null) + left;
		}
		g2d.dispose();
		return bufferedImage;
	}

	// ---------------------------------------------------------------- colors

	/**
	 * Sets graphics color to provided color. If color is <code>null</code> color is set to transparent.
	 */
	public static void setColor(Graphics2D g, Color color) {
		if (color != null) {
			g.setColor(color);
		} else {
			g.setComposite(AlphaComposite.Src);	// transparent
			g.setColor(new Color(0, 0, 0, 0));
		}
	}

	/**
	 * Replaces transparent background with provided color.
	 */
	public static BufferedImage replaceTransparentColor(BufferedImage image, Color color) {
		int width = image.getWidth(null);
		int height = image.getHeight();
		BufferedImage bufferedImage = createImage(width, height);
		Graphics2D g = bufferedImage.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bufferedImage;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Quickly shows prepared image.
	 */
	public static void showImage(Image image) {
		JFrame frame = new JFrame();
		frame.setTitle("Jodd GfxGen image preview");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Saves image to disk.
	 */
	public static void saveImage(BufferedImage image, String formatName, String destination) {
		try {
			ImageIO.write(image, formatName, new File(destination));
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	/**
	 * Saves image to disk, while format is determined by destination file extension.
	 */
	public static void saveImage(BufferedImage image, String destination) {
		String formatName = destination.substring(destination.lastIndexOf('.') + 1);
		saveImage(image, formatName, destination);
	}

	/**
	 * Loads images from disk.
	 */
	public static BufferedImage loadImage(String source) {
		try {
			return ImageIO.read(new File(source));
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return null;
		}
	}
}