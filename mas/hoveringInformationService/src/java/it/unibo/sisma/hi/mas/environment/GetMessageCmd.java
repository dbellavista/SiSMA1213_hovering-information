package it.unibo.sisma.hi.mas.environment;

import cartago.IBlockingCmd;

public class GetMessageCmd implements IBlockingCmd {

	private MessageQueue queue;
	private Object receiverName;
	private Message finalMsg;

	public GetMessageCmd(MessageQueue queue, Object receiverName) {
		super();
		this.queue = queue;
		this.receiverName = receiverName;
	}

	// http://cartago.sourceforge.net/?page_id=112

	@Override
	public void exec() {
		try {
			finalMsg = queue.getMessage(receiverName, true);
		} catch (InterruptedException e) {
			finalMsg = null;
		}
	}

	public Message getFinalMsg() {
		return finalMsg;
	}

}
