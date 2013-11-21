// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cartago.Artifact;
import cartago.LINK;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

public class EnvironmentArtifact extends Artifact {

	private Map<RecentCommunication, RecentCommunication> commMap;

	private HashMap<Object, double[]> positions;
	private HashMap<Object, String> deviceNames;
	private HashMap<Object, double[]> points;
	private HashMap<Object, MessageQueue> messages;
	private double worldWidth;
	private double worldHeight;

	void init(double worldWidth, double worldHeight) {
		commMap = new HashMap<>();

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		positions = new HashMap<>();
		points = new HashMap<>();
		messages = new HashMap<>();
		deviceNames = new HashMap<>();
	}

	/*
	 * INITIALIZATION METHODS
	 */

	@OPERATION
	void createPointInterest(Object ID, double posx, double posy,
			double comm_range) {
		points.put(ID, new double[] { posx, posy, comm_range });
	}

	@OPERATION
	void removePointInsterest(Object ID) {
		points.remove(ID);
	}

	@OPERATION
	void enter(Object ID, String deviceName, double posx, double posy) {
		positions.put(ID, new double[] { posx, posy });
		messages.put(ID, new MessageQueue());
		deviceNames.put(ID, deviceName);
	}

	@OPERATION
	void exit(Object ID) {
		deviceNames.remove(ID);
		positions.remove(ID);
		messages.remove(ID);
	}

	/*
	 * PEOPLE METHODS
	 */

	@LINK
	void move(Object ID, double rel_x2, double rel_y2, double speed) {

		double[] pos = positions.get(ID);
		double x1 = pos[0], x3, y1 = pos[1], y3;
		double x2 = rel_x2 + x1, y2 = rel_y2 + y1;

		double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		if (speed >= length) {
			x3 = x2;
			y3 = y2;
		} else {
			x3 = x1 + (speed / length) * (x2 - x1);
			y3 = y1 + (speed / length) * (y2 - y1);
		}
		if (x3 < 0 || x3 > worldWidth) {
			x3 = (x3 < 0) ? 0 : worldWidth;
			// System.out.println("OUT OF BOUND X!");
			// failed("Movement failure: out of bound!", "fail", ID, "Wanted: "
			// + x3 + " max " + worldWidth);
		}
		if (y3 < 0 || y3 > worldHeight) {
			y3 = (y3 < 0) ? 0 : worldHeight;
			// System.out.println("OUT OF BOUND Y!");
			// failed("Movement failure: out of bound!", "fail", ID, "Wanted: "
			// + y3 + " max " + worldHeight);
		}
		pos[0] = x3;
		pos[1] = y3;
	}

	@LINK
	void sense(Object ID, OpFeedbackParam<PersonSenseData[]> people,
			OpFeedbackParam<PoTSenseData[]> pointOfInterest) {
		double[] mypos = positions.get(ID);
		Collection<PersonSenseData> pl = new ArrayList<>();
		Collection<PoTSenseData> potl = new ArrayList<>();
		for (Entry<Object, double[]> posEntry : positions.entrySet()) {
			if (posEntry.getKey().equals(ID)) {
				continue;
			}
			double[] pos = posEntry.getValue();
			pl.add(new PersonSenseData(new double[] { pos[0] - mypos[0],
					pos[1] - mypos[1] }, posEntry.getKey()));
		}
		for (Entry<Object, double[]> l : points.entrySet()) {
			double[] pos = points.get(l.getKey());
			potl.add(new PoTSenseData(new double[] { pos[0] - mypos[0],
					pos[1] - mypos[1] }, l.getKey()));
		}
		people.set(pl.toArray(new PersonSenseData[pl.size()]));
		pointOfInterest.set(potl.toArray(new PoTSenseData[potl.size()]));
	}

	/*
	 * MOBILE RESOURCE METHODS
	 */
	@LINK
	void discoverNeighbors(Object ID, Number commRange,
			OpFeedbackParam<HashMap<Object, Object>> mobileIDs) {
		double[] mypos = positions.get(ID);
		HashMap<Object, Object> ids = new HashMap<>();
		for (Entry<Object, double[]> posEntry : positions.entrySet()) {
			if (posEntry.getKey().equals(ID)) {
				continue;
			}
			double[] pos = posEntry.getValue();
			if (inRange(mypos, pos, commRange)) {
				ids.put(posEntry.getKey(), posEntry.getKey());
			}
		}
		mobileIDs.set(ids);
	}

