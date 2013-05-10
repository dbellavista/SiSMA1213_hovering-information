// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;


import java.util.Collection;

import cartago.*;

@ARTIFACT_INFO(outports = { @OUTPORT(name = "env-link") })
public class MobileResourceArtifact extends Artifact {

	private double range;
	private double storage;
	private Object ID;

	void init(Object ID, Number range, Number storage) {
		this.ID = ID;
		this.range = range.doubleValue();
		this.storage = storage.doubleValue();
		defineObsProperty("neighbours", (Object) new Object[0]);
		defineObsProperty("position", (Object) new Object[0]);
	}

	@OPERATION
	void discoverNeighbour() {
		OpFeedbackParam<Collection<Object>> mobileIDs = new OpFeedbackParam<>();

		try {
			execLinkedOp("env-link", "discoverneighbour", ID, range, mobileIDs);
			ObsProperty neighbours = getObsProperty("neighbours");

			neighbours.updateValue(mobileIDs.get().toArray());

		} catch (Exception e) {
			e.printStackTrace();
			failed("DiscoverNeighbour linked operation failed", "fail", ID, e);
		}
	}

	@OPERATION
	void sendMessage(Object receiverID, Object receiverName, Object message) {
		try {
			execLinkedOp("env-link", "sendMessage", ID, range, receiverID,
					receiverName, message);
		} catch (Exception e) {
			e.printStackTrace();
			failed("sendMessage linked operation failed", "fail", ID, e);
		}
	}

	@OPERATION
	void receiveMessage(Object receiverName, OpFeedbackParam<Object> sender,
			OpFeedbackParam<Object> message) {
		try {
			execLinkedOp("env-link", "receiveMessage", ID, receiverName,
					sender, message);
			// TODO: how to map ArtifactID with Jason?
			signal("message", receiverName, sender, message);
		} catch (Exception e) {
			e.printStackTrace();
			failed("receiveMessage linked operation failed", "fail", ID, e);
		}
	}

	@OPERATION
	void insertData() {

	}

	@OPERATION
	void removeData() {

	}

	@OPERATION
	void getRemainingSpace() {

	}

	@OPERATION
	void getMaximumSize() {

	}

	@OPERATION
	void obtainPosition() {
		OpFeedbackParam<double[]> position = new OpFeedbackParam<>();
		try {
			execLinkedOp("env-link", "obtainPosition", ID, position);
			ObsProperty psProp = getObsProperty("position");
			psProp.updateValue(position.get());
		} catch (Exception e) {
			e.printStackTrace();
			failed("obtainPosition linked operation failed", "fail", ID, e);
		}
	}

}