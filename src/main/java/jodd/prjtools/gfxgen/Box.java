package jodd.prjtools.gfxgen;

import static jodd.prjtools.gfxgen.ImageUtil.flipHorizontal;
import static jodd.prjtools.gfxgen.ImageUtil.flipVertical;
import static jodd.prjtools.gfxgen.ImageUtil.setColor;

import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;

/**
 * Everything for the rounded boxes.
 */
public class Box {

	// ---------------------------------------------------------------- main

	public static BufferedImage createRoundedCornersCssSpriteV(int width, int height, Color backColor, Color innerColor) {
		return createRoundedCornersCssSpriteV(width, height, 0, backColor, innerColor, null);
	}
	public static BufferedImage createRoundedCornersCssSpriteH(int width, int height, Color backColor, Color innerColor) {
		return createRoundedCornersCssSpriteH(width, height, 0, backColor, innerColor, null);
	}

	/**
	 * Creates CSS sprite with all rounded corners.
	 * @see #createOvalCorner(int, int, int, java.awt.Color, java.awt.Color, java.awt.Color)
	 */
	public static BufferedImage createRoundedCornersCssSpriteV(int width, int height, int borderWidth, Color backColor, Color innerColor, Color borderColor) {
		BufferedImage image1 = createOvalCorner(width, height, borderWidth, backColor, innerColor, borderColor);
		BufferedImage image2 = flipHorizontal(image1);
		BufferedImage image3 = flipVertical(image1);
		BufferedImage image4 = flipVertical(image2);
		return ImageUtil.joinVertical(image1, image2, image3, image4);
	}

	public static BufferedImage createRoundedCornersCssSpriteH(int width, int height, int borderWidth, Color backColor, Color innerColor, Color borderColor) {
		BufferedImage image1 = createOvalCorner(width, height, borderWidth, backColor, innerColor, borderColor);
		BufferedImage image2 = flipHorizontal(image1);
		BufferedImage image3 = flipVertical(image1);
		BufferedImage image4 = flipVertical(image2);
		return ImageUtil.joinHorizontal(image1, image2, image3, image4);
	}


	// ---------------------------------------------------------------- oval


	public static BufferedImage createOvalCorner(int width, int height, Color backColor, Color innerColor) {
		return createOvalCorner(width, height, 0, backColor, innerColor, null);
	}

	/**
	 * Creates oval corner.
	 */
	public static BufferedImage createOvalCorner(int width, int height, int border, Color backColor, Color innerColor, Color borderColor) {
		BufferedImage image = ImageUtil.createImage(width, height);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		setColor(g, backColor);
		g.fillRect(0, 0, width, height);

		int xR = width * 2;
		int yR = height * 2;

		if (border != 0) {
			setColor(g, borderColor);
			g.fillOval(0, 0, xR, yR);
		}

		setColor(g, innerColor);
		g.fillOval(border, border, xR - border * 2, yR - border * 2);
		if (border == 0) {
			setColor(g, new Color(backColor.getRGB() | (0xBB << 24), true));
			g.setStroke(new BasicStroke(1.5f));
			g.drawOval(border-1, border-1, xR - border * 2, yR - border * 2);
		}

		g.dispose();
		return image;
	}

	public static void main(String[] args) {
		Image image = createRoundedCornersCssSpriteV(20, 20, Color.RED, Color.BLUE);
		ImageUtil.showImage(image);
	}

}
