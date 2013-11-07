package it.unibo.sisma.hi.mas.sim;

import it.unibo.sisma.hoveringinf.entities.HoveringInformation;
import it.unibo.sisma.hoveringinf.entities.MobileNode;
import it.unibo.sisma.hoveringinf.entities.PieceOfHoveringInformation;
import it.unibo.sisma.hoveringinf.entities.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GUIEnvironmentFactory {

	public World createWorld(double worldWidth, double worldHeight,
			Object[] nodes, Object[] pointOfInterest) {
		List<HoveringInformation> hoveringInformations = new ArrayList<>(
				pointOfInterest.length);
		HashMap<String, HoveringInformation> hovering_names = new HashMap<>();
		
		List<MobileNode> mobileNodes = new ArrayList<>(nodes.length);
		List<PieceOfHoveringInformation> pieceOfHoveringInformation = new ArrayList<>();
		

		for (int i = 0; i < pointOfInterest.length; i++) {
			Object[] p = (Object[]) pointOfInterest[i];
			Object[] pos = (Object[]) p[1];
			HoveringInformation hi = new HoveringInformation(toDouble(pos[0]),
					toDouble(pos[1]), 50);
			hoveringInformations.add(hi);
			hovering_names.put((String) p[0], hi);
		}

		for (int i = 0; i < nodes.length; i++) {
			Object[] p = (Object[]) nodes[i];
			Object[] pos = (Object[]) p[2];
			ArrayList<PieceOfHoveringInformation> tmpPieces = new ArrayList<>();
			for (Object o : (Object[]) p[6]) {
				Object[] phi_data = (Object[]) o;
				Number n = (Number) phi_data[1];
				tmpPieces.add(new PieceOfHoveringInformation(
						hovering_names.get(phi_data[0]), n.intValue()));
			}

			MobileNode mn = new MobileNode(toInt(p[4]), toInt(p[5]),
					toInt(p[3]), toDouble(pos[0]), toDouble(pos[1]),
					Collections.<MobileNode> emptyList(), tmpPieces);
			mobileNodes.add(mn);
			pieceOfHoveringInformation.addAll(tmpPieces);
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
