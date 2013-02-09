package it.unibo.sisma.hoveringinf.entities;

import java.util.List;

/**
 * An hovering information.
 * 
 * @author Daniele Bellavista
 *
 */
public class HoveringInformation {

	private double xAnchor;
	private double yAnchor;
	private double anchorRange;
	
	private int size;
	
	private List<PieceOfHoveringInformation> children;

	public HoveringInformation(double xAnchor, double yAnchor,
			double anchorRange, int size,
			List<PieceOfHoveringInformation> children) {
		super();
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;
		this.anchorRange = anchorRange;
		this.size = size;
		this.children = children;
	}

	public double getxAnchor() {
		return xAnchor;
	}

	public double getyAnchor() {
		return yAnchor;
	}

	public double getAnchorRange() {
		return anchorRange;
	}

	public int getSize() {
		return size;
	}

	public List<PieceOfHoveringInformation> getChildren() {
		return children;
	}
	
}
