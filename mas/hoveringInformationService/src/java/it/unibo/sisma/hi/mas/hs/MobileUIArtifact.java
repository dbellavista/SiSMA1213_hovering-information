// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import cartago.*;

@ARTIFACT_INFO(
		outports = {
				@OUTPORT(name = "to-device")
				}
		)
public class MobileUIArtifact extends Artifact {
	
	void init() {
		
	}
	
	@OPERATION void createInformation() {
		try {
			execLinkedOp("to-device", "createInformation");

		} catch (Exception e) {
			e.printStackTrace();
			failed("CreateInformation linked operation failed", "fail", e);
		}
	}
	
	@LINK void showInformation() {
		
	}
}

