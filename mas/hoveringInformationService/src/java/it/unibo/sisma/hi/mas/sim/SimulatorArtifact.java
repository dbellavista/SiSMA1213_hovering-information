// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.sim;

import it.unibo.sisma.hi.mas.social.PersonSenseData;
import it.unibo.sisma.hi.mas.social.PoTSenseData;
import it.unibo.sisma.hoveringinf.graphic.WindowCloseListener;
import it.unibo.sisma.hoveringinf.system.SimulatorUI;

import java.util.ArrayList;
import java.util.Collection;

import cartago.*;

/**
 * Simulator artifact: input configuration, configure, start and show the
 * system.
 * 
 * @author Daniele Bellavista
 * 
 */
@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-1") })
public class SimulatorArtifact extends Artifact {

	private SimulatorUI ui;

	void init(int guiWidth, int guiHeight, int guiRefresh) {
		ui = new SimulatorUI();
		ui.init(guiWidth, guiHeight);
		ui.config(new WindowCloseListener() {
			@Override
			public void onClose() {
				System.exit(0);
			}
		});
		ui.start();
	}

	@OPERATION
	void inquireNodes() {

	}

	@OPERATION
	void inquireHovering() {

	}

	@OPERATION
	void inquireEnvironment(
			OpFeedbackParam<Collection<PersonSenseData>> people,
			OpFeedbackParam<Collection<PoTSenseData>> pointOfInterest,
			OpFeedbackParam<Double> worldWidth,
			OpFeedbackParam<Double> worldHeight) {
		try {
			execLinkedOp("out-1", "inquireEnvironment", people,
					pointOfInterest, worldWidth, worldHeight);
		} catch (Exception e) {
			e.printStackTrace();
			failed("Environment linked operation failed", "fail", e);
		}
	}

	@SuppressWarnings("unchecked")
	@OPERATION
	void showSimulation(Object people, Object pointOfInterest,
			double worldWidth, double worldHeight) {
		GUIEnvironmentFactory factory = new GUIEnvironmentFactory();
		ui.render(factory.createWorld(worldWidth, worldHeight,
				(ArrayList<PersonSenseData>) people,
				(ArrayList<PoTSenseData>) pointOfInterest));
	}
}