// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import cartago.*;

@ARTIFACT_INFO(
		outports = {
				@OUTPORT(name = "to-ui")
				}
		)
public class MobileUIInterfaceArtifact extends Artifact {
	
	void init() {
		
	}

	@LINK void createInformation() {
		
	}
	
	@OPERATION void showInformation() {
		try {
			execLinkedOp("to-ui", "showInformation");

		} catch (Exception e) {
			e.printStackTrace();
			failed("ShowInformation linked operation failed", "fail", e);
		}
	}
}
