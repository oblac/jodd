package jodd.io.watch;

import java.io.File;

public class DirWatcherEvent {

	private final Type type;
	private final File target;
	private final long timestamp;

	/**
	 * Event type that describes file change.
	 */
	public enum Type {
		CREATED,
		DELETED,
		MODIFIED
	}

	DirWatcherEvent(Type type, File target) {
		this.type = type;
		this.target = target;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Returns event type.
	 */
	public Type type() {
		return type;
	}

	/**
	 * Returns event target.
	 */
	public File target() {
		return target;
	}

	/**
	 * Returns event creation timestamp.
	 */
	public long timestamp() {
		return timestamp;
	}
}
