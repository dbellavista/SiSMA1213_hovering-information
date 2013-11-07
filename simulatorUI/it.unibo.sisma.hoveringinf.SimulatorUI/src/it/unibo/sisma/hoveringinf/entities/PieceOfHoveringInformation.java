package it.unibo.sisma.hoveringinf.entities;

/**
 * 
 * A piece of hovering information.
 * 
 * @author Daniele Bellavista
 *
 */
public class PieceOfHoveringInformation {

	private HoveringInformation parent;
	private MobileNode host;

	private int size;
	
	public PieceOfHoveringInformation(HoveringInformation parent, int size) {
		this.parent = parent;
		this.size = size;
		parent.addChild(this);
	}

	void setHost(MobileNode host) {
		this.host = host;
	}
	
	public HoveringInformation getParent() {
		return parent;
	}

	public MobileNode getHost() {
		return host;
	}

	public int getSize() {
		return size;
	}
	
}
