package it.unibo.sisma.hi.mas.environment;

public class Message {

	private Object message;
	private Object receiverName;
	private Object senderID;
	private Object senderName;

	public Message(Object message, Object receiverName, Object senderID, Object senderName) {
		this.message = message;
		this.receiverName = receiverName;
		this.senderID = senderID;
		this.senderName = senderName;
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
	
	public Object getSenderName() {
		return senderName;
	}

}