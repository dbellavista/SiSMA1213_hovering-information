package it.unibo.sisma.hi.mas.hs;

public class Data {

	private String value;
	private double size;

	public Data(String value, double size) {
		super();
		this.value = value;
		this.size = size;
	}

	public String getValue() {
		return value;
	}

	public double getSize() {
		return size;
	}

	public void setValue(String newValue) {
		this.value = newValue;
		
	}

}
