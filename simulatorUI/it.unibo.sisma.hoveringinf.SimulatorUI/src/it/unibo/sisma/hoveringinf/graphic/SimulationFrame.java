package it.unibo.sisma.hoveringinf.graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import it.unibo.sisma.hoveringinf.entities.World;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The frame showing the simulation.
 * 
 * @author Daniele Bellavista
 * 
 */
public class SimulationFrame {

	private World wordToDraw;
	private JFrame frame;

	public SimulationFrame(String title, int width, int height) {
		frame = new JFrame(title);
		frame.setBackground(Color.white);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.getContentPane().setLayout(new BorderLayout());
	}

	public void setVisible(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(b);
			}
		});
	}

	public void render(final World world) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (wordToDraw != null) {
					frame.getContentPane().removeAll();
				}
				if (world != null) {
					wordToDraw = world;
					frame.getContentPane().add(new GraphicWorld(world),
							BorderLayout.CENTER);
					frame.revalidate();
					frame.repaint();
					frame.getContentPane().revalidate();
					frame.getContentPane().repaint();
				}
			}
		});
	}

	public void addWindowCloseListener(final WindowCloseListener l) {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				l.onClose();
			}
		});
	}

}