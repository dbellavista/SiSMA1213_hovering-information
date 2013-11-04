package it.unibo.sisma.hi.mas.environment;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class MessageQueue {

	private Deque<Message> messages;

	public MessageQueue() {
		messages = new ArrayDeque<>();
	}

	public synchronized void insertMessage(Message mess) {
		messages.add(mess);
		notifyAll();
	}

	public synchronized Message getMessage(Object receiverName, boolean blocking)
			throws InterruptedException {
		while (true) {
			Message m = getFirst(receiverName);
			if (m == null && blocking) {
				wait();
			} else {
				return m;
			}
		}
	}

	private synchronized Message getFirst(Object receiverName) {
		Iterator<Message> it = messages.iterator();
		while (it.hasNext()) {
			Message m = it.next();
			if (m.getReceiverName().equals(receiverName)) {
				it.remove();
				return m;
			}
		}
		return null;
	}
}