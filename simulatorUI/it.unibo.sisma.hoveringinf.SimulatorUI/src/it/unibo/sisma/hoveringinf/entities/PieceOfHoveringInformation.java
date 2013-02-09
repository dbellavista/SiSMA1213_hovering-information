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
	
	public PieceOfHoveringInformation(HoveringInformation parent,
			MobileNode host) {
		super();
		this.parent = parent;
		this.host = host;
	}

	public HoveringInformation getParent() {
		return parent;
	}

	public MobileNode getHost() {
		return host;
	}
	
}
