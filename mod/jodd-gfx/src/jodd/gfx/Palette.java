// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx;

/**
 * Palette represents alternate way of coloring pixels.
 * Instead of storing the exact RBG colors, palette may be used
 * for holding values of colors and then using them from the
 * palette.
 */
public class Palette {

	private int	totalCol;
	public int getTotalColors() {
		return totalCol;
	}

	private int[] colors;

	public Palette(int tc) {
		totalCol = tc;
		colors = new int[totalCol];
	}

	public int getColor(int ndx) {
		if (ndx < 0) {
			return -1;
		}
		if (ndx >= totalCol) {
			return -1;
		}
		return colors[ndx];
	}

	public void setColor(int ndx, int color) {
		if (ndx < 0) {
			return;
		}
		if (ndx >= totalCol) {
			return;
		}
		colors[ndx] = color;
	}


	public static int getRed(int color) {
		return (color & 0xFF0000) >> 16;
	}

	public static int getGreen(int color) {
		return (color & 0x00FF00) >> 8;
	}
	public static int getBlue(int color) {
		return (color & 0x0000FF);
	}

	public static int getRGB(int red, int green, int blue) {
		return ( ((red & 0xFF) << 16) + ((green & 0xFF) << 8) + (blue & 0xFF));
	}

	/**
	 * Fills palette gradiently.
	 *
	 * @param startNdx first color index (inclusive)
	 * @param endNdx   last color index (inclusive)
	 */
	public void gradientFill(int startNdx, int endNdx, int startCol, int endCol) {
		int color = startCol;
		int steps = endNdx - startNdx + 1;
		
		float rColor = getRed(color);
		float gColor = getGreen(color);
		float bColor = getBlue(color);

		float rColorStep = (getRed(endCol) - rColor) / (float) steps;
		float gColorStep = (getGreen(endCol) - gColor) / (float) steps;
		float bColorStep = (getBlue(endCol) - bColor) / (float) steps;

		while (steps > 0) {
			setColor(startNdx, color);
			rColor += rColorStep;
			gColor += gColorStep;
			bColor += bColorStep;
			color =	getRGB((int)(rColor + 0.5), (int)(gColor + 0.5), (int)(bColor + 0.5));
			startNdx++;
			steps--;
		}
	}

}

