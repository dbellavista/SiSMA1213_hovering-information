// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cartago.ARTIFACT_INFO;
import cartago.Artifact;
import cartago.OPERATION;
import cartago.OUTPORT;
import cartago.ObsProperty;
import cartago.OpFeedbackParam;

@ARTIFACT_INFO(outports = { @OUTPORT(name = "env-link") })
public class MobileResourceArtifact extends Artifact {

	private double range;
	private MobileStorage storage;
	private Object ID;
	Collection<Object> neighbors;

	void init(Object ID, Number range, Number storage) {

		this.ID = ID;
		this.range = range.doubleValue();
		neighbors = new ArrayList<>();
		this.storage = new MobileStorage(storage.doubleValue());
		defineObsProperty("neighbors",
				(Object) neighbors.toArray(new Object[neighbors.size()]));
		defineObsProperty("position", (Object) new Object[0]);

		defineObsProperty("total_space", this.storage.getTotalSpace());
		defineObsProperty("free_space", this.storage.getFreeSpace());
		defineObsProperty("data", (Object) new Object[0]);
	}

	@OPERATION
	void discoverNeighbors(OpFeedbackParam<Object[]> mobileIDs) {
		try {
			OpFeedbackParam<HashMap<Object, Object>> mobHash = new OpFeedbackParam<>();
			execLinkedOp("env-link", "discoverNeighbors", ID, range, mobHash);

			HashMap<Object, Object> newNeigh = mobHash.get();
			mobileIDs.set(newNeigh.keySet()
					.toArray(new Object[newNeigh.size()]));

			Iterator<Object> oit = neighbors.iterator();
			boolean changes = false;
			while (oit.hasNext()) {
				Object old = oit.next();
				if (newNeigh.containsKey(old)) {
					newNeigh.remove(old);
				} else {
					signal("neighbor_gone", old);
					oit.remove();
					changes = true;
				}
			}
			changes = changes || newNeigh.size() > 0;
			for (Object key : newNeigh.keySet()) {
				signal("new_neighbor", key);
				neighbors.add(key);
			}
			if (changes) {
				getObsProperty("neighbors")
						.updateValue(
								(Object) neighbors.toArray(new Object[neighbors
										.size()]));
			}

		} catch (Exception e) {
			e.printStackTrace();
			failed("DiscoverNeighbors linked operation failed", "fail", ID, e);
		}
	}

	@OPERATION
	void sendMessage(Object senderName, Object receiverID, Object receiverName,
			Object message, OpFeedbackParam<Boolean> Res) {
		try {
			execLinkedOp("env-link", "sendMessage", ID, senderName, range,
					receiverID, receiverName, message);
			Res.set(true);
		} catch (Exception e) {
			// e.printStackTrace();
			// failed("sendMessage linked operation failed", "fail", ID, e);
			Res.set(false);
		}
	}

	@OPERATION
	void receiveMessage(Object receiverName, OpFeedbackParam<Boolean> res,
			OpFeedbackParam<Object> sender, OpFeedbackParam<Object> senderName,
			OpFeedbackParam<Object> message) {

		try {
			execLinkedOp("env-link", "receiveMessage", ID, receiverName,
					sender, senderName, message);
			if (message.get() != null) {
				res.set(true);
				// TODO: how to map AgentID with Jason?
				// signal("message", receiverName, sender.get(), message.get());
			} else {
				res.set(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			failed("receiveMessage linked operation failed", "fail", ID, e);
		}
	}

	@OPERATION
	void allocateData(Object ID, double size, OpFeedbackParam<Boolean> res) {
		res.set(storage.allocateData(ID, size));
		ObsProperty fsProp = getObsProperty("free_space");
		fsProp.updateValue(this.storage.getFreeSpace());
		ObsProperty dataProp = getObsProperty("data");
		dataProp.updateValue(this.storage.getAllData());
	}

	@OPERATION
	void editData(Object ID, String data) {
		storage.editData(ID, data);
		ObsProperty dataProp = getObsProperty("data");
		dataProp.updateValue(this.storage.getAllData());
	}

	@OPERATION
	void removeData(Object ID) {
		storage.freeData(ID);
		ObsProperty fsProp = getObsProperty("free_space");
		fsProp.updateValue(this.storage.getFreeSpace());
		ObsProperty dataProp = getObsProperty("data");
		dataProp.updateValue(this.storage.getAllData());
	}

	@OPERATION
	void getData(Object ID, OpFeedbackParam<String> data,
			OpFeedbackParam<Boolean> res) {
		String datastr = storage.getData(ID);
		data.set(datastr);
		res.set(datastr != null);
	}

	@OPERATION
	void obtainPosition(OpFeedbackParam<Double> x, OpFeedbackParam<Double> y) {
		try {
			OpFeedbackParam<double[]> position = new OpFeedbackParam<>();
			execLinkedOp("env-link", "obtainPosition", ID, position);
			x.set(position.get()[0]);
			y.set(position.get()[1]);
			ObsProperty psProp = getObsProperty("position");
			psProp.updateValue(position.get());
		} catch (Exception e) {
			e.printStackTrace();
			failed("obtainPosition linked operation failed", "fail", ID, e);
		}
	}

}