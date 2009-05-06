package jodd.gfx;

import jodd.gfx.delay.Delayer;
import jodd.gfx.delay.DefaultDelayer;

import javax.swing.JPanel;
import java.util.Random;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * The core of the gfx applications, bundled with some utilility methods.
 */
public abstract class GfxPanel extends JPanel implements Runnable {

	// ---------------------------------------------------------------- major settings

	/**
	 * Setting: name.
	 */
	public String name;

	/**
	 * Setting: dimensions.
	 */
	public int width, height;

	/**
	 * Setting: desired frame rate. If set to 0, there will be no animation loop.
	 */
	public int framerate;


	// ---------------------------------------------------------------- other settings

	public Delayer delayer = new DefaultDelayer();

	// ---------------------------------------------------------------- init

	/**
	 * Main sprite used as a screen, i.e. backgorund.
	 */
	public Sprite screen;

	/**
 	 * Initialization is called by the GfxAppRunner. It performs the following:
	 * <li>calls the setup() method</li>
	 * <li>performs internal initialization, such as creates screen sprite etc.</li>
	 * <li>calls the init() method</li>
	 * <p>
	 *
	 * This method should never be called directly from client code, but by some
	 * of <i>runners</i>.
	 *
	 * @see #setup()
	 * @see #init()
	 */
	public void doInit() {
		setup();
		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
		setDoubleBuffered(true);
		screen = new Sprite(width, height);
		init();
	}

	/**
	 * Setups the application. Must be implemented by gfx application.
	 * It must set the name, width, height and framerate properties.
	 */
	public abstract void setup();

	/**
	 * Optional callback invoked immediatly after the initialization.
	 * Often used for creating various objects etc.
	 */
	public void init() {}


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
	 * Also, when renedring takes too long, updates will be
	 * still called at constant framerate.
	 */
	public void update() {}


	/**
	 * Indicates that frame should be renedered to screen. It is called by
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
	 * Animator thread runs at specified framerate and invokes <code>loop()</code>
	 * in every cycle.
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

	protected Random rnd = new Random(System.currentTimeMillis());

	/**
	 * Returns random int from range [0, max).
	 */
	public int random(int max) {
		return rnd.nextInt(max);
	}

}
