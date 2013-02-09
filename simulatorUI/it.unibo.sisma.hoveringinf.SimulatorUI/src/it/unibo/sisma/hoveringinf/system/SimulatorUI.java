package it.unibo.sisma.hoveringinf.system;

import javax.swing.JFrame;

import it.unibo.sisma.hoveringinf.entities.World;

/**
 * The SimulatorUI system.
 * 
 * @author Daniele Bellavista
 * 
 */
public class SimulatorUI {

	private JFrame simulatorFrame;

	public void init(int sizeX, int sizeY) {
		simulatorFrame = new JFrame("Hovering Information simulation");
		simulatorFrame.setSize(sizeX, sizeY);
		
	}

	public void config() {

	}

	public void start() {
		simulatorFrame.setVisible(true);

	}

	public void update(World e) {

	}

	public void stop() {
		simulatorFrame.setVisible(false);
	}
}
