package jodd.bean.data;

public class ValueA implements Value {

	public ValueA(String value) {
		this.value = value;
	}
	
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
