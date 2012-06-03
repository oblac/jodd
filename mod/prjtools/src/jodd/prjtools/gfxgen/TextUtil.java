package jodd.prjtools.gfxgen;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;

/**
 * Utilities for drawing text.
 */
public class TextUtil {

	public static final String SILKSCREEN_FONTNAME = "Silkscreen";

	public static float getStringWidth(Graphics2D g, Font font, String string) {
		FontRenderContext frc = g.getFontRenderContext();
		Rectangle2D bounds = font.getStringBounds(string, frc);
		return (float) bounds.getWidth();
	}

	public static int drawChar(Graphics2D g, Font font, String charStr, int x, int y) {
		int width = (int) getStringWidth(g, font, charStr);
		g.drawString(charStr, x, y);
		return x + width - 1;
	}

	public static boolean fontExist(String fontName) {
		Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (Font allFont : allFonts) {
			if (fontName.equals(allFont.getName())) {
				return true;
			}
		}
		return false;
	}

	public static Font setFont(Graphics2D g, String fontName, Color textColor) throws Exception {
		if (!fontExist(fontName)) {
			throw new Exception("Font doesn't exist!");
		}
		Font font = new Font(fontName, Font.TRUETYPE_FONT, 8);
		g.setFont(font);
		if (textColor != null) {
			g.setColor(textColor);
		} else {
			g.setColor(Color.black);
		}
		return font;
	}

	public static void drawStringWithMinSpacing(Graphics2D g, Font font, String string, int x, int y) {
		char[] chars = string.toCharArray();
		int nextX = x;
		for (char ch : chars) {
			if (ch == ' ') {
				nextX += 1;
			} else {
				nextX = drawChar(g, font, String.valueOf(ch), nextX, y);
			}
		}
	}

	public static int getStringWithMinSpacingWidth(Graphics2D g, Font font, String string) {
		char[] chars = string.toCharArray();
		int width = 0;
		for (char ch : chars) {
			if (ch == ' ') {
				width += 1;
			} else {
				width += (int) getStringWidth(g, font, String.valueOf(ch)) - 1;
			}
		}
		return width;
	}

}
