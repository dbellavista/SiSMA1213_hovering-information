package it.unibo.sisma.hi.mas.environment;

public class RecentCommunication {

	public static final double validity = 2 * 1000;
	private Object sender;
	private Object receiver;
	private double timestamp;

	public RecentCommunication(Object sender, Object receiver) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.timestamp = System.currentTimeMillis();
	}

	public Object getSender() {
		return sender;
	}

	public Object getReceiver() {
		return receiver;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - timestamp > validity;
	}

	public void update() {
		this.timestamp = System.currentTimeMillis();
	}

	public Object[] toObjectArray() {
		return new Object[] { sender, receiver };
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((receiver == null) ? 0 : receiver.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecentCommunication other = (RecentCommunication) obj;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		return true;
	}

}
