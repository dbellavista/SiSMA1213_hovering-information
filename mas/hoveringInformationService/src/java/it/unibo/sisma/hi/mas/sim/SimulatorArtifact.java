// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.sim;

import it.unibo.sisma.hi.mas.environment.PersonSenseData;
import it.unibo.sisma.hi.mas.environment.PoTSenseData;

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
@ARTIFACT_INFO(outports = { @OUTPORT(name = "inq-env-port"),
		@OUTPORT(name = "ui-port") })
public class SimulatorArtifact extends Artifact {

	private int guiRefresh;

	void init(int guiRefresh) {
		this.guiRefresh = guiRefresh;
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
			execLinkedOp("inq-env-port", "inquireEnvironment", people,
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
		try {
			execLinkedOp("ui-port", "render", factory.createWorld(worldWidth, worldHeight,
					(ArrayList<PersonSenseData>) people,
					(ArrayList<PoTSenseData>) pointOfInterest));
		} catch (OperationException e) {
			e.printStackTrace();
			failed("UI linked operation failed", "fail", e);
		}
	}
}