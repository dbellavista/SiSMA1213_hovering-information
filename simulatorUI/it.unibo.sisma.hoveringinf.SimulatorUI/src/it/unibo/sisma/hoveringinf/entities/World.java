package it.unibo.sisma.hoveringinf.entities;

import java.util.List;

/**
 * The world to be drawn.
 * @author Daniele Bellavista
 *
 */
public class World {

	private List<HoveringInformation> hoveringInformations; 	
	private List<MobileNode> mobileNodes;
	private List<MobileNode> pieceOfHoveringInformation;
	
	public World(List<HoveringInformation> hoveringInformations,
			List<MobileNode> mobileNodes,
			List<MobileNode> pieceOfHoveringInformation) {
		super();
		this.hoveringInformations = hoveringInformations;
		this.mobileNodes = mobileNodes;
		this.pieceOfHoveringInformation = pieceOfHoveringInformation;
	}
	
	
	
}
