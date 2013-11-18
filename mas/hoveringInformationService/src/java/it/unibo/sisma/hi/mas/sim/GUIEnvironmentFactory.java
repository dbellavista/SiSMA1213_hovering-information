package it.unibo.sisma.hi.mas.sim;

import it.unibo.sisma.hoveringinf.entities.HoveringInformation;
import it.unibo.sisma.hoveringinf.entities.MobileNode;
import it.unibo.sisma.hoveringinf.entities.PieceOfHoveringInformation;
import it.unibo.sisma.hoveringinf.entities.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUIEnvironmentFactory {

	public World createWorld(double worldWidth, double worldHeight,
			Object[] nodes, Object[] pointOfInterest, Object[] listRecent_tmp) {
		List<HoveringInformation> hoveringInformations = new ArrayList<>(
				pointOfInterest.length);
		HashMap<String, HoveringInformation> hovering_names = new HashMap<>();
		HashMap<String, MobileNode> mobNodesNames = new HashMap<>();

		List<MobileNode> mobileNodes = new ArrayList<>(nodes.length);
		List<PieceOfHoveringInformation> pieceOfHoveringInformation = new ArrayList<>();

		for (int i = 0; i < pointOfInterest.length; i++) {
			Object[] p = (Object[]) pointOfInterest[i];
			Object[] pos = (Object[]) p[1];
			HoveringInformation hi = new HoveringInformation((String) p[0],
					toDouble(pos[0]), toDouble(pos[1]), toDouble(pos[2]));
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
				tmpPieces.add(new PieceOfHoveringInformation(hovering_names
						.get(phi_data[0]), n.intValue()));
			}

			List<MobileNode> connList = new ArrayList<>();

			MobileNode mn = new MobileNode((String) p[0], toInt(p[4]),
					toInt(p[5]), toInt(p[3]), toDouble(pos[0]),
					toDouble(pos[1]), connList, tmpPieces);
			mobNodesNames.put((String) p[0], mn);
			mobileNodes.add(mn);
			pieceOfHoveringInformation.addAll(tmpPieces);
		}

		for (Object o : listRecent_tmp) {
			Object[] val = (Object[]) o;
			mobNodesNames.get(val[0]).getConnectedNodes()
					.add(mobNodesNames.get(val[1]));
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