	@OPERATION
	void backdoorSendMessage(Object receiverID, Object receiverName,
			Object message) {
		messages.get(receiverID).insertMessage(
				new Message(message, receiverName, "__BACKDOOR__",
						"__Initiator__"));
	}

	@LINK
	void sendMessage(Object senderID, Object senderName, Number commRange,
			Object receiverID, Object receiverName, Object message) {
		double[] mypos;
		mypos = positions.get(senderID);
		if (mypos == null) {
			failed("Sender not found!");
			return;
		}
		double[] destPos = positions.get(receiverID);
		if (destPos == null) {
			failed("Receiver not found!");
			return;
		}
		if (inRange(mypos, destPos, commRange)) {
			messages.get(receiverID).insertMessage(
					new Message(message, receiverName, senderID, senderName));
			addNewRecentCommunication(senderID, receiverID);
		} else {
			failed("Receiver not in range!");
			return;
		}
	}

	private void addNewRecentCommunication(Object senderID,
			Object receiverID) {
		RecentCommunication newRc = new RecentCommunication(senderID,
				receiverID);
		RecentCommunication oldRc = commMap.remove(newRc);
		if (oldRc != null) {
			oldRc.update();
			newRc = oldRc;
		}
		commMap.put(newRc, newRc);
	}

	private Object[][] generateRecentCommList() {
		ArrayList<Object[]> list = new ArrayList<>();
		ArrayList<RecentCommunication> toRemove = new ArrayList<>();
		for (RecentCommunication rc : commMap.keySet()) {
			if (rc.isExpired()) {
				toRemove.add(rc);
			} else {
				list.add(rc.toObjectArray());
			}
		}
		for (RecentCommunication rc : toRemove) {
			commMap.remove(rc);
		}
		return list.toArray(new Object[list.size()][]);
	}

	@LINK
	void receiveMessage(Object ID, Object receiverName,
			OpFeedbackParam<Object> senderID,
			OpFeedbackParam<Object> senderName, OpFeedbackParam<Object> message) {
		MessageQueue queue = messages.get(ID);
		GetMessageCmd getCmd = new GetMessageCmd(queue, receiverName);
		await(getCmd);
		Message m = getCmd.getFinalMsg();
		// Message m = queue.getMessage(receiverName, false);
		if (m == null) {
			senderID.set(null);
			message.set(null);
		} else {
			senderID.set(m.getSenderID());
			senderName.set(m.getSenderName());
			message.set(m.getMessage());
		}
	}

	@LINK
	void obtainPosition(Object ID, OpFeedbackParam<double[]> position) {
		position.set(positions.get(ID));
	}

	/*
	 * INQUISITION METHODS
	 */
	@LINK
	void inquireEnvironment(OpFeedbackParam<Object[]> people,
			OpFeedbackParam<Object[]> pointOfInterest,
			OpFeedbackParam<Object[][]> listRecentComm,
			OpFeedbackParam<Double> worldWidth,
			OpFeedbackParam<Double> worldHeight) {
		List<Object> pl = new ArrayList<>();
		List<Object> potl = new ArrayList<>();
		listRecentComm.set(generateRecentCommList());
		// listRecentComm.set(new Object[0][2]);
		for (Entry<Object, double[]> posEntry : positions.entrySet()) {
			double[] pos = posEntry.getValue();
			String devName = deviceNames.get(posEntry.getKey());
			pl.add(new Object[] { posEntry.getKey(), devName, pos });
		}
		for (Entry<Object, double[]> l : points.entrySet()) {
			double[] pos = points.get(l.getKey());
			potl.add(new Object[] { l.getKey(), pos });
		}
		worldWidth.set(this.worldWidth);
		worldHeight.set(this.worldHeight);
		people.set(pl.toArray(new Object[pl.size()]));
		pointOfInterest.set(potl.toArray(new Object[potl.size()]));
	}

	private boolean inRange(double[] pos1, double[] pos2, Number range) {
		return (sq(pos1[0] - pos2[0]) + sq(pos1[1] - pos2[1])) <= sq(range
				.doubleValue());
	}

	private double sq(double p) {
		return p * p;
	}

}