// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

/**
 * SwingSpy examines swing layouts.
 * <p>
 * Installation is simple, just add the following lines in your startup code:
 * <pre>
 * try {
 * 		Class.forName("jodd.swingspy.SwingSpy").getMethod("install").invoke(null);
 * } catch (Exception e) {
 * 		System.err.println("SwingSpy is not installed... ");
 * }
 * </pre>
 * <br>
 * When SwingSpy is installed, you can invoke it by pressing
 * <b>SHIFT</b> + <b>CTRL</b> + <b>click</b> hot-key combination.
 */
package jodd.swingspy;