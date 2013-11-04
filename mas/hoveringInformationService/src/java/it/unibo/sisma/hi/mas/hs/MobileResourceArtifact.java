// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import java.util.ArrayList;
import java.util.Arrays;
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

	void init(Object ID, Number range, Number storage) {
		this.ID = ID;
		this.range = range.doubleValue();
		this.storage = new MobileStorage(storage.doubleValue());
		defineObsProperty("neighbours", (Object) new Object[0]);
		defineObsProperty("position", (Object) new Object[0]);
		
		defineObsProperty("total_space", this.storage.getTotalSpace());
		defineObsProperty("free_space", this.storage.getFreeSpace());
		defineObsProperty("data", (Object) new Object[0]);
	}

	@OPERATION
	void discoverNeighbour(OpFeedbackParam<Object[]> mobileIDs) {
		try {
			execLinkedOp("env-link", "discoverNeighbour", ID, range, mobileIDs);
			ObsProperty neighbours = getObsProperty("neighbours");
			List<Object> oldNeigh = Arrays.asList((Object[]) neighbours.getValue());
			List<Object> newNeigh = new ArrayList<>(Arrays.asList(mobileIDs.get()));

			Iterator<Object> oit = oldNeigh.iterator();
			while (oit.hasNext()) {
				Object old = oit.next();
				Iterator<Object> nit = newNeigh.iterator();
				boolean found = false;
				while (!found && nit.hasNext()) {
					Object newn = nit.next();
					if (old.equals(newn)) {
						found = true;
						nit.remove();
					}
				}
				if (!found) {
					signal("neighbour_gone", old);
				}
			}
			Iterator<Object> nit = newNeigh.iterator();
			while (nit.hasNext()) {
				Object newn = nit.next();
				signal("new_neighbour", newn);
			}

			neighbours.updateValue(mobileIDs.get());

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
			OpFeedbackParam<Object> senderName, OpFeedbackParam<Object> message) {
		try {
			execLinkedOp("env-link", "receiveMessage", ID, receiverName,
					sender, message);
			if(message.get() != null) {
				// TODO: how to map AgentID with Jason?
				signal("message", receiverName, sender.get(), message.get());
			}
		} catch (Exception e) {
			e.printStackTrace();
			failed("receiveMessage linked operation failed", "fail", ID, e);
		}
	}

	@OPERATION
	void allocateData(Object ID, double size, OpFeedbackParam<Boolean> res) {
		res.set(storage.allocateData(ID, size));
	}

	@OPERATION
	void editData(Object ID, String data) {
		storage.editData(ID, data);
	}
	
	@OPERATION
	void removeData(Object ID) {
		storage.freeData(ID);
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