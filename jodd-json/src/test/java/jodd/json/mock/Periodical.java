package jodd.json.mock;

public abstract class Periodical {

	private String name;

	protected Periodical(String name) {
		this.name = name;
	}

	public abstract String getID();

	public abstract void setID(String id);

	public String getName() {
		return name;
	}
}
