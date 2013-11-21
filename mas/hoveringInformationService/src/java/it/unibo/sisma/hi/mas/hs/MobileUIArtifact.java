// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import cartago.*;

@ARTIFACT_INFO(outports = { @OUTPORT(name = "to-device") })
public class MobileUIArtifact extends Artifact {

	void init() {

	}

	@LINK
	void showInformation(Object hoverName, Object data) {
		signal("information", hoverName, data);
	}

	@OPERATION
	void startDevice() {
		try {
			execLinkedOp("to-device", "startDevice");

		} catch (Exception e) {
			e.printStackTrace();
			failed("startDevice linked operation failed", "fail",
					e.getMessage());
		}
	}

	@OPERATION
	void stopDevice() {
		try {
			execLinkedOp("to-device", "stopDevice");

		} catch (Exception e) {
			e.printStackTrace();
			failed("stopDevice linked operation failed", "fail", e.getMessage());
		}
	}
}
