package it.unibo.sisma.hoveringinf.system;

import it.unibo.sisma.hoveringinf.entities.World;
import it.unibo.sisma.hoveringinf.graphic.SimulationFrame;
import it.unibo.sisma.hoveringinf.graphic.WindowCloseListener;

/**
 * The SimulatorUI system.
 * 
 * @author Daniele Bellavista
 * 
 */
public class SimulatorUI {

	private SimulationFrame simulatorFrame;

	/**
	 * Initialize the simulator.
	 * 
	 * @param width
	 * @param height
	 */
	public void init(int width, int height) {
		simulatorFrame = new SimulationFrame("Hovering Information simulation",
				width, height);
	}

	/**
	 * COnfigure the simulator, attaching a {@link WindowCloseListener}.
	 * 
	 * @param windowCloseListener
	 */
	public void config(WindowCloseListener windowCloseListener) {
		simulatorFrame.addWindowCloseListener(windowCloseListener);
	}

	/**
	 * Start the UI, showing the render window
	 */
	public void start() {
		simulatorFrame.setVisible(true);

	}

	/**
	 * Render the given {@link World}
	 * 
	 * @param e
	 *            : the world to render. If null is passed, the UI will render
	 *            nothing.
	 */
	public void render(World e) {
		simulatorFrame.render(e);
	}

	/**
	 * Stop the UI, hiding the render window.
	 */
	public void stop() {
		simulatorFrame.setVisible(false);
	}
}
