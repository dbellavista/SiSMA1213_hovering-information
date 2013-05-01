// CArtAgO artifact code for project hoveringInformationService

package it.unibo.sisma.hi.mas.sim;

import it.unibo.sisma.hoveringinf.entities.World;
import it.unibo.sisma.hoveringinf.graphic.WindowCloseListener;
import it.unibo.sisma.hoveringinf.system.SimulatorUI;
import cartago.Artifact;
import cartago.LINK;

public class UIArtifact extends Artifact {

	private SimulatorUI ui;

	void init(int guiWidth, int guiHeight) {
		ui = new SimulatorUI();
		ui.init(guiWidth, guiHeight);
		ui.config(new WindowCloseListener() {
			@Override
			public void onClose() {
				System.exit(0);
			}
		});
		ui.start();
	}

	@LINK
	void render(World world) {
		ui.render(world);
	}
}
