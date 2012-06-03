package jodd.prjtools.gfxgen;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

/**
 * Graphics2D drawing utilities.
 */
public class DrawUtil {

	public static void drawRect(Image image, int x, int y, int width, int height, Color background) {
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setColor(background);
		g2.fillRect(x, y, width, height);
		g2.dispose();
	}

	public static void drawTriangle(Image image, int x1, int y1, int x2, int y2, int x3, int y3, Color background) {
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		
		g2.setColor(background);

		GeneralPath path = new GeneralPath();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.closePath();

		g2.fill(path);
		g2.dispose();
	}

	public static void paintVerticalGradient(Image image, int x, int y, int w, int h, int height, Color startColor, Color endColor) {
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setPaint(new GradientPaint(0, y, startColor, 0, height, endColor));
		g2.fillRect(x, y, w, h);
		g2.dispose();
	}

	/**
	 * Paints a horizontal gradient background from the start color to the end color.
	 */
	public static void paintHorizontalGradient(Image image, int x, int y, int w, int h, int width, Color startColor, Color endColor) {
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setPaint(new GradientPaint(x, 0, startColor, width, 0, endColor));
		g2.fillRect(x, y, w, h);
		g2.dispose();
	}

}
