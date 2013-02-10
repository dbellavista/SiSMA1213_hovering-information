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
	private List<PieceOfHoveringInformation> pieceOfHoveringInformation;
	private int width;
	private int height;
	
	public World(List<HoveringInformation> hoveringInformations,
			List<MobileNode> mobileNodes,
			List<PieceOfHoveringInformation> pieceOfHoveringInformation, int width, int height) {
		super();
		this.hoveringInformations = hoveringInformations;
		this.mobileNodes = mobileNodes;
		this.pieceOfHoveringInformation = pieceOfHoveringInformation;
		this.width = width;
		this.height = height;
	}

	public List<HoveringInformation> getHoveringInformations() {
		return hoveringInformations;
	}

	public List<MobileNode> getMobileNodes() {
		return mobileNodes;
	}

	public List<PieceOfHoveringInformation> getPieceOfHoveringInformation() {
		return pieceOfHoveringInformation;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
