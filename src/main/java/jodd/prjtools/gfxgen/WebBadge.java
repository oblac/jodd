package jodd.prjtools.gfxgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import static jodd.prjtools.gfxgen.TextUtil.*;

/**
 * Creates web bage using 'Silkscreen' font.
 */
public class WebBadge {

	private static final Color BORDER_COLOR = new Color(0x666666);

	public static BufferedImage createWebBadge(Image logo, String text, Color background, Color textColor) throws Exception {
		BufferedImage image = createWebBadge(text, background, textColor);
		Graphics2D g = image.createGraphics();
		if (logo != null) {
			g.drawImage(logo, 2, 2, 23, image.getHeight() - 4, null);
		}
		return image;
	}

	public static BufferedImage createWebBadge(String leftText, String rightText, Color background, Color textColor) throws Exception {
		BufferedImage image = createWebBadge(rightText, background, textColor);
		Graphics2D g = image.createGraphics();
		if (leftText != null && leftText.length() != 0) {
			Font font = setFont(g, SILKSCREEN_FONTNAME, textColor);
			int width1 = (int) getStringWidth(g, font, leftText);
			int width2 = getStringWithMinSpacingWidth(g, font, leftText);
			if (width1 <= 24) {
				g.drawString(leftText, 13 - width1 / 2, 10);
			} else if (width2 <= 24) {
				drawStringWithMinSpacing(g, font, leftText, 13 - width2 / 2, 10);
			} else {
				throw new Exception("Left text is too long!");
			}
		}
		return image;
	}

	protected static BufferedImage createWebBadge(String text, Color background, Color textColor) throws Exception {

		int width = 80;
		int height = 15;

		BufferedImage image = ImageUtil.createImage(width, height);
		Graphics2D g = image.createGraphics();

		g.setColor(BORDER_COLOR);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.white);
		g.fillRect(1, 1, width - 2, height - 2);

		if (background == null) {
			g.setColor(BORDER_COLOR);
			g.setStroke(new BasicStroke(1));
			g.drawLine(26, 0, 26, height);
		} else {
			g.setColor(background);
			g.fillRect(26, 2, 52, height - 4);
		}

		if (text != null && text.length() != 0) {
			Font font = setFont(g, SILKSCREEN_FONTNAME, textColor);
			if (getStringWidth(g, font, text) <= 49) {
				g.drawString(text, 29, 10);
			} else if (getStringWithMinSpacingWidth(g, font, text) <= 49) {
				drawStringWithMinSpacing(g, font, text, 29, 10);
			} else {
				drawStringWithMinSpacing(g, font, text, 28, 10);
			}
		}
		g.dispose();
		return image;

	}


}
