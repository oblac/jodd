// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.swing;

import jodd.util.StringPool;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Helps to schedule delayed invocation of some task. <code>SwingUtilities.invokeLater()</code> method may be used,
 * but not always - if there are several events that could trigger such operation may occur subsequently
 * we are actually interested not in delayed invocation, but in one that will occur with specified
 * delay after <b>last</b> event in that sequence.
 */
public class DelayedInvoker extends Timer {

	protected Object parameter;                     // data that could be passed to listener
	public static final int DEFAULT_DELAY = 200;        // default delay


	/**
	 * Creates invoker that will fire event to provided listener after specified delay.
	 */
	public DelayedInvoker(int delay, ActionListener anActionListener) {
		super(delay, anActionListener);
		setRepeats(false);
	}

	/**
	 * Creates invoker that will fire event to provided listener after default delay.
	 */
	public DelayedInvoker(ActionListener actionListener) {
		this(DEFAULT_DELAY, actionListener);
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

	/**
	 * Schedules invocation of listener. Stores parameter and does the {@link #takeUp()}.
	 */
	public void takeUp(Object parameter) {
		setParameter(parameter);
		takeUp();
	}


	/**
	 * Scheduled invocation of listener - if timer is currently running, it's restarts it (so
	 * already scheduled invocation will not fired, If timer is not started - it simply starts it.
	 * Therefore, after calling this method the provided listener will be invoked once after
	 * specified delay if no subsequent invocations of that method will be performed.
	 * Many subsequent calls to this method could be performed, but listener will be called after
	 * specified delay only after LAST invocation.
	 */
	public void takeUp() {
		if (isRunning()) {
			restart();
		} else {
			start();
		}
	}

	/**
	 * Schedules invocation of listener with provided data and delay. Many subsequent calls to this
	 * method could be performed, but listener will be called after specified delay only after
	 * LAST invocation.
	 */
	public void takeUp(Object parameter, int delay) {
		setParameter(parameter);
		takeUp(delay);
	}

	/**
	 * Schedules invocation of specified listener after specified delay. If timer is started, performs restart of it,
	 * otherwise starts it. Many subsequent calls to this method could be performed, but listener
	 * performed, but listener LAST invocation.
	 */
	public void takeUp(int delay) {
		setDelay(delay);
		setInitialDelay(delay);
		if (isRunning()) {
			restart();
		} else {
			start();
		}
	}

	/**
	 * Utility method that allows to invoke listener associated with invoker immediately.
	 */
	public void force() {
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, StringPool.EMPTY));
		if (isRunning()) {
			stop();
		}
	}

	/**
	 * Internal method that fires invocation of associated listener.
	 */
	@Override
	protected void fireActionPerformed(final ActionEvent actionEvent) {
		SwingUtilities.invokeLater(new Runnable() {
			ActionEvent fActionEvent = actionEvent;
			public void run() {
				DelayedInvoker.super.fireActionPerformed(fActionEvent);
			}
		});
	}
}
