package it.unibo.sisma.hoveringinf.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Random;

import it.unibo.sisma.hoveringinf.entities.HoveringInformation;
import it.unibo.sisma.hoveringinf.entities.MobileNode;
import it.unibo.sisma.hoveringinf.entities.PieceOfHoveringInformation;
import it.unibo.sisma.hoveringinf.entities.World;

import javax.swing.JComponent;

/**
 * Graphic wrapper for {@link World}
 * 
 * @author Daniele Bellavista
 * 
 */
public class GraphicWorld extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private World world;

	/**
	 * Dashed stroke
	 */
	private final static float dash1[] = { 1.0f };
	private final static BasicStroke dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

	/**
	 * Person radius
	 */
	private final static double personSize = 16;
	/**
	 * Relative distance between a person and the buffer
	 */
	private final static double bufferPos = 10;
	/**
	 * Buffer width
	 */
	private final static double bufferWidth = 10;
	/**
	 * Buffer Height
	 */
	private final static double bufferHeight = 30;
	/**
	 * Map containing colors associated to {@link HoveringInformation}
	 */
	private HashMap<HoveringInformation, Color> colors;

	public GraphicWorld(World world, Long seed) {
		super();
		this.world = world;
		super.setSize(new Dimension(world.getWidth(), world.getHeight()));

		colors = new HashMap<>();

		Random r;
		if (seed == null) {
			r = new Random();
		} else {
			r = new Random(seed);
		}
		for (HoveringInformation hi : world.getHoveringInformations()) {
			colors.put(
					hi,
					new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 0.5f));
		}
	}

	public GraphicWorld(World world) {
		this(world, null);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.black);
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
		g.setColor(Color.white);
		g.fillRect(0, 0, world.getWidth(), world.getHeight());

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		int i = 0;
		for (HoveringInformation hi : world.getHoveringInformations()) {
			paintHoveringInformation(hi, i++, g2d);
		}
		i = 0;
		for (MobileNode mn : world.getMobileNodes()) {
			paintMobileNodes(mn, i++, g2d);
		}
	}

	/**
	 * Print a mobile nodes: the person, the buffer, p2p connection and hosted
	 * pieces of hovering information.
	 * 
	 * @param mn
	 * @param i
	 * @param g2d
	 */
	private void paintMobileNodes(MobileNode mn, int i, Graphics2D g2d) {
		// Mapping
		double mXpos = mx(mn.getxPos());
		double mYpos = mx(mn.getyPos());

		// Color
		Stroke original = g2d.getStroke();
		// Anchor area
		g2d.setColor(Color.black);
		g2d.draw(new Ellipse2D.Double(mXpos - personSize / 2, mYpos
				- personSize / 2, personSize, personSize));

		double j = 0;
		for (PieceOfHoveringInformation phi : mn.getHosted()) {
			HoveringInformation info = phi.getParent();

			g2d.setStroke(dashed);
			g2d.setColor(new Color(0.0f, 0.0f, 1.0f, 0.3f));
			g2d.draw(new Line2D.Double(mXpos, mYpos, info.getxAnchor(), info
					.getyAnchor()));

			g2d.setColor(colors.get(info));

			double w = bufferWidth;
			double h = phi.getSize() / ((double) mn.getBufferSize())
					* bufferHeight;
			double x = mXpos + bufferPos;
			double y = mYpos + bufferHeight / 2 - j - h;
			j += h;

			g2d.fill(new Rectangle2D.Double(x, y, w, h));
			g2d.setStroke(original);
			g2d.setColor(Color.black);
			g2d.draw(new Line2D.Double(x, y, x + w, y));

		}

		g2d.setStroke(original);
		g2d.setColor(Color.black);
		g2d.draw(new Rectangle2D.Double(mXpos + bufferPos, mYpos - bufferHeight
				/ 2, bufferWidth, bufferHeight));

		g2d.setColor(new Color(0.7f, 0.7f, 0.7f, 0.3f));
		g2d.setStroke(dashed);
		for (MobileNode conn : mn.getConnectedNodes()) {
			g2d.draw(new Line2D.Double(mXpos, mYpos, mx(conn.getxPos()),
					my(conn.getyPos())));
		}
		g2d.setStroke(original);
	}

	/**
	 * Print an hovering information: anchor area.
	 * 
	 * @param info
	 * @param i
	 * @param g2d
	 */
	private void paintHoveringInformation(HoveringInformation info, int i,
			Graphics2D g2d) {
		// Anchor area
		g2d.setColor(colors.get(info));
		g2d.fill(new Ellipse2D.Double(mx(info.getxAnchor()
				- info.getAnchorRange() / 2), my(info.getyAnchor()
				- info.getAnchorRange() / 2), mx(info.getAnchorRange()),
				my(info.getAnchorRange())));
		// Anchor
		// g2d.setColor(Color.RED.darker());
		// g2d.draw(new Ellipse2D.Double(info.getxAnchor(), info.getyAnchor(),
		// 5, 5));

	}

	private double mx(double x) {
		return x / world.getWidth() * getSize().width;
	}

	private double my(double y) {
		return y / world.getHeight() * getSize().height;
	}
}