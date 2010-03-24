// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx;

/**
 * Calculates current FPS.
 */
public class FpsMonitor {

	private long time = System.currentTimeMillis();
	private int fps;
	private int fpssum;
	private int fpscount = -5;

	/**
	 * Returns current FPS.
	 */
	public int get() {
		return fps;
	}

	/**
	 * Returns average FPS.
	 */
	public float getAverage() {
		if (fpscount > 0) {
			return (float) fpssum / fpscount;
		} else {
			return 0;
		}
	}

	private int frames;

	/**
	 * Monitors current fps and average fps. It is usualy used in the paint()
	 * method, or inside the game loop.
	 */
	public boolean monitor() {
		frames++;
		if ((System.currentTimeMillis() - time) >= 1000) {
			time = System.currentTimeMillis();
			fps = frames;
			frames = 0;

			fpscount++;
			if (fpscount > 0) {
				fpssum += fps;
			}
			return true;
		}
		return false;
	}

	public void print() {
		if (monitor()) {
			System.out.println("fps: " + get() + "     av/fps: " + getAverage());
		}
	}


}
