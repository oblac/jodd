package jodd.json.mock;

public class Address {
	private Person person;
	private String street;
	private String city;
	private String state;
	private Zipcode zipcode;

	public Address() {
	}

	public Address(String street, String city, String state, Zipcode zipcode) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Zipcode getZipcode() {
		return zipcode;
	}

	public void setZipcode(Zipcode zipcode) {
		this.zipcode = zipcode;
	}


	public void setPerson(Person person) {
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Address address = (Address) o;

		if (city != null ? !city.equals(address.city) : address.city != null) {
			return false;
		}
		if (state != null ? !state.equals(address.state) : address.state != null) {
			return false;
		}
		if (street != null ? !street.equals(address.street) : address.street != null) {
			return false;
		}
		if (zipcode != null ? !zipcode.equals(address.zipcode) : address.zipcode != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = street != null ? street.hashCode() : 0;
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (zipcode != null ? zipcode.hashCode() : 0);
		return result;
	}
}
