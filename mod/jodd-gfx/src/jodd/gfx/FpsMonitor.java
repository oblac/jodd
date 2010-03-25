// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx;

/**
 * Calculates current FPS.
 * todo add array with last XX value for average fps
 */
public class FpsMonitor {

	protected long time = System.currentTimeMillis();
	protected int fps;

	/**
	 * Returns current FPS.
	 */
	public int getFps() {
		return fps;
	}


	private int frames;

	/**
	 * Monitors current fps and average fps. Usually used in the <code>paint()</code>
	 * method or inside the game loop.
	 * Returns <code>true</code> when fps value is available.
	 */
	public boolean monitor() {
		frames++;
		if ((System.currentTimeMillis() - time) >= 1000) {
			time = System.currentTimeMillis();
			fps = frames;
			frames = 0;
			return true;
		}
		return false;
	}

	/**
	 * Prints out FPS.
	 */
	public void print() {
		if (monitor()) {
			System.out.println("fps: " + getFps());
		}
	}

}
