package jodd.json.mock.superhero;

public class SecretLair {

	private String name;

	protected SecretLair() {
	}

	public SecretLair(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SecretLair that = (SecretLair) o;

		if (name != null ? !name.equals(that.name) : that.name != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}
}
