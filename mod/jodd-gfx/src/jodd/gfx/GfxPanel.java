// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx;

import jodd.gfx.delay.Delayer;
import jodd.gfx.delay.NanoDelayer;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

/**
 * The base panel of gfx applications, bundled with some utility methods.
 */
public abstract class GfxPanel extends JPanel implements Runnable {

	// ---------------------------------------------------------------- major settings

	/**
	 * Panel name.
	 */
	public String name;

	/**
	 * Panel dimensions.
	 */
	public int width, height;

	/**
	 * Desired frame rate. If <code>0</code>, there will be no animation loop.
	 */
	public int framerate;


	/**
	 * {#Delayer delayer}.
	 */
	public Delayer delayer = new NanoDelayer();

	// ---------------------------------------------------------------- init

	/**
	 * Main sprite used as a screen, i.e. background.
	 */
	public Sprite screen;

	/**
 	 * Initialization is called by the GfxAppRunner. It performs the following:
	 * <li>calls the setup() method</li>
	 * <li>performs internal initialization, such as creates screen sprite etc.</li>
	 * <li>calls the init() method</li>
	 * <p>
	 * This method should never be called directly from client code, but by some
	 * of {@link jodd.gfx.runners.GfxAppletRunner runners}).
	 *
	 * @see #init()
	 * @see #ready()
	 */
	public final void initialize() {
		init();
		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
		setDoubleBuffered(true);
		screen = new Sprite(width, height);
		ready();
	}

	/**
	 * Setups the application. Must be implemented by gfx application
	 * to specify the name, width, height and desired framerate.
	 */
	public abstract void init();

	/**
	 * Optional callback invoked immediately after the initialization.
	 * Often used for creating various objects etc.
	 */
	public void ready() {}


	// ---------------------------------------------------------------- run

	/**
	 * Runs the gfx application.
	 */
	public void start() {
		if (framerate == 0) {
			return;
		}
		if (animator == null) {
			animator = new Thread(this);
			animator.start();
		}
	}

	/**
	 * Indicates that gfx application should update, but not render.
	 * Also, when rendering takes too long, updates will be
	 * still called at constant framerate.
	 */
	public void update() {}


	/**
	 * Indicates that frame should be rendered to screen. It is called by
	 * GfxAppRunner at constant framerate. However, if frame drawing
	 * takes long, framerate will drop.
	 */
	public abstract void paint();


	public void pause() {
        paused = true;
	}

	public void resume() {
		paused = false;
	}

	public void stop() {
		running = false;
	}

	// ---------------------------------------------------------------- animator thread

	private Thread animator;
	private boolean running;
	private boolean paused;

	public static final int MAX_SKIPPED_FRAMES = 16;

	/**
	 * Animator thread runs at specified framerate and invokes
	 * <code>loop()</code> in every cycle.
	 */
	public void run() {
		Graphics g = this.getGraphics();
		delayer.setDefaultDelay(1000000000L / framerate);
		running = true;
		paused = false;
		boolean paint = true;
		int skipped = 0;
		while (running) {
			delayer.start();
			if (paused == false) {
				update();
				if (skipped > MAX_SKIPPED_FRAMES) {
					skipped = 0;
					paint = true;
				}
				if (paint) {
					paint();
				} else {
					skipped++;
				}
			}
			g.drawImage(screen.img, 0, 0, null);		// active rendering
			paint = delayer.end();
		}
	}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (framerate == 0) {
			paint();
			g.drawImage(screen.img, 0, 0, null);
		}
    }

	// ---------------------------------------------------------------- various engine methods

	/**
	 * Delays execution by specified number of nanoseconds.
	 */
	public void sleep(long nanos) {
		delayer.sleep(nanos);
	}

	protected Random rnd = new Random();

	/**
	 * Returns random int from range [0, max).
	 */
	public int random(int max) {
		return rnd.nextInt(max);
	}

}
