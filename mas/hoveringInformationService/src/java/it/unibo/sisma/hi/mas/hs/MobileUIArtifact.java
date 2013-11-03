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
	
	@OPERATION void startDevice() {
		try {
			execLinkedOp("to-device", "startDevice");

		} catch (Exception e) {
			e.printStackTrace();
			failed("startDevice linked operation failed", "fail", e.getMessage());
		}
	}
	
	@OPERATION void stopDevice() {
		try {
			execLinkedOp("to-device", "stopDevice");

		} catch (Exception e) {
			e.printStackTrace();
			failed("stopDevice linked operation failed", "fail", e.getMessage());
		}
	}
}

