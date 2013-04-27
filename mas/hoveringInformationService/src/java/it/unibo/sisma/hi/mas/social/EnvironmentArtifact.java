// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.social;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import cartago.*;

public class EnvironmentArtifact extends Artifact {

	private ConcurrentHashMap<Object, ReentrantReadWriteLock> locks;
	private ConcurrentHashMap<Object, double[]> positions;
	private ConcurrentHashMap<Object, double[]> points;
	private double worldWidth;
	private double worldHeight;

	void init(double worldWidth, double worldHeight) {
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		locks = new ConcurrentHashMap<>();
		positions = new ConcurrentHashMap<>();
		points = new ConcurrentHashMap<>();
	}

	@OPERATION void createPointInterest(Object ID, double posx, double posy) {
		points.put(ID, new double[] { posx, posy });
	}

	@OPERATION void removePointInsterest(Object ID, double posx, double posy) {
		points.remove(ID);
	}

	@OPERATION void enter(Object ID, double posx, double posy) {
		ReentrantReadWriteLock s = new ReentrantReadWriteLock();
		WriteLock w = s.writeLock();
		locks.put(ID, s);
		positions.put(ID, new double[] { posx, posy });
		w.unlock();
	}

	@OPERATION void exit(Object ID) {
		WriteLock s = writeLock(ID);
		if (s == null)
			return;
		locks.remove(ID);
		positions.remove(ID);
		s.unlock();
	}

	@LINK void move(Object ID, double rel_x2, double rel_y2, double speed) {
		WriteLock s = writeLock(ID);
		if (s == null)
			return;
		double[] pos = positions.get(ID);
		double x1 = pos[0], x3, y1 = pos[0], y3;
		double x2 = rel_x2 + x1, y2 = rel_y2 + y1;

		double a = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		if (speed > a) {
			x3 = x2;
			y3 = y2;
		} else {
			x3 = x2 - (a - speed) / a * (x2 - x1);
			y3 = y2 - (a - speed) / a * (y2 - y1);
		}
		if (x3 < 0 || x3 > worldWidth) {
			failed("Movement failure: out of bound!", "fail", ID, "Wanted: "
					+ x3 + " max " + worldWidth);
		} else if (y3 < 0 || y3 > worldHeight) {
			failed("Movement failure: out of bound!", "fail", ID, "Wanted: "
					+ y3 + " max " + worldHeight);
		} else {
			pos[0] = x3;
			pos[1] = y3;
		}
		s.unlock();
	}

	@LINK void sense(Object ID, OpFeedbackParam<Collection<PersonSenseData>> people,
			OpFeedbackParam<Collection<PoTSenseData>> pointOfInterest) {
		ReadLock s = readLock(ID);
		double[] mypos = positions.get(ID);
		if (s == null)
			return;
		Collection<PersonSenseData> pl = new ArrayList<>();
		Collection<PoTSenseData> potl = new ArrayList<>();
		people.set(pl);
		pointOfInterest.set(potl);
		for (Entry<Object, ReentrantReadWriteLock> l : locks.entrySet()) {
			if (l.getKey().equals(ID)) {
				continue;
			}
			ReadLock r = readLock(l.getValue());
			if (r == null) {
				continue;
			}
			double[] pos = positions.get(ID);
			pl.add(new PersonSenseData(new double[] { pos[0] - mypos[0],
					pos[1] - mypos[1] }, l.getKey()));
			r.unlock();
		}
		for (Entry<Object, double[]> l : points.entrySet()) {
			double[] pos = points.get(ID);
			potl.add(new PoTSenseData(new double[] { pos[0] - mypos[0],
					pos[1] - mypos[1] }, l.getKey()));
		}
		s.unlock();
	}

	@INTERNAL_OPERATION WriteLock writeLock(Object ID) {
		ReentrantReadWriteLock s = locks.get(ID);
		if (s == null) {
			failed("Operation failure", "fail", ID, "Null lock");
			return null;
		}
		WriteLock r = s.writeLock();
		ReentrantReadWriteLock s2 = locks.get(ID);
		if (s2.equals(s)) {
			return r;
		} else {
			failed("Operation failure", "fail", ID, "Operation not valid");
			r.unlock();
			return null;
		}
	}

	@INTERNAL_OPERATION ReadLock readLock(Object ID) {
		ReentrantReadWriteLock s = locks.get(ID);
		if (s == null) {
			failed("Operation failure", "fail", ID, "Null lock");
			return null;
		}
		ReadLock r = s.readLock();
		ReentrantReadWriteLock s2 = locks.get(ID);
		if (s2.equals(s)) {
			return r;
		} else {
			failed("Operation failure", "fail", ID, "Operation not valid");
			r.unlock();
			return null;
		}
	}

}