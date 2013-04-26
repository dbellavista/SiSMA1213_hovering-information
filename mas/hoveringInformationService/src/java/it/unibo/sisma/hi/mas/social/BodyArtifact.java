// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.social;

import java.util.Collection;

import cartago.*;

@ARTIFACT_INFO(
	outports = {
		@OUTPORT(name = "out-1")
	}
) public class BodyArtifact extends Artifact {

	private Object ID;

	void init(Object ID) {
		defineObsProperty("people", (Object) new double[0][0]);
		defineObsProperty("points", (Object) new double[0][0]);
		this.ID = ID;
	}

	@OPERATION void sense() {
		OpFeedbackParam<Collection<PersonSenseData>> people = new OpFeedbackParam<>();
		OpFeedbackParam<Collection<PoTSenseData>> points = new OpFeedbackParam<>();
		try {
			execLinkedOp("out-1", "sense", ID, people, points);
			ObsProperty peoplep = getObsProperty("people");
			ObsProperty pointsp = getObsProperty("points");

			peoplep.updateValue(toArray(people.get()));
			pointsp.updateValue(toArray(points.get()));

		} catch (Exception e) {
			e.printStackTrace();
			failed("Sense linked operation failed", "fail", ID, e);
		}
	}
	@INTERNAL_OPERATION Object toArray(Collection<? extends IToArrayable> collection) {
		Object[] res = new Object[collection.size()];
		int i = 0;
		for (IToArrayable a : collection) {
			res[i++] = a.toArray();
		}
		return res;
	}

	@OPERATION void move(double x, double y, double speed) {
		try {
			execLinkedOp("out-1", "move", ID, x, y, speed);
		} catch (Exception e) {
			e.printStackTrace();
			failed("Move linked operation failed", "fail", ID, e);
		}
	}
}
