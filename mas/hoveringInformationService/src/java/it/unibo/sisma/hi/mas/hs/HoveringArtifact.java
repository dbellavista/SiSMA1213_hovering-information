// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import cartago.*;

public class HoveringArtifact extends Artifact {
	
	private byte[] data;
	private Object ID;

	void init(Object ID, int dataSize) {
		data = new byte[dataSize];
		this.ID = ID;
		byte[] name = ID.toString().getBytes();
		int i = 0;
		for(; i < dataSize && i < name.length; i++) {
			data[i] = name[i];			
		}
		for(; i < dataSize; i++) {
			data[i] = 'D';			
		}
	}
	
	@OPERATION
	void inc() {
		
	}
}

