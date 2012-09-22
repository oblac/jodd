package jodd.prjtools.gfxgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

/**
 * Various markers (success, etc).
 */
public class Markers {

	public static BufferedImage createSuccessMarker(int dimension, boolean straight) {
		BufferedImage image = ImageUtil.createImage(dimension, dimension);
		Graphics2D graphics = (Graphics2D) image.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(new Color(0, 196, 0));
		graphics.fillOval(0, 0, dimension - 1, dimension - 1);

		// create spot in the upper-left corner using temporary graphics with clip set to the icon outline
		GradientPaint spot = new GradientPaint(0, 0, new Color(255, 255, 255, 200),
				dimension, dimension, new Color(255, 255, 255, 0));
		Graphics2D tempGraphics = (Graphics2D) graphics.create();
		tempGraphics.setPaint(spot);
		tempGraphics.setClip(new Ellipse2D.Double(0, 0, dimension - 1, dimension - 1));
		tempGraphics.fillRect(0, 0, dimension, dimension);
		tempGraphics.dispose();

		// draw outline of the icon
		graphics.setColor(new Color(0, 0, 0, 128));
		graphics.drawOval(0, 0, dimension - 1, dimension - 1);

		// draw the V sign
		float dimOuter = (float) (0.5f * StrictMath.pow(dimension, 0.75));
		float dimInner = (float) (0.28f * StrictMath.pow(dimension, 0.75));

		// create the path itself
		GeneralPath gp = new GeneralPath();
		if (straight) {
			gp.moveTo(0.25f * dimension, 0.45f * dimension);
			gp.lineTo(0.45f * dimension, 0.65f * dimension);
			gp.lineTo(0.85f * dimension, 0.12f * dimension);
		} else {
			gp.moveTo(0.25f * dimension, 0.45f * dimension);
			gp.quadTo(0.35f * dimension, 0.52f * dimension, 0.45f * dimension, 0.65f * dimension);
			gp.quadTo(0.65f * dimension, 0.3f * dimension, 0.85f * dimension, 0.12f * dimension);
		}

		// draw blackish outline
		graphics.setStroke(new BasicStroke(dimOuter, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.setColor(new Color(0, 0, 0, 196));
		graphics.draw(gp);
		// draw white inside
		graphics.setStroke(new BasicStroke(dimInner, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.setColor(Color.white);
		graphics.draw(gp);

		// end
		graphics.dispose();
		return image;
	}

}
