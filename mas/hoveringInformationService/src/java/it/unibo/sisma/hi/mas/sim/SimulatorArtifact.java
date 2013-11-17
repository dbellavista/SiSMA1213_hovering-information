// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.sim;

import cartago.ARTIFACT_INFO;
import cartago.Artifact;
import cartago.OPERATION;
import cartago.OUTPORT;
import cartago.OpFeedbackParam;
import cartago.OperationException;

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

	@SuppressWarnings("unused")
	private int guiRefresh;

	void init(int guiRefresh) {
		this.guiRefresh = guiRefresh;
	}

	@OPERATION
	void inquireHovering() {

	}

	@OPERATION
	void inquireEnvironment(OpFeedbackParam<Object[]> people,
			OpFeedbackParam<Object[]> pointOfInterest,
			OpFeedbackParam<Object[][]> listRecent,
			OpFeedbackParam<Double> worldWidth,
			OpFeedbackParam<Double> worldHeight) {
		try {
			execLinkedOp("inq-env-port", "inquireEnvironment", people,
					pointOfInterest, listRecent, worldWidth, worldHeight);
		} catch (Exception e) {
			e.printStackTrace();
			failed("Environment linked operation failed", "fail", e);
		}
	}

	@OPERATION
	void showSimulation(Object[] nodes, Object[] pointOfInterest, Object[] listRecent,
			double worldWidth, double worldHeight) {
		GUIEnvironmentFactory factory = new GUIEnvironmentFactory();
		try {
			execLinkedOp("ui-port", "render", factory.createWorld(worldWidth,
					worldHeight, nodes, pointOfInterest, listRecent));
		} catch (OperationException e) {
			e.printStackTrace();
			failed("UI linked operation failed", "fail", e);
		}
	}
}