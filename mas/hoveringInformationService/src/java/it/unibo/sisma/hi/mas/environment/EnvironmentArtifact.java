// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.LINK;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

public class EnvironmentArtifact extends Artifact {

	private Map<RecentCommunication, RecentCommunication> commMap;

	private ConcurrentHashMap<Object, ReentrantReadWriteLock> locks;
	private ConcurrentHashMap<Object, double[]> positions;
	private ConcurrentHashMap<Object, String> deviceNames;
	private ConcurrentHashMap<Object, double[]> points;
	private ConcurrentHashMap<Object, MessageQueue> messages;
	private double worldWidth;
	private double worldHeight;

	void init(double worldWidth, double worldHeight) {
		commMap = new HashMap<>();

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		locks = new ConcurrentHashMap<>();
		positions = new ConcurrentHashMap<>();
		points = new ConcurrentHashMap<>();
		messages = new ConcurrentHashMap<>();
		deviceNames = new ConcurrentHashMap<>();
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
		ReentrantReadWriteLock s = new ReentrantReadWriteLock();
		WriteLock w = s.writeLock();
		w.lock();
		locks.put(ID, s);
		positions.put(ID, new double[] { posx, posy });
		messages.put(ID, new MessageQueue());
		deviceNames.put(ID, deviceName);
		w.unlock();
	}

	@OPERATION
	void exit(Object ID) {
		WriteLock s = writeLock(ID);
		if (s == null)
			return;
		s.lock();
		locks.remove(ID);
		deviceNames.remove(ID);
		positions.remove(ID);
		messages.remove(ID);
		s.unlock();
	}

	/*
	 * PEOPLE METHODS
	 */

	@LINK
	void move(Object ID, double rel_x2, double rel_y2, double speed) {

		WriteLock s = writeLock(ID);
		if (s == null)
			return;
		s.lock();
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
		s.unlock();
	}

	@LINK
	void sense(Object ID, OpFeedbackParam<PersonSenseData[]> people,
			OpFeedbackParam<PoTSenseData[]> pointOfInterest) {
		ReadLock s = readLock(ID);
		if (s == null)
			return;
		s.lock();
		double[] mypos = positions.get(ID);
		Collection<PersonSenseData> pl = new ArrayList<>();
		Collection<PoTSenseData> potl = new ArrayList<>();
		for (Entry<Object, ReentrantReadWriteLock> l : locks.entrySet()) {
			if (l.getKey().equals(ID)) {
				continue;
			}
			ReadLock r = l.getValue().readLock();
			r.lock();
			double[] pos = positions.get(l.getKey());
			pl.add(new PersonSenseData(new double[] { pos[0] - mypos[0],
					pos[1] - mypos[1] }, l.getKey()));
			r.unlock();
		}
		for (Entry<Object, double[]> l : points.entrySet()) {
			double[] pos = points.get(l.getKey());
			potl.add(new PoTSenseData(new double[] { pos[0] - mypos[0],
					pos[1] - mypos[1] }, l.getKey()));
		}
		s.unlock();
		people.set(pl.toArray(new PersonSenseData[pl.size()]));
		pointOfInterest.set(potl.toArray(new PoTSenseData[potl.size()]));
	}

	/*
	 * MOBILE RESOURCE METHODS
	 */
	@LINK
	void discoverNeighbour(Object ID, Number commRange,
			OpFeedbackParam<Object[]> mobileIDs) {
		ReadLock mr = readLock(ID);
		mr.lock();
		double[] mypos = positions.get(ID);
		Collection<Object> ids = new ArrayList<>();
		for (Entry<Object, ReentrantReadWriteLock> l : locks.entrySet()) {
			if (l.getKey().equals(ID)) {
				continue;
			}
			ReadLock r = l.getValue().readLock();
			r.lock();
			double[] pos = positions.get(l.getKey());
			if (inRange(mypos, pos, commRange)) {
				ids.add(l.getKey());
			}
			r.unlock();
		}
		mr.unlock();
		mobileIDs.set(ids.toArray(new Object[ids.size()]));
	}

	@OPERATION
	void backdoorSendMessage(Object receiverID, Object receiverName,
			Object message) {
		ReadLock r = readLock(receiverID);
		if (r == null) {
			throw new RuntimeException("Receiver not found!");
		}
		r.lock();
		messages.get(receiverID).insertMessage(
				new Message(message, receiverName, "__BACKDOOR__",
						"__Initiator__"));
		r.unlock();
	}

	@LINK
	void sendMessage(Object senderID, Object senderName, Number commRange,
			Object receiverID, Object receiverName, Object message) {
		double[] mypos;
		ReadLock mr = null;
		ReadLock r = null;
		try {
			try {
				mr = readLock(senderID);
				mr.lock();
				mypos = positions.get(senderID);
			} catch (Exception e) {
				failed("Sender not found!");
				return;
			}
			if (mypos == null) {
				failed("Sender not found!");
				return;
			}
			// TODO: add list recently messaged
			r = readLock(receiverID);
			if (r == null) {
				failed("Receiver not found!");
				return;
			}
			r.lock();
			double[] destPos = positions.get(receiverID);
			if (inRange(mypos, destPos, commRange)) {
				messages.get(receiverID)
						.insertMessage(
								new Message(message, receiverName, senderID,
										senderName));
				addNewRecentCommunication(senderID, receiverID);
			} else {
				failed("Receiver not in range!");
				return;
			}
		} finally {
			if (r != null) {
				r.unlock();
			}
			if (mr != null) {
				mr.unlock();
			}
		}
	}

	@INTERNAL_OPERATION
	synchronized void addNewRecentCommunication(Object senderID,
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

	@INTERNAL_OPERATION
	synchronized Object[][] generateRecentCommList() {
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
		ReadLock r = readLock(ID);
		r.lock();
		MessageQueue queue = messages.get(ID);
		r.unlock();

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
		ReadLock mr = readLock(ID);
		mr.lock();
		position.set(positions.get(ID));
		mr.unlock();
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
		for (Entry<Object, ReentrantReadWriteLock> l : locks.entrySet()) {
			ReadLock r = l.getValue().readLock();
			r.lock();
			double[] pos = positions.get(l.getKey());
			String devName = deviceNames.get(l.getKey());
			pl.add(new Object[] { l.getKey(), devName, pos });
			r.unlock();
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

	@INTERNAL_OPERATION
	WriteLock writeLock(Object ID) {
		ReentrantReadWriteLock s = locks.get(ID);
		if (s == null) {
			failed("Operation failure", "fail", ID, "Null lock");
			return null;
		}
		return s.writeLock();
	}

	@INTERNAL_OPERATION
	ReadLock readLock(Object ID) {
		ReentrantReadWriteLock s = locks.get(ID);
		if (s == null) {
			failed("Operation failure", "fail", ID, "Null lock");
			return null;
		}
		return s.readLock();
	}

	@INTERNAL_OPERATION
	boolean inRange(double[] pos1, double[] pos2, Number range) {
		return (sq(pos1[0] - pos2[0]) + sq(pos1[1] - pos2[1])) <= sq(range
				.doubleValue());
	}

	@INTERNAL_OPERATION
	double sq(double p) {
		return p * p;
	}

}