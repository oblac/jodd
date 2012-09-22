package jodd.prjtools.gfxgen;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.GradientPaint;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;

/**
 * Draws gradient box.
 */
public class GradientBox {

	public static BufferedImage createGradientBox(int width, int height,
										   Color backgroundColor1, Color backgroundColor2,
										   Color borderColor, Color borderHighlight,
										   Color borderColorAlpha1, Color borderColorAlpha2) {
		BufferedImage image = ImageUtil.createImage(width, height);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int arc = 8;

		Shape buttonShape = new RoundRectangle2D.Double(0, 0, (double) width, (double) height, (double) arc, (double) arc);
		Shape oldClip = g2d.getClip();

		g2d.setClip(buttonShape);
		g2d.setColor(backgroundColor1);
		g2d.fillRect(0, 0, width, height / 2);
		g2d.setColor(backgroundColor2);
		g2d.fillRect(0, height / 2, 0, height / 2);


		g2d.setClip(oldClip);
		GradientPaint vPaint = new GradientPaint(0, 0, borderColor, 0, height, borderHighlight);
		g2d.setPaint(vPaint);
		g2d.drawRoundRect(0, 0, width, height, arc, arc);

		g2d.clipRect(0, 0, width + 1, height - arc / 4);
		g2d.setColor(borderColorAlpha1);
		g2d.drawRoundRect(0, 1, width, height - 1, arc, arc - 1);

		g2d.setClip(oldClip);
        g2d.setColor(borderColorAlpha2);
        g2d.drawRoundRect(1, 2, width - 2, height - 3, arc, arc - 2);

		g2d.dispose();
		return image;
	}

	public static void main(String[] args) {
			Image gb = createGradientBox(200, 50,
					new Color(235,247,223), new Color(214,219,191),
					new Color(86,88,72), new Color(225,224,224),
					new Color(86,88,72,100), new Color(86,88,72,50));
			ImageUtil.showImage(gb);

	}
}
