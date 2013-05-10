package it.unibo.sisma.hi.mas.environment;

public class Message {

	private Object message;
	private Object receiverName;
	private Object senderID;

	public Message(Object message, Object receiverName, Object senderID) {
		this.message = message;
		this.receiverName = receiverName;
		this.senderID = senderID;
	}

	public Object getMessage() {
		return message;
	}

	public Object getReceiverName() {
		return receiverName;
	}

	public Object getSenderID() {
		return senderID;
	}

}