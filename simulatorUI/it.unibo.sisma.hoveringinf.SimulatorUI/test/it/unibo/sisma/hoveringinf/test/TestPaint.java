package it.unibo.sisma.hoveringinf.test;

import it.unibo.sisma.hoveringinf.entities.HoveringInformation;
import it.unibo.sisma.hoveringinf.entities.MobileNode;
import it.unibo.sisma.hoveringinf.entities.PieceOfHoveringInformation;
import it.unibo.sisma.hoveringinf.entities.World;
import it.unibo.sisma.hoveringinf.graphic.SimulationFrame;
import it.unibo.sisma.hoveringinf.graphic.WindowCloseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestPaint {

	private final static int width = 800;
	private final static int height = 800;

	private List<PieceOfHoveringInformation> pieces;
	private List<MobileNode> mobilenodes;
	private List<HoveringInformation> hoveringInformations;

	private World world;
	private SimulationFrame testFrame;

	private Semaphore closed;

	@Before
	public void setUp() throws Exception {
		pieces = new ArrayList<>();
		mobilenodes = new ArrayList<>();
		hoveringInformations = new ArrayList<>();
		world = new World(hoveringInformations, mobilenodes, pieces, width,
				height);

		testFrame = new SimulationFrame("Testing...", width, height);
		closed = new Semaphore(0);
		testFrame.addWindowCloseListener(new WindowCloseListener() {

			@Override
			public void onClose() {
				closed.release();
			}
		});
	}

	@After
	public void tearDown() throws Exception {
		testFrame.setVisible(false);
	}

	@Test
	public void testBounds() throws Exception {
		HoveringInformation hi = new HoveringInformation(0, 0, 100, 0);
		hoveringInformations.add(hi);
		hi = new HoveringInformation(width, 0, 100, 0);
		hoveringInformations.add(hi);
		hi = new HoveringInformation(0, height, 100, 0);
		hoveringInformations.add(hi);
		hi = new HoveringInformation(width, height, 100, 0);
		hoveringInformations.add(hi);
		hi = new HoveringInformation(width/2, height/2, width, 0);
		hoveringInformations.add(hi);
		hi = new HoveringInformation(width/2, height/2, height, 0);
		hoveringInformations.add(hi);

		testFrame.setVisible(true);
		testFrame.render(world);

		closed.acquire();

		assert (true);
	}

	@Test
	@Ignore
	public void testHoveringInformation() throws Exception {

		Random r = new Random();

		for (int i = 0; i < 40; i++) {
			HoveringInformation h1 = new HoveringInformation(
					r.nextInt(width - 100) + 10, r.nextInt(height - 100) + 10,
					r.nextInt(Math.min(width, height) - 50) + 10, 10);
			hoveringInformations.add(h1);
		}

		testFrame.setVisible(true);
		testFrame.render(world);

		closed.acquire();

		assert (true);
	}

	@Test
	@Ignore
	public void testMobileNodes() throws Exception {

		Random r = new Random();

		MobileNode mn = new MobileNode(100, 80, 12, r.nextInt(width),
				r.nextInt(height), Collections.<MobileNode> emptyList(),
				Collections.<PieceOfHoveringInformation> emptyList());
		mobilenodes.add(mn);
		for (int i = 0; i < 40; i++) {
			int s = r.nextInt(mobilenodes.size());
			mn = new MobileNode(100, 80, 12, r.nextInt(width),
					r.nextInt(height), new ArrayList<>(mobilenodes.subList(s,
							r.nextInt(mobilenodes.size() - s) + s)),
					Collections.<PieceOfHoveringInformation> emptyList());
			mobilenodes.add(mn);
		}

		testFrame.setVisible(true);
		testFrame.render(world);

		closed.acquire();

		assert (true);
	}

	@Test
	@Ignore
	public void testMobileHovering() throws Exception {

		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			HoveringInformation h1 = new HoveringInformation(
					r.nextInt(width - 100) + 10, r.nextInt(height - 100) + 10,
					r.nextInt(Math.min(width, height) / 4) + 10, 10);
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
				tmpl.add(new PieceOfHoveringInformation(hoveringInformations
						.get(r.nextInt(hoveringInformations.size()))));
			}
			pieces.addAll(tmpl);

			int s = r.nextInt(mobilenodes.size());
			mn = new MobileNode(100, (j + 1) * 10, 12, r.nextInt(width),
					r.nextInt(height), new ArrayList<>(mobilenodes.subList(s,
							r.nextInt(mobilenodes.size() - s) + s)), tmpl);
			mobilenodes.add(mn);
		}

		testFrame.setVisible(true);
		testFrame.render(world);

		closed.acquire();

		assert (true);
	}

}
