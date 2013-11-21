package it.unibo.sisma.hi.mas.environment;

import cartago.IBlockingCmd;

public class SleepCmd implements IBlockingCmd {

	private long timeout;

	public SleepCmd(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public void exec() {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
		}
	}

}
