package it.unibo.sisma.hi.mas.sim;

import it.unibo.sisma.hoveringinf.entities.HoveringInformation;
import it.unibo.sisma.hoveringinf.entities.MobileNode;
import it.unibo.sisma.hoveringinf.entities.PieceOfHoveringInformation;
import it.unibo.sisma.hoveringinf.entities.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUIEnvironmentFactory {

	public World createWorld(double worldWidth, double worldHeight,
			Object[] nodes, Object[] pointOfInterest) {
		List<HoveringInformation> hoveringInformations = new ArrayList<>(
				pointOfInterest.length);
		List<MobileNode> mobileNodes = new ArrayList<>(nodes.length);
		List<PieceOfHoveringInformation> pieceOfHoveringInformation = Collections
				.emptyList();

		for (int i = 0; i < nodes.length; i++) {
			Object[] p = (Object[]) nodes[i];
			Object[] pos = (Object[]) p[2];
			MobileNode mn = new MobileNode(toInt(p[4]), toInt(p[5]),
					toInt(p[3]), toDouble(pos[0]), toDouble(pos[1]),
					Collections.<MobileNode> emptyList(),
					Collections.<PieceOfHoveringInformation> emptyList());
			mobileNodes.add(mn);
		}

		for (int i = 0; i < pointOfInterest.length; i++) {
			Object[] p = (Object[]) pointOfInterest[i];
			Object[] pos = (Object[]) p[1];
			HoveringInformation hi = new HoveringInformation(toDouble(pos[0]),
					toDouble(pos[1]), 50, 0);
			hoveringInformations.add(hi);
		}

		return new World(hoveringInformations, mobileNodes,
				pieceOfHoveringInformation, (int) worldWidth, (int) worldHeight);
	}

	private double toDouble(Object n) {
		return ((Number) n).doubleValue();
	}

	private int toInt(Object n) {
		return ((Number) n).intValue();
	}

}
