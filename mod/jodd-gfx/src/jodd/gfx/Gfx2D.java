// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx;

import java.awt.Color;

/**
 * Various 2D methods. They should be combined with <code>Graphics2D</code> methods,
 * for optimal performances.
 */
public class Gfx2D {

	// ----------------------------------------------------------------	pixel methods

	/**
	 * Set pixel color with boundaries check, about 8 times faster then setRBG().
	 * It is also faster then Graphics2D.line()
	 *
	 * @param x      x coordinate
	 * @param y      y coordinate
	 * @param c      color
	 */
	public static void set(Sprite sprite, int x, int y, int c) {
		if ((x < 0) || (x >= sprite.width)) {
			return;
		}
		if ((y < 0) || (y >= sprite.height)) {
			return;
		}
		sprite.buf[sprite.rows[y] + x] = c;
	}

	/**
	 * Fast set pixel without boundaries checking, approx. 14 times faster then setRBG().
	 *
	 * @param x		x coordinate
	 * @param y		y coordinate
	 * @param c		new color
	 */
	public static void setf(Sprite sprite, int x, int y, int c) {
		sprite.buf[sprite.rows[y] + x] = c;
	}

	/**
	 * Get pixel color with boundaries check.
	 *
	 * @param x      x coordinate
	 * @param y      y coordinate
	 *
	 * @return pixel color
	 */
	public static int get(Sprite sprite, int x, int y) {
		if ((x < 0) || (x >= sprite.width)) {
			return 0;
		}
		if ((y < 0) || (y >= sprite.height)) {
			return 0;
		}
		return sprite.buf[sprite.rows[y] + x];
	}

	/**
	 * Fast get pixel color, without boundaries checking.
	 *
	 * @param x      x coordinate
	 * @param y      y coordinate
	 *
	 * @return pixel color
	 */
	public static int getf(Sprite sprite, int x, int y) {
		return sprite.buf[sprite.rows[y] + x];
	}

	// ----------------------------------------------------------------	fill methods

	/**
	 * Fills sprite with the same color (i.e. clears it).
	 * It is <b>not</b> faster then fillRect(), but it is aware of alpha channel.
	 *
	 * @param col	fill color
	 */
	public static void fill(Sprite s, int col) {
		int i = 0;
		int left = s.size % 16;
		int total = s.size - left;
		int[] pixels = s.buf;

		while (i < total) {
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
			pixels[i++] = col;
		}
		while (left > 0) {
			pixels[i++] = col;
			left--;
		}
	}
    /**
	 * Frequently used shortcut.
	 */
	public static void fillRect(Sprite s, Color c) {
		s.g2d.setColor(c);
		s.g2d.fillRect(0, 0, s.width, s.height);
	}

	// ----------------------------------------------------------------	draw methods

	/**
	 * Uses Graphics2D.drawImage() method for drawing one sprite on the other.
	 * The fastest way, slightly faster then pixels array copy.
	 */
	public static void draw(Sprite dest, Sprite src, int x, int y) {
		dest.g2d.drawImage(src.img, x, y, null);
	}


	// ----------------------------------------------------------------	line methods

	/**
	 * Optimized Bresenham line algorithm. However, it is still a bit slower then
	 * <code>Graphics2D.drawLine()</code>, but it is aware of alpha channel.
	 *
	 * @param s		destination sprite
	 * @param x0	line start
	 * @param y0	line start
	 * @param x1	line end
	 * @param y1	line end
	 * @param color	line color
	 */
	public static void line(Sprite s, int x0, int y0, int x1, int y1, int color) {
		int dy = y1 - y0;
		int dx = x1 - x0;
		int stepx, stepy;

		if (dy < 0) { dy = -dy;  stepy = -1; } else { stepy = 1; }
		if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }
		dy <<= 1;								// dy is now 2*dy
		dx <<= 1;								// dx is now 2*dx

