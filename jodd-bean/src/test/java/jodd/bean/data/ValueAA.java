package jodd.bean.data;

public class ValueAA implements Value {

	@Override
	public String getString() {
		return value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	private String value;
}
