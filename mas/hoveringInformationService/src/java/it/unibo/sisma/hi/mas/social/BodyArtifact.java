// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.social;

import java.util.Random;

import it.unibo.sisma.hi.mas.environment.PersonSenseData;
import it.unibo.sisma.hi.mas.environment.PoTSenseData;
import it.unibo.sisma.hi.mas.interfaces.IToArrayable;
import cartago.*;

@ARTIFACT_INFO(
	outports = {
		@OUTPORT(name = "env-link")
	}
) public class BodyArtifact extends Artifact {

	private Object ID;
	private Random r;

	void init(Object ID) {
		r = new Random();
		defineObsProperty("people", (Object) new Object[0][0]);
		defineObsProperty("points", (Object) new Object[0][0]);
		this.ID = ID;
	}

	@OPERATION void sense() {
		OpFeedbackParam<PersonSenseData[]> people = new OpFeedbackParam<>();
		OpFeedbackParam<PoTSenseData[]> points = new OpFeedbackParam<>();
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
	@INTERNAL_OPERATION Object toArray(IToArrayable[] collection) {
		Object[] res = new Object[collection.length];
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
	
	@OPERATION void randomInt(OpFeedbackParam<Integer> res, int min, int max) {
		res.set(r.nextInt(max + 1 - min) + min);
	}
}
