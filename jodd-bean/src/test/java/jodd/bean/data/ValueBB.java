package jodd.bean.data;

public class ValueBB implements Value {

	@Override
	public String getString() {
		return String.valueOf(value);
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	private int value;
	
}
