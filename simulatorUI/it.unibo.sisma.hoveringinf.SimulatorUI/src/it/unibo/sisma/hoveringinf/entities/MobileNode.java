package it.unibo.sisma.hoveringinf.entities;

import java.util.List;

/**
 * Mobile node.
 * 
 * @author Daniele Bellavista
 * 
 */
public class MobileNode {

	private int bufferSize;
	private int bufferUsage;

	private double commRange;

	private double xPos;
	private double yPos;

	private List<MobileNode> connectedNodes;
	private List<PieceOfHoveringInformation> hosted;

	public MobileNode(int bufferSize, int bufferUsage, double commRange,
			double xPos, double yPos, List<MobileNode> connectedNodes,
			List<PieceOfHoveringInformation> hosted) {
		super();
		this.bufferSize = bufferSize;
		this.bufferUsage = bufferUsage;
		this.commRange = commRange;
		this.xPos = xPos;
		this.yPos = yPos;
		this.connectedNodes = connectedNodes;
		this.hosted = hosted;

		for (PieceOfHoveringInformation pieceOfHoveringInformation : hosted) {
			pieceOfHoveringInformation.setHost(this);
		}

	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int getBufferUsage() {
		return bufferUsage;
	}

	public double getCommRange() {
		return commRange;
	}

	public double getxPos() {
		return xPos;
	}

	public double getyPos() {
		return yPos;
	}

	public List<MobileNode> getConnectedNodes() {
		return connectedNodes;
	}

	public List<PieceOfHoveringInformation> getHosted() {
		return hosted;
	}

}
