// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.hs;

import cartago.*;

@ARTIFACT_INFO(outports = { @OUTPORT(name = "to-ui") })
public class MobileUIInterfaceArtifact extends Artifact {

	void init() {

	}

	@OPERATION
	void showInformation(Object hoverName, Object hoverData) {
		try {
			execLinkedOp("to-ui", "showInformation", hoverName, hoverData);

		} catch (Exception e) {
			e.printStackTrace();
			failed("ShowInformation linked operation failed", "fail", e);
		}
	}

	@LINK
	void startDevice() {
		signal("start_command");
	}

	@LINK
	void stopDevice() {
		signal("stop_command");
	}
}
