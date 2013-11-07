package it.unibo.sisma.hoveringinf.entities;

import java.util.ArrayList;
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
	private List<PieceOfHoveringInformation> children;

	public HoveringInformation(double xAnchor, double yAnchor,
			double anchorRange) {
		super();
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;
		this.anchorRange = anchorRange;
		children = new ArrayList<>();
	}

	void addChild(PieceOfHoveringInformation piece) {
		children.add(piece);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(anchorRange);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		temp = Double.doubleToLongBits(xAnchor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yAnchor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HoveringInformation other = (HoveringInformation) obj;
		if (Double.doubleToLongBits(anchorRange) != Double
				.doubleToLongBits(other.anchorRange))
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (Double.doubleToLongBits(xAnchor) != Double
				.doubleToLongBits(other.xAnchor))
			return false;
		if (Double.doubleToLongBits(yAnchor) != Double
				.doubleToLongBits(other.yAnchor))
			return false;
		return true;
	}

}