		set(s, x0, y0, color);
		if (dx > dy) {
			int fraction = dy - (dx >> 1);		// same as 2*dy - dx
			while (x0 != x1) {
				if (fraction >= 0) {
					y0 += stepy;
					fraction -= dx;				// same as fraction -= 2*dx
				}
				x0 += stepx;
				fraction += dy;					// same as fraction -= 2*dy
				set(s, x0, y0, color);
			}
		} else {
			int fraction = dx - (dy >> 1);
			while (y0 != y1) {
				if (fraction >= 0) {
					x0 += stepx;
					fraction -= dy;
				}
				y0 += stepy;
				fraction += dx;
				set(s, x0, y0, color);
			}
		}
	}

	/**
	 * Draws horizontal line - faster then <code>Graphics2D.drawLine()</code>
	 */
	public static void lineh(Sprite s, int x0, int y0, int x1, int color) {
		if ((y0 < 0) || (y0 >= s.height)) {
			return;
		}
		if (x1 < x0) {
			int temp = x1;
			x1 = x0;
			x0 = temp;
		}
		if (x1 < 0) {
			return;
		}
		if (x0 >= s.width) {
			return;
		}
		if (x0 < 0) {
			x0 = 0;
		}
		if (x1 >= s.width) {
			x1 = s.width - 1;
		}

		for (int i = x0; i < x1; i++) {
			setf(s, i, y0, color);
		}
	}
	/**
	 * Draws vertical line.
	 */
	public static void linev(Sprite s, int x0, int y0, int y1, int color) {
		if ((x0 < 0) || (x0 >= s.width)) {
			return;
		}
		if (y1 < y0) {
			int temp = y1;
			y1 = y0;
			y0 = temp;
		}
		if (y1 < 0) {
			return;
		}
		if (y0 >= s.height) {
			return;
		}
		if (y0 < 0) {
			y0 = 0;
		}
		if (y1 >= s.height) {
			y1 = s.height - 1;
		}

		for (int i = y0; i < y1; i++) {
			setf(s, x0, i, color);
		}
	}

	/**
	 * Draws a box (i.e. rectangle). Same speed as Graphics2D.
	 */
	public static void box(Sprite s, int x0, int y0, int x1, int y1, int color) {
		lineh(s, x0, y0, x1, color);	// draw horizontal parallel lines
		lineh(s, x0, y1, x1, color);
		linev(s, x0, y0, y1, color);	// draw vertical parallel lines
		linev(s, x1, y0, y1, color);
	}

	/**
	 * * Draws a rectangle (i.e. rectangle). Same speed as Graphics2D.
	 */
	public static void rect(Sprite s, int x0, int y0, int w, int h, int color) {
        int x1 = x0 + w;
		int y1 = y0 + h;

		// horizontal
		int start = x0;
		int end = x1;
		if (start < 0) {
			start = 0;
		}
		if (end >= s.width) {
			end = s.width - 1;
		}
		if ((y0 >= 0) && (y0 < s.height)) {
			for (int i = start; i < end; i++) {
				setf(s, i, y0, color);
			}
		}
		if ((y1 >= 0) && (y1 < s.height)) {
			for (int i = start; i < end; i++) {
				setf(s, i, y1, color);
			}
		}

        // vertical
		if (y0 < 0) {
			y0 = 0;
		}
		if (y1 >= s.height) {
			y1 = s.height - 1;
		}
		if ((x0 >= 0) && (x0 < s.width)) {
			for (int i = y0; i < y1; i++) {
				setf(s, x0, i, color);
			}
		}
		if ((x1 >= 0) && (x1 < s.width)) {
			for (int i = y0; i < y1; i++) {
				setf(s, x1, i, color);
			}
		}
	}

	// ----------------------------------------------------------------	circle mathods


	private static void circlePoints(Sprite s, int cx, int cy, int x, int y, int color) {
		if (x == 0) {
			set(s, cx, cy + y, color);
			set(s, cx, cy - y, color);
			set(s, cx + y, cy, color);
			set(s, cx - y, cy, color);
		} else
		if (x == y) {
			set(s, cx + x, cy + y, color);
			set(s, cx - x, cy + y, color);
			set(s, cx + x, cy - y, color);
			set(s, cx - x, cy - y, color);
		} else
		if (x < y) {
			set(s, cx + x, cy + y, color);
			set(s, cx - x, cy + y, color);
			set(s, cx + x, cy - y, color);
			set(s, cx - x, cy - y, color);
			set(s, cx + y, cy + x, color);
			set(s, cx - y, cy + x, color);
			set(s, cx + y, cy - x, color);
			set(s, cx - y, cy - x, color);
		}
	}

	/**
	 * Circle drawing implementing Midpoint Circle Algorithm.
	 * Much much faster then <code>Graphica2d.drawOval()</code>
	 */
	public static void circle(Sprite s, int xCenter, int yCenter, int radius, int color) {
		int x = 0;
		int y = radius;
		int p = (5 - radius * 4) / 4;

		circlePoints(s, xCenter, yCenter, x, y, color);
		while (x < y) {
			x++;
			if (p < 0) {
				p += 2 * x + 1;
			} else {
				y--;
				p += 2 * (x - y) + 1;
			}
			circlePoints(s, xCenter, yCenter, x, y, color);
		}
	}


	// ----------------------------------------------------------------	fill methods

	public static void boundaryFill(Sprite s, int x, int y, int fill, int boundary) {
		if ((x < 0) || (x >= s.width)) {
			return;
		}
		if ((y < 0) || (y >= s.height)) {
			return;
		}
		int current = getf(s, x, y);
		if ((current != boundary) & (current != fill)) {
			setf(s, x, y, fill);
			boundaryFill(s, x+1, y, fill, boundary);
			boundaryFill(s,x, y+1, fill, boundary);
			boundaryFill(s,x-1, y, fill, boundary);
			boundaryFill(s,x, y-1, fill, boundary);
		}
	}


	public static void floodFill(Sprite s, int x, int y, int fill) {
		if ((x < 0) || (x >= s.width)) {
			return;
		}
		if ((y < 0) || (y >= s.height)) {
			return;
		}
		int old = getf(s, x, y);
		if (old == fill) {
			return;
		}
		setf(s, x, y, fill);
		fillEast(s, x+1, y, fill, old);
		fillSouth(s, x, y+1, fill, old);
		fillWest(s, x-1, y, fill, old);
		fillNorth(s, x, y-1, fill, old);
	}
	private static void fillEast(Sprite s, int x, int y, int fill, int old) {
		if (x >= s.width) {
			return;
		}
		if (getf(s, x, y) == old) {
			setf(s, x, y, fill);
			fillEast(s, x+1, y, fill, old);
			fillSouth(s, x, y+1, fill, old);
			fillNorth(s, x, y-1, fill, old);
		}
	}
	private static void fillSouth(Sprite s, int x, int y, int fill, int old) {
		if (y >= s.height) {
			return;
		}
		if (getf(s, x, y) == old) {
			setf(s, x, y, fill);
			fillEast(s, x+1, y, fill, old);
			fillSouth(s, x, y+1, fill, old);
			fillWest(s, x-1, y, fill, old);
		}
	}
	private static void fillWest(Sprite s, int x, int y, int fill, int old) {
		if (x < 0) {
			return;
		}
		if (getf(s, x, y) == old) {
			setf(s, x, y, fill);
			fillSouth(s, x, y+1, fill, old);
			fillWest(s, x-1, y, fill, old);
			fillNorth(s, x, y-1, fill, old);
		}
	}
	private static void fillNorth(Sprite s, int x, int y, int fill, int old) {
		if (y < 0) {
			return;
		}
		if (getf(s, x, y) == old) {
			setf(s, x, y, fill);
			fillEast(s, x+1, y, fill, old);
			fillWest(s, x-1, y, fill, old);
			fillNorth(s, x, y-1, fill, old);
		}
	}


	public static void floodFill8(Sprite s, int x, int y, int fill, int old) {
		if ((x < 0) || (x >= s.width)) {
			return;
		}
		if ((y < 0) || (y >= s.height)) {
			return;
		}
		if (getf(s, x, y) == old) {
			setf(s, x, y, fill);
			floodFill8(s, x+1, y, fill, old);
			floodFill8(s, x, y+1, fill, old);
			floodFill8(s, x-1, y, fill, old);
			floodFill8(s, x, y-1, fill, old);
			floodFill8(s, x+1, y+1, fill, old);
			floodFill8(s, x-1, y+1, fill, old);
			floodFill8(s, x-1, y-1, fill, old);
			floodFill8(s, x+1, y-1, fill, old);
		}
	}
}
