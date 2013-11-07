package it.unibo.sisma.hoveringinf.test;

import it.unibo.sisma.hoveringinf.entities.HoveringInformation;
import it.unibo.sisma.hoveringinf.entities.MobileNode;
import it.unibo.sisma.hoveringinf.entities.PieceOfHoveringInformation;
import it.unibo.sisma.hoveringinf.entities.World;
import it.unibo.sisma.hoveringinf.graphic.WindowCloseListener;
import it.unibo.sisma.hoveringinf.system.SimulatorUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSimulation {

	private final static int width = 1200;
	private final static int height = 800;

	private List<PieceOfHoveringInformation> pieces;
	private List<MobileNode> mobilenodes;
	private List<HoveringInformation> hoveringInformations;

	private World world;
	private SimulatorUI simulator;
	private Semaphore closed;

	private void clear() {
		pieces = new ArrayList<>();
		mobilenodes = new ArrayList<>();
		hoveringInformations = new ArrayList<>();
		world = new World(hoveringInformations, mobilenodes, pieces, width,
				height);
	}

	@Before
	public void setUp() throws Exception {

		closed = new Semaphore(0);

		simulator = new SimulatorUI();
		simulator.init(width, height);
		simulator.config(new WindowCloseListener() {

			@Override
			public void onClose() {
				closed.release();
			}
		});
	}

	@After
	public void tearDown() throws Exception {
		simulator.stop();
	}

	@Test
	public void testSimulation() throws Exception {
		simulator.start();

		while (!closed.tryAcquire()) {
			clear();
			Random r = new Random();

			for (int i = 0; i < 10; i++) {
				HoveringInformation h1 = new HoveringInformation(
						r.nextInt(width - 100) + 10,
						r.nextInt(height - 100) + 10, r.nextInt(Math.min(width,
								height) / 4) + 10);
				hoveringInformations.add(h1);
			}

			MobileNode mn = new MobileNode(100, 80, 12, r.nextInt(width),
					r.nextInt(height), Collections.<MobileNode> emptyList(),
					Collections.<PieceOfHoveringInformation> emptyList());
			mobilenodes.add(mn);
			for (int i = 0; i < 40; i++) {

				int j;
				List<PieceOfHoveringInformation> tmpl = new ArrayList<>();
				for (j = 0; j < r.nextInt(10); j++) {
					tmpl.add(new PieceOfHoveringInformation(
							hoveringInformations.get(r
									.nextInt(hoveringInformations.size())), 10));
				}
				pieces.addAll(tmpl);

				int s = r.nextInt(mobilenodes.size());
				mn = new MobileNode(100, (j + 1) * 10, 12, r.nextInt(width),
						r.nextInt(height), new ArrayList<>(mobilenodes.subList(
								s, r.nextInt(mobilenodes.size() - s) + s)),
						tmpl);
				mobilenodes.add(mn);
			}
			simulator.render(world);

			Thread.sleep(1000);
		}

		assert (true);
	}

}
