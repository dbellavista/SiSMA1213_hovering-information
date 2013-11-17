// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import cartago.*;

public class HoveringArtifact extends Artifact {

	private double[] curr_position = null;
	private byte[] data;
	@SuppressWarnings("unused")
	private Object ID;
	private long lastTime;

	void init(Object ID, int dataSize) {
		data = new byte[dataSize];
		this.ID = ID;
		byte[] name = ID.toString().getBytes();
		int i = 0;
		for (; i < dataSize && i < name.length; i++) {
			data[i] = name[i];
		}
		for (; i < dataSize; i++) {
			data[i] = 'D';
		}
		defineObsProperty("distance", 0.0);
		defineObsProperty("speed", 0.0);
		defineObsProperty("motion_direction",
				(Object) new Object[] { 0.0, 0.0 });
		defineObsProperty("anchor_vector", (Object) new Object[] { 0.0, 0.0 });
	}

	@OPERATION
	void exp(OpFeedbackParam<Double> ret, double ex) {
		ret.set(Math.exp(ex));
	}

	@OPERATION
	void distance(OpFeedbackParam<Double> ret, double x1, double y1, double x2,
			double y2) {
		ret.set(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
	}

	@OPERATION
	void computePositionalData(double x, double y, double anchorX,
			double anchorY, OpFeedbackParam<Double> speedRet,
			OpFeedbackParam<Double> distanceRet,
			OpFeedbackParam<double[]> motionDirectionRet,
			OpFeedbackParam<double[]> anchorVectorRet) {

		double speed;
		double[] direction = new double[2];
		double[] anchorVector = new double[2];
		double distance = Math.sqrt((x - anchorX) * (x - anchorX)
				+ (y - anchorY) * (y - anchorY));

		long curTime = System.currentTimeMillis();

		if (curr_position == null) {
			speed = 0;
			curr_position = new double[2];
		} else {
			direction[0] = x - curr_position[0];
			direction[1] = y - curr_position[1];
			double dirNorm = Math.sqrt(direction[0] * direction[0]
					+ direction[1] * direction[1]);
			direction[0] /= dirNorm;
			direction[1] /= dirNorm;

			anchorVector[0] = x - anchorX;
			anchorVector[1] = y - anchorY;
			double anchVecNorm = Math.sqrt(anchorVector[0] * anchorVector[0]
					+ anchorVector[1] * anchorVector[1]);
			anchorVector[0] /= anchVecNorm;
			direction[1] /= anchVecNorm;

			speed = dirNorm / (lastTime - curTime);

		}
		speedRet.set(speed);
		distanceRet.set(distance);
		motionDirectionRet.set(direction);
		anchorVectorRet.set(anchorVector);

		curr_position[0] = x;
		curr_position[1] = y;
		lastTime = curTime;
	}
}
