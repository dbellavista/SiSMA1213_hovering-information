package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setSize(300, 300);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new MyPanel(50.0, 200.0, 300.0, 0.0, 50.0),
				BorderLayout.CENTER);
		frame.setVisible(true);
		frame.revalidate();
		frame.repaint();
	}

}

class MyPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	double x1;
	double x2;
	double x3;
	double y1;
	double y2;
	double y3;

	public MyPanel(double x1, double y1, double x2, double y2, double s) {
		super();
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		double a = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		x3 = x2 - (a - s) / a * (x2 - x1);
		y3 = y2 - (a - s) / a * (y2 - y1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setColor(Color.black);
		g2d.draw(new Line2D.Double(x1, y1, x2, y2));
		g2d.setColor(Color.red);
		g2d.fill(new Ellipse2D.Double(x1 - 3, y1 - 3, 6, 6));
		g2d.fill(new Ellipse2D.Double(x2 - 3, y2 - 3, 6, 6));
		g2d.setColor(Color.blue);
		g2d.fill(new Ellipse2D.Double(x3 - 3, y3 - 3, 6, 6));
	}

}
