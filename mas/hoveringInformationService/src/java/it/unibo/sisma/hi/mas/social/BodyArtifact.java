// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.social;

import it.unibo.sisma.hi.mas.environment.PersonSenseData;
import it.unibo.sisma.hi.mas.environment.PoTSenseData;
import it.unibo.sisma.hi.mas.interfaces.IToArrayable;

import java.util.Collection;

import cartago.*;

@ARTIFACT_INFO(
	outports = {
		@OUTPORT(name = "env-link")
	}
) public class BodyArtifact extends Artifact {

	private Object ID;

	void init(Object ID) {
		defineObsProperty("people", (Object) new Object[0][0]);
		defineObsProperty("points", (Object) new Object[0][0]);
		this.ID = ID;
	}

	@OPERATION void sense() {
		OpFeedbackParam<Collection<PersonSenseData>> people = new OpFeedbackParam<>();
		OpFeedbackParam<Collection<PoTSenseData>> points = new OpFeedbackParam<>();
		try {
			execLinkedOp("env-link", "sense", ID, people, points);
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
			execLinkedOp("env-link", "move", ID, x, y, speed);
		} catch (Exception e) {
			e.printStackTrace();
			failed("Move linked operation failed", "fail", ID, e);
		}
	}
}
