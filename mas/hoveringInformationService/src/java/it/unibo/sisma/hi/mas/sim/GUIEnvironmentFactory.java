package it.unibo.sisma.hi.mas.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import it.unibo.sisma.hi.mas.social.PersonSenseData;
import it.unibo.sisma.hi.mas.social.PoTSenseData;
import it.unibo.sisma.hoveringinf.entities.HoveringInformation;
import it.unibo.sisma.hoveringinf.entities.MobileNode;
import it.unibo.sisma.hoveringinf.entities.PieceOfHoveringInformation;
import it.unibo.sisma.hoveringinf.entities.World;

public class GUIEnvironmentFactory {

	public World createWorld(double worldWidth, double worldHeight,
			ArrayList<PersonSenseData> people, ArrayList<PoTSenseData> pointOfInterest) {
		List<HoveringInformation> hoveringInformations = new ArrayList<>(
				pointOfInterest.size());
		List<MobileNode> mobileNodes = new ArrayList<>(people.size());
		List<PieceOfHoveringInformation> pieceOfHoveringInformation = Collections
				.emptyList();

		for (PersonSenseData p : people) {
			MobileNode mn = new MobileNode(100, 0, 0, p.getPosition()[0],
					p.getPosition()[1], Collections.<MobileNode> emptyList(),
					Collections.<PieceOfHoveringInformation> emptyList());
			mobileNodes.add(mn);
		}

		for (PoTSenseData p : pointOfInterest) {
			HoveringInformation hi = new HoveringInformation(
					p.getPosition()[0], p.getPosition()[1], 50, 0);
			hoveringInformations.add(hi);
		}

		return new World(hoveringInformations, mobileNodes,
				pieceOfHoveringInformation, (int) worldWidth, (int) worldHeight);
	}
}
