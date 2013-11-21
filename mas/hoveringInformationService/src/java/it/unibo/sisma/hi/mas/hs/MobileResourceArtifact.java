// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import it.unibo.sisma.hi.mas.environment.SleepCmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

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
	private Collection<Object> neighbors;
	private SleepCmd sleepCmd;
	private boolean obtainingPos;

	void init(Object ID, Number range, Number storage) {

		this.ID = ID;
		this.range = range.doubleValue();
		neighbors = new ArrayList<>();
		this.storage = new MobileStorage(storage.doubleValue());
		sleepCmd = new SleepCmd(500);

		defineObsProperty("neighbors",
				(Object) neighbors.toArray(new Object[neighbors.size()]));
		defineObsProperty("position", (Object) new double[2]);

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
	void sendMessage(Object senderName, Object receiverID, Object receiverName,
			Object message) {
		try {
			execLinkedOp("env-link", "sendMessage", ID, senderName, range,
					receiverID, receiverName, message);
		} catch (Exception e) {
			// e.printStackTrace();
			// failed("sendMessage linked operation failed", "fail", ID, e);
		}
	}


	@OPERATION
	void imIn(Number x, Number y, Number ax, Number ay, Number area,
			OpFeedbackParam<Boolean> Res) {
		double dx = x.doubleValue();
		double dy = y.doubleValue();
		double dax = ax.doubleValue();
		double day = ay.doubleValue();
		double darea = area.doubleValue();
		Res.set(Math.sqrt((dx - dax) * (dx - dax) + (dy - day) * (dy - day)) < darea);
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
	void stopObtainingPosition() {
		obtainingPos = false;
	}

	@OPERATION
	void startObtainingPosition() {
		obtainingPos = true;

		OpFeedbackParam<double[]> position = new OpFeedbackParam<>();
		while (obtainingPos) {
			await(sleepCmd);
			try {
				ObsProperty psProp = getObsProperty("position");
				obtainPositionIn(position);
				double[] newPos = position.get();
				double[] pos = (double[]) psProp.getValue();
				if (pos[0] != newPos[0] || pos[1] != newPos[1]) {
					pos[0] = newPos[0];
					pos[1] = newPos[1];
					psProp.updateValue(pos);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@OPERATION
	void obtainPosition(OpFeedbackParam<Double> x, OpFeedbackParam<Double> y) {
		if (!obtainingPos) {
			try {
				OpFeedbackParam<double[]> position = new OpFeedbackParam<>();
				obtainPositionIn(position);
				if (x != null && y != null) {
					x.set(position.get()[0]);
					y.set(position.get()[1]);
				}
				ObsProperty psProp = getObsProperty("position");
				psProp.updateValue(position.get());
			} catch (Exception e) {
				e.printStackTrace();
				failed("ObtainPosition operation failed", "fail", ID,
						e.getMessage());
			}
		} else {
			ObsProperty psProp = getObsProperty("position");
			double[] tmp = (double[]) psProp.getValue();
			x.set(tmp[0]);
			y.set(tmp[1]);
		}
	}

	private void obtainPositionIn(OpFeedbackParam<double[]> position) {
		try {
			execLinkedOp("env-link", "obtainPosition", ID, position);
		} catch (Exception e) {
			e.printStackTrace();
			failed("Internal: obtainPosition linked operation failed", "fail",
					ID, e);
		}
	}
}