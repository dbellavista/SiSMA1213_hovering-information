package it.unibo.sisma.hi.mas.social;

public class PersonSenseData implements IToArrayable {
	
	private double[] position;
	private Object ID;

	public PersonSenseData(double[] position, Object iD) {
		super();
		this.position = position;
		ID = iD;
	}

	public double[] getPosition() {
		return position;
	}

	public Object getID() {
		return ID;
	}

	@Override
	public Object[] toArray() {
		return new Object[] { ID, position[0], position[1] };
	}
}
