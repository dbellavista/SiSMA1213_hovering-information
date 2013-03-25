package test;

import cartago.*;

public class Counter extends Artifact {

	void init() {
		defineObsProperty("count",
				(Object) new double[][] { { 1, 2 }, { 2, 3 } });
	}

	@OPERATION
	void inc() {
		ObsProperty prop = getObsProperty("count");
		prop.updateValue(new double[][] { { 1, 2 }, { 2, 3 } });
	}
}