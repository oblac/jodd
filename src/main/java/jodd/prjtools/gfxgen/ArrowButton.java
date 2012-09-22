package jodd.prjtools.gfxgen;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Nice looking arrow. Arrow points from top to bottom.
 */
public class ArrowButton {

	public static BufferedImage createArrow(int width, int height, int inset, Color shapeColor) {
		BufferedImage image = ImageUtil.createImage(width, height);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// calculate the size of the button
		int shapeHeight = height - (inset * 2);
		int shapeWidth = width - (inset * 2);

		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D bg = buffer.createGraphics();
		bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// paint the background of the button
		bg.setColor(Color.WHITE);
		bg.fillRect(0, 0, width, height);

		// create the arrow shape
		Polygon shape = createShape(shapeWidth, shapeHeight);

		// translate our coordinates based on the insets we want
		bg.translate(inset, inset);

		// create the gradient paint for the background of the arrow
		Color gradientStartColor = shapeColor.brighter().brighter();
		Color gradientEndColor = gradientStartColor.brighter().brighter().brighter().brighter();
		Paint paint = new GradientPaint(inset, 0, gradientStartColor, shapeWidth, 0, gradientEndColor, false);
		bg.setPaint(paint);

		// fill in the background of the arrow
		bg.fill(shape);

		// create the stroke for the outline of the arrow
		bg.setColor(shapeColor);
		int strokeSize = (int) (0.05 * shapeWidth);
		bg.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

		// draw the outline of the arrow
		bg.draw(shape);

		// create a smaller arrow
		int insetSize = (int) (0.06 * shapeWidth);
		Polygon smallerShape = createSmallerPolygon(shape, insetSize);
		bg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

		// fill the smaller arrow
		bg.setColor(shapeColor.brighter().brighter());
		bg.fill(smallerShape);


		// draw the circle "highlight" inside of the smaller arrow
		bg.setClip(smallerShape);
		gradientStartColor = shapeColor.brighter().brighter();
		gradientEndColor = shapeColor;
		paint = new GradientPaint(0, 0, gradientStartColor, shapeWidth, shapeHeight, gradientEndColor, false);
		bg.setPaint(paint);
		bg.fillOval(0 - inset - (shapeWidth / 3), 0 - inset + (shapeHeight / 4), (int) (shapeWidth * 1.05), (int) (shapeHeight * 1.5));

		g2.drawImage(buffer, 0, 0, null);

		g2.dispose();
		return image;
	}

	protected static Polygon createShape(int width, int height) {
		int hInset = (int) (width * 0.25);
		int vInset = (int) (height * 0.35);
		int hCenter = width / 2;

		Polygon p = new Polygon();
		p.addPoint(hInset, 0);
		p.addPoint(hInset, vInset);
		p.addPoint(0, vInset);
		p.addPoint(hCenter, height);
		p.addPoint(width, vInset);
		p.addPoint(width - hInset, vInset);
		p.addPoint(width - hInset, 0);

		return p;
	}

	protected static Polygon createSmallerPolygon(Polygon polygon, int inset) {
		Rectangle bounds = polygon.getBounds();

		int horizontalCenter = bounds.width / 2;
		int verticalCenter = bounds.height / 2;

		Polygon p = new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
		for (int i = 0; i < p.xpoints.length; i++) {
			int x = p.xpoints[i];
			if (x < horizontalCenter) {
				x += inset;
			} else if (x > horizontalCenter) {
				x -= inset;
			}
			p.xpoints[i] = x;
		}

		for (int i = 0; i < p.ypoints.length; i++) {
			int y = p.ypoints[i];
			if (y < verticalCenter) {
				y += inset;
			} else if (y > verticalCenter) {
				y -= inset;
			}
			p.ypoints[i] = y;
		}


		int len1 = horizontalCenter - polygon.xpoints[2];
		int len2 = polygon.ypoints[3] - polygon.ypoints[1];
		float ratio = (float) len1 / (float) len2;

		p.ypoints[3] -= (int) (inset * ratio);

		int newVerticalLen = p.ypoints[3] - p.ypoints[1];
		int newHorizontalLen = (int) (ratio * newVerticalLen);
		p.xpoints[2] = (horizontalCenter - newHorizontalLen);
		p.xpoints[4] = (horizontalCenter + newHorizontalLen);
		return p;
	}

	public static void main(String[] args) {
		Image arr = createArrow(200, 200, 10, new Color(125, 50, 50));
		ImageUtil.showImage(arr);
	}
}