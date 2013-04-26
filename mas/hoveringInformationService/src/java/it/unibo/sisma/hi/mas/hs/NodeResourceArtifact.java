// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import cartago.*;

public class NodeResourceArtifact extends Artifact {
	
	private double range;
	private double storage;

	void init(Number range, Number storage) {
		this.range = range.doubleValue();
		this.storage = storage.doubleValue();
	}
	
}

