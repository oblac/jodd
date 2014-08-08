package jodd.introspector.tst;

public class Overload {

	String company;

	// not a property setter
	public void setCompany(StringBuilder sb) {
		this.company = sb.toString();
	}

	public String getCompany() {
		return company;
	}
}